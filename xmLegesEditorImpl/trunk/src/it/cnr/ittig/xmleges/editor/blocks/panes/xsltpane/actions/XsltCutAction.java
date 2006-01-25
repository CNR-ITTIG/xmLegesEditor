package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;

/**
 * Taglia il testo e lo copia nella clipboard.
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
public class XsltCutAction extends XsltAction {

	public XsltCutAction() {
		super(DefaultEditorKit.cutAction);
	}

	public void actionPerformed(ActionEvent e) {
		if (getXsltTextPane(e).canCut())
			super.actionPerformed(e);
	}
}
