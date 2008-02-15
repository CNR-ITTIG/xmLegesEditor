package it.cnr.ittig.xmleges.editor.blocks.panes.dalos.synset;

import it.cnr.ittig.xmleges.editor.blocks.panes.dalos.DalosPane;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangChangedEvent;

import java.awt.BorderLayout;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JLabel;

public abstract class SynsetPane extends DalosPane {

	//Init Combo Box containing flags
	public void initialize() throws Exception {
		langPanel = utilDalos.getLanguageSwitchPanel();
		setGlobalFlag(utilDalos.getGlobalLang());
		panel.add(langPanel.getPanel(), BorderLayout.SOUTH);
		super.initialize();
	}

	public void manageEvent(EventObject event) {
		
		super.manageEvent(event);
		
		// gestione eventi LangChanged
		if(event instanceof LangChangedEvent){
			if(((LangChangedEvent)event).getIsGlobalLang()){
				setGlobalFlag(utilDalos.getGlobalLang());
			}
			else{
				setLocalFlag(((LangChangedEvent)event).getLang());	
			}
		}
	}

	/*
	 * Set selected flag in the combo box.
	 */
	protected void setLocalFlag(String lang) {
		if(langPanel != null){		
		    JComboBox langCombo = langPanel.getLangCombo();	
			int index = utilDalos.dalosLangToIndex(lang);
			if(langCombo == null) {
				logger.error("ERROR! setFlag() cbx is null!");
				return;
			}
			if(langCombo.getSelectedIndex()!=index){
				langCombo.setSelectedIndex(index);
			}
		}
	}	
	
	/*
	 * Set selected flag in the JLabel.
	 */
	protected void setGlobalFlag(String lang) {
		if(langPanel != null){		
		    JLabel  fromLang = langPanel.getFromLang();	
			
			if(fromLang == null) {
				logger.error("ERROR! setFlag() label is null!");
				return;
			}
			fromLang.setIcon(i18n.getIconFor("editor.dalos.action.tolanguage."+lang.toLowerCase()+".icon"));
			setLocalFlag(lang);			
		}
	}

	protected void updateObserver(Synset syn) {
		
		if(syn != null) {
			setLocalFlag(syn.getLanguage());
		}
	}
}