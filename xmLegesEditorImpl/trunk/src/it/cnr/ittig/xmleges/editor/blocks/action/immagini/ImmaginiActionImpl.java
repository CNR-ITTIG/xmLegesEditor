package it.cnr.ittig.xmleges.editor.blocks.action.immagini;

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
import it.cnr.ittig.xmleges.editor.services.action.immagini.ImmaginiAction;
import it.cnr.ittig.xmleges.editor.services.form.immagini.ImmaginiForm;
import it.cnr.ittig.xmleges.editor.services.dom.immagini.Immagini;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.immagini</code>.
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
 */ 
public class ImmaginiActionImpl implements ImmaginiAction, EventManagerListener,  Loggable, Serviceable, Initializable {

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	doImmaginiAction immaginiAction;

	Node activeNode;
	
	Immagini domimmagini;

	int start;

	int end;

	ImmaginiForm immagini;

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
		immagini = (ImmaginiForm) serviceManager.lookup(ImmaginiForm.class);
		domimmagini = (Immagini) serviceManager.lookup(Immagini.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		immaginiAction = new doImmaginiAction();
		actionManager.registerAction("editor.insert.immagini", immaginiAction);
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

	protected void enableActions() {
		if (activeNode == null) 
			immaginiAction.setEnabled(false);
		else			
			immaginiAction.setEnabled(domimmagini.canInsert(activeNode));
	}

	public class doImmaginiAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
				doImmagini(); 
		}
	}

	public void doImmagini() {

		immaginiAction.setEnabled(false);
		immagini.openForm(activeNode);
		enableActions();
	}

}

