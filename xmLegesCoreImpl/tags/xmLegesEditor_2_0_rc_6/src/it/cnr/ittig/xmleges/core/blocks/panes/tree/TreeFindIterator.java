package it.cnr.ittig.xmleges.core.blocks.panes.tree;

import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.util.Vector;

import org.w3c.dom.Node;

/**
 * Classe per effettuare la ricerca di nodi nell'albero.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class TreeFindIterator implements FindIterator {

	TreePaneImpl treePaneImpl;

	String text;

	boolean caseSensitive;

	Vector find = new Vector();

	int last = -1;

	public TreeFindIterator(TreePaneImpl treePaneImpl) {
		this.treePaneImpl = treePaneImpl;
	}

	public void initFind(String text) {
		this.text = text;
		find(treePaneImpl.documentManager.getRootElement(), find, text, caseSensitive);
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	private void find(Node node, Vector ret, String text, boolean caseSensitive) {
		ret.clear();
		String toFind = caseSensitive ? text : text.toLowerCase();
		findNodes(node, ret, toFind, caseSensitive);
		last = -1;
	}

	private void findNodes(Node node, Vector ret, String text, boolean caseSensitive) {
		if (node == null)
			return;
		if (UtilDom.isTextNode(node)) {
			String nodeValue = caseSensitive ? node.getNodeValue() : node.getNodeValue().toLowerCase();
			if (nodeValue.indexOf(text) != -1)
				ret.add(node);
		} else {
			String nodeName = caseSensitive ? node.getNodeName() : node.getNodeName().toLowerCase();
			if (nodeName.indexOf(text) != -1)
				ret.add(node);
			for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling())
				findNodes(n, ret, text, caseSensitive);
		}
	}

	public boolean next() {
		last++;
		if (last >= find.size()) {
			last = -1;
			find.clear();
			return false;
		} else {
			treePaneImpl.selectNode((Node) find.get(last), true);
			return true;
		}
	}

	public boolean canReplace(String text) {
		return false;
	}

	public void replace(String text) {
		// do nothing
	}

}
