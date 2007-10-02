package it.cnr.ittig.xmleges.core.services.form;

/**
 * Interfaccia necessaria per aggiungere a una form un oggetto che permette di
 * verificare la validit&agrave; dei dati della form e procedere con la sua
 * chiusura nel momento in cui viene premuto il tasto di conferma.
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
public interface FormVerifier {

	/**
	 * Verifica la validit&agrave; dei dati della form.
	 * 
	 * @return <code>true</code> sei dati sono corretti
	 */
	public boolean verifyForm();

	/**
	 * Restituisce il messaggio di errore da visualizzare se il controllo di
	 * validit&agrave; fallisce.
	 * 
	 * @return messaggio di errore
	 */
	public String getErrorMessage();
}
