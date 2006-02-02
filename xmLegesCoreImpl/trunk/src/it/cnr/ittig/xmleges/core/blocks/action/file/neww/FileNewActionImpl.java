package it.cnr.ittig.xmleges.core.blocks.action.file.neww;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.file.neww.FileNewAction;
import it.cnr.ittig.xmleges.core.services.action.file.open.FileOpenAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.file.neww.FileNewAction</code>.</h1>
 * <h1>Descrizione</h1>
 * Il nuovo documento viene creato in base al template specificato nella configurazione.
 * Se non &grave; presente apre una finestra di dialogo che permette di scegliere il
 * template apportuno. La directory dei template &egrave; memorizzata nel
 * PreferenceManager (sezione FileNewActionImpl) e considerata di default. <br>
 * Questa implementazione registra l'azione <code>file.new</code> nell'ActionManager.
 * <br>
 * 
 * <h1>Configurazione</h1>
 * La configurazione pu&ograve; avere i seguenti tag (tutti opzionali):
 * <ul>
 * <li><code>&lt;file&gt;</code>: indica il template per i nuovi documenti; </li>
 * <li><code>&lt;dir&gt;</code>: indica la directory per i template.</li>
 * </ul>
 * Esempio:
 * 
 * <pre>
 *     &lt;dir&gt;./templates&lt;/dir&gt;
 * </pre>
 * 
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.action.file.open.FileOpenAction:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.preference.PreferenceManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>file.new: descrizione dell'azione come specificato nell'ActionManager; </li>
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
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.services.preference.PreferenceManager
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FileNewActionImpl extends AbstractAction implements FileNewAction, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	ActionManager actionManager;

	PreferenceManager preferenceManager;

	DocumentManager documentManager;

	Frame frame;

	FileOpenAction fileOpenAction;

	File templateFile;

	File templateDir;

	JFileChooser fileChooser;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		fileOpenAction = (FileOpenAction) serviceManager.lookup(FileOpenAction.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		frame = (Frame) serviceManager.lookup(Frame.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		try {
			templateFile = new File(configuration.getChild("file").getValue());
		} catch (Exception ex) {
			logger.debug("No tag <file> in configuration");
		}
		try {
			templateDir = new File(configuration.getChild("dir").getValue());
		} catch (Exception ex) {
			logger.debug("No tag <dir> in configuration");
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("file.new", this);
		Properties props = preferenceManager.getPreferenceAsProperties("FileNewActionImpl");
		if (props.containsKey("file")) {
			File file = new File(props.getProperty("file"));
			if (file.exists() && file.isFile()) {
				templateFile = file;
				logger.debug("Template from preference:" + file.getAbsolutePath());
			} else
				logger.debug("No template in preference:" + file.getAbsolutePath());

		}
		if (props.containsKey("dir")) {
			File dir = new File(props.getProperty("dir"));
			if (dir.exists() && dir.isDirectory()) {
				templateDir = dir;
				logger.debug("Dir from preference:" + dir.getAbsolutePath());
			} else
				logger.debug("No dir in preference:" + dir.getAbsolutePath());
		}
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		File dir;
		if (templateDir != null && templateDir.exists() && templateDir.isDirectory())
			dir = templateDir;
		else
			dir = new File(System.getProperty("user.home"));
		fileChooser.setCurrentDirectory(dir);
	}

	// ///////////////////////////////////////////////// FileNewAction Interface
	public boolean doNew() {
		logger.debug("file:" + templateFile);
		logger.debug("dir:" + templateDir);
		String toOpen;
		if (templateFile != null && templateFile.exists() && templateFile.isFile())
			toOpen = templateFile.getAbsolutePath();
		else if (fileChooser.showOpenDialog(frame.getComponent()) == JFileChooser.APPROVE_OPTION) {
			toOpen = fileChooser.getSelectedFile().getAbsolutePath();
			Properties props = preferenceManager.getPreferenceAsProperties("FileNewActionImpl");
			props.setProperty("dir", fileChooser.getCurrentDirectory().getAbsolutePath());
			preferenceManager.setPreference("FileNewActionImpl", props);
		} else {
			logger.debug("Cancel pressed.");
			return false;
		}
		logger.debug("Template: " + toOpen);
		if (fileOpenAction.doOpen(toOpen)) {
			documentManager.setChanged(false);
			documentManager.setNew(true);
			return true;
		} else
			return false;
	}

	// ////////////////////////////////////////////////// Extends AbstractAction
	public void actionPerformed(ActionEvent e) {
		doNew();
	}
}
