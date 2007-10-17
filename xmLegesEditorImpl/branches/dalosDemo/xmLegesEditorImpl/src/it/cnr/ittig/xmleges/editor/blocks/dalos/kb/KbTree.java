package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.OntoClass;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

public class KbTree {
	
	private SynsetTree tree;
	private SynsetTree tmpTree;
	
	private TreeModel treeModel;
	
	private KbContainer kbc;
	
	private Set topClasses;
	private Map linked;
	
	private I18n i18n;
	
	public KbTree(KbContainer k, I18n in) {
		
		kbc = k;
		i18n = in;		
		topClasses = null;
		linked = null;
		
		tree = null;
		tmpTree = null;
	}
	
	public SynsetTree getTree() {

		if(tree != null) {
			return tree;
		}
		
		topClasses = new HashSet();
		
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
		
		OntModel om = kbc.getModel("domain", "micro");
		if(!KbConf.MERGE_DOMAIN) {			
			OntClass ocRoot = om.getOntClass(KbConf.ROOT_CLASS);
			expandClass(ocRoot, null, tmpTree);
		} else {
			Collection topClasses = kbc.getTopClasses();
			for(Iterator i = topClasses.iterator(); i.hasNext();) {
				String ocName = (String) i.next();
				OntoClass cl = new OntoClass(ocName);
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(cl);
				((SynsetTree) tmpTree).addNode(newNode);
				//Prendi la relativa OntClass in *questo* OntModel:
				String ocNameNs = KbConf.DOMAIN_ONTO_NS + ocName;				
				OntClass oc = om.getOntClass(ocNameNs);
				if(oc == null) {
					System.out.println("## ERROR ## top class is null!! " +
							"name: " + ocNameNs);
					return null;
				}
				expandClass(oc, newNode, tmpTree);
			}
		}
		
		addSynsets();
		
		adjustTree();
		
		long t2 = System.currentTimeMillis();
		System.out.println("...tree loaded! (" + Long.toString(t2 - t1) + " ms)\n");
		
		return tree;
	}
	
