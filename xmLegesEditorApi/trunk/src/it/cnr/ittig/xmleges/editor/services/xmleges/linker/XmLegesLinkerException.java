package it.cnr.ittig.xmleges.editor.services.xmleges.linker;

/**
 * Eccezione per il Parser dei Riferimenti.
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
public class XmLegesLinkerException extends Exception {

	/**
	 * Costruttore di RiferimentiException.
	 * 
	 * @param message messaggio
	 */
	public XmLegesLinkerException(String message) {
		super(message);
	}

	/**
	 * Costruttore di RiferimentiException.
	 * 
	 * @param message messaggio
	 * @param cause causa
	 */
	public XmLegesLinkerException(String message, Throwable cause) {
		super(message, cause);
	}

}
