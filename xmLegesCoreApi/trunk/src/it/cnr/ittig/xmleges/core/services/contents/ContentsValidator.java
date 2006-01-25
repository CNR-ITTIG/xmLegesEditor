package it.cnr.ittig.xmleges.core.services.contents;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la verifica dei contenuti del documento.
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
 * @version 1.0
 * @author <a href="mailto:agnoloni@dsi.unifi.it">Tommaso Agnoloni </a>
 */
public interface ContentsValidator extends Service {

	/**
	 * Abilita o disabilita il controllo dei contenuti del documento.
	 * 
	 * @param enabled <code>true</code> per abilitare il controllo
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Indica se il controllo dei contenuti &egrave; abilitato.
	 * 
	 * @return <code>true</code> se &egrave; abilitato
	 */
	public boolean isEnabled();
}
