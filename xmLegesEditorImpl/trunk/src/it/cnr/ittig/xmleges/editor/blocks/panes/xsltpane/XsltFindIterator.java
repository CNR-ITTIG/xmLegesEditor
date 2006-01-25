package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;

import javax.swing.text.BadLocationException;

/**
 * Classe per cercare e sostituire il testo.
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
public class XsltFindIterator implements FindIterator {
	Logger logger;

	XsltTextPane pane;

	String toFind;

	boolean caseSens;

	int lastPos;

	/**
	 * Costruttore di <code>XsltFindIterator</code>.
	 */
	public XsltFindIterator(XsltPaneImpl xsltPaneImpl, XsltTextPane pane) {
		super();
		this.pane = pane;
		this.logger = xsltPaneImpl.getLogger();
	}

	public void initFind(String text) {
		this.toFind = text;
		setCaseSensitive(false);
		lastPos = 0;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSens = caseSensitive;
	}

	public boolean next() {
		try {
			int pos;
			XsltDocument.XsltLeafElement lastLeaf = null;
			do {
				XsltDocument.XsltLeafElement leaf = pane.getXsltDocument().getLeafByPos(lastPos);
				int s = leaf.getStartOffset();
				int e = leaf.getEndOffset();
				if (caseSens)
					pos = pane.getText(s, e - s).indexOf(toFind, lastPos - s);
				else
					pos = pane.getText(s, e - s).toLowerCase().indexOf(toFind.toLowerCase(), lastPos - s);
				String leafText = pane.getText(s, e - s);
				if (!caseSens)
					leafText = leafText.toLowerCase();
				pos = leafText.indexOf(toFind, lastPos - s);
				if (pos == -1)
					lastPos = e;
				else {
					pane.setSelectionStart(s + pos);
					pane.setSelectionEnd(s + pos + toFind.length());
					lastPos = s + pos + toFind.length();
				}
				if (lastLeaf == leaf) {
					lastPos = -1;
					break;
				} else
					lastLeaf = leaf;
			} while (pos == -1);
		} catch (BadLocationException ex) {
			logger.error(ex.toString(), ex);
		}
		return lastPos != -1;
	}

	public boolean canReplace(String text) {
		return false;
	}

	public void replace(String text) {
		// TODO Auto-generated method stub
		logger.info("REPLACE TODO");
	}

}
