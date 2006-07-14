package it.cnr.ittig.xmleges.editor.services.dom.meta.cnr;

import it.cnr.ittig.services.manager.Service;
/**
 * Servizio per l'inserimento dell'inquadramento del documento.
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

public interface MetaCnr extends Service {
	
	/**
	 * Restituisce i metadati proprietari del cnr scritti sul documento.
	 * 
	 * @return metadati proprietari
	 */
	public String[] getProprietario();

	/**
	 * Scrive sul documento i metadati cnr associati
	 * 
	 * @param metadati
	 */
	public void setProprietario(String[] metadati);
}
