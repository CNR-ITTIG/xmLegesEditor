package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions;

import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.AntiAliasedTextPane;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.DeleteNextPrevAction;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.w3c.dom.Node;

/**
 * Azione cancellazione carattere successivo.
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
 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
 */
public class XsltDeleteNextCharAction extends XsltAction {

	public XsltDeleteNextCharAction() {
		super(DefaultEditorKit.deleteNextCharAction);
	}

	public void actionPerformed(ActionEvent e) {
		AntiAliasedTextPane pane = (AntiAliasedTextPane) getTextComponent(e);

		HTMLDocument doc = (HTMLDocument) pane.getDocument();

		Element currElem = doc.getCharacterElement(pane.getCaretPosition());

		Node modNode = pane.getXsltMapper().getDomById(pane.getElementId(currElem));

		if (pane.getXsltMapper().getParentByGen(modNode) != null)
			return;

		Element[] containingSpans = pane.getEnclosingSpans(currElem);
		int start = currElem.getStartOffset() + 1;
		int end = currElem.getEndOffset();
		if (containingSpans != null) {
			start = containingSpans[0].getEndOffset() + 1;
			end = containingSpans[1].getStartOffset();
		}

		String elemText = getText(e, currElem);
		String defText = pane.getXsltMapper().getI18nNodeText(modNode);
		if ((modNode.getNodeType() == Node.COMMENT_NODE || modNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) && elemText != null
				&& elemText.equals(defText))
			return;

		if (pane.getCaretPosition() < end || pane.getSelectionStart() != pane.getSelectionEnd()) {
			super.actionPerformed(e);
		} else {
			DeleteNextPrevAction action = pane.getPane().getDeleteNextPrevAction();
			if (action != null) {
				Node modNodeOrParent = pane.getXsltMapper().getDomById(pane.getElementId(currElem), true);
				Element nextElem = pane.getNextTextElement(currElem);
				if (nextElem != null) {
					Node nextNode = pane.getXsltMapper().getDomById(pane.getElementId(nextElem), true);

					action.deleteOnEnd(modNodeOrParent, nextNode);
				}
			}
		}

		elemText = getText(e, currElem);
		if (elemText != null && elemText.length() == 0) {
			insertDefaultText(pane, currElem, doc);
		}
	}
}
