package it.cnr.ittig.xmleges.editor.services.form.meta.ciclodivita;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;

/**
 * Servizio per la gestione dei metadati vigenze del documento NIR.
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
 * @author <a href="mailto:t.paba@onetech.it">Tommaso Paba</a>
 */
public interface CiclodiVitaEventoForm extends Service {

	/**
	 * Apre la form per l'inserimento dei metadati descrittori di un documento
	 * NIR.
	 * 
	 * @return <code>true</code> se la form &egrave; valida
	 */
	public boolean openForm();

	/**
	 * Restituisce gli eventi del documento.
	 * 
	 * @return eventi del documento
	 */
	public Evento[] getEventi();

	/**
	 * Imposta l'elenco degli eventi del documento
	 * 
	 * @param eventi del documento
	 */
	public void setEventi(Evento[] eventi);
	
	
	/**
	 * Restituisce l'eventi selezionato del listtetfield.
	 * 
	 * @return evento selezionato del documento
	 */
	public Evento getSelectedEvento();
	
	

	
	/**
	 * Imposta la DTD del documento (base, completo...)
	 * 
	 * @param tipoDTD DTD del documento
	 */
	public void setTipoDTD(String tipoDTD);
}
