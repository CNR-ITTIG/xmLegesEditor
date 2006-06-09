package it.cnr.ittig.xmleges.editor.services.dom.rinvii;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.util.Vector;

import org.w3c.dom.Node;

/**
 * Servizio per l'inserimento o la modifica di un riferimento interno o esterno
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface Rinvii extends Service {

	/**
	 * Indica se ??? possibile inserire un nodo <code>&lt;rif&gt;</code>
	 * all'interno del nodo <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile inserire un nodo
	 *         <code>&lt;rif&gt;</code>
	 */
	public boolean canInsert(Node node);

	/**
	 * @param node
	 * @param start
	 * @param end
	 * @param urn
	 * @param descrizioneMRif
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node insert(Node node, int start, int end, Vector urn, Vector descrizioneMRif);

	/**
	 * Inserisce un nodo <code>&lt;rif&gt;</code>o mrif all'interno del nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @param start inizio della selezione
	 * @param end fine della selezione
	 * @param Vector vettore delle urn dei riferimenti
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node insert(Node node, int start, int end, Urn urn);

	/**
	 * Inserisce un nodo <code>&lt;rif&gt;</code> all'interno del nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @param start inizio della selezione
	 * @param end fine della selezione
	 * @param id id del riferimento (attributo xlink:href)
	 * @param text testo del riferimento
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node insert(Node node, int start, int end, String id, String text);

	/**
	 * @param node
	 * @param start
	 * @param end
	 * @param id
	 * @param descrizioneMRif
	 * @return
	 */
	public Node insert(Node node, int start, int end, String[] id, String[] descrizioneMRif);

	/**
	 * Indica se ??? possibile modificare il nodo <code>&lt;rif&gt;</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile modificare in nodo
	 */
	public boolean canChange(Node node);

	/**
	 * Modifica il nodo <code>&lt;rif&gt;</code>.
	 * 
	 * @param node nodo di riferimento
	 * @param urn urn del riferimento
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node change(Node node, Urn urn);

	/**
	 * @param node
	 * @param urn
	 * @param descrizioneMRif
	 * @return
	 */
	public Node change(Node node, Vector urn, Vector descrizioneMRif);

	/**
	 * Modifica il nodo <code>&lt;rif&gt;</code>.
	 * 
	 * @param node nodo di riferimento
	 * @param id id del riferimento (attributo xlink:href)
	 * @param text testo del riferimento
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node change(Node node, String id, String text);

	/**
	 * @param node
	 * @param id
	 * @param descrizioneMRif
	 * @return
	 */
	public Node change(Node node, String[] id, String[] descrizioneMRif);

}
