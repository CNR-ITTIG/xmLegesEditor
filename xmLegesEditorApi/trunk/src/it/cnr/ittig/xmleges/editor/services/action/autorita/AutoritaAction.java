package it.cnr.ittig.xmleges.editor.services.action.autorita;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la sincronizzazione del registro delle autorit?
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public interface AutoritaAction extends Service {

	/** Indica che il registro e' gia' aggiornato */
	public final static int ALREADY_UPDATED = 0;

	/** Indica che il registro e' stato aggiornato con successo */
	public final static int SUCCESFULY_UPDATED = 1;

	/** Indica che non e' possibile connettersi col server */
	public final static int FAILED_CONNECTION = 2;

	/** Indica altri errori nell'aggiornamento */
	public final static int ERROR = 3;

	/**
	 * Sincronizza il registro delle autorita col registro remoto
	 * 
	 * @return true se l'operazione e' andata a buon fine
	 */
	public int sincronizzaAutorita();

}
