package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.PaneFocusGainedEvent;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangChangedEvent;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.synset.SynsetSourcePane;

import java.util.EventObject;

public class SynsetSourcePaneImpl extends SynsetPane
implements EventManagerListener, Loggable, Serviceable, 
Initializable, Startable, SynsetSourcePane {
	
	SourceContainer synsetPane;
	
	public void initialize() throws Exception {

		tabbedPaneName = "editor.panes.dalos.source";
		
		synsetPane = new SourceContainer();
		synsetPane.setI18n(i18n);
		synsetPane.setUtilDalos(utilDalos);
		
		scrollPane.setViewportView(synsetPane);
		
		synsetPane.clearContent();
		
		eventManager.addListener(this, PaneFocusGainedEvent.class);
		eventManager.addListener(this, LangChangedEvent.class);
		
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
	
	void showSynsetSource(Synset syn) {
		synsetPane.clearContent();
		kbManager.addSources(syn);
		synsetPane.draw(syn);
	}
	
}