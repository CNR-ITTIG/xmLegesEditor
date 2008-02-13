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

		HTMLDocument doc = (HTMLDocument) pane.getDocument();

		Element currElem = doc.getCharacterElement(pane.getCaretPosition());

		Node modNode = pane.getXsltMapper().getDomById(pane.getElementId(currElem));

		if (pane.getXsltMapper().getParentByGen(modNode) != null)
			return;

		int start = currElem.getStartOffset();
		
		String elemText = getText(e, currElem);
		
		
//      QUESTO E' INUTILE PERCHE' I NODI CON TESTO DI DEFAULT SONO MAPPATI :  getParentByGen(modNode) != null
//		String defText = pane.getXsltMapper().getI18nNodeText(modNode);		
//		if(elemText != null && elemText.equals(defText))
//			return;
		
		// FIXME spostare il controllo da xmLegesCore a xmLegesEditor
		// aggiunto controllo Procesing Instruction <?rif> readonly
		
		if(modNode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE && modNode.getNodeValue().startsWith("<?rif")) {
			return;
		}
		
		//if(pane.getCaretPosition() == start+1  && pane.getSelectionStart() == pane.getSelectionEnd()) return;
		
		if (pane.getCaretPosition() > start+1 || pane.getSelectionStart() != pane.getSelectionEnd()) { // se non siamo all'inizio del tag o c'e' selezione
			super.actionPerformed(e);
			elemText = getText(e, currElem);
			if (elemText != null && elemText.length() == 0) // elemento svuotato;  
				insertDefaultText(pane, currElem, doc);
		} 
		else{     // su inizio tag attivazione della action specifica
			DeleteNextPrevAction action = pane.getPane().getDeleteNextPrevAction();
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
