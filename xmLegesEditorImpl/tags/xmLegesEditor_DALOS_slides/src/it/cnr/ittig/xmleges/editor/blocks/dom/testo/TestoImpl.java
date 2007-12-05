package it.cnr.ittig.xmleges.editor.blocks.dom.testo;

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
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.testo.Testo;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.testo.Testo</code>.</h1>
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
 * Tecniche dell'informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */

public class TestoImpl implements Testo, Loggable, Serviceable {

	Logger logger;

	Node testoNode;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	UtilRulesManager utilRulesManager;

	NirUtilDom nirUtilDom;

	int contaNodiTesto;

	int contaNodiTestoConAncestor;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
	}

	// //////////////////////// AZIONE SUL TESTO ///////////////////////////

	public boolean canTestoAction(Node node, String action) {
		boolean ritorno = false;
		int onOff = 0;

		onOff = actionOnOff(node, action);
		if (onOff == 1)
			ritorno = canTestoActionOn(node, action);
		if (onOff == 2)
			ritorno = canTestoActionOff(node, action);
		return ritorno;
	}

	private int actionOnOff(Node node, String action) {
		int valore = 0;
		Node found = UtilDom.findParentByName(node, action);
		if (found == null)
			valore = 1;
		else
			valore = 2;
		return valore;
	}

	private boolean canTestoActionOn(Node node, String action) {

		if (node == null)
			return false;
		try {
			if (node.getParentNode() != null)
				return (dtdRulesManager.queryAppendable(node.getParentNode()).contains(action)
						|| dtdRulesManager.queryInsertableInside(node.getParentNode(), node).contains(action)
						|| dtdRulesManager.queryInsertableAfter(node.getParentNode(), node).contains(action) || dtdRulesManager.queryInsertableBefore(
						node.getParentNode(), node).contains(action));
			return false;
		} catch (DtdRulesManagerException ex) {
			return false;
		}
	}

	private boolean canTestoActionOff(Node node, String action) {
		boolean returnValue = false;
		try {
			Node found = UtilDom.findParentByName(node, action);

			if (found == node.getParentNode()) { // il parent del node e' un
													// h:b
				logger.debug("can rimuovi tag" + action);
				returnValue = canRimuoviTag(node, action);
			} else { // il nodo h: che voglio rimuovere sta piu' su
				logger.debug("can scandisci list" + action);

				NodeList childList = found.getChildNodes();
				int len = childList.getLength();

				// verifica se e' possibile rimuovere il nodo found dall'albero
				// ed agganciare i suoi figli al padre
				for (int i = 0; i < len; i++) {
					if (!dtdRulesManager.queryCanInsertBefore(found.getParentNode(), found, childList.item(i)))
						return false;
				}
				if (!dtdRulesManager.queryCanDelete(found.getParentNode(), found))
					return false;

				// scorre il sottoalbero; il primo figlio per cui
				// canChangeTagInSubTree e' false, restituisce false
				int flag[] = new int[childList.getLength() + 1];
				for (int i = 0; i < len; i++)
					flag[i] = 0;

				for (int i = 0; i < len; i++) {
					flag[i] = canChangeTagInSubTree(childList.item(i), node, action);
					logger.debug("flag i" + flag[i]);
					if (flag[i] == 1)
						return false;
				}
				return true;
			}
		} catch (DtdRulesManagerException ex) {
			return false;
		}
		return returnValue;
	}

	private int canChangeTagInSubTree(Node child, Node node, String action) {

		Document documento = documento = documentManager.getDocumentAsDom();
		int returnValue = 0;

		try {
			NodeList childList;
			int i;
			int localFlag = 0;

			if (UtilDom.isTextNode(child)) {
				if (child == node)
					returnValue = canAddTag(node, action);
				else {
					Node p = child.getParentNode();
					if (!dtdRulesManager.queryAppendable(child).contains(action))
						localFlag = 1;
					if (!dtdRulesManager.queryInsertableBefore(p, child.getNextSibling()).contains(action))
						localFlag = 1;
				}
			}
			// FIXME in questo if sembra che faccia la stessa cosa di sopra;
			// verificare
			if (nodeOnlyTag(child, action)) {
				Node p = child.getParentNode();
				if (!dtdRulesManager.queryAppendable(child).contains(action))
					localFlag = 1;
				if (!dtdRulesManager.queryInsertableBefore(p, child.getNextSibling()).contains(action))
					localFlag = 1;
			}

			childList = child.getChildNodes();
			if (childList != null) {
				int len = childList.getLength();
				int flag[] = new int[len + 1];
				Node vector[] = new Node[len];
				flag[len] = returnValue;
				for (i = 0; i < len; i++) {
					vector[i] = childList.item(i);
					flag[i] = 0;
				}
				for (i = 0; i < len; i++)
					flag[i] = canChangeTagInSubTree(vector[i], node, action);
				for (i = 0; i < len + 1; i++)
					if (flag[i] == 1)
						returnValue = 1;
			}
		} // end of try
		catch (DtdRulesManagerException ex) {
		}
		return returnValue;
	}

	public Node doActionOn(Node node, int start, int end, String action) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			Node modificato = utilRulesManager.encloseTextInTag(node, start, end, action, "h");
			documentManager.commitEdit(tr);
			return modificato;
		} catch (DocumentManagerException ex) {
			return node;
		}
	}

	public Node doActionOff(Node node, int start, int end, String action) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			Node found = UtilDom.findParentByName(node, action);
			Node pfound = found.getParentNode();
			Node p = node.getParentNode();
			Node modificato = null;

			logger.debug("found: " + UtilDom.getPathName(found) + " pfound: " + UtilDom.getPathName(pfound) + " p: " + UtilDom.getPathName(p) + " node: "
					+ UtilDom.getPathName(node) + node);

			if (found == p) { // il nodo action da rimuovere e' il padre di
								// node
				logger.debug("rimuovi tag");
				modificato = rimuoviTag(node, start, end, action);
			} else { // il nodo action da rimuovere e' piu' su nell'albero
				// RIMUOVO IL NODO FOUND DALL'ALBERO E VI AGGANCIO I SUOI FIGLI
				NodeList childList;
				int i;
				int len;
				childList = found.getChildNodes();
				len = childList.getLength();
				Node vector[] = new Node[len];

				for (i = 0; i < len; i++)
					vector[i] = childList.item(i);

				for (i = 0; i < len; i++) {
					Node child = vector[i];
					pfound.insertBefore(child, found);
				}
				pfound.removeChild(found);

				for (i = 0; i < len; i++)
					changeTagInSubTree(vector[i], node, start, end, action);
				
				modificato = pfound;
			}
			documentManager.commitEdit(tr);
			return modificato;
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	// //////////NEW PART: AZIONE SUL ONLY TAG ///////////////////////////

	public boolean canTestoActionRemoveTag(Node node, String azione) {
		try {
			Node p = node.getParentNode();
			if ((node.getNodeName() == azione) && (dtdRulesManager.queryCanDelete(p, node)))
				return true;
		} catch (DtdRulesManagerException ex) {
			return false;
		}
		return false;
	}

	public Node doActionOffOnlyTag(Node node) {
		try {
			EditTransaction tr = documentManager.beginEdit();

			Node p = node.getParentNode();
			p.removeChild(node);

			documentManager.commitEdit(tr);
			return p;
		} catch (DocumentManagerException ex) {
			return null;
		}
	}

	private void changeTagInSubTree(Node child, Node node, int start, int end, String action) {
		Document documento = documento = documentManager.getDocumentAsDom();
		NodeList childList;
		int i;

		Node newTag = documento.createElementNS(UtilDom.getNameSpaceURIforElement(node, "h"), action);
		Node p = child.getParentNode();
		Node brother = child.getNextSibling();

		if (UtilDom.isTextNode(child)) {
			if (child == node)
				addTag(node, start, end, action);
			else {
				if (!child.getNodeValue().trim().equals(""))
					newTag.appendChild(child);
				p.insertBefore(newTag, brother);
			}
		}

		// PARTE AGGIUNTA DA TESTARE
		if (nodeOnlyTag(child, action)) {
			newTag.appendChild(child);
			p.insertBefore(newTag, brother);
		}
		// FINE DI PARTE AGGIUNTA

		childList = child.getChildNodes();
		if (childList != null) {
			int len = childList.getLength();
			Node vector[] = new Node[len];
			for (i = 0; i < len; i++)
				vector[i] = childList.item(i);
			for (i = 0; i < len; i++)
				changeTagInSubTree(vector[i], node, start, end, action);
		}
	}

	private Node rimuoviTag(Node node, int start, int end, String action) {
		Document documento = documento = documentManager.getDocumentAsDom();

		String value = UtilDom.getText(node).trim();
		String testo1 = value.substring(0, start);
		String testob = value.substring(start, end);
		String testo2 = value.substring(end, value.length());

		Node p = node.getParentNode();
		Node ancestor = p.getParentNode();
		Node modificato=null;

		if (testo1.equals("") && testo2.equals("")) {
			if (ancestor != null) {
				ancestor.insertBefore(node, p);
				ancestor.removeChild(p);
				modificato=ancestor;
			}
		} else {
			logger.debug(" TESTO IMPL : PARTE DI STRINGA ");
			Node newActualTag1 = documento.createElementNS(UtilDom.getNameSpaceURIforElement(node, "h"), action);
			Node newActualTag2 = documento.createElementNS(UtilDom.getNameSpaceURIforElement(node, "h"), action);
			if (ancestor != null) {
				if (!testo1.equals("") && !testo2.equals("")) {
					node.setNodeValue(testob);
					newActualTag1.appendChild(documento.createTextNode(testo1));
					newActualTag2.appendChild(documento.createTextNode(testo2));
					ancestor.insertBefore(newActualTag2, p);
					ancestor.insertBefore(node, newActualTag2);
					ancestor.insertBefore(newActualTag1, node);
					ancestor.removeChild(p);
				}
				if (testo1.equals("")) {
					node.setNodeValue(testob);
					newActualTag2.appendChild(documento.createTextNode(testo2));
					ancestor.insertBefore(newActualTag2, p);
					ancestor.insertBefore(node, newActualTag2);
					ancestor.removeChild(p);
				}
				if (testo2.equals("")) {
					node.setNodeValue(testob);
					newActualTag1.appendChild(documento.createTextNode(testo1));
					ancestor.insertBefore(node, p);
					ancestor.insertBefore(newActualTag1, node);
					ancestor.removeChild(p);
				}
				modificato=ancestor;
			}
		}
		UtilDom.mergeTextNodes(ancestor);
		return modificato;
	}

	private void addTag(Node node, int start, int end, String action) {
		Document documento = documentManager.getDocumentAsDom();

		String value = UtilDom.getText(node).trim();
		String testo1 = value.substring(0, start);
		String testob = value.substring(start, end);
		String testo2 = value.substring(end, value.length());
		Node p = node.getParentNode();

		Node newTag1 = documento.createElementNS(UtilDom.getNameSpaceURIforElement(node, "h"), action);
		Node newTag2 = documento.createElementNS(UtilDom.getNameSpaceURIforElement(node, "h"), action);

		if (!testo1.equals("") || !testo2.equals("")) {
			node.setNodeValue(testob);
			if (p != null) {
				if (!testo1.equals("") && !testo2.equals("")) {
					newTag1.appendChild(documento.createTextNode(testo1));
					newTag2.appendChild(documento.createTextNode(testo2));
					p.insertBefore(newTag1, node);
					p.appendChild(newTag2);
				} else if (testo1.equals("")) {
					newTag2.appendChild(documento.createTextNode(testo2));
					p.appendChild(newTag2);
				} else {
					newTag1.appendChild(documento.createTextNode(testo1));
					p.insertBefore(newTag1, node);
				}
			}
		}
	}

	private int canAddTag(Node node, String action) {
		try {
			Node p = node.getParentNode();

			if (p != null)
				if (dtdRulesManager.queryInsertableBefore(p, node).contains(action))
					return 0;
		} catch (DtdRulesManagerException ex) {
			return 1;
		}
		return 1;
	}

	private boolean canRimuoviTag(Node node, String action) {

		try {
			logger.debug("sono dentro can rimuovi tag");

			Node p = node.getParentNode();
			Node ancestor = p.getParentNode();

			if (dtdRulesManager.queryCanInsertBefore(ancestor, p, node) && dtdRulesManager.queryCanDelete(ancestor, p))
				return true;
		} catch (DtdRulesManagerException ex) {
			return false;
		}
		return false;
	}

	// //////////NEW PART: AZIONE SUL TREE////////////////////////////

	private int tagTreeOnOff(Node node, String azione) {
		contaNodiTesto = 0;
		contaNodiTestoConAncestor = 0;
		scorriTree(node, azione);
		return contaNodiTesto - contaNodiTestoConAncestor;
	}

	private void scorriTree(Node node, String azione) {

		if (node == null)
			return;

		if (UtilDom.isTextNode(node)) {
			contaNodiTesto++;
			Node found = UtilDom.findParentByName(node, azione);
			if (found != null)
				contaNodiTestoConAncestor++;
		}

		NodeList childList = node.getChildNodes();
		int len = childList.getLength();
		int i;
		Node vector[] = new Node[len];
		for (i = 0; i < len; i++) {
			vector[i] = childList.item(i);
		}

		if (childList != null) {
			for (i = 0; i < len; i++) {
				scorriTree(vector[i], azione);
			}
		}
		return;
	}

	public boolean canDoTagTree(Node node, String azione) {
		boolean returnValue = true;

		// restituisce il numero di nodi testo - numero di nodi testo con
		// ancestor di tipo azione
		// se > 0 ci sono nodi testo liberi (ON) altrimenti sono tutti occupati
		// (OFF)
		int onOff = tagTreeOnOff(node, azione);

		if (contaNodiTesto == 0)
			return false;

		if (onOff > 0) { // ON
			if (!canMettiTagInTree(node, azione))
				returnValue = false;

			if (!canMergeTagInTree(node, azione))
				returnValue = false;
		} else { // OFF
			Node found = UtilDom.findParentByName(node, azione);
			if ((found != null) && (found != node))// trova un ancestor azione
													// prima pero' del nodo
													// selezionato
				returnValue = false;
			else {
				if (!canRimuoviTagInTree(node, azione))
					returnValue = false;
			}
		}

		return returnValue;
	}

	private boolean canMettiTagInTree(Node node, String azione) {
		Document documento = documento = documentManager.getDocumentAsDom();
		boolean returnValue = true;

		try {
			if (node == null)
				returnValue = true;
			else {
				// Nel caso in cui sia un nodo di testo metto bold solo se
				// il nodo corrente non ha gia' un antenanto di tipo bold.
				if (UtilDom.isTextNode(node) || nodeOnlyTag(node, azione)) {
					Node found = UtilDom.findParentByName(node, azione);
					if (found == null) {
						Node newTag = documento.createElementNS(UtilDom.getNameSpaceURIforElement(node, "h"), azione);
						Node p = node.getParentNode();
						Node brother = node.getNextSibling();
						if (brother != null) {
							if (!(dtdRulesManager.queryCanAppend(newTag, node) && dtdRulesManager.queryCanInsertBefore(p, brother, newTag)))
								returnValue = false;
						} else {
							if (!(dtdRulesManager.queryCanAppend(newTag, node) && dtdRulesManager.queryCanAppend(p, newTag)))
								returnValue = false;
						}
					}
				}
			}

			NodeList childList = node.getChildNodes();
			int len = childList.getLength();
			int i;
			Node vector[] = new Node[len];
			for (i = 0; i < len; i++) {
				vector[i] = childList.item(i);
			}

			if (childList != null) {
				for (i = 0; i < len; i++) {
					if (!canMettiTagInTree(vector[i], azione))
						returnValue = false;
				}
			}
		} catch (DtdRulesManagerException ex) {
			returnValue = false;
		}
		return returnValue;
	}

	private boolean canMergeTagInTree(Node node, String azione) {

		boolean returnValue = true;
		if (node == null)
			return true;
		if (!canMergeTagNodes(node, azione))
			returnValue = false;

		NodeList childList = node.getChildNodes();
		int len = childList.getLength();
		if (len > 0) {
			for (int i = 0; i < len; i++) {
				if (canMergeTagInTree(childList.item(i), azione) == false)
					returnValue = false;
			}
		}
		return returnValue;
	}

	private boolean canRimuoviTagInTree(Node node, String azione) {

		boolean returnValue = true;
		try {
			if (node == null)
				returnValue = true;
			if (node.getNodeName() == azione) {
				Node p = node.getParentNode();
				int j;
				int len1;
				NodeList childList1 = node.getChildNodes();
				len1 = childList1.getLength();
				Node vector1[] = new Node[len1];

				for (j = 0; j < len1; j++)
					vector1[j] = childList1.item(j);

				for (j = 0; j < len1; j++) {
					Node child1 = vector1[j];
					if (!dtdRulesManager.queryCanInsertBefore(p, node, child1))
						returnValue = false;
				}
				if (!dtdRulesManager.queryCanDelete(p, node))
					returnValue = false;
				return returnValue;
				// appena trova un nodo azione da rimuovere, deve smettere con
				// la ricorsione del suo sottoalbero
			} else {
				NodeList childList = node.getChildNodes();
				int len = childList.getLength();
				int i;
				Node vector[] = new Node[len];
				for (i = 0; i < len; i++) {
					vector[i] = childList.item(i);
				}
				if (childList != null) {
					for (i = 0; i < len; i++) {
						if (!canRimuoviTagInTree(vector[i], azione))
							returnValue = false;
					}
				}
			}// end of else
		} // end of try
		catch (DtdRulesManagerException ex) {
			// logger.error(ex.getMessage(),ex);
		}
		return returnValue;
	}

	public void doTagTree(Node node, String azione) {

		int onOff = tagTreeOnOff(node, azione);

		if (contaNodiTesto == 0)
			return;
		
		if (onOff > 0) {
			try {
				EditTransaction tr = documentManager.beginEdit();
				mettiTagInTree(node, azione);
				logger.debug("chiamo mergeBoldInTree di " + node);
				mergeTagInTree(node, azione);
				documentManager.commitEdit(tr);
			} catch (DocumentManagerException ex) {
				// logger.error(ex.getMessage(),ex);
			}
		} else {
			try {
				EditTransaction tr = documentManager.beginEdit();
				rimuoviTagInTree(node, azione);
				documentManager.commitEdit(tr);
			} catch (DocumentManagerException ex) {
				// logger.error(ex.getMessage(),ex);
			}
		}
	}

	private boolean nodeOnlyTag(Node node, String azione) {
		// CONTROLLA SE IL NODO ATTUALE E' UN TAG VUOTO
		if (((node.getNodeName() == "h:b") || (node.getNodeName() == "h:i") || (node.getNodeName() == "h:u") || (node.getNodeName() == "h:sup") || (node
				.getNodeName() == "h:sub"))
				&& (node.getFirstChild() == null) && (node.getNodeName() != azione))
			return true;
		return false;
	}

	private void mettiTagInTree(Node node, String azione) {

		Document documento = documento = documentManager.getDocumentAsDom();
		logger.debug("mettiBoldInTree");

		if (node == null)
			return;
		else {
			// Nel caso in cui sia un nodo di testo metto bold solo se
			// il nodo corrente non ha gia' un antenanto di tipo bold.
			if (UtilDom.isTextNode(node)) {
				Node found = UtilDom.findParentByName(node, azione);
				if (found == null) {
					Node newTag = documento.createElementNS(UtilDom.getNameSpaceURIforElement(node, "h"), azione);
					Node p = node.getParentNode();
					Node brother = node.getNextSibling();
					if (node.getNodeValue().trim() != "")
						newTag.appendChild(node);
					p.insertBefore(newTag, brother);
				}
			}
			if (nodeOnlyTag(node, azione)) {
				Node found = UtilDom.findParentByName(node, azione);
				if (found == null) {
					Node newTag = documento.createElementNS(UtilDom.getNameSpaceURIforElement(node, "h"), azione);
					Node p = node.getParentNode();
					Node brother = node.getNextSibling();

					newTag.appendChild(node);
					p.insertBefore(newTag, brother);
				}
			}

			logger.debug("metti in bold node path" + UtilDom.getPathName(node));

			NodeList childList = node.getChildNodes();
			int len = childList.getLength();
			int i;
			Node vector[] = new Node[len];
			for (i = 0; i < len; i++) {
				vector[i] = childList.item(i);
				logger.debug("metti in bold child" + vector[i]);
			}

			if (childList != null) {
				for (i = 0; i < len; i++) {
					mettiTagInTree(vector[i], azione);
				}
			}
		}
		return;
	}

	private void rimuoviTagInTree(Node node, String azione) {

		if (node == null)
			return;

		logger.debug("node name" + node.getNodeName());
		// BLOCCO DI GESTIONE DEL NODO
		if (node.getNodeName() == azione) {
			Node p = node.getParentNode();
			int j;
			int len1;
			NodeList childList1 = node.getChildNodes();
			len1 = childList1.getLength();
			Node vector1[] = new Node[len1];

			logger.debug("nodo azione trovato da rimuovere" + UtilDom.getPathName(node));

			for (j = 0; j < len1; j++)
				vector1[j] = childList1.item(j);

			for (j = 0; j < len1; j++) {
				Node child1 = vector1[j];
				p.insertBefore(child1, node);
			}

			p.removeChild(node);
			UtilDom.mergeTextNodes(p);

			// appena trova un nodo azione da rimuovere, deve smettere con la
			// ricorsione del suo sottoalbero
		}// //FINE BLOCCO DI GESTIONE DEL NODO
		// blocco di ricorsione dove vado ancora a cercare
		// un tag da rimuovere
		else {
			NodeList childList = node.getChildNodes();
			int len = childList.getLength();
			int i;
			Node vector[] = new Node[len];
			for (i = 0; i < len; i++) {
				vector[i] = childList.item(i);
			}
			if (childList != null) {
				for (i = 0; i < len; i++) {
					rimuoviTagInTree(vector[i], azione);
				}
			}
		}// end of else
		return;
	}

	private void mergeTagInTree(Node node, String azione) {
		if (node == null)
			return;

		mergeTagNodes(node, azione);
		NodeList childList = node.getChildNodes();
		int len = childList.getLength();
		int i;

		if (len > 0) {
			for (i = 0; i < len; i++) {
				logger.debug("chiamo mergeBoldInTree di " + childList.item(i));
				mergeTagInTree(childList.item(i), azione);
			}
		}
		return;
	}

	private void mergeTagNodes(Node node, String azione) {

		int flagValidCondition = 0;
		int primaVolta = 0;
		NodeList nl = node.getChildNodes();
		Node p = nl.item(0);
		String text = "";
		Document documento = documento = documentManager.getDocumentAsDom();

		logger.debug("sono dentro mergeTagNodes" + node);

		for (int i = 1; i < nl.getLength(); i++) {
			logger.debug("nodo i" + nl.item(i) + " nodo p " + p);

			if ((((nl.item(i).getNodeName() == azione) && (nl.item(i).getChildNodes().getLength() == 0)) || ((nl.item(i).getNodeName() == azione)
					&& (nl.item(i).getChildNodes().getLength() == 1) && (UtilDom.isTextNode(nl.item(i).getFirstChild()))))
					&& (((p.getNodeName() == azione) && (p.getChildNodes().getLength() == 0)) || ((p.getNodeName() == azione)
							&& (p.getChildNodes().getLength() == 1) && (UtilDom.isTextNode(p.getFirstChild()))))) {
				logger.debug("ho trovato valida una condizione");
				flagValidCondition = 1;

				Node child2 = null;

				if ((p.getChildNodes().getLength() == 1) && (primaVolta == 0)) {
					text = p.getFirstChild().getNodeValue(); // .trim();
					primaVolta = 1;
				}
				if (nl.item(i).getChildNodes().getLength() == 1)
					child2 = nl.item(i).getFirstChild();
				if (child2 != null)
					text = text + child2.getNodeValue(); // .trim();

				node.removeChild(nl.item(i));
				i--;
			} else {
				if (flagValidCondition == 1) {
					if (p.getChildNodes().getLength() == 1)
						p.getFirstChild().setNodeValue(text);
					else {
						Node child = null;
						child = documento.createTextNode(text);
						p.appendChild(child);
					}
					primaVolta = 0;
					flagValidCondition = 0;
					text = "";
				}
				p = nl.item(i);
			}
		} // fine del for

		if (flagValidCondition == 1) {
			if (p.getChildNodes().getLength() == 1)
				p.getFirstChild().setNodeValue(text);
			else {
				Node child = null;
				child = documento.createTextNode(text);
				p.appendChild(child);
			}
		}
	}

	private boolean canMergeTagNodes(Node node, String azione) {
		Document documento = documento = documentManager.getDocumentAsDom();
		boolean returnValue = true;

		try {
			int flagValidCondition = 0;
			NodeList nl = node.getChildNodes();
			Node p = nl.item(0);

			for (int i = 1; i < nl.getLength(); i++) {
				if ((((nl.item(i).getNodeName() == azione) && (nl.item(i).getChildNodes().getLength() == 0)) || ((nl.item(i).getNodeName() == azione)
						&& (nl.item(i).getChildNodes().getLength() == 1) && (UtilDom.isTextNode(nl.item(i).getFirstChild()))))

						&& (((p.getNodeName() == azione) && (p.getChildNodes().getLength() == 0)) || ((p.getNodeName() == azione)
								&& (p.getChildNodes().getLength() == 1) && (UtilDom.isTextNode(p.getFirstChild()))))) {

					flagValidCondition = 1;

					if (!dtdRulesManager.queryCanDelete(node, nl.item(i)))
						returnValue = false;

				} else {
					if (flagValidCondition == 1) {
						if (p.getChildNodes().getLength() == 0) {
							Node child = documento.createTextNode("");
							if (!dtdRulesManager.queryCanAppend(p, child))
								returnValue = false;
						}
						flagValidCondition = 0;
					}
					p = nl.item(i);
				} // fine di else
			}// fine di for

			if (flagValidCondition == 1) {
				if (p.getChildNodes().getLength() == 0) {
					Node child = documento.createTextNode("");
					if (!dtdRulesManager.queryCanAppend(p, child))
						returnValue = false;
				}
			}
		}

		catch (DtdRulesManagerException ex) {
			returnValue = false;
			// logger.error(ex.getMessage(),ex);
		}

		return returnValue;
	}

}
