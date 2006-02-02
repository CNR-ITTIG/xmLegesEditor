package it.cnr.ittig.xmleges.core.services.document;

/**
 * Eccezione emessa da <code>DocumentManager</code>.
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
 * @see it.cnr.ittig.xmleges.core.services.document.DocumentManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class DocumentManagerException extends Exception {

	/**
	 * Costruisce l'eccezione con il messaggio <code>message</code>.
	 * 
	 * @param message messaggio dell'eccezione
	 */
	public DocumentManagerException(String message) {
		super(message);
	}
}
