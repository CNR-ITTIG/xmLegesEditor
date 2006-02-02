package it.cnr.ittig.xmleges.core.services.spellcheck.dom;

import java.util.EventObject;

/**
 * Classe evento per notificare gli errori di controllo ortografico.
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
 * @see it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheck
 * @see it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheck
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class DomSpellCheckEvent extends EventObject {

	DomSpellCheckWord[] words;

	/**
	 * Crea una nuova istanza di <code>DomSpellCheckEvent</code> per l'evento
	 * di controllo ortagrafico.
	 * 
	 * @param source sorgente dell'evento
	 * @param words elenco parole con errore
	 */
	public DomSpellCheckEvent(Object source, DomSpellCheckWord[] words) {
		super(source);
		this.words = words;
	}

	/**
	 * Indica se ci sono parole con errore.
	 * 
	 * @return <code>true</code> se ci sono parole con errore
	 */
	public boolean hasWords() {
		return this.words != null && this.words.length > 0;
	}

	/**
	 * Restituisce l'elenco delle parole con errore
	 * 
	 * @return elenco delle parole con errore
	 */
	public DomSpellCheckWord[] getWords() {
		return this.words;
	}

}
