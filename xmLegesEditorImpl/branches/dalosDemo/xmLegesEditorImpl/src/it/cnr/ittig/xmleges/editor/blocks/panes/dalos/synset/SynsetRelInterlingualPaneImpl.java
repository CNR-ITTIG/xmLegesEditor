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
import it.cnr.ittig.xmleges.editor.services.panes.dalos.synset.SynsetRelInterlingualPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JScrollBar;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class SynsetRelInterlingualPaneImpl extends SynsetRelPane 
implements EventManagerListener, Loggable, Serviceable, 
Initializable, Startable, SynsetRelInterlingualPane {

	private String destLang = null;
	
	public void initialize() throws Exception {
		
		tabbedPaneName = "editor.panes.dalos.interlingualrelation";
				
		eventManager.addListener(this, PaneFocusGainedEvent.class);
		eventManager.addListener(this, LangChangedEvent.class);
		
		super.initialize();
		
		tree.addMouseListener(new InterLangTreeMouseAdapter());
		
	}
	
	public void manageEvent(EventObject event) {
		
		
		// gestione eventi LangChanged
		if(event instanceof LangChangedEvent){
			if(!((LangChangedEvent)event).getIsGlobalLang()){				
				//	ho rimesso questo perche' altriment nel caso in cui il pannello interlingual sia gia' selezionato non lo fa
				Object syn = observableSynset.getSynset();
				if( !(syn instanceof Synset) ) {
					return;
				}
				clearTree();
				focusGainedEvent((Synset) syn);
				
				
				// CONDIZIONE PER SELEZIONARE AUTOMATICAMENTE IL PANNELLO TRANSLATION
				destLang = ((LangChangedEvent) event).getLang();
				if(!destLang.equals(utilDalos.getGlobalLang()) && ((Synset)syn).getLanguage().equals(utilDalos.getGlobalLang()))	
					frame.setSelectedPane(this);
				
			}
		}
		super.manageEvent(event);
	}

	void focusGainedEvent(Synset syn) {

		if(syn == null) {
			return;
		}	
		showInterlingualRelations(syn);					
	}
	
	void showInterlingualRelations(Synset syn) {
		
		//Mappa da relazioni a synset collection
		Map relationToSynsets = kbManager.getInterlingualProperties(syn, destLang); 
		
		DefaultMutableTreeNode top = null, node = null;

		clearTree();

		tree.setRootUserObject(syn.toString());
		tree.setRootVisible(true);
	
		if(relationToSynsets == null || relationToSynsets.size() < 1) {
			return;
		}
		
		addLingualLinks(relationToSynsets, KbConf.MATCH_EQ, top);
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
	
	protected class InterLangTreeMouseAdapter extends MouseAdapter {
		
		public void mouseClicked(MouseEvent e) {
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			if (path != null) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
				try {
					if(n.getUserObject() instanceof Synset){   	
						if(e.getClickCount()==2){
							selectSynset((Synset)n.getUserObject());
							frame.setSelectedPane("editor.panes.dalos.synsetdetails");
							//refreshTree((Synset)n.getUserObject());
						}
					}
					else
						System.err.println("NOT Synset selected on tree");
				} catch (ClassCastException exc) {
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					System.err.println("right click on tree");
				}
			}
		}
	}
}