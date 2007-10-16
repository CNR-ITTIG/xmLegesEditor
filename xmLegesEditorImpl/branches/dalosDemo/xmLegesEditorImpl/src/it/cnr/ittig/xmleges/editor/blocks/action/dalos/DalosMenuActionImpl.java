package it.cnr.ittig.xmleges.editor.blocks.action.dalos;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.dalos.DalosMenuAction;
import it.cnr.ittig.xmleges.editor.services.dom.allineamento.Allineamento;
import it.cnr.ittig.xmleges.editor.services.dom.tabelle.Tabelle;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.allineamento.AllineamentoAction</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>action-manager</li>
 * <li>event-manager</li>
 * <li>selection-manager</li>
 * <li>editor-dom-allineamento</li>
 * <li>editor-dom-tabelle</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>editor.allineamento.oriz.sx</li>
 * <li>editor.allineamento.oriz.dx</li>
 * <li>editor.allineamento.oriz.centro</li>
 * <li>editor.allineamento.oriz.giustificato</li>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class DalosMenuActionImpl implements DalosMenuAction, Loggable, Serviceable, Initializable, EventManagerListener {
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;
	
	Node activeNode = null;

	ShowViewAction showViewAction;
	

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		showViewAction = new ShowViewAction();
		actionManager.registerAction("editor.dalos.showview", showViewAction);
		actionManager.registerAction("editor.dalos.switchlang.it", new SwitchLangAction());
		actionManager.registerAction("editor.dalos.switchlang.en", new SwitchLangAction());
		actionManager.registerAction("editor.dalos.switchlang.nl", new SwitchLangAction());
		actionManager.registerAction("editor.dalos.switchlang.es", new SwitchLangAction());
	}

	public void manageEvent(EventObject event) {
	}

	protected void enableActions(Node n) {
		showViewAction.setEnabled(true);
	}

	public class ShowViewAction extends AbstractAction {
		String tipo;

		public ShowViewAction() {
			
		}

		public boolean canDoAction(Node n) {
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			System.err.println("--------SHOW DALOS VIEW----------");
		}
	}
	
	public class SwitchLangAction extends AbstractAction {
		String tipo;

		public SwitchLangAction() {
			
		}

		public boolean canDoAction(Node n) {
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			System.err.println("--------SWITCH MASTER LANGUAGE----------");
		}
	}

}
