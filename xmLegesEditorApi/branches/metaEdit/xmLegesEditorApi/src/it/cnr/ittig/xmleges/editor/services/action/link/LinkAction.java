package it.cnr.ittig.xmleges.editor.services.action.link;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per l'inserimento di un nuovo link ad un ipertesto.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface LinkAction extends Service {
	/**
	 * Apertura della form per l'inserimento di un link ad un ipertesto
	 */
	public void doChangeLink();

	

}
