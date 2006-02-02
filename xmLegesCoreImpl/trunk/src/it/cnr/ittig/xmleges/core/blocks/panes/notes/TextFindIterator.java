package it.cnr.ittig.xmleges.core.blocks.panes.notes;

import it.cnr.ittig.xmleges.core.services.frame.FindIterator;

import javax.swing.text.JTextComponent;

/**
 * FindIterator per il testo piatto.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class TextFindIterator implements FindIterator {

	JTextComponent textComponent;

	String text;

	String textLower;

	String toFind;

	String toFindLower;

	int toFindLen;

	int toFindPos;

	int lastFind;

	boolean caseSensitive = false;

	/**
	 * Costruttore di <code>TextFindIterator</code>.
	 */
	public TextFindIterator() {
		super();
	}

	public void set(JTextComponent textComponent) {
		this.textComponent = textComponent;

	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void initFind(String text) {
		this.text = this.textComponent.getText();
		this.textLower = this.textComponent.getText().toLowerCase();
		this.toFind = text;
		this.toFindLower = text.toLowerCase();
		this.toFindPos = 0;
		this.toFindLen = text.length();
		this.lastFind = -1;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean next() {
		if (caseSensitive)
			lastFind = text.indexOf(toFind, toFindPos);
		else
			lastFind = textLower.indexOf(toFindLower, toFindPos);
		if (lastFind != -1) {
			toFindPos = lastFind + toFindLen;
			textComponent.setCaretPosition(lastFind);
			textComponent.setSelectionStart(lastFind);
			textComponent.setSelectionEnd(lastFind + toFind.length());
		} else {
			textComponent.setCaretPosition(0);
			textComponent.setSelectionStart(0);
			textComponent.setSelectionEnd(0);
		}
		return lastFind != -1;
	}

	public boolean canReplace(String text) {
		// TODO canReplace
		return false;
	}

	public void replace(String text) {
		// TODO replace
	}

}
