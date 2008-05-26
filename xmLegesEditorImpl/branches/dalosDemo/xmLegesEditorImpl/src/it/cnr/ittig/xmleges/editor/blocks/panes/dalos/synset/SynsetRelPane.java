package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;

import it.cnr.ittig.xmleges.core.services.frame.PaneFocusGainedEvent;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public abstract class SynsetRelPane extends SynsetPane {
	
	SynsetTree tree;

	public void initialize() throws Exception {

		DefaultMutableTreeNode tmpRoot = new DefaultMutableTreeNode(" - ");
     	DefaultTreeModel tmpModel = new DefaultTreeModel(tmpRoot);     	
     	tree = new SynsetTree(tmpModel,i18n);
     	tree.addMouseListener(new RelationTreeMouseAdapter());			
		scrollPane.setViewportView(tree);
		
		super.initialize();
	}

	public void manageEvent(EventObject event) {

		super.manageEvent(event);
		
		if (event instanceof PaneFocusGainedEvent && 
				((PaneFocusGainedEvent)event).getPane().equals(this)){
			Object syn = observableSynset.getSynset();
			if( !(syn instanceof Synset) ) {
				return;
			}
			clearTree();
			focusGainedEvent((Synset) syn);			
		}		
	}

	protected void updateObserver(Synset syn) {	
		super.updateObserver(syn);
		//if(!frame.isSelectedPane(this)){  // aggiorna l'albero solo se non sto navigando sull'albero stesso; in quel caso lo aggiorn con double click
			refreshTree(syn);
		//}
	}
	
	protected void refreshTree(Synset syn){
	    if(this.getPaneAsComponent().isShowing()){
	    	clearTree();
	    	focusGainedEvent(syn);	
	    }
	}
	
	abstract void focusGainedEvent(Synset syn);
	
	void clearTree() {
		tree.removeChildren();
		tree.setRootVisible(false);
		tree.reloadModel();
	}
	
	void selectSynset(Synset activeSynset){

		observableSynset.setSynset(activeSynset);
	}
	
	protected class RelationTreeMouseAdapter extends MouseAdapter {
		
		public void mouseClicked(MouseEvent e) {
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			if (path != null) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
				try {
					if(n.getUserObject() instanceof Synset){   
						if(e.getClickCount()==2){
							selectSynset((Synset)n.getUserObject());
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
