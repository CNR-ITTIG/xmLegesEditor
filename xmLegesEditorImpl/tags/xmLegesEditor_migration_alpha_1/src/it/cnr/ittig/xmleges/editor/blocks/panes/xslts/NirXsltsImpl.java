package it.cnr.ittig.xmleges.editor.blocks.panes.xslts;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.panes.xslts.NirXslts;

import java.io.File;
import java.util.Hashtable;

/**
 * <h1>Implementazione del servizio</h1>
 * <code>it.cnr.ittig.xmleges.editor.services.panes.xslts.NirPanesXslts</code>
 * <h1>Descrizione</h1>
 * Servizio per la gestione dei file di trasformazione XSLT e fogli di stile CSS.
 * <h1>Configurazione</h1>
 * La configurazione pu&ograve; avere i seguenti tag (tutti opzionali):
 * <ul>
 * <li><code>&lt;xslt&gt;</code>: specifica un file xsl; </li>
 * <li><code>&lt;css&gt;</code>: specifica un file css.</li>
 * </ul>
 * Esempio:
 * 
 * <pre>
 *     &lt;xslt	name="nomedisistemaxslt"	file="nomesulfilesystemdelxslt.xsl" / &gt;
 *     &lt;css	name="nomedisistemacss"	file="nomesulfilesystemdelcss.xsl"	startup="true" / &gt;
 * </pre>
 * 
 * <h1>Dipendenze</h1>
 * Nessuna
 * <h1>I18n</h1>
 * Nessuno
 * 
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
public class NirXsltsImpl implements NirXslts, Loggable, Configurable {
	Logger logger;

	Hashtable xsltHash = new Hashtable();

	Hashtable cssHash = new Hashtable();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		Configuration c[];
		c = configuration.getChildren();
		for (int i = 0; i < c.length; i++) {
			if (c[i].getName().equals("xslt"))
				xsltHash.put(c[i].getAttribute("name"), c[i].getAttribute("file"));
			else
				cssHash.put(c[i].getAttribute("name"), c[i].getAttribute("file"));
			if (c[i].getAttributeAsBoolean("startup", false))
				copyFile(c[i].getAttribute("file"));
		}
	}

	// ///////////////////////////////////////////////// NirPanesXslts Interface
	public boolean hasXslt(String name) {
		return getResource(name, true) != null;
	}

	public File getXslt(String name) {
		return getResource(name, true);
	}

	public boolean hasCss(String name) {
		return getResource(name, false) != null;
	}

	public File getCss(String name) {
		return getResource(name, false);
	}

	protected synchronized File getResource(String name, boolean xslt) {
		String file = (String) (xslt ? xsltHash.get(name) : cssHash.get(name));
		logger.debug("file:" + file);
		copyFile(file);
		return UtilFile.getFileFromTemp(file);
	}

	protected synchronized void copyFile(String file) {
		//FIXME Scommentare se non si vuole sovrascrivere i file (xsl) e (css) già presenti nella TEMP
		//if (!UtilFile.fileExistInTemp(file))
			UtilFile.copyFileInTemp(getClass().getResourceAsStream(file), file);
	}

}
