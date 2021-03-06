package it.cnr.ittig.xmleges.core.services.action.file.importt;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la conversione di un testo non in formato xml.
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
public interface FileImportAction extends Service {

	/**
	 * Converte un document.
	 * 
	 * @return <code>true</code> se l'operazione &egrave; terminata con
	 *         successo
	 */
	public boolean doImport();
}
