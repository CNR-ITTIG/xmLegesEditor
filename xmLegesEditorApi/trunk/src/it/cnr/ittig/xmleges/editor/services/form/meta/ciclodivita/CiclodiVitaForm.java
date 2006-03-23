package it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Pubblicazione;

/**
 * Servizio per la gestione della form per il ciclodivita del documento .
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public interface CiclodiVitaForm extends Service {

	/**
	 * Apre la form per l'inserimento degli eventi e delle relazioni associate ad un documento
	 * NIR.
	 * 
	 * @return <code>true</code> se la form &egrave; valida
	 */
	public boolean openForm();


	/**
	 * Restituisce gli eventi associati al documento.
	 * 
	 * @return vigenze
	 */
	public Evento[] getEventi();

	/**
	 * Restituisce le relazioni ulteriori (non legate agli eventi) con altri
	 * documenti.
	 * 
	 * @return relazioni con altri documenti
	 */
	public Relazione[] getRelazioniUlteriori();



	/**
	 * Restituisce il tipo del documento (originale, vigente, multivigente).
	 * 
	 * @return tipo del documento
	 */
	public String getTipoDocumento();

	/**
	 * Imposta il tipo del documento
	 * 
	 * @param tipoDocumento tipo del documento
	 */
	public void setTipoDocumento(String tipoDocumento);

	/**
	 * Imposta la DTD del documento (base, completo...)
	 * 
	 * @param tipoDTD DTD del documento
	 */
	public void setTipoDTD(String tipoDTD);



	/**
	 * Imposta sulla form gli eventi associati al documento
	 * 
	 * @param eventi eventi associati al documento
	 */
	public void setEventi(Evento[] eventi);

	/**
	 * Imposta le relazioni ulteriori (non legate agli eventi) del documento
	 * 
	 * @param relazioni relazioni del documento
	 */
	public void setRelazioniUlteriori(Relazione[] relazioniUlteriori);


}
