package it.cnr.ittig.xmleges.core.services.action.file.save;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per salvare il documento corrente.
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
public interface FileSaveAction extends Service {

	/**
	 * Salva direttamente il file.
	 */
	public boolean doSave();

	/**
	 * Salva il file con nome.
	 */
	public boolean doSaveAs();
}
