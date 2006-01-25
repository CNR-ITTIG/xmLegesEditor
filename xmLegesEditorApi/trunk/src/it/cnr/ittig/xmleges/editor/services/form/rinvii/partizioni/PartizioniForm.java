package it.cnr.ittig.xmleges.editor.services.form.rinvii.partizioni;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per ...
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
 * @author <a href="mailto:sarti@dii.unisi.it">Lorenzo Sarti </a>
 */
public interface PartizioniForm extends Service {
	/**
	 * Apre la form Partizioni
	 */
	public boolean openForm();

	/**
	 * @return restituisce la stringa che descrive la partizione inserita Es:
	 *         Articolo 1 comma 2 Es: Articolo 3 comma 2, 3
	 */
	public String getPartizioneEstesa();
}
