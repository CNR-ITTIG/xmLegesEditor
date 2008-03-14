package it.cnr.ittig.xmleges.editor.blocks.form.browser;

import java.util.EventObject;

/**
 * Classe evento per notificare un evento dal browser.
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
public class BrowserEvent extends EventObject {

	boolean urlDownload = false;
	
	/**
	 * Crea una nuova istanza di <code>BrowserEvent</code> per
	 * l'evento di .....
	 * 
	 * @param source sorgente dell'evento
	 */
	public BrowserEvent(Object source) {
		super(source);
		urlDownload = true;
	}

	/**
	 * Indica se l'evento rappresenta una url raggiunta.
	 * 
	 * @return <code>true</code> se ho raggiunto una prefissata url
	 */
	public boolean isUrlDownload() {
		return this.urlDownload;
	}

}
