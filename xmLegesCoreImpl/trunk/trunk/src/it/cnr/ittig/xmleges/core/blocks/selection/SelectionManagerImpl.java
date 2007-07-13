package it.cnr.ittig.xmleges.core.blocks.selection;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;

import java.util.EventObject;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.selection.SelectionManager</code>.</h1>
 * <h1>Descrizione</h1>
 * Questa implementazione rimane in ascolto per eventuali eventi che le competono emessi
 * da altri componenti. In tale situazione memorizza le variazioni ma non emette nessun
 * evento.
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>event-manager</li>
 * </ul>
 * <h1>I18n</h1>
 * Nessuna.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class SelectionManagerImpl implements SelectionManager, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	EventManager eventManager;

	Node activeNode;

	Node[] selectedNodes;

	int selStart = -1;

	int selEnd = -1;

	final static Node[] unselectedNodes = new Node[0];

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof DocumentOpenedEvent || event instanceof DocumentClosedEvent) {
			setSelectedNodes(event.getSource(), unselectedNodes);
			setActiveNode(event.getSource(), null);
			removeTextSelection(event.getSource());
		}
	}

	// ////////////////////////////////////////////// SelectionManager Interface
	public synchronized void setActiveNode(Object source, Node node) {
		logger.debug("setActiveNode: node=" + node);
		if (isActiveNodeChanged(node)) {
			this.activeNode = node;
			this.selStart = -1;
			this.selEnd = -1;
			this.selectedNodes = unselectedNodes;
			if (source != null)
				eventManager.fireEvent(new SelectionChangedEvent(source, node));
		}
	}

	protected boolean isActiveNodeChanged(Node node) {
		return (activeNode == null && node != null) || (activeNode != null && node == null) || (activeNode != null && !activeNode.equals(node));
	}

	public synchronized Node getActiveNode() {
		return this.activeNode;
	}

	public synchronized void setSelectedNodes(Object source, Node[] nodes) {
		this.activeNode = null;
		this.selStart = -1;
		this.selEnd = -1;
		this.selectedNodes = nodes;
		if (source != null)
			eventManager.fireEvent(new SelectionChangedEvent(source, nodes));
	}

	public synchronized Node[] getSelectedNodes() {
		return this.selectedNodes;
	}

	public synchronized void setSelectedText(Object source, Node node, int start, int end) {
		logger.debug("setSelectedText: " + start + "-" + end + " node=" + node);
		boolean isActiveNodeChanged = isActiveNodeChanged(node);
		this.activeNode = node;
		this.selStart = start;
		this.selEnd = end;
		this.selectedNodes = unselectedNodes;
		if (source != null)
			eventManager.fireEvent(new SelectionChangedEvent(source, node, start, end, isActiveNodeChanged));
	}

	public synchronized void removeTextSelection(Object source) {
		this.selStart = -1;
		this.selEnd = -1;
		if (source != null)
			eventManager.fireEvent(new SelectionChangedEvent(source, activeNode, -1, -1, false));
	}

	public synchronized boolean isTextSelected() {
		return selStart != -1 && selEnd != -1;
	}

	public synchronized int getTextSelectionStart() {
		return this.selStart;
	}

	public synchronized int getTextSelectionEnd() {
		return this.selEnd;
	}

}
