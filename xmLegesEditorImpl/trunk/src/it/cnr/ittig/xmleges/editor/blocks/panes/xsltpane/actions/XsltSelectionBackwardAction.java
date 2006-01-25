package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions;

import java.awt.event.ActionEvent;

/**
 * Azione selezione del carattere precedente.
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
 * @author <a href="mailto:mollicone@ittig.cnr.it">Maurizio Mollicone</a>
 */
public class XsltSelectionBackwardAction extends XsltAction {

	public XsltSelectionBackwardAction() {
		super("selection-backward");
	}

	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		getXsltTextPane(e).updateSelections(true);
	}

}
