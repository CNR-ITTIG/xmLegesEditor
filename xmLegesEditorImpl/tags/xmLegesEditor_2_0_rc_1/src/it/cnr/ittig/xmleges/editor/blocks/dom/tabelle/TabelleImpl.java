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

/*
 * Il nodo che viene passato ad ogni metodo e' il nodo giusto nella gerarchia
 * dell'albero DOM
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

	
	public boolean canDeleteRiga(Node node) {

		if (UtilDom.findParentByName(node, "h:tr") != null) {
			Node nodoRiga = UtilDom.findParentByName(node, "h:tr");
			if (nodoRiga.getParentNode() != null) {
				Node parent = nodoRiga.getParentNode();
				if (parent.getNodeName().equals("h:thead") || parent.getNodeName().equals("h:tfoot"))
					return true;
				else {
					try {
						/*
						 * if (UtilDom.findParentByName(node,"h:tr") != null){
						 * Node nodoRiga =
						 * UtilDom.findParentByName(node,"h:tr"); if
						 * (nodoRiga.getParentNode() != null){ Node parent =
						 * nodoRiga.getParentNode();
						 */
						// if (parent.getNodeName().equals("h:thead") ||
						// parent.getNodeName().equals("h:tfoot")
						// || dtdRulesManager.queryCanDelete(parent,nodoRiga)){
						if (dtdRulesManager.queryCanDelete(parent, nodoRiga))
							return true;
						// }

						// }
						// }
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
					// Collection coll =
					// dtdRulesManager.queryInsertableBefore(parent,nodoCella);
					if (dtdRulesManager.queryCanDelete(parent, nodoCella)) {
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

	public boolean canMergeRighe(Node node1, Node node2) {

		try {
			if (UtilDom.findParentByName(node1, "h:tr") != null && UtilDom.findParentByName(node2, "h:tr") != null) {
				Node nodoRiga1 = UtilDom.findParentByName(node1, "h:tr");
				Node nodoRiga2 = UtilDom.findParentByName(node2, "h:tr");
				if (nodoRiga2.getParentNode() != null) {
					Node parentnext = nodoRiga2.getParentNode();
					// if (!(next.getNodeName().equals ("h:tr"))){

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

	// da fare canDo per allineamento verticale ??????????

	public boolean canAllignTextCol(Node node) {
		try {
			if (UtilDom.findParentByName(node, "h:td") != null) {
				// if ((n[i].getNodeType() == Node.ELEMENT_NODE)){
				if (dtdRulesManager.queryIsValidAttribute("h:td", "align")) {
					return true;

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
				Node nodoTesto = tabella.getOwnerDocument().createTextNode("");
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

	public void eliminaTabella(Node pos) {

		if (canDeleteTable(pos)) {
			EditTransaction tr = null;
			try {
				tr = documentManager.beginEdit();
				pos.getParentNode().removeChild(pos);
				documentManager.commitEdit(tr);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
			}
		}

	}

	// crea una riga prima o dopo quella in posizione pos
	public Node creaRiga(Node pos, boolean appendi) {

		if (pos.getParentNode() != null) {
			Node genitore = pos.getParentNode();
			int numCelle = pos.getChildNodes().getLength(); // ricavo il numero
			// di celle
			// per impostare le colonne
			// della nuova riga
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

	public void eliminaRiga(Node pos) {

		if (canDeleteRiga(pos)) {
			EditTransaction tr = null;
			try {
				tr = documentManager.beginEdit();
				Node genitore = pos.getParentNode();
				// Node htr = genitore.removeChild(pos);
				logger.debug("##### rimosso htr");

				if (!(genitore.hasChildNodes())) { // genitore: head, foot o
					// body
					logger.debug("##### head/foot/body hanno 1 solo figlio htr");
					Node nonno = genitore.getParentNode(); // nonno: table
					// elimino thead, hfoot o tbody se non hanno altri figli
					if (genitore.getNodeName().equals("h:thead") || genitore.getNodeName().equals("h:tfoot")) {
						nonno.removeChild(genitore); // si evita dtdRM
						logger.debug("##### rimuovo head/foot");
						// documentManager.commitEdit(tr);

					} else {
						try {
							if (dtdRulesManager.queryCanDelete(nonno, genitore)) {
								logger.debug("##### posso eliminare h/f/b");
								nonno.removeChild(genitore);
								// documentManager.commitEdit(tr);
								NodeList nd = nonno.getChildNodes(); // lista
								// figli
								// di
								// table: head,
								// foot, body
								if (nd.getLength() > 1) {
									boolean trovatoAltroBody = false;
									for (int i = 0; i < nd.getLength() && !trovatoAltroBody; i++) {
										if (nd.item(i).getNodeName().equals("h:body")) {
											trovatoAltroBody = true;
										}
									}

									// elimino la tabella se non ci sono pi?
									// body
									if (nonno.getNodeName().equals("h:table") && !(trovatoAltroBody)) {
										if (dtdRulesManager.queryCanDelete(nonno.getParentNode(), nonno))
											nonno.getParentNode().removeChild(nonno);
										// documentManager.commitEdit(tr);
									}
								}
							}
						} catch (DtdRulesManagerException ex) {
							//logger.error(ex.getMessage(), ex);
						}
					}

				}
				documentManager.commitEdit(tr);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
			}

		}
	}

	public void mergeRighe(Node pos1, Node pos2) {
		Node nodoriga1 = UtilDom.findParentByName(pos1, "h:tr");
		Node nodoriga2 = UtilDom.findParentByName(pos2, "h:tr");
		/*
		 * if (nodoriga1 != null && nodoriga2 != null){
		 * if(UtilDom.getCommonAncestor(nodoriga1,nodoriga2).getNodeName().equals("h:table")||
		 * UtilDom.getCommonAncestor(nodoriga1,nodoriga2).getNodeName().equals("h:thead") ||
		 * UtilDom.getCommonAncestor(nodoriga1,nodoriga2).getNodeName().equals("h:tbody")){
		 */

		if (canMergeRighe(pos1, pos2)) {
			EditTransaction tr = null;
			try {
				tr = documentManager.beginEdit();

				Vector figlinodoriga1 = getChildHtd(nodoriga1);
				Vector figlinodoriga2 = getChildHtd(nodoriga2);
				if (figlinodoriga1.size() == figlinodoriga2.size()) {
					for (int j = 0; j < figlinodoriga1.size(); j++) {
						Node A = (Node) figlinodoriga1.elementAt(j);
						Node B = (Node) figlinodoriga2.elementAt(j);
						String textA = UtilDom.getTextNode(A);
						String textB = UtilDom.getTextNode(B);
						String textAB = textA + textB;
						UtilDom.setTextNode(A, textAB);

					}

				}

				documentManager.commitEdit(tr);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
			}
		}

		// }
		// }
	}

	// inserisce una colonna prima o dopo pos, dove pos ? una cella
	// della colonna di riferimento
	public Node[] creaColonna(Node pos, boolean appendi) {

		Node genitore = pos.getParentNode();
		int indice = UtilDom.getChildIndex(genitore, pos); // indice della
		// colonna
		Node nonno = genitore.getParentNode();
		Node bisnonno = nonno.getParentNode();

		// NodeList fg = nonno.getChildNodes();//lista delle righe
		Vector headbodyfoot = UtilDom.getChildElements(bisnonno);
		Vector colonna = new Vector();

		// if (canDeleteColonna(pos)) {
		EditTransaction tr = null;
		try {
			tr = documentManager.beginEdit();

			for (int j = 0; j < headbodyfoot.size(); j++) {
				Node htr = (Node) headbodyfoot.elementAt(j);
				if (!(htr.getNodeName().equals("h:caption"))) {
					NodeList fg = htr.getChildNodes();// lista delle righe
					for (int i = 0; i < fg.getLength(); i++) {
						NodeList np = fg.item(i).getChildNodes(); // lista
						// colonne
						Node nuovaCella = pos.getOwnerDocument().createElementNS(UtilDom.getNameSpaceURIforElement(pos, "h"), "h:td");
						// Node nodoTesto =
						// pos.getOwnerDocument().createTextNode("");
						// nuovaCella.appendChild(nodoTesto);

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
	public void eliminaColonna(Node pos) {

		Node genitore = pos.getParentNode();
		int indice = UtilDom.getChildIndex(genitore, pos);
		Node nonno = genitore.getParentNode();
		Node bisnonno = nonno.getParentNode();
		// NodeList fg = nonno.getChildNodes(); //lista delle righe
		Vector headbodyfoot = UtilDom.getChildElements(bisnonno);
		boolean figli = true;

		if (canDeleteColonna(pos)) {
			EditTransaction tr = null;
			try {
				tr = documentManager.beginEdit();

				for (int j = 0; j < headbodyfoot.size(); j++) { // scarta primo
					// nodo
					// caption
					Node htr = (Node) headbodyfoot.elementAt(j);
					if (!(htr.getNodeName().equals("h:caption"))) {
						NodeList fg = htr.getChildNodes();// lista delle righe
						for (int i = 0; i < fg.getLength(); i++) {
							NodeList np = fg.item(i).getChildNodes();// lista
							// colonne
							Node daeliminare = np.item(indice);
							fg.item(i).removeChild(daeliminare);
							logger.debug("##### elimina colonna");
							figli = figli | fg.item(i).hasChildNodes();

						}
						if (!figli) {
							// se le righe della tabella non hanno pi? colonne
							// elimino la
							// tabella
							if (nonno.getNodeName().equals("h:tbody")) {
								nonno.getParentNode().getParentNode().removeChild(nonno.getParentNode());
								logger.debug("##### elimina colonna: elimino tutta la tab");

							} else if (nonno.getNodeName().equals("h:thead") | nonno.getNodeName().equals("h:foot")) {
								nonno.getParentNode().removeChild(nonno);
								logger.debug("##### elimina colonna");
							}

						}
					}
				}

				documentManager.commitEdit(tr);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
			}

		}
	}

	// riceve in ingresso la lista di celle appartenenti alla colonna da
	// eliminare
	// private void eliminaColonna(NodeList listaNodi) {
	//
	// Node primoNodo = listaNodi.item(0);
	// Node genitore = primoNodo.getParentNode();
	// Node nonno = genitore.getParentNode();
	// NodeList fg = nonno.getChildNodes();
	//
	// for (int i = 0; i < fg.getLength(); i++) {
	// fg.item(i).removeChild(listaNodi.item(i));
	// }
	//
	// }

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

		Node genitore = pos.getParentNode();
		int indice = UtilDom.getChildIndex(genitore, pos);
		Node nonno = genitore.getParentNode();
		Node bisnonno = nonno.getParentNode();
		Vector headbodyfoot = UtilDom.getChildElements(bisnonno);
		// NodeList righe = nonno.getChildNodes(); //lista delle righe

		if (canDeleteTable(pos)) {
			EditTransaction tr = null;
			try {
				tr = documentManager.beginEdit();
				for (int j = 1; j < headbodyfoot.size(); j++) {
					Node htr = (Node) headbodyfoot.elementAt(j);
					NodeList righe = htr.getChildNodes();// lista delle righe

					for (int i = 0; i < righe.getLength(); i++) {
						NodeList colonne = righe.item(i).getChildNodes();// per
						// tutte
						// le righe
						// prendi la
						// lista dei
						// figli
						Node nodoIndice = colonne.item(indice); // fra tutti i
						// figli
						// prendi quelli con
						// indice=col da allineare
						UtilDom.setAttributeValue(nodoIndice, "align", allinea);
					}

				}
				documentManager.commitEdit(tr);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
			}
		}

		/*
		 * ALLINEAMENTO ORIZZ if (allinea.equals("left")){
		 * UtilDom.setAttributeValue(nodoIndice,"align","left"); } if
		 * (allinea.equals("right")) {
		 * UtilDom.setAttributeValue(nodoIndice,"align","right"); }
		 * if(allinea.equals("center")) {
		 * UtilDom.setAttributeValue(nodoIndice,"align","center"); }
		 * if(allinea.equals("justify")) {
		 * UtilDom.setAttributeValue(nodoIndice,"align","justify"); }
		 * ALLINEAMENTO VERT if(allinea.equals("top")) {
		 * UtilDom.setAttributeValue(nodoIndice,"valign","top"); }
		 * if(allinea.equals("middle")) {
		 * UtilDom.setAttributeValue(nodoIndice,"valign","middle"); }
		 * if(allinea.equals("bottom")) {
		 * UtilDom.setAttributeValue(nodoIndice,"valign","bottom"); }
		 */

	}

	// a partire dal nodo corrente, costruisce le righe e le colonne della
	// tabella
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
			// Node nodoTesto = riga.getOwnerDocument().createTextNode("");
			// newcell.appendChild(nodoTesto);
			riga.appendChild(newcell);
		}
		return riga;
	}

}
