package it.cnr.ittig.xmleges.core.services.dom.comment;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per l'inserimento nel dom di un commento
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
public interface Comment extends Service {

	/**
	 * Indica se &egrave; possibile inserire un commento come figlio di node o
	 * spezzando ooportunamente il nodo se di tipo testo.
	 * 
	 * @param node node nel/dal quale creare il commento
	 * @param start posizione di inizio
	 * @param end posizione di fine
	 * @return true se pu&ograve; inserire un commento
	 */
	public boolean canInsertComment(Node node, int start, int end);

	/**
	 * Inserisci un commento come figlio di node o spezzando ooportunamente il
	 * nodo se di tipo testo.
	 * 
	 * @param node node nel/dal quale creare il commento
	 * @param start posizione di inizio
	 * @param end posizione di fine
	 * @return nodo creato
	 */
	public Node insertComment(Node node, int start, int end);

	/**
	 * Indica se &egrave; possibile inserire una
	 * <code>processing-instruction</code> come figlio di node o spezzando
	 * ooportunamente il nodo se di tipo testo.
	 * 
	 * @param node node nel/dal quale creare la
	 *        <code>processing-instruction</code>*
	 * @param start posizione di inizio
	 * @param end posizione di fine
	 * @return true se pu&ograve; inserire un commento
	 */
	public boolean canInsertPI(Node node, int start, int end);

	/**
	 * Inserisce una <code>processing-instruction</code> come figlio di node o
	 * spezzando ooportunamente il nodo se di tipo testo.
	 * 
	 * @param node node nel/dal quale creare la
	 *        <code>processing-instruction</code>
	 * @param name nome della <code>processing-instruction</code>
	 * @param start posizione di inizio
	 * @param end posizione di fine
	 * @return nodo creato
	 */
	public Node insertPI(Node node, String name, int start, int end);
}
