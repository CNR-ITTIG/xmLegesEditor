package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.PaneFocusGainedEvent;
import it.cnr.ittig.xmleges.editor.blocks.dalos.kb.KbConf;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangChangedEvent;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.synset.SynsetRelLinguisticPane;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JScrollBar;
import javax.swing.tree.DefaultMutableTreeNode;

public class SynsetRelLinguisticPaneImpl extends SynsetRelPane 
implements EventManagerListener, Loggable, Serviceable, 
Initializable, Startable, SynsetRelLinguisticPane {

	public void initialize() throws Exception {
		
		tabbedPaneName = "editor.panes.dalos.linguisticrelation";
				
		eventManager.addListener(this, PaneFocusGainedEvent.class);
		eventManager.addListener(this, LangChangedEvent.class);
		
		super.initialize();
	}

	void focusGainedEvent(Synset syn) {

		if(syn == null) {
			return;
		}
		
		kbManager.addLexicalProperties(syn);
		showLinguisticRelations(syn);					
	}
	
	void showLinguisticRelations(Synset syn) {
		
		Collection relations = syn.lexicalToSynset.keySet(); 
		
		DefaultMutableTreeNode top = null, node = null;

		clearTree();

		tree.setRootUserObject(syn.toString());
		tree.setRootVisible(true);
	
		if(relations != null) {

			String rel = "";
	
			for(Iterator i = relations.iterator(); i.hasNext();) {
				String thisRel = (String) i.next();
				if(thisRel.equalsIgnoreCase("language-property")) {
					continue;
				}
				if( !rel.equals(thisRel) ) {
					tree.expandChilds(top);
					rel = thisRel;
					top = new DefaultMutableTreeNode(rel);
					tree.addNode(top);
					//System.out.println("++ NODE: " + rel);
				}
				Collection values = (Collection) syn.lexicalToSynset.get(thisRel);
				for(Iterator k = values.iterator(); k.hasNext();) {
					Synset item = (Synset) k.next();
					node = new DefaultMutableTreeNode(item);
					//System.out.println("++++ LEAF: " + item);
					top.add(node);				
				}
			}
			
			tree.expandChilds(top);
		}

		//INTERLINGUAL RELATIONS ON THE SAME LANGUAGE
		//Mappa da relazioni a synset collection
		Map relationToSynsets = kbManager.getInterlingualProperties(syn, syn.getLanguage()); 

		if(relationToSynsets == null || relationToSynsets.size() < 1) {
			return;
		}
		
		Collection syns = (Collection) relationToSynsets.get(KbConf.MATCH_EQ);
		if(syns.size() != 1) {
			//Skip self-equivalence
			addLingualLinks(relationToSynsets, KbConf.MATCH_EQ, top);
		}
		addLingualLinks(relationToSynsets, KbConf.MATCH_BROADER, top);
		addLingualLinks(relationToSynsets, KbConf.MATCH_NARROW, top);
		addLingualLinks(relationToSynsets, KbConf.MATCH_FUZZY, top);
		addLingualLinks(relationToSynsets, KbConf.MATCH_COHYPO, top);
		addLingualLinks(relationToSynsets, KbConf.MATCH_EQSYN, top);
		tree.expandChilds(top);
		
		JScrollBar vbar = scrollPane.getVerticalScrollBar();
		vbar.setValue(vbar.getMinimum());
		JScrollBar hbar = scrollPane.getHorizontalScrollBar();
		hbar.setValue(hbar.getMinimum());
	}
	
	private void addLingualLinks(Map rTs, String relName, DefaultMutableTreeNode top) {
		
		Collection syns = (Collection) rTs.get(relName);
		
		if(syns.size() > 0) {
			tree.expandChilds(top);
			top = new DefaultMutableTreeNode(relName);
			tree.addNode(top);
		}
		for(Iterator s = syns.iterator(); s.hasNext(); ) {
			Synset item = (Synset) s.next();
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
			top.add(node);
		}
		tree.expandChilds(top);
	}


}