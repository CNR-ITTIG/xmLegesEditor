package it.cnr.ittig.xmleges.core.services.action.file.validator;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per verificare se il documento &egrave; valido.
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
public interface FileValidatorAction extends Service {

	/**
	 * Verifica se il documento &egrave; valido.
	 * 
	 * @return <code>true</code> se il documento &egrave; valido
	 */
	public boolean doValidation();
	
	
	/**
	 * 
	 * @param yesNo
	 */
	public void showMessage();

}
