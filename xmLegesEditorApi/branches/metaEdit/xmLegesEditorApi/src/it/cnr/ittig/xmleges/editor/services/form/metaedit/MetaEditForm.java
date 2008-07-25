package it.cnr.ittig.xmleges.editor.services.form.metaedit;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per ...
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
 * @author <a href="mailto:turchi@ittig.cnr.it">Fabrizio Turchi </a>
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public interface MetaEditForm extends Service {

	
	/**
	 * 
	 *
	 */
	public void openForm();
	
	/**
	 * Indica se la form e' stata chiusa premendo OK o Cancel
	 */
	public boolean isOKClicked();
	
	/**
	 * 
	 * @param idPartizione
	 */
	public void setIdPartizione(String idPartizione);
	
	/**
	 * 
	 * @param disposizioni
	 */
	public void setDisposizioni(Node[] disposizioni);
	
	/**
	 * 
	 * @return
	 */
	public Node[] getDisposizioni();

}
