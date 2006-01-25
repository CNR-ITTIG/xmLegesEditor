package it.cnr.ittig.xmleges.core.blocks.action.edit.comment;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.edit.comment.CommentAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.dom.comment.Comment;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.edit.comment.CommentAction</code>. </h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra le azioni <code>edit.comment</code> e
 * <code>edit.procinstr</code> nell'ActionManager. <br>
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>dom-comment</li>;
 * <li>action-manager</li>;
 * <li>event-manager</li>;
 * <li>selection-manager</li>.
 * </ul>
 * <h1>I18n</h1>
 * Dipendente dall'implementazione dell'ActionManager per i nomi delle azioni edit.comment
 * e edit.procinstr.
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
 * @see it.cnr.ittig.xmleges.core.services.dom.comment.Comment
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @see it.cnr.ittig.xmleges.core.services.selection.SelectionManager
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class CommentActionImpl implements CommentAction, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	Comment comment;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	CommentActionImpl.ComAction comAction;

	CommentActionImpl.PiAction piAction;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		comment = (Comment) serviceManager.lookup(Comment.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		comAction = new ComAction();
		comAction.setEnabled(false);
		actionManager.registerAction("edit.comment", comAction);
		piAction = new PiAction();
		piAction.setEnabled(false);
		actionManager.registerAction("edit.procinstr", piAction);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
	}

	// /////////////////////////////////////////////// CommentAction Interface
	public boolean doComment() {
		Node n = selectionManager.getActiveNode();
		int s = selectionManager.getTextSelectionStart();
		int e = selectionManager.getTextSelectionEnd();
		if (comment.canInsertComment(n, s, e)) {
			Node c = comment.insertComment(n, s, e);
			if (c != null) {
				selectionManager.setActiveNode(null, c);
				return true;
			}
		}
		return false;
	}

	public boolean doProcessingInstruction() {
		Node n = selectionManager.getActiveNode();
		int s = selectionManager.getTextSelectionStart();
		int e = selectionManager.getTextSelectionEnd();
		if (comment.canInsertPI(n, s, e)) {
			Node c = comment.insertPI(n, "error", s, e);
			if (c != null) {
				selectionManager.setActiveNode(null, c);
				return true;
			}
		}
		return false;
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof SelectionChangedEvent) {
			SelectionChangedEvent evt = (SelectionChangedEvent) event;
			if (evt.isActiveNodeChanged()) {
				Node n = selectionManager.getActiveNode();
				int s = selectionManager.getTextSelectionStart();
				int e = selectionManager.getTextSelectionEnd();
				comAction.setEnabled(comment.canInsertComment(n, s, e));
				piAction.setEnabled(comment.canInsertPI(n, s, e));
			}
		} else if (event instanceof DocumentOpenedEvent || event instanceof DocumentClosedEvent) {
			comAction.setEnabled(false);
			piAction.setEnabled(false);
		}
	}

	protected class ComAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doComment();
		}
	}

	protected class PiAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doProcessingInstruction();
		}
	}

}
