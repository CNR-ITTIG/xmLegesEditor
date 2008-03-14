package it.cnr.ittig.xmleges.core.services.panes.tree;

import javax.swing.Icon;

import org.w3c.dom.Node;

/**
 * Interfaccia necessaria ad altri componenti se desiderano personalizzare la
 * visualizzazione dei nodi del pannello.
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
public interface TreePaneCellRenderer {
	/**
	 * Informa se &egrave; in grado di costruire correttamente il testo che deve
	 * essere visualizzato ed eventualmente l'icona per il nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo che deve essere visualizzato nell'albero
	 * @return <code>true</code> se &egrave; in grado di visualizzare il testo
	 */
	public boolean canRender(Node node);

	/**
	 * Restituisce il testo che deve essere visualizzato.
	 * 
	 * @param node nodo che deve essere visualizzato
	 * @return testo che deve essere visualizzato
	 */
	public String getText(Node node);

	/**
	 * Informa se &egrave; in grado di fornire un'icona per il nodo
	 * <code>node</code>
	 * 
	 * @param node nodo che deve essere visualizzato
	 * @return <code>true</code> se &egrave; in grado di restituire un'icona
	 *         per <code>node</code>
	 */
	public boolean hasIcon(Node node);

	/**
	 * Restituisce l'icona per il nodo <code>node</code>.
	 * 
	 * @param node nodo che deve essere visualizzato
	 * @param sel <code>true</code> se il nodo &egrave; selezionato
	 * @param expanded <code>true</code> se il nodo non &egrave; una foglia ed
	 *        &egrave; espanso
	 * @param leaf <code>true</code> se il nodo &egrave; una foglia
	 * @return l'icona che deve essere visualizzato
	 */
	public Icon getIcon(Node node, boolean sel, boolean expanded, boolean leaf);

}
