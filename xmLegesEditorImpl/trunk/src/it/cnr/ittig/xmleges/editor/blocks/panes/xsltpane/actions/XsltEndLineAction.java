package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions;

import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.XsltDocument;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;

/**
 * Azione fine linea.
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
public class XsltEndLineAction extends XsltAction {

	public XsltEndLineAction() {
		super(DefaultEditorKit.endLineAction);
	}

	public void actionPerformed(ActionEvent event) {
		if (getLogger(event).isDebugEnabled())
			getLogger(event).debug(getName().toUpperCase() + " BEGIN");
		XsltDocument.XsltLeafElement leaf = getActiveLeaf(event);
		int e = leaf.getEndOffset();
		if (e != getCurrentCaretPosition(event))
			setCaretPosition(event, e);
		else {
			fireDefaultAction(event);
			goToPreviousValidLeaf(event);
		}
		if (getLogger(event).isDebugEnabled())
			getLogger(event).debug(getName().toUpperCase() + " END");
	}

}
