package it.cnr.ittig.xmleges.editor.services.action.file.export;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per esportare il documento NIR corrente. 
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
public interface NirFileExportAction extends Service {

	/**
	 * Esporta il documento corrente nel browser.
	 */
	public boolean doExportBrowser();

	/**
	 * Esporta il documento corrente in HTML.
	 */
	public boolean doExportHTML();

	/**
	 * Esporta il documento corrente in PDF.
	 */
	public boolean doExportPDF();
	
	/**
	 * Esporta il documento corrente in RTF.
	 */
	public boolean doExportRTF();

	/**
	 * Produce il testo a fronte di un DDL sul Browser
	 */
	public boolean doTestoAFronte();

}
