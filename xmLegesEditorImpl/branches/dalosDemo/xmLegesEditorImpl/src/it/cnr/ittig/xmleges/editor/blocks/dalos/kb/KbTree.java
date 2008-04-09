package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.PivotOntoClass;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.TreeOntoClass;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class KbTree {
	
	private SynsetTree tree;
	private SynsetTree tmpTree;
	
	private TreeModel treeModel;
	
	private KbContainer kbc;
	
	private Set headClasses;
	
	private I18n i18n;
	
	private KbManagerImpl kbm;
	
	private String lang;
	
	public KbTree(KbContainer kbc, KbManagerImpl kbm, I18n in) {
		
		this.kbc = kbc;
		this.kbm = kbm;
		i18n = in;
		lang = kbc.getLanguage();
		
		headClasses = null;

		tree = null;
		tmpTree = null;
	}
		
	public SynsetTree getTree() {

		if(tree != null) {
			return tree;
		}
		
		headClasses = new HashSet();
		
		long t1 = System.currentTimeMillis();		
		System.out.println("Init tree...");

     	DefaultMutableTreeNode tmpRoot1 = new DefaultMutableTreeNode(" - ");
     	DefaultTreeModel tmpModel1 = new DefaultTreeModel(tmpRoot1);     	
     	DefaultMutableTreeNode tmpRoot2 = new DefaultMutableTreeNode(" - ");
     	DefaultTreeModel tmpModel2 = new DefaultTreeModel(tmpRoot2);     	
		
     	tree = new SynsetTree(tmpModel1, i18n);
		tree.setRootUserObject("CONSUMER LAW");
		tree.setRootVisible(true);
		
     	tmpTree = new SynsetTree(tmpModel2, i18n);	
		tmpTree.setRootUserObject("CONSUMER LAW");
		tmpTree.setRootVisible(true);
		
		OntModel om = KbModelFactory.getModel("domain", "micro", kbc.getLanguage());
		Collection topClasses = kbc.getTopClasses();
		for(Iterator i = topClasses.iterator(); i.hasNext();) {
			String ocUri = (String) i.next();			
			TreeOntoClass cl = getOntoClass(ocUri, om);
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(cl);
			((SynsetTree) tmpTree).addNode(newNode);
			//Prendi la relativa OntClass in *questo* OntModel:
			OntClass oc = om.getOntClass(ocUri);
			if(oc == null) {
				System.out.println("## ERROR ## top class is null!! " +
						"name: " + ocUri);
				return null;
			}
			expandClass(om, oc, newNode, tmpTree);
		}
		
		addSynsets();
		
		adjustTree();
		
		long t2 = System.currentTimeMillis();
		System.out.println("...tree loaded! (" + Long.toString(t2 - t1) + " ms)\n");
		
		return tree;
	}
	
	void setSelection(Synset syn) {
		
		treeModel = tree.getModel();
		Object root = treeModel.getRoot();
				
		collapseTree();
		
		if(syn == null) {
			return;
		}
		
		//Non va bene questo: cerca solo nei nodi e foglie gi� espansi e cerca
		//con un prefisso non con l'exact matching.
		//TreePath path = tree.getNextMatch(syn.toString(), 0, Position.Bias.Forward);

		Collection paths = new Vector();
		walkAndExpand(root, syn.toString(), paths);
		
		if(paths.size() == 0) {
			//System.out.println(">> ...not found!");
			return;
		}
		
		//System.out.println(">> founded! Selecting paths " + paths);

		//tree.setSelectionPaths((TreePath[]) paths.toArray(new TreePath[0]));
        //tree.setSelectionPath((TreePath) ((Vector) paths).get(0));
		tree.addSelectionPaths((TreePath[]) paths.toArray(new TreePath[0]));
	}
	
	private void collapseTree() {
		
		Object o = treeModel.getRoot();
		int  cc = treeModel.getChildCount(o);
		for( int i = 0; i < cc; i++) {
			Object child = treeModel.getChild(o, i);
			tree.collapsePath(new TreePath(((DefaultMutableTreeNode) child).getPath()));
		}
	}
	
	private void walkAndExpand(Object o, String key, Collection paths) {
		
		int  cc = treeModel.getChildCount(o);
		for( int i = 0; i < cc; i++) {
			Object child = treeModel.getChild(o, i);
			Object data = ((DefaultMutableTreeNode) child).getUserObject();
			if(data instanceof Synset &&
					key.equalsIgnoreCase((data.toString()))) {
				TreePath thisPath = new TreePath(((DefaultMutableTreeNode) child).getPath());
				tree.expandPath(thisPath);
				paths.add(thisPath);
				tree.scrollPathToVisible(thisPath);
			}			
			walkAndExpand(child, key, paths);
		}
	}
	
	private void expandClass(OntModel om, OntClass oc, 
			DefaultMutableTreeNode node, JTree tmpTree) {

		DefaultMutableTreeNode newNode = null;
		
		for(Iterator i = oc.listSubClasses(true); i.hasNext();) {
			OntClass c = (OntClass) i.next();
			
			//escludi le classi al di fuori della consumer law
			if(!c.isAnon() && !c.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
				continue;
			}
			
			if(!c.isAnon() && !c.isRestriction()) {
				String uri = c.getNameSpace() + c.getLocalName();

				TreeOntoClass cl = getOntoClass(uri, om);
				newNode = new DefaultMutableTreeNode(cl);
				if(node == null) {
					if(!headClasses.contains(cl)) {
						headClasses.add(cl);
						((SynsetTree) tmpTree).addNode(newNode);
					}
				} else {
					node.add(newNode);
				}
				expandClass(om, c, newNode, tmpTree);
			} else {
				expandClass(om, c, node, tmpTree);
			}			
		}		
	}
	
	private TreeOntoClass getOntoClass(String uri, OntModel om) {
		
		TreeOntoClass toc = kbm.getTreeClass(uri);
		if(toc == null) {
			//It must be added!
			OntClass oc = om.getOntClass(uri);
			toc = kbm.addTreeClass(uri, oc.getLocalName());
		} else {
//			System.out.println("getOntoClass() - class found: " + uri);
		}
		
		return toc;
	}
	
	private void addSynsets() {
		
		System.out.println("Adding synsets to tree...");
		
		TreeModel model = tmpTree.getModel();
		Object root = model.getRoot();
		walk(model, root);
		
		//Show also non-classified synsets?
		//addRemainingSynsets();		
	}	

	private void walk(TreeModel model, Object o) {
		
		int  cc = model.getChildCount(o);
		for( int i = 0; i < cc; i++) {
			Object child = model.getChild(o, i);
			Object data = ((DefaultMutableTreeNode) child).getUserObject();
			if( !(data instanceof TreeOntoClass) ) {
				if(data instanceof Synset) {
					//Sono i synset che vengono aggiunti!?
					continue;
				}
				if(data == null) {
					System.err.println(">> ERROR on walk() data is null!");
				} else {
					System.err.println(">> ERROR on walk() data:" + data);					
				}
				return;
			}
			
			TreeOntoClass toc = (TreeOntoClass) data;			
			Collection pocs = toc.getConcepts();
			for(Iterator pi = pocs.iterator(); pi.hasNext(); ) {
				PivotOntoClass poc = (PivotOntoClass) pi.next();
				Synset syn = poc.getTerm(lang);
				if(syn == null) {
					//Language 'lang' not supported within this 'poc' class
					//System.err.println("ERROR on walk() - syn is null - poc: " + poc);
					continue;
				}
				toc.addTerm(syn);
				DefaultMutableTreeNode newNode =
					new DefaultMutableTreeNode(syn);
				((DefaultMutableTreeNode) child).add(newNode);
			}
			
			walk(model, child);
		}
	} 

	private void adjustTree() {
		
		TreeModel model = tmpTree.getModel();
		Object root = model.getRoot();

		System.out.println("Sorting tree...");
		TreeModel model2 = tree.getModel();
		Object root2 = model2.getRoot();
		sortedWalk(model, root, model2, root2);
		
		//Delete temp tree...
		tmpTree = null;

		System.out.println("Adjusting tree...");
		adjustLeaf(model2, root2);		
	}
	
	private void sortedWalk(TreeModel model1, Object node1, 
			TreeModel model2, Object node2) {
		
		Vector sortedChildren = new Vector();
		for(int i = 0; i < model1.getChildCount(node1); i++) {
			addSortedNode((DefaultMutableTreeNode) model1.getChild(node1, i),
					sortedChildren);
		}
		
		for(Iterator i = sortedChildren.iterator(); i.hasNext();) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) i.next();
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(node.getUserObject());
			if(model2.getRoot().equals(node2)) {
				tree.addNode(newNode);
			} else {
				((DefaultMutableTreeNode) node2).add(newNode);
			}
			sortedWalk(model1, node, model2, newNode);
		}
	}
	
	private void adjustLeaf(TreeModel model, Object o) {
			
		int  cc = model.getChildCount(o);
		for(int i = 0; i < cc; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) model.getChild(o, i);
			Object data = child.getUserObject();
			if(model.isLeaf(child) && data instanceof TreeOntoClass) {
				//Aggiungi un nodo fittizio
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("(empty)");
				child.add(newNode);
			} else {
				adjustLeaf(model, (Object) child);
			}
		}
	} 		

	private void addSortedNode(DefaultMutableTreeNode node, Vector sortedNodes) {
		
		if(sortedNodes.size() == 0) {
			sortedNodes.add(node);
			return;
		}
		boolean ins = false;
		Object userObj = node.getUserObject();
		if(userObj instanceof TreeOntoClass) {
			for(int i = 0; i < sortedNodes.size(); i++) {
				DefaultMutableTreeNode item = (DefaultMutableTreeNode) sortedNodes.get(i);
				Object itemObj = item.getUserObject();
				if(itemObj instanceof TreeOntoClass) {					
					if(itemObj.toString().compareToIgnoreCase(userObj.toString()) < 0) {
						continue;
					}
					if(itemObj.toString().compareToIgnoreCase(userObj.toString()) > 0) {
						sortedNodes.add(i, node);					
						ins = true;
						break;
					}
				} else {
					sortedNodes.add(i, node);
					ins = true;
					break;
				}				
			}
		}
		if(userObj instanceof Synset) {
			for(int i = 0; i < sortedNodes.size(); i++) {
				DefaultMutableTreeNode item = (DefaultMutableTreeNode) sortedNodes.get(i);
				Object itemObj = item.getUserObject();
				if(itemObj instanceof TreeOntoClass) {
					continue;
				} else {
					if(itemObj.toString().compareToIgnoreCase(userObj.toString()) < 0) {
						continue;
					}
					if(itemObj.toString().compareToIgnoreCase(userObj.toString()) > 0) {
						sortedNodes.add(i, node);					
						ins = true;
						break;
					}
				}
			}
			
		}

		if(!ins) {
			//Inserisci alla fine del vettore
			sortedNodes.add(node);
		}		
	}
}
