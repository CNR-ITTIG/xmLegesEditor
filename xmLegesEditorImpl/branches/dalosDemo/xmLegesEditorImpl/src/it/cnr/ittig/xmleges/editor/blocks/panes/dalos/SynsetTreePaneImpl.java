package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetSelectionEvent;
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
		
		tabbedPaneName = "editor.panes.dalos.synsettree";
		
		tree = kbManager.getTree("IT");		
		//tree.setRootVisible(false);
		tree.setShowsRootHandles(false);
		tree.putClientProperty("JTree.lineStyle", "None");
		//selectedSynset = null;
		tree.addMouseListener(new SynsetTreePaneMouseAdapter());

		scrollPane.getViewport().setOpaque(false);
//		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		Image logoDalos = null;		
		logoDalos = i18n.getImageFor("editor.panes.dalos.logo");		
		System.out.println("logoDalos: " + logoDalos);		
		ImagePanel iPanel = new ImagePanel(logoDalos);
		iPanel.setLayout(new BorderLayout());
		iPanel.add(tree, BorderLayout.CENTER);
		iPanel.setBackground(Color.WHITE);
		scrollPane.getViewport().setView(iPanel);
		//scrollPane.add(iPanel);
				
		tree.setOpaque(false);
		iPanel.setOpaque(true);

//		panel.add(scrollPane, BorderLayout.CENTER);
//		
//		frame.addPane(this, false);
        
		eventManager.addListener(this, SynsetSelectionEvent.class);
		
		super.initialize();
	}
	
	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		
		super.manageEvent(event);
		
		if (event instanceof SynsetSelectionEvent){

			kbManager.setTreeSelection(selectedSynset);
			
			JScrollBar vbar = scrollPane.getVerticalScrollBar();
			vbar.setValue(vbar.getMinimum());
			JScrollBar hbar = scrollPane.getHorizontalScrollBar();
			hbar.setValue(hbar.getMinimum());
		}
	}

	private void selectSynset(Synset activeSynset){
		eventManager.fireEvent(new SynsetSelectionEvent(this, activeSynset));
	}
	
	protected class SynsetTreePaneMouseAdapter extends MouseAdapter {
		
		public void mouseClicked(MouseEvent e) {
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			if (path != null) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
				try {
					if(n.getUserObject() instanceof Synset){
						selectSynset((Synset)n.getUserObject());
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