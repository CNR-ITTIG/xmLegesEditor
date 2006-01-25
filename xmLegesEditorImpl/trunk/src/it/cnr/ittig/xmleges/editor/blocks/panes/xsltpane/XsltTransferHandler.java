package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dnd.DomTransferHandler;

import javax.swing.JComponent;

import org.w3c.dom.Node;

/**
 * Classe per la gestione del <i>DragAndDrop </i> per l' <code>XsltPaneImpl</code>.
 * 
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class XsltTransferHandler extends DomTransferHandler {

	XsltPaneImpl xsltPaneImpl;

	Logger logger;

	SelectionManager selectionManager;

	public XsltTransferHandler(XsltPaneImpl xsltPaneImpl) {
		this.xsltPaneImpl = xsltPaneImpl;
		this.logger = xsltPaneImpl.getLogger();
		this.selectionManager = xsltPaneImpl.getSelectionManager();
	}

	protected boolean isDomSupported(JComponent c) {
		logger.info("sel:" + selectionManager.getTextSelectionStart() + " - " + selectionManager.getTextSelectionEnd());
		return selectionManager.getActiveNode() != null && !selectionManager.isTextSelected();
	}

	protected Node exportDom(JComponent c) {
		return selectionManager.getActiveNode();
	}

	protected boolean importDom(JComponent c, Node node) {
		logger.debug("XsltTransferHandler.importDom:" + node);
		int pos = xsltPaneImpl.getXsltTextPane().getCaretPosition();
		XsltDocument d = xsltPaneImpl.getXsltDocument();
		XsltDocument.XsltLeafElement leaf = d.getLeafByPos(pos);
		String text = leaf.getText();
		pos -= leaf.getStartOffset();
		return xsltPaneImpl.getUtilRulesManager().insertSubTreeInText(node, pos, true);
	}

	protected boolean isStringSupported(JComponent c) {
		Node n = selectionManager.getActiveNode();
		int start = selectionManager.getTextSelectionStart();
		int end = selectionManager.getTextSelectionEnd();
		return n != null && start != end && start >= 0;
	}

	protected String exportString(JComponent c) {
		int start = selectionManager.getTextSelectionStart();
		int end = selectionManager.getTextSelectionEnd();
		String s = selectionManager.getActiveNode().getNodeValue();
		if (s != null)
			return s.substring(start, end);
		else
			return null;
	}

	protected boolean importString(JComponent c, String str) {
		logger.debug("XsltTransferHandler.importString:" + str);
		int pos = xsltPaneImpl.getXsltTextPane().getCaretPosition();
		XsltDocument d = xsltPaneImpl.getXsltDocument();
		XsltDocument.XsltLeafElement leaf = d.getLeafByPos(pos);
		String text = leaf.getText();
		pos -= leaf.getStartOffset();
		text = text.substring(0, pos) + str + text.substring(pos);
		// TODO transaction
		leaf.updateDom(text);
		return true;
	}

	protected void cleanup(JComponent c, boolean remove) {
		logger.debug("XsltTransferHandler.cleanup");
	}
}
