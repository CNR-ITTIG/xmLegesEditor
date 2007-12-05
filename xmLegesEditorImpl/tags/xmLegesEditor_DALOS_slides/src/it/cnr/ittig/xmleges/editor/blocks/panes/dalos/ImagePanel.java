package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	
	private Image image = null;
	
	private int iWidth;
	private int iHeight;
	
	public ImagePanel(Image image) {
		
		this.image = image;
		this.iWidth = image.getWidth(this) / 2;
		this.iHeight = image.getHeight(this) / 2;		
	}

	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		if(image != null) {
			int x = this.getParent().getWidth() / 2 - iWidth;
			int y = this.getParent().getHeight() / 2 - iHeight;
			g.drawImage(image, x, y, this);
		}
	}
}
