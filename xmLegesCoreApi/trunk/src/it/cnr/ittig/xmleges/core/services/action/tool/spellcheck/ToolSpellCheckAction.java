package it.cnr.ittig.xmleges.core.services.action.tool.spellcheck;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per l'avvio del controllo ortografico e l'attivazione del controllo
 * ortografico automatico.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface ToolSpellCheckAction extends Service {

	/**
	 * Attiva la form per il controllo ortografico.
	 */
	public void doSpellCheck();

	/**
	 * Abilita o disabilita il controllo ortografico automatico.
	 * 
	 * @param auto <code>true</code> per abilitare il controllo ortografico
	 *        automatico
	 */
	public void setAutoSpellCheck(boolean auto);
}
