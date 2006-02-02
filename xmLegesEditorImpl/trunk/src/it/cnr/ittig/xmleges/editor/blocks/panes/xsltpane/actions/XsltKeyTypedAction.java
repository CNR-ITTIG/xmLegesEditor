package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.actions;

import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.XsltDocument;
import it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane.XsltTextPane;

import java.awt.event.ActionEvent;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;

/**
 * Azione pressione tasto.
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
 * @author <a href="mailto:mollicone@ittig.cnr.it">Maurizio Mollicone </a>
 */
public class XsltKeyTypedAction extends XsltAction {

	public XsltKeyTypedAction() {
		super(DefaultEditorKit.defaultKeyTypedAction);
	}

	public void actionPerformed(ActionEvent e) {
		if (getLogger(e).isDebugEnabled())
			getLogger(e).debug(getName().toUpperCase() + " BEGIN");

		XsltDocument.XsltLeafElement leaf = getActiveLeaf(e);
		if (leaf.getId() == null)
			return;

		XsltTextPane pane = getXsltTextPane(e);
		int caret = getCurrentCaretPosition(e);
		int start = leaf.getStartOffset();
		int end = leaf.getEndOffset();
		if (getLogger(e).isDebugEnabled()) {
			getLogger(e).debug("leaf=" + leaf.toString());
			getLogger(e).debug("caret=" + caret);
		}

		String content = e.getActionCommand();
		int mod = e.getModifiers();
		getLogger(e).info(getName().toUpperCase() + " content: " + content);
		if (leaf.isEmpty()) {
			if (checkKeys(content, mod)) {
				leaf.updateDom(content);
				setActiveLeaf(e, leaf);
				XsltBackwardAction back = new XsltBackwardAction();
				back.actionPerformed(e);
			}
			return;
		}

		if (checkKeys(content, mod)) {
			try {
				String text = pane.getText(start, end - start);
				getLogger(e).debug("text=" + text + "content=" + content);
				int selStart = pane.getSelectionStart();
				int selEnd = pane.getSelectionEnd();
				StringBuffer modText = new StringBuffer();
				int newCaretOffset;
				if (selStart != selEnd) {
					modText.append(text.substring(0, selStart - start));
					modText.append(content);
					newCaretOffset = modText.length();
					modText.append(text.substring(selEnd - start));
				} else {
					modText.append(text.substring(0, caret - start));
					modText.append(content);
					newCaretOffset = modText.length();
					modText.append(text.substring(caret - start));
				}

				getLogger(e).debug("modtext=" + modText.toString());
				leaf.updateDom(modText.toString());

				XsltBackwardAction back = new XsltBackwardAction();
				back.actionPerformed(e);
				pane.setCaretPosition(start + newCaretOffset);
			} catch (BadLocationException ex) {
				getLogger(e).error(ex.toString(), ex);
			}
		}

		if (getLogger(e).isDebugEnabled())
			getLogger(e).debug(getName().toUpperCase() + " END");
	}

	protected boolean checkKeys(String content, int mod) {
		if ((content != null) && (content.length() > 0) && ((mod & ActionEvent.ALT_MASK) == (mod & ActionEvent.CTRL_MASK))) {
			char c = content.charAt(0);
			if ((c >= 0x20) && (c != 0x7F))
				return true;
		}
		return false;
	}
}
