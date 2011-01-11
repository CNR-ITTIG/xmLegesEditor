package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.w3c.dom.Node;

/**
 * Classe per cercare e sostituire il testo.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
 */
public class XsltFindIterator implements FindIterator {
	XsltPaneImpl xsltPaneImpl;

	Logger logger;

	String toFind;

	boolean caseSens;

	int lastPos;

	AntiAliasedTextPane pane;

	/**
	 * Costruttore di <code>XsltFindIterator</code>.
	 */
	public XsltFindIterator(XsltPaneImpl xsltPaneImpl) {
		super();
		this.xsltPaneImpl = xsltPaneImpl;
		this.logger = xsltPaneImpl.getLogger();
	}

	public void initFind(String text) {
		this.toFind = text;
		setCaseSensitive(false);
		lastPos = 0;
		pane = xsltPaneImpl.getTextPane();
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSens = caseSensitive;
	}

	public boolean next() {
		try {
			int pos;
			Element lastLeaf = null;
			do {
				Element leaf = pane.getHTMLDocument().getCharacterElement(lastPos);
				int s = leaf.getStartOffset();
				int e = leaf.getEndOffset();
				if (caseSens)
                    pos = pane.getText(s, e - s).indexOf(toFind, lastPos - s);
				else
                    pos = pane.getText(s, e - s).toLowerCase().indexOf(toFind.toLowerCase(), lastPos - s);
				String leafText = pane.getText(s, e - s);
				if (!caseSens) {
                    leafText = leafText.toLowerCase();
                    pos = leafText.indexOf(toFind.toLowerCase(), lastPos - s);
				}
				else
                    pos = leafText.indexOf(toFind, lastPos - s);
				
				if (pos == -1)
					lastPos = e;
				else {
					Element foundElem = pane.getHTMLDocument().getCharacterElement(s+pos);
					String id = pane.getElementId(foundElem);
					Node foundNode = pane.getXsltMapper().getDomById(id);
					
					// il selectText prima serve per mantenere l'allineamento fra testo selezionato e
					// nodo selezionato (altrimenti sfalsati)
					// quello sotto per garantire un pane.getSelectedText!=null e abilitare il replace
					
					pane.selectText(s + pos, s + pos + toFind.length());
					pane.selectNode(new Node[] {foundNode});
					pane.selectText(s + pos, s + pos + toFind.length());
					
					lastPos = s + pos + toFind.length();
				}
				if (lastLeaf == leaf) {
					lastPos = -1;
					break;
				} else
					lastLeaf = leaf;
			} while (pos == -1);
		} catch (BadLocationException ex) {
			xsltPaneImpl.getLogger().error(ex.toString(), ex);
		}
		return lastPos != -1;
	}

	public boolean canReplace(String text) {
		return pane.getSelectedText() != null;
	}

	public void replace(String text) {
		pane.replaceSelection(text);
	}

}
