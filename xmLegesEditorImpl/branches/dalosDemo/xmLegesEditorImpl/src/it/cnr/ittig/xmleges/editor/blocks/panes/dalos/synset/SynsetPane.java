package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;

import it.cnr.ittig.xmleges.editor.blocks.panes.dalos.DalosPane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.EventObject;

import javax.swing.JComboBox;

public abstract class SynsetPane extends DalosPane {

	//Init Combo Box containing flags
	public void initialize() throws Exception {
		
		//panel.add(utilDalos.getLanguageSwitchPanel(), BorderLayout.SOUTH);
		comboPanel = utilDalos.getLanguageSwitchPanel();
		panel.add(comboPanel, BorderLayout.SOUTH);
		
		super.initialize();
	}
	
	public void manageEvent(EventObject event) {
		
		super.manageEvent(event);
	}

	/*
	 * Set selected flag in the combo box.
	 */
	protected void setFlag() {
		Component[] components = comboPanel.getComponents();
		JComboBox cbx = null;		
		for(int i = 0; i < components.length; i++) {
			Component item = components[i];
			if(item instanceof JComboBox) {
				cbx = (JComboBox) item;
			}
		}
		if(cbx == null) {
			logger.error("ERROR! setFlag() cbx is null!");
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
}
