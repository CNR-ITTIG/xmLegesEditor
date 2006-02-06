package it.cnr.ittig.xmleges.core.blocks.action.edit.extracttext;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.edit.extracttext.ExtractTextAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.dom.extracttext.ExtractText;
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
 * <code>it.cnr.ittig.xmleges.editor.services.action.edit.extracttext.ExtractTextAction</code>. </h1>
 * <h1>Descrizione</h1>
 * Questa implementazione registra le azioni <code>edit.comment</code> e
 * <code>edit.procinstr</code> nell'ActionManager. <br>
 * <h1>Configurazione</h1>
 * Nessuna.
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class ExtractTextActionImpl implements ExtractTextAction, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	ExtractText extractText;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	ETAction extractTextAction;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		extractText = (ExtractText) serviceManager.lookup(ExtractText.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		extractTextAction = new ETAction();
		extractTextAction.setEnabled(false);
		actionManager.registerAction("edit.extracttext", extractTextAction);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
	}

	// /////////////////////////////////////////////// CommentAction Interface
	public boolean doExtractText() {
		Node n = selectionManager.getActiveNode();
		int s = selectionManager.getTextSelectionStart();
		int e = selectionManager.getTextSelectionEnd();
		if (extractText.canExtractText(n, s, e)) {
			Node c = extractText.extractText(n, s, e);
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
			Node n = selectionManager.getActiveNode();
			int s = selectionManager.getTextSelectionStart();
			int e = selectionManager.getTextSelectionEnd();
			extractTextAction.setEnabled(extractText.canExtractText(n, s, e));
		} else if (event instanceof DocumentOpenedEvent || event instanceof DocumentClosedEvent) {
			extractTextAction.setEnabled(false);
		}
	}

	protected class ETAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doExtractText();
		}
	}

}
