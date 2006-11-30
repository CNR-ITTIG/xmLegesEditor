package it.cnr.ittig.xmleges.editor.blocks.action.testo;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.testo.TestoAction;
import it.cnr.ittig.xmleges.editor.services.dom.testo.Testo;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.testo</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class TestoActionImpl implements TestoAction, EventManagerListener, Loggable, Serviceable, Initializable {

	Logger logger;

	int onlyTag = 0;

	int tree = 0;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	Testo testo;

	Node activeNode;
	
	int start, end;

	Node[] activeNodeList;

	AbstractAction[] actions = new AbstractAction[] { new GrassettoAction(), new CorsivoAction(), new SottolineatoAction(), new ApiceAction(),
			new PediceAction()};

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		testo = (Testo) serviceManager.lookup(Testo.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		int i = 0;
		actionManager.registerAction("editor.testo.grassetto", actions[i++]);
		actionManager.registerAction("editor.testo.corsivo", actions[i++]);
		actionManager.registerAction("editor.testo.sottolineato", actions[i++]);
		actionManager.registerAction("editor.testo.apice", actions[i++]);
		actionManager.registerAction("editor.testo.pedice", actions[i++]);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		enableActions();
	}

	public void enableActions() {
		if (activeNode == null) {
			for (int i = 0; i < actions.length; i++)
				actions[i].setEnabled(false);
		} else {
			logger.debug("START enableTesto");
			actions[0].setEnabled(testo.canTestoAction(activeNode, "h:b"));
			actions[1].setEnabled(testo.canTestoAction(activeNode, "h:i"));
			actions[2].setEnabled(testo.canTestoAction(activeNode, "h:u"));
			actions[3].setEnabled(testo.canTestoAction(activeNode, "h:sup"));
			actions[4].setEnabled(testo.canTestoAction(activeNode, "h:sub"));
			logger.debug("END enableTesto");
		}
	}

	public void enableActionTagTree() {
		if (activeNode == null) {
			for (int i = 0; i < actions.length; i++)
				actions[i].setEnabled(false);
		} else {
			actions[0].setEnabled(testo.canDoTagTree(activeNode, "h:b"));
			actions[1].setEnabled(testo.canDoTagTree(activeNode, "h:i"));
			actions[2].setEnabled(testo.canDoTagTree(activeNode, "h:u"));
			actions[3].setEnabled(testo.canDoTagTree(activeNode, "h:sup"));
			actions[4].setEnabled(testo.canDoTagTree(activeNode, "h:sub"));
		}
	}

	public void enableActionsRemoveTag() {
		actions[0].setEnabled(testo.canTestoActionRemoveTag(activeNode, "h:b"));
		actions[1].setEnabled(testo.canTestoActionRemoveTag(activeNode, "h:i"));
		actions[2].setEnabled(testo.canTestoActionRemoveTag(activeNode, "h:u"));
		actions[3].setEnabled(testo.canTestoActionRemoveTag(activeNode, "h:sup"));
		actions[4].setEnabled(testo.canTestoActionRemoveTag(activeNode, "h:sub"));
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof SelectionChangedEvent) {
			if (((SelectionChangedEvent) event).isActiveNodeChanged()) {
				logger.debug("selectionChangedEvent: " + ((SelectionChangedEvent) event).toString());
				activeNode = ((SelectionChangedEvent) event).getActiveNode();
				start = ((SelectionChangedEvent) event).getTextSelectionStart();
				end = ((SelectionChangedEvent) event).getTextSelectionEnd();
				

				if (activeNode != null) {
					logger.debug("active node" + activeNode + " node type " + activeNode.getNodeType());
					if ((UtilDom.isTextNode(activeNode))) {
						onlyTag = 0;
						tree = 0;
						logger.debug("azione sul nodo testo");
						enableActions();
					} else {
						if (((activeNode.getNodeName() == "h:b") || (activeNode.getNodeName() == "h:i") || (activeNode.getNodeName() == "h:u")
								|| (activeNode.getNodeName() == "h:sup") || (activeNode.getNodeName() == "h:sub"))
								&& (activeNode.getFirstChild() == null)) {
							onlyTag = 1;
							tree = 0;
							logger.debug("azione sul nodo tag");
							enableActionsRemoveTag();
						} else {
							onlyTag = 2;
							tree = 1;
							logger.debug("azione sul tree");
							enableActionTagTree();
						}
					}
				} else {
					for (int i = 0; i < actions.length; i++)
						actions[i].setEnabled(false);
				}
			}
		} else if (event instanceof DocumentClosedEvent || event instanceof DocumentOpenedEvent) {
			for (int i = 0; i < actions.length; i++)
				actions[i].setEnabled(false);
		}
	}

	public void doAction(String action) {
		Node modificato = activeNode;
		if (onlyTag == 0) { // caso di azione su nodo testo
			//int start = selectionManager.getTextSelectionStart();
			//int end = selectionManager.getTextSelectionEnd();

			Node found = UtilDom.findParentByName(activeNode, action);
			
			if (found == null)
				modificato = testo.doActionOn(activeNode, start, end, action);
			else 
				modificato = testo.doActionOff(activeNode, start, end, action);
			
		} else if (onlyTag == 1) // caso di azione su nodo h:b vuoto
			modificato = testo.doActionOffOnlyTag(activeNode);
		
		if (tree == 1) {// caso di azione su altri nodi
			
			//FIXME: far restituire a doTagTree il nodo modificato
			if (UtilDom.findParentByName(activeNode, action) != null)
				modificato = activeNode.getParentNode();
			testo.doTagTree(activeNode, action);
		}
		selectionManager.setActiveNode(this, modificato);

	}

	public class GrassettoAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doAction("h:b");
		}
	}
	
	public class CorsivoAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doAction("h:i");
		}
	}

	public class SottolineatoAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doAction("h:u");
		}
	}

	public class ApiceAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doAction("h:sup");
		}
	}

	public class PediceAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doAction("h:sub");
		}
	}
}
