package it.cnr.ittig.xmleges.editor.services.form.vigenza;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Vigenza;

/**
 * Form per la selezione di una vigenza da applicare a un testo.
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
public interface VigenzaForm extends Service {

	/**
	 * Apre il form per la selezione della vigenza.
	 * 
	 * @param vigenze array con le vigenze del documento
	 * @param testo testo per il quale selezionare la vigenza
	 * @return vigenza selezionata, o null se l'utente ha premuto "Annulla"
	 */
	public Vigenza openForm(Vigenza[] vigenze, String testo);
}
