package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.PaneFocusGainedEvent;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetSelectionEvent;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.synset.SynsetRelLinguisticPane;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JScrollBar;
import javax.swing.tree.DefaultMutableTreeNode;

public class SynsetRelLinguisticPaneImpl extends SynsetRelPane 
implements EventManagerListener, Loggable, Serviceable, 
Initializable, Startable, SynsetRelLinguisticPane {

	public void initialize() throws Exception {
		
		tabbedPaneName = "editor.panes.dalos.linguisticrelation";
				
		eventManager.addListener(this, SynsetSelectionEvent.class);
		eventManager.addListener(this, PaneFocusGainedEvent.class);
		
		super.initialize();
	}

	void focusGainedEvent() {
		//System.err.println("Synchronize LinguisticRelationPane on "	+ selectedSynset.getLexicalForm());    		
		kbManager.addLexicalProperties(selectedSynset);
		showLinguisticRelations(selectedSynset);					
	}
	
	void showLinguisticRelations(Synset syn) {
		
		Collection relations = syn.lexicalToSynset.keySet(); 
		
		DefaultMutableTreeNode top = null, node = null;

		clearTree();

		tree.setRootUserObject(syn.toString());
		tree.setRootVisible(true);
	
		if(relations == null || relations.size() < 1) {
			System.out.println(">> relations is empty!!");
			return;
		}
		
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
		
		JScrollBar vbar = scrollPane.getVerticalScrollBar();
		vbar.setValue(vbar.getMinimum());
		JScrollBar hbar = scrollPane.getHorizontalScrollBar();
		hbar.setValue(hbar.getMinimum());
	}
}