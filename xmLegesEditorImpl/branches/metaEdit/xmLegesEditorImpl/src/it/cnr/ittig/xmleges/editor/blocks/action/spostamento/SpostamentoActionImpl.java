package it.cnr.ittig.xmleges.editor.blocks.action.spostamento;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.spostamento.SpostamentoAction;
import it.cnr.ittig.xmleges.editor.services.dom.liste.Liste;
import it.cnr.ittig.xmleges.editor.services.dom.spostamento.Spostamento;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.spostamento.SpostamentoAction</code>.
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class SpostamentoActionImpl implements SpostamentoAction, EventManagerListener, Loggable, Serviceable, Initializable {

	public abstract class MyAbstractAction extends AbstractAction {
		abstract public boolean canDoAction(Node n);
	}

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	Spostamento spostamento;

	Liste liste;

	NirUtilDom nirUtilDom;

	Node activeNode, applyNode;

	Node[] selNodes;

	MyAbstractAction[] actions = new MyAbstractAction[] { new MoveUpAction(), new MoveDownAction(), new PromuoviAction(), new RiduciAction() };

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		spostamento = (Spostamento) serviceManager.lookup(Spostamento.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		liste = (Liste) serviceManager.lookup(Liste.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		int i = 0;
		actionManager.registerAction("editor.spostamento.su", actions[i++]);
		actionManager.registerAction("editor.spostamento.giu", actions[i++]);
		actionManager.registerAction("editor.spostamento.promuovi", actions[i++]);
		actionManager.registerAction("editor.spostamento.riduci", actions[i++]);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		enableActions();
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof SelectionChangedEvent) {
			selNodes = ((SelectionChangedEvent) event).getSelectedNodes();
			if (((SelectionChangedEvent) event).isActiveNodeChanged()) {
				activeNode = ((SelectionChangedEvent) event).getActiveNode();
				enableActions();
			}
		}

		if (event instanceof DocumentClosedEvent) {
			for (int i = 0; i < actions.length; i++)
				actions[i].setEnabled(false);
		}
		/**
		 * TODO Sposta su: impedire alla lettera (numero) di uscire dal suo
		 * articolo
		 */
	}

	protected void enableActions() {
		logger.debug("START enableFreccine");
		String pathName = UtilDom.getPathName(activeNode);
		// se sono nell'articolato sposto i contenitori Nir ...

		if (activeNode != null
				&& (activeNode.getNodeName().equalsIgnoreCase("h:li") || (null != activeNode.getParentNode() && activeNode.getParentNode().getNodeName()
						.equalsIgnoreCase("h:li")))) {
			actions[0].setEnabled(liste.canMuoviSu(activeNode));
			actions[1].setEnabled(liste.canMuoviGiu(activeNode));
			actions[2].setEnabled(liste.canPromuovi(activeNode));
			actions[3].setEnabled(liste.canRiduci(activeNode));
			applyNode = UtilDom.findParentByName(activeNode, "h:li");
		} else {
			if (pathName.matches(".*\\.articolato\\..*")) {
				applyNode = getContainer(activeNode);
				logger.debug("applyNode: " + UtilDom.getPathName(applyNode));
			} else {
				if (nirUtilDom.getHpContainer(activeNode) != null)
					applyNode = nirUtilDom.getHpContainer(activeNode);
				else
					applyNode = activeNode;
			}

			if (applyNode == null || selNodes != null && selNodes.length > 1)
				for (int i = 0; i < actions.length; i++)
					actions[i].setEnabled(false);
			else {
				for (int i = 0; i < actions.length; i++)
					actions[i].setEnabled(actions[i].canDoAction(applyNode));
			}
		}
		logger.debug("END enableFreccine");
	}

	protected void setModified(Node node) {
		if (node != null) {
			selectionManager.setActiveNode(this, node);
			activeNode = node;
			enableActions();
		}
	}

	// ////////////////////////////////////////////// SpostamentoAction
	// Interface
	public void doMoveUp(Node node, Node target) {
		Node hli = UtilDom.findParentByName(node, "h:li");
		// if(node.getNodeName().equalsIgnoreCase("h:li"))
		if (hli != null) {
			setModified(liste.muoviSu(node));
		} else {
			setModified(spostamento.moveUp(target, node));
		}
	}

	public void doMoveDown(Node node, Node target) {
		if (node.getNodeName().equalsIgnoreCase("h:li"))
			setModified(liste.muoviGiu(node));
		else
			setModified(spostamento.moveDown(target, node));
	}

	public void doUpgrade(Node node) {
		if (node.getNodeName().equalsIgnoreCase("h:li"))
			setModified(liste.promuovi(node));
		else
			setModified(spostamento.upGrade(node));
	}

	public void doDowngrade(Node node) {
		// if(node.getNodeName().equalsIgnoreCase("h:li"))
		Node hli = UtilDom.findParentByName(node, "h:li");
		if (hli != null)
			setModified(liste.riduci(node, hli.getParentNode().getNodeName()));
		else
			setModified(spostamento.downGrade(node));
	}

	// ///////////////////////////////////////////////// Azioni
	public class MoveUpAction extends MyAbstractAction {
		Node target;

		public boolean canDoAction(Node n) {
			target = spostamento.canMoveUp(n);
			return (target != null);
		}

		public void actionPerformed(ActionEvent e) {
			doMoveUp(applyNode, target);
		}

	}

	public class MoveDownAction extends MyAbstractAction {
		Node target;

		public boolean canDoAction(Node n) {
			target = spostamento.canMoveDown(n);
			return (target != null);
		}

		public void actionPerformed(ActionEvent e) {
			doMoveDown(applyNode, target);
		}

	}

	public class PromuoviAction extends MyAbstractAction {

		public boolean canDoAction(Node n) {
			return (spostamento.canUpgrade(n));
		}

		public void actionPerformed(ActionEvent e) {
			doUpgrade(applyNode);
		}

	}

	public class RiduciAction extends MyAbstractAction {

		public boolean canDoAction(Node n) {
			return (spostamento.canDowngrade(n));
		}

		public void actionPerformed(ActionEvent e) {
			doDowngrade(applyNode);
		}

	}

	private Node getContainer(Node node) {
		if (node != null) {
			Node container = node;
			while (container != null && !nirUtilDom.isContainer(container))
				container = container.getParentNode();
			return container;
		}
		return (null);
	}

}
