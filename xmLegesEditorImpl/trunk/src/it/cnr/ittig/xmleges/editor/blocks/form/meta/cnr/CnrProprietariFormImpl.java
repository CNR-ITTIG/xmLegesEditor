package it.cnr.ittig.xmleges.editor.blocks.form.meta.cnr;

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
import it.cnr.ittig.xmleges.editor.services.form.meta.cnr.CnrProprietariForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
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
public class CnrProprietariFormImpl implements CnrProprietariForm, Loggable, Serviceable, Initializable, FormVerifier, Configurable {

	Logger logger;

	//   cnrProprietari.jfrm
	Form form;
	
	JTextField strutturaEmanante;

	JTextField autoritaEmanante;
	
	JComboBox tipoDestinatario;

	JComboBox areadisciplina_combo;
		
	JComboBox str_destinataria_combo;
//	editor.form.meta.cnr.str_dest_combo
	
	JComboBox tipo_provv_combo;
	
	JRadioButton dipartimentoButton;
	
	JRadioButton areaButton;
	
	JRadioButton disciplinaButton;
	
	ButtonGroup areadisciplinaGroup;
	
	String sceltoGruppo;
	
	String[] elenco_tipodest = new String[13];
	
	String[] elenco_strutturaDestinataria = new String[3];
	
	String[] elenco_tipoProvvedimento = new String[4];
	
	String[] elenco_disciplina = new String[17];
	
	String[] elenco_areaScientifica = new String[7];
		
	String[] elenco_areaDipartimento = new String[13];
	
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
		
		form.setHelpKey("help.contents.form.cnrproprietari");
		
		strutturaEmanante = (JTextField) form.getComponentByName("editor.form.meta.cnr.struttura");
		autoritaEmanante = (JTextField) form.getComponentByName("editor.form.meta.cnr.autorita");
		tipoDestinatario = (JComboBox) form.getComponentByName("editor.form.meta.cnr.destinatario");
		tipoDestinatario.removeAllItems();
		for (int i = 0; i < elenco_tipodest.length; i++)
			tipoDestinatario.addItem(elenco_tipodest[i]);		
		tipoDestinatario.setEditable(true);
		
		areadisciplina_combo = (JComboBox) form.getComponentByName("editor.form.meta.cnr.areadisciplina_combo");
		areadisciplina_combo.setEnabled(false);
		
		
		
		dipartimentoButton = (JRadioButton) form.getComponentByName("editor.form.meta.cnr.dipartimento_button");
		{
			dipartimentoButton.setSelected(true);
			sceltoGruppo="cnr:dipartimento";
			areadisciplina_combo.removeAllItems();
			for (int i = 0; i < elenco_areaDipartimento.length; i++)
				areadisciplina_combo.addItem(elenco_areaDipartimento[i]);		
			areadisciplina_combo.setEditable(true);
			areadisciplina_combo.setEnabled(true);
		}
		areaButton = (JRadioButton) form.getComponentByName("editor.form.meta.cnr.area_button");
		areaButton.setActionCommand("area");
		disciplinaButton = (JRadioButton) form.getComponentByName("editor.form.meta.cnr.disciplina_button");
		disciplinaButton.setActionCommand("disciplina");
		
