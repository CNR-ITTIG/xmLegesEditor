package it.cnr.ittig.services.manager;

/**
 * Eccezione emessa da ServiceManager.
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
 * @see it.cnr.ittig.services.manager.ServiceManager
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ServiceException extends Exception {

	/**
	 * 
	 */
	public ServiceException() {
		/**
		 * @author mirco To change the template for this generated type comment
		 *         go to Window&gt;Preferences&gt;Java&gt;Code
		 *         Generation&gt;Code and Comments
		 */

		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ServiceException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ServiceException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
