package it.cnr.ittig.xmleges.core.services.panes.xsltpane;

import org.w3c.dom.Node;

/**
 * Interfaccia per definire l'azione da compiere sulla pressione del tasto
 * invio.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>,
 */
public interface DeleteNextPrevAction {
	/**
	 * Metodo invocato sulla pressione del tasto <code>backspace</code> quando
	 * il cursore si trova all'inizio del testo.
	 * 
	 * @param curr nodo corrente
	 * @param prev node precedente visualizzato
	 * @return nuova posizione del cursore
	 */
	public int backspaceOnStart(Node curr, Node prev);

	/**
	 * Metodo invocato sulla pressione del tasto <code>delete</code> quando il
	 * cursore si trova alla fine del testo.
	 * 
	 * @param curr nodo corrente
	 * @param next node successivo visualizzato
	 * @return nuova posizione del cursore
	 */
	public int deleteOnEnd(Node curr, Node next);

}
