package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions;

import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.XsltDocument;

import java.awt.event.ActionEvent;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;

/**
 * Azione inizio documento.
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
public class XsltBeginAction extends XsltAction {

	public XsltBeginAction() {
		super(DefaultEditorKit.beginAction);
	}

	public void actionPerformed(ActionEvent event) {
		if (getLogger(event).isDebugEnabled())
			getLogger(event).debug(getName().toUpperCase() + " BEGIN");
		Element[] elems = getXsltDocument(event).getRootElements();
		XsltDocument.XsltLeafElement leaf = null;
		for (int i = 0; i < elems.length; i++) {
			leaf = getFirstLeaf(elems[i]);
			if (leaf != null)
				break;
		}
		if (leaf != null) {
			setActiveLeaf(event, leaf);
			setCaretPosition(event, leaf.getStartOffset());
		} else {
			fireDefaultAction(event);
			setActiveLeaf(event);
		}
		if (getLogger(event).isDebugEnabled())
			getLogger(event).debug(getName().toUpperCase() + " END");
	}

	protected XsltDocument.XsltLeafElement getFirstLeaf(Element elem) {
		XsltDocument.XsltLeafElement leaf = null;
		if (elem instanceof XsltDocument.XsltLeafElement) {
			leaf = (XsltDocument.XsltLeafElement) elem;
			if (leaf.getId() == null)
				leaf = null;
		} else
			for (int i = 0; i < elem.getElementCount(); i++) {
				leaf = getFirstLeaf(elem.getElement(i));
				if (leaf != null)
					break;
			}
		return leaf;
	}
}
