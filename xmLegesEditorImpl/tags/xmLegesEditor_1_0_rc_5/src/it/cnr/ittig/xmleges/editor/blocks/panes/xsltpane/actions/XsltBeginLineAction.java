package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions;

import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.XsltDocument;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;

/**
 * Azione inizio linea.
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
public class XsltBeginLineAction extends XsltAction {

	public XsltBeginLineAction() {
		super(DefaultEditorKit.beginLineAction);
	}

	public void actionPerformed(ActionEvent event) {
		if (getLogger(event).isDebugEnabled())
			getLogger(event).debug(getName().toUpperCase() + " BEGIN");
		XsltDocument.XsltLeafElement leaf = getActiveLeaf(event);
		int s = leaf.getStartOffset();
		if (s != getCurrentCaretPosition(event))
			setCaretPosition(event, s);
		else {
			fireDefaultAction(event);
			goToNextValidLeaf(event);
		}
		if (getLogger(event).isDebugEnabled())
			getLogger(event).debug(getName().toUpperCase() + " END");
	}
}
