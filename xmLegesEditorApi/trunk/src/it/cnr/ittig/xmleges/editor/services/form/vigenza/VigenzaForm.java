package it.cnr.ittig.xmleges.editor.services.form.vigenza;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;

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
	public Evento openForm(Evento[] vigenze, String testo);
	
	
	
	
	/////////////////////   NUOVA    INTERFACCIA   ////////////////
	
	
	/**
	 * Apre il form per la selezione/modifica della vigenza (solo vigore).
	 * @param testo Testo selezionato a cui applicare la vigenza o nome della partizione
	 * @param inizioVigore
	 * @param fineVigore
	 * @param status  (omissis|abrogato|annullato|sospeso)
	 * @return <code>true</code> se &egrave; stato premuto ok.
	 */
	public boolean openForm(String testo, Evento inizioVigore, Evento fineVigore, String status);
	
	/**
	 * Apre il form per la selezione/modifica della vigenza (vigore ed efficacia)
	 * @param testo Testo selezionato a cui applicare la vigenza o nome della partizione
	 * @param inizioVigore
	 * @param fineVigore
	 * @param inizioEfficacia
	 * @param fineEfficacia
	 * @param status  (omissis|abrogato|annullato|sospeso)
	 * @return <code>true</code> se &egrave; stato premuto ok.
	 */
	public boolean openForm(String testo, Evento inizioVigore, Evento fineVigore, Evento inizioEfficacia, Evento fineEfficacia, String status);
	
	/**
	 * 
	 * @return
	 */
	public Evento getInizioVigore();
	
	/**
	 * 
	 * @return
	 */
	public Evento getFineVigore();

	/**
	 * 
	 * @return
	 */
	public Evento getInizioEfficacia();
		
	/**
	 * 
	 * @return
	 */
	public Evento getFineEfficacia();
	
	/**
	 * 
	 * @return
	 */
	public String getStatus();
	
}
