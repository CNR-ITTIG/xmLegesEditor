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
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.editor.services.action.disposizioni.attive.IlcAction;
import it.cnr.ittig.xmleges.editor.services.action.disposizioni.attive.IlcForm;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;



/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.disposizioni.attive.IlcAction</code>.</h1>
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

public class IlcActionImpl implements IlcAction, Loggable, EventManagerListener, Serviceable, Initializable {
	
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	DocumentManager documentManager;

	IlcForm ilcForm;
	
	IlcAzione ilcAction = new IlcAzione();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		ilcForm = (IlcForm) serviceManager.lookup(IlcForm.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("tool.ilc", ilcAction);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		ilcAction.setEnabled(false);
	}

	public void manageEvent(EventObject event) {

		if (event instanceof DocumentClosedEvent) {
			ilcAction.setEnabled(false);
		}
		
		if (event instanceof DocumentOpenedEvent) {
			ilcAction.setEnabled(documentManager.getDocumentAsDom().getElementsByTagName("mod").getLength()>0);
		}
	}

	public void doIlc() {
		ilcForm.openForm();		
	}
	// /////////////////////////////////////////////// Azioni
	public class IlcAzione extends AbstractAction {
		
		public void actionPerformed(ActionEvent e) {
			doIlc();
		}
	}
}
