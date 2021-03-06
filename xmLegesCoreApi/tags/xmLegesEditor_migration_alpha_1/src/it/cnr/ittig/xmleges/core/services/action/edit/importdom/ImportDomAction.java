package it.cnr.ittig.xmleges.core.services.action.edit.importdom;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per l'azione di inserimento di struttura nel documento.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface ImportDomAction extends Service {

	/**
	 * Apre la form per l'inserimento della struttura da altro file.
	 */
	public void doImportDom();

}
