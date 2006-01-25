package it.cnr.ittig.xmleges.editor.services.action.rinumerazione;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per l'attivazione della rinumerazione automatica del documento.
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
public interface RinumerazioneAction extends Service {

	/**
	 * Attiva o disattiva la rinumerazione automatica.
	 * 
	 * @param attiva <code>true</code> per attivare la rinumerazione
	 */
	public void doSetRinumerazione(boolean attiva);

}
