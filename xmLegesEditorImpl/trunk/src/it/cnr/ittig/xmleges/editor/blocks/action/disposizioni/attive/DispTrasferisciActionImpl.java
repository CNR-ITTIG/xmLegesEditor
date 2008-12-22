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
import it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.editor.services.action.disposizioni.attive.DispTrasferisciAction;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.DispTrasferisciForm;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;


/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.disposizioni.attive.TrasferisciAttInPassAction</code>.</h1>
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

public class DispTrasferisciActionImpl implements DispTrasferisciAction, Loggable, EventManagerListener, Serviceable, Initializable {
	
	Logger logger;
	ActionManager actionManager;
	EventManager eventManager;
	DocumentManager documentManager;
	DispTrasferisciForm dispTrasferisciForm;
	Node activeNode;

	AbstractAction trasferisciAction = new trasferisciAction();
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		dispTrasferisciForm = (DispTrasferisciForm) serviceManager.lookup(DispTrasferisciForm.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("editor.disposizioni.trasferisci", trasferisciAction);
		eventManager.addListener(this, DocumentChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		trasferisciAction.setEnabled(false);
	}

	public void manageEvent(EventObject event) {
		
		if (event instanceof DocumentOpenedEvent && !documentManager.isEmpty()) { 
			trasferisciAction.setEnabled(documentManager.getDocumentAsDom().getElementsByTagName("attiva").getLength()>0);
		}
		
		if (event instanceof DocumentClosedEvent) {
			trasferisciAction.setEnabled(false);
		}
		
		if (event instanceof DocumentChangedEvent)
			trasferisciAction.setEnabled(documentManager.getDocumentAsDom().getElementsByTagName("attiva").getLength()>0);
	}

	public void doTrasferisci() {
		dispTrasferisciForm.openForm();		
	}
	
	// /////////////////////////////////////////////// Azioni
	public class trasferisciAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doTrasferisci();
		}
	}
}

