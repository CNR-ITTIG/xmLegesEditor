package it.cnr.ittig.xmleges.core.blocks.panes.tree;

import it.cnr.ittig.xmleges.core.services.panes.tree.TreePaneCellRenderer;

import java.awt.Component;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.w3c.dom.Node;

/**
 * Classe che si preoccupa di visualizzare i nodi dell'albero.
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
public class TreePaneCellRendererImpl extends DefaultTreeCellRenderer {

	Vector renderers = new Vector();

	public void addRenderer(TreePaneCellRenderer renderer) {
		if (renderer != null && !renderers.contains(renderer))
			renderers.add(renderer);
	}

	public void removeRenderer(TreePaneCellRenderer renderer) {
		if (renderer != null && renderers.contains(renderer))
			renderers.remove(renderer);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		String text = null;
		Icon icon = null;
		try {
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
			Node node = (Node) treeNode.getUserObject();
			Enumeration en = renderers.elements();
			while (en.hasMoreElements()) {
				TreePaneCellRenderer renderer = (TreePaneCellRenderer) en.nextElement();
				if (renderer.canRender(node)) {
					text = renderer.getText(node);
					if (renderer.hasIcon(node))
						icon = renderer.getIcon(node, sel, expanded, leaf);
					break;
				}
			}
			if (text == null)
				if (node.getNodeValue() != null)
					text = node.getNodeValue();
				else
					text = node.getNodeName();
		} catch (Exception ex) {
			return this;
		}
		if (text == null)
			setText(value.toString());
		else
			setText(text);
		if (icon != null)
			setIcon(icon);
		return this;
	}
}