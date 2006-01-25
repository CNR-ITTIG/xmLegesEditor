package it.cnr.ittig.xmleges.core.services.document;

import org.w3c.dom.Node;

/**
 * Classe per memorizzare le modifiche apportate al DOM intercettate tramite gli
 * eventi DOM level 2.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface DomEdit {

	/** Tipo di modifica: sconosciuta. */
	public final static int UNKNOW = 0;

	/** Tipo di modifica: sottoalbero modificato. */
	public final static int SUBTREE_MODIFIED = 1;

	/** Tipo di modifica: nodo inserito. */
	public final static int NODE_INSERTED = 2;

	/** Tipo di modifica: nodo rimosso. */
	public final static int NODE_REMOVED = 3;

	/** Tipo di modifica: testo modificato. */
	public final static int CHAR_NODE_MODIFIED = 4;

	/** Tipo di modifica: attributo modificato. */
	public final static int ATTR_NODE_MODIFIED = 5;

	/**
	 * Restituisce il tipo di modifica che &egrave; compreso tra:
	 * <ul>
	 * <li>NODE_INSERTED;</li>
	 * <li>NODE_REMOVED;</li>
	 * <li>CHAR_NODE_MODIFIED;</li>
	 * <li>ATTR_NODE_MODIFIED;</li>
	 * </ul>
	 * 
	 * @return tipo di modifica
	 */
	public int getType();

	/**
	 * Restituisce il nodo modificato.
	 * 
	 * @return nodo modificato
	 */
	public Node getNode();

	/**
	 * Restituisce l'attributo modificato.
	 * 
	 * @return attributo modificato
	 */
	public String getAttribute();

	/**
	 * Restituisce il padre del nodo modificato.
	 * 
	 * @return padre del nodo modificato
	 */
	public Node getParentNode();

	/**
	 * Restituisce il fratello successivo al nodo modificato.
	 * 
	 * @return fratello successivo al nodo modificato
	 */
	public Node getNextSibling();

	/**
	 * Restituisce il testo precedente alla modifica. Se il nodo non &egrave; di
	 * testo allora restituisce <code>null</code>.
	 * 
	 * @return testo precedente alla modifica o <code>null</code>
	 */
	public String getTextPrev();

	/**
	 * Restituisce il testo nuovo generato dalla modifica. Se il nodo non
	 * &egrave; di testo allora restituisce <code>null</code>.
	 * 
	 * @return nuovo testo o <code>null</code>
	 */
	public String getTextNew();

}
