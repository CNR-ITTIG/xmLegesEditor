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
 *  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5036141
 *  Bug ID:   	 5036141
 *  Synopsis 	JTextArea stops firing CaretEvents after Drag-And-Drop			
 * </p>
 * 
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
			e.printStackTrace();
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
		// DEBUGGING
		// System.err.println("XsltTransferHandler.importString() : TARGET POSITION = "+pos+" PANE: "+tc.getPane().getName());
		HTMLDocument doc = tc.getHTMLDocument();

		Element currElem = doc.getCharacterElement(pos);
		Element enclosingSpan = tc.getMappedSpan(pos);
		if (enclosingSpan == null) {
			shouldRemove = false;
			return true;
		}

		Node currNode = tc.getXsltMapper().getDomById(tc.getElementId(currElem));
		
		// DEBUGGING
		// System.err.println("XsltTransferHandler.importString() : TARGET NODE CONTENT = "+currNode.getNodeValue()+ " PANE: "+tc.getPane().getName());
		

		if (tc.getXsltMapper().getParentByGen(currNode) != null) {
			try {
				doc.replace(enclosingSpan.getStartOffset()+1, enclosingSpan.getEndOffset() - enclosingSpan.getStartOffset()-2, str, currElem.getAttributes());
			} catch (BadLocationException ble) {
				ble.printStackTrace();
			}
		} else {
			try {
				doc.insertString(pos, str, doc.getCharacterElement(enclosingSpan.getStartOffset()).getAttributes());
				
				
				// a questo punto parte il setDot ma non arriva il caretEvent a AntialiasedTextPane.caretUpdate; 
				int currpos = pos+str.length();
				
				Element currelem = tc.getMappedSpan(currpos);
				if(currelem != null){ 
					
					//Node selNode = tc.getXsltMapper().getDomById(id, true);
					//tc.update(selNode);
					tc.removeAllHighlights();
					tc.highlightElement(currelem);
				}
				
				//tc.getPane().getSelectionManager().setActiveNode(this, currNode);
				//tc.getPane().fireSelectionChanged(currNode,-1,-1);
				//tc.getPane().firePaneStatusChanged();
				
				tc.getCaret().setDot(pos+str.length());
				
				// DEBUGGING
				//System.err.println("XsltTransferHandler.importString() -->     setDot   : NEW TARGET POSITION = "+pos+str.length()+" PANE: "+tc.getPane().getName());
				//System.err.println("XsltTransferHandler.importString() -->     should write on "+id);

			} catch (BadLocationException ble) {
				ble.printStackTrace();
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
					//Node currNode = source.getXsltMapper().getDomById(source.getElementId(currElem));
					int start = currElem.getStartOffset(), end = currElem.getEndOffset();
					if (p0.getOffset() == start+1 && p1.getOffset() == end-1) {
						doc.replace(p0.getOffset(), p1.getOffset() - p0.getOffset(), source.getDefaultText(currElem),currElem.getAttributes());
					} else {
						//System.err.println("XsltTranferHandler: ---> cleaning up");
						doc.remove(p0.getOffset(), p1.getOffset() - p0.getOffset());
					}
					//source.update(currNode);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
		source = null;
	}
}
