package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;

import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;



public class LemmaListCellRenderer extends JLabel implements ListCellRenderer {

	private I18n i18n;
	
	public LemmaListCellRenderer() {
		setOpaque(true);
		setVerticalAlignment(CENTER);
	}
	
	public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
	
		Color background;
		Color foreground;
		
		if(isSelected) {
			background = list.getSelectionBackground();
			foreground = list.getSelectionForeground();
		} else {
			background = Color.WHITE;
			//foreground = Color.RED;
			foreground = Color.BLACK;
		}

		if(value instanceof Synset)
			setIcon(i18n.getIconFor("editor.panes.dalos.synsetlist.icon"));
		
//		if(value instanceof Lemma) {
//			setIcon(UtilEditor.getIcon("lemma1"));
//		} else if(value instanceof Concetto) {
//			setIcon(UtilEditor.getIcon("concetto"));
//		}
		
		//setFont();
		setText(value.toString());
		
		setBackground(background);
		setForeground(foreground);
		
		return this;
	}
	
	/*
	 * Non deve essere specificato un valore preciso
	 * per la larghezza, ma solo per l'altezza!
	 * 
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		
		Dimension d = super.getPreferredSize();
		d.height = 17;
		return d;
	}
	
	public void setI18n(I18n i18n){
		this.i18n = i18n;
	}
	
}
