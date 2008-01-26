package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.tabbed;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.editor.services.dalos.kb.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.util.UtilDalos;
import it.cnr.ittig.xmleges.editor.services.panes.dalos.SynsetSelectionEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public abstract class SynsetPane {

	Logger logger;
	
	Frame frame;

	EventManager eventManager;

	JPanel panel = new JPanel(new BorderLayout());
	
	JScrollPane scrollPane = new JScrollPane();
		
	I18n i18n;
	
	ActionManager actionManager;
	
	Synset selectedSynset = null;
	
	UtilDalos utilDalos;
	
	String tabbedPaneName = "tabbed.pane.name.not.initialized";
	
	KbManager kbManager = null;
	
	JPanel comboPanel = null;
	
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
	}
		
	/////////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		
		//panel.add(utilDalos.getLanguageSwitchPanel(), BorderLayout.SOUTH);
		comboPanel = utilDalos.getLanguageSwitchPanel();
		panel.add(comboPanel, BorderLayout.SOUTH);
	}
	
	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof SynsetSelectionEvent){
			selectedSynset = ((SynsetSelectionEvent)event).getActiveSynset();
		}
	}

	/*
	 * Set selected flag in the combo box.
	 */
	void setFlag() {
		Component[] components = comboPanel.getComponents();
		JComboBox cbx = null;		
		for(int i = 0; i < components.length; i++) {
			Component item = components[i];
			if(item instanceof JComboBox) {
				cbx = (JComboBox) item;
			}
		}
		if(cbx == null) {
			System.err.println("ERROR! setFlag() cbx is null!");
			return;
		}
		String lang = selectedSynset.getLanguage();
		int index = 0;
		//Cambiare secondo valori in un file di conf...
		if(lang.equalsIgnoreCase("en")) {
			index = 1;
		}
		if(lang.equalsIgnoreCase("es")) {
			index = 2;
		}
		if(lang.equalsIgnoreCase("nl")) {
			index = 3;
		}
		//System.out.println("Setting flag " + index + "...");
		cbx.setSelectedIndex(index);
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
