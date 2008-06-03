package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.PaneFocusGainedEvent;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.TreeOntoClass;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangChangedEvent;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.synset.SynsetRelSemanticPane;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JScrollBar;
import javax.swing.tree.DefaultMutableTreeNode;

public class SynsetRelSemanticPaneImpl extends SynsetRelPane 
implements EventManagerListener, Loggable, Serviceable, 
Initializable, Startable, SynsetRelSemanticPane {
	
	public void initialize() throws Exception {

		tabbedPaneName = "editor.panes.dalos.semanticrelation";

		eventManager.addListener(this, PaneFocusGainedEvent.class);
		eventManager.addListener(this, LangChangedEvent.class);
		
		super.initialize();
	}

	void focusGainedEvent(Synset syn) {

		if(syn == null) {
			return;
		}
		
		//kbManager.addSemanticProperties(syn);
		showSemanticRelations(syn);
	}
	
	void focusGainedEvent(TreeOntoClass toc) {

		if(toc == null) {
			return;
		}

		showSemanticRelations(toc);
	}

	protected void updateObserver(TreeOntoClass toc) {
		
	    if(this.getPaneAsComponent().isShowing()){
	    	clearTree();
	    	focusGainedEvent(toc);	
	    }
	}
	
	//TOC selected
	void showSemanticRelations(TreeOntoClass toc) {
	
		clearTree();
		tree.setRootUserObject(toc.toString());
		tree.setRootVisible(true);

		analyzeToc(toc);

		JScrollBar vbar = scrollPane.getVerticalScrollBar();
		vbar.setValue(vbar.getMinimum());
		JScrollBar hbar = scrollPane.getHorizontalScrollBar();
		hbar.setValue(hbar.getMinimum());
	}
	
	//SYN selected
	void showSemanticRelations(Synset syn) {
				
		//DefaultMutableTreeNode top = null; //, node = null;

		clearTree();
		tree.setRootUserObject(syn.toString());
		tree.setRootVisible(true);

		Collection tocs = kbManager.getSortedTreeClass(syn);
		
		for(Iterator i = tocs.iterator(); i.hasNext(); ) {
			TreeOntoClass toc = (TreeOntoClass) i.next();
			analyzeToc(toc);
		}
		
		//tree.expandChilds(top);
		
		JScrollBar vbar = scrollPane.getVerticalScrollBar();
		vbar.setValue(vbar.getMinimum());
		JScrollBar hbar = scrollPane.getHorizontalScrollBar();
		hbar.setValue(hbar.getMinimum());
	}
	
	private void analyzeToc(TreeOntoClass toc) {
		
		Map semanticProperties = toc.getSemanticProperties();
		
		//DEBUG
//		System.out.println("Analyzing TOC...");
//		for(Iterator i = semanticProperties.keySet().iterator(); i.hasNext(); ) {
//			String key = (String) i.next();
//			System.out.println(">>> KEY: " + key);
//			Collection tocs = (Collection) semanticProperties.get(key);
//			for(Iterator k = tocs.iterator(); k.hasNext(); ) {
//				TreeOntoClass otoc = (TreeOntoClass) k.next();
//				System.out.println(">>>>>> OTOC: " + otoc);
//			}
//		}

		DefaultMutableTreeNode top = new DefaultMutableTreeNode(toc);
		tree.addNode(top);
		
		Collection sortedValues = new TreeSet();
		sortedValues.addAll(semanticProperties.keySet());
		for(Iterator k = sortedValues.iterator(); k.hasNext(); ) {
			String relName = (String) k.next();
			//System.out.println(">> Adding relName " + relName);
			DefaultMutableTreeNode relNode = new DefaultMutableTreeNode(relName);
			top.add(relNode);
			tree.expandChilds(relNode);
			Collection otocs = (Collection) semanticProperties.get(relName);
			for(Iterator z = otocs.iterator(); z.hasNext(); ) {
				TreeOntoClass otoc = (TreeOntoClass) z.next();
				DefaultMutableTreeNode otocNode = new DefaultMutableTreeNode(otoc);
				relNode.add(otocNode);
				tree.expandChilds(otocNode);
				//fake leaf?
				otocNode.add(new DefaultMutableTreeNode(" "));
			}
		}
		tree.expandChilds(top);
	}

	
//	void showSemanticRelations(Synset syn) {
//		
//		Collection relations = syn.semanticToSynset.keySet(); 
//		
//		DefaultMutableTreeNode top = null, node = null;
//
//		clearTree();
//
//		tree.setRootUserObject(syn.toString());
//		tree.setRootVisible(true);
//	
//		if(relations == null || relations.size() < 1) {
//			return;
//		}
//		
//		String rel = "";
//
//		for(Iterator i = relations.iterator(); i.hasNext();) {
//			String thisRel = (String) i.next();
//			if(thisRel.equalsIgnoreCase("semantic-property")) {
//				continue;
//			}
//			if( !rel.equals(thisRel) ) {
//				tree.expandChilds(top);
//				rel = thisRel;
//				top = new DefaultMutableTreeNode(rel);
//				tree.addNode(top);
//			}
//			Collection values = (Collection) syn.semanticToSynset.get(thisRel);
//			for(Iterator k = values.iterator(); k.hasNext();) {
//				Synset item = (Synset) k.next();
//				node = new DefaultMutableTreeNode(item);
//				top.add(node);				
//			}
//		}
//		
//		tree.expandChilds(top);
//		
//		JScrollBar vbar = scrollPane.getVerticalScrollBar();
//		vbar.setValue(vbar.getMinimum());
//		JScrollBar hbar = scrollPane.getHorizontalScrollBar();
//		hbar.setValue(hbar.getMinimum());
//	}
}
