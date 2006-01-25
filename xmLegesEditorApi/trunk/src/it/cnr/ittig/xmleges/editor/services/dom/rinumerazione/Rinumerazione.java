package it.cnr.ittig.xmleges.editor.services.dom.rinumerazione;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Document;

/**
 * Servizio per l'aggiornamento degli ID dei nodi ed la rinumerazione delle partizioni.
 * <br>
 * La rinumerazione (non l'aggiornamentodegli ID) deve essere attivata tramite il metodo
 * <code>setRinumerazione(true)</code> altrimenti il metodo <code>aggiorna</code>
 * effettua l'aggiornamento dei soli ID dei nodi.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface Rinumerazione extends Service {

	/**
	 * Aggiorna gli ID dei nodi del documento e i loro numeri se &egrave; stato impostato
	 * tramite il metodo <code>setRinumerazione(true)</code>.
	 * 
	 * @param document documento da aggiornare
	 */
	public void aggiorna(Document document);

	/**
	 * Imposta l'aggiornamento dei numeri oltre che degli ID dei nodi del documento.
	 * 
	 * @param renum <code>true</code> se il metodo <code>aggiorna</code> deve
	 *            aggiornare anche i numeri.
	 */
	public void setRinumerazione(boolean renum);

	/**
	 * Indica se la rinumerazione &egrave; attiva.
	 * 
	 * @return <code>true</code> se &egrave; attiva
	 */
	public boolean isRinumerazione();

	/**
	 * Restituisce il tipo di numerazione per gli ndr (cardinale, romano, letterale)
	 * 
	 * @return tipo numerazione
	 */
	public String getRinumerazioneNdr();

	/**
	 * Imposta il tipo di numerazione per gli ndr (cardinale, romano, letterale)
	 * 
	 * @param tipo
	 */
	public void setRinumerazioneNdr(String tipo);

}
