package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions;

import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.AntiAliasedTextPane;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.w3c.dom.Node;

/**
 * Azione pressione tasto.
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
 * @author <a href="mailto:m.bruni@onetech.it">Matteo Bruni</a>
 */
public class XsltKeyTypedAction extends XsltAction {

	final static String SPACE = " ";

	public XsltKeyTypedAction() {
		super(DefaultEditorKit.defaultKeyTypedAction);
	}

	public void actionPerformed(ActionEvent e) {

		String actionCommand = e.getActionCommand();

		if (!checkKeys(actionCommand, e.getModifiers()))
			return;

		AntiAliasedTextPane pane = (AntiAliasedTextPane) getTextComponent(e);

		HTMLDocument doc = (HTMLDocument) pane.getDocument();

		Element currElem = doc.getCharacterElement(pane.getCaretPosition());
		Element enclosingSpan = pane.getEnclosingSpan(currElem);

		if (enclosingSpan == null)
			return;

		// verifica di spazi prima o dopo il cursore. se ci sono  o siamo a inizio tag non inserisce
		if (SPACE.equals(actionCommand)) {
			int caret = pane.getCaretPosition();
			try {    
				if (SPACE.equals(pane.getText(caret - 1, 1)) || caret-1 == currElem.getStartOffset())
					return;
			} catch (BadLocationException ex) {
			}
			try {
				if (SPACE.equals(pane.getText(caret, 1)) && caret + 1 != currElem.getEndOffset())
					return;
			} catch (BadLocationException ex) {
			}
		}

		Node modNode = pane.getXsltMapper().getDomById(pane.getElementId(currElem));

		
		// FIXME spostare il controllo da xmLegesCore a xmLegesEditor
		// aggiunto controllo Processing Instruction ?rif readonly
		if(modNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && modNode.getNodeValue().startsWith("<rif")) {
			return;
		}
		
		if (pane.getXsltMapper().getParentByGen(modNode) != null
				|| (modNode.getNodeType() == Node.COMMENT_NODE && getText(e, currElem).equals(pane.getXsltMapper().getI18nNodeText(modNode)))
				|| (modNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && getText(e, currElem).equals(pane.getXsltMapper().getI18nNodeText(modNode)))) {
			pane.select(enclosingSpan.getStartOffset()+1, enclosingSpan.getEndOffset()-1);
		}

		super.actionPerformed(e);
	}

	protected boolean checkKeys(String content, int mod) {
		if ((content != null) && (content.length() > 0) && ((mod & ActionEvent.ALT_MASK) == (mod & ActionEvent.CTRL_MASK))) {
			char c = content.charAt(0);
			if ((c >= 0x20) && (c != 0x7F))
				return true;
		}
		return false;
	}
}
