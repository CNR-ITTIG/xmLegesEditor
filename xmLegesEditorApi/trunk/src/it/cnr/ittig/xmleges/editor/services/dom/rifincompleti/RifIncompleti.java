package it.cnr.ittig.xmleges.editor.services.dom.rifincompleti;
import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import org.w3c.dom.Node;

/**
 * Servizio per la correzione o l'annullamento di un riferimento incompleto.
 * 
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
	 * Indica se &egrave; possibile sostituire un nodo <code>&lt;&#63;rif&gt;</code> incompleto con un nodo <code>&lt;rif&gt;</code>
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile trasformare il nodo
	 *         <code>&lt;rif&gt;</code>
	 */
	public boolean canFix(Node node);

	
	/**
	 * trasforma il riferimento incompleto in un riferimento
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @param text parte testuale della urn
	 * @param urn del riferimento
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node setRif(Node node, String text, Urn urn);

	/**
	 * trasforma il riferimento incompleto in un riferimento INTERNO
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @param text parte testuale del riferimento
	 * @param rif riferimento interno
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node setRif(Node node, String text, String rif);

	/**
	 * Sostituisce al nodo <code>&lt;&#63;rif&gt;</code> incompleto il suo contenuto testuale
	 * 
	 * @param node nodo di riferimento
	 * @param plainText testo piatto del rif Incompleto
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node setPlainText(Node node, String plainText);
	
	/**
	 * Restituisce la sottostringa contenente la Urn dal nodo <code>&lt;&#63;rif&gt;</code> incompleto
	 * @param node
	 * @return
	 */
	public String getUrn(Node node);
	
	/**
	 * Restituisce la sottostringa contenente la form testuale dal nodo <code>&lt;&#63;rif&gt;</code> incompleto
	 * @param node
	 * @return
	 */
	public String getText(Node node);
	
	
	/**
	 * Restituisce la lista di tutti i riferimenti incompleti presenti nel documento a partire dalla radice
	 * @return Array dei nodi <code>&lt;&#63;rif&gt;</code>
	 */
	public Node[] getList();

	
}
