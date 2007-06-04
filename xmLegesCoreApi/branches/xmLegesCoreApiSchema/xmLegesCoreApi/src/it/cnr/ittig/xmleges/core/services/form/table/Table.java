package it.cnr.ittig.xmleges.core.services.form.table;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.CommonForm;

import java.util.Vector;

/**
 * Servizio per la visuslizzazione di una tabella di riepilogo.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2005</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author Lorenzo Pasquinelli
 */

public interface Table extends Service, CommonForm {

	/**
	 * Imposta gli elementi visualizzati nella tabella
	 * 
	 * @param source
	 */
	public void setSource(Vector source);

	/**
	 * Restituisce gli elementi visualizzati nella tabella
	 * 
	 * @return
	 */
	public Vector getDestination();
}
