package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.passive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.NotaForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.DispPassiveForm;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JLabel;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.disposizioni.passive.NotaForm</code>.
 * </h1>
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
 * @version 1.0
 */
public class NotaFormImpl implements NotaForm, Loggable, Serviceable, Initializable, ActionListener, EventManagerListener {
	Logger logger;

	boolean idSelezionato;
	int start;
	int end;
	
	Form form;
	DispPassiveForm disposizioni;

	JTextArea prenota;
	JTextArea autonota;
	JTextArea postnota;
	JLabel pretesto;
	JLabel autotesto;
	JLabel posttesto;
	JLabel implicitatesto;
	JCheckBox implicita; 
	JButton fine;
	JButton indietro;
	
	EventManager eventManager;

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		disposizioni = (DispPassiveForm) serviceManager.lookup(DispPassiveForm.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
	}

	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentClosedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("Nota.jfrm"));
		form.setName("editor.form.disposizioni.passive");
		form.setCustomButtons(null);
		
		form.setHelpKey("help.contents.form.disposizionipassive.nota");
		prenota = (JTextArea) form.getComponentByName("editor.disposizioni.passive.prenota");
		autonota = (JTextArea) form.getComponentByName("editor.disposizioni.passive.autonota");
		postnota = (JTextArea) form.getComponentByName("editor.disposizioni.passive.postnota");
		pretesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.pretesto");
		autotesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.autotesto");
		posttesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.posttesto");
		implicitatesto = (JLabel) form.getComponentByName("editor.disposizioni.passive.implicitatesto");
		implicita = (JCheckBox)  form.getComponentByName("editor.disposizioni.passive.implicita");
		
		fine = (JButton) form.getComponentByName("editor.form.disposizioni.passive.btn.fine");
		indietro = (JButton) form.getComponentByName("editor.form.disposizioni.passive.btn.indietro");
		fine.addActionListener(this);
		indietro.addActionListener(this);
		form.setSize(360, 350);
	}

	public void openForm(FormClosedListener listener, String nota) {

		idSelezionato = false;
		prenota.setText("");
		postnota.setText("");
		autonota.setText(nota);
		implicita.setSelected(false);
		form.showDialog(listener);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fine) {
			disposizioni.setOperazioneProssima();
			disposizioni.setPostnota(postnota.getText().trim());
			disposizioni.setPrenota(prenota.getText().trim());
			disposizioni.setImplicita(implicita.isSelected());
			form.close();
		}
		if (e.getSource() == indietro) {
			form.close();
		}
	}
	
	public void manageEvent(EventObject event) {
		if (form.isDialogVisible()) 
				form.close();
	}
}
