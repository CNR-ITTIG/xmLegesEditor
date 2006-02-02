package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions;

import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.XsltDocument;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.XsltTextPane;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;

/**
 * Azione cancellazione carattere precedente.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class XsltDeletePrevCharAction extends XsltAction {

	XsltBackwardAction xsltBackwardAction;

	public XsltDeletePrevCharAction() {
		super(DefaultEditorKit.deletePrevCharAction);
	}

	public void setBackwardAction(XsltBackwardAction xsltBackwardAction) {
		this.xsltBackwardAction = xsltBackwardAction;
	}

	public void actionPerformed(ActionEvent e) {
		if (getLogger(e).isDebugEnabled())
			getLogger(e).debug(getName().toUpperCase() + " BEGIN");
		XsltDocument.XsltLeafElement leaf = getActiveLeaf(e);

		if (leaf == null)
			return;

		XsltTextPane pane = getXsltTextPane(e);

		int tmpCaret = getCurrentCaretPosition(e);
		int selStart = pane.getSelectionStart();
		int selEnd = pane.getSelectionEnd();
		if (!leaf.isEmpty() && (tmpCaret != leaf.getStartOffset() || selStart != selEnd)) {
			int newCaretPos;
			if (selStart == selEnd) {
				newCaretPos = tmpCaret - 1;
			} else {
				newCaretPos = selStart;
			}
			if (leaf.getText().length() == 1) {
				leaf.updateDom((String) null);
			} else {
				super.actionPerformed(e);
				leaf.updateDom(getText(e, leaf));
			}
			XsltBackwardAction back = new XsltBackwardAction();
			back.actionPerformed(e);
			getXsltTextPane(e).setCaretPosition(newCaretPos);
		}

		if (getLogger(e).isDebugEnabled())
			getLogger(e).debug(getName().toUpperCase() + " END");
	}

}
