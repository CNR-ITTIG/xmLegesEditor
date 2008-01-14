package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions;

import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.AntiAliasedTextPane;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.InsertBreakAction;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.w3c.dom.Node;

/**
 * Azione su invio.
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
public class XsltInsertBreakAction extends XsltAction {

	public XsltInsertBreakAction() {
		super(DefaultEditorKit.insertBreakAction);
	}

	public void actionPerformed(ActionEvent e) {
		AntiAliasedTextPane pane = (AntiAliasedTextPane) getTextComponent(e);
		InsertBreakAction action = pane.getPane().getInsertBreakAction();
		if (action != null) {
			HTMLDocument doc = (HTMLDocument) pane.getDocument();

			Element currElem = doc.getCharacterElement(pane.getCaretPosition());
			Element enclosingSpan = pane.getMappedSpan(pane.getCaretPosition());
			if (enclosingSpan == null)
				return;

			Node modNode = pane.getXsltMapper().getDomById(pane.getElementId(currElem), true);
			
			// FIXME spostare il controllo da xmLegesCore a xmLegesEditor
			// aggiunto controllo Procesing Instruction <?rif> readonly
			if(modNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && modNode.getNodeValue().startsWith("<rif")) {
				return;
			}
			
			int relSelStart = pane.getSelectionStart() - enclosingSpan.getStartOffset();
			int relSelEnd = pane.getSelectionEnd() - enclosingSpan.getStartOffset();

			action.insertBreak(modNode, relSelStart, relSelEnd);
		}
	}
}
