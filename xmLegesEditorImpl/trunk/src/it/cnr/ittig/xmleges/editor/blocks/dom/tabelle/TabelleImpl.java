package it.cnr.ittig.xmleges.editor.blocks.dom.tabelle;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.tabelle.Tabelle;

import java.util.Collection;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.tabelle.Tabelle</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>dtd-rules-manager</li>
 * <li>document-manager</li>
 * </ul>
 * <h1>I18n</h1>
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


public class TabelleImpl implements Tabelle, Loggable, Serviceable {
	Logger logger;

	Node tabella;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	
	public int canInsertTable(Node node) {
		try {
			if (node != null) {
				if (dtdRulesManager.queryAppendable(node).contains("h:table")) 
					return 1;
				if (dtdRulesManager.queryPrependable(node).contains("h:table")) 
				    return 2;
				
				if (node.getParentNode() != null) {
					if(dtdRulesManager.queryInsertableInside(node.getParentNode(), node).contains("h:table"))
						return 3;
					if (dtdRulesManager.queryInsertableAfter(node.getParentNode(), node).contains("h:table")) 
						return 4;
					if (dtdRulesManager.queryInsertableBefore(node.getParentNode(), node).contains("h:table")) 
						return 5;
				}
			}
		} catch (DtdRulesManagerException ex) {
			return 0;
		}
		return 0;
	}

	
	public boolean canDeleteTable(Node node) {
		try {
			Node nodoTab = UtilDom.findParentByName(node, "h:table");
			if (nodoTab != null && nodoTab.getParentNode() != null && dtdRulesManager.queryCanDelete(nodoTab.getParentNode(), nodoTab)) {
				return true;
			}
		} catch (DtdRulesManagerException ex) {
			return false;
		}
		return false;
	}

	
	public boolean canPrepRiga(Node node) {
		try {
			if (UtilDom.findParentByName(node, "h:tr") != null) {
				Node nodoRiga = UtilDom.findParentByName(node, "h:tr");
				if (nodoRiga.getParentNode() != null) {
					Node parent = nodoRiga.getParentNode();
					if (parent.getNodeName().equals("h:tbody")) {
						Collection coll = dtdRulesManager.queryInsertableBefore(nodoRiga.getParentNode(), nodoRiga);
						if (nodoRiga != null && parent != null && coll.contains("h:tr")) {
							return true;
						}
					}
				}
			}
		} catch (DtdRulesManagerException ex) {
			return false;
		}
		return false;
	}

	
	public boolean canAppRiga(Node node) {
		try {
			if (UtilDom.findParentByName(node, "h:tr") != null) {
				Node nodoRiga = UtilDom.findParentByName(node, "h:tr");
				if (nodoRiga.getParentNode() != null) {
					Node parent = nodoRiga.getParentNode();
					if (parent.getNodeName().equals("h:tbody")) {
						Collection coll = dtdRulesManager.queryInsertableAfter(parent, nodoRiga);
						if (nodoRiga != null && coll.contains("h:tr")) {
							return true;
						}
					}
				}
			}
		} catch (DtdRulesManagerException ex) {	
			return false;
		}
		return false;
	}

	public boolean canAllignTextCol(Node node) {

		Node nodoAvo = UtilDom.findParentByName(node, "h:table");
		if (nodoAvo != null) {
	         NodeList figli = nodoAvo.getChildNodes();
	         for (int i=1; i< figli.getLength(); i++)
		         if (figli.item(i).getNodeName().equals("h:thead") || figli.item(i).getNodeName().equals("h:tfoot"))
		        	 return true;
             if (nodoAvo.getLastChild().getChildNodes().getLength()>1) 
            	 return true;
             return false;
		}
		return false;
	}

	
	public boolean canDeleteRiga(Node node) {

		if (UtilDom.findParentByName(node, "h:tr") != null) {
			Node nodoRiga = UtilDom.findParentByName(node, "h:tr");
			if (nodoRiga.getParentNode() != null) {
				Node parent = nodoRiga.getParentNode();
				if (parent.getNodeName().equals("h:thead") || parent.getNodeName().equals("h:tfoot"))
					return true;
				else {
					try {
						if (dtdRulesManager.queryCanDelete(parent, nodoRiga))
							return true;
					} catch (DtdRulesManagerException ex) {
						return false;
					}
				}
			}
		}
		return false;
	}

