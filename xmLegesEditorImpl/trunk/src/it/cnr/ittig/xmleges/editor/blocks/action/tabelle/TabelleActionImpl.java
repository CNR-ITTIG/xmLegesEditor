package it.cnr.ittig.xmleges.editor.blocks.action.tabelle;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
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
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.tabelle.TabelleAction;
import it.cnr.ittig.xmleges.editor.services.dom.tabelle.Tabelle;
import it.cnr.ittig.xmleges.editor.services.form.tabelle.TabelleForm;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmlegis.editor.action.tabelle.TabelleAction</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>document-manager</li>
 * <li>dtd-rules-manager</li>
 * <li>selection-manager</li>
 * <li>event-manager</li>
 * <li>action-manager</li>
 * <li>editor-dom-tabelle</li>
 * <li>editor-form-tabelle</li>
 * <li>util-msg</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>editor.tabelle.crea</li>
 * <li>editor.tabelle.elimina</li>
 * <li>editor.tabelle.riga.prependi</li>
 * <li>editor.tabelle.riga.appendi</li>
 * <li>editor.tabelle.riga.elimina</li>
 * <li>editor.tabelle.colonna.prependi</li>
 * <li>editor.tabelle.colonna.appendi</li>
 * <li>editor.tabelle.colonna.elimina</li>
 * <li>editor.tabelle.righe.merge</li>
 * <li>editor.tabelle.testo.allinea</li>
 * <li>editor.tabelle.elimina.msg</li>
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
 * @author Cristina Mercatanti
 */
public class TabelleActionImpl implements TabelleAction, Loggable, Serviceable, Configurable, Initializable, EventManagerListener {

	public abstract class MyAbstractAction extends AbstractAction {

		abstract public boolean canDoAction(Node[] n);
	}

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	DocumentManager documentManager;

	Tabelle tabelle;

	TabelleForm tabelleForm;

	Node activeNode;

	Node[] selectedNodes;

	DtdRulesManager dtdRulesManager;

	UtilMsg utilMsg;

	MyAbstractAction[] actions = new MyAbstractAction[] { new CreaTabellaAction(), new EliminaTabellaAction(), new PrependiRigaAction(),
			new AppendiRigaAction(), new EliminaRigaAction(), new PrependiColonnaAction(), new AppendiColonnaAction(), new EliminaColonnaAction(),
			new MergeRigheAction(), new AllineaTestoAction() };

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		tabelle = (Tabelle) serviceManager.lookup(Tabelle.class);
		tabelleForm = (TabelleForm) serviceManager.lookup(TabelleForm.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		int i = 0;
		actionManager.registerAction("editor.tabelle.crea", actions[i++]);
		actionManager.registerAction("editor.tabelle.elimina", actions[i++]);
		actionManager.registerAction("editor.tabelle.riga.prependi", actions[i++]);
		actionManager.registerAction("editor.tabelle.riga.appendi", actions[i++]);
		actionManager.registerAction("editor.tabelle.riga.elimina", actions[i++]);
		actionManager.registerAction("editor.tabelle.colonna.prependi", actions[i++]);
		actionManager.registerAction("editor.tabelle.colonna.appendi", actions[i++]);
		actionManager.registerAction("editor.tabelle.colonna.elimina", actions[i++]);
		actionManager.registerAction("editor.tabelle.righe.merge", actions[i++]);
		actionManager.registerAction("editor.tabelle.testo.allinea", actions[i++]);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		enableActions();

	}

	public synchronized void manageEvent(EventObject event) {
		if (event instanceof SelectionChangedEvent) {
			logger.debug("manageEvent: " + event);
			SelectionChangedEvent e = (SelectionChangedEvent) event;
			if (e.isActiveNodeChanged()) {
				logger.debug("ActiveNodeChanged event");
				selectedNodes = new Node[] { e.getActiveNode() };
				enableActions();
			} else if (e.isSelectedNodesChanged()) {
				logger.debug("SelectedNodesChangedEvent");
				logger.debug("length: " + e.getSelectedNodes().length);
				selectedNodes = e.getSelectedNodes();
				for (int i = 0; i < e.getSelectedNodes().length; i++)
					logger.debug("SelectedNodes: " + e.getSelectedNodes()[i].getNodeName());
				enableActions();
			}
		} else if (event instanceof DocumentClosedEvent || event instanceof DocumentOpenedEvent) {
			for (int i = 0; i < actions.length; i++)
				actions[i].setEnabled(false);
		}
	}

