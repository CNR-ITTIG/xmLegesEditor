package it.cnr.ittig.xmleges.core.blocks.action.file.exit;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.file.exit.FileExitAction;
import it.cnr.ittig.xmleges.core.services.action.file.save.FileSaveAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.exit.ExitAction</code>.</h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra l'azione <code>file.exit</code> nell'ActionManager.
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.action.file.save.FileSaveAction:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.msg.UtilMsg:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>action.file.exit.save: messaggio salvare documento se modificato;</li>
 * <li>file.exit: descrizione azione come descritto in ActionManager.</li>
 * </ul>
 * 
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
public class FileExitActionImpl extends AbstractAction implements FileExitAction, Loggable, Serviceable, Initializable {

	Logger logger;

	ActionManager actionManager;

	FileSaveAction saveAction;

	DocumentManager documentManager;

	UtilMsg utilMsg;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		saveAction = (FileSaveAction) serviceManager.lookup(FileSaveAction.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("file.exit", this);
	}

	// //////////////////////////////////////////////// FileExitAction Interface
	public void doExit() {
		int exit = 0;
		if (!documentManager.isEmpty() && documentManager.isChanged() && (exit = utilMsg.msgYesNoCancel("action.file.exit.save")) == 1)
			if (saveAction.doSave())
				System.exit(0);
		if (exit == 0)
			System.exit(0);
	}

	public void actionPerformed(ActionEvent e) {
		doExit();
	}
}
