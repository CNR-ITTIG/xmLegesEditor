package it.cnr.ittig.xmleges.editor.blocks.action.disposizioni.attive;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.disposizioni.attive.DispAttiveAction;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispAttiveForm;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispMarkerForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;


/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.disposizioni.attive.DispAttiveAction</code>.</h1>
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
 */

public class DispAttiveActionImpl implements DispAttiveAction, Loggable, EventManagerListener, Serviceable, Initializable {
	
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	DocumentManager documentManager;

	DispAttiveForm dispAttiveForm;
	
	DispMarkerForm dispMarkerForm;

	NirUtilDom nirUtilDom;
	
	Node activeNode;
	
	DispoAttivaAction dispoAttivaAction = new DispoAttivaAction();
	DispoMarkerAction dispoMarkerAction = new DispoMarkerAction();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		dispAttiveForm = (DispAttiveForm) serviceManager.lookup(DispAttiveForm.class);
		dispMarkerForm = (DispMarkerForm) serviceManager.lookup(DispMarkerForm.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("editor.disposizioni.attive", dispoAttivaAction);
		actionManager.registerAction("editor.disposizioni.marker", dispoMarkerAction);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
		dispoAttivaAction.setEnabled(false);
		dispoMarkerAction.setEnabled(false);
	}

	public void manageEvent(EventObject event) {

		if (event instanceof DocumentClosedEvent) {
			dispoAttivaAction.setEnabled(false);
			dispoMarkerAction.setEnabled(false);
		}
		
		if (event instanceof SelectionChangedEvent) {
			activeNode = ((SelectionChangedEvent) event).getActiveNode();
			if (activeNode != null) {
				dispoAttivaAction.setEnabled(UtilDom.findParentByName(activeNode, "mod")!=null);
				Node vir = UtilDom.findParentByName(activeNode, "virgolette");
				if (vir!=null) {
//					if ("struttura".equals(UtilDom.getAttributeValueAsString(vir, "tipo")))
						dispoMarkerAction.setEnabled(true);
//					else
//						dispoMarkerAction.setEnabled(false);
				}
				else 
					dispoMarkerAction.setEnabled(false);
			}	
			else {
				dispoAttivaAction.setEnabled(false);
				dispoMarkerAction.setEnabled(false);
			}
		}
	}

	public void doDispAttiva() {
			dispAttiveForm.openForm(true);		
	}
	
	public void doMarkerAttiva() {
			if (dispMarkerForm.openForm())
				dispMarkerForm.setMeta();		
	}
	// /////////////////////////////////////////////// Azioni
	public class DispoAttivaAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			doDispAttiva();
		}
	}
	public class DispoMarkerAction extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			doMarkerAttiva();
		}
	}
}
