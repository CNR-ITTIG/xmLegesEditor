package it.cnr.ittig.xmleges.editor.services.dom.meta.urndocumento;

import org.w3c.dom.Document;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;
/**
 * Servizio per la gestione delle urn del documento.
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
 * 
 */

public interface MetaUrnDocumento extends Service {
	
	/**
	 * Inserisce le urn del documento principale nel tag meta
	 * @param doc DOM del documento
	 * @param urn Array delle urn da settare
	 * @param updateIntestazione <code>true</code> se si desidera aggiornare l'intestazione del documento sulla base della urn
 	 * @param updateEmananti <code>true</code> se si desidera aggiornare la lista degli emananti del documento sulla base della urn
	 * @return <code>true</code> se l'operazione &egrave; andata buon fine
	 */
	public boolean setUrnOnDocument(Document doc, Urn[] urn, boolean updateIntestazione, boolean updateEmananti);
	
	/**
	 * Restituisce le urn del documento principale lette dal tag meta 
	 * @param doc DOM del documento
	 * @return Array delle urn del documento
	 */
	public Urn[] getUrnFromDocument(Document doc);
	

}

