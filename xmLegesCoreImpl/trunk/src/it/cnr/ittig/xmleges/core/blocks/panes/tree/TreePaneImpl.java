package it.cnr.ittig.xmleges.core.blocks.panes.tree;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.DomEdit;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.PaneStatusChangedEvent;
import it.cnr.ittig.xmleges.core.services.panes.tree.TreePane;
import it.cnr.ittig.xmleges.core.services.panes.tree.TreePaneCellRenderer;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.clipboard.UtilClipboard;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Properties;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.tree.TreePane</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>event-manager</li>
 * <li>document-manager</li>
 * <li>selection-manager</li>
 * <li>bars</li>
 * <li>util-ui</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>, <a
 *         href="mailto:t.paba@onetech.it">Tommaso Paba </a>
 */
public class TreePaneImpl implements TreePane, EventManagerListener, Loggable, Serviceable, Initializable, Startable {
	Logger logger;

	PreferenceManager preferenceManager;

	EventManager eventManager;

	DocumentManager documentManager;

	DtdRulesManager dtdRulesManager;

	UtilRulesManager utilRulesManager;

	SelectionManager selectionManager;

	Bars bars;

	UtilUI utilUi;

	JPopupMenu popupMenu;

	String name;

	JTree tree;

	DefaultTreeModel treeModel;

	TreePaneCellRendererImpl cellRenderer;

	boolean localSelection = false;

	JPanel panel = new JPanel(new BorderLayout());

	JToolBar toolbar;

	JScrollPane scroll = new JScrollPane();

	TreeFindIterator treeFindIterator = new TreeFindIterator(this);

	TreePaneAction treePaneAction[] = new TreePaneAction[] { new ParentAction(), new PrevSibAction(), new NextSibAction(), new FirstChildAction(),
			new ExpandAllAction(), new CollapseAllAction(), new ExpandSelectedAction() };

	JToggleButton expandSelectedButton;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		utilUi = (UtilUI) serviceManager.lookup(UtilUI.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		tree = new JTree(new DefaultMutableTreeNode(""));
		scroll.setViewportView(tree);
		panel.add(scroll, BorderLayout.CENTER);
		// tree.addTreeSelectionListener(this);
		cellRenderer = new TreePaneCellRendererImpl();
		tree.setCellRenderer(cellRenderer);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentChangedEvent.class);
		popupMenu = bars.getPopup(false);
		tree.addMouseListener(new TreePaneMouseAdapter());
		tree.setTransferHandler(new TreeTransferHandler(this));
		tree.setDragEnabled(true);

		panel.setDoubleBuffered(true);
		scroll.setDoubleBuffered(true);
		tree.setDoubleBuffered(true);

