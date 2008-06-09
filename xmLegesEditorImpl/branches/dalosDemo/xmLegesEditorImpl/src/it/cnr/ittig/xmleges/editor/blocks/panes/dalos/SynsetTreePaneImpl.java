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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class SynsetTreePaneImpl extends DalosPane 
implements EventManagerListener, Loggable, Serviceable, 
	Initializable, Startable, SynsetTreePane,ActionListener {

	JTree tree;
	JPopupMenu popupMenu;
	JCheckBox setInferred;

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		eventManager.addListener(this, LangChangedEvent.class);
		
		tabbedPaneName = "editor.panes.dalos.synsettree";
		
		popupMenu = new JPopupMenu();
		setInferred = new JCheckBox(i18n.getTextFor("editor.panes.dalos.synsettree.check.setinferred"));
		setInferred.addActionListener(this);
		
		// TODO default FALSE; piazzarlo nelle preference;
		setInferred.setSelected(false);
		utilDalos.setIsInferred(false);
		
		
		JToolBar bar = new JToolBar();
		bar.add(setInferred);
		panel.add(bar, BorderLayout.SOUTH);
		showTree(utilDalos.getGlobalLang());
		super.initialize();
	}
	

	private void showTree(String lang){
		tree = kbManager.getTree(lang);
		tree.setShowsRootHandles(false);
		tree.putClientProperty("JTree.lineStyle", "None");
		tree.addMouseListener(new SynsetTreePaneMouseAdapter());
		tree.addTreeSelectionListener(new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e) {
		        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		                           tree.getLastSelectedPathComponent();

		    /* if nothing is selected */ 
		        if (node == null) return;

		    /* retrieve the node that was selected */ 
		        Object nodeInfo = node.getUserObject();
			
		    /* React to the node selection. */
		        if(nodeInfo != null) {
		        	selectSynset(nodeInfo);
		        }
			
		    }
		});

		scrollPane.getViewport().setOpaque(false);
		
		Image logoDalos = null;		
		logoDalos = i18n.getImageFor("editor.panes.dalos.logo");				
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
			
			utilDalos.setTreeOntoLang(null);
			
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			if (path != null) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) path.getLastPathComponent();
				tree.setSelectionPath(new TreePath(n.getPath()));
				if(e.getButton() == MouseEvent.BUTTON1){
					try {
						selectSynset(n.getUserObject());
					} catch (ClassCastException exc) {
					}
				}
				if (e.getButton() == MouseEvent.BUTTON3) {
					if(n.getUserObject() instanceof TreeOntoClass){
						updatePopupMenu((TreeOntoClass)n.getUserObject());
						popupMenu.show(tree, e.getX(), e.getY());
					}
				}
			}
		}
	}
	
	
	protected void updatePopupMenu(TreeOntoClass toc) {
		
		popupMenu.removeAll();
					
		JMenuItem menuItem;
		String lang;
			
		for(int i=0; i<utilDalos.getDalosLang().length;i++){
			lang = utilDalos.getDalosLang()[i];
			menuItem = new JMenuItem(lang, i18n.getIconFor("editor.dalos.action.tolanguage."+lang.toLowerCase()+".icon"));
			menuItem.addActionListener(new OntoFlagAction(lang,toc));
			popupMenu.add(menuItem);
		}
	}					
	
	
	protected class OntoFlagAction extends AbstractAction {
		private String lang;
		private TreeOntoClass toc;
		
		private OntoFlagAction(String lang, TreeOntoClass toc) {
			this.lang = lang;
			this.toc = toc;
		}

		public void actionPerformed(ActionEvent e) {
			utilDalos.setTreeOntoLang(lang);
			selectSynset(toc);
		}
	}


	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==setInferred){
			utilDalos.setIsInferred(setInferred.isSelected());
		}
		
	}
	
	
}
