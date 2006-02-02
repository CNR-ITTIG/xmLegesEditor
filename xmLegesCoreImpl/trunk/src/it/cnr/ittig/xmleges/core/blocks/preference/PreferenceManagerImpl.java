package it.cnr.ittig.xmleges.core.blocks.preference;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Disposable;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.preference.PreferenceManager</code>.</h1>
 * <h1>Descrizione</h1>
 * Questa implementazione usa un file XML per salvare le preferenze.
 * <h1>Configurazione</h1>
 * La configurazione <b>deve </b> evere il tag &lt;file&gt; contenente il nome del file
 * sul quale salvare le preference. <br>
 * Esempio:
 * 
 * </pre>
 * 
 * &lt;file&gt;preference.xml&lt;/file&gt;
 * 
 * </pre>
 * 
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>util-dom:1.0</li>
 * <li>util-domwriter:1.0</li>
 * <li>util-file:1.0</li>
 * <li>util-xml:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * Nessuno.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @version 1.0
 */
public class PreferenceManagerImpl implements PreferenceManager, Loggable, Configurable, Disposable, Initializable {
	Logger logger;

	Document conf;

	File preferenceFile;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		preferenceFile = new File(configuration.getChild("file").getValue());
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		logger.info("Loading preferences...");
		try {
			conf = UtilXml.readXML(preferenceFile);
			if (conf == null) {
				conf = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				conf.appendChild(conf.createElement("preference"));
				logger.warn("File not found!");
			} else
				logger.info("Loading preferences OK");
		} catch (Exception ex) {
			logger.warn("File not found!");
		}
	}

	// //////////////////////////////////////////////////// Disposable Interface
	public void dispose() {
		logger.info("Saving preferences...");
		try {
			DOMWriter writer = new DOMWriter();
			writer.setFormat(false);
			writer.setTrimText(false);
			writer.setOutput(preferenceFile);
			writer.write(conf);
			logger.info("Saving preferences OK");
		} catch (Exception ex) {
			logger.error("Saving preferences KO", ex);
		}
	}

	// ///////////////////////////////////////////// PreferenceManager Interface
	public boolean hasPreference(String section) {
		return UtilDom.findDirectChild(conf.getDocumentElement(), section) == null;
	}

	public Node getPreferenceAsDOM(String section) {
		// return UtilDom.findDirectChild(conf.getDocumentElement(), section);
		return UtilDom.checkAndCreate(conf.getDocumentElement(), section);
	}

	public InputStream getPreferenceAsInputStream(String section) {
		try {
			return new ByteArrayInputStream(UtilDom.getTextNode(getPreferenceAsDOM(section)).getBytes());
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			return null;
		}
	}

	public Properties getPreferenceAsProperties(String section) {
		try {
			Properties props = new Properties();
			props.load(getPreferenceAsInputStream(section));
			return props;
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			return null;
		}
	}

	public String getPreferenceAsString(String section) {
		try {
			StringBuffer sb = new StringBuffer();
			BufferedInputStream bis = new BufferedInputStream(getPreferenceAsInputStream(section));
			// ottimizzare con un reader
			while (bis.available() > 0)
				sb.append((char) bis.read());
			return sb.toString();
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			return null;
		}
	}

	public Vector getPreferenceAsVector(String section) {
		try {
			Properties p = getPreferenceAsProperties(section);
			int size = Integer.parseInt(p.getProperty("size"));
			Vector ret = new Vector(size);
			for (int i = 0; i < size; i++)
				ret.addElement(p.get("" + i).toString());
			return ret;
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			return new Vector();
		}
	}

	public void setPreference(String section, Node pref) {
		try {
			Node node = getPreferenceAsDOM(section);
			UtilDom.removeAllChildren(node);
			node.appendChild(pref);
		} catch (Exception ex) {
			logger.error("Error saving properties for section:" + section, ex);
		}
	}

	public void setPreference(String section, Properties pref) {
		try {
			Node node = getPreferenceAsDOM(section);
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			pref.store(baos, null);
			baos.flush();
			UtilDom.setTextNode(node, baos.toString());
		} catch (Exception ex) {
			logger.error("Error saving properties for section:" + section, ex);
		}
	}

	public void setPreference(String section, String pref) {
		try {
			Node node = getPreferenceAsDOM(section);
			UtilDom.setTextNode(node, pref);
		} catch (Exception ex) {
			logger.error("Error saving properties for section:" + section, ex);
		}
	}

	public void setPreference(String section, InputStream pref) {
		try {
			Node node = getPreferenceAsDOM(section);
			UtilDom.setTextNode(node, UtilFile.inputStreamToString(pref));
		} catch (Exception ex) {
			logger.error("Error saving properties for section:" + section, ex);
		}
	}

	public void setPreference(String section, Vector v) {
		try {
			Properties p = new Properties();
			int size = v.size();
			p.put("size", "" + size);
			for (int i = 0; i < size; i++)
				p.put("" + i, v.get(i).toString());
			setPreference(section, p);
		} catch (Exception ex) {
			logger.error("Error saving properties for section:" + section, ex);
		}

	}
}
