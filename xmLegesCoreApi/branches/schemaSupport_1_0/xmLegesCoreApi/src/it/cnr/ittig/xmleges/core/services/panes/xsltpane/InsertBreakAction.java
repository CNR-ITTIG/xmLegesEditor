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
public interface InsertBreakAction {

	/**
	 * Metodo invocato sulla pressione del tasto invio.
	 * 
	 * @param node nodo corrente
	 * @param start inizio della selezione
	 * @param end fine della selezione
	 */
	public void insertBreak(Node node, int start, int end);

}
