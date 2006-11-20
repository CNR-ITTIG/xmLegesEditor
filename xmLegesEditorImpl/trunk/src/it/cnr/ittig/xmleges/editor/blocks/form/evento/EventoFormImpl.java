package it.cnr.ittig.xmleges.editor.blocks.form.evento;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.MetaCiclodivita;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.form.evento.EventoForm;
import it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita.CiclodiVitaEventoForm;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JTextField;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.evento.EventoForm</code>.</h1>
 * <h1>Permette la selezione o l'inserimento di un evento</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @see
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class EventoFormImpl implements EventoForm, Loggable, Serviceable, Initializable {

	Logger logger;

	Form form;
	
	CiclodiVitaEventoForm ciclodivitaeventoForm;

	JTextField textField;
	
	MetaCiclodivita metaciclodivita;
	
	Evento selectedEvento;


	AbstractButton openFormBtn;
	
	AbstractButton clearBtn;
	
	SelectionManager selectionManager;
	
		

	
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		ciclodivitaeventoForm = (CiclodiVitaEventoForm) serviceManager.lookup(CiclodiVitaEventoForm.class);
		metaciclodivita = (MetaCiclodivita) serviceManager.lookup(MetaCiclodivita.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("Evento.jfrm"));
		textField = (JTextField) form.getComponentByName("editor.form.evento.evento");
		textField.setText("");
		textField.setEditable(false);
		openFormBtn = (AbstractButton) form.getComponentByName("editor.form.evento.modifica");
		openFormBtn.setAction(new EventoFormAction());
		clearBtn = (AbstractButton ) form.getComponentByName("editor.form.evento.clear");
		clearBtn.setAction(new ClearEventoAction());
		
		form.setHelpKey("help.contents.form.evento");
	}

	// /////////////////////////////////////////////////////// UrnForm Interface
	public Component getAsComponent() {
		return form.getAsComponent();
	}

	public void setEnabled(boolean enabled) {
		textField.setEnabled(enabled);
		openFormBtn.setEnabled(enabled);
	}

	
	public Evento getEvento() {
		return this.selectedEvento;
	}

	public void setEvento(Evento evento) {
		this.selectedEvento = evento;
		textField.setText(evento!=null?evento.toString():"");
	}

	
	public boolean openForm() {
		return ciclodivitaeventoForm.openForm();
		
	}
	
	private boolean isUpdatedEventi(Evento[] oldEventi, Evento[] newEventi){
		if(oldEventi.length != newEventi.length)
			return true;
		for(int i=0; i<oldEventi.length; i++){
			if(!oldEventi[i].toString().equalsIgnoreCase(newEventi[i].toString()))
				return true;
		}
		return false;
	}

	
	private void clearEvento() {
		this.selectedEvento = null;
		textField.setText("");		
		
	}
	
	
	protected class EventoFormAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			Node node = selectionManager.getActiveNode();
			metaciclodivita.setActiveNode(node);
			Evento[] eventiOnDom=metaciclodivita.getEventi();
			Relazione[] relazioniOnDom = metaciclodivita.getRelazioni();
			ciclodivitaeventoForm.setEventi(eventiOnDom);
			Relazione[] relazioniUlteriori = metaciclodivita.getRelazioniUlteriori(eventiOnDom,relazioniOnDom);
			ciclodivitaeventoForm.setRel_totali(metaciclodivita.mergeRelazioni(ciclodivitaeventoForm.getEventi(),relazioniUlteriori));
			if(openForm()){
				// 1 - risetta tutti i nodi evento anche se non ci sono state variazioni
				if(isUpdatedEventi(metaciclodivita.getEventi(), ciclodivitaeventoForm.getEventi())){
					
					metaciclodivita.setEventi(ciclodivitaeventoForm.getEventi());
					metaciclodivita.setRelazioni(metaciclodivita.mergeRelazioni(ciclodivitaeventoForm.getEventi(),relazioniUlteriori));
					
					
				}
				selectedEvento = ciclodivitaeventoForm.getSelectedEvento();
				textField.setText(selectedEvento!=null?selectedEvento.toString():"");
			}
			
		}
	}
	
	protected class ClearEventoAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			clearEvento();
		}
	}

//	public Vector getRemovedEvents() {
//		return ciclodivitaeventoForm.getLastRemovedEvents();
//	}

	public void setTextField(JTextField textField) {
		this.textField = textField;
	}

	
	


}
