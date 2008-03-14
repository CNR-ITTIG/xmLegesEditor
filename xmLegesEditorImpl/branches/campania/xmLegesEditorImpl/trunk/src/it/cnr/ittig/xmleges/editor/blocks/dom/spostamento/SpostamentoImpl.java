package it.cnr.ittig.xmleges.editor.blocks.dom.spostamento;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.dom.spostamento.Spostamento;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.spostamento.Spostamento</code>.
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
 * 
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class SpostamentoImpl implements Spostamento, Loggable, Serviceable {

	Logger logger;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	NirUtilDom nirUtilDom;

	Rinumerazione rinumerazione;

	UtilMsg utilMsg;

	SelectionManager selectionManager;

	Node modified = null;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
	}

	// //////////////////////////////////////////////////// Spostamento
	// Interface

	public Node canMoveUp(Node node) {
		try {
			if (dtdRulesManager.queryCanDelete(node.getParentNode(), node))
				return (queryCanMoveUp(getPathIndex(node), nirUtilDom.getNIRElement(node.getOwnerDocument()).getFirstChild(), node, null));
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	public Node canMoveDown(Node node) {
		try {
			if (dtdRulesManager.queryCanDelete(node.getParentNode(), node))
				return (queryCanMoveDown(getPathIndex(node), nirUtilDom.getNIRElement(node.getOwnerDocument()).getFirstChild(), node, null));
			return null;
		} catch (Exception ex) {
			return null;
		}
	}

	public Node moveUp(Node target, Node newNode) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (dtdRulesManager.queryCanInsertBefore(target.getParentNode(), target, newNode)) {
				target.getParentNode().insertBefore(newNode, target);
				rinumerazione.aggiorna(target.getOwnerDocument());
				documentManager.commitEdit(tr);
				return newNode;
			} else {
				documentManager.rollbackEdit(tr);
				utilMsg.msgError("editor.dom.spostamento.errore");
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public Node moveDown(Node target, Node newNode) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (dtdRulesManager.queryCanInsertAfter(target.getParentNode(), target, newNode)) {
				target.getParentNode().insertBefore(newNode, target.getNextSibling());
				rinumerazione.aggiorna(target.getOwnerDocument());
				documentManager.commitEdit(tr);
				return newNode;
			} else {
				documentManager.rollbackEdit(tr);
				utilMsg.msgError("editor.dom.spostamento.errore");
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public Node upGrade(Node node) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (upGradeNode(node)) {
				rinumerazione.aggiorna(node.getOwnerDocument());
				documentManager.commitEdit(tr);
				return modified;
			} else {
				documentManager.rollbackEdit(tr);
				utilMsg.msgError("editor.dom.spostamento.errore");
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public Node downGrade(Node node) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (downGradeNode(node)) {
				rinumerazione.aggiorna(node.getOwnerDocument());
				documentManager.commitEdit(tr);
				return modified;
			} else {
				documentManager.rollbackEdit(tr);
				utilMsg.msgError("editor.dom.spostamento.errore");
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

	private Node queryCanMoveUp(int[] idx, Node start, Node moved, Node result) {
		int index, i;
		NodeList children = start != null ? start.getChildNodes() : null;
		Node res;

		if (idx != null) {
			index = idx[0];
			idx = pruneIndex(idx);
		} else
			index = children != null ? children.getLength() - 1 : -1;

		if (start != null && start != moved) {
			res = queryCanMoveUp(idx, children.item(index), moved, result);
			i = index - 1;
			while (res == null && i >= 0) {
				if (!start.getNodeName().equals(moved.getNodeName())) // dovrebbe
					// esssere
					// if(!start.getNodeName()
					// <(NIR)
					// moved.getNodeName()
					// ; va
					// in
					// profondit?
					// solo
					// se ci
					// sono
					// partizioni
					// dello
					// stesso
					// livello
					// di
					// quella
					// da
					// inserire
					res = queryCanMoveUp(null, children.item(i), moved, res); // children.item(i).getLastChild()
				i--;
			}
			if (res == null && isInsertableBefore(start.getParentNode(), start, moved)) { // sovrascrive
				// solo
				// se
				// non
				// ha
				// ancora
				// trovato
				res = start;
				logger.debug("--------->  lo mette qui:  " + UtilDom.getPathName(start) + "  " + start.getNodeValue());
			}
			logger.debug("queryCANMOVEUP: ho visitato il nodo: " + UtilDom.getPathName(start) + "  " + start.getNodeValue());
			return res;
		}
		return (null);
	}

	private Node queryCanMoveDown(int[] idx, Node start, Node moved, Node result) {
		int index, i;
		NodeList children = start != null ? start.getChildNodes() : null;
		Node res;

		if (idx != null) {
			index = idx[0];
			idx = pruneIndex(idx);
		} else
			index = 0;

		int length = children != null ? children.getLength() - 1 : -1;

		if (start != null && start != moved) {
			res = queryCanMoveDown(idx, children.item(index), moved, result);
			i = index + 1;
			while (res == null && i <= length) {
				if (!start.getNodeName().equals(moved.getNodeName()))
					res = queryCanMoveDown(null, children.item(i), moved, res); // children.item(i).getLastChild()
				i++;
			}
			if (res == null && isInsertableAfter(start.getParentNode(), start, moved)) { // sovrascrive
				// solo
				// se
				// non
				// ha
				// ancora
				// trovato
				res = start;
				logger.debug("--------->  queryCanMoveDown: lo mette qui:  " + UtilDom.getPathName(start) + "  " + start.getNodeValue());
			}
			logger.debug("queryCANMOVEDOWN: ho visitato il nodo: " + UtilDom.getPathName(start) + "  " + start.getNodeValue());
			return res;
		}
		return (null);
	}

	public boolean canUpgrade(Node node) {
		if (UtilDom.getPathName(node).endsWith("el") || UtilDom.getPathName(node).endsWith("en") || UtilDom.getPathName(node).endsWith("ep"))
			return true;
		return false;
	}

	public boolean canDowngrade(Node node) {
		Node brother = node.getPreviousSibling();
		if (brother != null) {
			if ((UtilDom.getPathName(node).endsWith("comma") || UtilDom.getPathName(node).endsWith("el") || UtilDom.getPathName(node).endsWith("en") || UtilDom.getPathName(node).endsWith("ep"))
					&& node.getNodeName().equals(brother.getNodeName()))
				return true;
		}
		return false;
	}

	private boolean upGradeNode(Node node) {
		Node newElement;
		Document doc = node.getOwnerDocument();
		Node parent = node.getParentNode();
		logger.debug(node.getNodeName() + " upgraded to " + parent.getNodeName());

		// cambio il contenitore di node
		newElement = (Node) doc.createElement(parent.getNodeName());
		
		// assegna a newElement tutti gli attributi che aveva node; (ad esempio per preservare la vigenza)
		UtilDom.replaceAttributes(newElement,node);
		UtilDom.setIdAttribute(newElement, UtilDom.getAttributeValueAsString(node, "id"));
		
		Vector children = UtilDom.getAllChildElements(node);
		for (int i = 0; i < children.size(); i++)
			newElement.appendChild((Node) children.get(i));
		parent.removeChild(node);
		// //////////////////////////////////

		if (hasAlinea(newElement)) // se il nodo che ho upgradato ha l'alinea
			newElement = swapElEn(newElement);
		try {
			if (!dtdRulesManager.queryIsValid(parent)) { // il nodo
				// contenitore ha
				// perso validita'
				Node corpo = (Node) doc.createElement("corpo");
				Node alinea = UtilDom.findDirectChild(parent, "alinea");
				if (alinea != null) {
					children = UtilDom.getAllChildElements(alinea);
					for (int i = 0; i < children.size(); i++)
						corpo.appendChild((Node) children.get(i));
					parent.removeChild(alinea);
				}
				Node coda = UtilDom.findDirectChild(parent, "coda");
				if (coda != null) {
					children = UtilDom.getAllChildElements(coda);
					for (int i = 0; i < children.size(); i++)
						corpo.appendChild((Node) children.get(i));
					parent.removeChild(coda);
				}
				parent.appendChild(corpo);
			}
		} catch (DtdRulesManagerException ex) {
			return false;
		}
		parent.getParentNode().insertBefore(newElement, parent.getNextSibling()); // insertAfter
		modified = newElement;
		return true;
	}

	private Node swapElEn(Node node) {

		Vector children;
		if (node != null) {
			children = UtilDom.getAllChildElements(node);
			for (int i = 0; i < children.size(); i++) {
				if (((Node) children.get(i)).getNodeName().equals("el")) {
					children.set(i, changeTag((Node) children.get(i), "en"));
				}
				if (((Node) children.get(i)).getNodeName().equals("en")) {
					children.set(i, changeTag((Node) children.get(i), "el"));
				}
				swapElEn((Node) children.get(i));
			}
		}
		return node;
	}

	private Node changeTag(Node node, String newTag) {
		Document doc = node.getOwnerDocument();
		Node newElement = doc.createElement(newTag);
		Vector children = UtilDom.getAllChildElements(node);
		for (int i = 0; i < children.size(); i++)
			newElement.appendChild((Node) children.get(i));
		node.getParentNode().replaceChild(newElement, node);
		return newElement;
	}

	private boolean hasAlinea(Node node) {
		return (UtilDom.findDirectChild(node, "alinea") != null);
	}

	private boolean downGradeNode(Node node) {

		Node newElement;
		String newEl;
		Document doc = node.getOwnerDocument();
		Node brother = node.getPreviousSibling();

		if (brother.getNodeName().equals("comma") || brother.getNodeName().equals("en"))
			newEl = "el";
		else
			newEl = "en";
		logger.debug(node.getNodeName() + " downgraded  to " + newEl);

		// cambio il contenitore di node
		newElement = (Node) doc.createElement(newEl);
		
		// assegna a newElement tutti gli attributi che aveva node; (ad esempio per preservare la vigenza)
		UtilDom.replaceAttributes(newElement,node);
		UtilDom.setIdAttribute(newElement, UtilDom.getAttributeValueAsString(node, "id"));
	
		Vector children = UtilDom.getAllChildElements(node);
		for (int i = 0; i < children.size(); i++)
			newElement.appendChild((Node) children.get(i));
		node.getParentNode().removeChild(node);
		// ///////////////////////////////////////////////////

		if (hasAlinea(newElement)) // se il nodo che ho downgradato ha l'alinea
			newElement = swapElEn(newElement);

		// ///////////////////////////////////////////////////
		try {
			if (!hasAlinea(brother)) { // se il fratello a cui appendere non ha
				// l'alinea
				Node alinea = (Node) doc.createElement("alinea");
				Node corpo = UtilDom.findDirectChild(brother, "corpo");
				children = UtilDom.getAllChildElements(corpo);
				for (int i = 0; i < children.size(); i++)
					alinea.appendChild((Node) children.get(i));
				corpo.getParentNode().replaceChild(alinea, corpo);
			}
			// cerca punto di inserimento a partire dal fondo
			Node lastChild = brother.getLastChild();
			while (!dtdRulesManager.queryCanInsertAfter(brother, lastChild, newElement))
				lastChild = lastChild.getPreviousSibling();
			if (lastChild != null) {
				brother.insertBefore(newElement, lastChild.getNextSibling()); // non
				// basta
				// appenderlo,
				// ci
				// puo
				// essere
				// una
				// coda
				modified = newElement;
				return true;
			}
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

	private int[] pruneIndex(int[] idx) {
		if (idx.length < 2)
			return null;
		int[] temp = new int[idx.length - 1];
		for (int i = 1; i < idx.length; i++)
			temp[i - 1] = idx[i];
		return temp;
	}

	private int[] getPathIndex(Node node) {
		return (pruneIndex(UtilDom.getPathIndexes(node)));
	}

	private boolean isInsertableBefore(Node parent, Node node, Node newNode) {
		try {
			return (dtdRulesManager.queryCanInsertBefore(parent, node, newNode));
		} catch (DtdRulesManagerException ex) {
			return false;
		}
	}

	private boolean isInsertableAfter(Node parent, Node node, Node newNode) {
		try {
			return (dtdRulesManager.queryCanInsertAfter(parent, node, newNode));
		} catch (DtdRulesManagerException ex) {
			return false;
		}
	}

}
