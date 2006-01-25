package it.cnr.ittig.xmleges.editor.services.action.spostamento;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
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
 * @author <a href="mailto:agnoloni@dsi.unifi.it">Tommaso Agnoloni </a>
 */
public interface SpostamentoAction extends Service {

	/**
	 * @param node
	 */
	public void doMoveUp(Node node, Node target);

	/**
	 * @param node
	 */
	public void doMoveDown(Node node, Node target);

}
