package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset.SynsetDetailsPaneImpl;
import it.cnr.ittig.xmleges.editor.services.dalos.action.SynsetMarkupAction;
import it.cnr.ittig.xmleges.editor.services.dalos.kb.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetListPane;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetSelectionEvent;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.synset.SynsetDetailsPane;

import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.w3c.dom.Node;

/**						
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.dalos.ConceptsPane</code>.</h1>
 * <h1>Descrizione</h1>
 * Servizio per la visualizzazione del pannello .....
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.core.services.bars.Bars:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.blocks.util.ui.UtilUI:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.bugreport.BugReport:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>panes.segnalazioni</code>: chiave che contiene il nome del pannello;</li>
 * <li><code>panes.segnalazioni.open</code>: messaggio di apertura della maschera del bugreport;</li>
 * <li><code>panes.segnalazioni.clear</code>: messaggio di cancellazione delle segnalazioni.</li>
 * </ul>
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @see it.cnr.ittig.xmleges.core.services.util.ui.UtilUI
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */

public class SynsetListPaneImpl implements SynsetListPane, EventManagerListener, Loggable, Serviceable, Initializable, Startable {

	Logger logger;
	
	Frame frame;

	EventManager eventManager;

	UtilUI utilUI;

	Bars bars;

	JPanel panel = new JPanel(new BorderLayout());
	
	JScrollPane scrollPane = new JScrollPane();

	JList list = new JList();
	
	JTextField  textWords = new JTextField();
	
	JComboBox   searchType;

	FindAction findAction = new FindAction();

	JPopupMenu popupMenu;

	KbManager kbManager;
	
	I18n i18n;
	
	SynsetMarkupAction synsetMarkupAction;
	
	String[] searchTypes={"Contains", "Starts with","Ends with","Matches"};
	
	SynsetDetailsPaneImpl synsetDetailsPane;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}
	
	
	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		kbManager = (KbManager) serviceManager.lookup(KbManager.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
		synsetMarkupAction = (SynsetMarkupAction) serviceManager.lookup(SynsetMarkupAction.class);
		synsetDetailsPane = (SynsetDetailsPaneImpl) serviceManager.lookup(SynsetDetailsPane.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
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
		panel.add(scrollPane);
		frame.addPane(this, false);
		
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
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof SynsetSelectionEvent){
		
			frame.setShowingPane(synsetDetailsPane, true);
			
			System.err.println("synsetdetailspane setvisible true");
			list.setSelectedValue(((SynsetSelectionEvent)event).getActiveSynset(), true);			
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
	
	
	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
		
	}

	public void stop() throws Exception {
	}

	// ///////////////////////////////////////////////////// SegnalazioniPane Interface
	public String getName() {
		return "editor.panes.dalos.synsetlist";
	}

	public Component getPaneAsComponent() {
		return panel;
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

	public boolean canCut() {
		return false;
	}

	public void cut() throws PaneException {
	}

	public boolean canCopy() {
		return false;
	}

	public void copy() throws PaneException {
	}

	public boolean canPaste() {
		return false;
	}

	public void paste() throws PaneException {
	}

	public boolean canPasteAsText() {
		return false;
	}

	public void pasteAsText() throws PaneException {
	}

	public boolean canDelete() {
		return false;
	}

	public void delete() throws PaneException {
	}

	public boolean canPrint() {
		return false;
	}

	public Component getComponentToPrint() {
		return null;
	}

	public boolean canFind() {
		return false;
	}

	public FindIterator getFindIterator() {
		return null;
	}

	public void reload() {
	}
	
	
	
	private void selectSynset(Synset activeSynset){
		if(activeSynset!=null)
			eventManager.fireEvent(new SynsetSelectionEvent(this, activeSynset));
	}
	
	
	protected class SynsetListSelectionListener implements ListSelectionListener {
		
		public void valueChanged(ListSelectionEvent e) {
			if(list.getSelectedValue()!=null){
				
				frame.setShowingPane(synsetDetailsPane, true);
			
				System.err.println("synsetdetailspane setvisible true");
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
