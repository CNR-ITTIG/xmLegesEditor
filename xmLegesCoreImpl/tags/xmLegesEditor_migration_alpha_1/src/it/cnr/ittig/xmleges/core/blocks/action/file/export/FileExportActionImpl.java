package it.cnr.ittig.xmleges.core.blocks.action.file.export;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.file.export.FileExportAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.filetextfield.FileTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextField;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.pdf.UtilPdf;
import it.cnr.ittig.xmleges.core.services.util.rtf.UtilRtf;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.RegexpFileFilter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.xslt.UtilXslt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.EventObject;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.w3c.dom.Node;
/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.file.save.FileSaveAction</code>.</h1>
 * <h1>Descrizione</h1>
 * Esegue il salvataggio su file del documento corrente. Se il documento
 * &egrave; nuovo (<code>DocumentManager.isNew</code>) chiede il nome e lo
 * aggiunge alla lista dei file recenti (<code>FileOpenAction.addLast</code>).
 * <br>
 * Questa implementazione registra le azioni <code>file.save</code> e
 * <code>file.saveas</code> nell'ActionManager.
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.bars.Bars:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.msg.UtilMsg:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.action.file.open.FileOpenAction:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>file.save: descrizione dell'azione come specificato nell'ActionManager;
 * </li>
 * <li>file.saveas: descrizione dell'azione come specificato
 * nell'ActionManager;</li>
 * <li>action.file.save.replace: messaggio conferma se file esistente;</li>
 * <li>action.file.save.saved: testo status bar se file salvato;</li>
 * <li>action.file.save.error.file: testo messaggio se errore di salvataggio;
 * </li>
 * <li>action.file.save.error.encoding: testo se encoding non supportato.</li>
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
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.blocks.action.ActionManagerImpl
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FileExportActionImpl implements FileExportAction, EventManagerListener, ListTextFieldEditor, Loggable, Serviceable, Configurable, Initializable,
		Startable {
	
	
	Logger logger;

	PreferenceManager preferenceManager;

	ActionManager actionManager;

	DocumentManager documentManager;

	UtilMsg utilMsg;

	UtilPdf utilPdf;
	
	UtilRtf utilRtf;

	EventManager eventManager;
	
	Form form;

	ListTextField listTextField;

	FileTextField fileTextField;

	ExportBrowserAction exportBrowserAction;

	ExportHTMLAction exportHTMLAction;

	ExportPDFAction exportPDFAction;
	
	ExportRTFAction exportRTFAction;

	JFileChooser fileChooser;

	String lastExport = null;

	String[] browsers;
	
	String[] readerPdf;
	
	

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		form = (Form) serviceManager.lookup(Form.class);
		listTextField = (ListTextField) serviceManager.lookup(ListTextField.class);
		fileTextField = (FileTextField) serviceManager.lookup(FileTextField.class);
		utilPdf = (UtilPdf) serviceManager.lookup(UtilPdf.class);
		utilRtf = (UtilRtf) serviceManager.lookup(UtilRtf.class);			
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration conf) throws ConfigurationException {
		Configuration bs = conf.getChild("browsers");
		if (bs != null) {
			Configuration[] b = bs.getChildren("browser");
			browsers = new String[b.length];
			for (int i = 0; i < b.length; i++)
				browsers[i] = b[i].getValue();
		}
		bs = conf.getChild("readerpdf");
		if (bs != null) {
			Configuration[] b = bs.getChildren("reader");
			readerPdf = new String[b.length];
			for (int i = 0; i < b.length; i++)
				readerPdf[i] = b[i].getValue();
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		form.setName("file.export.form");
		fileTextField.setFileFilter(new RegexpFileFilter("XSL File", ".*\\.xsl[t]?"));

		form.setMainComponent(getClass().getResourceAsStream("Export.jfrm"));
		form.replaceComponent("file.export.form.export.list", listTextField.getAsComponent());

		listTextField.setEditor(this);

		exportBrowserAction = new ExportBrowserAction();
		actionManager.registerAction("file.export.browser", exportBrowserAction);
		exportHTMLAction = new ExportHTMLAction();
		actionManager.registerAction("file.export.html", exportHTMLAction);
		exportPDFAction = new ExportPDFAction();
		exportRTFAction = new ExportRTFAction();
		actionManager.registerAction("file.export.pdf", exportPDFAction);
		actionManager.registerAction("file.export.rtf", exportRTFAction);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		try {
			lastExport = p.getProperty("lastexport");
		} catch (Exception ex) {
		}
		initFileChooser();
		manageEvent(null);
	}

	// /////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
		Vector v = preferenceManager.getPreferenceAsVector(getClass().getName());
		listTextField.setListElements(v);
	}

	public void stop() throws Exception {
		Vector v = listTextField.getListElements();
		preferenceManager.setPreference(getClass().getName(), v);

		logger.debug("saving last exported...");
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		try {
			p.setProperty("lastexport", lastExport);
		} catch (Exception ex) {
		}
		preferenceManager.setPreference(getClass().getName(), p);
		logger.debug("saving last exported OK");
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		exportBrowserAction.setEnabled(!documentManager.isEmpty());
		exportHTMLAction.setEnabled(!documentManager.isEmpty());
		exportPDFAction.setEnabled(!documentManager.isEmpty());
		exportRTFAction.setEnabled(!documentManager.isEmpty());
	}

	// ////////////////////////////////////////////// FileExportAction Interface


	//	 ///////////////////// salva come HTML
	public boolean doExportHTML() {
		 form.showDialog();
		 if (form.isOk()) {
			 if (listTextField.getSelectedItem() == null) {
				 utilMsg.msgError("file.export.error.xslt");
			 } else if (fileChooser.showSaveDialog(form.getAsComponent()) == JFileChooser.APPROVE_OPTION)
				 exportHTML(new File(listTextField.getSelectedItem().toString()),fileChooser.getSelectedFile());
		 }
		 return false;
	}
		
		
	// ///////////////////// esporta su browser
	public boolean doExportBrowser() {	
		form.showDialog();
     	if (form.isOk()) {
     		if (listTextField.getSelectedItem() == null) 
     			utilMsg.msgError("file.export.error.xslt");
		    else
		    	try {
		    		File temp = UtilFile.createTemp("export.html");
		    		temp.deleteOnExit();
		    		if (exportHTML(new File(listTextField.getSelectedItem().toString()),temp)) {
		    			for (int i = 0; i < browsers.length; i++)
		    				try {
		    					String cmd = browsers[i] + " " + temp.getAbsolutePath();
		    					Runtime.getRuntime().exec(cmd);
		    					break;
		    				} catch (Exception ex) {
		    				}
		    			return true;
		    		}
		    	} catch (Exception ex) {
		    		utilMsg.msgError("file.export.error.browser");
		    		logger.error(ex.toString(), ex);
		    	}
     	}
		return false;
	}
	
	
	protected boolean exportHTML(File xslt, File dest) {
		try {
			DOMWriter domWriter = new DOMWriter();
			domWriter.setCanonical(true);
			domWriter.setFormat(false);
			domWriter.setOutput(dest);
			
			Node res = UtilXslt.applyXslt(documentManager.getDocumentAsDom(), xslt, documentManager.getEncoding());
			domWriter.write(res);
			return true;
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			return false;
		}
	}
	
	
    private String cmdWin(String cmd) {
    	
    	StringTokenizer token = new StringTokenizer(cmd, "\\");
    	String comando=token.nextToken();
    	String temp;
		while (token.hasMoreTokens()) {
			temp = token.nextToken();
			if (temp.indexOf(" ")==-1)
				comando += "\\" + temp; 
			else
				comando += "\\\"" + temp +"\"";
		}
    	return comando;
    }
	
	
	public boolean doExportPDF() {

		String XSL_FO_GU; 
		String dtdName = documentManager.getDtdName();
		
//		if (dtdName.startsWith("nir") && !nirUtilDom.isDocCNR(null))    // documenti NIR
//			XSL_FO_GU = xslts.getXslt("pdf-gazzettaufficiale").getAbsolutePath();
//		else 
//			XSL_FO_GU = xslts.getXslt("pdf-cnr").getAbsolutePath();
//	
//		String osName = System.getProperty("os.name");
//
//		try {
//			JFileChooser fileChooser = new JFileChooser();
//			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//			String filterDesc = "Portable Document Format (*.pdf)";
//			String[] masks = new String[1];
//			masks[0] = ".*\\.[pP][dD][fF]$";
//			fileChooser.setFileFilter(new RegexpFileFilter(filterDesc, masks));
//			fileChooser.setCurrentDirectory(getLastExportAsFile() != null ? getLastExportAsFile().getParentFile() : null);
//
//			if (fileChooser.showSaveDialog(null) == JFileChooser.CANCEL_OPTION)
//				return false;
//			else {
//				File file = fileChooser.getSelectedFile();
//				if (!file.getAbsolutePath().matches("^.*\\.[pP][dD][fF]$"))
//					file = new File(file.getAbsolutePath() + ".pdf");
//
//				// Controlla l'esistenza del file
//				if (file.exists()) {
//					if (!utilMsg.msgYesNo("action.file.save.replace")) {
//						return false;
//					}
//				}
//
//				utilPdf.convertXML2PDF(documentManager.getDocumentAsDom(), XSL_FO_GU, file.getAbsolutePath());
//				lastExport = file.getAbsolutePath();
//
//				if (osName.toLowerCase().matches("windows.*")) {
//					String nomeFile = cmdWin(file.getAbsolutePath());
//					for (int i = 0; i < readerPdf.length; i++)
//						try {
//							String cmd = readerPdf[i] + " " + nomeFile;
//							Runtime.getRuntime().exec(cmd);
//							break;
//						} catch (Exception ex) {
//						}
//				}
//				else {
//					String nomeFile = file.getAbsolutePath();
//					for (int i = 0; i < readerPdf.length; i++)
//						try {
//							String temp[] = new String[2];
//							temp[0] = readerPdf[i];
//							temp[1] = nomeFile;
//							Runtime.getRuntime().exec(temp);	
//							break;
//						} catch (Exception ex) {
//						}
//				}
//				return true;
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			return false;
//		}
		return false;
	}
    
    
	public boolean doExportRTF() {
		
		String XSL_FO_GU; 
		String dtdName = documentManager.getDtdName();
		
//		if (dtdName.startsWith("nir") && !nirUtilDom.isDocCNR(null))    // documenti NIR
//			XSL_FO_GU = xslts.getXslt("pdf-gazzettaufficiale").getAbsolutePath();
//		else 
//			XSL_FO_GU = xslts.getXslt("pdf-cnr").getAbsolutePath();
//	
//		String osName = System.getProperty("os.name");
//
//		try {
//			JFileChooser fileChooser = new JFileChooser();
//			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//			String filterDesc = "Rich Text Format (*.rtf)";
//			String[] masks = new String[1];
//			masks[0] = ".*\\.[rR][tT][fF]$";
//			fileChooser.setFileFilter(new RegexpFileFilter(filterDesc, masks));
//			fileChooser.setCurrentDirectory(getLastExportAsFile() != null ? getLastExportAsFile().getParentFile() : null);
//
//			if (fileChooser.showSaveDialog(null) == JFileChooser.CANCEL_OPTION)
//				return false;
//			else {
//				File file = fileChooser.getSelectedFile();
//				if (!file.getAbsolutePath().matches("^.*\\.[rR][tT][fF]$"))
//					file = new File(file.getAbsolutePath() + ".rtf");
//
//				// Controlla l'esistenza del file
//				if (file.exists()) {
//					if (!utilMsg.msgYesNo("action.file.save.replace")) {
//						return false;
//					}
//				}
//
//				utilRtf.convertXML2RTF(documentManager.getDocumentAsDom(), XSL_FO_GU, file.getAbsolutePath());
//				lastExport = file.getAbsolutePath();
//
//				// FIXME prendere il path di OPENOFFICE/WORD dalle preference
//				if (osName.equalsIgnoreCase("linux")) {
//					if (Runtime.getRuntime().exec("oowrite2 " + file.getAbsolutePath()) == null)
//						Runtime.getRuntime().exec("oowriter2 " + file.getAbsolutePath());
//				} else if (osName.toLowerCase().matches("windows.*")) {
//					 String nomeFile = cmdWin(file.getAbsolutePath());
//					 Runtime.getRuntime().exec("cmd /C start " + nomeFile);					 
//				}
//				return true;
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			return false;
//		}
		
		return false;
		
	}



	// /////////////////////////////////////////// ListTextFieldEditor Interface
	public boolean checkData() {
		File file = fileTextField.getFile();
		return file != null && file.exists() && file.canRead();
	}

	public void clearFields() {
		fileTextField.set("");
	}

	public Component getAsComponent() {
		return fileTextField.getAsComponent();
	}

	public Object getElement() {
		return fileTextField.getFile().getAbsolutePath();
	}

	public String getErrorMessage() {
		return "file.export.error";
	}

	public Dimension getPreferredSize() {
		return null;
	}

	public void setElement(Object object) {
		String s = object.toString();
		fileTextField.set(s);
	}

	private void initFileChooser() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new RegexpFileFilter("*.html", ".*\\.htm[l]?"));
	}

	private File getLastExportAsFile() {
		try {
			return new File(lastExport);
		} catch (Exception ex) {
			return null;
		}
	}

	protected class ExportBrowserAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doExportBrowser();
		}
	}

	protected class ExportHTMLAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doExportHTML();
		}
	}

	protected class ExportPDFAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doExportPDF();
		}
	}

	
	
	protected class ExportRTFAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doExportRTF();
		}
	}
}





