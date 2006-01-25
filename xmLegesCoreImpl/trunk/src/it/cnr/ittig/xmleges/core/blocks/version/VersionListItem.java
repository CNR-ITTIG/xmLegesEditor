package it.cnr.ittig.xmleges.core.blocks.version;

/**
 * Classe contenitore delle versioni con relative url del file contenente la nuova
 * versione e sua descrizione.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class VersionListItem {
	/** Versione */
	String version = "";

	/** URL del file dell'applicazione */
	String url = "";

	/** Descrizione della nuova versione */
	String desc = "";

	/**
	 * Restituisce la versione.
	 * 
	 * @return versione
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Imposta la versione.
	 * 
	 * @param version versione
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Restituisce la URL.
	 * 
	 * @return URL
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Imposta la URL
	 * 
	 * @param url url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Restituisce la descrizione.
	 * 
	 * @return descrizione
	 */
	public String getDescription() {
		return this.desc;
	}

	/**
	 * Imposta la descrizione.
	 * 
	 * @param desc descrizione
	 */
	public void setDescription(String desc) {
		this.desc = desc;
	}

	public String toString() {
		return this.version;
	}
}
