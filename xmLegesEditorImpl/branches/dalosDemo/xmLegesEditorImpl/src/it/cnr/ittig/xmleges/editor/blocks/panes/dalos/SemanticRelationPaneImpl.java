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
import it.cnr.ittig.xmleges.core.services.frame.PaneFocusGainedEvent;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.editor.services.dalos.kb.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;
import it.cnr.ittig.xmleges.editor.services.dalos.util.UtilDalos;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SemanticRelationPane;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetSelectionEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

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

public class SemanticRelationPaneImpl implements SemanticRelationPane, EventManagerListener, Loggable, Serviceable, Initializable, Startable {

	Logger logger;
	
	Frame frame;

	EventManager eventManager;

	UtilUI utilUI;

	Bars bars;

	JPanel panel = new JPanel(new BorderLayout());
	
	JScrollPane scrollPane = new JScrollPane();

	JList list = new JList();
	
	JTree tree;
	
	KbManager kbManager;
	
	SynsetTree relazioniTree;
	
	Synset selectedSyn = null;
	
	I18n i18n;
	
	boolean update = false;
	
	UtilDalos utilDalos;

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
		utilDalos = (UtilDalos) serviceManager.lookup(UtilDalos.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
				
		DefaultMutableTreeNode tmpRoot = new DefaultMutableTreeNode(" - ");
     	DefaultTreeModel tmpModel = new DefaultTreeModel(tmpRoot);     	
     	relazioniTree = new SynsetTree(tmpModel,i18n);
     	relazioniTree.addMouseListener(new SemanticRelationTreeMouseAdapter());
			
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		
		panel.add(utilDalos.getLanguageSwitchPanel(), BorderLayout.SOUTH);
		
		scrollPane.setViewportView(relazioniTree);		
		frame.addPane(this, false);
				
		eventManager.addListener(this, SynsetSelectionEvent.class);
		eventManager.addListener(this, PaneFocusGainedEvent.class);
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		
		//visible = this.getPaneAsComponent().isShowing();
		
		if (event instanceof SynsetSelectionEvent)
				selectedSyn = ((SynsetSelectionEvent) event).getActiveSynset();
		
		if (event instanceof PaneFocusGainedEvent && ((PaneFocusGainedEvent)event).getPane().equals(this)){
			clearTree(relazioniTree);
			System.err.println("Synchronize SemanticRelationPane on "	+ selectedSyn.getLexicalForm());    		
			kbManager.addSemanticProperties(selectedSyn);
			showSemanticRelations(selectedSyn);			
		}
		
	}
	
	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
		
	}

	public void stop() throws Exception {
	}

	// ///////////////////////////////////////////////////// SegnalazioniPane Interface
	public String getName() {
		return "editor.panes.dalos.semanticrelation";
	}

	public Component getPaneAsComponent() {
		return panel;
	}
	
	private void clearTree(SynsetTree tree) {

		tree.removeChildren();
		tree.setRootVisible(false);
		tree.reloadModel();
	}
	
//	void showSemanticRelations(Synset syn) {
//		
//		Collection relations = syn.semanticToSynset.keySet(); 
//		
//		DefaultMutableTreeNode top = null, node = null;
//
//		clearTree(relazioniTree);
//
//		relazioniTree.setRootUserObject(syn.toString());
//		relazioniTree.setRootVisible(true);
//	
//		if(relations == null || relations.size() < 1) {
//			return;
//		}
//		
//		String rel = "";
//
//		for(Iterator i = relations.iterator(); i.hasNext();) {
//			String thisRel = (String) i.next();
//			if(thisRel.equalsIgnoreCase("semantic-property")) {
//				continue;
//			}
//			if( !rel.equals(thisRel) ) {
//				relazioniTree.expandChilds(top);
//				rel = thisRel;
//				top = new DefaultMutableTreeNode(rel);
//				relazioniTree.addNode(top);
//			}
//			Collection values = (Collection) syn.semanticToSynset.get(thisRel);
//			for(Iterator k = values.iterator(); k.hasNext();) {
//				Synset item = (Synset) k.next();
//				node = new DefaultMutableTreeNode(item);
//				top.add(node);				
//			}
//		}
//		
//		relazioniTree.expandChilds(top);
//		
//		JScrollBar vbar = scrollPane.getVerticalScrollBar();
//		vbar.setValue(vbar.getMinimum());
//		JScrollBar hbar = scrollPane.getHorizontalScrollBar();
//		hbar.setValue(hbar.getMinimum());
//	}
	
	//FAKE TREE:
	void showSemanticRelations(Synset syn) {
		
		clearTree(relazioniTree);
		
		relazioniTree.setRootUserObject("service providing");
		relazioniTree.setRootVisible(true);

		DefaultMutableTreeNode propNode = new DefaultMutableTreeNode("qualified_by");
		((SynsetTree) relazioniTree).addNode(propNode);
		DefaultMutableTreeNode classNode1 = new DefaultMutableTreeNode("Contract");
		propNode.add(classNode1);
		DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode("distance contract");
		classNode1.add(leafNode);
		leafNode = new DefaultMutableTreeNode("sale contract");
		classNode1.add(leafNode);
		propNode = new DefaultMutableTreeNode("has_partecipant");
		((SynsetTree) relazioniTree).addNode(propNode);
		DefaultMutableTreeNode classNode2 = new DefaultMutableTreeNode("Legal Person");
		propNode.add(classNode2);
		leafNode = new DefaultMutableTreeNode("authority");
		classNode2.add(leafNode);
		leafNode = new DefaultMutableTreeNode("administrative authority");
		classNode2.add(leafNode);
		leafNode = new DefaultMutableTreeNode("trade groups");
		classNode2.add(leafNode);
		DefaultMutableTreeNode classNode3 = new DefaultMutableTreeNode("Natural Person");
		propNode.add(classNode3);
		leafNode = new DefaultMutableTreeNode("natural person");
		classNode3.add(leafNode);
		leafNode = new DefaultMutableTreeNode("person");
		classNode3.add(leafNode);
		propNode = new DefaultMutableTreeNode("has_role");
		((SynsetTree) relazioniTree).addNode(propNode);
		DefaultMutableTreeNode classNode4 = new DefaultMutableTreeNode("Service Provider");
		propNode.add(classNode4);
		leafNode = new DefaultMutableTreeNode("information society service provider");
		classNode4.add(leafNode);
		leafNode = new DefaultMutableTreeNode("service provider");
		classNode4.add(leafNode);
		leafNode = new DefaultMutableTreeNode("supplier");
		classNode4.add(leafNode);
		DefaultMutableTreeNode classNode5 = new DefaultMutableTreeNode("Recipient of the Service");
		propNode.add(classNode5);
		leafNode = new DefaultMutableTreeNode("recipient of the service");
		classNode5.add(leafNode);
		
		relazioniTree.expandChilds(classNode1);
		relazioniTree.expandChilds(classNode2);
		relazioniTree.expandChilds(classNode3);
		relazioniTree.expandChilds(classNode4);
		relazioniTree.expandChilds(classNode5);

		JScrollBar vbar = scrollPane.getVerticalScrollBar();
		vbar.setValue(vbar.getMinimum());
		JScrollBar hbar = scrollPane.getHorizontalScrollBar();
		hbar.setValue(hbar.getMinimum());
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
		eventManager.fireEvent(new SynsetSelectionEvent(this, activeSynset));
	}
	
	protected class SemanticRelationTreeMouseAdapter extends MouseAdapter {
		
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
