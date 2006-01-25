package it.cnr.ittig.xmleges.core.blocks.document;

import it.cnr.ittig.xmleges.core.services.document.DomEdit;

import org.apache.xerces.dom.events.MutationEventImpl;
import org.w3c.dom.Node;

/**
 * Implementazione delll'interfaccia <code>it.cnr.ittig.xmleges.editor.services.document.DomEdit</code>
 * per memorizzare le modifiche apportate al DOM intercettate tramite l'implementazione di
 * Xerces di DOM Level 2.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class DomEditImpl implements DomEdit {

	long timestamp;

	short phase;

	Object type;

	Node node;

	Node parent;

	Node nextSibling;

	String attribute;

	String textPrev;

	String textNew;

	/**
	 * Costruttore di DomEdit.
	 * 
	 * @param evt evento Xerces di variazione del DOM
	 */
	public DomEditImpl(MutationEventImpl evt) {
		super();
		timestamp = evt.getTimeStamp();
		type = evt.getType();
		textPrev = evt.getPrevValue();
		textNew = evt.getNewValue();
		node = (Node) evt.getTarget();
		parent = node.getParentNode();
		nextSibling = node.getNextSibling();
		attribute = evt.getAttrName();
	}

	/**
	 * Restituisce il tipo di modifica.
	 * 
	 * @return tipo di modifica
	 */
	public int getType() {
		if (this.type.equals(MutationEventImpl.DOM_CHARACTER_DATA_MODIFIED))
			return CHAR_NODE_MODIFIED;
		else if (this.type.equals(MutationEventImpl.DOM_SUBTREE_MODIFIED))
			return SUBTREE_MODIFIED;
		else if (this.type.equals(MutationEventImpl.DOM_NODE_INSERTED))
			return NODE_INSERTED;
		else if (this.type.equals(MutationEventImpl.DOM_NODE_REMOVED))
			return NODE_REMOVED;
		else if (this.type.equals(MutationEventImpl.DOM_ATTR_MODIFIED))
			return ATTR_NODE_MODIFIED;
		return UNKNOW;
	}

	/**
	 * Restituisce il nodo modificato.
	 * 
	 * @return nodo modificato
	 */
	public Node getNode() {
		return this.node;
	}

	/**
	 * Restituisce l'attributo modificato.
	 * 
	 * @return attributo modificato
	 */
	public String getAttribute() {
		return this.attribute;
	}

	/**
	 * Restituisce il padre del nodo modificato.
	 * 
	 * @return padre del nodo modificato
	 */
	public Node getParentNode() {
		return this.parent;
	}

	/**
	 * Restituisce il fratello successivo al nodo modificato.
	 * 
	 * @return fratello successivo al nodo modificato
	 */
	public Node getNextSibling() {
		return this.nextSibling;
	}

	/**
	 * Restituisce il testo precedente alla modifica. Se il nodo non &egrave; di testo
	 * allora restituisce <code>null</code>.
	 * 
	 * @return testo precedente alla modifica o <code>null</code>
	 */
	public String getTextPrev() {
		return this.textPrev;
	}

	/**
	 * Restituisce il testo nuovo generato dalla modifica. Se il nodo non &egrave; di
	 * testo allora restituisce <code>null</code>.
	 * 
	 * @return nuovo testo o <code>null</code>
	 */
	public String getTextNew() {
		return this.textNew;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("DomEdit: time=");
		sb.append(timestamp);
		sb.append(" phase=");
		sb.append(phase);
		sb.append(" type=");
		sb.append(type);
		sb.append(" node=");
		sb.append(node);
		sb.append(" textPrev=");
		sb.append(textPrev);
		sb.append(" textNew=");
		sb.append(textNew);
		sb.append(" parent=");
		sb.append(node.getParentNode());
		sb.append(" next=");
		sb.append(node.getNextSibling());
		return sb.toString();
	}

}
