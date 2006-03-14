/*
 * Created on Dec 7, 2004
 */
package it.cnr.ittig.xmleges.editor.blocks.dom.partizioni;

/**
 * @author agnoloni
 */

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author mpapini Modificata da Enrico Modificata da Tommaso
 */
public class NodeInserter {
	Logger logger;

	NirUtilDom nirUtilDom;

	UtilRulesManager utilRulesManager;

	Node modificato = null;

	/** Creates a new instance of NodeInserter */
	public NodeInserter(PartizioniImpl partizioniImpl) {
		this.logger = partizioniImpl.getLogger();
		this.nirUtilDom = partizioniImpl.getNirUtilDom();
		this.utilRulesManager = partizioniImpl.getUtilRulesManager();
	}

	public int canInsertNewNode(Node template, Document doc, Node activeNode, DtdRulesManager dtdRulesManager) {
		logger.debug("ask for canInsertNewNode");

		Node activeNodeContainer = getContainer(activeNode); // PARENT

		// rubrica trattata come caso a parte (non e' un contenitore)
		if (template.getNodeName().equalsIgnoreCase("rubrica")) {
			if (canInsertNewRubrica(activeNodeContainer, dtdRulesManager))
				return 0;
			return -1;
		}

		// 0- inserimento allo stesso livello es: template: comma - activeNode:
		// comma
		if (canInsertTemplateAsBrother(template, activeNodeContainer, dtdRulesManager))
			return 0;

		// 1- inserimento a livello superiore con split es: template: articolo -
		// activeNode: comma
		Node splitNode = getSplitNode(template, activeNodeContainer, dtdRulesManager);
		Node insertPoint = getParentContainer(splitNode);
		if (canInsertTemplateAsBrother(template, insertPoint, dtdRulesManager))
			return 1;

		// 2- inserimento a livello superiore senza split; appeso al parent. es:
		// template:articolo - activeNode:comma (es appende l'articolo a un
		// capo)
		Node parentContainer = getFirstParentContainerForTemplate(template, activeNodeContainer, dtdRulesManager);
		if (parentContainer != null && canAppendTemplate(template, parentContainer, dtdRulesManager))
			return 2;

		// 3- partizione racchiusa in una partizione di livello superiore; es:
		// template: capo - activeNode: articolo -> capo racchiude articoli
		if (parentContainer != null && canInsertTemplateAsParent(template, parentContainer, dtdRulesManager))
			return 3;

		if (canSwapCorpo2Alinea(template, activeNodeContainer, dtdRulesManager))
			return 4;

		return -1;
	}

	public boolean insertNewNode(String elem_name, Document doc, Node activeNode, DtdRulesManager dtdRulesManager, int action) {
		Node template = getNodeTemplate(elem_name, doc, dtdRulesManager);
		return (insertNewNode(template, doc, activeNode, dtdRulesManager, action));
	}

	public boolean insertNewNode(Node template, Document doc, Node activeNode, DtdRulesManager dtdRulesManager, int action) {

		Node activeNodeContainer = getContainer(activeNode); // PARENT
		if (template != null) {
			// rubrica caso trattato a parte: non e' un container
			if (template.getNodeName().equalsIgnoreCase("rubrica"))
				return (insertNewRubrica(template, doc, activeNode, dtdRulesManager));
			switch (action) {
			case -1:
				return (tryToInsert(template, doc, activeNode, dtdRulesManager));
			case 0:
				logger.debug("insertTemplateASBrother");
				logger.debug("activeNodeContainer ");
				logger.debug("activeNodeContainer " + activeNodeContainer != null ? UtilDom.getPathName(activeNodeContainer) : "null");
				return (insertTemplateAsBrother(template, activeNodeContainer, dtdRulesManager));
			case 1:
				logger.debug("splitAndInsert");
				logger.debug("activeNodeContainer ");
				logger.debug("activeNodeContainer " + activeNodeContainer != null ? UtilDom.getPathName(activeNodeContainer) : "null");
				return (splitAndInsert(template, doc, activeNodeContainer, dtdRulesManager));
			case 2:
				logger.debug("splitAndAppend");
				logger.debug("activeNodeContainer ");
				logger.debug("activeNodeContainer " + activeNodeContainer != null ? UtilDom.getPathName(activeNodeContainer) : "null");
				return (splitAndAppend(template, doc, activeNodeContainer, dtdRulesManager));
			case 3:
				logger.debug("insertTemplateAsParent");
				logger.debug("activeNodeContainer ");
				logger.debug("activeNodeContainer " + activeNodeContainer != null ? UtilDom.getPathName(activeNodeContainer) : "null");
				Node parentContainer = getFirstParentContainerForTemplate(template, activeNodeContainer, dtdRulesManager);
				return (insertTemplateAsParent(template, parentContainer, dtdRulesManager));
			case 4:
				logger.debug("swapCorpo2Alinea");
				logger.debug("activeNodeContainer ");
				logger.debug("activeNodeContainer " + activeNodeContainer != null ? UtilDom.getPathName(activeNodeContainer) : "null");
				return (swapCorpo2Alinea(activeNodeContainer, doc, dtdRulesManager));
			default:
				return false;
			}
		}
		return false;
	}

