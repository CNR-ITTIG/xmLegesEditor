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
	 * Indica se &egrave; possibile estrarre il testo selezionato fra start ed end 
	 * dal nodo node ed appenderlo come fratello.
	 * 
	 * @param node node nel/dal quale creare il commento
	 * @param start posizione di inizio
	 * @param end posizione di fine
	 * @return true se pu&ograve; inserire un commento
	 */
	public boolean canExtractText(Node node, int start, int end);

	/**
	 * Estrae il testo selezionato fra start ed end dal nodo node e lo appende come fratello.
	 * Racchiuso in una transazione.
	 * 
	 * @param node node dal quale estrarre il testo
	 * @param start posizione di inizio
	 * @param end posizione di fine
	 * @return nodo estratto
	 */
	public Node extractText(Node node, int start, int end);
	
	/**
	 * Estrae il testo selezionato fra start ed end dal nodo node e lo appende come fratello.
	 * NON racchiuso in una transazione.
	 * 
	 * @param node node dal quale estrarre il testo
	 * @param start posizione di inizio
	 * @param end posizione di fine
	 * @return nodo estratto
	 */
	public Node extractTextDOM(Node node, int start, int end);

}
