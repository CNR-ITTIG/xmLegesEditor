package it.cnr.ittig.xmleges.editor.services.form.meta.descrittori;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Vigenza;

/**
 * Servizio per la gestione dei metadati vigenze del documento NIR.
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
 * @author <a href="mailto:t.paba@onetech.it">Tommaso Paba</a>
 */
public interface MetaDescrittoriVigenzaForm extends Service {

	/**
	 * Apre la form per l'inserimento dei metadati descrittori di un documento
	 * NIR.
	 * 
	 * @return <code>true</code> se la form &egrave; valida
	 */
	public boolean openForm();

	/**
	 * Restituisce le vigenze del documento.
	 * 
	 * @return vigenze del documento
	 */
	public Vigenza[] getVigenze();

	/**
	 * Imposta l'elenco dei periodi di vigenza del documento
	 * 
	 * @param vigenze vigenze del documento
	 */
	public void setVigenze(Vigenza[] vigenze);

	/**
	 * Imposta l'elenco delle relazioni ulteriori (non legate alle vigenze)
	 * 
	 * @param relazioni relazioni del documento
	 */
	public void setRelazioniUlteriori(Relazione[] relazioniUlteriori);

	/**
	 * Restituisce il tipo del documento (originale, vigente, multivigente).
	 * 
	 * @return tipo del documento
	 */
	public String getTipoDocumento();

	/**
	 * Imposta il tipo del documento (originale, vigente, multivigente)
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
}
