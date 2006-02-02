package it.cnr.ittig.xmleges.core.blocks.action.file.open;

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
import it.cnr.ittig.xmleges.core.services.action.file.open.FileOpenAction;
import it.cnr.ittig.xmleges.core.services.action.file.save.FileSaveAction;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.file.RegexpFileFilter;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.file.open.FileOpenAction</code>.</h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra nell'ActionManager l'azione <code>file.open</code> e
 * le azioni da <code>file.openrecent.0</code> fino a <code>file.openrecent.n</code>
 * dove <i>n </i> &egrave; il numero massimo di documenti recenti da memorizzare (default
 * 4). <br>
 * I file aperti sono memorizzati tramite <code>PreferenceManager</code> nella sezione
 * <b>ActionFileOpenImpl </b>.
 * <h1>Configurazione</h1>
 * La configurazione pu&ograve; avere i seguenti tag:
 * <ul>
 * <li><code>&lt;maxlast&gt;</code>: numero di documenti recenti da memorizzare;</li>
 * <li><code>&lt;filter&gt;</code>: filtro per la finestra di dialogo, contiene i tag:
 * <ul>
 * <li><code>&lt;description&gt;</code>: descrizione del filtro;</li>
 * <li><code>&lt;mask&gt;</code>: maschera (stringa contenente espressione regolare).</li>
 * </ul>
 * </li>
 * </ul>
 * Esempio: <br>
 * 
 * <pre>
 *     &lt;maxlast&gt;8&lt;/maxlast&gt;
 *     &lt;filter&gt;
 *       &lt;description&gt;Xml Files&lt;/description&gt;
 *       &lt;mask&gt;*.xml&lt;/mask&gt;
 *     &lt;/filter&gt;
 * </pre>
 * 
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.action.file.save.FileSaveAction:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.preference.PreferenceManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.msg.UtilMsg:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>action.file.open.save: messaggio su nuovo documento se corrente modificato;</li>
 * <li>action.file.open.error: messaggio se errore in apertura sorgente;</li>
 * <li>file.open: descrizione dell'azione come specificato nell'ActionManager; </li>
 * <li>file.openrecent.x: descrizione dell'azione (x varia da 0 fino a maxlast) come
 * specificato nell'ActionManager.</li>
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
 * @see it.cnr.ittig.xmleges.core.blocks.action.ActionManagerImpl
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FileOpenActionImpl implements FileOpenAction, Loggable, Serviceable, Configurable, Initializable, Startable {
	Logger logger;

	Frame frame;

	DocumentManager docManager;

	PreferenceManager preferenceManager;

	Bars bars;

	FileSaveAction fileSaveAction;

	ActionManager actionManager;

	UtilMsg utilMsg;

	JFileChooser fileChooser = new JFileChooser();

	OpenLastAction[] lasts;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		docManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		fileSaveAction = (FileSaveAction) serviceManager.lookup(FileSaveAction.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		Configuration c = configuration.getChild("maxlast");
		if (c != null)
			lasts = new OpenLastAction[c.getValueAsInteger(4)];
		else
			lasts = new OpenLastAction[4];
		logger.debug("maxlast:" + lasts.length);
		c = configuration.getChild("filter");
		// <filter>
		// <mask>*.txt</mask>
		// <description>Text Files</description>
		// </filter>
		if (c != null) {
			String desc = c.getChild("description").getValue();
			Configuration cs[] = c.getChildren("mask");
			String[] masks = new String[cs.length];
			for (int i = 0; i < cs.length; i++)
				masks[i] = cs[i].getValue();
			fileChooser.setFileFilter(new RegexpFileFilter(desc, masks));
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		actionManager.registerAction("file.open", new OpenAction());
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		for (int i = 0; i < lasts.length; i++) {
			String actionName = "file.openrecent." + i;
			lasts[i] = new OpenLastAction();
			lasts[i].putValue(Action.ACTION_COMMAND_KEY, actionName);
			try {
				lasts[i].setSource(p.getProperty("last" + i));
			} catch (Exception ex) {
			}
			actionManager.registerAction(actionName, lasts[i]);
		}
	}

	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws java.lang.Exception {
	}

	public void stop() throws java.lang.Exception {
		logger.debug("saving open recent...");
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		for (int i = 0; i < lasts.length; i++)
			try {
				p.setProperty("last" + i, lasts[i].getSource());
			} catch (Exception ex) {
			}
		preferenceManager.setPreference(getClass().getName(), p);
		logger.debug("saving open recent OK");
	}

	// //////////////////////////////////////////////// FileOpenAction Interface
	public boolean doOpen() {
		fileChooser.setCurrentDirectory(getLastOpenedAsFile() != null ? getLastOpenedAsFile().getParentFile() : null);
		if (fileChooser.showOpenDialog(frame.getComponent()) == JFileChooser.APPROVE_OPTION)
			return doOpen(fileChooser.getSelectedFile().getAbsolutePath());
		return false;
	}

	public boolean doOpen(String file) {
		return doOpen(file, true);
	}

	public boolean doOpen(String file, boolean addLast) {
		frame.setInteraction(false);
		if (!docManager.isEmpty() && docManager.isChanged() && utilMsg.msgWarning("action.file.open.save"))
			fileSaveAction.doSave();
		logger.debug("File to open: " + file);
		if (!docManager.openSource(file, false)) {
			frame.setInteraction(true);
			utilMsg.msgError("action.file.open.error");
		}
		docManager.setChanged(false);
		if (docManager.isEmpty()) {
			frame.setInteraction(true);
			return false;
		}
		if (addLast)
			addLast(file);
		frame.setInteraction(true);
		return true;
	}

	public void addLast(String source) {
		int last;
		for (last = lasts.length - 1; last >= 0; last--) {
			String s = lasts[last].getSource();
			if (s != null && s.equalsIgnoreCase(source))
				break;
		}
		if (last == -1)
			last = lasts.length - 1;
		for (int i = last; i > 0; i--)
			lasts[i].setSource(lasts[i - 1].getSource());
		lasts[0].setSource(source);
	}

	private File getLastOpenedAsFile() {
		try {
			return new File(lasts[0].getSource());
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Azione per l'apertura di un file.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 * 
	 */
	protected class OpenAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			doOpen();
		}
	}

	/**
	 * Azione per gli ultimi file aperti.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 * 
	 */
	protected class OpenLastAction extends AbstractAction {
		String source;

		public OpenLastAction() {
			setEnabled(false);
		}

		public String getSource() {
			return source;
		}

		public void setSource(String string) {
			source = string;
			if (source != null) {
				putValue(Action.NAME, source);
				setEnabled(true);
			} else {
				putValue(Action.NAME, " ");
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (source != null) {
				logger.debug("open:" + source);
				doOpen(source);
			} else
				logger.warn("open source null");

		}

	}

}
