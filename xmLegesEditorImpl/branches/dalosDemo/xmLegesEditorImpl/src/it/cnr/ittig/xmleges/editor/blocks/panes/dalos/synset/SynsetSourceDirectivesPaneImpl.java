package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.PaneFocusGainedEvent;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangChangedEvent;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.synset.SynsetSourceDirectivesPane;

public class SynsetSourceDirectivesPaneImpl extends SynsetSourcePane implements EventManagerListener, Loggable, Serviceable, 
Initializable, Startable, SynsetSourceDirectivesPane {
	
	
	public void initialize() throws Exception {

		tabbedPaneName = "editor.panes.dalos.directives.source";	
	
		eventManager.addListener(this, PaneFocusGainedEvent.class);
		eventManager.addListener(this, LangChangedEvent.class);
	
		super.initialize();
	}


	
	void showSynsetSource(Synset syn) {
		synsetPane.clearContent();
		kbManager.addSources(syn);
		synsetPane.drawDirective(syn);
	}
	
}