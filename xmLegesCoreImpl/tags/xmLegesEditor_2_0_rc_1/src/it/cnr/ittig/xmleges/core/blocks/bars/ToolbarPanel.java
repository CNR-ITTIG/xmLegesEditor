package it.cnr.ittig.xmleges.core.blocks.bars;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * Pannello che mantiene le toolbar e si aggiorna automaticamente.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * 
 */
public class ToolbarPanel extends JPanel implements ContainerListener, ComponentListener, AncestorListener {

	public ToolbarPanel() {
		addContainerListener(this);
		addComponentListener(this);
		addAncestorListener(this);

		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	}

	public void componentAdded(ContainerEvent e) {
		e.getComponent().addComponentListener(this);
		e.getContainer().addContainerListener(this);
		e.getChild().addComponentListener(this);
		ricomponi();
	}

	public void componentRemoved(ContainerEvent e) {
		ricomponi();
	}

	public void componentHidden(ComponentEvent e) {
		ricomponi();
	}

	public void componentMoved(ComponentEvent e) {
		ricomponi();
	}

	public void componentResized(ComponentEvent e) {
		ricomponi();
	}

	public void componentShown(ComponentEvent e) {
		ricomponi();
	}

	public void ancestorAdded(AncestorEvent ancestorEvent) {
		ricomponi();
	}

	public void ancestorMoved(AncestorEvent ancestorEvent) {
		ricomponi();
	}

	public void ancestorRemoved(AncestorEvent ancestorEvent) {
		ricomponi();
	}

	private void ricomponi() {
		if (getParent() == null)
			return;
		Dimension dimParent = getParent().getSize();
		Component[] comps = getComponents();
		int width = 0;
		int height = 5;
		int r = 1;
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].isVisible()) {
				Dimension dim = comps[i].getSize();
				if (width + dim.width > dimParent.width) {
					if (i > 0)
						r++;
					width = dim.width;
				} else {
					width += dim.width;
				}
				if (height < dim.height)
					height = dim.height;
			}
		}

		setPreferredSize(new Dimension(dimParent.width, r * height));
		setSize(new Dimension(dimParent.width, r * height));
		// invalidate();
		// repaint();
		if (getParent() != null) {
			getParent().invalidate();
			getParent().repaint();
		}
	}

}
