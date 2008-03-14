package it.cnr.ittig.xmleges.core.services.dom.extracttext;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per l'estrazione di un testo da un tag
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface ExtractText extends Service {

	/**
	 * Indica se &egrave; possibile inserire un commento come figlio di node o
	 * spezzando ooportunamente il nodo se di tipo testo.
	 * 
	 * @param node node nel/dal quale creare il commento
	 * @param start posizione di inizio
	 * @param end posizione di fine
	 * @return true se pu&ograve; inserire un commento
	 */
	public boolean canExtractText(Node node, int start, int end);

	/**
	 * Inserisci un commento come figlio di node o spezzando ooportunamente il
	 * nodo se di tipo testo.
	 * 
	 * @param node node nel/dal quale creare il commento
	 * @param start posizione di inizio
	 * @param end posizione di fine
	 * @return nodo creato
	 */
	public Node extractText(Node node, int start, int end);

}
