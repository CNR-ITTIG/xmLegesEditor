package it.cnr.ittig.xmleges.editor.blocks.form.meta.pacto;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.editor.services.form.meta.pacto.PactoProprietariForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.meta.pacto.CnrProprietariForm</code>.
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
public class PactoProprietariFormImpl implements PactoProprietariForm, Loggable, Serviceable, Initializable  {

	Logger logger;

	Form form;
	
	JTextField nproposta;
	
	JTextField ufficio;

	JTextField relatore;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		
		form.setMainComponent(getClass().getResourceAsStream("pactoProprietari.jfrm"));
		form.setName("editor.form.meta.pacto");
		//form.setHelpKey("help.contents.form.pactoproprietari");
		
		nproposta = (JTextField) form.getComponentByName("editor.form.meta.pacto.nproposta");
		ufficio = (JTextField) form.getComponentByName("editor.form.meta.pacto.ufficio");
		relatore = (JTextField) form.getComponentByName("editor.form.meta.pacto.relatore");						
	}

	// ////////////////////////////////////////////// MetaDescrittoriFormInterface
	public boolean openForm() {
		form.setSize(400, 200);
		form.showDialog(false);
		return form.isOk();
	}

	public String[] getProprietari() {
		return new String[] {nproposta.getText().trim(),ufficio.getText().trim(),relatore.getText().trim()};
	}
	
	public void setProprietari(String[] metadati) {
		if(metadati!=null){
			nproposta.setText(metadati[0]);
			ufficio.setText(metadati[1]);
			relatore.setText(metadati[2]);
		}
		else {
			nproposta.setText("");
			ufficio.setText("");
			relatore.setText("");
		}
	}
}

