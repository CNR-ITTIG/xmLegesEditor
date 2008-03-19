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
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.tabelle.TabelleAction;
import it.cnr.ittig.xmleges.editor.services.dom.tabelle.Tabelle;
import it.cnr.ittig.xmleges.editor.services.form.tabelle.TabelleForm;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.action.tabelle.TabelleAction</code>.</h1>
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
	
	int start;

	int end;

	Node[] selectedNodes;

	DtdRulesManager dtdRulesManager;
	
	UtilRulesManager utilRulesManager;

	UtilMsg utilMsg;

	MyAbstractAction[] actions = new MyAbstractAction[] { new CreaTabellaAction(), new EliminaTabellaAction(), new PrependiRigaAction(),
			new AppendiRigaAction(), new EliminaRigaAction(), new PrependiColonnaAction(), new AppendiColonnaAction(), new EliminaColonnaAction(),
			new MergeUpRigheAction(), new MergeDownRigheAction(), new MergeSxColonneAction(), new MergeDxColonneAction() };

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
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
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
		actionManager.registerAction("editor.tabelle.righe.mergeup", actions[i++]);
		actionManager.registerAction("editor.tabelle.righe.mergedown", actions[i++]);
		actionManager.registerAction("editor.tabelle.colonna.mergesx", actions[i++]);
		actionManager.registerAction("editor.tabelle.colonna.mergedx", actions[i++]);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		enableActions();

	}

	public synchronized void manageEvent(EventObject event) {
		if (event instanceof SelectionChangedEvent) {
			logger.debug("manageEvent: " + event);
			SelectionChangedEvent e = (SelectionChangedEvent) event;
			start = selectionManager.getTextSelectionStart();
			end = selectionManager.getTextSelectionEnd();
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

    private Node SelezioneNodoCorrente (Node nodo, String avo) {
    	if (avo!=null)
    		nodo = UtilDom.findParentByName(nodo, avo);
    	if (nodo!=null)
    		if (nodo.getPreviousSibling() != null)
    			return nodo.getPreviousSibling();
    		else if (nodo.getNextSibling() != null)
    			    return nodo.getNextSibling();
    		     else if (nodo.getParentNode() != null)
			             return nodo.getParentNode();
    	return nodo;
    }
	
	
	// ///////////////////////////////////////////////// Azioni
	public class CreaTabellaAction extends MyAbstractAction {

		int azione;

		public boolean canDoAction(Node[] n) {
			
			//Se non è selezionato nulla le funzioni canInsertTable non funzionano
			if (end==-1) 
	            end =1;
			if (start==-1) 
	            start =1;
			
			if (n != null && n.length > 0) {
				if (logger.isDebugEnabled())
					logger.debug("nodo selezionato: " + n[0].getNodeName());
				if (n.length == 1 && tabelle.canInsertTable(n[0]) != 0) {
					return true;
				}
			}
			if (logger.isDebugEnabled())
				logger.debug("canCreaTabella false");
			return false;
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
					if (canDoAction(selectedNodes)) {
						Node parent = selectedNodes[0].getParentNode();

						try {
							EditTransaction tr = documentManager.beginEdit();

							// INSERT INSIDE
							if (tabelle.canInsertTable(selectedNodes[0]) == 3) {
								utilRulesManager.insertNodeInText(selectedNodes[0], start, end, tabella, false);
								documentManager.commitEdit(tr);
							}
							// INSERT AFTER
							else if (tabelle.canInsertTable(selectedNodes[0]) == 4) {
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
							// INSERT BEFORE	
							} else if (tabelle.canInsertTable(selectedNodes[0]) == 5) {
								if (dtdRulesManager.queryCanInsertBefore(parent, selectedNodes[0], tabella)) {
									parent.insertBefore(tabella, selectedNodes[0]);
									documentManager.commitEdit(tr);
								}
							// APPEND
							} else if (tabelle.canInsertTable(selectedNodes[0]) == 1) {
								if (dtdRulesManager.queryCanAppend(selectedNodes[0], tabella)) {
									selectedNodes[0].appendChild(tabella);
									documentManager.commitEdit(tr);
								}
							// PREPEND
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
		}

		public void actionPerformed(ActionEvent e) {
			if (canDoAction(selectedNodes)) {
				if (utilMsg.msgYesNo("editor.tabelle.elimina.msg")) {
					logger.debug("EliminoTabella");
					Node nodoTab = UtilDom.findParentByName(selectedNodes[0], "h:table");
					
					Node nodoSel = SelezioneNodoCorrente (nodoTab, "h:table");
					if (tabelle.eliminaTabella(nodoTab))
							selectionManager.setActiveNode(this,nodoSel);
				}
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
				logger.debug("Prependo Riga");
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
				logger.debug("Appendo Riga");
				Node nodoRiga = UtilDom.findParentByName(selectedNodes[0], "h:tr");
				Node nuovaRiga = tabelle.creaRiga(nodoRiga, true);
				selectionManager.setActiveNode(this, nuovaRiga);
			}
		}
	}

	public class EliminaRigaAction extends MyAbstractAction {
		//
		// Per elimina righe lascio la possibilità della selezione multipla, quindi
		// non è più suff.controllare "canDeleteRiga" x singole righe, bisogna anche
		// considerare il numero di righe che andrò a cancellare e il tipo.
		//     (Al momento accantonato)
		//

		public boolean canDoAction(Node[] n) {
			if (n.length == 1 && tabelle.canDeleteRiga(n[0])) {
				return true;
			}
			return false;

// versione con selez.multipla						
//			//int ContaHead = 0;
//			//int ContaFoot = 0;
//			int ContaBody = 0;
//			for (int i = 0; i < n.length; i++) {
//				   //if (n[i].getParentNode().getNodeName().equals("h:thead")) ContaHead++;
//				   //if (n[i].getParentNode().getNodeName().equals("h:tfoot")) ContaFoot++;
//				   if (n[i].getParentNode().getNodeName().equals("h:tbody")) ContaBody++;		   
//			}
//			if (UtilDom.findParentByName(selectedNodes[0], "h:table").getLastChild().getChildNodes().getLength() > ContaBody)
//				return true;
//			else 
//				return false;
		}

		public void actionPerformed(ActionEvent e) {
			if (canDoAction(selectedNodes)) {
				for (int i = 0; i < selectedNodes.length; i++) {
					Node nodoRiga = UtilDom.findParentByName(selectedNodes[i], "h:tr");					
					Node nodoSel = SelezioneNodoCorrente (nodoRiga, "h:tr");
					EditTransaction tr = null;
		  			try {
		  			  tr = documentManager.beginEdit();	
		  			  logger.debug("EliminoRiga");	
		  			  if (tabelle.eliminaRiga(nodoRiga)) {
							selectionManager.setActiveNode(null,nodoSel);
							documentManager.commitEdit(tr); 
 					   }	   
 					   else 
 						   documentManager.rollbackEdit(tr);
					} 
		  			catch (DocumentManagerException ex) {
						logger.error(ex.getMessage(), ex);
						documentManager.rollbackEdit(tr);
					}											
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
				logger.debug("PrependoColonna");
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
				logger.debug("AppendoColonna");
				Node nodoCella = UtilDom.findParentByName(selectedNodes[0], "h:td");
				Node[] nuovaColonna = tabelle.creaColonna(nodoCella, true);
				selectionManager.setSelectedNodes(this, nuovaColonna);

			}
		}
	}

	public class EliminaColonnaAction extends MyAbstractAction {
		public boolean canDoAction(Node[] n) {
			//
			// Per elimina colonna lascio la possibilità della selezione multipla, quindi
			// non è più suff.controllare "canDeleteColonna" x singole colonne, ma bisogna
			// contare il numero di colonne che andrò a cancellare.
			//     (Al momento accantonato)
			//

			if (n.length == 1 && tabelle.canDeleteColonna(n[0])) {
				return true;
			}
			return false;
						
//versione con selez.multipla
//			if (UtilDom.findParentByName(selectedNodes[0], "h:table").getLastChild().getChildNodes().getLength() > n.length)
//				return true;
//			else 
//				return false;			
		}

		public void actionPerformed(ActionEvent e) {
			if (canDoAction(selectedNodes)) {
				for (int i = 0; i < selectedNodes.length; i++) { //????????????????????
					Node nodoCella = UtilDom.findParentByName(selectedNodes[i], "h:td");
					logger.debug("EliminoColonna");
					Node nodoSel = SelezioneNodoCorrente (nodoCella, null);
		 			EditTransaction tr = null;				
		  			try {
		  			  tr = documentManager.beginEdit();	
	
 					  if (tabelle.eliminaColonna(nodoCella)) {
							selectionManager.setActiveNode(this,nodoSel);
							documentManager.commitEdit(tr); 
 					   }	   
 					   else 
 						   documentManager.rollbackEdit(tr);
					} 
		  			catch (DocumentManagerException ex) {
						logger.error(ex.getMessage(), ex);
						documentManager.rollbackEdit(tr);
					}					  
 			    }
			}
		}
	}

	public class MergeSxColonneAction extends MyAbstractAction {

		public boolean canDoAction(Node[] n) {

    		if (n.length == 1 && tabelle.canMergeSxColonne(n[0])) 
					return true;
			return false;
		}

		public void actionPerformed(ActionEvent e) {

			logger.debug("MERGEsxCOLONNE"); 
			Node nodoColonna = UtilDom.findParentByName(selectedNodes[0], "h:td");
			Node nodoSel = nodoColonna.getPreviousSibling();

 			EditTransaction tr = null;				
  			try {
  			  tr = documentManager.beginEdit();	
			  if (tabelle.mergeColonne(nodoColonna.getPreviousSibling(), nodoColonna)) {
			   if (tabelle.eliminaColonna(nodoColonna)) {
				   selectionManager.setActiveNode(this,nodoSel);
				   documentManager.commitEdit(tr); 
			   }	   
			   else 
				   documentManager.rollbackEdit(tr);
			  }
			} 
  			catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
			}
		}
	}

	public class MergeDxColonneAction extends MyAbstractAction {

		public boolean canDoAction(Node[] n) {

			
    		if (n.length == 1 && tabelle.canMergeDxColonne(n[0])) 
					return true;
			return false;
		}

		public void actionPerformed(ActionEvent e) {

			logger.debug("MERGEdxCOLONNE");
			Node nodoColonna = UtilDom.findParentByName(selectedNodes[0], "h:td");
			Node nodoSel = nodoColonna;
 			EditTransaction tr = null;				
  			try {
  			  tr = documentManager.beginEdit();	
			  if (tabelle.mergeColonne(nodoColonna, nodoColonna.getNextSibling())) {
			   if (tabelle.eliminaColonna(nodoColonna.getNextSibling())) { 
				   selectionManager.setActiveNode(this,nodoSel);
				   documentManager.commitEdit(tr); 
			   }	   
			   else 
				   documentManager.rollbackEdit(tr);
			  }
			} 
  			catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
			}			
		}
	}
		
	public class MergeUpRigheAction extends MyAbstractAction {

		public boolean canDoAction(Node[] n) {

//GERARDO:  Funzione scritta per la selezione multipla... attualmente non usata
//			for (int i = 1; i < n.length; i++) {
//				if (!(tabelle.canMergeRighe(n[0], n[i]))) {
//					return false;
//				}
//			}
			
			if (n.length == 1 && tabelle.canMergeUpRighe(n[0])) {
					return true;
			}	
			return false;
		}

		public void actionPerformed(ActionEvent e) {

			logger.debug("MERGEupRIGHE"); 
			Node nodoRiga1 = UtilDom.findParentByName(selectedNodes[0], "h:tr");
			Node nodoRiga2 = null;
 			EditTransaction tr = null;				
  			try {
  			  tr = documentManager.beginEdit();	
			  if (nodoRiga1.getParentNode().getNodeName().equals("h:tbody")) 
		    	nodoRiga2 = nodoRiga1.getPreviousSibling();	
		      else
		    	nodoRiga2 = nodoRiga1.getParentNode().getNextSibling().getLastChild();
		      Node nodoSel = nodoRiga2;
		      logger.debug("mergerighe UP...");
		      if (tabelle.mergeRighe(nodoRiga2, nodoRiga1)) {
			   logger.debug("eliminariga...");
			   if (tabelle.eliminaRiga(nodoRiga1)) {
				   selectionManager.setActiveNode(this,nodoSel);
				   documentManager.commitEdit(tr); 
			   }	   
			   else 
				   documentManager.rollbackEdit(tr);
			  }
			} 
  			catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
			}
		}
	}

	public class MergeDownRigheAction extends MyAbstractAction {
		
		public boolean canDoAction(Node[] n) {
			
			if (n.length == 1 && tabelle.canMergeDownRighe(n[0])) {			
					return true;
			}			
			return false;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			
			logger.debug("MERGEdownRIGHE"); 
			Node nodoRiga1 = UtilDom.findParentByName(selectedNodes[0], "h:tr");
			Node nodoRiga2 = null;
 			EditTransaction tr = null;				
  			try {			
  			  tr = documentManager.beginEdit();
		      if (nodoRiga1.getParentNode().getNodeName().equals("h:tbody")) 
		    	nodoRiga2 = nodoRiga1.getNextSibling();	
		      else {
		    	nodoRiga2 = nodoRiga1.getParentNode();
 		  	    while (!nodoRiga2.getNextSibling().getNodeName().equals("h:tbody"))
		  		  nodoRiga2 = nodoRiga2.getNextSibling();
		  	    nodoRiga2 = nodoRiga2.getNextSibling().getFirstChild();		    
		      }
		    			
		      Node nodoSel = nodoRiga1;
		      logger.debug("mergerighe DOWN...");
			  if (tabelle.mergeRighe(nodoRiga1, nodoRiga2)) {
			   logger.debug("eliminariga...");
			   if (tabelle.eliminaRiga(nodoRiga2)) { 
				   selectionManager.setActiveNode(this,nodoSel);
				   documentManager.commitEdit(tr); 
			   }	   
			   else 
				   documentManager.rollbackEdit(tr);
			  }
			} 
  			catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
			}
		}
	}
	
}
