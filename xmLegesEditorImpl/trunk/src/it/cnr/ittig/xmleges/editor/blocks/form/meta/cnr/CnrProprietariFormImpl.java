package it.cnr.ittig.xmleges.editor.blocks.form.meta.cnr;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.editor.services.form.meta.cnr.CnrProprietariForm;

import javax.swing.JTextField;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.form.meta.cnr.CnrProprietariForm</code>.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>, <a
 *         href="mailto:t.paba@onetech.it">Tommaso Paba</a>
 */
public class CnrProprietariFormImpl implements CnrProprietariForm, Loggable, Serviceable, Initializable, FormVerifier {

	Logger logger;

	//   cnrProprietari.jfrm
	Form form;
	
	JTextField strutturaEmanante;

	JTextField autoritaEmanante;
	
	JTextField tipoDestinatario;

	JTextField tagDiscipline;
	
//	JComboBox tagDiscipline;
	
	String errorMessage = "";

	
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
		
		
		form.setMainComponent(getClass().getResourceAsStream("cnrProprietari.jfrm"));
		

		form.setName("editor.form.meta.cnr");
		
		strutturaEmanante = (JTextField) form.getComponentByName("editor.form.meta.cnr.struttura");
		autoritaEmanante = (JTextField) form.getComponentByName("editor.form.meta.cnr.autorita");
		tipoDestinatario = (JTextField) form.getComponentByName("editor.form.meta.cnr.destinatario");
		
		tagDiscipline = (JTextField) form.getComponentByName("editor.form.meta.cnr.disciplina");
		
//		tagDiscipline = (JComboBox) form.getComponentByName("editor.form.meta.cnr.disciplina");
//		tagDiscipline.addItem("aaa");
//		tagDiscipline.addItem("bbb");
//		tagDiscipline.addItem("ccc");
//		tagDiscipline.addItem("ddd");
//		tagDiscipline.addItem("eee");
//		
//		tagDiscipline.setEnabled(true);
		
		
	}

	// ////////////////////////////////////////////// MetaDescrittoriForm
	// Interface
	public boolean openForm() {
		form.setSize(600, 150);
		form.showDialog();
		return form.isOk();
	}

		

	public boolean verifyForm() {
		return true;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String[] getProprietari() {
		return new String[] {
				strutturaEmanante.getText(),
				autoritaEmanante.getText(),
				tipoDestinatario.getText(),
				tagDiscipline.getText()};
	}

	public void setProprietari(String[] metadati) {
		strutturaEmanante.setText(metadati[0]);
		autoritaEmanante.setText(metadati[1]);
		tipoDestinatario.setText(metadati[2]);
		tagDiscipline.setText(metadati[3]);
		
	}

	
}
