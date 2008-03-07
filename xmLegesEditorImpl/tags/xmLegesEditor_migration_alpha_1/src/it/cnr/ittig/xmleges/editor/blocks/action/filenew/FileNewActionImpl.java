package it.cnr.ittig.xmleges.editor.blocks.action.filenew;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.file.neww.FileNewAction;
import it.cnr.ittig.xmleges.core.services.action.file.open.FileOpenAction;
import it.cnr.ittig.xmleges.core.services.action.file.save.FileSaveAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.editor.services.action.rinumerazione.RinumerazioneAction;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.form.filenew.FileNewForm;
import it.cnr.ittig.xmleges.editor.services.template.Template;
import it.cnr.ittig.xmleges.editor.services.template.TemplateException;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.file.neww.NirFileNewAction</code>.</h1>
 * <h1>Descrizione</h1>
 * Servizio per creare un nuovo documento in base ad un template XML e una DTD.
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.form.filenew.FileNewForm:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.template.Template:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.action.file.open.FileOpenAction:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.action.file.save.FileSaveAction:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg:1.0</li> 
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>action.file.open.save</code>: Messaggio di salvataggio file modificato;</li>
 * <li><code>file.new</code>: descrizione dell'azione come specificato nell'ActionManager; </li>
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FileNewActionImpl extends AbstractAction implements FileNewAction, Loggable, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	FileNewForm fileNewForm;

	Template template;

	DocumentManager dm;

	UtilMsg utilMsg;

	FileSaveAction fileSaveAction;

	FileOpenAction fileOpenAction;
	
	//Rinumerazione rinumerazione;
	//RinumerazioneAction rinumerazioneAction;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		fileNewForm = (FileNewForm) serviceManager.lookup(FileNewForm.class);
		template = (Template) serviceManager.lookup(Template.class);
		dm = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		fileOpenAction = (FileOpenAction) serviceManager.lookup(FileOpenAction.class);
		fileSaveAction = (FileSaveAction) serviceManager.lookup(FileSaveAction.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		
		//rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		//rinumerazioneAction = (RinumerazioneAction) serviceManager.lookup(RinumerazioneAction.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("file.new", this);
	}

	public boolean doNew() {
		if (!dm.isEmpty() && dm.isChanged() && utilMsg.msgWarning("action.file.open.save")) {
			fileSaveAction.doSave();
			dm.close();
		}
		File templatefile;
		fileNewForm.openForm();
		if (fileNewForm.isOKClicked()) {
			try {
				templatefile = template.getNirTemplate(fileNewForm.getSelectedTemplate(), fileNewForm.getSelectedDTD());
				if (fileOpenAction.doOpen(templatefile.getAbsolutePath(), false)) {
					dm.setNew(true);
					
					//imposto la Rinumerazione ATTIVA (se non lo � gi�)
					//if (!rinumerazione.isRinumerazione())
					//	rinumerazioneAction.doSetRinumerazione(true);
					
					return true;
				} else
					return false;
			} catch (TemplateException e) {
				logger.error("Unable to open template file " + fileNewForm.getSelectedTemplate());
				return false;
			}
		}
		return false;
	}

	// ////////////////////////////////////////////////// Extends AbstractAction
	public void actionPerformed(ActionEvent e) {
		doNew();
	}

}
