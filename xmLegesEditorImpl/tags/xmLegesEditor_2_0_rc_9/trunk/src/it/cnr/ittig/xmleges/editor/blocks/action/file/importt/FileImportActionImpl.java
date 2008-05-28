package it.cnr.ittig.xmleges.editor.blocks.action.file.importt;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.file.importt.FileImportAction;
import it.cnr.ittig.xmleges.core.services.action.file.open.FileOpenAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.util.file.RegexpFileFilter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.action.rinumerazione.RinumerazioneAction;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.form.xmleges.marker.XmLegesMarkerForm;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.file.importt.FileImportAction</code>.</h1>
 * Questa implementazione registra l'azione <code>file.import</code>
 * nell'ActionManager.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FileImportActionImpl extends AbstractAction implements FileImportAction, Loggable, Serviceable, Startable, Initializable {
	Logger logger;

	Frame frame;
	
	ActionManager actionManager;

	DocumentManager documentManager;

	PreferenceManager preferenceManager;

	XmLegesMarkerForm parser;

	JFileChooser fileChooser;

	FileOpenAction fileOpenAction;

	String lastImport = null;
	
	Rinumerazione rinumerazione;
	RinumerazioneAction rinumerazioneAction;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		parser = (XmLegesMarkerForm) serviceManager.lookup(XmLegesMarkerForm.class);
		fileOpenAction = (FileOpenAction) serviceManager.lookup(FileOpenAction.class);
		
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		rinumerazioneAction = (RinumerazioneAction) serviceManager.lookup(RinumerazioneAction.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("file.import", this);
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		try {
			lastImport = p.getProperty("lastimport");
		} catch (Exception ex) {
		}
		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new RegexpFileFilter("doc, html, pdf, txt", ".*\\.(doc|html?|pdf|txt)"));
	}

	public void start() throws Exception {
		// TODO Auto-generated method stub
	}

	public void stop() throws java.lang.Exception {
		logger.debug("saving last imported...");
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		try {
			p.setProperty("lastimport", lastImport);
		} catch (Exception ex) {
		}
		preferenceManager.setPreference(getClass().getName(), p);
		logger.debug("saving last imported OK");
	}

	// ////////////////////////////////////////////// FileImportAction Interface
	public boolean doImport() {
		fileChooser.setCurrentDirectory(getLastImportAsFile() != null ? getLastImportAsFile().getParentFile() : null);
		if (fileChooser.showOpenDialog(frame.getComponent()) == JFileChooser.APPROVE_OPTION) {
			parser.parse(fileChooser.getSelectedFile());
			lastImport = fileChooser.getSelectedFile().getAbsolutePath();
			if (parser.isParseOk()) {
				UtilFile.copyFileInTemp(parser.getResult(), "import.xml");
				if (fileOpenAction.doOpen(UtilFile.getFileFromTemp("import.xml").getAbsolutePath(), false)) {
					documentManager.setNew(true);
					
					//imposto la Rinumerazione DISATTIVA (se non lo � gi�)
					if (rinumerazione.isRinumerazione())
						rinumerazioneAction.doSetRinumerazione(false);
					
					return true;
				}
				return false;
			}
		}
		return false;
	}

	private File getLastImportAsFile() {
		try {
			return new File(lastImport);
		} catch (Exception ex) {
			return null;
		}
	}

	// ////////////////////////////////////////////////// AbstractAction Extends
	public void actionPerformed(ActionEvent e) {
		doImport();
	}
}
