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
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.editor.services.dalos.kb.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.LinguisticRelationPane;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetSelectionEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
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

public class LinguisticRelationPaneImpl implements LinguisticRelationPane, EventManagerListener, Loggable, Serviceable, Initializable, Startable {

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
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
				
		frame.addPane(this, false);
        
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
	
//		tree = kbManager.getTree("IT");		
//		scrollPane.setViewportView(tree);
		
//		tree.addMouseListener(new SynsetTreePaneMouseAdapter());
		
		eventManager.addListener(this, SynsetSelectionEvent.class);
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof SynsetSelectionEvent){
		//	System.err.println("Synchronize LinguisticRelationPane on    "+((SynsetSelectionEvent)event).getActiveSynset().getLexicalForm());
			
		}
	}
	
	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
		
	}

	public void stop() throws Exception {
	}

	// ///////////////////////////////////////////////////// SegnalazioniPane Interface
	public String getName() {
		return "editor.panes.dalos.linguisticrelation";
	}

	public Component getPaneAsComponent() {
		return panel;
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
