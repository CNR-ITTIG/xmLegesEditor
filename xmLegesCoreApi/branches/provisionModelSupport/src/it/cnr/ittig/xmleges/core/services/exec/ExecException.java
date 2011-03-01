package it.cnr.ittig.xmleges.core.services.exec;

/**
 * Eccezione emessa durante l'uso di Exec.
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
 * @see it.cnr.ittig.xmleges.core.services.exec.Exec
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ExecException extends Exception {

	/** Comando che &egrave; fallito. */
	String command;

	/**
	 * Crea una nuova istanza di <code>ExecException</code> con il messaggio
	 * <code>msg</code>.
	 * 
	 * @param msg messaggio
	 * @param command comando che &egrave; fallito
	 */
	public ExecException(String msg, String command) {
		super(msg);
		this.command = command;
	}

	/**
	 * Restituisce il comando che &egrave; fallito.
	 * 
	 * @return comando fallito
	 */
	public String getCommand() {
		return this.command;
	}

	public String toString() {
		return "command='" + getCommand() + '\'';
	}

}
