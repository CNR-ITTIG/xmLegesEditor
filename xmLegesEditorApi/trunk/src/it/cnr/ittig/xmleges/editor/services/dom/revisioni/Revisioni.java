package it.cnr.ittig.xmleges.editor.services.dom.revisioni;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Servizio per la gestione delle revisioni di un disegno di legge
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface Revisioni extends Service {

	public final static String INSERITO = "inserito";

	public final static String SOPPRESSO = "soppresso";

	public final static String IDENTICO = "identico";

	/**
	 * Funzione per l'abilitazione delle azioni di inserimento o soppressione di
	 * una partizione o di un testo
	 * 
	 * @param node nodo su cui applicare la modifica dello status
	 * @param status (INSERITO|SOPPRESSO) che si vuole applicare
	 * @param start inizio selezione se &egrave; un nodo testo
	 * @param end fine selezione se &egrave; un nodo testo
	 * @return null se non si puo' settare la modifica; altrimenti restituisce
	 *         il nuovo status (INSERITO|IDENTICO|SOPPRESSO) dipendentemente
	 *         dallo stato attuale del nodo
	 */
	public String canSetModifica(Node node, String status, int start, int end);

	/**
	 * Funzione per settare a inserito,identico o soppresso lo status di una
	 * partizione o di un testo selezionato
	 * 
	 * @param node
	 * @param start
	 * @param end
	 * @param status stato (INSERITO|IDENTICO|SOPPRESSO) che viene applicato
	 *        all'attributo status
	 * @return nodo modificato; null se l'operazione non &egrave; andata a buon
	 *         fine.
	 */
	public Node setModifica(Node node, int start, int end, String status);

	/**
	 * Funzione per l'abilitazione dell'azione per la visualizzazione del testo
	 * a fronte sul browser
	 * 
	 * @return <code>true</code> se l'operazione puo' essere abilitata
	 */
	public boolean canTestoaFronte();

	/**
	 * Funzione per l'abilitazione della funzione per il passaggio del testo
	 * approvato a un nuovo documento nell'iter
	 * 
	 * @return <code>true</code> se l'operazione puo' essere abilitata
	 */
	public boolean canPassaggio();

	/**
	 * Funzione per l'abilitazione dell'azione per la generazione degli
	 * emendamenti
	 * 
	 * @return <code>true</code> se l'operazione puo' essere abilitata
	 */
	public boolean canEmendamenti();

	/**
	 * Funzione per l'estrazione del testo approvato (partizioni con status
	 * inserito o identico)
	 * 
	 * @param node
	 * @return
	 */
	public Node getFinalVersion(Node node);

	/**
	 * Funzione per l'estrazione da un documento di tipo dl della forma testuale
	 * degli emendamenti
	 * 
	 * @param doc
	 * @return Vettore di stringhe con la lista degli emendamenti; null se vuoto
	 */
	public Node[] getEmendamenti(Document oldDoc, Document newDoc);

}
