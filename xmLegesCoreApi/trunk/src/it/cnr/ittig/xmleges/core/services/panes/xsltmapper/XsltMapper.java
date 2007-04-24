package it.cnr.ittig.xmleges.core.services.panes.xsltmapper;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per effettuare la mappatura tra i nodi DOM e XSLT per il pannello di
 * modifica XsltPane.
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
public interface XsltMapper extends Service {

	/** Nome del nodo XSLT che incapsula il nodo testo. */
	public final static String TEXT_NODE_NAME = "XsltMapperTextNode";

	/** Nome dell'attributo dell'XSLT per l'id. */
	public final static String ID_NAME = "XsltMapperId";

	/** Nome del nodo XSLT che incapsula il nodo ProcessingInstruction. */
	public final static String PI_NODE_NAME = "XsltMapperPI";

	/** Nome dell'attributo per il target della ProgessingInstruction. */
	public final static String PI_TARGET = "XsltMapperPITarget";

	/** Nome del nodo XSLT che incapsula il nodo ProcessingInstruction. */
	public final static String C_NODE_NAME = "XsltMapperC";

	/**
	 * Restituisce il nodo DOM dato un id.
	 * 
	 * @param id id del nodo
	 * @return nodo XSLT
	 */
	public Node getDomById(String id);

	/**
	 * Restituisce i nodi DOM dato un array di id.
	 * 
	 * @param ids id dei nodi
	 * @return nodi XSLT
	 */
	public Node[] getDomById(String[] ids);

	/**
	 * Restituisce il nodo DOM dato un id, sostituendo eventualmente i nodi
	 * testo generati con il loro parent.
	 * 
	 * @param id id del nodo
	 * @param replaceGenNodes indica se sostituire i nodi generati con il loro
	 *        parent
	 * @return nodo XSLT
	 */
	public Node getDomById(String id, boolean replaceGenNodes);

	/**
	 * Restituisce i nodi DOM dato un array di id, sostituendo eventualmente i
	 * nodi testo generati con il loro parent.
	 * 
	 * @param ids id dei nodi
	 * @param replaceGenNodes indica se sostituire i nodi generati con il loro
	 *        parent
	 * @return nodi XSLT
	 */
	public Node[] getDomById(String[] ids, boolean replaceGenNodes);

	/**
	 * Restituisce l'id dato il nodo DOM.
	 * 
	 * @param node nodo XSLT
	 * @return id del nodo
	 */
	public String getIdByDom(Node node);

	/**
	 * Mappa una coppia di nodi DOM nella tabella dei nodi generati
	 * 
	 * @param newNode il nodo testo generato
	 * @param parentNode il nodo che dovr&agrave; diventare suo parent
	 */
	public void mapGen2Parent(Node newNode, Node parentNode);

	/**
	 * Restituisce il nodo DOM genitore di un nodo testo generato dalla
	 * trasformazione.
	 * 
	 * @param gen il nodo testo generato
	 * @return nodo DOM
	 */
	public Node getParentByGen(Node gen);

	/**
	 * Restituisce il nodo testo generato dalla trasformazione a partire dal suo
	 * 'futuro' padre.
	 * 
	 * @param gen il nodo padre
	 * @return nodo DOM generato
	 */
	public Node getGenByParent(Node gen);

	/**
	 * Rimuove dalle tabelle dei mapping un nodo testo generato o il futuro
	 * padre di esso.
	 * 
	 * @param node il nodo testo generato o un nodo futuro padre del nodo
	 *        generato
	 * @return id del nodo
	 */
	public void removeGen2Parent(Node node);

	/**
	 * Restituisce il testo internazionalizzato da visualizzare per i nodi
	 * vuoti.
	 * 
	 * @param node il nodo di cui ottenere il valore di default
	 * @return testo di default
	 */
	public String getI18nNodeText(Node node);
}
