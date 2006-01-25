package it.cnr.ittig.xmleges.core.services.exec;

/**
 * Eccezione emessa se &egrave; scaduto il timeout.
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
 * @see it.cnr.ittig.xmleges.core.services.exec.Exec
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ExecTimeoutException extends ExecException {

	/** Timeout impostato per l'esecuzione del comando. */
	int timeout;

	/**
	 * Crea una nuova istanza di <code>ExecTimeoutException</code> con il
	 * messaggio <code>msg</code>.
	 * 
	 * @param msg messaggio
	 * @param command comando che &egrave; fallito
	 * @param timeout timeout impostato
	 */
	public ExecTimeoutException(String msg, String command, int timeout) {
		super(msg, command);
		this.timeout = timeout;
	}

	/**
	 * Restituisce il timeout impostato per eseguire il comando.
	 * 
	 * @return timeout
	 */
	public int getTimeout() {
		return this.timeout;
	}

	public String toString() {
		return super.toString() + ",timeout=" + getTimeout();
	}

}
