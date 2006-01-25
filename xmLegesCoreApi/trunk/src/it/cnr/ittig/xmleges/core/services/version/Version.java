package it.cnr.ittig.xmleges.core.services.version;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per ottenere la versione corrente dell'applicazione o recuperare e
 * installare nuove versioni.
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
public interface Version extends Service {

	/**
	 * Restituisce la versione corrente.
	 * 
	 * @return la stringa contenente la versione corrente
	 */
	public String getVersion();

	/**
	 * Controlla la disponibilit&agrave; di nuove versioni e aggiorna il file di
	 * partenza dell'applicazione.
	 */
	public void update();

}
