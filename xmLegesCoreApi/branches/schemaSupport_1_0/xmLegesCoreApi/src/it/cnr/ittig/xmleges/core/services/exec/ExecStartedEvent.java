package it.cnr.ittig.xmleges.core.services.exec;

import java.util.EventObject;

/**
 * Classe evento per notificare l'inizio dell'esecuzione dell'applicazione.
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
public class ExecStartedEvent extends EventObject {

	/**
	 * Costruisce l'evento di inizio esecuzione.
	 * 
	 * @param source sorgente che emette il messaggio
	 */
	public ExecStartedEvent(Object source) {
		super(source);
	}

}
