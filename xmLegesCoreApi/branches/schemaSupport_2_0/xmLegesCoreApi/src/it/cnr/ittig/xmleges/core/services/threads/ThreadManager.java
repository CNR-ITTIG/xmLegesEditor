package it.cnr.ittig.xmleges.core.services.threads;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per il mantenimento di un pool di thread per effettuare l'esecuzione
 * di oggetti che implementano l'interfaccia Runnable.
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
public interface ThreadManager extends Service {

	/**
	 * Esegue l'oggetto in ingresso all'interno di un thread del pool.
	 * 
	 * @param runnable oggetto da eseguire
	 */
	public void execute(Runnable runnable);

}