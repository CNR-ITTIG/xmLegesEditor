package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;

/**
 * Azione fine paragrafo.
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
public class XsltEndParagraphAction extends XsltAction {

	public XsltEndParagraphAction() {
		super(DefaultEditorKit.endParagraphAction);
	}

	public void actionPerformed(ActionEvent event) {
		super.actionPerformed(event);
		goToPreviousValidLeaf(event);
	}
}
