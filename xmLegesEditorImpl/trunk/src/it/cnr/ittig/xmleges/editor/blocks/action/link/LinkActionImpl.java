package it.cnr.ittig.xmleges.editor.blocks.action.link;

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
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.link.LinkAction;
import it.cnr.ittig.xmleges.editor.services.dom.link.Link;
import it.cnr.ittig.xmleges.editor.services.form.link.LinkForm;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.link</code>.
 * </h1>
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
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class LinkActionImpl implements LinkAction, EventManagerListener,  Loggable, Serviceable, Initializable {

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	LinkAction linkAction;

	Node activeNode;

	int start;

	int end;

	LinkForm link;

	Link domlink;

	FormClosedListener listener;

	// /////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		link = (LinkForm) serviceManager.lookup(LinkForm.class);
		domlink = (Link) serviceManager.lookup(Link.class);

		
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		linkAction = new LinkAction();
		actionManager.registerAction("editor.link", linkAction);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		enableActions();		
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof SelectionChangedEvent) {
			activeNode = ((SelectionChangedEvent) event).getActiveNode();
			start = selectionManager.getTextSelectionStart();
			end = selectionManager.getTextSelectionEnd();
			
			enableActions();
		}
		
	}

	protected void setModified(Node modified) {
		if (modified != null) {
			selectionManager.setActiveNode(this, modified);
			activeNode = modified;
			logger.debug(" set modified " + UtilDom.getPathName(modified));
		} else
			logger.debug(" modified null in set modified ");
	}

	

	protected void enableActions() {
		if (activeNode == null) 
			linkAction.setEnabled(false);
		else			
			linkAction.setEnabled(domlink.canInsert(activeNode));
		
	}

	public void doChangeLink() {
		linkAction.setEnabled(false);	 				
		Node node = selectionManager.getActiveNode();	
		if (node.getNodeName().equals("#text") && node.getParentNode().getNodeName().equals("rif"))
			node = node.getParentNode();	
		link.openForm(node, domlink.getText(selectionManager.getActiveNode()), domlink.getUrl(selectionManager.getActiveNode()));
		enableActions();
		
	}

	public class LinkAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
				doChangeLink(); 
		}
	}

}