	public boolean canPrepColonna(Node node) {

		try {
			if (UtilDom.findParentByName(node, "h:td") != null) {
				Node nodoCella = UtilDom.findParentByName(node, "h:td");
				if (nodoCella.getParentNode() != null) {
					Node parent = nodoCella.getParentNode();
					Collection coll = dtdRulesManager.queryInsertableBefore(parent, nodoCella);
					if (nodoCella != null && parent != null && coll.contains("h:td")) {
						return true;
					}
				}
			}
		} catch (DtdRulesManagerException ex) {
			return false;
		}

		return false;
	}

	public boolean canAppColonna(Node node) {

		try {
			if (UtilDom.findParentByName(node, "h:td") != null) {
				Node nodoCella = UtilDom.findParentByName(node, "h:td");
				if (nodoCella.getParentNode() != null) {
					Node parent = nodoCella.getParentNode();
					Collection coll = dtdRulesManager.queryInsertableBefore(parent, nodoCella);
					if (nodoCella != null && parent != null && coll.contains("h:td")) {
						return true;
					}
				}
			}
		} catch (DtdRulesManagerException ex) {
			//logger.error(ex.getMessage(), ex);
			return false;
		}

		return false;
	}

	public boolean canDeleteColonna(Node node) {

		try {
			if (UtilDom.findParentByName(node, "h:td") != null) {
				Node nodoCella = UtilDom.findParentByName(node, "h:td");
				if (nodoCella.getParentNode() != null) {
					Node parent = nodoCella.getParentNode();
					if (dtdRulesManager.queryCanDelete(parent, nodoCella)) {
						return true;
					}
				}

			}
		} catch (DtdRulesManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return false;
		}
		return false;
	}

	public boolean canMergeSxColonne(Node node) {
		
		try {
		    if (UtilDom.findParentByName(node, "h:td") != null) {
		      Node nodoCella = UtilDom.findParentByName(node, "h:td");
		      if (nodoCella.getPreviousSibling() != null) { 
	 		    if (dtdRulesManager.queryCanDelete(nodoCella.getParentNode(), nodoCella)) {
				  logger.debug("candoaction-MergeSxColonne");
				  return true;
			    }
		      }
		    }

		} catch (DtdRulesManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return false;
		}

		return false;
	}
	
    public boolean canMergeDxColonne(Node node) {
		
    	try {
		    if (UtilDom.findParentByName(node, "h:td") != null) {
		      Node nodoCella = UtilDom.findParentByName(node, "h:td");
		      if (nodoCella.getNextSibling() != null) { 
	 		    if (dtdRulesManager.queryCanDelete(nodoCella.getParentNode(), nodoCella)) {
				  logger.debug("candoaction-MergeDxColonne");
				  return true;
			    }
		      }
		    }

		} catch (DtdRulesManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return false;
		}

		return false;
	}
	public boolean canMergeUpRighe(Node node) {

		try {
			Node nodoRiga1 = UtilDom.findParentByName(node, "h:tr");
		    if (nodoRiga1 != null) { 
		      Node nodoRiga2 = null;
			  if (nodoRiga1.getParentNode().getNodeName().equals("h:tbody")) 
		    	nodoRiga2 = nodoRiga1.getPreviousSibling();	
		      else 
		    	if (nodoRiga1.getParentNode().getNodeName().equals("h:tfoot")) 
		    	  nodoRiga2 = nodoRiga1.getParentNode().getNextSibling().getFirstChild();
		    	else 
		    	  return false;
		      if ((nodoRiga2 != null) && (nodoRiga2.getNodeName().equals("h:tr"))) {
			    Node parent = nodoRiga2.getParentNode();
	 		    if (dtdRulesManager.queryCanDelete(parent, nodoRiga2)) {
				  logger.debug("candoaction-MergeUpRighe");
				  return true;
			    }
		      }
		    }

		} catch (DtdRulesManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return false;
		}
		
		return false;
	}
	
