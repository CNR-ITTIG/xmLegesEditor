package it.cnr.ittig.xmleges.editor.services.dom.immagini;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per l'inserimento di una immagine
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
 */
public interface Immagini extends Service {

	/**
	 * Indica se &egrave; possibile inserire un nodo <code>&lt;img&gt;</code>
	 * all'interno del nodo <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile inserire un nodo
	 *         <code>&lt;img&gt;</code>
	 */
	public boolean canInsert(Node node);

	/**
	 * @param node
	 * @param start
	 * @param end
	 * @param ............vari ed eventuali
	 * @return <code>null</code> se la modifica non &egrave; riuscita; nodo
	 *         modificato
	 */
	public Node insert(Node node, String src, int width, String tipoWidth, int height, String tipoHeight, String alt);

}
