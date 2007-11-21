package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.util.UtilDalos;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetDetailsPane;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetSelectionEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.EventObject;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**						
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetDetailsPane</code>.</h1>
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

public class SynsetDetailsPaneImpl implements SynsetDetailsPane, EventManagerListener, Loggable, Serviceable, Initializable, Startable {

	Logger logger;
	
	Frame frame;

	EventManager eventManager;

	JPanel panel = new JPanel(new BorderLayout());
	
	JScrollPane scrollPane = new JScrollPane();
		
	SynsetDetails synsetPane;
	
	I18n i18n;
	
	ActionManager actionManager;
	
	Synset selectedSynset = null;
	
	UtilDalos utilDalos;

	

	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}
	
	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
		utilDalos = (UtilDalos) serviceManager.lookup(UtilDalos.class);
	}
	

	
	/////////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		
		String[] icons = new String[] { 		
				"images/kontact_journal.png", "images/lexical.png","images/signature.png"
	    };
		
		for (int i = 0; i < icons.length; i++) {
			UtilFile.copyFileInTempDir(getClass().getResourceAsStream(icons[i]),"dalos", icons[i]);
		}
		
		
		
		panel.add(utilDalos.getLanguageSwitchPanel(), BorderLayout.SOUTH);
		
		synsetPane = new SynsetDetails();
		synsetPane.setI18n(i18n);
		
		scrollPane.setViewportView(synsetPane);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		
		synsetPane.clearContent();		
		eventManager.addListener(this, SynsetSelectionEvent.class);
	}
	
	
	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof SynsetSelectionEvent){
			selectedSynset = ((SynsetSelectionEvent)event).getActiveSynset();
			synsetPane.setSynset(selectedSynset);
			synsetPane.draw();
		}			
	}
	
	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
		frame.addPane(this, false);
	}

	public void stop() throws Exception {
	}

	// ///////////////////////////////////////////////////// SegnalazioniPane Interface
	public String getName() {
		return "editor.panes.dalos.synsetdetails";
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
	
}
