package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions;

import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.XsltDocument;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;

/**
 * Azione freccia sinistra.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class XsltBackwardAction extends XsltAction {

	public XsltBackwardAction() {
		super(DefaultEditorKit.backwardAction);
	}

	public void actionPerformed(ActionEvent e) {
		if (getLogger(e).isDebugEnabled())
			getLogger(e).debug(getName().toUpperCase() + " BEGIN");

		int pos = getCurrentCaretPosition(e);
		XsltDocument.XsltLeafElement prev = getPrevLeaf(e, pos - 1);
		if (prev != null) {
			if (getActiveLeaf(e) != prev) {
				setActiveLeaf(e, prev);
				setCaretPosition(e, prev.getEndOffset());
			} else
				super.actionPerformed(e);
		}
		getXsltTextPane(e).updateSelections(true);
		if (getLogger(e).isDebugEnabled())
			getLogger(e).debug(getName().toUpperCase() + " END");
	}

}
