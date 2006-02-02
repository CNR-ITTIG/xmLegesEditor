package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;

import org.w3c.dom.Node;

/**
 * Mouse adapter per il pannello di testo.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
 */
public class XsltMouseAdapter extends MouseAdapter {

	XsltPaneImpl paneImpl;

	JMenu insert = null;
	boolean firstTime = true;

	/**
	 * 
	 */
	public XsltMouseAdapter(XsltPaneImpl paneImpl) {
		super();
		this.paneImpl = paneImpl;
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
			Node node = paneImpl.selectionManager.getActiveNode();
			int start = paneImpl.selectionManager.getTextSelectionStart();
			int end = paneImpl.selectionManager.getTextSelectionEnd();
			updateMenus(node,start,end);
			paneImpl.popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}

	}

	public void mousePressed(MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() != MouseEvent.BUTTON3) {
			paneImpl.textPane.getHighlighter().removeAllHighlights();
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		super.mouseEntered(e);
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		super.mouseExited(e);
	}

	protected void updateMenus(Node node, int start, int end) {
		if (firstTime) {
			firstTime = false;
			paneImpl.popupMenu.addSeparator();
		}
		if (insert != null) {
			paneImpl.popupMenu.remove(insert);
		}
		insert = paneImpl.getUtilRulesManager().createMenuInsert(node, start, end);
		paneImpl.popupMenu.add(insert);
    }

}
