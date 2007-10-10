package it.cnr.ittig.xmleges.editor.services.dalos.dom;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

import org.w3c.dom.Node;

/**
 * Servizio per l'assegnazione di 
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface SynsetMarkup extends Service {
	
	

	/**
	 * Funzione per l'abilitazione dell'azione di assegnazione della vigenza
	 * 
	 * @param node nodo sul cui testo si vuole applicare la vigenza
	 * @return </code>true</code> se l'azione puo' essere abilitata
	 */
	public boolean canSetSynset(Node node);
	
	
	/**
	 * Funzione Dom per l'assegnazione di un intervallo di vigenza ad una
	 * porzione di testo
	 * 
	 * @param node nodo sul cui testo si applica la vigenza
	 * @param selectedText testo selezionato
	 * @param start inizio selezione del testo
	 * @param end fine selezione del testo
	 * @param vigenza oggetto contenente la vigenza
	 * @return </code>true</code> se la vigenza e' stata inserita
	 *         correttamente
	 */
	public Node setSynset(Node node, int start, int end, Synset synset);
	
	
	
	

}
