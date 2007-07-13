package it.cnr.ittig.xmleges.editor.blocks.action.liste;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.editor.services.action.liste.ListeAction;
import it.cnr.ittig.xmleges.editor.services.dom.liste.Liste;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.liste.ListeAction</code>.</h1>
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
public class ListeActionImpl implements ListeAction, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	SelectionManager selectionManager;

	Liste liste;

	Node activeNode;

	String tipolista;

	AbstractAction[] actions = new AbstractAction[] { new ListaNumerataAction(), new ListaPuntataAction(), new LivelloIncAction(), new LivelloDecAction() };

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		liste = (Liste) serviceManager.lookup(Liste.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		int i = 0;
		actionManager.registerAction("editor.liste.numerata", actions[i++]);
		actionManager.registerAction("editor.liste.puntata", actions[i++]);
		actionManager.registerAction("editor.liste.livello.inc", actions[i++]);
		actionManager.registerAction("editor.liste.livello.dec", actions[i++]);
		eventManager.addListener(this, SelectionChangedEvent.class);
		enableActions();
	}

	protected void enableActions() {
		if (activeNode == null)
			for (int i = 0; i < actions.length; i++)
				actions[i].setEnabled(false);
		else {
			actions[0].setEnabled(liste.canCreaLista(activeNode, "h:ol") != 0);
			actions[1].setEnabled(liste.canCreaLista(activeNode, "h:ul") != 0);
			actions[2].setEnabled(liste.canPromuovi(activeNode));
			actions[3].setEnabled(liste.canRiduci(activeNode));

		}
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {

		if (event instanceof SelectionChangedEvent) {
			// selNodes = ((SelectionChangedEvent) event).getSelectedNodes();
			if (((SelectionChangedEvent) event).isActiveNodeChanged()) {
				activeNode = ((SelectionChangedEvent) event).getActiveNode();
				enableActions();
			}
		}

		if (event instanceof DocumentClosedEvent) {
			for (int i = 0; i < actions.length; i++)
				actions[i].setEnabled(false);
		}
		// activeNode = ((SelectionChangedEvent) event).getActiveNode();
		// enableActions();
	}

	// /////////////////////////////////////////////////// ListeAction Interface
	public void doLista(String tipolista) {

		Node lista = liste.creaLista(activeNode, tipolista);
		Node parent = activeNode.getParentNode();

		try {

			try {
				EditTransaction tr = documentManager.beginEdit();

				if (liste.canCreaLista(activeNode, tipolista) == 3) {
					if (activeNode.getNextSibling() != null) {
						Node next = activeNode.getNextSibling();

						if (dtdRulesManager.queryCanInsertBefore(parent, next, lista)) {
							parent.insertBefore(lista, next);
							documentManager.commitEdit(tr);
						}
					} else if (dtdRulesManager.queryCanAppend(parent, lista)) {
						parent.appendChild(lista);
						documentManager.commitEdit(tr);
					}
				} else if (liste.canCreaLista(activeNode, tipolista) == 4) {
					if (dtdRulesManager.queryCanInsertBefore(parent, activeNode, lista)) {
						parent.insertBefore(lista, activeNode);
						documentManager.commitEdit(tr);
					}
				} else if (liste.canCreaLista(activeNode, tipolista) == 1) {
					if (dtdRulesManager.queryCanAppend(activeNode, lista)) {
						activeNode.appendChild(lista);
						documentManager.commitEdit(tr);
					}
				} else if (liste.canCreaLista(activeNode, tipolista) == 2) {
					if (dtdRulesManager.queryCanPrepend(activeNode, lista)) {
						activeNode.insertBefore(lista, activeNode.getFirstChild());
						documentManager.commitEdit(tr);
					}

				} else
					documentManager.rollbackEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
			}

		} catch (DtdRulesManagerException ex) {
			logger.error(ex.getMessage(), ex);
		}
		selectionManager.setActiveNode(this, lista);
		activeNode = selectionManager.getActiveNode();

	}

	/*
	 * public void doListaPuntata() { try { Node lista =
	 * liste.creaListaNN(activeNode); Node parent = activeNode.getParentNode();
	 * try { EditTransaction tr = documentManager.beginEdit(); if
	 * (liste.canCreaListaNUM(activeNode) == 3) { if
	 * (activeNode.getNextSibling() != null) { Node next =
	 * activeNode.getNextSibling(); if
	 * (dtdRulesManager.queryCanInsertBefore(parent, next, lista)) {
	 * parent.insertBefore(lista, next); documentManager.commitEdit(tr); } }
	 * else if (dtdRulesManager.queryCanAppend(parent, lista)) {
	 * parent.appendChild(lista); documentManager.commitEdit(tr); } } else if
	 * (liste.canCreaListaNUM(activeNode) == 4) { if
	 * (dtdRulesManager.queryCanInsertBefore(parent, activeNode, lista)) {
	 * parent.insertBefore(lista, activeNode); documentManager.commitEdit(tr); } }
	 * else if (liste.canCreaListaNUM(activeNode) == 1) { if
	 * (dtdRulesManager.queryCanAppend(activeNode, lista)) {
	 * activeNode.appendChild(lista); documentManager.commitEdit(tr); } } else
	 * if (liste.canCreaListaNUM(activeNode) == 2) { if
	 * (dtdRulesManager.queryCanPrepend(activeNode, lista)) {
	 * activeNode.insertBefore(lista, activeNode.getFirstChild());
	 * documentManager.commitEdit(tr); } } else
	 * documentManager.rollbackEdit(tr); } catch (DocumentManagerException ex) {
	 * logger.error(ex.getMessage(),ex); } } catch (DtdRulesManagerException ex) {
	 * logger.error(ex.getMessage(), ex); } }
	 */

	public void doCambiaLivello(boolean inc) {
		if (inc == true)
			liste.muoviSu(activeNode);
		else
			liste.muoviGiu(activeNode);
	}

	/**
	 * Azione per lista <b>numerata </b>.
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria
	 * e Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
	 * General Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
	 */
	public class ListaNumerataAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doLista("h:ol");
		}
	}

	/**
	 * Azione per lista <b>puntata </b>.
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria
	 * e Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
	 * General Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
	 */
	public class ListaPuntataAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doLista("h:ul");
		}
	}

	/**
	 * Azione per <b>incrementare il livello </b> ad un elemento della lista.
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria
	 * e Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
	 * General Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
	 */
	public class LivelloIncAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doCambiaLivello(true);
		}
	}

	/**
	 * Azione per <b>decrementare il livello </b> ad un elemento della lista.
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria
	 * e Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
	 * General Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
	 */
	public class LivelloDecAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doCambiaLivello(false);
		}
	}

}
