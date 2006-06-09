package it.cnr.ittig.xmleges.editor.services.dom.annessi;

import it.cnr.ittig.services.manager.Service;

import java.io.File;

/**
 * Servizio per la gestione degli annessi interni ed esterni al documento
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public interface Annessi extends Service {

	/**
	 * Inserisce un nuovo elemento annesso contenente il file
	 * <code>annesso<code> nel documento 
	 * 
	 * @param denAnnesso  denominazione dell'annesso
	 * @param titAnnesso  titolo dell'annesso
	 * @param preAnnesso  preambolo dell'annesso
	 * @param annesso     file da inserire come annesso
	 */
	public void setAnnessoInterno(String denAnnesso, String titAnnesso, String preAnnesso, File annesso);

	/**
	 * Inserisce un nuovo elemento annesso contenente il riferimento
	 * <code>rifEsterno<code> nel documento 
	 * 
	 * @param denAnnesso  denominazione dell'annesso
	 * @param titAnnesso  titolo dell'annesso
	 * @param preAnnesso  preambolo dell'annesso
	 */
	public void setAnnessoEsterno(String denAnnesso, String titAnnesso, String preAnnesso);

}
