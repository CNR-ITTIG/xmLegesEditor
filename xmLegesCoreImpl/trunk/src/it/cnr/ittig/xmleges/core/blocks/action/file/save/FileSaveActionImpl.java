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
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentSavedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
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
 * <li>file.save: descrizione dell'azione come specificato nell'ActionManager; </li>
 * <li>file.saveas: descrizione dell'azione come specificato nell'ActionManager;</li>
 * <li>action.file.save.replace: messaggio conferma se file esistente;</li>
 * <li>action.file.save.saved: testo status bar se file salvato;</li>
 * <li>action.file.save.error.file: testo messaggio se errore di salvataggio; </li>
 * <li>action.file.save.error.encoding: testo se encoding non supportato.</li>
 * </ul>
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.blocks.action.ActionManagerImpl
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

	FileSaveActionImpl.SaveAsAction saveAsAction;

	String defaultEncoding = "UTF-8";

	JFileChooser fileChooser;

	String filterDesc = "";

	String filterExt = "";

	String lastSaved = null;

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
		saveAsAction.setEnabled(!documentManager.isEmpty());
	}

	// //////////////////////////////////////////////// FileSaveAction Interface
	public boolean doSave() {
		if (documentManager.isNew())
			return doSaveAs();
		else
			return saveFile(new File(documentManager.getSourceName()));
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

	protected class SaveAsAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doSaveAs();
		}
	}
}
