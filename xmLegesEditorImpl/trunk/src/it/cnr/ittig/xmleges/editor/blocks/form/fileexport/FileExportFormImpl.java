package it.cnr.ittig.xmleges.editor.blocks.form.fileexport;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.form.fileexport.FileExportForm;

import it.cnr.ittig.xmleges.core.util.date.UtilDate;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

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


//editor.form.fileexport.datavigenza
//editor.form.fileexport.radio.monovigente
//editor.form.fileexport.radio.multivigente

public class FileExportFormImpl implements FileExportForm, Loggable, Serviceable, Initializable, FormVerifier {

	Logger logger;
	Form form;
	DateForm dataVigenza;
	
	JRadioButton radioMonovigente;
	JRadioButton radioMultivigente;
	
	UtilMsg utilmsg;
	

	String errorMessage = "";

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		dataVigenza = (DateForm) serviceManager.lookup(DateForm.class);	
		utilmsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}


	public boolean openForm() {
		if (!form.hasMainComponent())
			try {
				initialize();
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		dataVigenza.set(null);
		form.showDialog();
		return form.isOk();
	}
	
	
	// ///////////////////////////////////////////////// Initializable Interface
	
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("FileExport.jfrm"));
		form.replaceComponent("editor.form.fileexport.datavigenza", dataVigenza.getAsComponent());
		radioMonovigente = (JRadioButton) form.getComponentByName("editor.form.fileexport.radio.monovigente");
		radioMultivigente = (JRadioButton) form.getComponentByName("editor.form.fileexport.radio.multivigente");
		ButtonGroup grupporadio = new ButtonGroup();
		grupporadio.add(radioMonovigente);
		grupporadio.add(radioMultivigente);
		radioMonovigente.setSelected(true);
		dataVigenza.set(null);
		form.setSize(350, 150);
		form.setName("editor.form.fileexport");
		form.addFormVerifier(this);
		
	}

	
	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean verifyForm() {
//		(newrinviiformimpl)
		// FIXME sistemare verifyForm: se data < entrataInVigore setEntrataInVigore e msg;
		// se data > dataOdierna : set Data Odierna
		
		boolean isValid = true;
		
		if(dataVigenza.getAsDate().compareTo(UtilDate.getCurrentDate())>0){
			utilmsg.msgInfo("editor.form.fileexport.msg.err.dateaftervalid");
			dataVigenza.set(UtilDate.getCurrentDate());
		}
		
		if(isMonoVigente() && (dataVigenza.getAsYYYYMMDD()==null || dataVigenza.getAsYYYYMMDD().trim().length()!=8))
				isValid = false;
		
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
	
}
	