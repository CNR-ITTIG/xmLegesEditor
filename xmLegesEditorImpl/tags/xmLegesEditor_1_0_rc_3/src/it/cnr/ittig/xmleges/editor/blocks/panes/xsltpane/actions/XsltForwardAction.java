package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions;

import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.XsltDocument;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;

/**
 * Azione freccia destra.
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
public class XsltForwardAction extends XsltAction {

	public XsltForwardAction() {
		super(DefaultEditorKit.forwardAction);
	}

	public void actionPerformed(ActionEvent e) {
		if (getLogger(e).isDebugEnabled())
			getLogger(e).debug(getName().toUpperCase() + " BEGIN");
		XsltDocument.XsltLeafElement curr = getLeafAtCurrentCaretPosition(e);
		int pos = getCurrentCaretPosition(e);

		if (getActiveLeaf(e) != curr) {
			XsltDocument.XsltLeafElement next = getNextLeaf(e, pos);
			if (next != null) {
				setActiveLeaf(e, next);
				setCaretPosition(e, next.getStartOffset());
			}
		} else {
			if (getActiveLeaf(e) != getLeafAtCaretPosition(e, pos + 1)) {
				super.actionPerformed(e);
				setActiveLeaf(e, getLeafAtCaretPosition(e, pos));
			} else {

				getLogger(e).debug("Call super...");
				super.actionPerformed(e);
			}
		}
		getXsltTextPane(e).updateSelections(true);
		if (getLogger(e).isDebugEnabled())
			getLogger(e).debug(getName().toUpperCase() + " END");
	}
}
