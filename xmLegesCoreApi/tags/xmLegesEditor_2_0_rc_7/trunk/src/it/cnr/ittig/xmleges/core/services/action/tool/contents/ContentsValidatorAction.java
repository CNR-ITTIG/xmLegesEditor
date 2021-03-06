package it.cnr.ittig.xmleges.core.services.action.tool.contents;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per attivare o disattivare la varifica dei contenuti del documento.
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
public interface ContentsValidatorAction extends Service {

	/**
	 * Abilita il controllo dei contenuti del documento.
	 * 
	 * @param enable <code>true</code> per abilitare il controllo
	 */
	public void doEnable(boolean enable);
}
