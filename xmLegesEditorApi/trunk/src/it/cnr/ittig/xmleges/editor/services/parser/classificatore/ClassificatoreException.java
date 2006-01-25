package it.cnr.ittig.xmleges.editor.services.parser.classificatore;

/**
 * Eccezione per il Classificatore delle disposizioni.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ClassificatoreException extends Exception {

	/**
	 * Costruttore di ClassificatoreException.
	 * 
	 * @param message messaggio
	 */
	public ClassificatoreException(String message) {
		super(message);
	}

	/**
	 * Costruttore di ClassificatoreException.
	 * 
	 * @param message messaggio
	 * @param cause causa
	 */
	public ClassificatoreException(String message, Throwable cause) {
		super(message, cause);
	}

}
