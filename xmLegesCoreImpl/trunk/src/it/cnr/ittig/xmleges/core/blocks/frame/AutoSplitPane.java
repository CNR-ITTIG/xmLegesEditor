package it.cnr.ittig.xmleges.core.blocks.frame;

import it.cnr.ittig.services.manager.Logger;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei </a>
 */
public class AutoSplitPane extends JPanel {
	Logger logger;

	JSplitPane splitPane;

	Component topLeft = null;

	Component bottomRight = null;

	boolean internal;

	int location;

	public AutoSplitPane(FrameImpl frameImpl, int orientation) {
		this.logger = frameImpl.getLogger();
		setDoubleBuffered(FrameImpl.DOUBLE_BUFFER);
		splitPane = new JSplitPane(orientation);
		splitPane.setOneTouchExpandable(true);
		setLayout(new BorderLayout());
	}

	public void add(Component topLeft, Component bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
		update();
	}

	protected void update() {
		if (topLeft == null && bottomRight == null) {
			setVisible(false);
		} else {
			removeAll();
			setVisible(false);
			if ((topLeft == null || !topLeft.isVisible()) && (bottomRight != null && bottomRight.isVisible())) {
				logger.debug("autosplit with only bottomRight");
				add(bottomRight, BorderLayout.CENTER);
				setVisible(true);
			} else if ((bottomRight == null || !bottomRight.isVisible()) && (topLeft != null && topLeft.isVisible())) {
				logger.debug("autosplit with only topLeft");
				setVisible(true);
				add(topLeft, BorderLayout.CENTER);
			} else if (!topLeft.isVisible() && !bottomRight.isVisible()) {
				setVisible(false);
			} else {
				this.location = splitPane.getDividerLocation();
				logger.debug("autosplit with topLeft and bottomRight");
				if (splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
					splitPane.setLeftComponent(topLeft);
					splitPane.setRightComponent(bottomRight);
				} else {
					splitPane.setTopComponent(topLeft);
					splitPane.setBottomComponent(bottomRight);
				}
				add(splitPane, BorderLayout.CENTER);
				splitPane.setDividerLocation(location);
				setVisible(true);
			}
		}
	}

	public void setDividerLocation(int location) {
		this.location = location;
		splitPane.setDividerLocation(location);
	}

	public int getDividerLocation() {
		return splitPane.getDividerLocation();
	}

}
