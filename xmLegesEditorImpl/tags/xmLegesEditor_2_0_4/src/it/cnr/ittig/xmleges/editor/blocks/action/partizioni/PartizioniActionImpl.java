package it.cnr.ittig.xmleges.editor.blocks.action.partizioni;

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
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.partizioni.PartizioniAction;
import it.cnr.ittig.xmleges.editor.services.dom.partizioni.Partizioni;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.partizioni.PartizioniAction</code>.
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
public class PartizioniActionImpl implements PartizioniAction, EventManagerListener, Loggable, Serviceable, Initializable {

	public abstract class MyAbstractAction extends AbstractAction {
		abstract public boolean canDoAction(Node n);
	}

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	Partizioni partizioni;

	UtilMsg utilMsg;

	Node activeNode;

	Node[] selNodes;

	MyAbstractAction[] actions = new MyAbstractAction[] { new LibroAction(), new ParteAction(), new TitoloAction(), new CapoAction(), new SezioneAction(),
			new ArticoloAction(), new CommaAction(), new LetteraAction(), new NumeroAction(), new PuntoAction(), new RubricaAction() };

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		partizioni = (Partizioni) serviceManager.lookup(Partizioni.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		int i = 0;
		actionManager.registerAction("editor.partizioni.libro", actions[i++]);
		actionManager.registerAction("editor.partizioni.parte", actions[i++]);
		actionManager.registerAction("editor.partizioni.titolo", actions[i++]);
		actionManager.registerAction("editor.partizioni.capo", actions[i++]);
		actionManager.registerAction("editor.partizioni.sezione", actions[i++]);
		actionManager.registerAction("editor.partizioni.articolo", actions[i++]);
		actionManager.registerAction("editor.partizioni.comma", actions[i++]);
		actionManager.registerAction("editor.partizioni.lettera", actions[i++]);
		actionManager.registerAction("editor.partizioni.numero", actions[i++]);
		actionManager.registerAction("editor.partizioni.punto", actions[i++]);
		actionManager.registerAction("editor.partizioni.rubrica", actions[i++]);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		enableActions();
	}

	protected void enableActions() {
		if (activeNode == null || selNodes != null && selNodes.length > 1)
			for (int i = 0; i < actions.length; i++)
				actions[i].setEnabled(false);
		else {
			String pathName = UtilDom.getPathName(activeNode);
			if (!pathName.matches(".*\\.articolato\\..*"))
				for (int i = 0; i < actions.length; i++)
					actions[i].setEnabled(false);
			else
				for (int i = 0; i < actions.length; i++)
					actions[i].setEnabled(actions[i].canDoAction(activeNode));
		}
	}

	protected void setModified(Node modified) {
		if (modified != null) {
			modified = getCorpo(modified);
			selectionManager.setActiveNode(this, modified);
			activeNode = modified;
			enableActions();
		} else
			logger.debug(" modified null in set modified ");
	}

