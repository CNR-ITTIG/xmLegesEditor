package it.cnr.ittig.xmleges.core.blocks.action.edit.importdom;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.w3c.dom.Node;

/**
 * TreeCellRender per l'albero sorgente per visualizzare i nodi importati.
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

public class ImportDomTreeCellRenderer extends DefaultTreeCellRenderer {
	ImportDomActionImpl importDomActionImpl;

	public ImportDomTreeCellRenderer(ImportDomActionImpl importDomActionImpl) {
		this.importDomActionImpl = importDomActionImpl;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		try {
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
			Node node = (Node) treeNode.getUserObject();
			if (importDomActionImpl.isImported(node))
				c.setForeground(Color.BLUE);
		} catch (Exception ex) {
		}
		return c;
	}

}
