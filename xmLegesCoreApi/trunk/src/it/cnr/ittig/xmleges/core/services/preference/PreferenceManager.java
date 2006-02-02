package it.cnr.ittig.xmleges.core.services.preference;

import it.cnr.ittig.services.manager.Service;

import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import org.w3c.dom.Node;

/**
 * Servizio per la gestione la configurazione modificabile dall'utente.
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
 * @version 1.0
 */
public interface PreferenceManager extends Service {

	/**
	 * Indica se la sezione <i>section </i> &egrave; contenuta nel file di
	 * configurazione.
	 * 
	 * @param section nome della sezione
	 * @return <i>true </i> se esiste
	 */
	public boolean hasPreference(String section);

	/**
	 * Restituisce la configurazione della sezione <i>section </> sottoforma di
	 * <code>org.w3c.dom.Node</code>.
	 * 
	 * @param section nome della sezione
	 * @return configurazione
	 */
	public Node getPreferenceAsDOM(String section);

	/**
	 * Restituisce la configurazione della sezione <i>section </> sottoforma di
	 * <code>java.io.InputStream</code>.
	 * 
	 * @param section nome della sezione
	 * @return configurazione
	 */
	public InputStream getPreferenceAsInputStream(String section);

	/**
	 * Restituisce la configurazione della sezione <i>section </> sottoforma di
	 * <code>java.util.Properties</code>.
	 * 
	 * @param section nome della sezione
	 * @return configurazione
	 */
	public Properties getPreferenceAsProperties(String section);

	/**
	 * Restituisce la configurazione della sezione <i>section </> sottoforma di
	 * <code>java.lang.String</code>.
	 * 
	 * @param section nome della sezione
	 * @return configurazione
	 */
	public String getPreferenceAsString(String section);

	/**
	 * Restituisce la configurazione della sezione <i>section </> sottoforma di
	 * <code>java.util.Vector</code> di stringhe.
	 * 
	 * @param section nome della sezione
	 * @return configurazione
	 */
	public Vector getPreferenceAsVector(String section);

	/**
	 * Imposta la configurazione della sezione <i>section </>.
	 * 
	 * @param section nome della sezione
	 * @param pref configurazione
	 */
	public void setPreference(String section, Node pref);

	/**
	 * Imposta la configurazione della sezione <i>section </>.
	 * 
	 * @param section nome della sezione
	 * @param pref configurazione
	 */
	public void setPreference(String section, InputStream pref);

	/**
	 * Imposta la configurazione della sezione <i>section </>.
	 * 
	 * @param section nome della sezione
	 * @param pref configurazione
	 */
	public void setPreference(String section, Properties pref);

	/**
	 * Imposta la configurazione della sezione <i>section </>.
	 * 
	 * @param section nome della sezione
	 * @param pref configurazione
	 */
	public void setPreference(String section, String pref);

	/**
	 * Imposta la configurazione della sezione <i>section </> salvando ogni
	 * elemento del vettore con il corrispondente risultato del metodo
	 * <code>toString()</code>.
	 * 
	 * @param section nome della sezione
	 * @param pref configurazione
	 */
	public void setPreference(String section, Vector pref);
}
