package it.cnr.ittig.xmleges.editor.blocks.dom.rifincompleti;

import it.cnr.ittig.xmleges.editor.services.dom.rifincompleti.DomRifIncompleti;

import org.w3c.dom.Node;

/**
 * Interfaccia per indicare il riferimento errato all'interno di un nodo DOM.
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class DomRifIncompletiImpl implements DomRifIncompleti{
	
	private Node node;

	public DomRifIncompletiImpl(Node node) {
		this.node = node;
	}


	/**
	 * Restituisce il nodo contenente il riferimento incompleto.
	 * 
	 * @return nodo contenente il riferimento incompleto.
	 */
	public Node getNode() {
		
		return node;
	}
}
