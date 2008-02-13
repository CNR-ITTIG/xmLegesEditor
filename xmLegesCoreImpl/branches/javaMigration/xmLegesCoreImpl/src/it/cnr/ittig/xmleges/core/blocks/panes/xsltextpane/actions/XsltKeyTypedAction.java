package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions;

import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.AntiAliasedTextPane;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;

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

		int caret = pane.getCaretPosition();
		Element span = pane.getMappedSpan(caret);

		if (span == null)
			return;

		// verifica di spazi prima o dopo il cursore. 
		// se ci sono  o siamo a inizio tag non inserisce
		/* FIXME: 
		 * va fatta verificando di essere all'interno dello stesso elemento
		 */
		if (SPACE.equals(actionCommand)) {
			try {
				if (SPACE.equals(pane.getText(caret - 1, 1))) return;
			} catch (BadLocationException ex) { }
			try {
				if (SPACE.equals(pane.getText(caret, 1))) return;
			} catch (BadLocationException ex) {	}
		}

		Node modNode = pane.getXsltMapper().getDomById(pane.getElementId(span));

		if(modNode == null) return;
		
		// FIXME spostare il controllo da xmLegesCore a xmLegesEditor
		// aggiunto controllo Processing Instruction ?rif readonly
		if(modNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && modNode.getNodeValue().startsWith("<rif")) {
			return;
		}
		
		int start = span.getStartOffset();
		int end = span.getEndOffset();

		if (pane.getXsltMapper().getParentByGen(modNode) != null) {	
			pane.select(start+1, end-1);
		} 
//		QUESTO E' INUTILE PERCHE' I NODI CON TESTO DI DEFAULT SONO MAPPATI :  getParentByGen(modNode) != null		
//		else {
//			String elemText = getText(e, span);
//			String defText = pane.getDefaultText(span);
//		
//			if(elemText != null && defText != null && defText.equals(elemText)) {
//				pane.select(start+1, end-1);
//			} else if(elemText == null || defText == null) {
//				return;
//			}
//		}
		
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
