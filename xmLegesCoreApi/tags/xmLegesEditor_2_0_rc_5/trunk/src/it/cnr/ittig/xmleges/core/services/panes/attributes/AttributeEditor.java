package it.cnr.ittig.xmleges.core.services.panes.attributes;

import java.awt.Component;

import org.w3c.dom.Node;

/**
 * Interfaccia per definire gli editor specializzati nella modifica assistita di
 * certi nodi dell'albero DOM del documento.
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
public interface AttributeEditor {

	/**
	 * Indica se l'editor pu&ograve; modificare l'attributo <code>attrib</code>
	 * del nodo <code>node</code>.
	 * 
	 * @param node nodo dell'albero DOM che ha l'attributo <code>attrib</code>
	 * @param attrib nodo attributo
	 * @return <code>true</code> se pu&ograve; modificare l'attributo
	 *         <code>attrib</code>
	 */
	public boolean canEdit(Node node, Node attrib);

	/**
	 * Restituisce il componente che permette la modifica dell'attributo
	 * <code>attrib</code> del nodo <code>node</code>.
	 * 
	 * @param node nodo dell'albero DOM che ha l'attributo <code>attrib</code>
	 * @param attrib nodo attributo
	 * @return componente che permette la modifica dell'attributo
	 */
	public Component getEditor(Node node, Node attrib);

	/**
	 * Indica se l'editor ha la possibilit&agrave; di attivare una form per
	 * modificare l'attributo <code>attrib</code> del nodo <code>node</code>.
	 * 
	 * @param node nodo dell'albero DOM che ha l'attributo <code>attrib</code>
	 * @param attrib nodo attributo
	 * @return <code>true</code> se pu&ograve; modificare l'attributo tramite
	 *         una form
	 */
	public boolean hasForm(Node node, Node attrib);

	/**
	 * Attiva la form per la modifica dell'attributo <code>attrib</code> del
	 * nodo <code>node</code> se il motodo <code>hasForm</code> ha
	 * restituito <code>true</code>.
	 * 
	 * @param node nodo dell'albero DOM che ha l'attributo <code>attrib</code>
	 * @param attrib nodo attributo
	 * @return componente che permette la modifica dell'attributo
	 */
	public void showEditorForm();

	/**
	 * Restituisce il valore dell'editor per assegnarlo all'attributo
	 * <code>attrib</code> del nodo <code>node</code>.
	 * 
	 * @param node nodo dell'albero DOM che ha l'attributo <code>attrib</code>
	 * @param attrib nodo attributo
	 * @return valore che deve essere assegnato all'attributo
	 *         <code>attrib</code>
	 */
	public String getValueChanged(Node node, Node attrib);

}