		toolbar = new JToolBar();
		panel.add(toolbar, BorderLayout.SOUTH);
		int i = 0;
		toolbar.add(utilUi.applyI18n("panes.tree.action.parent", treePaneAction[i++]));
		toolbar.add(utilUi.applyI18n("panes.tree.action.prevsib", treePaneAction[i++]));
		toolbar.add(utilUi.applyI18n("panes.tree.action.nextsib", treePaneAction[i++]));
		toolbar.add(utilUi.applyI18n("panes.tree.action.firstchild", treePaneAction[i++]));
		toolbar.addSeparator();
		toolbar.add(utilUi.applyI18n("panes.tree.action.expandall", treePaneAction[i++]));
		toolbar.add(utilUi.applyI18n("panes.tree.action.collapseall", treePaneAction[i++]));
		expandSelectedButton = new JToggleButton(treePaneAction[i++]);
		expandSelectedButton.setName("panes.tree.action.collapsesel");
		toolbar.add(utilUi.applyI18n(expandSelectedButton));
		enableActions();
	}

	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		expandSelectedButton.setSelected(Boolean.valueOf(p.getProperty("expand.selected.only", "True")).booleanValue());
	}

	public void stop() throws Exception {
		Properties p = new Properties();
		p.put("expand.selected.only", "" + expandSelectedButton.isSelected());
		preferenceManager.setPreference(getClass().getName(), p);
	}

	protected void enableActions() {
		if (documentManager.isEmpty())
			for (int i = 0; i < treePaneAction.length; i++)
				treePaneAction[i].setEnabled(false);
		else
			for (int i = 0; i < treePaneAction.length; i++)
				treePaneAction[i].checkEnable();
	}

	// ///////////////////////////////////////// EventManagerListener Interface
	public synchronized void manageEvent(EventObject event) {
		if (event instanceof DocumentChangedEvent) {
			DocumentChangedEvent e = (DocumentChangedEvent) event;
			manageEditTransaction(e.getTransaction());
		} else if (event instanceof SelectionChangedEvent) {
			SelectionChangedEvent e = (SelectionChangedEvent) event;
			if (e.isActiveNodeChanged())
				selectNode(e.getActiveNode(), false);
			else if (e.isSelectedNodesChanged())
				selectNodes(e.getSelectedNodes(), false);
		} else if (event instanceof DocumentOpenedEvent) {
			reload();
		} else if (event instanceof DocumentClosedEvent) {
			reload();
		}
	}

	protected void manageEditTransaction(EditTransaction tr) {

		DomEdit[] edits = tr.getEdits();
		Vector v = new Vector();
		// DefaultMutableTreeNode nodeToSelect = null;

		for (int i = 0; i < edits.length; i++) {
			if (edits[i].getType() == DomEdit.SUBTREE_MODIFIED && edits[i].getNode().getParentNode() != null)
				v.addElement(edits[i].getNode());
		}

		if (v.size() > 0) {
			Node[] nodes = new Node[v.size()];
			v.copyInto(nodes);
			Node n = UtilDom.getCommonAncestor(nodes);

			if (logger.isDebugEnabled())
				logger.debug("updated common ancestor: " + UtilDom.getPathName(n) + "; depth: " + UtilDom.getPathIndexes(n).length);

			if (UtilDom.getPathIndexes(n).length > 1) {
				DefaultMutableTreeNode ins = utilUi.createDefaultMutableTreeNode(n, true);
				DefaultMutableTreeNode rep = getTreeNode(n);
				DefaultMutableTreeNode par = getTreeNode(n.getParentNode());

				par.insert(ins, par.getIndex(rep));
				par.remove(rep);
				logger.debug("reload parziale");
				treeModel.reload(ins);
			} else {
				logger.debug("reloaded all");
				reload();
			}
			selectNode(selectionManager.getActiveNode(), false);
		}
	}

	protected DefaultMutableTreeNode getTreeNode(Node node) {
		int[] indexes = UtilDom.getPathIndexes(node);
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeModel.getRoot();
		try {
			for (int i = 0; i < indexes.length; i++) {
				if (logger.isDebugEnabled())
					logger.debug("indexes[" + i + "]=" + indexes[i]);
				treeNode = (DefaultMutableTreeNode) treeNode.getChildAt(indexes[i]);
			}
		} catch (Exception ex) {
			return null;
		}
		return treeNode;
	}

	Node[] selNode = new Node[1];

	protected/* synchronized */void selectNode(Node node, boolean local) {
		if (node != null) {
			selNode[0] = node;
			selectNodes(selNode, local);
		}
	}

	protected/* synchronized */void selectNodes(Node[] nodes, boolean local) {
		// TreePath[] paths = new TreePath[nodes.length];
		Vector pathsVector = new Vector();
		for (int i = 0; i < nodes.length; i++) {
			DefaultMutableTreeNode treeNode = getTreeNode(nodes[i]);
			if (treeNode != null)
				// paths[i] = new TreePath(treeNode.getPath());
				pathsVector.add(new TreePath(treeNode.getPath()));
		}
		TreePath[] paths = new TreePath[pathsVector.size()];
		pathsVector.copyInto(paths);
		if (paths.length > 0) {
			tree.setSelectionPaths(paths);
			tree.fireTreeExpanded(paths[0]);
			if (local)
				if (paths.length == 1)
					selectionManager.setActiveNode(this, nodes[0]);
				else
					selectionManager.setSelectedNodes(this, nodes);
			eventManager.fireEvent(new PaneStatusChangedEvent(this, this));
			enableActions();
			expandSelectedOnly();
			tree.scrollPathToVisible(paths[0]);
			tree.repaint();
			// SwingUtilities.getRoot(tree).repaint();
		}
	}

	// ////////////////////////////////////////////////////// TreePane Interface
	public void setName(String name) {
		this.name = name;
	}

	public void addRenderer(TreePaneCellRenderer renderer) {
		cellRenderer.addRenderer(renderer);
	}

	public void removeRenderer(TreePaneCellRenderer renderer) {
		cellRenderer.removeRenderer(renderer);
	}

	// ////////////////////////////////////////////////////////// Pane Interface
	public String getName() {
		return this.name;
	}

	public Component getPaneAsComponent() {
		return panel;
	}

	public boolean canCut() {
		return canDelete();
	}

	public void cut() {
		Node n = selectionManager.getActiveNode();
		UtilClipboard.set(n);
		deleteNode(n);
	}

	public boolean canCopy() {
		return selectionManager.getActiveNode() != null;
	}

	public void copy() {
		UtilClipboard.set(selectionManager.getActiveNode());
	}

	public boolean canPaste() {
		if (UtilClipboard.hasNode())
			try {
				Node selNode = selectionManager.getActiveNode();
				Node node = UtilClipboard.getAsNode();
				// TODO altre possibilita - es insertBefore/After
				return dtdRulesManager.queryCanAppend(selNode, node) || dtdRulesManager.queryCanInsertAfter(selNode.getParentNode(), selNode, node)
						|| dtdRulesManager.queryCanAppend(selNode.getParentNode(), node);
			} catch (Exception ex) {
				return false;
			}
		else
			return false;
	}

	public void paste() {
		Node selNode = selectionManager.getActiveNode();
		Node node = UtilClipboard.getAsNode().cloneNode(true);
		try {
			if (dtdRulesManager.queryCanAppend(selNode, node)) {
				selNode.appendChild(node);
				UtilDom.mergeTextNodes(selNode);
				selectNode(node, true);
			} else if (dtdRulesManager.queryCanInsertAfter(selNode.getParentNode(), selNode, node)) {
				UtilDom.insertAfter(node, selNode);
				UtilDom.mergeTextNodes(selNode.getParentNode());
				selectNode(node, true);
			} else if (dtdRulesManager.queryCanAppend(selNode.getParentNode(), node)) {
				selNode.getParentNode().appendChild(node);
				UtilDom.mergeTextNodes(selNode.getParentNode());
				selectNode(node, true);
			}
		} catch (Exception ex) {
		}
	}

	public boolean canPasteAsText() {
		try {
			Node selNode = selectionManager.getActiveNode();
			return (UtilClipboard.hasNode() && dtdRulesManager.queryTextContent(selNode));
		} catch (Exception ex) {
			return false;
		}
	}

	public void pasteAsText() {
		try {
			Node selNode = selectionManager.getActiveNode();
			Node node = UtilClipboard.getAsNode();
			if (dtdRulesManager.queryTextContent(selNode)) {
				String oldValue = selNode.getNodeValue();
				selNode.setNodeValue(oldValue + node.getNodeValue());
			}
		} catch (Exception ex) {
		}
	}

	public boolean canDelete() {
		try {
			Node n = selectionManager.getActiveNode();
			return dtdRulesManager.queryCanDelete(n.getParentNode(), n);
		} catch (Exception ex) {
			return false;
		}
	}

	public void delete() {
		TreePath path = tree.getSelectionPath();
		DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
		Node node = (Node) n.getUserObject();
		deleteNode(node);
	}

	private void deleteNode(Node node) {

		// TODO verificare con DtdRulesManager

		Node nodeToSelect = node.getPreviousSibling();
		if (nodeToSelect == null)
			nodeToSelect = node.getNextSibling();
		if (nodeToSelect == null)
			nodeToSelect = node.getParentNode();

		Node parent = node.getParentNode();
		parent.removeChild(node);
		UtilDom.mergeTextNodes(parent);

		if (nodeToSelect != null) {
			selectionManager.setActiveNode(this, nodeToSelect);
			selectNode(selectionManager.getActiveNode(), false);
		}
	}

	public boolean canPrint() {
		return false;
	}

	public Component getComponentToPrint() {
		return null;
	}

	public boolean canFind() {
		return documentManager.getRootElement() != null;
	}

	public FindIterator getFindIterator() {
		return treeFindIterator;
	}

	public void reload() {
		if (documentManager.isEmpty())
			treeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
		else
			treeModel = utilUi.createDefaultTreeModel(documentManager.getRootElement());
		tree.setModel(treeModel);
	}

	SelectionManager getSelectionManager() {
		return this.selectionManager;
	}

	Logger getLogger() {
		return this.logger;
	}

	// ///////////////////////////////////////////////////////// Toolbar Actions
	protected abstract class TreePaneAction extends AbstractAction {
		public abstract void checkEnable();

		public abstract void doAction();

		public void actionPerformed(ActionEvent e) {
			doAction();
			enableActions();
		}
	}

	protected class ParentAction extends TreePaneAction {
		public void checkEnable() {
			boolean enable = false;
			TreePath treePath = tree.getSelectionPath();
			if (treePath != null)
				enable = ((TreeNode) treePath.getLastPathComponent()).getParent() != null;
			setEnabled(enable);
		}

		public void doAction() {
			tree.setSelectionPath(tree.getSelectionPath().getParentPath());
		}
	}

	protected class FirstChildAction extends TreePaneAction {
		public void checkEnable() {
			boolean enable = false;
			TreePath treePath = tree.getSelectionPath();
			if (treePath != null)
				try {
					enable = ((TreeNode) treePath.getLastPathComponent()).getChildAt(0) != null;
				} catch (ArrayIndexOutOfBoundsException exc) {
					enable = false;
				}
			setEnabled(enable);
		}

		public void doAction() {
			TreePath treePath = tree.getSelectionPath();
			DefaultMutableTreeNode n = (DefaultMutableTreeNode) treePath.getLastPathComponent();
			Node node;
			try {
				node = (Node) n.getUserObject();
				selectNode(node.getFirstChild(), true);
			} catch (ClassCastException exc) {
			}
		}
	}

	protected class PrevSibAction extends TreePaneAction {
		public void checkEnable() {
			boolean enable = false;
			TreePath treePath = tree.getSelectionPath();
			if (treePath != null) {
				TreeNode cur = (TreeNode) treePath.getLastPathComponent();
				TreeNode par = cur.getParent();
				if (par != null)
					enable = par.getIndex(cur) > 0;
			}
			setEnabled(enable);
		}

		public void doAction() {
			TreePath path = tree.getSelectionPath();
			TreeNode cur = (TreeNode) path.getLastPathComponent();
			TreeNode par = cur.getParent();
			TreeNode sib = par.getChildAt(par.getIndex(cur) - 1);
			tree.setSelectionPath(path.getParentPath().pathByAddingChild(sib));
		}
	}

	protected class NextSibAction extends TreePaneAction {
		public void checkEnable() {
			boolean enable = false;
			TreePath treePath = tree.getSelectionPath();
			if (treePath != null) {
				TreeNode cur = (TreeNode) treePath.getLastPathComponent();
				TreeNode par = cur.getParent();
				if (par != null)
					enable = par.getIndex(cur) != par.getChildCount() - 1;
			}
			setEnabled(enable);

		}

		public void doAction() {
			TreePath path = tree.getSelectionPath();
			TreeNode cur = (TreeNode) path.getLastPathComponent();
			TreeNode par = cur.getParent();
			TreeNode sib = par.getChildAt(par.getIndex(cur) + 1);
			tree.setSelectionPath(path.getParentPath().pathByAddingChild(sib));
		}
	}

	protected class ExpandAllAction extends TreePaneAction {
		public void checkEnable() {
			setEnabled(isNonLeafSelected());
		}

		public void doAction() {
			TreePath[] paths = tree.getSelectionPaths();
			for (int i = 0; i < paths.length; i++)
				expandPathDeep(paths[i]);
			tree.setSelectionPath(paths[0]);
		}
	}

	protected class CollapseAllAction extends TreePaneAction {
		public void checkEnable() {
			setEnabled(isNonLeafSelected());
		}

		public void doAction() {
			TreePath[] paths = tree.getSelectionPaths();
			for (int i = 0; i < paths.length; i++)
				collapsePathDeep(tree.getSelectionPath());
			tree.setSelectionPath(paths[0]);
		}
	}

	protected class ExpandSelectedAction extends TreePaneAction {
		public void checkEnable() {
			setEnabled(!documentManager.isEmpty());
		}

		public void doAction() {
			expandSelectedOnly();
		}
	}

	protected class TrimNodesAction extends TreePaneAction {
		public void checkEnable() {
			setEnabled(!isNonLeafSelected());
		}

		public void doAction() {
			EditTransaction tr = null;
			try {
				Node n = selectionManager.getActiveNode();
				if (n != null) {
					documentManager.beginEdit();
					UtilDom.trimTextNode(n, true);
					UtilDom.mergeTextNodes(n, true);
					if (tr.getEdits().length > 0)
						documentManager.commitEdit(tr);
					else
						documentManager.rollbackEdit(tr);

				}
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
				documentManager.rollbackEdit(tr);
			}
			expandSelectedOnly();
		}
	}

	protected boolean isNonLeafSelected() {
		boolean enable = false;
		TreePath[] treePaths = tree.getSelectionPaths();
		if (treePaths != null)
			for (int i = 0; i < treePaths.length; i++)
				if (!((TreeNode) treePaths[i].getLastPathComponent()).isLeaf()) {
					enable = true;
					break;
				}
		return enable;
	}

	protected void expandPathDeep(TreePath path) {
		TreeNode node = (TreeNode) path.getLastPathComponent();
		if (node.isLeaf())
			tree.fireTreeExpanded(path);
		else
			for (Enumeration en = node.children(); en.hasMoreElements();)
				expandPathDeep(path.pathByAddingChild(en.nextElement()));
	}

	protected void collapsePathDeep(TreePath path) {
		TreeNode node = (TreeNode) path.getLastPathComponent();
		if (node.isLeaf())
			return;
		else
			for (Enumeration en = node.children(); en.hasMoreElements();)
				collapsePathDeep(path.pathByAddingChild(en.nextElement()));
		tree.collapsePath(path);
		// tree.fireTreeCollapsed(path);
	}

	protected void expandSelectedOnly() {
		if (expandSelectedButton.isSelected()) {
			TreePath[] paths = tree.getSelectionPaths();
			Enumeration en = tree.getExpandedDescendants(new TreePath(treeModel.getRoot()));
			while (en != null && en.hasMoreElements()) {
				TreePath path = (TreePath) en.nextElement();
				tree.collapsePath(path);
			}
			for (int i = 0; i < paths.length; i++)
				tree.fireTreeExpanded(paths[i]);
			tree.setSelectionPath(paths[0]);
		}
	}

	protected class TreePaneMouseAdapter extends MouseAdapter {
		// JMenu insertBefore = null;
		// JMenu insertAfter = null;
		// JMenu insertNode = null;
		JMenu insert = null;

		boolean firstTime = true;

		public void mouseClicked(MouseEvent e) {
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			if (path != null) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
				Node node = null;
				try {
					node = (Node) n.getUserObject();
					// TODO check nodi selezionati
					selectNode(node, true);
				} catch (ClassCastException exc) {
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					updateMenus(node);
					popupMenu.show(tree, e.getX(), e.getY());
				}
			}
		}

		protected void updateMenus(Node node) {
			if (firstTime) {
				firstTime = false;
				popupMenu.addSeparator();
			}
			if (insert != null) {
				// popupMenu.remove(insertBefore);
				// popupMenu.remove(insertAfter);
				// popupMenu.remove(insertNode);
				popupMenu.remove(insert);
			}
			// TODO lasciare solo il menu insert ed eliminare insertBefore, insertAfter,
			// insetNode
			insert = utilRulesManager.createMenuInsert(node);
			// insertBefore = utilRulesManager.createMenuInsertBefore(node);
			// insertAfter = utilRulesManager.createMenuInsertAfter(node);
			// insertNode = utilRulesManager.createMenuInsertNode(node);
			popupMenu.add(insert);
			// popupMenu.add(insertBefore);
			// popupMenu.add(insertAfter);
			// popupMenu.add(insertNode);
		}
	}
}
