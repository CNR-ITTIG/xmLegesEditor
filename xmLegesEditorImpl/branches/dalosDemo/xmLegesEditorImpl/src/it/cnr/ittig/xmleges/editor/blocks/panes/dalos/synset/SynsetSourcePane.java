package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;

import it.cnr.ittig.xmleges.core.services.frame.PaneFocusGainedEvent;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

import java.util.EventObject;

public abstract class SynsetSourcePane extends SynsetPane{
	
	SourceContainer synsetPane;
	
	public void initialize() throws Exception {

		//tabbedPaneName = "editor.panes.dalos.directives.source";
		
		synsetPane = new SourceContainer();
		synsetPane.setI18n(i18n);
		synsetPane.setUtilDalos(utilDalos);
		
		scrollPane.setViewportView(synsetPane);
		
		synsetPane.clearContent();
		
		super.initialize();
	}

	public void manageEvent(EventObject event) {
		
		super.manageEvent(event);
				
		if (event instanceof PaneFocusGainedEvent && 
				((PaneFocusGainedEvent)event).getPane().equals(this)) {
			
			Object selectedSynset = observableSynset.getSynset();
			if( !(selectedSynset instanceof Synset) ) {
				return;
			}
			showSynsetSource((Synset)selectedSynset);
		}
	}
	
	protected void updateObserver(Synset syn) {

		super.updateObserver(syn);
		
		 if(this.getPaneAsComponent().isShowing())
			 showSynsetSource(syn);
			 
	}
	
	abstract void showSynsetSource(Synset syn);
//	{
//		synsetPane.clearContent();
//		kbManager.addSources(syn);
//		synsetPane.draw(syn);
//	}
	
}