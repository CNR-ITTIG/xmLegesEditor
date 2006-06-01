package it.cnr.ittig.xmleges.core.services.exec;

import java.util.EventObject;

/**
 * Classe evento per notificare la fine dell'esecuzione dell'applicazione.
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
 */
public class ExecFinishedEvent extends EventObject {

	/** Valore di uscita dell'applicazione. */
	int exitValue;

	/** Flag per uscita con fine timeout. */
	boolean timeout;

	/** Flag per uscita forzata. */
	boolean terminated;

	/**
	 * Costruisce l'evento di fine esecuzione.
	 * 
	 * @param source sorgente che emette il messaggio
	 * @param msg messaggio
	 */
	public ExecFinishedEvent(Object source, int exitValue, boolean timeout, boolean terminated) {
		super(source);
		this.exitValue = exitValue;
		this.timeout = timeout;
		this.terminated = terminated;
	}

	/**
	 * Restituisce il valore di uscita dell'applicazione.
	 * 
	 * @return valore di uscita dell'applicazione
	 */
	public int getExitValue() {
		return this.exitValue;
	}

	/**
	 * Indica se l'applicazione &egrave; stata interrotta per timeout.
	 * 
	 * @return <code>true</code> se &egrave; stata interrotta per timeout
	 */
	public boolean isExitOnTimeout() {
		return this.timeout;
	}

	/**
	 * Indica se l'applicazione &egrave; stata interrotta.
	 * 
	 * @return <code>true</code> se &egrave; stata interrotta
	 */
	public boolean isTerminated() {
		return this.terminated;
	}

}
