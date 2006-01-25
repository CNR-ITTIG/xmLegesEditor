package it.cnr.ittig.xmleges.core.services.bars;

import it.cnr.ittig.services.manager.Service;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 * Servizio per la creazione delle barre dei menu, degli strumenti e di stato.
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
public interface Bars extends Service {

	/**
	 * Restituisce il menu sottoforma di javax.swing.JMenuBar.
	 * 
	 * @return menu
	 */
	public JMenuBar getMenuBar();

	/**
	 * Restituisce il pannello delle barre degli strumenti.
	 * 
	 * @return menu
	 */
	public JPanel getToolBarPanel();

	/**
	 * Restituisce il pannello con la barra di stato.
	 * 
	 * @return menu
	 */
	public StatusBar getStatusBar();

	public JPopupMenu getPopup(boolean activeOnly);

}
