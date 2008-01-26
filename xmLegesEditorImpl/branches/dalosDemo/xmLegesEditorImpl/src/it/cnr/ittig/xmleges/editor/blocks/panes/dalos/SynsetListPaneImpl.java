package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dalos.action.SynsetMarkupAction;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetListPane;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetSelectionEvent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.w3c.dom.Node;

public class SynsetListPaneImpl 
extends DalosPane 
implements EventManagerListener, Loggable, Serviceable, 
	Initializable, Startable, SynsetListPane {

	JList list = new JList();
	JTextField  textWords = new JTextField();
	JPopupMenu popupMenu;
	JComboBox searchType;
	SynsetMarkupAction synsetMarkupAction;
	FindAction findAction = new FindAction();
	String[] searchTypes={"Contains", "Starts with","Ends with","Matches"};
			
	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {

		synsetMarkupAction = (SynsetMarkupAction) serviceManager.lookup(SynsetMarkupAction.class);

		super.service(serviceManager);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		tabbedPaneName = "editor.panes.dalos.synsetlist";
		
		popupMenu = new JPopupMenu();
		JToolBar bar = new JToolBar();
		
		searchType = new JComboBox(searchTypes);
		searchType.setSelectedIndex(0);
		
        textWords.setPreferredSize(new Dimension(300,24));
        textWords.setEditable(true);
	    textWords.addActionListener(new TextFieldActionListener());	   
	    
	    bar.add(searchType);
		bar.add(textWords);
		bar.add(utilUI.applyI18n("editor.panes.dalos.synsetlist.find", findAction));
		
		panel.add(bar, BorderLayout.SOUTH);
		scrollPane.setViewportView(list);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        Collection synsets = kbManager.getSynsets("IT");
        
        //Usa un cell renderer per i lemmi
        LemmaListCellRenderer renderer = new LemmaListCellRenderer();
        renderer.setI18n(i18n);
        list.addMouseListener(new SynsetListPaneMouseAdapter());
        list.addListSelectionListener(new SynsetListSelectionListener());
		
        list.setCellRenderer(renderer);
        list.setListData(synsets.toArray());
     
		eventManager.addListener(this,SynsetSelectionEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
		
		super.initialize();
	}

	public void manageEvent(EventObject event) {

		super.manageEvent(event);
		
		if (event instanceof SynsetSelectionEvent){
		
			frame.setShowingPane(synsetDetailsPane, true);			
			//System.err.println("synsetdetailspane setvisible true");			
		}
		if (event instanceof SelectionChangedEvent){
			Node activeNode = ((SelectionChangedEvent)event).getActiveNode();
			if(isDalosSpan(activeNode)){
				list.setSelectedValue(kbManager.getSynset(getSynsetURI(activeNode), "IT"), true);
				//selectSynset(kbManager.getSynset(getSynsetURI(activeNode), "IT"));
				System.err.println("Synchronize on "+getSynsetURI(activeNode));
			}	
		}
	}	

	// ///////////////////////////////////////////////////////// Toolbar Actions
	/**
	 * Azione per l'apertura del BugReport.
	 */
	class FindAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			System.err.println("Find ACTION pressed");		
			searchAndDisplaySynsets();
		}
	}

	private boolean isDalosSpan(Node node){
		if(node!=null){
			if(UtilDom.isTextNode(node))
				node = node.getParentNode();
			return ((node.getNodeName().indexOf("span")!=-1) && (UtilDom.getAttributeValueAsString(node, "h:style").equalsIgnoreCase("dalos")));
		}
		return false;
	}
	
	private String getSynsetURI(Node node){
		if(UtilDom.isTextNode(node))
	    	node = node.getParentNode();
		return(UtilDom.getAttributeValueAsString(node, "h:class"));
	}
	
	private void searchAndDisplaySynsets(){
		Collection res = kbManager.search(textWords.getText(), (String)searchType.getSelectedItem(), "IT");
		if(res!=null && !res.isEmpty()){
			list.setListData(res.toArray());
			list.setSelectedIndex(0);
		}else
			list.setListData(new Vector());
	}

	private void selectSynset(Synset activeSynset){
		if(activeSynset!=null)
			eventManager.fireEvent(new SynsetSelectionEvent(this, activeSynset));
	}
	
	protected class SynsetListSelectionListener implements ListSelectionListener {
		
		public void valueChanged(ListSelectionEvent e) {
			if(list.getSelectedValue()!=null){
				
				frame.setShowingPane(synsetDetailsPane, true);
				//System.err.println("synsetdetailspane setvisible true");
				selectSynset((Synset)list.getSelectedValue());
			}
		}
	}
	
	protected class TextFieldActionListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			System.err.println("action performed on textfieldddd");
			searchAndDisplaySynsets();
		}
	}
	
	// METTER TUTTO IN UN UTIL A PARTE   PER RICHIAMARLO DAI VARI POSTI   ???? 	
	protected class SynsetListPaneMouseAdapter extends MouseAdapter {
		
		JMenu insert = null;
		
		public void mouseClicked(MouseEvent e) {
			Synset selectedSyn = (Synset)list.getSelectedValue();
			if(e.getClickCount()==2){				
				synsetMarkupAction.doSynsetMarkup(selectedSyn, selectedSyn.getLexicalForm());
				System.err.println("<"+selectedSyn.getURI()+">"+selectedSyn.getLexicalForm()+"</"+selectedSyn.getURI()+">");
			}
			if (e.getButton() == MouseEvent.BUTTON3) {
				updatePopupMenu(selectedSyn);
				popupMenu.show(list, e.getX(), e.getY());
			}
		}
	
		protected void updatePopupMenu(Synset synset) {
			popupMenu.removeAll();
						
			Collection variants = synset.getVariants();
			variants.remove(synset.getLexicalForm());			
			
			// LEXICAL FORM
			Font font = new Font(null, Font.BOLD, 10);
			JMenuItem menuItem = new JMenuItem(synset.getLexicalForm(),i18n.getIconFor("editor.panes.dalos.item.lexical"));
			menuItem.setFont(font);
			//menuItem.setFont(Font)
			menuItem.addActionListener(new InsertVariantAction(synset,synset.getLexicalForm()));
			popupMenu.add(menuItem);
			
			if(variants.size()>0)
				popupMenu.addSeparator();			
			
			// VARIANTS
			for (Iterator it = variants.iterator(); it.hasNext();) {
				String next = (String) it.next();
                menuItem = new JMenuItem(next,i18n.getIconFor("editor.panes.dalos.item.lexical"));
				menuItem.addActionListener(new InsertVariantAction(synset,next));
				popupMenu.add(menuItem);
			}
		}					
	
		protected class InsertVariantAction extends AbstractAction {
			private Synset synset;
			private String variant;
			
			private InsertVariantAction(Synset synset, String variant) {
				this.synset = synset;
				this.variant = variant;
			}

			public void actionPerformed(ActionEvent e) {
				synsetMarkupAction.doSynsetMarkup(synset,variant);
			}
		}
	}
}
