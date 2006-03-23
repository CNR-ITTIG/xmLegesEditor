package it.cnr.ittig.xmleges.editor.services.action.meta;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la gestione delle azioni per l'inserimento dei metadati di un
 * documento NIR.
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
public interface MetaAction extends Service {

	/**
	 * Gestione dei metadati descrittori.
	 */
	public void doDescrittori();
	
	/**
	 * Gestione del ciclo di vita del documento
	 *
	 */
	public void doCiclodiVita();

	/**
	 * Gestione della Urn del documento.
	 */
	public void doUrn();

}
