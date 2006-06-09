package it.cnr.ittig.xmleges.editor.services.form.ndr;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la visualizzazione di una form per recuperare i dati necessari
 * per una nota del redattore.
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
public interface NdrForm extends Service {
	/**
	 * Apre la form Ndr
	 * 
	 * @param textSelected testo selezionato sul pannello che diventa il testo
	 *        della nota
	 * @param ndrs lista dei nodi ndr presenti nel documento
	 * @return <code>true</code> se &egrave; stato premuto ok
	 */
	public boolean openForm(String textSelected, Object[] ndrs);

	/**
	 * Restituisce l'oggetto di ndrs selezionato (passato nel metodo
	 * <code>openForm</code>) o <code>null</code> se non selezionato.
	 * 
	 * @return oggetto selezionato
	 */
	public Object getNdrSelezionato();

	/**
	 * Restituisce il testo della nota.
	 * 
	 * @return testo della nota
	 */
	public String getTestoNota();

	/**
	 * Restituisce il tipo di numerazione per le note
	 * 
	 * @return </code>true</code> se &egrave selezionata la numerazione
	 *         cardinale
	 */
	public boolean isCardinale();

	/**
	 * Restituisce il tipo di numerazione per le note
	 * 
	 * @return </code>true</code> se &egrave selezionata la numerazione
	 *         letterale
	 */
	public boolean isLetterale();

	/**
	 * Restituisce il tipo di numerazione per le note
	 * 
	 * @return </code>true</code> se &egrave selezionata la numerazione romana
	 */
	public boolean isRomano();

}
