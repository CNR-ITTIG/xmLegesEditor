package it.cnr.ittig.xmleges.core.blocks.panes.source;

import it.cnr.ittig.xmleges.core.services.frame.FindIterator;

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
public class SourceFindIterator implements FindIterator {
	SourceTextPane textPane;

	String toFind;

	boolean caseSens;

	int lastPos;


	/**
	 * Costruttore di <code>XsltFindIterator</code>.
	 */
	public SourceFindIterator(SourceTextPane textPane) {
		super();
		this.textPane = textPane;
		
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
			
			String text = textPane.getText();
			
			do {
			
				
				if (caseSens)
                    pos = text.indexOf(toFind, lastPos);
				else
                    pos = text.toLowerCase().indexOf(toFind.toLowerCase(), lastPos );
				
				if (pos == -1){
					lastPos = -1;
					break;
				}
				
				else {
					
					// il selectText serve per garantire un pane.getSelectedText!=null e abilitare il replace
					textPane.setCaretPosition(pos);
					textPane.moveCaretPosition(pos + toFind.length());
					
										
					lastPos = pos + toFind.length();
				}
				
			} while (pos == -1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lastPos != -1;
	}

	public boolean canReplace(String text) {
		return textPane.getSelectedText() != null;
	}

	public void replace(String text) {
		textPane.replaceSelection(text);
		
	}

}
