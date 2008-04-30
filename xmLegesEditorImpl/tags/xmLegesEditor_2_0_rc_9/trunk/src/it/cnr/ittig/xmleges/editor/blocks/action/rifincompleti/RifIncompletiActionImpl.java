package it.cnr.ittig.xmleges.editor.blocks.action.rifincompleti;

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
import it.cnr.ittig.xmleges.editor.services.action.rifincompleti.RifIncompletiAction;
import it.cnr.ittig.xmleges.editor.services.dom.rifincompleti.RifIncompleti;
import it.cnr.ittig.xmleges.editor.services.form.rifincompleti.RifIncompletiForm;

import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.rinvii.RinviiAction</code>.
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
public class RifIncompletiActionImpl implements RifIncompletiAction, EventManagerListener,  Loggable, Serviceable, Initializable {

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	RifIncompletoAction rifIncompletoAction;

	NirUtilUrn nirUtilUrn;

	NirUtilDom nirUtilDom;

	Node activeNode;

	Node primoNodoAttivo;

	Node nodeRif;

	boolean isOpenForm = false;

	boolean changeInt = false;

	int start;

	int end;

	int primostart;

	int primoend;

	RifIncompletiForm rifincompleti;

	RifIncompleti domrifincompleti;

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
		rifincompleti = (RifIncompletiForm) serviceManager.lookup(RifIncompletiForm.class);
		domrifincompleti = (RifIncompleti) serviceManager.lookup(RifIncompleti.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		rifIncompletoAction = new RifIncompletoAction();
		actionManager.registerAction("editor.rifincompleto", rifIncompletoAction);
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
		if (activeNode == null) {
			rifIncompletoAction.setEnabled(false);
			
		} else {
			rifIncompletoAction.setEnabled(domrifincompleti.canFix(activeNode));
		}
	}

	public void doChangeRifIncompleto() {
		rifIncompletoAction.setEnabled(false);
		 				
		Node node = selectionManager.getActiveNode();			
		
		rifincompleti.openForm(domrifincompleti.getText(node), domrifincompleti.getUrn(node));
		enableActions();
		
	}

	public class RifIncompletoAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			//if (domrinvii.canInsert(activeNode)) {
				doChangeRifIncompleto();
			//} 
		}
	}

}
