package it.cnr.ittig.xmleges.core.services.license;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la gestione delle licenze.
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
public interface LicenseManager extends Service {

	/**
	 * Controlla la licenza visualizzando il testo <code>text</code> se non
	 * precedentemente accettata.
	 * 
	 * @param key chiave per la licenza
	 * @param text testo della licenza
	 * @return <code>true</code> se &egrave; stata accetta
	 */
	public boolean checkLicense(String key, String text);

}
