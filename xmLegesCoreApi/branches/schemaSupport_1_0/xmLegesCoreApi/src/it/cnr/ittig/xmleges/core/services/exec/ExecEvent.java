package it.cnr.ittig.xmleges.core.services.exec;

import java.util.EventObject;

/**
 * Classe evento per notificare messaggi durante l'esecuzione dell'applicazione
 * che li emette tramite stderr. I messaggi possono essere di tipo:
 * <ul>
 * <li>debug: se ha prefisso <code>debug:</code>;</li>
 * <li>info: se ha prefisso <code>info:</code>;</li>
 * <li>warn: se ha prefisso <code>warn:</code>;</li>
 * <li>error: se ha prefisso <code>error:</code>;</li>
 * <li>perc: se ha prefisso <code>perc:</code>.</li>
 * </ul>
 * Se il messaggio non ha un prefisso allora &egrave; considerato generico ed
 * &egrave; recuperabile attraverso il metodo <code>getGenericMsg</code>.
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
public class ExecEvent extends EventObject {

	/** Testo del messaggio */
	String msg;

	/**
	 * Costruisce l'evento con il messaggio specificato.
	 * 
	 * @param source sorgente che emette il messaggio
	 * @param msg messaggio
	 */
	public ExecEvent(Object source, String msg) {
		super(source);
		this.msg = msg;
	}

	/**
	 * Indica se il messaggio &egrave; di tipo <code>debug</code>.
	 * 
	 * @return <code>true</code> se &egrave; di tipo <code>debug</code>
	 */
	public boolean isDebug() {
		return msg.startsWith("debug");
	}

	/**
	 * Restituisce il messaggio.
	 * 
	 * @return messaggio
	 */
	public String getDebugMsg() {
		return msg.substring(6).trim();
	}

	/**
	 * Indica se il messaggio &egrave; di tipo <code>info</code>.
	 * 
	 * @return <code>true</code> se &egrave; di tipo <code>info</code>
	 */
	public boolean isInfo() {
		return msg.startsWith("info");
	}

	/**
	 * Restituisce il messaggio.
	 * 
	 * @return messaggio
	 */
	public String getInfoMsg() {
		return msg.substring(5).trim();
	}

	/**
	 * Indica se il messaggio &egrave; di tipo <code>warning</code>.
	 * 
	 * @return <code>true</code> se &egrave; di tipo <code>warning</code>
	 */
	public boolean isWarn() {
		return msg.startsWith("warn");
	}

	/**
	 * Restituisce il messaggio.
	 * 
	 * @return messaggio
	 */
	public String getWarnMsg() {
		return msg.substring(5).trim();
	}

	/**
	 * Indica se il messaggio &egrave; di tipo <code>error</code>.
	 * 
	 * @return <code>true</code> se &egrave; di tipo <code>error</code>
	 */
	public boolean isError() {
		return msg.startsWith("error");
	}

	/**
	 * Restituisce il messaggio.
	 * 
	 * @return messaggio
	 */
	public String getErrorMsg() {
		return msg.substring(6).trim();
	}

	/**
	 * Indica se il messaggio contiene la percentuale di avanzamento
	 * dell'esecuzione.
	 * 
	 * @return <code>true</code> se contiene la percentuale
	 */

	public boolean isPerc() {
		return msg.startsWith("perc");
	}

	/**
	 * Restituisce la percentuale di avanzamento.
	 * 
	 * @return percentuale (intero compreso tra 0 e 100)
	 */
	public int getPerc() {
		try {
			return Integer.parseInt(msg.substring(26).trim());
		} catch (Exception ex) {
			return 0;
		}
	}

	/**
	 * Indica se il messaggio &egrave; generico, cio&egrave; non &egrave; di
	 * nessun tipo.
	 * 
	 * @return <code>true</code> se &egrave; generico
	 */
	public boolean isGeneric() {
		return !(isDebug() || isInfo() || isWarn() || isError() || isPerc());
	}

	/**
	 * Restituisce il messaggio.
	 * 
	 * @return messaggio
	 */
	public String getGenericMsg() {
		return msg.trim();
	}
}
