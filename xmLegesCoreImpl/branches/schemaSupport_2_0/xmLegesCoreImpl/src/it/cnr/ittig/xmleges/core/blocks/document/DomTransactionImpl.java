package it.cnr.ittig.xmleges.core.blocks.document;

import it.cnr.ittig.xmleges.core.services.document.DomEdit;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;

import java.util.Vector;

import org.w3c.dom.Node;

/**
 * Implementazione dell'interfaccia
 * <code>it.cnr.ittig.xmleges.editor.services.document.DomTransaction</code> necessaria per individuare
 * un gruppo di modifiche al documento.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */

public class DomTransactionImpl extends Vector implements EditTransaction {

	Object source;

	DomEdit[] edits = null;

	Node activeNode;

	Node[] selectedNodes;

	boolean isTextSelection;

	int textSelectionStart;

	int textSelectionEnd;

	public DomTransactionImpl(Object source, Node activeNode, Node[] selectedNodes, boolean isTextSelection, int textSelectionStart, int textSelectionEnd) {
		this.source = source;
		this.activeNode = activeNode;
		this.selectedNodes = selectedNodes;
		this.isTextSelection = isTextSelection;
		this.textSelectionStart = textSelectionStart;
		this.textSelectionEnd = textSelectionEnd;
	}

	public DomEdit[] getEdits() {
		if (edits == null || edits.length != size()) {
			edits = new DomEdit[size()];
			copyInto(edits);
		}
		return edits;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("[");
		for (int i = 0; i < size(); i++)
			if (this.get(i) == null)
				sb.append("null\n");
			else
				sb.append(this.get(i).toString() + "\n");
		sb.append(']');
		return sb.toString();
	}

	public Object getSource() {
		return this.source;
	}

	public Node getActiveNode() {
		return activeNode;
	}

	public Node[] getSelectedNodes() {
		return selectedNodes;
	}

	public boolean isTextSelection() {
		return isTextSelection;
	}

	public int getTextSelectionEnd() {
		return textSelectionEnd;
	}

	public int getTextSelectionStart() {
		return textSelectionStart;
	}
}