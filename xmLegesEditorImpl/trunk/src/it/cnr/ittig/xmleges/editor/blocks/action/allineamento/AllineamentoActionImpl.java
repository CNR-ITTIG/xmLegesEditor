package it.cnr.ittig.xmleges.editor.blocks.action.allineamento;

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
import it.cnr.ittig.xmleges.editor.services.action.allineamento.AllineamentoAction;
import it.cnr.ittig.xmleges.editor.services.dom.allineamento.Allineamento;
import it.cnr.ittig.xmleges.editor.services.dom.tabelle.Tabelle;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmlegis.editor.services.action.allineamento.AllineamentoAction</code>.</h1>
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
 * @author <a href="mailto:cristina.mercatanti@tin.it">Cristina Mercatanti </a>
 */
public class AllineamentoActionImpl implements AllineamentoAction, Loggable, Serviceable, Initializable, EventManagerListener {
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	Allineamento allineamento;

	Tabelle tabelle;

	Node activeNode = null;

	Node[] selectedNodes;

	AllineaAction[] allineaActions = new AllineaAction[4];

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		allineamento = (Allineamento) serviceManager.lookup(Allineamento.class);
		tabelle = (Tabelle) serviceManager.lookup(Tabelle.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		allineaActions[0] = new AllineaAction("left");
		allineaActions[1] = new AllineaAction("right");
		allineaActions[2] = new AllineaAction("center");
		allineaActions[3] = new AllineaAction("justify");

		actionManager.registerAction("editor.allineamento.oriz.sx", allineaActions[0]);
		actionManager.registerAction("editor.allineamento.oriz.dx", allineaActions[1]);
		actionManager.registerAction("editor.allineamento.oriz.centro", allineaActions[2]);
		actionManager.registerAction("editor.allineamento.oriz.giustificato", allineaActions[3]);

		// eventManager.addListener(this, DocumentOpenedEvent.class);
		// eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
		enableActions(null);
	}

	public void manageEvent(EventObject event) {
		SelectionChangedEvent e = (SelectionChangedEvent) event;
		if (e.isActiveNodeChanged()) {
			activeNode = e.getActiveNode();
			enableActions(activeNode);
		}
	}

	protected void enableActions(Node n) {
		for (int i = 0; i < allineaActions.length; i++)
			allineaActions[i].setEnabled(allineaActions[i].canDoAction(n));
	}

	public class AllineaAction extends AbstractAction {
		String tipo;

		public AllineaAction(String tipo) {
			this.tipo = tipo;
		}

		public boolean canDoAction(Node n) {
			// if (n == null) verificato !=null in canallign
			if (tabelle.canAllignTextCol(n) || allineamento.canAlignText(n)) {
				logger.debug("####### can allign true");

				return true;
			}

			logger.debug("####### can allign false");
			// TODO can di tabelle, poi generico
			return false;

		}

		public void actionPerformed(ActionEvent e) {
			// TODO allinea di tabelle, poi generico
			logger.debug("actionPerfAllinea");
			logger.debug("###### tipo " + tipo);
			if (tabelle.canAllignTextCol(activeNode)) {

				tabelle.allineaTestoCol(activeNode, tipo);
				logger.debug("#### siamo in tab");
			}

			else {
				if (allineamento.canAlignText(activeNode)) {
					allineamento.alignText(activeNode, tipo);
					logger.debug("#### NON siamo in tab");

				}

			}
		}
	}

}
