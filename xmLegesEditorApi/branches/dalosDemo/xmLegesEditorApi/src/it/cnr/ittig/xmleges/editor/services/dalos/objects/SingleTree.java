package it.cnr.ittig.xmleges.editor.services.dalos.objects;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class SingleTree extends JTree {
	/*
	 * Contenitore della struttura "tree".
	 * Estende JTree, ma gestisce anche il TreeModel (root, ecc...).
	 */

	DefaultTreeModel treeModel;
	DefaultMutableTreeNode root;

	public SingleTree(DefaultTreeModel model) {
		
		super(model);

		treeModel = model;
		root = (DefaultMutableTreeNode) treeModel.getRoot();
		
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setRootVisible(false);
	}

	public void addNode(DefaultMutableTreeNode child) {
		if(root != null) {
			treeModel.insertNodeInto(child, root, root.getChildCount());
			this.scrollPathToVisible(new TreePath(child.getPath()));
		}
	}

	public void removeChildren() {
		root.removeAllChildren();
	}
	
	public void setRootUserObject(String obj) {
		root.setUserObject(obj);
	}
	
	public void reloadModel() {
		treeModel.reload();
	}
	
	public void expand(DefaultMutableTreeNode node) {
		TreePath treePath = new TreePath(node.getPath());
		this.expandPath(treePath);
		//this.scrollPathToVisible(treePath);
	}

	/*
	 * Espandi tutti i nodi nella child array list di 'node'.
	 */
	public void expandChilds(DefaultMutableTreeNode node) {
		if(node == null) {
			return;
		}
		int size = node.getChildCount();
		if(size == 0) {
			return;
		}
		DefaultMutableTreeNode child = null;
		TreePath treePath = null;
		for(int i = 0; i < size; i++) {
			child = (DefaultMutableTreeNode) node.getChildAt(i);
			treePath = new TreePath(child.getPath());
			this.expandPath(treePath);
		}
		this.scrollPathToVisible(treePath);
	}
}
