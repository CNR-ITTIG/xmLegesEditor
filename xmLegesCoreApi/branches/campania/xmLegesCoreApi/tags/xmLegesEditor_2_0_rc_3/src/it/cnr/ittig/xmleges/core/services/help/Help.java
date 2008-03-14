package it.cnr.ittig.xmleges.core.services.help;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;

import java.awt.Component;

/**
 * Servizio per la gestione dell'help.
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
public interface Help extends Service {

	/**
	 * Invoca l'help sulla chiave <code>key</code>.
	 * 
	 * @param key chiave per l'help
	 */
	public void helpOn(String key);
	
	/**
	 * Invoca l'help di una form sulla chiave <code>key</code>.
	 * @param key  chiave per l'help
	 * @param listener listener sulla form che chiede l'help
     * @param owner form che chiede l'help
	 */
	public void helpOnForm(String key, FormClosedListener listener, Component owner);

	/**
	 * Verifica se <code>key</code> &egrave; utilizzabile come chiave per
	 * l'help.
	 * 
	 * @param key chiave da verificare
	 * @return <code>true</code> se &egrave; utilizzabile
	 */
	public boolean hasKey(String key);

	/**
	 * Visualizza la finestra di dialogo delle informazioni sull'applicazione.
	 */
	public void about();
}
