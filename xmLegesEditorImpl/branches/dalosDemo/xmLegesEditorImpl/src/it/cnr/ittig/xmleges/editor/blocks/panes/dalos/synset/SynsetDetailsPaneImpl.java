package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangChangedEvent;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.synset.SynsetDetailsPane;

public class SynsetDetailsPaneImpl extends SynsetPane 
implements EventManagerListener, Loggable, Serviceable, 
	Initializable, Startable, SynsetDetailsPane  {

	DetailsContainer synsetPane;
	
	public void initialize() throws Exception {		
		
		tabbedPaneName = "editor.panes.dalos.synsetdetails";
		
		///////////////////////////////////////////
		//METTERE DA UN'ALTRA PARTE !!!!
		String[] icons = new String[] { 		
				"../images/kontact_journal.png", 
				"../images/lexical.png",
				"../images/signature.png" };
		
		for (int i = 0; i < icons.length; i++) {
			UtilFile.copyFileInTempDir(getClass().
					getResourceAsStream(icons[i]),"dalos",icons[i]);
		}		
		///////////////////////////////////////////
		
		synsetPane = new DetailsContainer();
		synsetPane.setI18n(i18n);
		
		scrollPane.setViewportView(synsetPane);
		
		synsetPane.clearContent();
		
		eventManager.addListener(this, LangChangedEvent.class);
		
		super.initialize();
	}

	protected void updateObserver(Synset syn) {
		
		synsetPane.setSynset(syn);
		synsetPane.draw();
		//frame.setShowingPane(this, true);
	}
}
