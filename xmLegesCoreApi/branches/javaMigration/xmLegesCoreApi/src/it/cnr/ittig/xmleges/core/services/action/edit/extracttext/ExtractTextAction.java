package it.cnr.ittig.xmleges.core.services.action.edit.extracttext;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per l'estrazione di un testo selezionato da un tag.
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public interface ExtractTextAction extends Service {

	/**
	 * Effettua l'estrazione di un testo selezionato.
	 * 
	 * @return <code>true</code> se l'inserimento &egrave; stato effettuato.
	 */
	public boolean doExtractText();

}
