package it.cnr.ittig.xmleges.editor.blocks.form.meta.cnr;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.editor.services.form.meta.cnr.CnrProprietariForm;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
	
	JComboBox str_destinataria_combo;
//	editor.form.meta.cnr.str_dest_combo
	
	JTextField str_destinataria_text;
//	editor.form.meta.cnr.str_dest_text
	
	JButton str_destinataria_button;
	
	JComboBox tipo_provv_combo;
	
	JTextField tipo_provv_text;

	JButton tipo_provv_button;
	
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
		//form.setSize(400,150);

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
		
		str_destinataria_combo = (JComboBox) form.getComponentByName("editor.form.meta.cnr.str_dest_combo");
		str_destinataria_text = (JTextField) form.getComponentByName("editor.form.meta.cnr.str_dest_text");
		str_destinataria_button = (JButton) form.getComponentByName("editor.form.meta.cnr.str_dest_button");
		
		str_destinataria_combo.addItem("uno");
		str_destinataria_combo.addItem("due");
		str_destinataria_combo.addItem("tre");
		
		str_destinataria_button.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  str_destinataria_combo.addItem(str_destinataria_text.getText());
		    	  str_destinataria_combo.setSelectedItem(str_destinataria_text.getText());
		    	  str_destinataria_text.setText(null);
		    	  
		      }
		    });
		
		tipo_provv_combo = (JComboBox) form.getComponentByName("editor.form.meta.cnr.tipo_provv_combo");
		tipo_provv_text = (JTextField) form.getComponentByName("editor.form.meta.cnr.tipo_provv_text");
		tipo_provv_button = (JButton) form.getComponentByName("editor.form.meta.cnr.tipo_provv_button");
		
		tipo_provv_combo.addItem("quattro");
		tipo_provv_combo.addItem("cinque");
		tipo_provv_combo.addItem("sei");
		
		tipo_provv_button.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  tipo_provv_combo.addItem(tipo_provv_text.getText());
		    	  tipo_provv_combo.setSelectedItem(tipo_provv_text.getText());
		    	  tipo_provv_text.setText(null);
		    	  
		      }
		    });
		
	}

	// ////////////////////////////////////////////// MetaDescrittoriForm
	// Interface
	public boolean openForm() {
		form.setSize(650, 330);
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
				tagDiscipline.getText(),
				str_destinataria_combo.getSelectedItem().toString(),
				tipo_provv_combo.getSelectedItem().toString()};
				
	}

	public void setProprietari(String[] metadati) {
		strutturaEmanante.setText(metadati[0]);
		autoritaEmanante.setText(metadati[1]);
		tipoDestinatario.setText(metadati[2]);
		tagDiscipline.setText(metadati[3]);
		str_destinataria_combo.removeItem(metadati[4]);
		str_destinataria_combo.addItem(metadati[4]);
		str_destinataria_combo.setSelectedItem(metadati[4]);
		tipo_provv_combo.removeItem(metadati[5]);
		tipo_provv_combo.addItem(metadati[5]);
		tipo_provv_combo.setSelectedItem(metadati[5]);
		
	}

	
}
