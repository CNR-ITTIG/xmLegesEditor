package it.cnr.ittig.xmleges.editor.blocks.form.vigenza;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.form.evento.EventoForm;
import it.cnr.ittig.xmleges.editor.services.form.vigenza.VigenzaForm;

import javax.swing.JList;
import javax.swing.JTextArea;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.vigenza.VigenzaForm</code>.</h1>
 * <h1>Descrizione</h1>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class VigenzaFormImpl implements VigenzaForm,  FormVerifier, Loggable, Serviceable, Initializable {

	Logger logger;

	Form form;
	
	EventoForm eventoinizioform;
	
	EventoForm eventofineform;

	JList vigenzeList;

	JTextArea textArea;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		eventoinizioform = (EventoForm) serviceManager.lookup(EventoForm.class);
		eventofineform = (EventoForm) serviceManager.lookup(EventoForm.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(getClass().getResourceAsStream("SelezioneVigenzaForm.jfrm"));
		form.setName("editor.selezionevigenzatesto");
		textArea = (JTextArea) form.getComponentByName("editor.selezionevigenzatesto.testo");
		
		
		form.replaceComponent("editor.selezionevigenza.eventoiniziovigore", eventoinizioform.getAsComponent());
		form.replaceComponent("editor.selezionevigenza.eventofinevigore", eventofineform.getAsComponent());
		
		//vigenzeList = (JList) form.getComponentByName("editor.selezionevigenzatesto.vigenzelist");
		//vigenzeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public Evento openForm(Evento[] vigenze, String testo) {

		if (testo != null) {
			textArea.setText(testo);
		} else {
			textArea.setText("");
		} 

		
//		if (vigenze != null && vigenze.length > 0) {
//			vigenzeList.setListData(vigenze);
//			vigenzeList.setSelectedIndex(0);
//		} else {
//			vigenzeList.removeAll();
//		}

		form.setSize(600, 350);
		form.showDialog();

//		if (form.isOk()) {
//			return (Evento) vigenzeList.getSelectedValue();
//		} else {
//			return null;
//		}
		return null;
	}

	
	////////////////////     NUOVA    INTERFACCIA    /////////////////////// 
	
	public boolean openForm(String testo, Evento inizioVigore, Evento fineVigore, String status) {
		return openForm(testo,inizioVigore,fineVigore,null,null,status);
	}
	
	public boolean openForm(String testo, Evento inizioVigore, Evento fineVigore, Evento inizioEfficacia, Evento fineEfficacia, String status) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public Evento getFineEfficacia() {
		// TODO Auto-generated method stub
		return null;
	}

	public Evento getFineVigore() {
		// TODO Auto-generated method stub
		return null;
	}

	public Evento getInizioEfficacia() {
		// TODO Auto-generated method stub
		return null;
	}

	public Evento getInizioVigore() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getStatus() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/////////////////////////////////////////////////////////////////////////

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean verifyForm() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
