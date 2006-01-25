package it.cnr.ittig.xmleges.core.services.panes.tree;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.frame.Pane;

/**
 * Servizio per visualizzare il documento DOM sottoforma di albero.
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
public interface TreePane extends Service, Pane {

	/**
	 * Imposta il nome del pannello.
	 * 
	 * @param name nome del pannello
	 */
	public void setName(String name);

	/**
	 * Aggiunge un oggetto che si preoccupa di visualizzare graficamente i nodi
	 * dell'albero.
	 * 
	 * @param renderer visualizzatore del nodo
	 */
	public void addRenderer(TreePaneCellRenderer renderer);

	/**
	 * Rimuove un oggetto che si preoccupa di visualizzare graficamente i nodi
	 * dell'albero.
	 * 
	 * @param renderer visualizzatore del nodo
	 */
	public void removeRenderer(TreePaneCellRenderer renderer);

}
