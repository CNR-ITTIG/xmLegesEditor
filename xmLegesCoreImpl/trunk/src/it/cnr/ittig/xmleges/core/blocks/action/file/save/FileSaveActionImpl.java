package it.cnr.ittig.xmleges.core.blocks.action.file.save;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.file.open.FileOpenAction;
import it.cnr.ittig.xmleges.core.services.action.file.save.FileSaveAction;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentSavedEvent;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dom.extracttext.ExtractText;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.RegexpFileFilter;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.EventObject;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.file.save.FileSaveAction</code>.</h1>
 * <h1>Descrizione</h1>
 * Esegue il salvataggio su file del documento corrente. Se il documento &egrave; nuovo (<code>DocumentManager.isNew</code>)
 * chiede il nome e lo aggiunge alla lista dei file recenti (<code>FileOpenAction.addLast</code>).
 * <br>
 * Questa implementazione registra le azioni <code>file.save</code> e
 * <code>file.saveas</code> nell'ActionManager.
 * <h1>Configurazione</h1>
 * La configurazione pu&ograve; avere i seguenti tag:
 * <ul>
 * <li><code>&lt;encoding&gt;</code>: encoding da utilizzare;</li>
 * <li><code>&lt;filter&gt;</code>: filtro per la finestra di dialogo, contiene i tag:
 * <ul>
 * <li><code>&lt;description&gt;</code>: descrizione del filtro;</li>
 * <li><code>&lt;extension&gt;</code>: estenzione da utilizzare per il documento da salvare.</li>
 * </ul>
 * </li>
 * </ul>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.preference.PreferenceManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.bars.Bars:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.msg.UtilMsg:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.action.file.open.FileOpenAction:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>file.save</code>: descrizione dell'azione come specificato nell'ActionManager; </li>
 * <li><code>file.saveas</code>: descrizione dell'azione come specificato nell'ActionManager;</li>
 * <li><code>file.removepi</code>: descrizione dell'azione come specificato nell'ActionManager;</li>
 * <li><code>action.file.save.replace</code>: messaggio conferma se file esistente;</li>
 * <li><code>action.file.save.saved</code>: testo status bar se file salvato;</li>
 * <li><code>action.file.save.error.file</code>: testo messaggio se errore di salvataggio; </li>
 * <li><code>action.file.save.error.encoding</code>: testo se encoding non supportato;</li>
 * <li><code>action.file.replacepi.save</code>: testo se si vuole il salvataggio del documento.</li>
 * </ul>
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @see it.cnr.ittig.xmleges.core.services.preference.PreferenceManager
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FileSaveActionImpl implements FileSaveAction, EventManagerListener, Loggable, Serviceable, Configurable, Startable, Initializable {

	Logger logger;

	ActionManager actionManager;

	DocumentManager documentManager;

	PreferenceManager preferenceManager;

	Bars bars;

	UtilMsg utilMsg;

	FileOpenAction openAction;

	EventManager eventManager;

	FileSaveActionImpl.SaveAction saveAction;

	FileSaveActionImpl.RemoveAction removeAction;
	
	FileSaveActionImpl.SaveAsAction saveAsAction;

	String defaultEncoding = "UTF-8";

	JFileChooser fileChooser;

	String filterDesc = "";

	String filterExt = "";

	String lastSaved = null;

	ExtractText extractText;
	
	SelectionManager selectionManager;
	
	EditTransaction tr;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		openAction = (FileOpenAction) serviceManager.lookup(FileOpenAction.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) {
		try {
			defaultEncoding = configuration.getChild("encoding").getValue();
			logger.info("Default encoding: '" + defaultEncoding + '\'');
		} catch (Exception ex) {
			logger.info("No default encoding in configuration. Using: '" + defaultEncoding + '\'');
		}

		try {
			Configuration c = configuration.getChild("filter");
			// <filter>
			// <description>Text Files</description>
			// <extension>txt</extension>
			// </filter>
			if (c != null) {
				filterDesc = c.getChild("description").getValue();
				filterExt = c.getChild("extension").getValue();
			}
		} catch (Exception ex) {
			logger.info("No configured filter for save dialog");
		}
		initFileChooser();
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		saveAction = new FileSaveActionImpl.SaveAction();
		actionManager.registerAction("file.save", saveAction);
		removeAction = new FileSaveActionImpl.RemoveAction();
		actionManager.registerAction("file.removepi", removeAction);
		saveAsAction = new FileSaveActionImpl.SaveAsAction();
		actionManager.registerAction("file.saveas", saveAsAction);
		eventManager.addListener(this, DocumentChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		try {
			lastSaved = p.getProperty("lastsaved");
		} catch (Exception ex) {
		}
	}

	public void start() throws Exception {
		// TODO Auto-generated method stub
	}

	public void stop() throws java.lang.Exception {
		logger.debug("saving last saved...");
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		try {
			p.setProperty("lastsaved", lastSaved);
		} catch (Exception ex) {
		}
		preferenceManager.setPreference(getClass().getName(), p);
		logger.debug("saving last saved OK");
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		saveAction.setEnabled(!documentManager.isEmpty() && documentManager.isChanged());
		removeAction.setEnabled(!documentManager.isEmpty());
		saveAsAction.setEnabled(!documentManager.isEmpty());
	}

	// //////////////////////////////////////////////// FileSaveAction Interface
	public boolean doSave() {
		if (documentManager.isNew())
			return doSaveAs();
		else
			return saveFile(new File(documentManager.getSourceName()));
	}

	public boolean doRemovePI() {
		//effettua la rimozione delle PI dal documento
		removePI();
		//Chiedo se si vuole il salvataggio del documento
		if (!utilMsg.msgYesNo("action.file.replacepi.save")) 
			return true;
		if (documentManager.isNew())
			return doSaveAs();
		else
			return saveFile(new File(documentManager.getSourceName()));	
	}

	private boolean removePI() {
		
		if (documentManager.getDocumentAsDom()==null)
			return false;
		NodeIterator nI = ((DocumentTraversal)documentManager.getDocumentAsDom()).createNodeIterator(documentManager.getDocumentAsDom().getDocumentElement(),NodeFilter.SHOW_PROCESSING_INSTRUCTION,null,true);	
		Node node;
		try {
			tr = documentManager.beginEdit();	
			while ((node = nI.nextNode()) != null ) {
			
				if (node.getNodeType()==Node.PROCESSING_INSTRUCTION_NODE) {
				
					//Inserire qui i casi da gestire:
					//ATTUALMENTE: per ?rif --> si tiene la parte testuale
					//             altrimenti --> si butta via il nodo. 
				
					if (((ProcessingInstruction)node).getTarget().equals("rif")) {
						logger.debug("estraggo testo da " + node.toString());									
						setPlainText(node,getText(node));					
					}	
					else {
						logger.debug("butto il nodo " + node.toString());
						Node parent = node.getParentNode();
						parent.removeChild(node);
						UtilDom.mergeTextNodes(parent);
					}
				}
			}
			documentManager.commitEdit(tr);
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
		}

		return true;
	}

	private String getText(Node node) {
        String temp = ((ProcessingInstruction)node).getData().substring(((ProcessingInstruction)node).getData().indexOf(">"),((ProcessingInstruction)node).getData().length());
		return (temp.substring(1,temp.indexOf("<")));		
	}

	private Node setPlainText(Node node, String plainText) {

		Node container = node.getParentNode(); // contenitore del testo
		Node ritorno = null;
 	    if (null != node.getPreviousSibling() && UtilDom.isTextNode(node.getPreviousSibling()) && null != node.getNextSibling() && UtilDom.isTextNode(node.getNextSibling())) {
			node.getPreviousSibling().setNodeValue(node.getPreviousSibling().getNodeValue() + " " + plainText + " " + node.getNextSibling().getNodeValue());
			ritorno = node.getPreviousSibling();
			container.removeChild(node.getNextSibling());
			container.removeChild(node);			
			return ritorno;
		} else { //Ho solo il fratello DX di tipo text
		  if (null != node.getNextSibling() && UtilDom.isTextNode(node.getNextSibling())) {
				node.getNextSibling().setNodeValue(plainText + " " + node.getNextSibling().getNodeValue());
				ritorno = node.getNextSibling();
				container.removeChild(node);			
				return ritorno;							
		  } else { //Ho solo il fratello SX di tipo text
			if (null != node.getPreviousSibling() && UtilDom.isTextNode(node.getPreviousSibling())) {
				node.getPreviousSibling().setNodeValue(node.getPreviousSibling().getNodeValue() + " " + plainText);
				ritorno = node.getPreviousSibling();
				container.removeChild(node);			
				return ritorno;							
			} else { //Non ho fratelli e/o ho fratelli non text
				     node.setNodeValue(plainText);
				     return node;
			  }
			}
		 }			
	}
	
	
	public boolean doSaveAs() {
		if (!documentManager.isNew())
			fileChooser.setCurrentDirectory(new File(documentManager.getSourceName()));
		else {
			initFileChooser();
		}
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

			File fileWithExtension;

			// Aggiungi l'estensione se necessario
			String fileName = fileChooser.getSelectedFile().getName();
			if (!fileName.endsWith(filterExt)) {
				fileWithExtension = new File(fileChooser.getSelectedFile().getAbsolutePath() + "." + filterExt);
			} else {
				fileWithExtension = fileChooser.getSelectedFile();
			}

			// Controlla l'esistenza del file
			if (fileWithExtension.exists()) {
				if (utilMsg.msgYesNo("action.file.save.replace")) {
					return saveFile(fileWithExtension);
				}
			} else {
				return saveFile(fileWithExtension);
			}
		}
		return false;
	}

	private boolean saveFile(File file) {
		String encoding;
		if (documentManager.getEncoding() == null) {
			logger.warn("No encoding found. Using default:" + defaultEncoding);
			encoding = defaultEncoding;
		} else
			encoding = documentManager.getEncoding();
		DOMWriter domWriter = new DOMWriter();
		domWriter.setCanonical(false);
		domWriter.setFormat(true);
		try {
			domWriter.setOutput(file, encoding);
			domWriter.write(documentManager.getDocumentAsDom());
			documentManager.setChanged(false);
			documentManager.setSourceName(file.getAbsolutePath());
			documentManager.setNew(false);
			bars.getStatusBar().setText("action.file.save.saved", "info");
			openAction.addLast(file.getAbsolutePath());
			lastSaved = file.getAbsolutePath();
			eventManager.fireEvent(new DocumentSavedEvent(this, documentManager.getDocumentAsDom()));
			return true;
		} catch (UnsupportedEncodingException ex) {
			utilMsg.msgError("action.file.save.error.encoding");
			logger.error(ex.toString(), ex);
		} catch (FileNotFoundException ex) {
			utilMsg.msgError("action.file.save.error.file");
			logger.error(ex.toString(), ex);
		}
		return false;
	}

	private void initFileChooser() {
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setCurrentDirectory(getLastSavedAsFile() != null ? getLastSavedAsFile().getParentFile() : null);

		if (filterExt != null && !"".equals(filterExt)) {
			String[] masks = new String[1];
			masks[0] = ".*\\." + filterExt;
			fileChooser.setFileFilter(new RegexpFileFilter(filterDesc, masks));
		}
	}

	private File getLastSavedAsFile() {
		try {
			return new File(lastSaved);
		} catch (Exception ex) {
			return null;
		}
	}

	protected class SaveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doSave();
		}
	}

	protected class RemoveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doRemovePI();
		}
	}
	
	protected class SaveAsAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doSaveAs();
		}
	}
}
