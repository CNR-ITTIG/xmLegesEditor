package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent;
import it.cnr.ittig.xmleges.core.services.document.DomEdit;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.panes.xsltmapper.XsltMapper;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.xslt.UtilXslt;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe che mantiene allinenato il documento HTML generato dal foglio di conversione
 * XSLT con il Document per l'XsltTextPane.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class XsltDocument extends HTMLDocument {
	XsltPaneImpl xsltPaneImpl;

	Logger logger;

	I18n i18n;

	XsltMapper xsltMapper;

	/** Documento originale */
	Document domOrig;

	/** Corrispondenza id - element */
	Hashtable id2element = new Hashtable(2000);

	/** File di trasformazione */
	File xsltFile;

	/** Parametri per il file di trasformazione */
	Hashtable xsltParam;

	/** Leaf attivo */
	XsltLeafElement activeLeaf = null;

	/** Crea una nuova istanza di XsltDocument */
	public XsltDocument(XsltPaneImpl xsltPaneImpl) {
		setPreservesUnknownTags(false);
		this.xsltPaneImpl = xsltPaneImpl;
		this.logger = xsltPaneImpl.getLogger();
		this.i18n = xsltPaneImpl.getI18n();
		this.xsltMapper = xsltPaneImpl.getXsltMapper();
		setParser(new XsltEditorKit(xsltPaneImpl).getParser());
	}

	public Logger getLogger() {
		return this.logger;
	}

	public String setDocument(Document dom, File xslFile, Hashtable xsltParam) {
		try {
			this.domOrig = dom;
			this.xsltFile = xslFile;
			this.xsltParam = xsltParam;
			id2element.clear();
			return getHtml(dom);
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			return "<b>errore</b>";
		}
	}

	final static String HTML_ERROR = "<center><b>ERROR</b></center>";

	protected String getHtml(Node node) {
		if (logger.isDebugEnabled())
			logger.debug("BEGIN getHTML(Node)");
		String ret = HTML_ERROR;
		Node nodeHtml = null;
		try {
			if (logger.isDebugEnabled())
				logger.debug("node: " + node);
			if (xsltParam != null)
				nodeHtml = UtilXslt.applyXslt(node, xsltFile, xsltParam);
			else
				nodeHtml = UtilXslt.applyXslt(node, xsltFile);

			ret = UtilDom.domToString(nodeHtml);
			ret = ret.substring(ret.indexOf('\n'));
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
		if (logger.isDebugEnabled() && nodeHtml != null)
			logger.debug("END getHTML(Node): " + UtilDom.domToString(nodeHtml, true));

		return ret;
	}

	public synchronized void manageEvent(DocumentChangedEvent e) {
		logger.debug("BEGIN manageEvent(DocumentChangedEvent)");
		DomEdit[] edits = e.getTransaction().getEdits();
		for (int i = 0; i < edits.length; i++) {
			if (edits[i].getType() == DomEdit.SUBTREE_MODIFIED && edits[i].getNode().getParentNode() != null) {
				update(edits[i].getNode());
			}
		}
		logger.debug("END manageEvent(DocumentChangedEvent)");
	}

	public void update(Node node) {
		logger.debug("START XsltDocument.update(Node)");
		if (node == null) {
			logger.debug("nodo nullo");
		} else {
			String id = UtilDom.getAttributeValueAsString(node, XsltMapper.ID_NAME);
			if (logger.isDebugEnabled())
				logger.debug("node= " + node + ", id= " + id);
			if (id != null) {
				XsltElement elem = (XsltElement) getElement(id);
				if (elem == null) {
					logger.warn("impossibile trovare il leaf corrispondente, aggiorno il genitore...");
					update(node.getParentNode());
				} else {
					if (logger.isDebugEnabled())
						logger.debug("aggiorno elemento " + elem);
					elem.updateFromXslt();
				}
			}
		}
		if (logger.isDebugEnabled())
			logger.debug("END XsltDocument.update(Node)");
	}

	protected String getIdInAttributeSet(AttributeSet a) {
		logger.debug("START getIdInAttributeSet(AttributeSet)");
		String id = null;
		Enumeration en = a.getAttributeNames();
		while (en.hasMoreElements() && id == null) {
			Object k = en.nextElement();
			String v = a.getAttribute(k).toString();
			if ("id".equals(k.toString()))
				id = v;
			else {
				int s = v.indexOf("id=");
				if (s != -1) {
					int e = v.indexOf(' ', s + 3);
					id = (e != -1) ? v.substring(s + 3, e) : v.substring(s + 3);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("AttributeSet=" + a);
			logger.debug("id=" + id);
		}
		logger.debug("END getIdInAttributeSet");
		return id;
	}

	protected Element createBranchElement(Element parent, AttributeSet a) {
		XsltBranchElement branch = new XsltBranchElement(parent, a);
		String id = branch.getId();
		if (id != null)
			id2element.put(id, branch);
		return branch;
	}

	public class XsltBranchElement extends HTMLDocument.BlockElement implements XsltElement {
		String id;

		public XsltBranchElement(Element parent, AttributeSet a) {
			super(parent, a);
			id = getIdInAttributeSet(a);
		}

		public String getId() {
			return id;
		}

		public boolean updateDom(String text) {
			return false;
		}

		public void updateFromXslt() {
			logger.debug("START branch updateFromXslt");

			// TODO Rivedere tutto aggiornamento del pannello dal dom

			// Node n = xsltMapper.getDomById(getId());
			// logger.debug("branch: " + this);
			// logger.debug("n: " + n);
			// if (n != null)
			// try {
			// int s = getStartOffset();
			// int e = getEndOffset();
			// String t = getHtml(n) + "\n";
			// logger.debug("s=" + s + " e=" + e + " t: " + t);
			// setOuterHTML(this, t);
			// // setInnerHTML(this, t);
			// // replaceHTML(s, e - s, t, this.getAttributes());
			// } catch (Exception ex) {
			// logger.error(ex.toString(), ex);
			// }
			logger.debug("END branch updateFromXslt");
		}

		public String toString() {
			StringBuffer sb = new StringBuffer("XsltBranchElement:");
			sb.append(" id=");
			sb.append(getId());
			sb.append(' ');
			sb.append(super.toString());
			return sb.toString();
		}

	}

	// ///////////////////////////// COSTRUZIONE DEGLI ELEMENTI DI TESTO
	protected Element createLeafElement(Element parent, AttributeSet a, int p0, int p1) {
		XsltLeafElement leaf = new XsltLeafElement(parent, a, p0, p1);
		String id = leaf.getId();
		if (id != null)
			id2element.put(id, leaf);
		return leaf;
	}

	public class XsltLeafElement extends HTMLDocument.RunElement implements XsltElement {
		String id;

		public XsltLeafElement(Element parent, AttributeSet a, int offs0, int offs1) {
			super(parent, a, offs0, offs1);
			id = getIdInAttributeSet(a);
		}

		public String getText() {
			try {
				return getDomByLeaf(this).getNodeValue();
			} catch (Exception ex) {
				return null;
			}
		}

		public String getId() {
			return id;
		}

		public boolean updateDom(String text) {
			logger.debug("BEGIN updateDom(String)");
			Node n = getDomByLeaf(this);
			if (n == null) { // error
				logger.debug("nessun nodo dom corrispondente al leaf attivo.");
				logger.debug("END updateDom(String)");
				return false;
			}
			logger.debug("nodo dom= " + n + ", testo= " + text);
			xsltPaneImpl.updateNode(n, text);
			try {
				int s = getStartOffset();
				int e = getEndOffset();
				if (text == null)
					text = n.getNodeValue();
				replace(s, e - s, text, getAttributes());
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}

			logger.debug("END updateDom(String)");
			return true;
		}

		public void updateFromXslt() {
			logger.debug("START leaf updateFromXslt");
			Node n = getDomByLeaf(this);
			logger.debug("leaf: " + this);
			logger.debug("n: " + n);
			if (n != null && UtilDom.isTextNode(n) && n.getNodeValue() != null)
				try {
					int s = getStartOffset();
					int e = getEndOffset();
					String t = n.getNodeValue();
					logger.debug("s=" + s + " e=" + e + " t: " + t);
					replace(s, e - s, t, getAttributes());
					// xsltPaneImpl.getXsltTextPane().setCaretPosition(this.getEndOffset());
				} catch (BadLocationException ex) {
					logger.error(ex.toString(), ex);
				}
			logger.debug("END leaf updateFromXslt");
		}

		public synchronized void setEmpty(boolean empty) {
			// this.empty = empty;
		}

		public synchronized boolean isEmpty() {
			if (getId() != null && !getId().trim().equals("")) {
				Node domNode = xsltMapper.getDomById(getId());
				return (xsltMapper.getParentByGen(domNode) != null);
			}

			return false;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer("XsltLeafElement: id=");
			sb.append(getId());
			sb.append(" empty=");
			sb.append(isEmpty());
			sb.append(' ');
			sb.append(super.toString());
			sb.append("\ndomNode=");
			sb.append(getDomByLeaf(this));
			return sb.toString();
		}
	}

	/**
	 * Imposta il leaf attivo.
	 * 
	 * @param leaf leaf attivo
	 * @param local <code>true</code> indica che l'impostazione &egrave; interna
	 *            all'implementazione del componente e quindi deve essere emesso l'evento
	 *            di variazione del nodo attivo
	 */
	public synchronized boolean setActiveLeaf(XsltLeafElement leaf, boolean local) {
		logger.debug("BEGIN setActiveLeaf");
		if (activeLeaf == leaf) {
			logger.debug("END setActiveLeaf");
			return false;
		}
		if (local) {
			Node n = getDomByLeaf(leaf);
			if (xsltMapper.getParentByGen(n) != null) {
				n = xsltMapper.getParentByGen(n);
			}
			xsltPaneImpl.fireActiveNodeChanged(n);
			xsltPaneImpl.firePaneStatusChanged();
		}
		this.selLeaf = null;
		this.activeLeaf = leaf;
		logger.debug("END setActiveLeaf");
		return true;
	}

	/**
	 * Imposta il leaf alla posizione <code>pos</code> come attivo .
	 * 
	 * @param leaf leaf attivo
	 */
	public synchronized boolean setActiveLeaf(int pos) {
		return setActiveLeaf(getLeafByPos(pos), true);
	}

	/**
	 * Restituisce il leaf attivo.
	 * 
	 * @return leaf attivo
	 */
	public synchronized XsltLeafElement getActiveLeaf() {
		return this.activeLeaf;
	}

	/**
	 * Imposta il nodo attivo.
	 * 
	 * @param node nuovo nodo attivo
	 */
	public void setActiveNode(Node node) {
		logger.debug("BEGIN setActiveNode(Node)");
		logger.debug("node= " + node);
		if (node == null) {
			logger.debug("END setActiveNode(Node)");
			activeLeaf = null;
			return;
		}
		if (xsltMapper.getGenByParent(node) != null) {
			node = xsltMapper.getGenByParent(node);
		}

		XsltElement leaf = getElementByDom(node);

		if (leaf != null && leaf instanceof XsltLeafElement) {
			logger.debug("leaf corrispondente= " + leaf);
			setActiveLeaf((XsltLeafElement) leaf, false);
			if (leaf != null) {
				xsltPaneImpl.getXsltTextPane().setCaretPosition(leaf.getEndOffset(), false);
			}
		} else if (leaf != null && leaf instanceof XsltBranchElement) {
			activeLeaf = null;
		}

		logger.debug("END setActiveNode");
	}

	XsltLeafElement[] selLeaf = new XsltLeafElement[0];

	public void setSelectedNodes(Node[] nodes) {
		logger.debug("BEGIN setSelectedNodes(Node[])");
		Vector sel = new Vector(100);
		for (int i = 0; i < nodes.length; i++)
			selectTextNodeOnly(nodes[i], sel);
		Enumeration en = sel.elements();
		Vector v = new Vector();
		while (en.hasMoreElements()) {
			Node n = (Node) en.nextElement();
			XsltElement leaf = getElementByDom(n);
			if (leaf != null && leaf instanceof XsltLeafElement) {
				v.addElement((XsltLeafElement) leaf);
			}
		}
		selLeaf = new XsltLeafElement[v.size()];
		v.copyInto(selLeaf);
		logger.debug("END setSelectedNodes(Node[])");
	}

	public XsltLeafElement[] getSelectedLeafs() {
		return this.selLeaf;
	}

	protected void selectTextNodeOnly(Node node, Vector nodes) {
		if (node.getNodeName() != null)
			nodes.addElement(node);
		NodeList nl = node.getChildNodes();
		int len = nl.getLength();
		for (int i = 0; i < len; i++)
			selectTextNodeOnly(nl.item(i), nodes);
	}

	// ///////////////////////////////////////////////////////////// SELECTION
	public Element[] getElements(int start, int end) {
		logger.debug("BEGIN getElements(int, int)");
		Vector elems = new Vector();
		Element[] roots = getRootElements();
		for (int i = 0; i < roots.length; i++)
			findElement(elems, roots[i], start, end);
		Element[] ret = new Element[elems.size()];
		elems.copyInto(ret);
		logger.debug("END getElements(int, int)");
		return ret;
	}

	private void findElement(Vector ris, Element elem, int start, int end) {
		int s = elem.getStartOffset();
		int e = elem.getEndOffset();
		if ((s >= start && s <= end) || (e >= start && e <= end)) {
			ris.addElement(elem);
		}
		for (int i = 0; i < elem.getElementCount(); i++)
			findElement(ris, elem.getElement(i), start, end);
	}

	protected void getElements(Vector ris, Element elem, int start, int end) {
		int s = elem.getStartOffset();
		int e = elem.getEndOffset();
		if (((start <= s) & (s <= end)) | ((start <= e) & (e <= end)) | ((start <= s) & (end >= e))) {
			ris.addElement(elem);
		}
		for (int i = 0; i < elem.getElementCount(); i++)
			getElements(ris, elem.getElement(i), start, end);
	}

	public XsltLeafElement[] getXsltLeafElements(int start, int end) {
		logger.debug("BEGIN getXsltLeafElements(int, int)");
		Vector elems = new Vector();
		Element[] roots = getRootElements();
		for (int i = 0; i < roots.length; i++) {
			getElements(elems, roots[i], start, end);
		}
		Element[] ret = null;
		Vector leaves = new Vector();
		XsltLeafElement[] res = null;
		if ((elems.size() == 0) & (getElement(start).getId() != null))
			leaves.addElement(getElement(start));
		else {
			ret = new Element[elems.size()];
			elems.copyInto(ret);
			for (int i = 0; i < ret.length; i++) {
				if (ret[i] instanceof XsltLeafElement)
					leaves.addElement(ret[i]);
			}
		}
		res = new XsltLeafElement[leaves.size()];
		leaves.copyInto(res);
		logger.debug("END getXsltLeafElements(int, int)");
		return res;
	}

	public XsltLeafElement getElement(int pos) {
		return (XsltLeafElement) getCharacterElement(pos);
	}

	// ////////////////////////////////////////////////////////////////// LEAF
	public XsltElement getElementByDom(Node node) {
		return getElementById(xsltMapper.getIdByDom(node));
	}

	public XsltElement getElementById(String id) {
		try {
			return (XsltElement) id2element.get(id);
		} catch (Exception ex) {
			return null;
		}
	}

	public XsltLeafElement getLeafByPos(int pos) {
		try {
			return (XsltLeafElement) getCharacterElement(pos);
		} catch (Exception ex) {
			return null;
		}
	}

	// //////////////////////////////////////////////////////////// DOM E LEAF
	public Node getDomByLeaf(XsltLeafElement leaf) {
		try {
			return xsltMapper.getDomById(leaf.getId());
		} catch (Exception ex) {
			return null;
		}
	}

	public XsltPaneImpl getXsltPaneImpl() {
		return xsltPaneImpl;
	}
}
