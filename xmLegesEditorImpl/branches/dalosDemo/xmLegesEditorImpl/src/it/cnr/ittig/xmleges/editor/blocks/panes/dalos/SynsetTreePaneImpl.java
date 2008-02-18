package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.TreeOntoClass;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangChangedEvent;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetTreePane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JScrollBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class SynsetTreePaneImpl extends DalosPane 
implements EventManagerListener, Loggable, Serviceable, 
	Initializable, Startable, SynsetTreePane {

	JTree tree;

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		eventManager.addListener(this, LangChangedEvent.class);
		
		tabbedPaneName = "editor.panes.dalos.synsettree";
		showTree(utilDalos.getGlobalLang());
		super.initialize();
	}
	

	private void showTree(String lang){
		tree = kbManager.getTree(lang);
		tree.setShowsRootHandles(false);
		tree.putClientProperty("JTree.lineStyle", "None");
		tree.addMouseListener(new SynsetTreePaneMouseAdapter());

		scrollPane.getViewport().setOpaque(false);
		
		Image logoDalos = null;		
		logoDalos = i18n.getImageFor("editor.panes.dalos.logo");		
		System.out.println("logoDalos: " + logoDalos);		
		ImagePanel iPanel = new ImagePanel(logoDalos);
		iPanel.setLayout(new BorderLayout());
		iPanel.add(tree, BorderLayout.CENTER);
		iPanel.setBackground(Color.WHITE);
		scrollPane.getViewport().setView(iPanel);
				
		tree.setOpaque(false);
		iPanel.setOpaque(true);
	}
	
	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		
		super.manageEvent(event);
		
		if(event instanceof LangChangedEvent){
			if(((LangChangedEvent)event).getIsGlobalLang()) {
				String lang = ((LangChangedEvent)event).getLang();			
				// BACCI! un  modo migliore per fare refresh dell'albero
				showTree(lang);
				try{
					super.initialize();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}

	}
	
	protected void updateObserver(Synset syn) {
		
		kbManager.setTreeSelection(syn);		
		JScrollBar vbar = scrollPane.getVerticalScrollBar();
		vbar.setValue(vbar.getMinimum());
		JScrollBar hbar = scrollPane.getHorizontalScrollBar();
		hbar.setValue(hbar.getMinimum());
	}

	private void selectSynset(Object activeSynset) {

		observableSynset.setSynset(activeSynset);
	}
	
	protected class SynsetTreePaneMouseAdapter extends MouseAdapter {
		
		public void mouseClicked(MouseEvent e) {
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			if (path != null) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
				try {
					Object selObj = n.getUserObject();
					if( selObj instanceof Synset){
						selectSynset(n.getUserObject());
					} else if( selObj instanceof TreeOntoClass){
						//TODO Show classified term in the list panel ?
						selectSynset(n.getUserObject());
					} else {
						System.err.println("NOT Synset selected on tree");
					}
				} catch (ClassCastException exc) {
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					System.err.println("right click on tree");
				}
			}
		}
	}
}
