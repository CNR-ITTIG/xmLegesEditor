package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions;

import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.AntiAliasedTextPane;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.KeyTypedAction;

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
		KeyTypedAction action = pane.getPane().getKeyTypedAction();
		

		int caret = pane.getCaretPosition();
		Element span = pane.getMappedSpan(caret);
		
		//DEBUG
		//System.err.println("XsltKeyTypedAction: writes on caret "+caret);
		//System.err.println("XsltKeyTypedAction: writes on span "+span.toString());

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
		
		//DEBUG
		//System.err.println("XsltKeyTypedAction: writes on ID "+pane.getElementId(span));
		//System.err.println("XsltKeyTypedAction: writes on Node "+modNode.getNodeValue());

		if(modNode == null) return;
		
		int start = span.getStartOffset();
		int end = span.getEndOffset();
		
		if(action != null && !action.canKeyTyped(modNode, start, end))     // azione specifica su keyTyped
			return;

		// casi in cui va sostituita l'etichetta vuota
		// 1 scrittura su nodi etichetta generati [generated node]
		// 2[COMMENT e PI hanno una gestione diversa non avendo figli testo]
		// 3 gestione etichette testo che compaiono su inserimento (#PCDATA)
		
		if (pane.getXsltMapper().getParentByGen(modNode) != null   
			|| (modNode.getNodeType() == Node.COMMENT_NODE && getText(e, span).equals(pane.getXsltMapper().getI18nNodeText(modNode))) 
			|| (modNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && getText(e, span).equals(pane.getXsltMapper().getI18nNodeText(modNode)))
			|| (getText(e, span).equals(pane.getXsltMapper().getI18nNodeText(modNode.getParentNode())))   // testo = etichetta 
			){		
				pane.select(start+1, end-1);
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
