package it.cnr.ittig.xmleges.core.blocks.action.edit.importdom;

import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TransferHandler per il DnD tra la sorgente e la destinazione.
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
 * @see it.cnr.ittig.xmleges.core.services.document.DocumentManager
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */

public class ImportTransferHandler extends TransferHandler {
	ImportDomActionImpl importDomActionImpl;

	DataFlavor domFlavor;

	String domType = DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Node.class.getName();

	JTree source = null;

	public ImportTransferHandler(ImportDomActionImpl importDomActionImpl) {
		this.importDomActionImpl = importDomActionImpl;
		try {
			domFlavor = new DataFlavor(domType);
		} catch (ClassNotFoundException e) {
			System.out.println("ArrayListTransferHandler: unable to create data flavor");
		}
	}

	public boolean importData(JComponent c, Transferable t) {
		if (source.equals(c) || !canImport(c, t.getTransferDataFlavors()))
			return false;

		try {
			JTree target = (JTree) c;
			Node node = null;
			if (hasDomFlavor(t.getTransferDataFlavors()))
				node = (Node) t.getTransferData(domFlavor);
			else
				return false;
			EditTransaction tr = null;
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) target.getLastSelectedPathComponent();
			Node dest = (Node) (treeNode).getUserObject();
			try {
				try {
					if (dest.getNodeName().equals(node.getNodeName())) {
						importDomActionImpl.logger.error("CAN APPEND CHILDS to " + dest);
						Vector v = new Vector();
						tr = importDomActionImpl.documentManager.beginEdit();
						NodeList nl = node.getChildNodes();
						boolean all = nl.getLength() > 0;
						for (int i = 0; i < nl.getLength(); i++) {
							Node n = nl.item(i);
							if (importDomActionImpl.dtdRulesManager.queryCanAppend(dest, n)) {
								dest.appendChild(dest.getOwnerDocument().importNode(n.cloneNode(true), true));
								v.addElement(n);
								importDomActionImpl.addImported(n);
							} else
								all = false;
						}
						if (all)
							importDomActionImpl.addImported(node);
						else {
							importDomActionImpl.utilMsg.msgError("edit.importdom.msg.notall");
							importDomActionImpl.addImported(v);
						}
						importDomActionImpl.documentManager.commitEdit(tr);
						rebuildSubTree(target, treeNode);
						source.invalidate();
						source.repaint();
					} else if (importDomActionImpl.dtdRulesManager.queryCanAppend(dest, node)) {
						importDomActionImpl.logger.error("CAN APPEND to " + dest);
						Node toImp = dest.getOwnerDocument().importNode(node.cloneNode(true), true);
						tr = importDomActionImpl.documentManager.beginEdit();
						dest.appendChild(toImp);
						importDomActionImpl.documentManager.commitEdit(tr);
						importDomActionImpl.addImported(node);
						rebuildSubTree(target, treeNode);
						source.invalidate();
						source.repaint();
					} else if (importDomActionImpl.dtdRulesManager.queryCanInsertBefore(dest.getParentNode(), dest, node)) {
						importDomActionImpl.logger.error("CAN INSERT BEFORE to " + dest);
						Node toImp = dest.getOwnerDocument().importNode(node.cloneNode(true), true);
						tr = importDomActionImpl.documentManager.beginEdit();
						dest.getParentNode().insertBefore(toImp, dest);
						importDomActionImpl.documentManager.commitEdit(tr);
						importDomActionImpl.addImported(node);
						rebuildSubTree(target, treeNode);
						source.invalidate();
						source.repaint();
					} else if (importDomActionImpl.dtdRulesManager.queryCanInsertAfter(dest.getParentNode(), dest, node)) {
						Node toImp = dest.getOwnerDocument().importNode(node.cloneNode(true), true);
						importDomActionImpl.logger.error("CAN INSERT AFTER to " + dest);
						tr = importDomActionImpl.documentManager.beginEdit();
						UtilDom.insertAfter(toImp, dest);
						importDomActionImpl.documentManager.commitEdit(tr);
						importDomActionImpl.addImported(node);
						rebuildSubTree(target, treeNode);
						source.invalidate();
						source.repaint();
					} else
						importDomActionImpl.utilMsg.msgError("edit.importdom.msg.error");

				} catch (DtdRulesManagerException ex) {
					importDomActionImpl.logger.error(ex.toString(), ex);
				}
			} catch (Exception ex) {
				importDomActionImpl.documentManager.rollbackEdit(tr);
			}

		} catch (UnsupportedFlavorException ex) {
			importDomActionImpl.logger.error(ex.toString(), ex);
			return false;
		} catch (IOException ex) {
			importDomActionImpl.logger.error(ex.toString(), ex);
			return false;
		}
		return false;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {
	}

	private boolean hasDomFlavor(DataFlavor[] flavors) {
		if (domFlavor != null)
			for (int i = 0; i < flavors.length; i++)
				if (flavors[i].equals(domFlavor))
					return true;
		return false;
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		return !source.equals(c) && hasDomFlavor(flavors);
	}

	protected Transferable createTransferable(JComponent c) {
		if (c instanceof JTree) {
			source = (JTree) c;
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) source.getLastSelectedPathComponent();
			Node node = (Node) treeNode.getUserObject();
			if (node != null)
				return new DomTransferable(node);
		}
		return null;
	}

	public int getSourceActions(JComponent c) {
		return COPY;
	}

	protected void rebuildSubTree(JTree tree, DefaultMutableTreeNode treeNode) {
		DefaultMutableTreeNode par = (DefaultMutableTreeNode) treeNode.getParent();
		Node node = (Node) treeNode.getUserObject();
		DefaultMutableTreeNode newTreeNode = importDomActionImpl.utilUI.createDefaultMutableTreeNode(node, true);
		par.insert(newTreeNode, par.getIndex(treeNode));
		treeNode.removeFromParent();
		DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
		treeModel.reload(par);
		TreePath path = new TreePath(newTreeNode.getPath());
		tree.setSelectionPath(path);
		tree.expandPath(path);
		tree.repaint();
	}

	public class DomTransferable implements Transferable {
		Node node;

		public DomTransferable(Node node) {
			this.node = node;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return node;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { domFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return domFlavor.equals(flavor);
		}
	}
}