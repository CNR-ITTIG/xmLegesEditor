package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dnd.DomTransferHandler;

import javax.swing.JComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.html.HTMLDocument;

import org.w3c.dom.Node;

/**
 * Classe per la gestione del <i>DragAndDrop </i> per l'
 * <code>XsltPaneImpl</code>.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
 */
public class XsltTransferHandler extends DomTransferHandler {

	AntiAliasedTextPane source = null;

	Position p0, p1;

	public XsltTransferHandler() {
	}

	protected boolean isDomSupported(JComponent c) {
		AntiAliasedTextPane pane = (AntiAliasedTextPane) c;
		Logger logger = pane.getPane().getLogger();
		SelectionManager selectionManager = pane.getPane().getSelectionManager();
		logger.info("sel:" + selectionManager.getTextSelectionStart() + " - " + selectionManager.getTextSelectionEnd());
		return selectionManager.getActiveNode() != null && !selectionManager.isTextSelected();
	}

	protected Node exportDom(JComponent c) {
		SelectionManager selectionManager = ((AntiAliasedTextPane) c).getPane().getSelectionManager();
		return selectionManager.getActiveNode();
	}

	protected boolean importDom(JComponent c, Node node) {
		return false;
	}

	protected boolean isStringSupported(JComponent c) {
		// SelectionManager selectionManager =
		// ((AntiAliasedTextPane)c).getPane().getSelectionManager();
		// Node n = selectionManager.getActiveNode();
		// int start = selectionManager.getTextSelectionStart();
		// int end = selectionManager.getTextSelectionEnd();
		// return n != null && start != end && start >= 0;
		return c instanceof JTextComponent;
	}

	protected String exportString(JComponent c) {
		source = (AntiAliasedTextPane) c;
		int start = source.getSelectionStart();
		int end = source.getSelectionEnd();
		Document doc = source.getDocument();
		if (start == end) {
			return null;
		}
		try {
			p0 = doc.createPosition(start);
			p1 = doc.createPosition(end);
		} catch (BadLocationException e) {
		}
		shouldRemove = true;
		return source.getSelectedText();
	}

	protected boolean importString(JComponent c, String str) {

		AntiAliasedTextPane tc = (AntiAliasedTextPane) c;

		if (tc.equals(source) && (tc.getCaretPosition() >= p0.getOffset()) && (tc.getCaretPosition() <= p1.getOffset())) {
			shouldRemove = false;
			return true;
		}

		int pos = tc.getCaretPosition();
		HTMLDocument doc = tc.getHTMLDocument();

		Element currElem = doc.getCharacterElement(pos);
		Element[] enclosingSpans = tc.getEnclosingSpans(currElem);
		if (enclosingSpans == null) {
			shouldRemove = false;
			return true;
		}

		Node currNode = tc.getXsltMapper().getDomById(tc.getElementId(currElem));

		if (tc.getXsltMapper().getParentByGen(currNode) != null) {
			try {
				doc.replace(enclosingSpans[0].getEndOffset() + 1, enclosingSpans[1].getStartOffset()
						- enclosingSpans[0].getEndOffset() - 1, str, currElem.getAttributes());
			} catch (BadLocationException ble) {
			}
		} else {
			try {
				doc.insertString(pos, str, doc.getCharacterElement(enclosingSpans[0].getEndOffset() + 1)
						.getAttributes());
			} catch (BadLocationException ble) {
			}
		}

		return true;
	}

	protected void cleanup(JComponent c, boolean remove) {
		if (shouldRemove && remove) {
			if ((p0 != null) && (p1 != null) && (p0.getOffset() != p1.getOffset())) {
				try {
					HTMLDocument doc = source.getHTMLDocument();
					Element currElem = source.getHTMLDocument().getCharacterElement(p0.getOffset());
					Node currNode = source.getXsltMapper().getDomById(source.getElementId(currElem));
					Element[] containingSpans = source.getEnclosingSpans(currElem);
					int start = currElem.getStartOffset() + 1, end = currElem.getEndOffset();
					String elemText = null;
					if (containingSpans != null) {
						start = containingSpans[0].getEndOffset() + 1;
						end = containingSpans[1].getStartOffset();
					}
					if (p0.getOffset() == start && p1.getOffset() == end) {
						doc.replace(p0.getOffset(), p1.getOffset() - p0.getOffset(), source.getDefaultText(currElem),
								currElem.getAttributes());
					} else {
						doc.remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
					}
				} catch (BadLocationException e) {
				}
			}
		}
		source = null;
	}
}
