package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.PaneFocusGainedEvent;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangChangedEvent;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetSelectionEvent;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.synset.SynsetRelSemanticPane;

import java.util.Collection;
import java.util.Iterator;

import javax.swing.JScrollBar;
import javax.swing.tree.DefaultMutableTreeNode;

public class SynsetRelSemanticPaneImpl extends SynsetRelPane 
implements EventManagerListener, Loggable, Serviceable, 
Initializable, Startable, SynsetRelSemanticPane {
	
	public void initialize() throws Exception {

		tabbedPaneName = "editor.panes.dalos.semanticrelation";

		eventManager.addListener(this, SynsetSelectionEvent.class);
		eventManager.addListener(this, PaneFocusGainedEvent.class);
		eventManager.addListener(this, LangChangedEvent.class);
		
		super.initialize();
	}

	void focusGainedEvent() {
		if(selectedSynset != null){
			System.err.println("Synchronize SemanticRelationPane on "	+ selectedSynset.getLexicalForm());    		
			kbManager.addSemanticProperties(selectedSynset);
			showSemanticRelations(selectedSynset);
		}
	}
	
	void showSemanticRelations(Synset syn) {
		
		Collection relations = syn.semanticToSynset.keySet(); 
		
		DefaultMutableTreeNode top = null, node = null;

		clearTree();

		tree.setRootUserObject(syn.toString());
		tree.setRootVisible(true);
	
		if(relations == null || relations.size() < 1) {
			return;
		}
		
		String rel = "";

		for(Iterator i = relations.iterator(); i.hasNext();) {
			String thisRel = (String) i.next();
			if(thisRel.equalsIgnoreCase("semantic-property")) {
				continue;
			}
			if( !rel.equals(thisRel) ) {
				tree.expandChilds(top);
				rel = thisRel;
				top = new DefaultMutableTreeNode(rel);
				tree.addNode(top);
			}
			Collection values = (Collection) syn.semanticToSynset.get(thisRel);
			for(Iterator k = values.iterator(); k.hasNext();) {
				Synset item = (Synset) k.next();
				node = new DefaultMutableTreeNode(item);
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
