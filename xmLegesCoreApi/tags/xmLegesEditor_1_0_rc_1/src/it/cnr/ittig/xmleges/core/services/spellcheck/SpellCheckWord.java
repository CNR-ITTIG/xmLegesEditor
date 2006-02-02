package it.cnr.ittig.xmleges.core.services.spellcheck;

/**
 * Interfaccia per indicare la parola errata all'interno di un nodo DOM.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public interface SpellCheckWord {

	/**
	 * Restituisce la parola errata.
	 * 
	 * @return parola errata
	 */
	public String getWord();

	/**
	 * Indice iniziale della parola errata.
	 * 
	 * @return indice iniziale
	 */
	public int getStartOffset();

	/**
	 * Indice finale della parola errata.
	 * 
	 * @return indice finale
	 */
	public int getEndOffset();

}
