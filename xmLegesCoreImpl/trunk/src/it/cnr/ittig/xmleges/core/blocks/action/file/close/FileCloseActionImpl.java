package it.cnr.ittig.xmleges.core.blocks.action.file.close;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.file.close.FileCloseAction;
import it.cnr.ittig.xmleges.core.services.action.file.save.FileSaveAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.file.close.FileCloseAction</code>. </h1>
 * <h1>Descrizione</h1>
 * Chiude il documento corrente. Se il documento &egrave; stato modificato (
 * <code>DocumentManager.isChanged</code> effettua l'operazione di salvataggio chiamando
 * l'azione <code>FileSavaAction.doSave</code>.<br>
 * Questa implementazione registra le azioni <code>file.close</code> nell'ActionManager.
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.action.file.save.FileSaveAction:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.msg.UtilMsg:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>file.close: descrizione dell'azione come specificato nell'ActionManager; </li>
 * <li>action.file.close.save: messaggio conferma se file modificato.</li>
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class FileCloseActionImpl extends AbstractAction implements FileCloseAction, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	DocumentManager documentManager;

	EventManager eventManager;

	FileSaveAction saveAction;

	UtilMsg utilMsg;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		saveAction = (FileSaveAction) serviceManager.lookup(FileSaveAction.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("file.close", this);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		setEnabled(!documentManager.isEmpty());
	}

	// /////////////////////////////////////////////// FileCloseAction Interface
	public boolean doClose() {
		int close = 0;
		if (documentManager.isChanged() && (close = utilMsg.msgYesNoCancel("action.file.close.save")) == 1)
			if (saveAction.doSave()) {
				documentManager.close();
				setEnabled(!documentManager.isEmpty());
			}
		if (close == 0) {
			documentManager.close();
			setEnabled(!documentManager.isEmpty());
		}
		return true;
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		setEnabled(!documentManager.isEmpty());
	}

	public void actionPerformed(ActionEvent e) {
		doClose();
	}

}
