package it.cnr.ittig.xmleges.editor.blocks.util.dom;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.util.Collection;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom</code>.</h1>
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class NirUtilDomImpl implements NirUtilDom, Loggable, Serviceable{
	Logger logger;

	RulesManager rulesManager;
	DocumentManager documentManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}


	
	public boolean isDtdBase() {
		if (documentManager.getDtdName().toLowerCase().indexOf("base") != -1 || documentManager.getDtdName().toLowerCase().indexOf("light") != -1)
			return true;
		return false;
	}

	public boolean isDtdDL() {
		if (documentManager.getDtdName().toLowerCase().indexOf("dl") != -1)
			return true;
		return false;
	}
	
	public boolean isDocCNR(Node activeNode) {
		//return(documentManager.getDocumentAsDom().getElementsByTagName("cnr:meta").getLength()>0);
	    Node activeMeta = findActiveMeta(documentManager.getDocumentAsDom(),activeNode);
	    Node[] cnrMeta = UtilDom.getElementsByTagName(documentManager.getDocumentAsDom(),activeMeta,"cnr:meta");
	    return (cnrMeta !=null && cnrMeta.length>0);
		
	}
	
	
	public Node getNIRElement(Document doc) {
		NodeList nl = doc.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
			if (nl.item(i).getNodeType() == Node.ELEMENT_NODE)
				return nl.item(i);
		return null;
	}

	public Node getTipoAtto(Document doc) {
		Node nir = getNIRElement(doc);
		NodeList nl = nir.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
			if (nl.item(i).getNodeType() == Node.ELEMENT_NODE)
				return nl.item(i);
		return null;
	}

	/**
	 * Controlla se un nodo e' un container (libro, parte, titolo, capo,
	 * articolo, comma)
	 * 
	 * @param node Il nodo da controllare
	 * @return true se il nodo e' considerato un container
	 */

	public boolean isContainer(org.w3c.dom.Node node) {

		Collection coll;
		Iterator iter;

		if (node == null || rulesManager == null)
			return false;

		// con la precedente regola il comma non veniva considerato un container
		// per la dtd base(!)
		// quindi per adesso meglio evitare sorprese..
		if (node.getNodeName().equalsIgnoreCase("el") || node.getNodeName().equalsIgnoreCase("en") || node.getNodeName().equalsIgnoreCase("ep") || node.getNodeName().equalsIgnoreCase("comma")
				|| node.getNodeName().equalsIgnoreCase("articolo") || node.getNodeName().equalsIgnoreCase("sezione")
				|| node.getNodeName().equalsIgnoreCase("capo") || node.getNodeName().equalsIgnoreCase("titolo") || node.getNodeName().equalsIgnoreCase("parte")
				|| node.getNodeName().equalsIgnoreCase("libro"))
			return true;

		try {
			coll = rulesManager.queryAppendable(node);
		} catch (Exception e) {
			return false;
		}

		if (!coll.isEmpty()) {
			iter = coll.iterator();
			while (iter.hasNext())
				if (((String) iter.next()).equalsIgnoreCase("#PCDATA"))
					return false;
			return true;
		} else
			return false;
	}

	public boolean isNirContainer(Node node) {

		if (node == null)
			return false;

		if (node.getNodeName().equalsIgnoreCase("el") || node.getNodeName().equalsIgnoreCase("en") || node.getNodeName().equalsIgnoreCase("ep") ||node.getNodeName().equalsIgnoreCase("comma")
				|| node.getNodeName().equalsIgnoreCase("articolo") || node.getNodeName().equalsIgnoreCase("sezione")
				|| node.getNodeName().equalsIgnoreCase("capo") || node.getNodeName().equalsIgnoreCase("titolo") || node.getNodeName().equalsIgnoreCase("parte")
				|| node.getNodeName().equalsIgnoreCase("libro"))
			return true;
		return false;
	}

	public Node getNirContainer(Node node) {
		if (node != null) {
			Node container = node;
			while (container != null && !isNirContainer(container))
				container = container.getParentNode();
			return container;
		}
		return (null);
	}

	public Node getParentContainer(Node node) {
		if (node != null) {
			Node container = node.getParentNode();
			while (container != null && !isContainer(container))
				container = container.getParentNode();
			return container;
		}
		return (null);
	}

	public Node getContainer(Node node) {
		if (node != null) {
			Node container = node;
			while (container != null && !isContainer(container))
				container = container.getParentNode();
			return container;
		}
		return (null);
	}

	public Node getHpContainer(Node node) {
		if (node != null) {
			Node container = node.getParentNode();
			while (container != null && container.getNodeName().indexOf("h:p") == -1)
				container = container.getParentNode();
			return container;
		}
		return (null);
	}

	/**
	 * Ritorna il pathname del nodo DOM
	 */
	public String getPathName(Node node) {
		if (node == null)
			return "";

		// first element
		String path = node.getNodeName();
		node = node.getParentNode();

		// rest of the hierarchy
		while (node != null && node.getNodeType() != Node.DOCUMENT_NODE) {
			path = node.getNodeName() + "." + path;
			node = node.getParentNode();
		}

		return path;
	}
	
	
	
	public Node checkAndCreateMeta(Document doc, Node node, String nome) {

		Node meta, found, child;
		meta = null;

		meta = findActiveMeta(doc, node);
		found = UtilDom.findDirectChild(meta, nome);

		if (found == null) { // se non c'e' il tag "nome" lo crea
			child = meta.getFirstChild();
			found = doc.createElement(nome);
			// qui getNodeTemplate
			// PROBLEMA:
			// la questione e' che prendendo i template magari il nodo minimale che crea non e' quello che si vuole ..
			
			try {
				// qui orderedInsertChild
				if (meta.getFirstChild() != null) {
					while (!rulesManager.queryCanInsertAfter(meta, child, found))
						child = child.getNextSibling();
					meta.insertBefore(found, child.getNextSibling());
				} else
					meta.appendChild(found);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			}
		}
		return (found);
	}
	
	

	/**
	 * se il nodo attivo ha un nodo inline lo restituisce, altrimenti lo crea
	 * 
	 * @param doc
	 * @param activeNode
	 * @return
	 */
	public Node checkAndCreateInlineMeta(Document doc, Node activeNode) {
		Node nodo = activeNode;
		Node inlinemeta;
		boolean executed = false;

		while (nodo.getParentNode() != null && !(nodo.getNodeName().equals("inlinemeta"))) {
			if (nodo.getPreviousSibling() != null)
				nodo = nodo.getPreviousSibling();
			else
				nodo = nodo.getParentNode();
		}
		if (nodo.getParentNode() != null) { // c'e' un inlinemeta
			return (nodo);
		}
		// non c'e' l' inlinemeta
		inlinemeta = doc.createElement("inlinemeta");
		nodo = activeNode;
		try {
			while (nodo.getParentNode() != null && !executed) {
				if (rulesManager.queryCanInsertBefore(nodo.getParentNode(), nodo.getParentNode().getFirstChild(), inlinemeta)) {
					if (rulesManager.queryIsValid(inlinemeta)) {
						nodo.getParentNode().insertBefore(inlinemeta, nodo.getParentNode().getFirstChild());
						executed = true;
						inlinemeta = UtilDom.findDirectChild(nodo.getParentNode(), "inlinemeta");
					}
				} else {
					nodo = nodo.getParentNode();
				}
			}
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		if (executed)
			return (inlinemeta);
		return (null);
	}
	
	
	public  Node findActiveMeta(Document doc, Node node){
		Node tagNode; 
		String tagName = "meta";
		
		// se non e' stato selezionato un nodo attivo prende il primo "tagName" del documento;
		if(node == null){
			if(doc.getElementsByTagName(tagName)!=null && doc.getElementsByTagName(tagName).getLength()>0)
				return doc.getElementsByTagName(tagName).item(0);
			else
				return null;
		}
		// cerca sopra al nodo attivo
		tagNode = findParentMeta(node);
		// se non lo trova sopra, cerca sotto
		if(tagNode == null){
			if(node.getParentNode()!=null)
				node = node.getParentNode();    // in questo modo cerca anche fra i fratelli
			tagNode = UtilDom.findRecursiveChild(node,tagName);
		}
		return tagNode;
	}
	
	/**
	 * Cerca il primo nodo antenato (padre o previousSibling) di tipo meta
	 */
	protected Node findParentMeta(Node node) {
		Node previous;
		String name = "meta";
		
		while (node != null )
			if (node.getNodeName().equals(name))
				return node;
			else{
				previous = node.getPreviousSibling();
		        if(previous != null)
		        	node = previous;
		        else{ 
		        	node = node.getParentNode();
		        }
			}
		return node;
	}


}
