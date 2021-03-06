package it.cnr.ittig.xmleges.editor.services.xmleges.marker;

/**
 * Eccezione per il Parser di Struttura.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class XmLegesMarkerException extends Exception {

	/**
	 * Costruttore di StrutturaException.
	 * 
	 * @param message messaggio
	 */
	public XmLegesMarkerException(String message) {
		super(message);
	}

	/**
	 * Costruttore di StrutturaException.
	 * 
	 * @param message messaggio
	 * @param cause causa
	 */
	public XmLegesMarkerException(String message, Throwable cause) {
		super(message, cause);
	}

}