	public Node getModificato() {
		return this.modificato;
	}

	private boolean canInsertNewRubrica(Node activeNodeContainer, DtdRulesManager dtdRulesManager) {
		try {
			if (dtdRulesManager.queryAppendable(activeNodeContainer).contains("rubrica"))
				return true;
			NodeList nl = activeNodeContainer.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				if (dtdRulesManager.queryInsertableAfter(activeNodeContainer, nl.item(i)).contains("rubrica"))
					return true;
			}
			return false;
		} catch (DtdRulesManagerException ex) {
			return false;
		}
	}

	private boolean insertNewRubrica(Node rubrica, Document doc, Node activeNode, DtdRulesManager dtdRulesManager) {
		Node activeNodeContainer = getContainer(activeNode);
		try {
			if (dtdRulesManager.queryCanAppend(activeNodeContainer, rubrica)) {
				activeNodeContainer.appendChild(rubrica);
				return true;
			}
			NodeList nl = activeNodeContainer.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				if (dtdRulesManager.queryCanInsertAfter(activeNodeContainer, nl.item(i), rubrica)) {
					UtilDom.insertAfter(rubrica, nl.item(i));
					return true;
				}
			}
			return false;
		} catch (DtdRulesManagerException ex) {
			return false;
		}
	}

	private boolean tryToInsert(Node template, Document doc, Node activeNode, DtdRulesManager dtdRulesManager) {
		Node activeNodeContainer = getContainer(activeNode); // PARENT

		// 0- inserimento allo stesso livello es: template: comma - activeNode:
		// comma
		if (canInsertTemplateAsBrother(template, activeNodeContainer, dtdRulesManager))
			return (insertTemplateAsBrother(template, activeNodeContainer, dtdRulesManager));

		Node splitNode = getSplitNode(template, activeNodeContainer, dtdRulesManager);
		Vector move = split(splitNode, dtdRulesManager);
		Node insertPoint = getParentContainer(splitNode);

		logger.debug("splitNode   " + nirUtilDom.getPathName(splitNode));
		logger.debug("insertPoint " + nirUtilDom.getPathName(insertPoint));
		logger.debug("activeNodeContainer  " + nirUtilDom.getPathName(activeNodeContainer));
		logger.debug("template  " + nirUtilDom.getPathName(template));

		// 1- inserimento a livello superiore con split es: template: articolo -
		// activeNode: comma
		if (canInsertTemplateAsBrother(template, insertPoint, dtdRulesManager))
			return (splitAndInsert(template, doc, activeNodeContainer, dtdRulesManager));

		Node parentContainer = getFirstParentContainerForTemplate(template, activeNodeContainer, dtdRulesManager);

		// 2- inserimento a livello superiore senza split; appeso al parent. es:
		// template:articolo - activeNode:comma (es appende l'articolo a un
		// capo)
		if (parentContainer != null && canAppendTemplate(template, parentContainer, dtdRulesManager))
			return (splitAndAppend(template, doc, activeNodeContainer, dtdRulesManager));

		// 3- partizione racchiusa in una partizione di livello superiore; es:
		// template: capo - activeNode: articolo -> capo racchiude articoli
		if (parentContainer != null && canInsertTemplateAsParent(template, parentContainer, dtdRulesManager))
			return (insertTemplateAsParent(template, parentContainer, dtdRulesManager));

		// 4- provo a trasformare corpo in alinea
		if (canSwapCorpo2Alinea(template, activeNodeContainer, dtdRulesManager))
			return (swapCorpo2Alinea(activeNodeContainer, doc, dtdRulesManager));

		return false;
	}

	private boolean canInsertTemplateAsBrother(Node template, Node activeNodeContainer, DtdRulesManager dtdRulesManager) {
		if (template != null && activeNodeContainer != null) {
			try {
				return (dtdRulesManager.queryCanInsertAfter(activeNodeContainer.getParentNode(), activeNodeContainer, template));
			} catch (DtdRulesManagerException ex) {
				return false;
			}
		}
		return (false);
	}

	private boolean insertTemplateAsBrother(Node template, Node activeNodeContainer, DtdRulesManager dtdRulesManager) {
		// Inserimento come fratello immediatamente successivo del nodo corrente
		// di tipo container
		try {
			Node nextNodeContainer = getNextContainer(activeNodeContainer);
			if (nextNodeContainer != null) // insertAfter(node) =
											// insertBefore(node.next)
				activeNodeContainer.getParentNode().insertBefore(template, nextNodeContainer);
			else
				activeNodeContainer.getParentNode().appendChild(template);

			modificato = template;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean canInsertTemplateAsParent(Node template, Node parentContainer, DtdRulesManager dtdRulesManager) {
		try {
			// Verifico se posso appendere i figli container di parentContainer
			// a template
			// Prendo solo i figli cotainer di parentContainer
			Vector pcChildren = getContainerChildren(parentContainer);
			Node prunedTemplate = removeContainerChild(template, dtdRulesManager);
			return (dtdRulesManager.queryCanEncloseIn(parentContainer, (Node) pcChildren.get(0), pcChildren.size(), prunedTemplate));
		} catch (DtdRulesManagerException ex) {
			return (false);
		}
	}

	/**
	 * Inserisco il template come contenitore di nodeContainer e degli eventuali
	 * fratelli e come figlio del parentContainer, parentContainer =>
	 * parentContainer |--nodeContainer |--template |--fratelli |--nodeContainer
	 * |--fratelli
	 */

	private boolean insertTemplateAsParent(Node template, Node parentContainer, DtdRulesManager dtdRulesManager) {
		try {
			// Prendo solo i figli container di parentContainer
			Vector pcChildren = getContainerChildren(parentContainer);

			Node prunedTemplate = removeContainerChild(template, dtdRulesManager);
			for (int i = 0; i < pcChildren.size(); i++)
				prunedTemplate.appendChild((Node) (pcChildren.get(i)));
			parentContainer.appendChild(prunedTemplate);
			modificato = prunedTemplate;
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private boolean canAppendTemplate(Node template, Node parentContainer, DtdRulesManager dtdRulesManager) {
		try {
			return (dtdRulesManager.queryCanAppend(parentContainer, template));
		} catch (DtdRulesManagerException ex) {
			return false;
		}
	}

	private boolean splitAndAppend(Node newNode, Document doc, Node activeNodeContainer, DtdRulesManager dtdRulesManager) {

		Node splitNode = getSplitNode(newNode, activeNodeContainer, dtdRulesManager);
		Vector move = split(splitNode, dtdRulesManager);

		Node parentContainer = getFirstParentContainerForTemplate(newNode, activeNodeContainer, dtdRulesManager);
		Node splitParent = getParentContainer(splitNode);
		Node completedTemplate = completeTemplate(newNode, move, dtdRulesManager);

		if (splitParent != null && UtilDom.findDirectChild(splitParent, splitNode.getNodeName()) == null) // splitParent
																											// svuotato
			splitParent.appendChild(getNodeTemplate(splitNode.getNodeName(), doc, dtdRulesManager));

		parentContainer.appendChild(completedTemplate);
		modificato = completedTemplate;
		return true;
	}

	private boolean splitAndInsert(Node newNode, Document doc, Node activeNodeContainer, DtdRulesManager dtdRulesManager) {

		Node splitNode = getSplitNode(newNode, activeNodeContainer, dtdRulesManager);
		Vector move = split(splitNode, dtdRulesManager);
		Node insertPoint = getParentContainer(splitNode);

		Node splitParent = getParentContainer(splitNode);
		Node completedTemplate = completeTemplate(newNode, move, dtdRulesManager);
		if ((UtilDom.findDirectChild(splitParent, splitNode.getNodeName())) == null)
			splitParent.appendChild(getNodeTemplate(splitNode.getNodeName(), doc, dtdRulesManager));
		return (insertTemplateAsBrother(completedTemplate, insertPoint, dtdRulesManager));
	}

	/**
	 * Cerco, se esiste, il parentContainer di activeNodeContainer in cui posso
	 * inserire template come figlio
	 */

	private Node getFirstParentContainerForTemplate(Node template, Node activeNodeContainer, DtdRulesManager dtdRulesManager) {
		Node prevParentContainer;
		Node parentContainer = activeNodeContainer;
		Vector contents = new Vector();
		try {
			do {
				if (dtdRulesManager.getAlternativeContents(parentContainer.getNodeName()).contains(template.getNodeName())
						|| dtdRulesManager.getAlternativeContents(parentContainer.getNodeName()).contains("num," + template.getNodeName()))
					return parentContainer;

				prevParentContainer = parentContainer;
				parentContainer = getParentContainer(parentContainer);
			} while (!prevParentContainer.getNodeName().equals("articolato"));
		} catch (DtdRulesManagerException ex) {
			return null;
		}
		return null;
	}

	/**
	 * restituisce i figli di tipo contenitore di parentContainer Prendo solo i
	 * figli container perche' se devo attaccare per esempio un Capo fra Titolo
	 * e Articolo il num e l'eventuale rubrica di Titolo devono rimanere
	 * <titolo> <titolo> <num> <num> <rubrica> => <rubrica> <articolo> <capo><num><rubrica>
	 * ... <articolo>
	 */

	private Vector getContainerChildren(Node parentContainer) {
		Vector pcChildren = new Vector();
		NodeList parentContainerChildren = parentContainer.getChildNodes();
		for (int i = 0; i < parentContainerChildren.getLength(); i++) {
			if (nirUtilDom.isContainer((Node) (parentContainerChildren.item(i))))
				pcChildren.addElement(parentContainerChildren.item(i));

		}
		return pcChildren;
	}

	private Node getContainer(Node node) {
		if (node != null) {
			// TODO modificare separando getContainer e getParentContainer in
			// modo che funzioni anche dai nodi dell'albero
			Node container = node;
			while (container != null && !nirUtilDom.isContainer(container))
				container = container.getParentNode();
			return container;
		}
		return (null);
	}

	private Node getParentContainer(Node node) {
		if (node != null) {
			// TODO modificare separando getContainer e getParentContainer in
			// modo che funzioni anche dai nodi dell'albero
			Node container = node.getParentNode();
			while (container != null && !nirUtilDom.isContainer(container))
				container = container.getParentNode();
			return container;
		}
		return (null);
	}

	private Node getNextContainer(Node node) {
		Node container = node.getNextSibling();

		if (container != null)
			while (!nirUtilDom.isContainer(container) && !container.getNodeName().equals("coda"))
				container = container.getNextSibling();
		return container;
	}

	/**
	 * Toglie dal template i figli di tipo container Serve quando si deve
	 * inglobare in template altri nodi e non inserire nodi vuoti inutili
	 */

	private Node removeContainerChild(Node template, DtdRulesManager dtdRulesManager) {
		NodeList child = template.getChildNodes();
		for (int i = 0; i < child.getLength(); i++) {
			if (nirUtilDom.isContainer(child.item(i)))
				template.removeChild(child.item(i));
		}
		return template;
	}

	private boolean canSwapCorpo2Alinea(Node template, Node activeNodeContainer, DtdRulesManager dtdRulesManager) {

		Vector contents = new Vector();

		try {
			// logger.debug("alternative contents
			// for:"+activeNodeContainer.getNodeName()+ " : "
			// +dtdRulesManager.getAlternativeContents(activeNodeContainer.getNodeName()).toString());
			if (UtilDom.findDirectChild(activeNodeContainer, "corpo") != null) {
				contents = dtdRulesManager.getAlternativeContents(activeNodeContainer.getNodeName());
				for (int j = 0; j < contents.size(); j++)
					if (((String) contents.get(j)).indexOf(template.getNodeName()) != -1) // il
																							// template
																							// e'
																							// ammesso
																							// come
																							// contenuto
																							// dell'active
																							// node
						return true;
			}
			return false;
		} catch (DtdRulesManagerException e) {
			return false;
		}
	}

	private boolean swapCorpo2Alinea(Node activeNodeContainer, Document doc, DtdRulesManager dtdRulesManager) {

		Node tmpParent, newNode, dummyNode;
		Vector children;

		Node corpo = UtilDom.findDirectChild(activeNodeContainer, "corpo");
		tmpParent = corpo.getParentNode(); // sara' comma o lettera

		newNode = doc.createElement("alinea");

		children = UtilDom.getAllChildElements(corpo);
		for (int i = 0; i < children.size(); i++)
			newNode.appendChild((Node) children.get(i));

		// appende l'alinea cosi' completata al vecchio nodo (sostituisce il
		// corpo con l'alinea)
		tmpParent.replaceChild(newNode, corpo);
		// riempito con una lettera o un numero vuoti
		dummyNode = getNodeTemplate("el", doc, dtdRulesManager);

		try {
			if (dtdRulesManager.queryCanInsertAfter(tmpParent, newNode, dummyNode))
				tmpParent.insertBefore(dummyNode, newNode.getNextSibling()); // insertAfter
			else { // altrimenti ci appendo un numero
				dummyNode = getNodeTemplate("en", doc, dtdRulesManager);
				tmpParent.insertBefore(dummyNode, newNode.getNextSibling()); // insertAfter
			}
		} catch (Exception ex) {
			return false;
		}

		// controllo se ho creato un nodo valido
		try {
			if (!dtdRulesManager.queryIsValid(tmpParent) || tmpParent.getNodeName().equalsIgnoreCase("en")) {
				tmpParent.replaceChild(corpo, newNode);
				tmpParent.removeChild(dummyNode);
				return false;
			} else {
				modificato = dummyNode;
				return true;
			}
		} catch (Exception ex) {
			return false;
		}
	}

	// fornisce un template del nodo minimale, arricchedolo sempre, se
	// possibile, per necessita' di visualizzazione
	// di un numero e di una rubrica
	private Node getNodeTemplate(String elem_name, Document doc, DtdRulesManager dtdRulesManager) {
		Node newNode = utilRulesManager.getNodeTemplate(doc, elem_name);
		// inserisci ovunque possibile il num
		addElement("num", doc, newNode, dtdRulesManager);
		// inserisci ovunque possibile la rubrica
		addElement("rubrica", doc, newNode, dtdRulesManager);
		return newNode;
	}

	private Node completeTemplate(Node template, Vector move, DtdRulesManager dtdRulesManager) {
		Node replace;
		int j = 0;

		if (move.size() > 0) {
			// toglie il primo nodo del template
			if ((replace = UtilDom.findDirectChild(template, ((Node) move.elementAt(0)).getNodeName())) != null) {
				UtilDom.replace(replace, (Node) move.elementAt(0));
				j = 1;
			}

			for (int i = j; i < move.size(); i++) {
				try {
					if (dtdRulesManager.queryCanAppend(template, (Node) (move.elementAt(i))))
						template.appendChild((Node) move.elementAt(i));
				} catch (DtdRulesManagerException ex) {
				}
			}
		}
		return (template);
	}

	// aggiunge, se possibile secondo il dtdRulesManager un
	// elemento di tipo elem_name al nodo node
	private void addElement(String elem_name, Document doc, Node node, DtdRulesManager dtdRulesManager) {
		NodeList childList;
		Vector childVect = new Vector();
		Node tmpNode, newNode;
		Iterator iterator;

		if (node != null) {

			newNode = doc.createElement(elem_name);
			childList = node.getChildNodes();
			// crea il vettore dei figli del padre del nodo corrente (tmpParent)
			for (int i = 0; i < childList.getLength(); i++) {
				tmpNode = childList.item(i);
				childVect.add(tmpNode);
			}

			iterator = childVect.iterator();
			while (iterator.hasNext()) {
				tmpNode = (Node) iterator.next();
				try {
					if (dtdRulesManager.queryCanInsertBefore(node, tmpNode, newNode)) {
						node.insertBefore(newNode, tmpNode);
						newNode = doc.createElement(elem_name);
					}
				} catch (Exception ex) {
				}
				addElement(elem_name, doc, tmpNode, dtdRulesManager);
			}

			try {
				if (dtdRulesManager.queryCanAppend(node, newNode)) {
					node.appendChild(newNode);
				}
			} catch (Exception exc) {
				// logger.error(exc.getMessage(), exc);
			}
		}
	}

	private Node getSplitNode(Node template, Node activeNodeContainer, DtdRulesManager dtdRulesManager) {

		if (activeNodeContainer != null) {
			try {
				while (!dtdRulesManager.queryCanAppend(template, activeNodeContainer)) {
					activeNodeContainer = getParentContainer(activeNodeContainer);
					if (activeNodeContainer == null)
						break;
				}
			} catch (DtdRulesManagerException ex) {
				// logger.error(ex.toString(),ex);
			}
		}
		return activeNodeContainer;
	}

	private Vector split(Node splitNode, DtdRulesManager dtdRulesManager) {

		Node parent = null;
		Vector moveNodes = new Vector();

		if (splitNode != null) {
			parent = getParentContainer(splitNode); // parentContainer o parent
													// ?
		}

		if (parent != null) {
			NodeList childs = parent.getChildNodes();
			logger.debug("----------> split: parent di activeNodeContainer  " + nirUtilDom.getPathName(parent));
			int i = 0;
			while (i < childs.getLength() && childs.item(i) != splitNode) {
				i = i + 1;
			}
			// se e' sull'ultimo non lo porta via
			if (i == childs.getLength() - 1)
				i = i + 1;

			for (int j = i; j < childs.getLength(); j++) {
				moveNodes.addElement(childs.item(j));
			}
		}
		return moveNodes;
	}

}
