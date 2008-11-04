package it.cnr.ittig.xmleges.core.blocks.frame;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

/**
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
 * <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei </a>
 */
public class AutoTabbedPane extends JPanel {
	Logger logger;

	I18n i18n;

	FrameImpl frameImpl;

	Vector paneFrames;

	JTabbedPane tabbedPane = new JTabbedPane();

	JPopupMenu menu;

	public AutoTabbedPane(FrameImpl frameImpl, String name) {
		this.frameImpl = frameImpl;
		this.logger = frameImpl.getLogger();
		this.i18n = frameImpl.getI18n();
		setName(name);
		setDoubleBuffered(FrameImpl.DOUBLE_BUFFER);
		tabbedPane.setDoubleBuffered(FrameImpl.DOUBLE_BUFFER);
		tabbedPane.addMouseListener(new TabbedMouseAdapter());
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		menu = createPopupMenu();
		update();
	}

	protected JPopupMenu createPopupMenu() {
		JPopupMenu popup = new JPopupMenu();
		popup.add(frameImpl.maximizeAction);
		popup.add(frameImpl.restoreAction);
		popup.addSeparator();
		popup.add(frameImpl.closeAction);
		popup.addSeparator();
		popup.add(frameImpl.reloadAction);
		return popup;
	}

	public void set(Vector paneFrames) {
		this.paneFrames = paneFrames;
		update();
	}

	public void setSelected(PaneFrame paneFrame) {
		tabbedPane.setSelectedComponent(paneFrame);
	}
	
	public boolean isSelected(PaneFrame paneFrame){
		return tabbedPane.getSelectedComponent().equals((Component)paneFrame);
	}

	public void update() {
		if (paneFrames == null)
			return;
		Enumeration en = paneFrames.elements();
		Vector v = new Vector();
		while (en.hasMoreElements()) {
			PaneFrame paneFrame = (PaneFrame) en.nextElement();
			logger.debug("paneFrame.hasPane():" + paneFrame.hasPane());
			if (paneFrame.hasPane() && paneFrame.isShow())
				v.addElement(paneFrame);
		}
		if (v.size() == 0) {
			logger.debug("autotab without visible panes");
			setVisible(false);
		} else {
			setVisible(true);
			logger.debug("autotab with " + v.size() + " elements");
			// add(tabbedPane, BorderLayout.CENTER);
			tabbedPane.removeAll();
			for (en = v.elements(); en.hasMoreElements();) {
				PaneFrame paneFrame = (PaneFrame) en.nextElement();
				tabbedPane.addTab(null, null, paneFrame, null);
			}
			updateTabNames();
		}
		updateTabColor();
	}

	protected void updateTabNames() {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			PaneFrame paneFrame = (PaneFrame) tabbedPane.getComponentAt(i);
			String name = paneFrame.getName();
			tabbedPane.setTitleAt(i, name != null ? i18n.getTextFor(name + ".text") : "");
			tabbedPane.setIconAt(i, name != null ? i18n.getIconFor(name + ".icon") : null);
			tabbedPane.setToolTipTextAt(i, name != null ? i18n.getTextFor(name + ".tooltip") : "");
		}
	}

	public class TabbedMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			frameImpl.focusGained((PaneFrame) tabbedPane.getSelectedComponent());
			updateTabColor();
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
				frameImpl.expand();
			if (e.getButton() == MouseEvent.BUTTON3)
				menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public void updateTabColor() {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (tabbedPane.getSelectedIndex() == i) {
				tabbedPane.setForegroundAt(i, frameImpl.getSelectedColor());
			} else {
				PaneFrame paneFrame = (PaneFrame) tabbedPane.getComponentAt(i);
				tabbedPane.setForegroundAt(i, paneFrame.isHighlighted() ? Color.RED : getForeground());
			}
		}
	}

	public void updateTabColor(PaneFrame paneSel, PaneFrame paneUnsel) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			PaneFrame paneFrame = (PaneFrame) tabbedPane.getComponentAt(i);
			if (paneFrame.equals(paneSel))
				tabbedPane.setForegroundAt(i, frameImpl.getSelectedColor());
			if (paneFrame.equals(paneUnsel))
				tabbedPane.setForegroundAt(i, getForeground());
		}
	}
}
