package it.cnr.ittig.xmleges.editor.services.form.meta.descrittori;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Pubblicazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Vigenza;

/**
 * Servizio per la gestione dei metadati generali del documento NIR.
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
public interface MetaDescrittoriForm extends Service {

	/**
	 * Apre la form per l'inserimento dei metadati descrittori di un documento
	 * NIR.
	 * 
	 * @return <code>true</code> se la form &egrave; valida
	 */
	public boolean openForm();

	/**
	 * Restituisce i dati necessari per la pubblicazione.
	 * 
	 * @return dati pubblicazione
	 */
	public Pubblicazione getPubblicazione();

	/**
	 * Restituisce gli alias del documento.
	 * 
	 * @return alias
	 */
	public String[] getAlias();

	/**
	 * Restituisce le vigenze del documento.
	 * 
	 * @return vigenze
	 */
	public Vigenza[] getVigenze();

	/**
	 * Restituisce le relazioni ulteriori (non legate alle vigenze) con altri
	 * documenti.
	 * 
	 * @return relazioni con altri documenti
	 */
	public Relazione[] getRelazioniUlteriori();

	/**
	 * Restituisce le altre pubblicazioni
	 * 
	 * @return altre pubblicazioni
	 */
	public Pubblicazione[] getAltrePubblicazioni();

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
	 * Imposta gli alias del documento
	 * 
	 * @param aliases alias del documento
	 */
	public void setAlias(String[] aliases);

	/**
	 * Imposta i periodi di vigenza del documento
	 * 
	 * @param vigenze vigenze del documento
	 */
	public void setVigenze(Vigenza[] vigenze);

	/**
	 * Imposta le relazioni ulteriori (non legate alle vigenze) del documento
	 * 
	 * @param relazioni relazioni del documento
	 */
	public void setRelazioniUlteriori(Relazione[] relazioniUlteriori);

	/**
	 * Imposta la pubblicazione del documento
	 * 
	 * @param altrePubblicazioni pubblicazioni del documento
	 */
	public void setPubblicazione(Pubblicazione pubblicazione);

	/**
	 * Imposta le altre pubblicazioni del documento
	 * 
	 * @param altrePubblicazioni pubblicazioni del documento
	 */
	public void setAltrePubblicazioni(Pubblicazione[] altrePubblicazioni);
}