	public boolean canMergeDownRighe(Node node) {
		
		try {
			Node nodoRiga1 = UtilDom.findParentByName(node, "h:tr");
		    if (nodoRiga1 != null) { 
		      Node nodoRiga2 = null;
		      if (nodoRiga1.getParentNode().getNodeName().equals("h:tbody"))
		    	nodoRiga2 = nodoRiga1.getNextSibling();
		      else 
		    	if (nodoRiga1.getParentNode().getNodeName().equals("h:thead")) {
		    	  nodoRiga2 = nodoRiga1.getParentNode();
		    	  while (!nodoRiga2.getNextSibling().getNodeName().equals("h:tbody"))
		    		  nodoRiga2 = nodoRiga2.getNextSibling();
		    	  nodoRiga2 = nodoRiga2.getNextSibling().getFirstChild();
		    	}  
		    	else return false;
		      if ((nodoRiga2 != null) && (nodoRiga2.getNodeName().equals("h:tr"))) {
			    Node parent = nodoRiga2.getParentNode();
	 		    if (dtdRulesManager.queryCanDelete(parent, nodoRiga2)) {
				  logger.debug("candoaction-MergeDownRighe");
				  return true;
			    }
		      }
		    }

		} catch (DtdRulesManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return false;
		}
		
		return false;
	}
	
	public boolean canMergeRighe(Node node1, Node node2) {

		try {
			if (UtilDom.findParentByName(node1, "h:tr") != null && UtilDom.findParentByName(node2, "h:tr") != null) {
				Node nodoRiga1 = UtilDom.findParentByName(node1, "h:tr");
				Node nodoRiga2 = UtilDom.findParentByName(node2, "h:tr");
				if (nodoRiga2.getParentNode() != null) {
					Node parentnext = nodoRiga2.getParentNode();

					if ((UtilDom.getCommonAncestor(nodoRiga2, nodoRiga1).getNodeName().equals("h:table")
							|| UtilDom.getCommonAncestor(nodoRiga2, nodoRiga1).getNodeName().equals("h:thead") || UtilDom.getCommonAncestor(nodoRiga2,
							nodoRiga1).getNodeName().equals("h:tbody"))) {

						if (dtdRulesManager.queryCanDelete(parentnext, nodoRiga2)) {
							// logger.debug("candoactionMERGE5");
							return true;
						}
					}

				}

			}

		} catch (DtdRulesManagerException ex) {
			//logger.error(ex.getMessage(), ex);
			return false;
		}

		return false;
	}


