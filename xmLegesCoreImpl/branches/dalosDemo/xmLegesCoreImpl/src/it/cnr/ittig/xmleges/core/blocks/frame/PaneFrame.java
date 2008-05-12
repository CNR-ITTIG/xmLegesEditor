package it.cnr.ittig.xmleges.core.blocks.frame;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.frame.Pane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

/**
 * @author Valentina Billi, <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei </a>
 */

public class PaneFrame extends JPanel implements FocusListener, HierarchyListener {

	/** Active pane's border. */
	Border activeBorder;

	/** Deactive pane's border. */
	Border deactiveBorder;

	Logger logger;

	Pane pane = null;

	String pos = null;

	int index = 100;

	FrameImpl frameImpl;

	boolean scrollable;

	boolean show = false;
	
	boolean isActive = false;

	boolean highlighted = false;

	public PaneFrame(FrameImpl frameimpl, String pos, int index) {
		this.frameImpl = frameimpl;
		this.logger = frameImpl.getLogger();
		this.pos = pos;
		this.index = index;
		this.activeBorder = BorderFactory.createLineBorder(frameImpl.getSelectedColor(), 2);
		this.deactiveBorder = BorderFactory.createLineBorder(getBackground(), 2);
		setDoubleBuffered(FrameImpl.DOUBLE_BUFFER);
		setBorder(deactiveBorder);
		setLayout(new BorderLayout());
		setVisible(false);
	}

	PaneFrame(FrameImpl frameImpl, Pane pane, boolean scrollable, String pos, int index) {
		this(frameImpl, pos, index);
		setPane(pane, scrollable);
	}

	public void setPane(Pane pane) {
		setPane(pane, this.scrollable);
	}

	public void setPane(Pane pane, boolean scrollable) {
		setName(pane.getName());
		this.pane = pane;
		this.scrollable = scrollable;
		removeAll();
		Component comp = pane.getPaneAsComponent();
		comp.addHierarchyListener(this);
		if (comp instanceof JComponent)
			((JComponent) comp).setDoubleBuffered(FrameImpl.DOUBLE_BUFFER);
		if (scrollable) {
			JScrollPane scroll = new JScrollPane();
			scroll.setDoubleBuffered(FrameImpl.DOUBLE_BUFFER);
			scroll.setViewportView(comp);
			add(scroll, BorderLayout.CENTER);
		} else {
			add(comp, BorderLayout.CENTER);
		}
		addDeepFocusListener(this);
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public boolean isShow() {
		return this.show;
	}

	public boolean hasPane() {
		return this.pane != null;
	}

	public void setPaneBorder(boolean active) {
		this.isActive = active;
		setBorder(active ? activeBorder : deactiveBorder);
	}
	
	public boolean isActive(){
		return this.isActive;
	}

	public Pane getPane() {
		return pane;
	}

	public String getPos() {
		return pos;
	}

	public int getIndex() {
		return index;
	}

	public boolean isHighlighted() {
		return this.highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public void updateFocusListeners() {
		removeDeepFocusListener(this);
		addDeepFocusListener(this);
	}

	protected void addDeepFocusListener(Component c) {
		if (c == null)
			return;
		c.addFocusListener(this);
		if (c instanceof Container) {
			Component[] cs = ((Container) c).getComponents();
			for (int i = 0; i < cs.length; i++)
				addDeepFocusListener(cs[i]);
		}
	}

	protected void removeDeepFocusListener(Component c) {
		if (c == null)
			return;
		c.removeFocusListener(this);
		if (c instanceof Container) {
			Component[] cs = ((Container) c).getComponents();
			for (int i = 0; i < cs.length; i++)
				removeDeepFocusListener(cs[i]);
		}
	}

	// ///////////////////////////////////////////////// FocusListener Interface
	public void focusGained(FocusEvent e) {
		frameImpl.focusGained(this);
	}

	public void focusLost(FocusEvent e) {
		frameImpl.focusLost(this);
	}

	// ///////////////////////////////////////////// HierarchyListener Interface
	public void hierarchyChanged(HierarchyEvent e) {
		// TODO Auto-generated method stub
		// removeDeepFocusListener(e.getChanged());
		// addDeepFocusListener(e.getChanged());
	}
}
