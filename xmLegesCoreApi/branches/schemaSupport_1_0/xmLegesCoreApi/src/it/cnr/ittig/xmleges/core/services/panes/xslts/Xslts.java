package it.cnr.ittig.xmleges.core.services.panes.xslts;

import it.cnr.ittig.services.manager.Service;

import java.io.File;

/**
 * Servizio per la gestione dei file di trasformazione XSLT e fogli di stile CSS
 * necessari per i pannelli di modifica del testo.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface Xslts extends Service {

	/**
	 * Informa se il componente &egrave; in grado di fornire il foglio di
	 * trasformazione XSLT identificato dalla chiave <code>key</code>.
	 * 
	 * @param name identificativo del foglio di trasformazione
	 * @return <code>true</code> se &egrave; disponibile
	 */
	public boolean hasXslt(String name);

	/**
	 * Restituisce il foglio di trasformazione XSLT identificato dalla chiave
	 * <code>name</code>.
	 * 
	 * @param name identificativo del foglio di trasformazione
	 * @return foglio di trasformazione o <code>null</code> se non disponibile
	 */
	public File getXslt(String name);

	/**
	 * Informa se il componente &egrave; in grado di fornire il foglio di stile
	 * CSS identificativo dalla chiave <code>name</code>.
	 * 
	 * @param name identificativo del foglio di stile
	 * @return <code>true</code> se &egrave; disponibile
	 */
	public boolean hasCss(String name);

	/**
	 * Restituisce il foglio di stile CSS identificato dalla chiave
	 * <code>name</code>.
	 * 
	 * @param name identificativo del foglio di stile
	 * @return foglio di stile o <code>null</code> se non disponibile
	 */
	public File getCss(String name);

}
