package it.cnr.ittig.xmleges.core.blocks.action.edit.cutcopypaste;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.edit.cutcopypaste.EditCutCopyPasteAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.Pane;
import it.cnr.ittig.xmleges.core.services.frame.PaneActivatedEvent;
import it.cnr.ittig.xmleges.core.services.frame.PaneEvent;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.frame.PaneStatusChangedEvent;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.edit.cutcopypaste.EditCutCopyPasteAction</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra le azioni <code>edit.cut</code>,
 * <code>edit.copy</code>,<code>edit.paste</code>, <code>edit.pastetext</code> e
 * <code>edit.delete</code> nell'ActionManager. <br>
 * L'attivazione delle varie azioni dipendono dal pannello corrente. Questa
 * implementazione attende l'evento <code>PaneActivatedEvent</code> emesso da
 * <code>Frame</code> per determinare il pannello attivo.
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.event.EventManager:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * Dipendente dall'implementazione dell'ActionManager per i nomi delle azioni: edit.cut,
 * edit.copy, edit.paste, edit.pastetext e edit.delete.
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
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class EditCutCopyPasteActionImpl implements EditCutCopyPasteAction, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	Frame frame;

	ActionManager actionManager;

	EventManager eventManager;

	EditCutCopyPasteActionImpl.CutAction cutAction;

	EditCutCopyPasteActionImpl.CopyAction copyAction;

	EditCutCopyPasteActionImpl.PasteAction pasteAction;

	EditCutCopyPasteActionImpl.PasteTextAction pasteTextAction;

	EditCutCopyPasteActionImpl.DeleteAction deleteAction;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		cutAction = new EditCutCopyPasteActionImpl.CutAction();
		actionManager.registerAction("edit.cut", cutAction);
		copyAction = new EditCutCopyPasteActionImpl.CopyAction();
		actionManager.registerAction("edit.copy", copyAction);
		pasteAction = new EditCutCopyPasteActionImpl.PasteAction();
		actionManager.registerAction("edit.paste", pasteAction);
		pasteTextAction = new EditCutCopyPasteActionImpl.PasteTextAction();
		actionManager.registerAction("edit.pastetext", pasteTextAction);
		deleteAction = new EditCutCopyPasteActionImpl.DeleteAction();
		actionManager.registerAction("edit.delete", deleteAction);
		eventManager.addListener(this, PaneActivatedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, PaneStatusChangedEvent.class);
		enableActions(null);
	}

	// //////////////////////////////////////// EditCutCopyPasteAction Interface
	Pane activePane = null;

	public void doCut() {
		try {
			activePane.cut();
		} catch (PaneException ex) {
			logger.error("Error in cut:", ex);
		}
	}

	public void doCopy() {
		try {
			activePane.copy();
		} catch (PaneException ex) {
			logger.error("Error in copy:", ex);
		}
	}

	public void doPaste() {
		try {
			activePane.paste();
		} catch (PaneException ex) {
			logger.error("Error in paste:", ex);
		}
	}

	public void doPasteText() {
		try {
			activePane.pasteAsText();
		} catch (PaneException ex) {
			logger.error("Error in paste as text:", ex);
		}
	}

	public void doDelete() {
		try {
			activePane.delete();
		} catch (PaneException ex) {
			logger.error("Error in delete:", ex);
		}
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof PaneEvent) {
			activePane = frame.getActivePane();
			logger.debug("Event from:" + activePane);
			enableActions(activePane);
		} else
			enableActions(null);
	}

	protected void enableActions(Pane pane) {
		if (pane == null) {
			cutAction.setEnabled(false);
			copyAction.setEnabled(false);
			pasteAction.setEnabled(false);
			pasteTextAction.setEnabled(false);
			deleteAction.setEnabled(false);
		} else {
			cutAction.setEnabled(pane.canCut());
			copyAction.setEnabled(pane.canCopy());
			pasteAction.setEnabled(pane.canPaste());
			pasteTextAction.setEnabled(pane.canPasteAsText());
			deleteAction.setEnabled(pane.canDelete());
		}
	}

	protected class CutAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doCut();
		}
	}

	protected class CopyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doCopy();
		}
	}

	protected class PasteAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doPaste();
		}
	}

	protected class PasteTextAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doPasteText();
		}
	}

	protected class DeleteAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doDelete();
		}
	}

}
