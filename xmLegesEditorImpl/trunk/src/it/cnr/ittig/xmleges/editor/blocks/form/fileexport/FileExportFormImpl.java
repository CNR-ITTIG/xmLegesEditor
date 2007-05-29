package it.cnr.ittig.xmleges.editor.blocks.form.fileexport;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.form.fileexport.FileExportForm;

import it.cnr.ittig.xmleges.core.util.date.UtilDate;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.form.fileexport.FileExportForm</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>Form</li>
 * <li>Provvedimenti</li>
 * <li>UtilUI</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 * 
 * @version 1.0
 * @author  Tommaso Agnoloni <agnoloni@ittig.cnr.it>
 */


public class FileExportFormImpl implements FileExportForm, Loggable, Serviceable, Initializable, FormVerifier {

	Logger logger;
	Form form;
	DateForm dataVigenza;
	
	JList sceltaDataVigenza;
	JLabel etichettaVigenze;
	
	JRadioButton radioMonovigente;
	JRadioButton radioMultivigente;
	
	UtilMsg utilmsg;

	String errorMessage = "";
	
	DocumentManager documentManager;
	
	DefaultListModel listModel = new DefaultListModel();
	
	MouseAdapter mouseAdapter = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				radioMonovigente.setSelected(true);				
				dataVigenza.set(new Date((String) listModel.getElementAt(sceltaDataVigenza.getSelectedIndex())));
			}
		}
	};

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		dataVigenza = (DateForm) serviceManager.lookup(DateForm.class);	
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}


	public boolean openForm() {
		if (!form.hasMainComponent())
			try {
				initialize();
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		listModel.removeAllElements();
		setDateDiVigenza();
		dataVigenza.set(null);
		form.showDialog();
		return form.isOk();
	}
	
	
	// ///////////////////////////////////////////////// Initializable Interface
	
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("FileExport.jfrm"));
		form.replaceComponent("editor.form.fileexport.datavigenza", dataVigenza.getAsComponent());
		sceltaDataVigenza = (JList) form.getComponentByName("editor.form.fileexport.datevigenza");
		sceltaDataVigenza.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sceltaDataVigenza.setModel(listModel);
		sceltaDataVigenza.addMouseListener(mouseAdapter);
		
		radioMonovigente = (JRadioButton) form.getComponentByName("editor.form.fileexport.radio.monovigente");
		radioMultivigente = (JRadioButton) form.getComponentByName("editor.form.fileexport.radio.multivigente");
		etichettaVigenze = (JLabel) form.getComponentByName("editor.form.fileexport.etiVigenze");
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(radioMonovigente);
		grupporadio.add(radioMultivigente);
		radioMonovigente.setSelected(true);
		dataVigenza.set(null);
		form.setSize(250, 270);
		form.setName("editor.form.fileexport");
		form.addFormVerifier(this);		
		
		form.setHelpKey("help.contents.form.fileexport");		
	}

	
	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean verifyForm() {
		
		boolean isValid = true;
		
		
		if(isMonoVigente()){
			if((dataVigenza.getAsYYYYMMDD()==null || dataVigenza.getAsYYYYMMDD().trim().length()!=8))
				isValid = false;
			else if(dataVigenza.getAsDate().compareTo(UtilDate.getCurrentDate())>0){
				utilmsg.msgInfo("editor.form.fileexport.msg.err.dateaftervalid");
				dataVigenza.set(UtilDate.getCurrentDate());
			}
		}
		
		
		if(!isValid)
			errorMessage = "editor.form.fileexport.msg.err.dateerror";
		
		return isValid;
	}
	

	public String getDataVigenza() {
		
		return dataVigenza.getAsYYYYMMDD();
	}

	public boolean isMonoVigente() {
		return radioMonovigente.isSelected();
	}

	public boolean isMultiVigente() {
		return radioMultivigente.isSelected();
	}
	
	private void setDateDiVigenza() {
		Document doc = documentManager.getDocumentAsDom();
		NodeList lista = doc.getElementsByTagName("evento");
		String temp;
		for(int i=0; i<lista.getLength();i++) {
			temp=lista.item(i).getAttributes().getNamedItem("data").getNodeValue();
			listModel.addElement(temp.substring(6, 8)+"/"+temp.substring(4, 6)+"/"+temp.substring(0, 4));
		}
	}
}
	