	// /////////////////////////////////////////////////////// Tabella Interface
	public Node creaTabella(int righe, int colonne, boolean caption, boolean head, boolean foot) {

		/*
		 * Tabelle: caption = titolo thead = testa o parte iniziale, che
		 * contiene le indicazioni sul contenuto della tabella; tfoot = piede o
		 * conclusione della tabella, che consente di tirare le somme tbody =
		 * contenuto o parte centrale (per adesso supponiamo che sia presente di
		 * default.
		 */
		Node root = documentManager.getDocumentAsDom().getDocumentElement();

		tabella = documentManager.getDocumentAsDom().createElementNS(UtilDom.getNameSpaceURIforElement(root, "h"), "h:table");

		if (caption) {
			Node titolo = tabella.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(root, "h"), "h:caption");

			try {
			 	Node nodoTesto = tabella.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(root, "h"), "h:caption");
			 	
				if (dtdRulesManager.queryCanAppend(titolo, nodoTesto)) {
					titolo.appendChild(nodoTesto);
				}
			} catch (DtdRulesManagerException ex) {
				logger.error(ex.getMessage(), ex);
			}
			tabella.appendChild(titolo);
		}

		if (head) {
			Node intestaz = tabella.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(root, "h"), "h:thead");
			// insertTab(intestaz,1,colonne);
			Node newrow = tabella.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(root, "h"), "h:tr");
			// insertCelle(newrow,colonne);
			for (int i = 1; i <= colonne; i++) {
				Node newcell = tabella.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(root, "h"), "h:td");
				// insertCelle(newrow,colonne);
				newrow.appendChild(newcell);
			}
			intestaz.appendChild(newrow);
			tabella.appendChild(intestaz);
		}

		if (foot) {
			Node piede = tabella.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(root, "h"), "h:tfoot");
			// insertTab(piede,1,colonne);
			Node newrow = tabella.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(root, "h"), "h:tr");
			// insertCelle(newrow,colonne);
			for (int i = 1; i <= colonne; i++) {
				Node newcell = tabella.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(root, "h"), "h:td");
				// insertCelle(newrow,colonne);
				newrow.appendChild(newcell);
			}
			piede.appendChild(newrow);
			tabella.appendChild(piede);

		}

		Node corpo = tabella.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(root, "h"), "h:tbody");
		insertTab(corpo, righe, colonne);
		tabella.appendChild(corpo);
		return tabella;
	}

	public boolean eliminaTabella(Node pos) {

		if (canDeleteTable(pos)) {
			EditTransaction tr = null;
			try {
				tr = documentManager.beginEdit();
				pos.getParentNode().removeChild(pos);
				documentManager.commitEdit(tr);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
				return false;
			}
			return true;
		}
        return false;
	}

	// crea una riga prima o dopo quella in posizione pos
	public Node creaRiga(Node pos, boolean appendi) {

		if (pos.getParentNode() != null) {
			Node genitore = pos.getParentNode();
			int numCelle = pos.getChildNodes().getLength(); 
			logger.debug("numero di celle della nuova riga: " + numCelle);

			// crea una nuova riga;
			Node nuovaRiga = pos.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(pos, "h"), "h:tr");
			for (int i = 0; i < numCelle; i++) {
				Node nuovaCella = pos.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(pos, "h"), "h:td");
				nuovaRiga.appendChild(nuovaCella);
			}

			if (appendi && canAppRiga(pos)) {
				EditTransaction tr = null;
				try {
					tr = documentManager.beginEdit();
					genitore.insertBefore(nuovaRiga, pos.getNextSibling());
					documentManager.commitEdit(tr);
				} catch (DocumentManagerException ex) {
					logger.error(ex.getMessage(), ex);
					documentManager.rollbackEdit(tr);
				}

			}

			else if (!appendi && canPrepRiga(pos)) {
				EditTransaction tr = null;
				try {
					tr = documentManager.beginEdit();
					genitore.insertBefore(nuovaRiga, pos);
					documentManager.commitEdit(tr);
				}
				catch (DocumentManagerException ex) {
					logger.error(ex.getMessage(), ex);
					documentManager.rollbackEdit(tr);
				}
			}
			return nuovaRiga;
		}
		return null;
	}


	public boolean eliminaRiga(Node pos) {
		
		if (canDeleteRiga(pos)) {
			Node genitore = pos.getParentNode();
			logger.debug("RimuovoRIGAtabella");
			if (genitore.getNodeName().equals("h:thead") || genitore.getNodeName().equals("h:tfoot")) {
				//rimuovo il h:tr
				genitore.removeChild(pos);
				//rimuovo anche il THEAD o TFOOT				
				pos = genitore;				
			    genitore = genitore.getParentNode(); 		
			}   
			genitore.removeChild(pos);
			return true;
		}
		logger.debug("RimozioneRIGAtabella FALLITA");
		return false;
	}

	public boolean mergeColonne(Node pos1, Node pos2) {

		//merge generico, anche di colonne non adiacenti
		Node parent = UtilDom.findParentByName(pos1, "h:table");
		if (parent != null && UtilDom.findParentByName(pos2, "h:table") == parent) {
			int numeroCol1 = -1;
			int numeroCol2 = -1;
			NodeList pos1parent = pos1.getParentNode().getChildNodes();
			NodeList pos2parent = pos2.getParentNode().getChildNodes();
			for (int i=0; i<pos1parent.getLength(); i++)
				if (pos1parent.item(i).equals(pos1)) numeroCol1 = i;
			for (int i=0; i<pos2parent.getLength(); i++)
				if (pos2parent.item(i).equals(pos2)) numeroCol2 = i;
			if (numeroCol1 != numeroCol2) {
				Node sostituisci = parent.getFirstChild();
				while (sostituisci != null) {
				   if (sostituisci.getNodeName().equals("h:thead") || sostituisci.getNodeName().equals("h:tfoot") || sostituisci.getNodeName().equals("h:tbody")) {
					   Node Riga = (Node) sostituisci.getFirstChild();
					   while (Riga != null) {
					     Node A = Riga.getFirstChild();
						 Node B = A;
					     for (int i=0; i<numeroCol1; i++) 
						     A = A.getNextSibling();
					     for (int i=0; i<numeroCol2; i++) 
						     B = B.getNextSibling();

									
						 if (A.getLastChild() != null && B.getFirstChild() != null) {
					      if ((A.getLastChild().getNodeType()==Node.TEXT_NODE) && (B.getFirstChild().getNodeType()==Node.TEXT_NODE)) {
  					         //merge fra 2 nodi testo (e copiare altri figli)
					         A.getLastChild().setNodeValue(A.getLastChild().getNodeValue()+" "+B.getFirstChild().getNodeValue());
					         NodeList figli = B.getChildNodes();
					         int numeroFigli = figli.getLength();
					         for (int i=1; i< numeroFigli; i++)
						         A.appendChild(figli.item(1));
					         Riga = Riga.getNextSibling();
					         continue;
				          }
						 } 
				         //else { 
					       //solo copia dei figli
					       NodeList figli = B.getChildNodes();
					       int numeroFigli = figli.getLength();
					       for (int i=0; i<numeroFigli; i++)
						       A.appendChild(figli.item(0));
				         //}				
 					     Riga = Riga.getNextSibling();
					   }
				   }
				   sostituisci = sostituisci.getNextSibling();
				}				
			} 
			return true;
		}
		return false;
	}
	
	public boolean mergeRighe(Node pos1, Node pos2) {
		
//		if (pos2.getParentNode().getNodeName().equals("h:tfoot")) {
//		  Node temp = pos2;
//		  pos2 = pos1;
//		  pos1 = temp;
//		}
	
		Node nodoriga1 = UtilDom.findParentByName(pos1, "h:tr");
		Node nodoriga2 = UtilDom.findParentByName(pos2, "h:tr");

	    //ERRORE: non mi fa il merge ALTO da Piede a Corpo (levato il controllo if per questo)
		//if (canMergeRighe(pos1, pos2)) {
				Vector figlinodoriga1 = getChildHtd(nodoriga1);
				Vector figlinodoriga2 = getChildHtd(nodoriga2);
				if (figlinodoriga1.size() == figlinodoriga2.size()) {	
					for (int j = 0; j < figlinodoriga1.size(); j++) {
						
						Node A = (Node) figlinodoriga1.elementAt(j);
					    Node B = (Node) figlinodoriga2.elementAt(j);	    			
			            if (A.getLastChild() != null && B.getFirstChild() != null) {
						  if ((A.getLastChild().getNodeType()==Node.TEXT_NODE) &&
								(B.getFirstChild().getNodeType()==Node.TEXT_NODE)) {
							//merge fra 2 nodi testo (e copiare altri figli)
							A.getLastChild().setNodeValue(A.getLastChild().getNodeValue()+" "+B.getFirstChild().getNodeValue());
							NodeList figli = B.getChildNodes();
						    int numeroFigli = figli.getLength();
							for (int i=1; i<numeroFigli; i++)
								A.appendChild(figli.item(1));
							continue;
						  }
			            }	
						//else { 
							//solo copia dei figli
							NodeList figli = B.getChildNodes();
						    int numeroFigli = figli.getLength();
							for (int i=0; i<numeroFigli; i++)
								A.appendChild(figli.item(0));
						//}		
					}
					return true;
				}
				return false;
		//}

	}

	// inserisce una colonna prima o dopo pos, dove pos è una cella
	// della colonna di riferimento
	public Node[] creaColonna(Node pos, boolean appendi) {

		Node genitore = pos.getParentNode();
		int indice = UtilDom.getChildIndex(genitore, pos); // indice della colonna
		Node nonno = genitore.getParentNode();
		Node bisnonno = nonno.getParentNode();

		Vector headbodyfoot = UtilDom.getChildElements(bisnonno);
		Vector colonna = new Vector();

		EditTransaction tr = null;
		try {
			tr = documentManager.beginEdit();

			for (int j = 0; j < headbodyfoot.size(); j++) {
				Node htr = (Node) headbodyfoot.elementAt(j);
				if (!(htr.getNodeName().equals("h:caption"))) {
					NodeList fg = htr.getChildNodes();// lista delle righe
					for (int i = 0; i < fg.getLength(); i++) {
						NodeList np = fg.item(i).getChildNodes(); // lista colonne
						Node nuovaCella = pos.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(pos, "h"), "h:td");

						if (appendi) {
							fg.item(i).insertBefore(nuovaCella, np.item(indice + 1));
							colonna.add(np.item(indice + 1));
						} else {
							fg.item(i).insertBefore(nuovaCella, np.item(indice));
							colonna.add(np.item(indice));
						}
					}
				}
			}
			documentManager.commitEdit(tr);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			documentManager.rollbackEdit(tr);
		}
		// }
		logger.debug("###### elementi colonna: " + colonna.size());

		Node[] colArr = new Node[colonna.size()];
		colonna.copyInto(colArr);

		return colArr;

	}

	// trovo l'indice della colonna da eliminare
	// e da ogni elemento-riga nell'albero tolgo
	// l'elemento-cella con quell'indice
	public boolean eliminaColonna(Node pos) {

		Node genitore = pos.getParentNode();
		int indice = UtilDom.getChildIndex(genitore, pos);
		Node nonno = genitore.getParentNode();
		Node bisnonno = nonno.getParentNode();
		// NodeList fg = nonno.getChildNodes(); //lista delle righe
		Vector headbodyfoot = UtilDom.getChildElements(bisnonno);

		if (canDeleteColonna(pos)) {
				for (int j = 0; j < headbodyfoot.size(); j++) { 
					Node htr = (Node) headbodyfoot.elementAt(j);
					if (!(htr.getNodeName().equals("h:caption"))) {
						NodeList fg = htr.getChildNodes();// lista delle righe
						for (int i = 0; i < fg.getLength(); i++) {
							NodeList np = fg.item(i).getChildNodes();// lista colonne
							Node daeliminare = np.item(indice);
							fg.item(i).removeChild(daeliminare);
							logger.debug("##### elimina colonna");
						}
					}
				}
            return true;
		}
		return false;
	}

	// inserisce in un vector i figli di tipo h:td di un nodo riga
	public Vector getChildHtd(org.w3c.dom.Node elem) {

		Vector childrenhtd = new Vector();
		if (elem != null) {
			org.w3c.dom.NodeList children = elem.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				if (children.item(i).getNodeName() == "h:td")
					childrenhtd.add(children.item(i));
			}
		}
		return childrenhtd;
	}

	public void allineaTestoCol(Node pos, String allinea) {

		
	//prova NON FUNZIONANTE	
//		if (canAllignTextCol(pos)) {
//			EditTransaction tr = null;
//			try {
//				int indice = UtilDom.getChildIndex(pos.getParentNode(), pos);
//				Node nodo = UtilDom.findParentByName(pos, "h:table").getFirstChild();
//				while (nodo != null) {
//					Node figlio = nodo.getFirstChild();
//					for (int i=0; i<indice; i++)
//						 figlio.getNextSibling();
//					UtilDom.setAttributeValue(figlio, "align", allinea);
//				}
//			} catch (Exception ex) {
//			    logger.error(ex.getMessage(), ex);
//			    documentManager.rollbackEdit(tr);
//			}
//				
//		}
				
		Node genitore = pos.getParentNode();
		int indice = UtilDom.getChildIndex(genitore, pos);
		Node nonno = genitore.getParentNode();
		Node bisnonno = nonno.getParentNode();
		Vector headbodyfoot = UtilDom.getChildElements(bisnonno);
		// NodeList righe = nonno.getChildNodes(); //lista delle righe

		if (canAllignTextCol(pos)) {
			
			EditTransaction tr = null;
			try {
				tr = documentManager.beginEdit();
				for (int j = 1; j < headbodyfoot.size(); j++) {
					Node htr = (Node) headbodyfoot.elementAt(j);
					NodeList righe = htr.getChildNodes();// lista delle righe

					for (int i = 0; i < righe.getLength(); i++) {
						NodeList colonne = righe.item(i).getChildNodes();
						// per tutte le righe prendi la lista dei figli 
						Node nodoIndice = colonne.item(indice);
//						(per correggere errore : FORSE)
						if (nodoIndice != null) 
//						if (!nodoIndice.getNodeName().equals("h:td"))
//  						    nodoIndice = UtilDom.findParentByName(nodoIndice, "h:td");
						// fra tutti i figli prendi quelli con indice=col da allineare
						   UtilDom.setAttributeValue(nodoIndice, "align", allinea);
					}

				}
				documentManager.commitEdit(tr);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
			}
		}
	}

	// a partire dal nodo corrente, costruisce le righe e le colonne della tabella
	protected Node insertTab(Node inizio, int righe, int colonne) {

		for (int i = 1; i <= righe; i++) {
			Node newrow = inizio.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(inizio, "h"), "h:tr");
			insertCelle(newrow, colonne);
			inizio.appendChild(newrow);
		}
		return inizio;
	}

	// costruisce le celle della riga
	protected Node insertCelle(Node riga, int colonne) {

		for (int h = 1; h <= colonne; h++) {
			Node newcell = riga.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(riga, "h"), "h:td");
			riga.appendChild(newcell);
		}
		return riga;
	}

}
