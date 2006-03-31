package it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Pubblicazione;

/**
 * Servizio per l'inserimento dei metadati generali.
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
public interface MetaCiclodivita extends Service {

	/**
	 * Imposta le relazioni del documento.
	 * 
	 * @param relazione relazione del documento
	 */
	public void setRelazione(Relazione relazione);

	/**
	 * Restituisce la Relazione del documento.
	 * 
	 * @return Relazione
	 */
	public Relazione getRelazione();

		/**
	 * Restituisce gli eventi del documento.
	 * 
	 * @return eventi del documento
	 */
	public Evento[] getEventi();

	/**
	 * Imposta gli eventi del documento.
	 * 
	 * @param evento evento
	 */
	public void setEvento(Evento[] evento);

	}
