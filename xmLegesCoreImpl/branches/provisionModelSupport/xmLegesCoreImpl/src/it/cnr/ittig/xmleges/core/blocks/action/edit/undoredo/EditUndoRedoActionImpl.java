package it.cnr.ittig.xmleges.core.blocks.action.edit.undoredo;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.edit.undoredo.EditUndoRedoAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.edit.undoredo.EditUndoRedoAction</code>. </h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra le azioni <code>edit.undo</code> e
 * <code>edit.redo</code> nell'ActionManager.
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.event.EventManager:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>edit.undo: descrizione dell'azione come specificato nell'ActionManager; </li>
 * <li>edit.redo: descrizione dell'azione come specificato nell'ActionManager; </li>
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
 * @see it.cnr.ittig.xmleges.core.services.document.DocumentManager
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class EditUndoRedoActionImpl implements EditUndoRedoAction, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	DocumentManager documentManager;

	EventManager eventManager;

	EditUndoRedoActionImpl.UndoAction undoAction;

	EditUndoRedoActionImpl.RedoAction redoAction;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		undoAction = new EditUndoRedoActionImpl.UndoAction();
		actionManager.registerAction("edit.undo", undoAction);
		redoAction = new EditUndoRedoActionImpl.RedoAction();
		actionManager.registerAction("edit.redo", redoAction);
		eventManager.addListener(this, DocumentChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		enableActions();
	}

	// //////////////////////////////////////////// EditUndoRedoAction Interface
	public void doUndo() {
		logger.debug("Calling undo...");
		documentManager.undo();
		logger.debug("Calling undo OK");
	}

	public void doRedo() {
		logger.debug("Calling redo...");
		documentManager.redo();
		logger.debug("Calling redo OK");
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		enableActions();
	}

	protected void enableActions() {
		undoAction.setEnabled(documentManager.canUndo());
		redoAction.setEnabled(documentManager.canRedo());
	}

	protected class UndoAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doUndo();
		}
	}

	protected class RedoAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doRedo();
		}
	}
}