		ActionListener areadisciplinaListener = new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("area")){
					areadisciplina_combo.removeAllItems();
					for (int i = 0; i < elenco_areaScientifica.length; i++)
						areadisciplina_combo.addItem(elenco_areaScientifica[i]);		
					areadisciplina_combo.setEditable(true);
					sceltoGruppo="cnr:areaScientifica";
				}else if(e.getActionCommand().equals("disciplina")){
					areadisciplina_combo.removeAllItems();
					for (int i = 0; i < elenco_disciplina.length; i++)
						areadisciplina_combo.addItem(elenco_disciplina[i]);		
					areadisciplina_combo.setEditable(true);
					sceltoGruppo="cnr:disciplina";
				} else{
					areadisciplina_combo.removeAllItems();
					for (int i = 0; i < elenco_areaDipartimento.length; i++)
						areadisciplina_combo.addItem(elenco_areaDipartimento[i]);		
					areadisciplina_combo.setEditable(true);
					sceltoGruppo="cnr:dipartimento";
				}
				areadisciplina_combo.setEnabled(true);
			}};
		areaButton.addActionListener(areadisciplinaListener);
		disciplinaButton.addActionListener(areadisciplinaListener);
		dipartimentoButton.addActionListener(areadisciplinaListener);
		
		areadisciplinaGroup = new ButtonGroup();
		areadisciplinaGroup.add(dipartimentoButton);
		areadisciplinaGroup.add(areaButton);
		areadisciplinaGroup.add(disciplinaButton);
						
		str_destinataria_combo = (JComboBox) form.getComponentByName("editor.form.meta.cnr.str_dest_combo");
		str_destinataria_combo.removeAllItems();
		for (int i = 0; i < elenco_strutturaDestinataria.length; i++)
			str_destinataria_combo.addItem(elenco_strutturaDestinataria[i]);		
		str_destinataria_combo.setEditable(true);
				
		tipo_provv_combo = (JComboBox) form.getComponentByName("editor.form.meta.cnr.tipo_provv_combo");
		
		tipo_provv_combo.removeAllItems();
		for (int i = 0; i < elenco_tipoProvvedimento.length; i++)
			tipo_provv_combo.addItem(elenco_tipoProvvedimento[i]);		
		tipo_provv_combo.setEditable(true);

	}

	// ////////////////////////////////////////////// MetaDescrittoriForm
	// Interface
	public boolean openForm() {
		form.setSize(600, 350);
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
				(String)tipoDestinatario.getSelectedItem(),
				(String)str_destinataria_combo.getSelectedItem(),
				(String)tipo_provv_combo.getSelectedItem(),
				(String)areadisciplina_combo.getSelectedItem(),
				sceltoGruppo,	//ritorna il selezionato del gruppo 
				};
				
	}
	
	public void setProprietari(String[] metadati) {
		if(metadati!=null && metadati.length==7){
			
			areaButton.setEnabled(true);
			if (metadati[6].equals("cnr:areaScientifica")) 
				areaButton.setSelected(true);
			else 
				if (metadati[6].equals("cnr:disciplina")) 
					disciplinaButton.setSelected(true);
				else
					dipartimentoButton.setEnabled(true);
		
			
			strutturaEmanante.setText(metadati[0]);
			autoritaEmanante.setText(metadati[1]);
			tipoDestinatario.removeItem(metadati[2]);
			tipoDestinatario.addItem(metadati[2]);
			tipoDestinatario.setSelectedItem(metadati[2]);
			
			areadisciplina_combo.removeItem(metadati[5]);
			areadisciplina_combo.addItem(metadati[5]);
			areadisciplina_combo.setSelectedItem(metadati[5]);
			
			str_destinataria_combo.removeItem(metadati[3]);
			str_destinataria_combo.addItem(metadati[3]);
			str_destinataria_combo.setSelectedItem(metadati[3]);
			tipo_provv_combo.removeItem(metadati[4]);
			tipo_provv_combo.addItem(metadati[4]);
			tipo_provv_combo.setSelectedItem(metadati[4]);
		}
		
	}

	public void configure(Configuration configuration) throws ConfigurationException {
		try {
			Configuration[] elencoConf = configuration.getChildren();
			int j=0, k=0, l=0, m=0, n=0, o=0;
			for (int i = 0; i < elencoConf.length; i++){
				if(elencoConf[i].getName().toLowerCase().startsWith("tipoprovvedimento")){
				   elenco_tipoProvvedimento[j] = elencoConf[i].getAttribute("name");
				   j++;
				}
				else if(elencoConf[i].getName().toLowerCase().startsWith("tipodestinatario")){
					   elenco_tipodest[k] = elencoConf[i].getAttribute("name");
					   k++;
				}
				else if(elencoConf[i].getName().toLowerCase().startsWith("strutturadestinataria")){
					   elenco_strutturaDestinataria[l] = elencoConf[i].getAttribute("name");
					   l++;
				}
				else if(elencoConf[i].getName().toLowerCase().startsWith("disciplina")){
					   elenco_disciplina[m] = elencoConf[i].getAttribute("name");
					   m++;
				}
				else if(elencoConf[i].getName().toLowerCase().startsWith("areascientifica")){
					   elenco_areaScientifica[n] = elencoConf[i].getAttribute("name");
					   n++;
				} else if(elencoConf[i].getName().toLowerCase().startsWith("dipartimento")){
						elenco_areaDipartimento[o] = elencoConf[i].getAttribute("name");
					   o++;
				}
			}
		} catch (ConfigurationException e) {
			logger.error("Impossibile leggere il file di configurazione");
		}
		
	}

	
}

