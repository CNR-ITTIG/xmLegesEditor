package it.cnr.ittig.xmleges.core.blocks.panes.tree;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dnd.DomTransferHandler;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Node;

/**
 * Classe per la gestione del <i>DragAndDrop </i> per il <code>TreePanel</code>.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class TreeTransferHandler extends DomTransferHandler {

	TreePaneImpl treePaneImpl;

	Node sourceNode = null;

	Logger logger;

	SelectionManager selectionManager;

	public TreeTransferHandler(TreePaneImpl treePaneImpl) {
		this.treePaneImpl = treePaneImpl;
		this.logger = treePaneImpl.getLogger();
		this.selectionManager = treePaneImpl.getSelectionManager();
	}

	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	protected boolean isDomSupported(JComponent c) {
		boolean retVal = ((DefaultMutableTreeNode) ((JTree) c).getLastSelectedPathComponent()).getUserObject() != null;
		return retVal;
	}

	protected Node exportDom(JComponent c) {
		logger.debug("TreeTransferHandler.exportDom");
		sourceNode = selectionManager.getActiveNode();
		shouldRemove = true;
		return sourceNode;
	}

	protected boolean importDom(JComponent c, Node dragged) {
		logger.debug("TreeTransferHandler.importDom");
		JTree tree = (JTree) c;

		Node dropped = (Node) ((DefaultMutableTreeNode) tree.getLastSelectedPathComponent()).getUserObject();

		Node commonAncestor = UtilDom.getCommonAncestor(dragged, dropped);

		if (commonAncestor == dragged || commonAncestor == dropped)
			shouldRemove = false;

		if (dropped == dragged)
			return true;

		boolean canAppend = false;

		try {
			canAppend = treePaneImpl.dtdRulesManager.queryCanAppend(dropped, dragged);
		} catch (DtdRulesManagerException drme) {
		}

		if (canAppend)
			dropped.appendChild(dragged.cloneNode(true));
		else
			shouldRemove = false;

		return true;
	}

	protected boolean isStringSupported(JComponent c) {
		Node selNode = (Node) ((DefaultMutableTreeNode) ((JTree) c).getLastSelectedPathComponent()).getUserObject();
		return selNode.getNodeType() == Node.TEXT_NODE;
	}

	protected String exportString(JComponent c) {
		return null;
	}

	protected boolean importString(JComponent c, String str) {
		return false;
	}

	protected void cleanup(JComponent c, boolean remove) {
		if (shouldRemove && remove && sourceNode != null) {
			sourceNode.getParentNode().removeChild(sourceNode);
		}
		sourceNode = null;
	}
}
