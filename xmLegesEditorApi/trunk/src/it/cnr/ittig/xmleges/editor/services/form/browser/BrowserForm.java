package it.cnr.ittig.xmleges.editor.services.form.browser;

import java.net.URL;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.core.services.form.CommonForm;

/**
 * Servizio per presentare un oggetto grafico per l'apertura di un browser.
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
 */
public interface BrowserForm extends Service, CommonForm {

	/**
	 * Imposta url del browser.
	 * 
	 * @param url
	 *            url da visualizzare.
	 */
	public void setUrl(URL url);

	/**
	 * Imposta un listener su un particolare url che se il browser raggiunge,
	 * 
	 * @param url da notificare.
	 */
	public void setUrlListener(String url);	

	
	/**
	 * Imposta url del browser.
	 * 
	 * @param url url da visualizzare.
	 */
	public void setUrl(String url);		
	/**
	 * Restituisce la url corrente del browser.
	 * 
	 * @return URL url corrente del browser.
	 */
	public URL getUrl();

	/**
	 * Restituisce l'html visualizzato dal browser.
	 * 
	 * @return String corrispondente all'html visualizzato dal browser.
	 */
	public String getHtml();
		
	
	
	/**
	 * Imposta le dimensioni della finestra del browser
	 * @param width larghezza della finestra
	 * @param height altezza della finestra
	 */
	public void setSize(int width, int height);
	
	// ALTRI metodi esposti dalla libreria Jdic
}
