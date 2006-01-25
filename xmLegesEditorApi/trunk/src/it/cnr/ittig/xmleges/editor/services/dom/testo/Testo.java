package it.cnr.ittig.xmleges.editor.services.dom.testo;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per l'applicazione dei tag html h:b, h:i, h:u, h:sup, h:inf, al
 * testo
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface Testo extends Service {
	/**
	 * @param node
	 * @param action
	 * @return
	 */
	public boolean canTestoAction(Node node, String action);

	/**
	 * @param node
	 * @param start
	 * @param end
	 * @param action
	 */
	public void doActionOff(Node node, int start, int end, String action);

	/**
	 * @param node
	 * @param start
	 * @param end
	 * @param action
	 */
	public void doActionOn(Node node, int start, int end, String action);

	/**
	 * @param node
	 * @param azione
	 * @return
	 */
	public boolean canTestoActionRemoveTag(Node node, String azione);

	/**
	 * @param node
	 */
	public void doActionOffOnlyTag(Node node);

	/**
	 * @param node
	 * @param azione
	 * @return
	 */
	public boolean canDoTagTree(Node node, String azione);

	/**
	 * @param node
	 * @param azione
	 */
	public void doTagTree(Node node, String azione);

}
