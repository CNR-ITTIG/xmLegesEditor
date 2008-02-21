package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.Pane;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset.SynsetDetailsPaneImpl;
import it.cnr.ittig.xmleges.editor.services.dalos.SynsetHolder;
import it.cnr.ittig.xmleges.editor.services.dalos.kb.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.TreeOntoClass;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangPanel;
import it.cnr.ittig.xmleges.editor.services.dalos.util.UtilDalos;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.synset.SynsetDetailsPane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.EventObject;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public abstract class DalosPane implements Pane, Observer {

	protected Logger logger;
	
	protected Frame frame;

	protected EventManager eventManager;

	protected JPanel panel = new JPanel(new BorderLayout());
	
	protected JScrollPane scrollPane = new JScrollPane();
		
	protected I18n i18n;
	
	protected ActionManager actionManager;

	protected UtilDalos utilDalos;
	
	protected UtilUI utilUI;
	
	protected Bars bars;
	
	protected String tabbedPaneName = "tabbed.pane.name.not.initialized";
	
	protected KbManager kbManager = null;
	
	protected LangPanel langPanel = null;
	
	protected SynsetDetailsPaneImpl synsetDetailsPane;
	
	protected SynsetHolder observableSynset = null; 
	
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
		kbManager = (KbManager) serviceManager.lookup(KbManager.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		synsetDetailsPane = (SynsetDetailsPaneImpl)serviceManager.lookup(SynsetDetailsPane.class);
		observableSynset = (SynsetHolder) serviceManager.lookup(SynsetHolder.class);
	}
		
	/////////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		frame.addPane(this, false);
		
		((Observable) observableSynset).addObserver(this);
	}
	
	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
	}
	
	public void update(Observable observable, Object obj) {
		
		if( !(observable instanceof SynsetHolder) ) {
			logger.error("observable is not a synset holder!");
			return;
		}
		
		if( obj == null || obj instanceof Synset) {
			//go on with specific implementations...
			//manage 'null' value too...
			updateObserver((Synset) obj);				
			return;			
		}
		
		if( obj != null && obj instanceof TreeOntoClass) {
			//go on with specific implementations...
			updateObserver((TreeOntoClass) obj);
			return;			
		}
		
		logger.error("obj is not a known instance! " + obj.getClass());
	}

	protected abstract void updateObserver(Synset syn);
	
	protected void updateObserver(TreeOntoClass toc){		
	}
	
	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
		frame.addPane(this, false);
	}

	public void stop() throws Exception {
	}

	// ///////////////////////////////////////////////////// SegnalazioniPane Interface
	public String getName() {
		return tabbedPaneName;
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
