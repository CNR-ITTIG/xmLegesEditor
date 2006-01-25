package it.cnr.ittig.xmleges.core.blocks.action.file.validator;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.file.open.FileOpenAction;
import it.cnr.ittig.xmleges.core.services.action.file.save.FileSaveAction;
import it.cnr.ittig.xmleges.core.services.action.file.validator.FileValidatorAction;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.file.validator.FileValidatorAction</code>.</h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra nell'ActionManager l'aziona <code>file.validator</code>.
 * <h1>Configurazione</h1>
 * Nessuna
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
 * <li>file.validator: descrizione dell'azione come specificato nell'ActionManager; </li>
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FileValidatorActionImpl implements FileValidatorAction, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	DocumentManager documentManager;

	EventManager eventManager;

	Bars bars;

	ActionManager actionManager;

	FileOpenAction fileOpenAction;

	FileSaveAction fileSaveAction;

	UtilMsg utilMsg;

	ShowValidationMsgAction showValidationMsgAction = new ShowValidationMsgAction();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		fileOpenAction = (FileOpenAction) serviceManager.lookup(FileOpenAction.class);
		fileSaveAction = (FileSaveAction) serviceManager.lookup(FileSaveAction.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		actionManager.registerAction("file.validator", showValidationMsgAction);
		bars.getStatusBar().setText("file.validator", "file-validator");
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof DocumentOpenedEvent)
			showMessage(false);
		else if (event instanceof DocumentClosedEvent)
			bars.getStatusBar().setText("file.validator", "file-validator");
	}

	// //////////////////////////////////////////////// FileOpenAction Interface
	public boolean doValidation() {
		if (fileSaveAction.doSave())
			return fileOpenAction.doOpen(documentManager.getSourceName(), false);
		else
			return false;
	}

	protected void showMessage(boolean yesNo) {
		String[] errors = documentManager.getErrors();
		bars.getStatusBar().setDefaultBackground("file-validator");
		bars.getStatusBar().setDefaultForeground("file-validator");
		if (documentManager.isEmpty())
			bars.getStatusBar().setText("file.validator", "file-validator");
		else if (documentManager.hasErrors()) {
			bars.getStatusBar().setText("file.validator.novalid", "file-validator");
			bars.getStatusBar().setBackground(Color.RED, "file-validator");
			bars.getStatusBar().setForeground(Color.RED, "file-validator");
			// TODO i18n
			StringBuffer sb = new StringBuffer("Documento non valido per i seguenti motivi:\n");
			for (int i = 0; i < errors.length; i++) {
				sb.append(errors[i]);
				sb.append('\n');
			}
			if (!yesNo) {
				// TODO I18n
				sb.append("\nUtilizzare NirEditor solo per rendere valido il documento.");
				utilMsg.msgError(sb.toString());
			} else {
				// TODO I18n
				sb.append("\nVerificare la validit? del documento?");
				if (utilMsg.msgYesNo(sb.toString()))
					doValidation();
			}
		} else
			bars.getStatusBar().setText("file.validator.valid", "file-validator");
	}

	/**
	 * Azione per la verifica del documento.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>Lincense: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 * 
	 */
	protected class ShowValidationMsgAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (documentManager.hasErrors())
				showMessage(true);
		}
	}

}
