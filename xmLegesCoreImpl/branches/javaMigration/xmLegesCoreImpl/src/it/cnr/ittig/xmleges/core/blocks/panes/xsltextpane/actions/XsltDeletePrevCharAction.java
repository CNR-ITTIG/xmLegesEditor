package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.actions;

import it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane.AntiAliasedTextPane;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.DeleteNextPrevAction;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

import org.w3c.dom.Node;

/**
 * Azione cancellazione carattere precedente.
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
public class XsltDeletePrevCharAction extends XsltAction {

	public XsltDeletePrevCharAction() {
		super(DefaultEditorKit.deletePrevCharAction);
	}

	public void actionPerformed(ActionEvent e) {
		AntiAliasedTextPane pane = (AntiAliasedTextPane) getTextComponent(e);
		DeleteNextPrevAction action = pane.getPane().getDeleteNextPrevAction();

		HTMLDocument doc = (HTMLDocument) pane.getDocument();

		Element currElem = pane.getMappedSpan(pane.getCaretPosition());

		Node modNode = pane.getXsltMapper().getDomById(pane.getElementId(currElem));

		if (pane.getXsltMapper().getParentByGen(modNode) != null)        // nodo vuoto [etichetta]
			return;
		
		if(action!=null && !action.canBackSpaceOnStart(modNode, null))   // azione non ammissibile per il nodo
			return;

		int start = currElem.getStartOffset();
		
		String elemText = getText(e, currElem);
		String defText = pane.getXsltMapper().getI18nNodeText(modNode);
		
		
		if ((modNode.getNodeType() == Node.COMMENT_NODE || modNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) && elemText != null && elemText.equals(defText))
			return;
		
	    // gestione etichette testo (#PCDATA)
		if((getText(e, currElem).equals(pane.getXsltMapper().getI18nNodeText(modNode.getParentNode()))))
				return;
	
		
		if (pane.getCaretPosition() > start+1 || pane.getSelectionStart() != pane.getSelectionEnd()) { // se non siamo all'inizio del tag o c'e' selezione
			super.actionPerformed(e);
			elemText = getText(e, currElem);
			if (elemText != null && elemText.length() == 0) // elemento svuotato;  
				insertDefaultText(pane, currElem, doc);
		} 
		else{     																					  // su inizio tag attivazione della action specifica			
			if (action != null) {     
				Node modNodeOrParent = pane.getXsltMapper().getDomById(pane.getElementId(currElem), true);
				Element prevElem = pane.getPrevTextElement(currElem);
				if (prevElem != null) {
					Node prevNode = pane.getXsltMapper().getDomById(pane.getElementId(prevElem), true);
					action.backspaceOnStart(modNodeOrParent, prevNode);
				}
			}
		}
	}
}
