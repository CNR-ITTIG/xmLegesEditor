package it.cnr.ittig.xmleges.core.util.dom;

import it.cnr.ittig.xmleges.core.services.panes.xsltmapper.XsltMapper;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.lang.UtilLang;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.swing.text.BadLocationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.ElementImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe di utilit&agrave; per documenti DOM.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class UtilDom {

	/**
	 * Converte il sottoalbero a partire da node in testo con tag xml
	 * 
	 * @param node nodo da trasformare in stringa
	 * @return stringa contenente il documento xml
	 * @return nodo xml rappresentante il nodo <code>node</code>
	 */
	public static String domToString(Node node) {
		return domToString(node, false);
	}

	/**
	 * Converte il sottoalbero a partire da node in testo con tag xml
	 * 
	 * @return stringa contenente il documento xml
	 * @param format indica se il file deve essere formattato (true) o no
	 *        (false)
	 * @param node nodo da trasformare in stringa
	 * @return nodo xml rappresentante il nodo <code>node</code>
	 */
	public static String domToString(Node node, boolean format) {
		return domToString(node, format, null);
	}

	/**
	 * Converte il sottoalbero a partire da node in testo con tag xml
	 * 
	 * @return stringa contenente il documento xml
	 * @param format indica se il file deve essere formattato (true) o no
	 *        (false)
	 * @param formatTab string usata per la tabulazione
	 * @param node nodo da trasformare in stringa
	 * @param formatTab string usata per la tabulazione
	 * @return nodo xml rappresentante il nodo <code>node</code>
	 */
	public static String domToString(Node node, boolean format, String formatTab) {
		return domToString(node, format, formatTab, true);
	}

	/**
	 * Converte il sottoalbero a partire da node in testo con tag xml
	 * 
	 * @return stringa contenente il documento xml
	 * @param format indica se il file deve essere formattato (true) o no
	 *        (false)
	 * @param formatTab string usata per la tabulazione
	 * @param node nodo da trasformare in stringa
	 * @param formatTab string usata per la tabulazione
	 * @return nodo xml rappresentante il nodo <code>node</code>
	 */
	public static String domToString(Node node, boolean format, String formatTab, boolean xmlns) {
		return domToString(node, format, formatTab, xmlns, true);
	}

	/**
	 * Converte il sottoalbero a partire da node in testo con tag xml
	 * 
	 * @return stringa contenente il documento xml
	 * @param format indica se il file deve essere formattato (true) o no
	 *        (false)
	 * @param formatTab string usata per la tabulazione
	 * @param node nodo da trasformare in stringa
	 * @param formatTab string usata per la tabulazione
	 * @param expandEmptyTag indica se espandere i nodi vuoti (&lt;p/&gt; o
	 *        &lt;p&gt;&lt;/p&gt;)
	 * @return nodo xml rappresentante il nodo <code>node</code>
	 */
	public static String domToString(Node node, boolean format, String formatTab, boolean xmlns, boolean expandEmptyTag) {
		DOMWriter writer = new DOMWriter();
		writer.setFormat(format);
		if (formatTab != null)
			writer.setFormatTab(formatTab);
		writer.setXmlns(xmlns);
		writer.setExpandEmptyTag(expandEmptyTag);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writer.setOutput(baos);
			writer.write(node);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return baos.toString();
	}

	/**
	 * Trasforma un'albero DOM in HTML
	 * 
	 * @param nodeXML la radice dell'albero DOM
	 * @param xslt path al file XSLT da usare per la trasformazione
	 */
	public static String domToHTML(Node nodeXML, String xslt) {
		try {

			// Transform DOM hierarchy in HTML hierarchy
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(xslt));

			DOMSource source = new DOMSource(nodeXML);
			DOMResult result = new DOMResult();
			transformer.transform(source, result);

			Node nodeHTML = result.getNode();

			// Transform HTML hierarchy in HTML text
			DOMWriter writer = new DOMWriter();
			writer.setFormat(false);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			writer.setOutput(baos);
			writer.write(nodeHTML);
			return baos.toString();
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * Controllo se un nodo e' un nodo testo e se ha dentro almeno una parola
	 * ovvero caratteri che non siano \n \t \b
	 * 
	 * @param node
	 * @throws DOMException
	 * @return
	 */
	public static boolean isTextNodeWithoutWords(Node node) throws DOMException {
		if (node == null)
			return false;
		if (node.getNodeType() != Node.TEXT_NODE)
			return false;
		// Se il contenuto e' solo \n \t \b dall'inizio alla fine
		// logger.debug("Dentro Utility "+ node.getNodeValue());
		// logger.debug(node.getNodeValue().matches("^\\s*$"));
		return node.getNodeValue().matches("^\\s*$");
	}

	/**
	 * Cerca il primo nodo padre con il nome specificato
	 * 
	 * @param node Nodo corrente
	 * @param name Nome del padre da cercare
	 * @return Il nodo padre con il nome specificato oppure null
	 */
	public static Node findParentByName(Node node, String name) {
		while (node != null)
			if (node.getNodeName().equals(name))
				return node;
			else
				node = node.getParentNode();
		return node;
	}

	/**
	 * Restituisce il nodo padre di tipo "Element"
	 */
	static public Element findElementPadre(Node nodo) {
		Node elem = nodo;
		while (elem.getNodeType() != Node.ELEMENT_NODE) {
			elem = elem.getParentNode();
		}
		return ((Element) elem);
	}

	/**
	 * Rimuove ricorsivamente tutti i nodi testo "vuoti" del sottoalbero
	 * individuato dal nodo <code>node</code>.
	 * 
	 * @param node nodo
	 */
	public static void removeEmptyText(Node node) {
		try {
			if (node == null)
				return;
			if (node.getNodeType() != Node.ELEMENT_NODE)
				return;

			Node child = node.getFirstChild();
			Node next = null;
			while (child != null) {
				next = child.getNextSibling();
				if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
					if (isVoid(child)) {
						node.removeChild(child);
					}
				} else
					removeEmptyText(child);
				child = next;
			}
		} catch (DOMException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Rimuove tutti i figli del nodo <i>node </i> specificato.
	 */
	public static void removeAllChildren(Node node) {
		NodeList nl = node.getChildNodes();
		while (nl.getLength() > 0)
			node.removeChild(nl.item(0));
	}

	/*
	 * public static void removeEmptyText(Node node) { //if (node == null ||
	 * node.getParentNode() == null) if (node == null) return; if
	 * (node.getNodeType() == Node.TEXT_NODE) if (isTextNodeWithoutWords(node)) {
	 * System.out.println("removing"); node.getParentNode().removeChild(node);
	 * return; } NodeList nl = node.getChildNodes(); for (int i = 0; i <
	 * nl.getLength(); i++) removeEmptyText(nl.item(i)); }
	 */

	public static Node checkAndCreate(Node node, String tagName) {
		Node e = findDirectChild(node, tagName);
		if (e != null)
			return e;
		try {
			Node newNode = node.getOwnerDocument().createElement(tagName);
			node.appendChild(newNode);
			return newNode;
		} catch (DOMException ex) {
		}
		return null;
	}

	public static Node checkAndDelete(Node node, String tagName) {
		Node e = findDirectChild(node, tagName);
		if (e == null)
			return e;
		try {
			node.removeChild(e);
		} catch (DOMException ex) {
		}
		return null;
	}

	public static Node findDirectChild(Node node, String tagName) {
		if (node == null)
			return null;
		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
			if (nl.item(i) != null && nl.item(i).getNodeName().equals(tagName) && nl.item(i).getNodeType() == Node.ELEMENT_NODE)
				return nl.item(i);
		return null;
	}

	public static void setTextNode(Node node, String text) {
		Node tmp = node.getOwnerDocument().createTextNode(text);
		if (node.getChildNodes().getLength() != 0) {
			if (node.getFirstChild().getNodeType() == Node.TEXT_NODE)
				node.replaceChild(tmp, node.getFirstChild());
		} else
			node.appendChild(tmp);
	}

	/**
	 * Restituisce il testo presente nel primo figlio se &egrave; di tipo text.
	 * 
	 * @param node
	 * @return
	 */
	public static String getTextNode(Node node) {
		if (node != null) {
			Node firstChild = node.getFirstChild();
			if (firstChild != null && firstChild.getNodeValue() != null)
				return firstChild.getNodeValue();
		}
		return "";
	}

	public static String getRecursiveTextNode(Node node) {
		String retVal = "";

		if (node.getNodeType() == Node.TEXT_NODE)
			retVal = node.getNodeValue().trim().replaceAll("\\s", " ");

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				retVal += " " + getRecursiveTextNode(list.item(i));
			}
		}

		return retVal;
	}

	public static void getTextChildren(XsltMapper mapper, Node node, Vector textChildren) {
		if (node.getNodeType() == Node.TEXT_NODE)
			textChildren.add(node);

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			if (mapper.getGenByParent(node) != null)
				textChildren.add(mapper.getGenByParent(node));
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				getTextChildren(mapper, list.item(i), textChildren);
			}
		}
	}

	public static String getRecursiveTextNodeII(Node node) {
		String retVal = "";

		if (node.getNodeType() == Node.TEXT_NODE)
			retVal = node.getNodeValue().trim().replaceAll("\\s", " ");

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			NodeList list = node.getChildNodes();
			if (list.getLength() > 0)
				retVal += getRecursiveTextNode(list.item(0));
			for (int i = 1; i < list.getLength(); i++) {
				retVal += "-" + getRecursiveTextNode(list.item(i));
			}
		}
		return retVal;
	}

	public static String findAttribute(Node node, String attributeName) {
		NamedNodeMap attList = node.getAttributes();
		if (attList != null) {
			Node attribute = attList.getNamedItem(attributeName);
			if (attribute != null) {
				return attribute.getNodeValue();
			}
		}
		return "";
	}

	public static boolean hasIdAttribute(Node node) {
		NamedNodeMap attList = node.getAttributes();
		if (attList != null) {
			Node attribute = attList.getNamedItem("id");
			if (attribute != null) {
				return isIdAttribute(attribute);
			}
		}
		return false;
	}

	private static boolean isIdAttribute(Node attribute) {

		try {
			return ((AttrImpl) ((Attr) attribute)).isId();
		} catch (ClassCastException e) {
			return false;
		}
	}

	public static String getAttributeValueAsString(Node node, String attribute) {
		NamedNodeMap nnm = node.getAttributes();
		Node value = (nnm != null) ? nnm.getNamedItem(attribute) : null;
		return (value != null) ? value.getNodeValue() : null;
	}

	public static String getAttributeValueAsString(Node node, String attribute, String def) {
		String ret = getAttributeValueAsString(node, attribute);
		return ret != null ? ret : def;
	}

	public static boolean getAttributeValueAsBoolean(Node node, String attribute) {
		return Boolean.valueOf(getAttributeValueAsString(node, attribute)).booleanValue();
		/*
		 * NamedNodeMap nnm = node.getAttributes(); Node value = (nnm != null) ?
		 * nnm.getNamedItem(attribute) : null; return (value != null) ?
		 * Boolean.valueOf(value.getNodeValue()).booleanValue() : false;
		 */
	}

	public static void setAttributeValue(Node node, String attribute, boolean value) {
		setAttributeValue(node, attribute, Boolean.toString(value));
	}

	public static void setIdAttribute(Node node, String id) {

		setAttributeValue(node, "id", id);
		try {
			((ElementImpl) ((Element) node)).setIdAttribute("id", true);
		} catch (ClassCastException ex) {
		}
	}

	public static void setAttributeValue(Node node, String attribute, String value) {
		NamedNodeMap nnm = node.getAttributes();
		Node attrNode = (nnm != null) ? nnm.getNamedItem(attribute) : null;
		if (attrNode == null) {
			Attr attr;
			if (attribute.indexOf(":") != -1) {
				attr = node.getOwnerDocument().createAttributeNS(getNameSpaceURIforElement(node, attribute.substring(0, attribute.indexOf(":"))), attribute);
			} else
				attr = node.getOwnerDocument().createAttribute(attribute);
			attr.setValue(value);
			node.getAttributes().setNamedItem(attr);
		} else
			attrNode.setNodeValue(value);
	}

	/***************************************************************************
	 * author: Enrico Controlla che il nodo Element sia di tipo EMPTY cioe' non
	 * ha figli element (ELEMENT_NODE) ne' figli testo (TEXT_NODE)
	 */
	public static boolean isElementEmpty(Element elem) {

		Node child_first = elem.getFirstChild();

		// Se l'element non ha figli
		// allora e' vuoto
		if (child_first == null)
			return true;

		Node child = child_first;

		// Controllo se fra i figli ci sono Element
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				// C'e' un figlio Element
				// il padre non e' EMPTY
				return false;
			}
			child = child.getNextSibling();
		}

		// Se non ci sono figli element
		// controllo che non ci siano figli testo
		child = child_first;
		while (child != null) {
			if (child.getNodeType() == Node.TEXT_NODE) {
				// C'e' un figlio TEXT
				// il padre non e' EMPTY
				return false;
			}
		}

		// Se non ci sono figli Element e non ci sono figli testo
		// allora l'elemento e' EMPTY
		return true;
	}

	/**
	 * Copia i figli di un nodo DOM all'interno di un Vector
	 */
	public static Vector getChildElements(org.w3c.dom.Node elem) {
		Vector elements = new Vector();
		org.w3c.dom.NodeList children = elem.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == Node.ELEMENT_NODE)
				elements.add(children.item(i));
		}
		return elements;
	}

	/**
	 * Copia i figli di un nodo, compresi i nodi testo, DOM all'interno di un
	 * Vector
	 */
	public static Vector getAllChildElements(org.w3c.dom.Node elem) {
		Vector elements = new Vector();
		org.w3c.dom.NodeList children = elem.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (!isVoid(children.item(i)))
				elements.add(children.item(i));
		}
		return elements;
	}

	/**
	 * Restituisce un vettore di Node contenente tutti i fratelli a destra di
	 * <code>node</code>
	 * 
	 * @param node
	 * @return
	 */
	public static Node[] getAllNextSibling(Node node) {
		Vector v = new Vector();
		Node next = node.getNextSibling();
		while (next != null) {
			v.add(next);
			next = next.getNextSibling();
		}
		Node[] ret = new Node[v.size()];
		v.copyInto(ret);
		return ret;
	}

	/**
	 * Restituisce un vettore di Node contenente tutti i fratelli a sinistra di
	 * <code>node</code>
	 * 
	 * @param node
	 * @return
	 */
	public static Node[] getAllPrevSibling(Node node) {
		Vector v = new Vector();
		Node prev = node.getPreviousSibling();
		while (prev != null) {
			v.add(prev);
			prev = prev.getPreviousSibling();
		}
		Node[] ret = new Node[v.size()];
		v.copyInto(ret);
		return ret;
	}

	/**
	 * Ritorna il testo contenuto all'interno di un insieme di nodi DOM e dei
	 * loro figli
	 * 
	 * @param elems i nodi da cui ricavare il testo
	 */
	public static String getText(Collection elems) {
		String text = null;
		for (Iterator i = elems.iterator(); i.hasNext();) {
			String node_text = getText((org.w3c.dom.Node) i.next());
			if (node_text != null) {
				if (text != null) {
					text = text + " " + node_text;
				} else {
					text = node_text;
				}
			}
		}
		return ((text != null) ? text : "");
	}

	/**
	 * Ritorna il testo contenuto all'interno di un nodo DOM e dei suoi figli
	 * 
	 * @param elem il nodo da cui ricavare il testo
	 * @return <code>null</code> se il nodo non contiene alcun testo
	 */
	public static String getText(org.w3c.dom.Node elem) {
		if (isVoid(elem))
			return null;

		if (elem.getNodeType() == Node.ELEMENT_NODE) {
			String text = null;
			NodeList children = elem.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				String child_text = getText(children.item(i));
				if (child_text != null) {
					if (text != null)
						text = text + " " + child_text;
					else
						text = child_text;
				}
			}
			return text;
		} else {
			return UtilLang.trimText(elem.getNodeValue());
		}
	}

	/**
	 * Controlla se il nodo DOM contiene qualcosa
	 */
	public static boolean isVoid(Node node) {
		if (node == null)
			return true;
		if (node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.CDATA_SECTION_NODE || node.getNodeType() == Node.COMMENT_NODE
				|| node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)
			return (UtilLang.trimText(node.getNodeValue()).length() == 0);
		return false;
	}

	/**
	 * Ritorna l'indice di un nodo all'interno del padre
	 * 
	 * @param parent il nodo padre
	 * @param child il nodo figlio
	 */
	public static int getChildIndex(Node parent, Node child) {
		if (parent == null || child == null)
			return -1;
		NodeList dom_children = parent.getChildNodes();
		for (int i = 0; i < dom_children.getLength(); i++) {
			if (dom_children.item(i) == child)
				return i;
		}
		return -1;
	}

	public static int getNoChildren(Node parent) {
		if (parent == null)
			return 0;

		org.w3c.dom.NodeList dom_children = parent.getChildNodes();
		return dom_children.getLength();
	}

	public static boolean isLastChild(Node parent, Node child) {
		if (parent == null || child == null)
			return false;
		return (getChildIndex(parent, child) == (getNoChildren(parent) - 1));
	}

	public static boolean isFirstChild(Node parent, Node child) {
		if (parent == null || child == null)
			return false;
		return (getChildIndex(parent, child) == 0);
	}

	/**
	 * Ritorna il pathname del nodo DOM
	 */
	public static String getPathName(Node node) {
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

	/**
	 * Restituisce gli indici dei nodi nella gerarchia di <code>node</code>
	 * fino alla radice.
	 * 
	 * @param node nodo DOM
	 * @return array di indici dei nodi
	 */
	public static int[] getPathIndexes(Node node) {
		Vector path = getPathFromNode(node);
		int[] ret = new int[path.size()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = ((Integer) path.get(i)).intValue();
		return ret;
	}

	/**
	 * Return a vector of child indexes to find a node in the element hierarchy
	 */
	public static Vector getPathFromNode(Node node) {
		Vector path = new Vector();

		Node nav = node;
		while (nav.getNodeType() != Node.DOCUMENT_NODE && nav.getParentNode() != null && nav.getParentNode().getNodeType() != Node.DOCUMENT_NODE) {
			Node parent = nav.getParentNode();
			int index = getChildIndex(parent, nav);
			path.insertElementAt(new Integer(index), 0);
			nav = parent;
		}

		return path;
	}

	/**
	 * Return the node in element hierarchy that correspond to the specified
	 * path
	 * 
	 * @throws BadLocationException
	 */
	public static Node getNodeFromPath(Document d, Vector path) throws BadLocationException {
		Node nav = d.getDocumentElement();
		for (Iterator i = path.iterator(); i.hasNext();) {
			int index = ((Integer) i.next()).intValue();
			Node child = (Node) nav.getChildNodes().item(index);
			if (child == null)
				throw new BadLocationException("Invalid path for document", index);
			nav = child;
		}
		return nav;
	}

	/**
	 * Ricerca nell'albero il padre comune ai due nodi.
	 * 
	 * @param node1 primo nodo
	 * @param node2 secondo nodo
	 * @return l'elemento trovato
	 */
	public static Node getCommonAncestor(Node node1, Node node2) {
		if (node1 == null && node2 == null)
			return null;

		if (node1 == null)
			return node2;
		if (node2 == null)
			return node1;

		int level1 = getPathFromNode(node1).size();
		int level2 = getPathFromNode(node2).size();

		if (level1 < level2)
			for (int l = level2; l > level1; l--, node2 = node2.getParentNode())
				;
		else if (level1 > level2)
			for (int l = level1; l > level2; l--, node1 = node1.getParentNode())
				;

		while (node1 != node2) {
			node1 = node1.getParentNode();
			node2 = node2.getParentNode();
		}
		return node1;
	}

	/**
	 * Ricerca nell'albero DOM il padre comune a tutti i nodi <code>nodes</code>.
	 * 
	 * @param nodes nodi sui quale cercare il padre comune
	 * @return padre comune
	 */
	public static Node getCommonAncestor(Node[] nodes) {
		if (nodes == null || nodes.length == 0)
			return null;
		Node common = nodes[0];
		for (int i = 1; i < nodes.length; i++)
			common = getCommonAncestor(common, nodes[i]);
		return common;
	}

	public static Node[] getCommonBrothers(Node[] nodes) {
		if (nodes == null)
			return null;

		Node commonAncestor = getCommonAncestor(nodes);

		Vector commonBros = new Vector();
		for (int i = 0; i < nodes.length; i++) {
			Node currNode = nodes[i];

			if (currNode == commonAncestor)
				return new Node[] { currNode };

			while (currNode.getParentNode() != commonAncestor) {
				currNode = currNode.getParentNode();
			}

			commonBros.add(currNode);
		}

		Node[] retVal = new Node[commonBros.size()];
		commonBros.copyInto(retVal);
		return retVal;
	}

	public static void replace(Node old_node, Node new_node) throws DOMException, NoSuchElementException {
		Node parent = old_node.getParentNode();
		if (parent == null)
			throw new NoSuchElementException("trying to replace Document node");
		parent.replaceChild(new_node, old_node);
	}

	public static void replaceAttributes(Node old_node, Node new_node) {
		if (old_node == null || new_node == null)
			return;

		// remove all old attributes
		NamedNodeMap old_attr_list = old_node.getAttributes();
		while (old_attr_list.getLength() > 0) {
			Node old_attr = old_attr_list.item(0);
			((Element) old_node).removeAttribute(old_attr.getNodeName());
		}

		// add all new attributes
		NamedNodeMap new_attr_list = new_node.getAttributes();
		for (int i = 0; i < new_attr_list.getLength(); i++) {
			Node new_attr = new_attr_list.item(i);
			((Element) old_node).setAttribute(new_attr.getNodeName(), new_attr.getNodeValue());
		}
	}

	public static String restrict(String value, int size) {
		if (value.length() > size)
			return (value.substring(0, size) + "...");
		return value;
	}

	public static String getNodeSummary(Node node) {
		if (node == null) {
			return "null";
		}

		short type = node.getNodeType();
		if (type == Node.ELEMENT_NODE) {
			String name = node.getNodeName().toLowerCase();

			if (name == "libro" || name == "sezione" || name == "capo" || name == "articolo") {
				String num = getTextNode(findDirectChild(node, "num"));
				if (num.length() > 0)
					name = num;

				String rubrica = getTextNode(findDirectChild(node, "rubrica"));
				if (rubrica.length() > 0) {
					name += " - " + restrict(rubrica, 15);
				}
			} else if (name == "comma") {
				String num = getTextNode(findDirectChild(node, "num"));
				if (num.length() > 0) {
					name += " ";
					for (int i = 0; i < num.length(); i++) {
						if (Character.isDigit(num.charAt(i))) {
							name += num.charAt(i);
						} else
							break;
					}
				}
			} else if (name == "el" || name == "en" || name == "ep") {
				if (name == "el")
					name = "lettera";
				else if (name == "en")
					name = "numero";
				else if (name == "ep")
					name = "punto";
				String num = getTextNode(findDirectChild(node, "num"));
				if (num.length() > 0) {
					name += " ";
					for (int i = 0; i < num.length(); i++) {
						if (Character.isLetterOrDigit(num.charAt(i))) {
							name += num.charAt(i);
						} else
							break;
					}
				}
			}

			return name;
		}
		if (type == Node.ATTRIBUTE_NODE) {
			return (node.getNodeName() + "=" + restrict(node.getNodeValue(), 15));
		}

		if (type == Node.CDATA_SECTION_NODE || type == Node.TEXT_NODE) {
			return restrict(node.getNodeValue(), 15);
		}

		if (type == Node.COMMENT_NODE) {
			return ("!" + restrict(node.getNodeValue(), 15));
		}

		if (type == Node.PROCESSING_INSTRUCTION_NODE) {
			return ("?" + node.getNodeName() + ":" + node.getNodeValue());
		}

		return node.getNodeName();
	}

	/**
	 * Fonde tutti i nodi di testo agiacenti figli del nodo <code>node</code>.
	 * 
	 * @param node padre dei nodi testo da unire
	 */
	public static void mergeTextNodes(Node node) {
		mergeTextNodes(node, false);
		// NodeList nl = node.getChildNodes();
		// Node p = nl.item(0);
		// for (int i = 1; i < nl.getLength(); i++) {
		// if (nl.item(i).getNodeType() == Node.TEXT_NODE && p.getNodeType() ==
		// Node.TEXT_NODE) {
		// p.setNodeValue(p.getNodeValue() + nl.item(i).getNodeValue());
		// node.removeChild(nl.item(i));
		// i--;
		// } else
		// p = nl.item(i);
		// }
	}

	public static void mergeTextNodes(Node node, boolean deep) {

		mergeTextNodes(node, deep, "");

		// NodeList nl = node.getChildNodes();
		// Node p = nl.item(0);
		// for (int i = 1; i < nl.getLength(); i++) {
		// if (nl.item(i).getNodeType() == Node.TEXT_NODE && p.getNodeType() ==
		// Node.TEXT_NODE) {
		// p.setNodeValue(p.getNodeValue() + nl.item(i).getNodeValue());
		// node.removeChild(nl.item(i));
		// i--;
		// } else if (deep && p.getNodeType() == Node.ELEMENT_NODE)
		// mergeTextNodes(p, true);
		// p = nl.item(i);
		// }

	}

	public static void mergeTextNodes(Node node, String separator) {
		mergeTextNodes(node, false, separator);
		// NodeList nl = node.getChildNodes();
		// Node p = nl.item(0);
		// for (int i = 1; i < nl.getLength(); i++) {
		// if (nl.item(i).getNodeType() == Node.TEXT_NODE && p.getNodeType() ==
		// Node.TEXT_NODE) {
		// p.setNodeValue(p.getNodeValue() + nl.item(i).getNodeValue());
		// node.removeChild(nl.item(i));
		// i--;
		// } else
		// p = nl.item(i);
		// }
	}

	public static void mergeTextNodes(Node node, boolean deep, String separator) {
		NodeList nl = node.getChildNodes();
		Node p = nl.item(0);
		for (int i = 1; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeType() == Node.TEXT_NODE && p.getNodeType() == Node.TEXT_NODE) {
				p.setNodeValue(p.getNodeValue() + separator + nl.item(i).getNodeValue());
				node.removeChild(nl.item(i));
				i--;
			} else if (deep && p.getNodeType() == Node.ELEMENT_NODE)
				mergeTextNodes(p, true, separator);
			p = nl.item(i);
		}

	}

	public static void trimAndMergeTextNodes(Node node, boolean deep) {
		trimTextNode(node, deep);
		mergeTextNodes(node, deep);
	}

	public static void trimTextNode(Node node, boolean deep) {
		if (isTextNode(node)) {
			String trim = UtilLang.trimText(node.getNodeValue());
			if (trim.length() == 0)
				node.getParentNode().removeChild(node);
			else if (trim.length() != node.getNodeValue().length())
				node.setNodeValue(trim);
		}

		if (deep) {
			NodeList nl = node.getChildNodes();
			Vector lista = new Vector(nl.getLength());
			for (int i = 0; i < nl.getLength(); i++) {
				lista.add(i, nl.item(i));
			}
			for (int i = 0; i < lista.size(); i++)
				trimTextNode((Node) lista.get(i), deep);
		}
	}

	/**
	 * Indica se in nodo <code>node</code> &egrave; di tipo testo, cio&egrave;
	 * <code>Node.CDATA_SECTION_NODE</code> o <code>Node.TEXT_NODE</code>;
	 * 
	 * @param node nodo da controllare
	 * @return <code>true</code> se &egrave; di tipo testo
	 */
	public static boolean isTextNode(Node node) {
		try {
			short t = node.getNodeType();
			return (t == Node.CDATA_SECTION_NODE || t == Node.TEXT_NODE);
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Controlla se il nodo <code>node</code> appartiene al sottoalbero
	 * individuato dal nodo <code>ancestor</code>.
	 * 
	 * @param node nodo figlio
	 * @param parent
	 * @return <code>true</code> se <code>node</code> appartiene al
	 *         sottoalbero individuato dal nodo <code>ancestor</code>
	 */
	public static boolean isAncestor(Node ancestor, Node node) {
		for (; node != null && !node.equals(ancestor); node = node.getParentNode())
			;
		return node != null;
	}

	public static void removeLevel(Node node) {
		NodeList nl = node.getChildNodes();
		Vector v = new Vector();
		for (int i = 0; i < nl.getLength(); i++) {
			NodeList nl2 = nl.item(i).getChildNodes();
			for (int j = 0; j < nl2.getLength(); j++)
				v.addElement(nl2.item(j));
		}
		for (Enumeration en = v.elements(); en.hasMoreElements();)
			node.appendChild((Node) en.nextElement());
	}

	public static String getNameSpaceURIforElement(Node node, String prefix) {
		return (UtilDom.getAttributeValueAsString(node.getOwnerDocument().getDocumentElement(), "xmlns:" + prefix));
	}

	/**
	 * Inserisce il nodo <code>newNode</code> come fratello successivo di
	 * <code>node</code>.
	 * 
	 * @param newNode nuovo nodo da inserire dopo <code>node</code>
	 * @param node nodo di riferimento
	 */
	public static void insertAfter(Node newNode, Node node) {
		if (node == null || node.getParentNode() == null)
			return;
		if (node.getNextSibling() != null)
			node.getParentNode().insertBefore(newNode, node.getNextSibling());
		else
			node.getParentNode().appendChild(newNode);
	}

	/**
	 * Indica se il nodo <code>child</code> appartine ad un sottoalbero
	 * individuato da almeno un nodo in <code>trees</code>.
	 * 
	 * @param child nodo org.w3c.dom.Node
	 * @param trees iterator sui nodi
	 * @return <code>true</code> se child appartiene all'albero di un nodo di
	 *         trees
	 */
	public static boolean isInSubtrees(Node child, Iterator trees) {
		while (trees.hasNext())
			try {
				Node node = (Node) trees.next();
				if (isAncestor(node, child))
					return true;
			} catch (ClassCastException ex) {
			}
		return false;
	}
}