	boolean setSelection(Synset syn) {
		
		treeModel = tree.getModel();
		Object root = treeModel.getRoot();
		
		collapseTree();
		
		System.out.println(">> Trying to find and select \"" + syn + "\" within tree...");		
			
		//Non va bene questo: cerca solo nei nodi e foglie già espansi e cerca
		//con un prefisso non con l'exact matching.
		//TreePath path = tree.getNextMatch(syn.toString(), 0, Position.Bias.Forward);

		Collection paths = new Vector();
		walkAndExpand(root, syn.toString(), paths);
		
		if(paths.size() == 0) {
			//System.out.println(">> ...not found!");
			return false;
		}
		
		//System.out.println(">> founded! Selecting paths " + paths);

		//tree.setSelectionPaths((TreePath[]) paths.toArray(new TreePath[0]));
        //tree.setSelectionPath((TreePath) ((Vector) paths).get(0));
		tree.addSelectionPaths((TreePath[]) paths.toArray(new TreePath[0]));
		
		return true;
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
				//TreePath exPath = new TreePath(((DefaultMutableTreeNode) child).getParent());
				TreePath thisPath = new TreePath(((DefaultMutableTreeNode) child).getPath());
				//System.out.println(">> Expanding path " + exPath);
				tree.expandPath(thisPath);
				paths.add(thisPath);
				tree.scrollPathToVisible(thisPath);
				//tree.addSelectionPath(thisPath);
			}			
			walkAndExpand(child, key, paths);
		}
	}
	
	private void expandClass(OntClass oc, DefaultMutableTreeNode node, JTree tmpTree) {
		//Problema: se una classe non ha sotto-classi e non ha synset
		//viene visualizzata come foglia!
		//Aggiungere dei children fittizi? (empty) ?
		
		//System.out.println("Expanding " + oc.getNameSpace() + oc.getLocalName() + "...");
		DefaultMutableTreeNode newNode = null;
		
		for(Iterator i = oc.listSubClasses(true); i.hasNext();) {
			OntClass c = (OntClass) i.next();
			
			//escludi le classi al di fuori della consumer law
			if(!c.isAnon() && !c.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
				continue;
			}
			
			if(!c.isAnon() && !c.isRestriction()) {
				String localName = c.getLocalName();

				OntoClass cl = new OntoClass(localName);
				newNode = new DefaultMutableTreeNode(cl);
				if(node == null) {
					if(!topClasses.contains(cl)) {
						topClasses.add(cl);
						((SynsetTree) tmpTree).addNode(newNode);
					}
				} else {
					node.add(newNode);
				}
				expandClass(c, newNode, tmpTree);
			} else {
				expandClass(c, node, tmpTree);
			}			
		}		
	}
	
	private void addSynsets() {
		
		System.out.println("Adding synsets to tree...");
		OntModel mapModel = kbc.getModel("mapping", "micro");
		
		linked = new HashMap(256, 0.70f);
		
		Resource subj = null;
		RDFNode obj = null;
		for(Iterator i = mapModel.listStatements(subj, RDF.type, obj); i.hasNext();) {
			Statement stm = (Statement) i.next();
			Resource thisSubj = stm.getSubject();
			RDFNode thisObjNode = stm.getObject();
			if(!thisObjNode.isResource()) {
				continue;				
			}
			Resource thisObj = (Resource) thisObjNode;
			if(thisObj.isAnon() ||
					!thisObj.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
				continue;
			}
//			System.out.println("Adding to linked: " + thisObj.getLocalName() +
//					" --> " + thisSubj.getLocalName());
			addLink(thisObj, thisSubj);
		}
		
		TreeModel model = tmpTree.getModel();
		Object root = model.getRoot();
		walk(model, root);
		
		//Non aggiungere per il momento i synset non classificati,
		//sono visibili nell'elenco dei synset totali
		//addRemainingSynsets();
		
		linked = null;
	}
	
	private void addLink(Resource oc, Resource syn) {
	
		Vector syns = null;
		String key = oc.getLocalName();
		syns = (Vector) linked.get(key);
		if(syns == null) {
			syns = new Vector();
			linked.put(key, syns);
		}
		syns.add(syn.getLocalName());
	}
	
	private void walk(TreeModel model, Object o) {
		
		int  cc = model.getChildCount(o);
		for( int i = 0; i < cc; i++) {
			Object child = model.getChild(o, i);
			Object data = ((DefaultMutableTreeNode) child).getUserObject();
			boolean isOntoClass = false;
			if(data instanceof OntoClass) {
				isOntoClass = true;
			} else  {
				//Sono i synset che vengono aggiunti??
				if(data instanceof Synset) {
					continue;
				}
				System.out.println(">>>>> " + data + " - " + isOntoClass);
				return;
			}
			
			Vector syns = (Vector) linked.get(data.toString());
			if(syns != null) {
				for(int k = 0; k < syns.size(); k++) {
					//System.out.println("Asking object mapped by " + syns.get(k));
					Synset syn = (Synset) kbc.synsets.get(syns.get(k));
					//System.out.println("++ Adding " + syn + " to " + child.toString());
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(syn);
					((DefaultMutableTreeNode) child).add(newNode);
				}
			}
			walk(model, child);
		}
	} 

	private void addRemainingSynsets() {
		
		Set addedSyns = new HashSet();
		Collection values = linked.values();
		for(Iterator i = values.iterator(); i.hasNext();) {
			Vector item = (Vector) i.next();
			for(int k = 0; k < item.size(); k++) {
				addedSyns.add(item.get(k));
			}
		}
		
		Collection syns = kbc.synsets.values(); 
		System.out.println("# synsets: " + syns.size() + 
				" (already classified: " + addedSyns.size() + ")");
		for(Iterator i = syns.iterator(); i.hasNext();) {
			Synset syn = (Synset) i.next();
			String localName = syn.getURI().substring(syn.getURI().lastIndexOf('#') + 1);
			if(!addedSyns.contains(localName)) {
				//System.out.println("++ Adding synset to root node: " + syn );
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(syn);
				//((DefaultMutableTreeNode) root).add(newNode); //Usare se il nodo non ï¿½ la root
				tmpTree.addNode(newNode);
			}
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
//			if(!(data instanceof Synset)) {
//				System.out.println("@@ " + child + " " + model.isLeaf(child) +
//						" " + (data instanceof OntoClass));
//			}
			if(model.isLeaf(child) && data instanceof OntoClass) {
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
		if(userObj instanceof OntoClass) {
			for(int i = 0; i < sortedNodes.size(); i++) {
				DefaultMutableTreeNode item = (DefaultMutableTreeNode) sortedNodes.get(i);
				Object itemObj = item.getUserObject();
				if(itemObj instanceof OntoClass) {					
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
				if(itemObj instanceof OntoClass) {
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