	protected void enableActions() {
		if (selectedNodes == null || selectedNodes.length == 0)
			for (int i = 0; i < actions.length; i++)
				actions[i].setEnabled(false);
		else {
			for (int i = 0; i < actions.length; i++)
				actions[i].setEnabled(actions[i].canDoAction(selectedNodes));
		}
	}

	// ///////////////////////////////////////////////// Azioni
	public class CreaTabellaAction extends MyAbstractAction {

		int azione;

		public boolean canDoAction(Node[] n) {
			if (n != null && n.length > 0) {
				if (logger.isDebugEnabled())
					logger.debug("nodo selezionato: " + n[0].getNodeName());
				if (n.length == 1 && tabelle.canInsertTable(n[0]) != 0) {
					// tabelle.canInsertTable(n[0]);
					if (logger.isDebugEnabled())
						logger.debug("canCreaTabella true");
					return true;
				}
			}
			if (logger.isDebugEnabled())
				logger.debug("canCreaTabella false");
			return false;
			/*
			 * try { if (n[0].getParentNode()!= null){ Collection node_coll1 =
			 * dtdRulesManager.queryInsertableAfter(n[0].getParentNode(),n[0]);
			 * if (node_coll1.contains("h:table")){ azione = 1; return true;
			 * }else{ Collection node_coll2 =
			 * dtdRulesManager.queryInsertableBefore(n[0].getParentNode(),n[0]);
			 * if (node_coll2.contains("h:table")){ azione = 2; return true; } } } }
			 * catch(DtdRulesManagerException ex) {
			 * logger.error(ex.getMessage(),ex); } return false; } else return
			 * false;
			 */
		}

