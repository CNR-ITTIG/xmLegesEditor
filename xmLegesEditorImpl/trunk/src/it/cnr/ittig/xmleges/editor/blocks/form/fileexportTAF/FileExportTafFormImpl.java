package it.cnr.ittig.xmleges.editor.blocks.form.fileexportTAF;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.form.date.DateForm;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.editor.services.form.fileExportTAF.FileExportTafForm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

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


public class FileExportTafFormImpl implements FileExportTafForm, Loggable, Serviceable, Initializable, FormVerifier {

	Logger logger;
	Form form;
	
	
	JList sceltaDataVigenza1;
	JLabel etichettaVigenze1;
	
	JList sceltaDataVigenza2;
	JLabel etichettaVigenze2;
	

	
	UtilMsg utilmsg;

	String errorMessage = "";
	
	DocumentManager documentManager;
	
	DefaultListModel listModel = new DefaultListModel();
	
//	MouseAdapter mouseAdapter = new MouseAdapter() {
//		public void mouseClicked(MouseEvent e) {
//			if (SwingUtilities.isLeftMouseButton(e)) {
//				radioMonovigente.setSelected(true);		
//				if (sceltaDataVigenza.getSelectedIndex()!=-1)
//					//dataVigenza.set(new Date((String) listModel.getElementAt(sceltaDataVigenza.getSelectedIndex())));
//					dataVigenza.set(getAsDate((String) listModel.getElementAt(sceltaDataVigenza.getSelectedIndex())));
//			}
//		}
//	};



	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);	
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
		
		form.showDialog();
		return form.isOk();
	}
	
	
	// ///////////////////////////////////////////////// Initializable Interface
	
	public void initialize() throws java.lang.Exception {
		form.setMainComponent(this.getClass().getResourceAsStream("FileExportTAF.jfrm"));
		
		
		
		sceltaDataVigenza1 = (JList) form.getComponentByName("editor.form.fileexportTAF.datevigenza1");
		sceltaDataVigenza1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sceltaDataVigenza1.setModel(listModel);
	//	sceltaDataVigenza1.addMouseListener(mouseAdapter);
		
		sceltaDataVigenza2 = (JList) form.getComponentByName("editor.form.fileexportTAF.datevigenza2");
		sceltaDataVigenza2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sceltaDataVigenza2.setModel(listModel);
	//	sceltaDataVigenza2.addMouseListener(mouseAdapter);
		
		
		
		etichettaVigenze1 = (JLabel) form.getComponentByName("editor.form.fileexportTAF.labeldata1");
		etichettaVigenze2 = (JLabel) form.getComponentByName("editor.form.fileexportTAF.labeldata2");
		
		form.setSize(500,300);
		form.setName("editor.form.fileexportTAF");
		form.addFormVerifier(this);		
		
		form.setHelpKey("help.contents.form.fileexport");		
	}

	
	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean verifyForm() {
		
		boolean isvalid = (getData1().compareTo(getData2())<0); 
		if (!isvalid)
			errorMessage = "editor.form.fileexport.msg.err.dateerror";
		return isvalid;
		
		
	}
	

	public String getData1() {		
		return getAsYYYYMMDD((String) listModel.getElementAt(sceltaDataVigenza1.getSelectedIndex()));
	}
	
	public String getData2() {
		return getAsYYYYMMDD((String) listModel.getElementAt(sceltaDataVigenza2.getSelectedIndex()));
		
	}
	
	


	private void setDateDiVigenza() {
		Document doc = documentManager.getDocumentAsDom();
		NodeList lista = doc.getElementsByTagName("evento");
		String data;
		String fonte;
		for(int i=0; i<lista.getLength();i++) {
			fonte=lista.item(i).getAttributes().getNamedItem("fonte").getNodeValue();
			if (fonte.indexOf("rp")!=-1 | fonte.indexOf("ro")!=-1) { 
				data=lista.item(i).getAttributes().getNamedItem("data").getNodeValue();
				listModel.addElement(data.substring(6, 8)+"/"+data.substring(4, 6)+"/"+data.substring(0, 4));
			}	
		}
	}

	
	public String getAsYYYYMMDD(String data) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		if (getAsDate(data) != null) {
			return sf.format(getAsDate(data));
		}
		return null;
	}
	
	
	
	
	
	private java.util.Date getAsDate(String data) {
		java.util.Date d;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(UtilDate.getDateFormat());
			d = sdf.parse(data);
			return d;
		} catch (ParseException ex) {
		}
		return null;
	}
	
}
	