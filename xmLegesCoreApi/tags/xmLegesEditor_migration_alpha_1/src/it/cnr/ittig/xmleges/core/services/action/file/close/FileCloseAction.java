package it.cnr.ittig.xmleges.core.services.action.file.close;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per chiudere il documento corrente.
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
 * @version 1.0
 */
public interface FileCloseAction extends Service {

	/**
	 * Chiude il documento corrente.
	 * 
	 * @return <code>true</code> se l'operazione &egrave; terminata con
	 *         successo
	 */
	public boolean doClose();
}