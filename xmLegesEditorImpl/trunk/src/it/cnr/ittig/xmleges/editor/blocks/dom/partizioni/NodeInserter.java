/*
 * Created on Dec 7, 2004
 */
package it.cnr.ittig.xmleges.editor.blocks.dom.partizioni;

/**
 * @author agnoloni
 */

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
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
	
	DocumentManager documentManager;

	Node modificato = null;

	/** Creates a new instance of NodeInserter */
	public NodeInserter(PartizioniImpl partizioniImpl) {
		this.logger = partizioniImpl.getLogger();
		this.nirUtilDom = partizioniImpl.getNirUtilDom();
		this.utilRulesManager = partizioniImpl.getUtilRulesManager();
		this.documentManager = partizioniImpl.getDocumentManager();
	}
	

	public int canInsertNewNode(Node template, Document doc, Node activeNode, RulesManager rulesManager) {
		logger.debug("ask for canInsertNewNode: " + template.getNodeName());

		Node activeNodeContainer = getContainer(activeNode); // PARENT

		// rubrica trattata come caso a parte (non e' un contenitore)
		if (template.getNodeName().equalsIgnoreCase("rubrica")) {
			if (canInsertNewRubrica(activeNodeContainer, rulesManager))
				return 0;
			return -1;
		}

		// 0- inserimento allo stesso livello es: template: comma - activeNode:
		// comma
		if (canInsertTemplateAsBrother(template, activeNodeContainer, rulesManager))
			return 0;

		// 1- inserimento a livello superiore con split es: template: articolo -
		// activeNode: comma
		Node splitNode = getSplitNode(template, activeNodeContainer, rulesManager);
		Node insertPoint = getParentContainer(splitNode);
		if (canInsertTemplateAsBrother(template, insertPoint, rulesManager))
			return 1;

		// 2- inserimento a livello superiore senza split; appeso al parent. es:
		// template:articolo - activeNode:comma (es appende l'articolo a un
		// capo)
		Node parentContainer = getFirstParentContainerForTemplate(template, activeNodeContainer, rulesManager);
		if (parentContainer != null && canAppendTemplate(template, parentContainer, rulesManager))
			return 2;

		// 3- partizione racchiusa in una partizione di livello superiore; es:
		// template: capo - activeNode: articolo -> capo racchiude articoli
		if (parentContainer != null && canInsertTemplateAsParent(template, parentContainer, rulesManager))
			return 3;

		if (canSwapCorpo2Alinea(template, activeNodeContainer, rulesManager, doc))
			return 4;

		return -1;
	}

	public boolean insertNewNode(String elem_name, Document doc, Node activeNode, RulesManager rulesManager, int action) {
		Node template = getNodeTemplate(elem_name, doc, rulesManager);
		return (insertNewNode(template, doc, activeNode, rulesManager, action));
	}

	public boolean insertNewNode(Node template, Document doc, Node activeNode, RulesManager rulesManager, int action) {

		Node activeNodeContainer = getContainer(activeNode); // PARENT
		if (template != null) {
			// rubrica caso trattato a parte: non e' un container
			if (template.getNodeName().equalsIgnoreCase("rubrica"))
				return (insertNewRubrica(template, doc, activeNode, rulesManager));
			switch (action) {
			case -1:
				return (tryToInsert(template, doc, activeNode, rulesManager));
			case 0:
				logger.debug("insertTemplateASBrother");
				logger.debug("activeNodeContainer ");
				logger.debug("activeNodeContainer " + activeNodeContainer != null ? UtilDom.getPathName(activeNodeContainer) : "null");
				return (insertTemplateAsBrother(template, activeNodeContainer, rulesManager));
			case 1:
				logger.debug("splitAndInsert");
				logger.debug("activeNodeContainer ");
				logger.debug("activeNodeContainer " + activeNodeContainer != null ? UtilDom.getPathName(activeNodeContainer) : "null");
				return (splitAndInsert(template, doc, activeNodeContainer, rulesManager));
			case 2:
				logger.debug("splitAndAppend");
				logger.debug("activeNodeContainer ");
				logger.debug("activeNodeContainer " + activeNodeContainer != null ? UtilDom.getPathName(activeNodeContainer) : "null");
				return (splitAndAppend(template, doc, activeNodeContainer, rulesManager));
			case 3:
				logger.debug("insertTemplateAsParent");
				logger.debug("activeNodeContainer ");
				logger.debug("activeNodeContainer " + activeNodeContainer != null ? UtilDom.getPathName(activeNodeContainer) : "null");
				Node parentContainer = getFirstParentContainerForTemplate(template, activeNodeContainer, rulesManager);
				return (insertTemplateAsParent(template, parentContainer, rulesManager));
			case 4:
				logger.debug("swapCorpo2Alinea");
				logger.debug("activeNodeContainer ");
				logger.debug("activeNodeContainer " + activeNodeContainer != null ? UtilDom.getPathName(activeNodeContainer) : "null");
				return (swapCorpo2Alinea(activeNodeContainer, doc, rulesManager, template.getNodeName()));
			default:
				return false;
			}
		}
		return false;
	}

	public Node getModificato() {
		return this.modificato;
	}

	private boolean canInsertNewRubrica(Node activeNodeContainer, RulesManager rulesManager) {
		try {
			if (rulesManager.queryAppendable(activeNodeContainer).contains("rubrica"))
				return true;
			NodeList nl = activeNodeContainer.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				if (rulesManager.queryInsertableAfter(activeNodeContainer, nl.item(i)).contains("rubrica"))
					return true;
			}
			return false;
		} catch (RulesManagerException ex) {
			return false;
		}
	}

	private boolean insertNewRubrica(Node rubrica, Document doc, Node activeNode, RulesManager rulesManager) {
		Node activeNodeContainer = getContainer(activeNode);
		try {
			if (rulesManager.queryCanAppend(activeNodeContainer, rubrica)) {
				activeNodeContainer.appendChild(rubrica);
				return true;
			}
			NodeList nl = activeNodeContainer.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				if (rulesManager.queryCanInsertAfter(activeNodeContainer, nl.item(i), rubrica)) {
					UtilDom.insertAfter(rubrica, nl.item(i));
					return true;
				}
			}
			return false;
		} catch (RulesManagerException ex) {
			return false;
		}
	}

	private boolean tryToInsert(Node template, Document doc, Node activeNode, RulesManager rulesManager) {
		Node activeNodeContainer = getContainer(activeNode); // PARENT

		// 0- inserimento allo stesso livello es: template: comma - activeNode:
		// comma
		if (canInsertTemplateAsBrother(template, activeNodeContainer, rulesManager))
			return (insertTemplateAsBrother(template, activeNodeContainer, rulesManager));

		Node splitNode = getSplitNode(template, activeNodeContainer, rulesManager);
		//Vector move = split(splitNode, rulesManager);
		Node insertPoint = getParentContainer(splitNode);

		logger.debug("splitNode   " + nirUtilDom.getPathName(splitNode));
		logger.debug("insertPoint " + nirUtilDom.getPathName(insertPoint));
		logger.debug("activeNodeContainer  " + nirUtilDom.getPathName(activeNodeContainer));
		logger.debug("template  " + nirUtilDom.getPathName(template));

		// 1- inserimento a livello superiore con split es: template: articolo -
		// activeNode: comma
		if (canInsertTemplateAsBrother(template, insertPoint, rulesManager))
			return (splitAndInsert(template, doc, activeNodeContainer, rulesManager));

		Node parentContainer = getFirstParentContainerForTemplate(template, activeNodeContainer, rulesManager);

		// 2- inserimento a livello superiore senza split; appeso al parent. es:
		// template:articolo - activeNode:comma (es appende l'articolo a un
		// capo)
		if (parentContainer != null && canAppendTemplate(template, parentContainer, rulesManager))
			return (splitAndAppend(template, doc, activeNodeContainer, rulesManager));

		// 3- partizione racchiusa in una partizione di livello superiore; es:
		// template: capo - activeNode: articolo -> capo racchiude articoli
		if (parentContainer != null && canInsertTemplateAsParent(template, parentContainer, rulesManager))
			return (insertTemplateAsParent(template, parentContainer, rulesManager));

		// 4- provo a trasformare corpo in alinea
		if (canSwapCorpo2Alinea(template, activeNodeContainer, rulesManager, doc))
			return (swapCorpo2Alinea(activeNodeContainer, doc, rulesManager, template.getNodeName()));

		return false;
	}

	private boolean canInsertTemplateAsBrother(Node template, Node activeNodeContainer, RulesManager rulesManager) {
		if (template != null && activeNodeContainer != null) {
			try {
				return (rulesManager.queryCanInsertAfter(activeNodeContainer.getParentNode(), activeNodeContainer, template));
			} catch (RulesManagerException ex) {
				return false;
			}
		}
		return (false);
	}

	private boolean insertTemplateAsBrother(Node template, Node activeNodeContainer, RulesManager rulesManager) {
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

	private boolean canInsertTemplateAsParent(Node template, Node parentContainer, RulesManager rulesManager) {
		try {
			// Verifico se posso appendere i figli container di parentContainer
			// a template
			// Prendo solo i figli container di parentContainer
			Vector pcChildren = getContainerChildren(parentContainer);
			Node prunedTemplate = removeContainerChild(template, rulesManager);
			if(pcChildren.size()>0)
			  return (rulesManager.queryCanEncloseIn(parentContainer, (Node) pcChildren.get(0), pcChildren.size(), prunedTemplate));
		    return false;
		} catch (RulesManagerException ex) {
			return (false);
		}
	}

	/**
	 * Inserisco il template come contenitore di nodeContainer e degli eventuali
	 * fratelli e come figlio del parentContainer, parentContainer =>
	 * parentContainer |--nodeContainer |--template |--fratelli |--nodeContainer
	 * |--fratelli
	 */

	private boolean insertTemplateAsParent(Node template, Node parentContainer, RulesManager rulesManager) {
		try {
			// Prendo solo i figli container di parentContainer
			Vector pcChildren = getContainerChildren(parentContainer);

			Node prunedTemplate = removeContainerChild(template, rulesManager);
			for (int i = 0; i < pcChildren.size(); i++)
				prunedTemplate.appendChild((Node) (pcChildren.get(i)));
			parentContainer.appendChild(prunedTemplate);
			modificato = prunedTemplate;
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private boolean canAppendTemplate(Node template, Node parentContainer, RulesManager rulesManager) {
		try {
			return (rulesManager.queryCanAppend(parentContainer, template));
		} catch (RulesManagerException ex) {
			return false;
		}
	}

	private boolean splitAndAppend(Node newNode, Document doc, Node activeNodeContainer, RulesManager rulesManager) {

		Node splitNode = getSplitNode(newNode, activeNodeContainer, rulesManager);
		Vector move = split(splitNode, rulesManager);

		Node parentContainer = getFirstParentContainerForTemplate(newNode, activeNodeContainer, rulesManager);
		Node splitParent = getParentContainer(splitNode);
		Node completedTemplate = completeTemplate(newNode, move, rulesManager);

		if (splitParent != null && UtilDom.findDirectChild(splitParent, splitNode.getNodeName()) == null) // splitParent
																											// svuotato
			splitParent.appendChild(getNodeTemplate(splitNode.getNodeName(), doc, rulesManager));

		parentContainer.appendChild(completedTemplate);
		modificato = completedTemplate;
		return true;
	}

	private boolean splitAndInsert(Node newNode, Document doc, Node activeNodeContainer, RulesManager rulesManager) {

		Node splitNode = getSplitNode(newNode, activeNodeContainer, rulesManager);
		Vector move = split(splitNode, rulesManager);
		Node insertPoint = getParentContainer(splitNode);

		Node splitParent = getParentContainer(splitNode);
		Node completedTemplate = completeTemplate(newNode, move, rulesManager);
		if ((UtilDom.findDirectChild(splitParent, splitNode.getNodeName())) == null)
			splitParent.appendChild(getNodeTemplate(splitNode.getNodeName(), doc, rulesManager));
		return (insertTemplateAsBrother(completedTemplate, insertPoint, rulesManager));
	}

	/**
	 * Cerco, se esiste, il parentContainer di activeNodeContainer in cui posso
	 * inserire template come figlio
	 */

	private Node getFirstParentContainerForTemplate(Node template, Node activeNodeContainer, RulesManager rulesManager) {
		Node prevParentContainer;
		Node parentContainer = activeNodeContainer;
		try {
			do {
				if (rulesManager.getAlternativeContents(parentContainer.getNodeName()).contains(template.getNodeName())
					|| rulesManager.getAlternativeContents(parentContainer.getNodeName()).contains("num," + template.getNodeName()))
					return parentContainer;

				prevParentContainer = parentContainer;
				parentContainer = getParentContainer(parentContainer);
			} while (!prevParentContainer.getNodeName().equals("articolato"));
		} catch (RulesManagerException ex) {
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

	private Node removeContainerChild(Node template, RulesManager rulesManager) {
		NodeList child = template.getChildNodes();
		for (int i = 0; i < child.getLength(); i++) {
			if (nirUtilDom.isContainer(child.item(i)))
				template.removeChild(child.item(i));
		}
		return template;
	}

	private boolean canSwapCorpo2Alinea(Node template, Node activeNodeContainer, RulesManager rulesManager, Document doc) {

		Vector contents = new Vector();

		try {
			// logger.debug("alternative contents
			// for:"+activeNodeContainer.getNodeName()+ " : "
			// +rulesManager.getAlternativeContents(activeNodeContainer.getNodeName()).toString());
			if (UtilDom.findDirectChild(activeNodeContainer, "corpo") != null) {
				
				
				//TODO migliorare gestione dtdFlessibile
				if(documentManager.getGrammarName().indexOf("flessibile")!=-1 || documentManager.getGrammarName().indexOf("light")!=-1)
					return true;
				
				// GESTIONE ALTERNATIVA PER XSD :  da provare
				
				//Vector alternatives = new Vector();
				//alternatives.add(template);
				//System.err.println(rulesManager.getDefaultContent(activeNodeContainer.getNodeName(),alternatives));   
				// questo modo funziona su dtd ma non su schema; verificare getGappedAlignment e contentModel/automa di comma
				
				
				contents = rulesManager.getAlternativeContents(activeNodeContainer.getNodeName());
				
				//TODO non funziona con lo Schema perche' non e' ancora implementato getAlternativeContents()
				for (int j = 0; j < contents.size(); j++)
				if (((String) contents.get(j)).indexOf(template.getNodeName()) != -1) // il template e' ammesso come contenuto dell'active node
					return true;				
			}
			return false;
		} catch (RulesManagerException e) {
			//e.printStackTrace();
			return false;
		}
	}

	private boolean swapCorpo2Alinea(Node activeNodeContainer, Document doc, RulesManager rulesManager, String tipoLista) {

		Node tmpParent, newNode, dummyNode;
		Vector children;

		Node corpo = UtilDom.findDirectChild(activeNodeContainer, "corpo");
		tmpParent = corpo.getParentNode(); // sara' comma o lettera

		newNode = UtilDom.createElement(doc, "alinea");

		children = UtilDom.getAllChildElements(corpo);
		for (int i = 0; i < children.size(); i++)
			newNode.appendChild((Node) children.get(i));

		// appende l'alinea cosi' completata al vecchio nodo (sostituisce il
		// corpo con l'alinea)
		tmpParent.replaceChild(newNode, corpo);
		// riempito con una lettera o un numero vuoti (o con un lista puntata)
		dummyNode = getNodeTemplate(tipoLista, doc, rulesManager);

		try {
			if (rulesManager.queryCanInsertAfter(tmpParent, newNode, dummyNode))
				tmpParent.insertBefore(dummyNode, newNode.getNextSibling()); // insertAfter
//			else { // altrimenti ci appendo un numero
//				dummyNode = getNodeTemplate("en", doc, rulesManager);
//				tmpParent.insertBefore(dummyNode, newNode.getNextSibling()); // insertAfter
//			}
		} catch (Exception ex) {
			return false;
		}

		// controllo se ho creato un nodo valido
		try {
			if (!rulesManager.queryIsValid(tmpParent) || tmpParent.getNodeName().equalsIgnoreCase("en")) {
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
	//TODO: migliorare gestione dtdFlessibile
	public Node getNodeTemplate(String elem_name, Document doc, RulesManager rulesManager) {
		
		Node newNode = utilRulesManager.getNodeTemplate(doc, elem_name);
		//TODO usare funzionalita' del RulesManager
		if(documentManager.getGrammarName().indexOf("flessibile")!=-1 || documentManager.getGrammarName().indexOf("light")!=-1){
			addElement("num", doc, newNode, rulesManager);
			addElement("rubrica", doc, newNode, rulesManager);
			if (elem_name.equals("articolo")) {				
				addElement("comma", doc, newNode, rulesManager);
				addElement("num", doc, newNode, rulesManager);
				addElement("corpo", doc, newNode, rulesManager);
			}	
			addElement("corpo", doc, newNode, rulesManager);	
		}	
		else {
			addElement("num", doc, newNode, rulesManager);
			addElement("rubrica", doc, newNode, rulesManager);
		}
		return newNode;
	}	
	
	private Node completeTemplate(Node template, Vector move, RulesManager rulesManager) {
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
					if (rulesManager.queryCanAppend(template, (Node) (move.elementAt(i))))
						template.appendChild((Node) move.elementAt(i));
				} catch (RulesManagerException ex) {
				}
			}
		}
		return (template);
	}

	// aggiunge, se possibile secondo il rulesManager un
	// elemento di tipo elem_name al nodo node
	private void addElement(String elem_name, Document doc, Node node, RulesManager rulesManager) {
		NodeList childList;
		Vector childVect = new Vector();
		Node tmpNode, newNode;
		Iterator iterator;

		if (node != null) {
		
			newNode = UtilDom.createElement(doc,elem_name);
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
					// FIXME per dtd2.1; causa CMArticolo errato aggiunto !brothersContain() && canInsertBeforeRubNum
					if (rulesManager.queryCanInsertBefore(node, tmpNode, newNode) && canInsertBeforeRubNum(tmpNode,newNode) && !brothersContain(node,elem_name)) {
						node.insertBefore(newNode, tmpNode);
					    newNode = UtilDom.createElement(doc,elem_name);
					}
				} catch (Exception ex) {
				}
				// lo aggiunge anche a tutti i figli [es ai commi figli di articolo]
				addElement(elem_name, doc, tmpNode, rulesManager);
			}

			try {
				if (rulesManager.queryCanAppend(node, newNode)) {
					node.appendChild(newNode);
				}
			} catch (Exception exc) {
				// logger.error(exc.getMessage(), exc);
			}
		}
	}
	
	private boolean canInsertBeforeRubNum(Node tmpNode, Node newNode){
		if(newNode.getNodeName().equalsIgnoreCase("rubrica") && tmpNode.getNodeName().equalsIgnoreCase("num"))
			return false;
		return true;
	}
	
	private boolean brothersContain(Node parent, String elemName){
		NodeList childList = parent.getChildNodes();
		for (int i = 0; i < childList.getLength(); i++) {
			if(childList.item(i).getNodeName().equalsIgnoreCase(elemName))
				return true;
		}
		return false;
	}

	private Node getSplitNode(Node template, Node activeNodeContainer, RulesManager rulesManager) {

		if (activeNodeContainer != null) {
			try {
				while (!rulesManager.queryCanAppend(template, activeNodeContainer)) {
					activeNodeContainer = getParentContainer(activeNodeContainer);
					if (activeNodeContainer == null)
						break;
				}
			} catch (RulesManagerException ex) {
				// logger.error(ex.toString(),ex);
			}
		}
		return activeNodeContainer;
	}

	private Vector split(Node splitNode, RulesManager rulesManager) {

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