		public void actionPerformed(ActionEvent e) {
			try {
				if (tabelleForm.openForm()) {
					int righe = tabelleForm.getRows();
					int colonne = tabelleForm.getCols();
					boolean titolo = tabelleForm.hasCaption();
					boolean head = tabelleForm.hasHead();
					boolean foot = tabelleForm.hasFoot();
					Node tabella = tabelle.creaTabella(righe, colonne, titolo, head, foot);
					// if (activeNode.getParentNode()!=null){
					// Node parent = activeNode.getParentNode();
					if (canDoAction(selectedNodes)) {
						Node parent = selectedNodes[0].getParentNode();

						try {
							EditTransaction tr = documentManager.beginEdit();

							if (tabelle.canInsertTable(selectedNodes[0]) == 3) {
								if (selectedNodes[0].getNextSibling() != null) {
									Node next = selectedNodes[0].getNextSibling();

									if (dtdRulesManager.queryCanInsertBefore(parent, next, tabella)) {
										parent.insertBefore(tabella, next);
										documentManager.commitEdit(tr);
									}
								} else if (dtdRulesManager.queryCanAppend(parent, tabella)) {
									parent.appendChild(tabella);
									documentManager.commitEdit(tr);
								}
							} else if (tabelle.canInsertTable(selectedNodes[0]) == 4) {
								if (dtdRulesManager.queryCanInsertBefore(parent, selectedNodes[0], tabella)) {
									parent.insertBefore(tabella, selectedNodes[0]);
									documentManager.commitEdit(tr);
								}
							} else if (tabelle.canInsertTable(selectedNodes[0]) == 1) {
								if (dtdRulesManager.queryCanAppend(selectedNodes[0], tabella)) {
									selectedNodes[0].appendChild(tabella);
									documentManager.commitEdit(tr);
								}
							} else if (tabelle.canInsertTable(selectedNodes[0]) == 2) {
								if (dtdRulesManager.queryCanPrepend(selectedNodes[0], tabella)) {
									selectedNodes[0].insertBefore(tabella, selectedNodes[0].getFirstChild());
									documentManager.commitEdit(tr);
								}

							} else
								documentManager.rollbackEdit(tr);

						} catch (DocumentManagerException ex) {
							logger.error(ex.getMessage(), ex);
						}

					}
				}
			} catch (DtdRulesManagerException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}

	public class EliminaTabellaAction extends MyAbstractAction {
		public boolean canDoAction(Node[] n) {
			if (n.length == 1 && tabelle.canDeleteTable(n[0])) {
				return true;
			}
			return false;
			/*
			 * try{ //logger.debug("candoactionELIMINATAB"); Node nodoTab =
			 * UtilDom.findParentByName(n[0],"h:table"); if(nodoTab != null &&
			 * nodoTab.getParentNode() != null &&
			 * dtdRulesManager.queryCanDelete(nodoTab.getParentNode(),nodoTab)) {
			 * return true; } } catch(DtdRulesManagerException ex) {
			 * logger.error(ex.getMessage(),ex); } return false; }else return
			 * false;
			 */
		}

		public void actionPerformed(ActionEvent e) {
			if (canDoAction(selectedNodes)) {

				if (utilMsg.msgYesNo("editor.tabelle.elimina.msg")) {
					logger.debug("actionperformedTAB");
					Node nodoTab = UtilDom.findParentByName(selectedNodes[0], "h:table");
					// Node parent = nodoTab.getParentNode();
					// if(parent != null &&
					// dtdRulesManager.queryCanDelete(parent,nodoTab)){
					tabelle.eliminaTabella(nodoTab);

				}
				// }
				// }
				// catch(DtdRulesManagerException ex) {
				// logger.error(ex.getMessage(),ex);
			}
		}
	}

	public class PrependiRigaAction extends MyAbstractAction {
		public boolean canDoAction(Node[] n) {
			if (n.length == 1 && tabelle.canPrepRiga(n[0])) {
				return true;
			}
			return false;
		}

		public void actionPerformed(ActionEvent e) {
			if (canDoAction(selectedNodes)) {
				Node nodoRiga = UtilDom.findParentByName(selectedNodes[0], "h:tr");
				Node nuovaRiga = tabelle.creaRiga(nodoRiga, false);
				selectionManager.setActiveNode(this, nuovaRiga);

			}
		}
	}

	public class AppendiRigaAction extends MyAbstractAction {
		public boolean canDoAction(Node[] n) {
			if (n.length == 1 && tabelle.canAppRiga(n[0])) {
				return true;
			}
			return false;
		}

		public void actionPerformed(ActionEvent e) {
			if (canDoAction(selectedNodes)) {
				Node nodoRiga = UtilDom.findParentByName(selectedNodes[0], "h:tr");
				Node nuovaRiga = tabelle.creaRiga(nodoRiga, true);
				selectionManager.setActiveNode(this, nuovaRiga);
			}
		}
	}

	public class EliminaRigaAction extends MyAbstractAction {
		public boolean canDoAction(Node[] n) {
			for (int i = 0; i < n.length; i++) {
				if (!(tabelle.canDeleteRiga(n[i]))) {
					return false;
				}
			}
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			if (canDoAction(selectedNodes)) {
				for (int i = 0; i < selectedNodes.length; i++) {
					Node nodoRiga = UtilDom.findParentByName(selectedNodes[i], "h:tr");
					// Node parent = nodoRiga.getParentNode();
					tabelle.eliminaRiga(nodoRiga);
				}
			}
		}
	}

	public class PrependiColonnaAction extends MyAbstractAction {
		public boolean canDoAction(Node[] n) {
			if (n.length == 1 && tabelle.canPrepColonna(n[0])) {
				return true;
			}
			return false;
		}

		public void actionPerformed(ActionEvent e) {
			if (canDoAction(selectedNodes)) {
				Node nodoCella = UtilDom.findParentByName(selectedNodes[0], "h:td");
				Node[] nuovaColonna = tabelle.creaColonna(nodoCella, false);
				selectionManager.setSelectedNodes(this, nuovaColonna);
			}
		}
	}

	public class AppendiColonnaAction extends MyAbstractAction {
		public boolean canDoAction(Node[] n) {
			if (n.length == 1 && tabelle.canAppColonna(n[0])) {
				return true;
			}
			return false;
		}

		public void actionPerformed(ActionEvent e) {
			if (canDoAction(selectedNodes)) {
				Node nodoCella = UtilDom.findParentByName(selectedNodes[0], "h:td");
				Node[] nuovaColonna = tabelle.creaColonna(nodoCella, true);
				selectionManager.setSelectedNodes(this, nuovaColonna);

			}
		}
	}

	public class EliminaColonnaAction extends MyAbstractAction {
		public boolean canDoAction(Node[] n) {

			for (int i = 0; i < n.length; i++) {
				if (!(tabelle.canDeleteColonna(n[i]))) {
					return false;
				}

			}
			return true;
		}

		public void actionPerformed(ActionEvent e) {
			if (canDoAction(selectedNodes)) {
				for (int i = 0; i < selectedNodes.length; i++) {
					Node nodoCella = UtilDom.findParentByName(selectedNodes[i], "h:td");
					logger.debug("#### action performed elimina col");
					tabelle.eliminaColonna(nodoCella);
				}
			}
		}
	}

	public class MergeRigheAction extends MyAbstractAction {

		public boolean canDoAction(Node[] n) {

			for (int i = 1; i < n.length; i++) {
				if (!(tabelle.canMergeRighe(n[0], n[i]))) {
					return false;
				}
			}
			return true;
			/*
			 * if(UtilDom.findParentByName(n[i],"h:tr") == null){ return false; }
			 * Node nodoRiga = UtilDom.findParentByName(n[i],"h:tr"); Node next =
			 * UtilDom.findParentByName(n[i+1],"h:tr"); if (next == null ||
			 * next.getParentNode() == null){ return false; }
			 * //logger.debug("candoactionMERGEBIS"); Node parentnext =
			 * next.getParentNode(); if (!(next.getNodeName().equals ("h:tr"))){
			 * return false; } //logger.debug("candoactionMERGE3"); if
			 * (!(UtilDom.getCommonAncestor(next,nodoRiga).getNodeName().equals("h:table") ||
			 * UtilDom.getCommonAncestor(next,nodoRiga).getNodeName().equals("h:thead") ||
			 * UtilDom.getCommonAncestor(next,nodoRiga).getNodeName().equals("h:tbody"))){
			 * //logger.debug("candoactionMERGE4"); return false; } if
			 * (!(dtdRulesManager.queryCanDelete(parentnext,next))){
			 * //logger.debug("candoactionMERGE5"); return false; } } }
			 * catch(DtdRulesManagerException ex) {
			 * logger.error(ex.getMessage(),ex); } return true;
			 */
		}

		public void actionPerformed(ActionEvent e) {

			if (canDoAction(selectedNodes)) {
				// logger.debug("actionperformedMERGERIGHE");
				for (int i = 1; i < selectedNodes.length; i++) {
					Node nodoRiga = UtilDom.findParentByName(selectedNodes[0], "h:tr");
					// if (nodoRiga != null) {
					Node next = UtilDom.findParentByName(selectedNodes[i], "h:tr");
					// if(next != null){
					/*
					 * if (next !=null && next.getNodeName().equals ("h:tr") &&
					 * UtilDom.getCommonAncestor(next,nodoRiga).equals("h:table") &&
					 * (new EliminaRigaAction()).canDoAction(next)/*&&
					 * tabelle.contaCelle(nodoRiga) ==
					 * tabelle.contaCelle(previous)){
					 */

					tabelle.mergeRighe(nodoRiga, next);
					// logger.debug("mergerighe????");
					// tabelle.eliminaRiga(next);

				}

				for (int i = 1; i < selectedNodes.length; i++) {
					tabelle.eliminaRiga(selectedNodes[i]);
				}
			}
		}

	}

	public class AllineaTestoAction extends MyAbstractAction {
		public boolean canDoAction(Node[] n) {

			for (int i = 0; i < n.length; i++) {
				if (!(tabelle.canAllignTextCol(n[i]))) {
					return false;
				}
			}
			return true;

		}

		public void actionPerformed(ActionEvent e) {
			logger.debug("actionPerfAllinea");
			if (canDoAction(selectedNodes)) {
				try {

					for (int i = 0; i < selectedNodes.length; i++) {
						logger.debug("actionPerfAllinea --- selectedNodes.length:" + selectedNodes.length);
						// Node nodoCella =
						// UtilDom.findParentByName(selectedNodes[i], "h:td");
						String all = "left";

						if (dtdRulesManager.queryIsValidAttributeValue("h:td", "align", all)) {
							tabelle.allineaTestoCol(selectedNodes[i], all);
						}

					}
				} catch (DtdRulesManagerException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}

	}

}
