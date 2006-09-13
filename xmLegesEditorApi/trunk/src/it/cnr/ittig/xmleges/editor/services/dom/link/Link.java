package it.cnr.ittig.xmleges.editor.services.dom.link;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per la correzione o l'annullamento di un link ad un ipertesto
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
public interface Link extends Service {

	/**
	 * Setta il link all'ipertesto
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @param url la stringa contenente la url del link
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node setUrl(Node node, String url);
	
	/**
	 * Setta la parte testuale del link all'ipertesto
	 * 
	 * @param node nodo di riferimento
	 * @param text testo del link
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node setText(Node node, String text);

	/**
	 * Setta il tipo del link all'ipertesto
	 * 
	 * @param node nodo di riferimento
	 * @param type la stringa indicante il tipo del link
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node setType(Node node, String type);
	
	/**
	 * Restituisce la sottostringa contenente la Url dal nodo
	 * @param node
	 * @return
	 */
	public String getUrl(Node node);
	
	/**
	 * Restituisce la sottostringa contenente la parte testuale del link all'ipertesto
	 * @param node
	 * @return
	 */
	public String getText(Node node);
	
	/**
	 * Restituisce il tipo del link
	 * @param node
	 * @return
	 */
	public String getType(Node node);
		
	/**
	 * Sostituisce al nodo <code>&lt;rif&gt;</code> il suo contenuto testuale.
	 * 
	 * @param node nodo di riferimento
	 * @param plainText testo piatto del rif Incompleto
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node setPlainText(Node node, String plainText);
	
	/**
	 * Indica se è possibile inserire un nodo <code>&lt;rif&gt;</code>
	 * all'interno del nodo <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile inserire un nodo
	 *         <code>&lt;rif&gt;</code>
	 */
	public boolean canInsert(Node node);

	/**
	 * Inserisce un nodo <code>&lt;rif&gt;</code>all'interno del nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @param testo testo del link
	 * @param url la stringa contenente la url del link
	 * @param type la stringa indicante il tipo del link
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node insert(Node nodo, int start, int end, String testo, String url, String type);
}