	protected Node getCorpo(Node node) {
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeName().toLowerCase().indexOf("corpo") != -1)
				return (nl.item(i));
		}
		return node;
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		// TODO AGGREGAZIONE
		if (event instanceof SelectionChangedEvent) {
			selNodes = ((SelectionChangedEvent) event).getSelectedNodes();
			if (((SelectionChangedEvent) event).isActiveNodeChanged()) {
				logger.debug("isActiveNodeChanged");
				activeNode = ((SelectionChangedEvent) event).getActiveNode();
				logger.debug("activeNode " + UtilDom.getPathName(activeNode));
				enableActions();
			}
		}

		if (event instanceof DocumentClosedEvent) {
			for (int i = 0; i < actions.length; i++)
				actions[i].setEnabled(false);
		}

	}

	// ////////////////////////////////////////////// PartizioniAction Interface
	public void doNewLibro(Node node) {
		// TODO AGGREGAZIONE
		Node modified = partizioni.nuovaPartizione(node, Partizioni.LIBRO);
		setModified(modified);
	}

	public void doNewLibro(Node node, int action) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.LIBRO, action));
	}

	public void doNewParte(Node node) {
		Node modified = partizioni.nuovaPartizione(node, Partizioni.PARTE);
		setModified(modified);
	}

	public void doNewParte(Node node, int action) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.PARTE, action));
	}

	public void doNewTitolo(Node node) {
		Node modified = partizioni.nuovaPartizione(node, Partizioni.TITOLO);
		setModified(modified);
	}

	public void doNewTitolo(Node node, int action) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.TITOLO, action));
	}

	public void doNewCapo(Node node) {
		Node modified = partizioni.nuovaPartizione(node, Partizioni.CAPO);
		setModified(modified);
	}

	public void doNewCapo(Node node, int action) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.CAPO, action));
	}

	public void doNewSezione(Node node) {
		Node modified = partizioni.nuovaPartizione(node, Partizioni.SEZIONE);
		setModified(modified);
	}

	public void doNewSezione(Node node, int action) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.SEZIONE, action));
	}

	public void doNewArticolo(Node node) {
		setModified(partizioni.nuovaPartizione(node, Partizioni.ARTICOLO));
	}

	public void doNewArticolo(Node node, int action) {
		setModified(partizioni.nuovaPartizione(node, Partizioni.ARTICOLO, action));
	}

	public void doNewComma(Node node) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.COMMA));
	}

	public void doNewComma(Node node, int action) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.COMMA, action));
	}

	public void doNewLettera(Node node) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.EL));
	}

	public void doNewLettera(Node node, int action) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.EL, action));
	}

	public void doNewNumero(Node node) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.EN));
	}

	public void doNewNumero(Node node, int action) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.EN, action));
	}
	
	public void doNewPunto(Node node) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.EP));
	}

	public void doNewPunto(Node node, int action) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.EP, action));
	}
	
	public void doNewRubrica(Node node) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.RUBRICA));
	}

	public void doNewRubrica(Node node, int action) {
		// TODO AGGREGAZIONE
		setModified(partizioni.nuovaPartizione(node, Partizioni.RUBRICA, action));
	}

	// ///////////////////////////////////////////////// Azioni
	public class LibroAction extends MyAbstractAction {
		int action;

		public boolean canDoAction(Node n) {
			// action = partizioni.canInsertNuovaPartizione(n,
			// Partizioni.LIBRO);
			// return (action != -1);
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			// doNewLibro(activeNode, action);
			doNewLibro(activeNode);
		}

	}

	public class ParteAction extends MyAbstractAction {
		int action;

		public boolean canDoAction(Node n) {
			// action = partizioni.canInsertNuovaPartizione(n,
			// Partizioni.PARTE);
			// return (action != -1);
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			// doNewParte(activeNode, action);
			doNewParte(activeNode);
		}
	}

	public class TitoloAction extends MyAbstractAction {
		int action;

		public boolean canDoAction(Node n) {
			// action = partizioni.canInsertNuovaPartizione(n,
			// Partizioni.TITOLO);
			// return (action != -1);
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			// doNewTitolo(activeNode, action);
			doNewTitolo(activeNode);
		}
	}

	public class CapoAction extends MyAbstractAction {
		int action;

		public boolean canDoAction(Node n) {
			// action = partizioni.canInsertNuovaPartizione(n, Partizioni.CAPO);
			// return (action != -1);
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			// doNewCapo(activeNode, action);
			doNewCapo(activeNode);
		}
	}

	public class SezioneAction extends MyAbstractAction {
		int action;

		public boolean canDoAction(Node n) {
			// action = partizioni.canInsertNuovaPartizione(n,
			// Partizioni.SEZIONE);
			// return (action != -1);
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			// doNewSezione(activeNode, action);
			doNewSezione(activeNode);
		}
	}

	public class ArticoloAction extends MyAbstractAction {
		int action;

		public boolean canDoAction(Node n) {
			action = partizioni.canInsertNuovaPartizione(n, Partizioni.ARTICOLO);
			return (action != -1);
		}

		public void actionPerformed(ActionEvent e) {
			doNewArticolo(activeNode, action);
		}
	}

	public class CommaAction extends MyAbstractAction {
		int action;

		public boolean canDoAction(Node n) {
			action = partizioni.canInsertNuovaPartizione(n, Partizioni.COMMA);
			return (action != -1);
		}

		public void actionPerformed(ActionEvent e) {
			doNewComma(activeNode, action);
		}
	}

	public class LetteraAction extends MyAbstractAction {
		int action;

		public boolean canDoAction(Node n) {
			action = partizioni.canInsertNuovaPartizione(n, Partizioni.EL);
			return (action != -1);
		}

		public void actionPerformed(ActionEvent e) {
			doNewLettera(activeNode, action);
		}
	}

	public class NumeroAction extends MyAbstractAction {
		int action;

		public boolean canDoAction(Node n) {
			action = partizioni.canInsertNuovaPartizione(n, Partizioni.EN);
			return (action != -1);
		}

		public void actionPerformed(ActionEvent e) {
			doNewNumero(activeNode, action);
		}
	}

	public class PuntoAction extends MyAbstractAction {
		int action;

		public boolean canDoAction(Node n) {
			action = partizioni.canInsertNuovaPartizione(n, Partizioni.EP);
			return (action != -1);
		}

		public void actionPerformed(ActionEvent e) {
			doNewPunto(activeNode, action);
		}
	}
	
	public class RubricaAction extends MyAbstractAction {
		int action;

		public boolean canDoAction(Node n) {
			action = partizioni.canInsertNuovaPartizione(n, Partizioni.RUBRICA);
			return (action != -1);
		}

		public void actionPerformed(ActionEvent e) {
			doNewRubrica(activeNode, action);
		}
	}

}
