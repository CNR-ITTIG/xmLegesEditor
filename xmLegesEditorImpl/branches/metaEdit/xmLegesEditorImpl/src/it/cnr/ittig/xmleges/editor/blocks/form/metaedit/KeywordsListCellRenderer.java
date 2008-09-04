package it.cnr.ittig.xmleges.editor.blocks.form.metaedit;


import it.cnr.ittig.xmleges.core.services.i18n.I18n;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.w3c.dom.Node;

import it.cnr.ittig.xmleges.core.util.dom.UtilDom;



public class KeywordsListCellRenderer extends JLabel implements ListCellRenderer {

	private I18n i18n;
	
	public KeywordsListCellRenderer() {
		setOpaque(true);
		setVerticalAlignment(CENTER);
	}
	
	public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
	
		Color background = Color.WHITE;
		Color foreground = Color.BLACK;
		int size = list.getFont().getSize();
		Font font = new Font(null, Font.PLAIN, size);


		if(value instanceof Node){
			setIcon(i18n.getIconFor("editor.form.metaedit.keywords.item.icon"));
			Node kw = (Node) value;			
			
			if(UtilDom.getAttributeValueAsString(kw, "Dominio").equalsIgnoreCase("S")){
				background = Color.YELLOW;
				foreground = Color.BLACK;
				font = new Font(null, Font.ITALIC, size);
			}
			
			
			if(isSelected) {
				background = list.getSelectionBackground();
				foreground = list.getSelectionForeground();
			} 

			
			setFont(font);
			setText(UtilDom.getAttributeValueAsString(kw, "Name"));
			
			setBackground(background);
			setForeground(foreground);
			
		}
		
		
		
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
