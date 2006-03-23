package it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;

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
public interface MetaDescrittori extends Service {

	/**
	 * Imposta la pubblicazione del documento.
	 * 
	 * @param pubblicazione pubblicazione del documento
	 */
	public void setPubblicazione(Pubblicazione pubblicazione);

	/**
	 * Restituisce la pubblicazione del documento.
	 * 
	 * @return pubblicazione
	 */
	public Pubblicazione getPubblicazione();

	/**
	 * Imposta le altre pubbilcazioni del documento.
	 * 
	 * @param pubblicazioni altre pubblicazioni
	 */
	public void setAltrePubblicazioni(Pubblicazione[] pubblicazioni);

	/**
	 * Restituisce le altre pubblicazioni del documento.
	 * 
	 * @return altre pubblicazioni
	 */
	public Pubblicazione[] getAltrePubblicazioni();

	/**
	 * Imposta gli alias del documento
	 * 
	 * @param alias alias del documento
	 */
	public void setAlias(String[] alias);

	/**
	 * Restituisce gli alias del documento
	 * 
	 * @return alias del documento
	 */
	public String[] getAlias();

	/**
	 * Restituisce le vigenze del documento.
	 * 
	 * @return vigenze del documento
	 */
	public Evento[] getVigenze();

	/**
	 * Imposta le vigenze del documento.
	 * 
	 * @param vigenza vigenze
	 */
	public void setVigenze(Evento[] vigenza);

	/**
	 * Restituisce le relazioni del documento con altri documenti
	 * 
	 * @return relazioni con altri documenti
	 */
	public Relazione[] getRelazioni();

	/**
	 * Imposta le relazioni del documento con altri documenti
	 * 
	 * @param relazioni relazioni con altri documenti
	 */
	public void setRelazioni(Relazione[] relazioni);
}
