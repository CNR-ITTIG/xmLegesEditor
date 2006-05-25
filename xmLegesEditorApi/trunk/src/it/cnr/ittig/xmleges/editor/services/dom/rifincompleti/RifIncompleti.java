package it.cnr.ittig.xmleges.editor.services.dom.rifincompleti;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import org.w3c.dom.Node;

/**
 * Servizio per la correzione o l'annullamento di un riferimento incompleto
 * di un
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface RifIncompleti extends Service {

	/**
	 * Indica se &egrave; possibile sostituire un nodo <code>&lt;?rif&gt;</code> incompleto con un nodo <code>&lt;rif&gt;</code>
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile inserire un nodo
	 *         <code>&lt;rif&gt;</code>
	 */
	public boolean canFix(Node node);

	
	/**
	 * Inserisce un nodo <code>&lt;rif&gt; all'interno del nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @param start inizio della selezione
	 * @param end fine della selezione
	 * @param Urn del riferimenti
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node setRif(Node node, int start, int end, Urn urn);
	
	
	/**
	 * Sostituisce al nodo <code>&lt;?rif&gt;</code> incompleto il suo contenuto testuale.
	 * 
	 * @param node nodo di riferimento
	 * @param start inizio della selezione
	 * @param end fine della selezione
	 * @param plainText testo piatto del rif Incompleto
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node setPlainText(Node node, int start, int end, String plainText);
	
	/**
	 * Restituisce la sottostringa contenente la Urn dal nodo <code>&lt;?rif&gt;</code> incompleto
	 * @param node
	 * @return
	 */
	public String getUrn(Node node);
	
	/**
	 * Restituisce la sottostringa contenente la form testuale dal nodo <code>&lt;?rif&gt;</code> incompleto
	 * @param node
	 * @return
	 */
	public String getText(Node node);
	
	
	/**
	 * Restituisce la lista di tutti i riferimenti incompleti presenti nel documento a partire dalla radice
	 * @return Array dei nodi <code>&lt;?rif&gt;</code>
	 */
	public Node[] getList();

	
}
