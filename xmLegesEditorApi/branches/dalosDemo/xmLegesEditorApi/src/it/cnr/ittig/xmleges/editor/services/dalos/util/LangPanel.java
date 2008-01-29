package it.cnr.ittig.xmleges.editor.services.dalos.util;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class LangPanel{
	
	Icon iconFrom;
	
	JPanel languageSwitchPanel = null;
	JComboBox toLangCombo = null;
	JLabel lblFrom = null;
	JLabel lblArrow = null;
	
	
	public LangPanel(ListCellRenderer renderer, AbstractAction[] toLangActions, ActionListener toLangComboActionListener){
		languageSwitchPanel  = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
		
		toLangCombo = new JComboBox(toLangActions);
		toLangCombo.addActionListener(toLangComboActionListener);
		
		toLangCombo.setRenderer(renderer);
		toLangCombo.setSelectedIndex(0);	
			
		lblFrom = new JLabel();
		lblArrow = new JLabel();
		
		languageSwitchPanel.add(lblFrom);
		languageSwitchPanel.add(lblArrow);
		languageSwitchPanel.add(toLangCombo);
	}
	

	
	public void setFromIcon(Icon iconFrom){
		lblFrom.setIcon(iconFrom);
	}
	
	public void setArrowIcon(Icon arrowIcon){
		lblArrow.setIcon(arrowIcon);
	}
	
	public JPanel getPanel(){
		return languageSwitchPanel;
	}
	
	public JComboBox getLangCombo(){
		return toLangCombo;
	}
	
	public JLabel getFromLang(){
		return lblFrom;
	}
	
}
