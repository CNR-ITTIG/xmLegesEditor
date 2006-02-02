package it.cnr.ittig.xmleges.editor.services.form.xmleges.marker;

import it.cnr.ittig.services.manager.Service;

import java.io.File;
import java.io.InputStream;

/**
 * Servizio per la gestione della form per recuperare i dati necessari per
 * eseguire il <i>parser di struttura </i> per la conversione in xml dei file in
 * formato txt o html.
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
public interface XmLegesMarkerForm extends Service {

	/**
	 * Apre la form per l'analisi di un file di testo o HTML convertendolo in
	 * XML.
	 * 
	 * @return <code>true</code> se l'analisi &egrave; terminata con successo
	 */
	public boolean parse();

	/**
	 * Analizza il file di testo o HTML convertendolo in XML.
	 * 
	 * @param file
	 * @return <code>true</code> se l'analisi &egrave; terminata con successo
	 */
	public boolean parse(File file);

	/**
	 * Informa se l'analisi del file &egrave; andata a buon fine.
	 * 
	 * @return <code>true</code> se analisi corretta
	 */
	public boolean isParseOk();

	/**
	 * Restituisce il risultato dell'analisi.
	 * 
	 * @return risultato
	 */
	public InputStream getResult();
}
