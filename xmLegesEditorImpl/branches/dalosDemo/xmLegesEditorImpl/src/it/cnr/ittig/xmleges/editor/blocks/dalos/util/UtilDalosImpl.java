package it.cnr.ittig.xmleges.editor.blocks.dalos.util;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.editor.services.dalos.kb.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangChangedEvent;
import it.cnr.ittig.xmleges.editor.services.dalos.util.LangPanel;
import it.cnr.ittig.xmleges.editor.services.dalos.util.UtilDalos;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dalos.kbmanager.KbManager</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 */

public class UtilDalosImpl implements UtilDalos, EventManagerListener, Loggable, Serviceable, Initializable, Startable {
	

	Logger logger;
	
	I18n i18n;
	
	EventManager eventManager;
	
	KbManager kbManager;
	
	PreferenceManager preferenceManager;

	AbstractAction[]  toLangActions;
	
	String globalLang = UtilDalos.IT; 	// DEFAULT LANG
	
	Properties prefs = null;
		
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		i18n = (I18n) serviceManager.lookup(I18n.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		kbManager = (KbManager) serviceManager.lookup(KbManager.class);
	}
	
	
	public void initialize() throws Exception {			
		toLangActions = new AbstractAction[] { new toLangAction(UtilDalos.IT,0), new toLangAction(UtilDalos.EN,1),new toLangAction(UtilDalos.NL,2),new toLangAction(UtilDalos.ES,3)};	
		eventManager.addListener(this, LangChangedEvent.class);
	}
	
	
	public void start() throws Exception {
		prefs = preferenceManager.getPreferenceAsProperties(getClass().getName());
		String s = prefs.getProperty("lang");
		if(s!=null)
			setGlobalLang(s);
	}

	
	public void stop() throws Exception {
		prefs.put("lang", globalLang);
		preferenceManager.setPreference(getClass().getName(), prefs);
	}
	
	
	public void manageEvent(EventObject event) {
		if(event instanceof LangChangedEvent){
			if(((LangChangedEvent)event).getIsGlobalLang()){
				setGlobalLang(((LangChangedEvent)event).getGlobalLang());
			}
		}	
	}
	
	
	public String getGlobalLang(){
		return this.globalLang;
	}
	
	
	private void setGlobalLang(String lang){
		this.globalLang = lang;
	}
	
	public class toLangAction extends AbstractAction {	
		
		String lang;
		int index;
		String iconKey;

		public toLangAction(String lang, int index){
			this.lang = lang;
			this.index = index;
			this.iconKey = "editor.dalos.action.tolanguage."+lang.toLowerCase()+".icon";
		}

		public void actionPerformed(ActionEvent e) {
			eventManager.fireEvent(new LangChangedEvent(this, false, null, index));
		}
		
		
		public String toString(){
			return lang;
		}
		
		public int getIndex(){
			return this.index;
		}
		
		public String getIconKey(){
			return this.iconKey;
		}
	}
	
		
	public LangPanel getLanguageSwitchPanel(){
		LangPanel lp = new LangPanel(new ComboBoxRenderer(),toLangActions,new toLangComboActionListener());
		lp.setArrowIcon(i18n.getIconFor("editor.dalos.action.tolanguage.to.icon"));
	    return lp;
	}
	
	
	protected class toLangComboActionListener implements ActionListener {	
		public void actionPerformed(ActionEvent e) {
			((toLangAction)((JComboBox)e.getSource()).getSelectedItem()).actionPerformed(e);
		}
	}
	
	protected class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		public ComboBoxRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		
		public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
			int selectedIndex = ((toLangAction)value).getIndex();
			
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			toLangAction tL = (toLangAction)toLangActions[selectedIndex];
			setIcon(i18n.getIconFor(tL.getIconKey()));
			return this;
		}		
	}
	
}


