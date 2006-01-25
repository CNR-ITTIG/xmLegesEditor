package it.cnr.ittig.xmleges.core.blocks.i18n;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.i18n.I18n</code>.</h1>
 * <h1>Descrizione</h1>
 * I dizionari di internazionalizzazione possono essere molteplici; le chiavi e i
 * corrispondenti valori sono cercati dal primo dizionario fino a quando la ricerca ha
 * successo. Il valore della chiave pu&ograve; referenziare altre chiavi tramite
 * <code>$(key)</code>.<br>
 * Esempio:
 * 
 * <pre>
 *   key=chiave
 *   key1=${key} 1
 *   kde2=${key} 2
 * </pre>
 * 
 * la richiesta <code>getTextFor("key2")</code> restituisce il testo
 * 
 * <pre>
 *   chiave 2
 * </pre>
 * 
 * Le chiavi aggiunte tramite i metodi <code>addKey</code> sono salvati nelle preferenze
 * tramite il servizio <code>PreferenceManager</code> nella sezione <b>I18nImpl </b>.
 * <h1>Configurazione</h1>
 * La configurazione deve avere i tag:
 * <ul>
 * <li><code>&lt;base&gt;</code>: contenente il file di internazionalizzazione
 * (default i18n);
 * <li><code>&lt;language&gt;</code>: con attributi <code>language</code> (default
 * "en"), <code>country</code> (default "US") e <code>variant</code> (default null).
 * </ul>
 * <b>Attenzione </b>: il container deve avere nel classpath i file specificato per i
 * dizionari.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.preference.PreferenceManager:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * Nussuno.
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
 * @see it.cnr.ittig.xmleges.core.services.i18n.I18n
 * @see it.cnr.ittig.xmleges.core.services.preference.PreferenceManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class I18nImpl implements I18n, Loggable, Configurable, Serviceable, Initializable {

	Logger logger;

	static Locale locale = null;

	static ResourceBundle[] bundles = null;

	static String[] bases = null;

	static Properties props = new Properties();

	PreferenceManager preference;

	Hashtable buffer = new Hashtable(1000);

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		preference = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		if (bases == null) {
			Configuration[] cs = configuration.getChildren("base");
			if (cs != null) {
				bases = new String[cs.length];
				for (int i = 0; i < cs.length; i++)
					bases[i] = cs[i].getValue();
			} else
				bases = new String[] { "i18n" };
		}
		if (locale == null) {
			Configuration c = configuration.getChild("locale");
			String language = c.getAttribute("language", "en");
			String country = c.getAttribute("country", "US");
			String variant = c.getAttribute("variant", null);
			if (variant != null)
				setLocale(language, country, variant);
			else
				setLocale(language, country);
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		if (locale == null)
			setLocale(Locale.getDefault());
		props = preference.getPreferenceAsProperties(getClass().getName());
	}

	// ////////////////////////////////////////////////////////// I18n Interface
	public void setLocale(String language) {
		setLocale(new Locale(language));
	}

	public void setLocale(String language, String country) {
		setLocale(new Locale(language, country));
	}

	public void setLocale(String language, String country, String variant) {
		setLocale(new Locale(language, country, variant));
	}

	public void setLocale(Locale locale) {
		I18nImpl.locale = locale;
		Locale.setDefault(locale);
		logger.info("Locale='" + locale.toString() + '\'');
		bundles = new ResourceBundle[bases.length];
		for (int i = 0; i < bases.length; i++) {
			try {
				try {
					bundles[i] = ResourceBundle.getBundle(bases[i], locale);
					logger.info("Bundle loaded for base:" + bases[i]);
				} catch (Exception ex) {
					bundles[i] = ResourceBundle.getBundle(bases[i]);
					logger.warn("Missing resource for locale:" + locale.toString());
					logger.warn("Using:" + bases[i]);
				}
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
		}
	}

	public Locale getLocale() {
		return locale;
	}

	public boolean hasKey(String key) {
		if (props.containsKey(key))
			return true;
		for (int i = 0; i < bundles.length; i++)
			try {
				bundles[i].getString(key);
				return true;
			} catch (Exception ex) {
			}
		return false;
	}

	public boolean hasNotEmptyKey(String key) {
		if (props.containsKey(key) && props.getProperty(key).trim().length() > 0)
			return true;
		for (int i = 0; i < bundles.length; i++)
			try {
				return bundles[i].getString(key).trim().length() > 0;
			} catch (Exception ex) {
			}
		return false;
	}

	public String getTextFor(String key) {
		String t = (String) buffer.get(key);
		if (t == null) {
			t = getValue(key);
			if (t != null)
				buffer.put(key, t);
		}
		return t;
	}

	public Image getImageFor(String key) {
		return loadImage(getValue(key));
	}

	public Icon getIconFor(String key) {
		return new ImageIcon(getImageFor(key));
	}

	public Object getObjectFor(String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class cl = Class.forName(getValue(key));
		return cl.newInstance();
	}

	public void addKey(String key, String value) {
		addKey(key, value, locale);
	}

	public void addKey(String key, String value, Locale locale) {
		if (locale == null)
			props.setProperty(key, value);
		else
			props.setProperty(locale.toString() + '.' + key, value);
		preference.setPreference(getClass().getName(), props);
	}

	protected String getValue(String key) {
		if (bundles == null) {
			logger.warn("All ResourceBundles are null");
			return null;
		}
		if (key == null) {
			logger.warn("key null");
			return null;
		}
		String fromProps = getValueFromProps(key);
		if (fromProps != null)
			return fromProps;
		String ret = key;
		for (int i = 0; i < bundles.length; i++)
			try {
				ret = bundles[i].getString(key);
				break;
			} catch (Exception ex) {
				// do nothing
			}
		String rep = replaceReferences(key, ret);
		if (key.equals(ret)) {
			if (logger.isDebugEnabled())
				logger.debug("missing: '" + key + '\'');
			return ret;
		} else
			return rep;

	}

	protected String getValueFromProps(String key) {
		if (props.containsKey(locale.toString() + key))
			return props.getProperty(locale.toString() + key);
		if (props.containsKey(key))
			return props.getProperty(key);
		return null;
	}

	protected String replaceReferences(String key, String value) {
		// if (logger.isDebugEnabled())
		// logger.debug("replace:key=" + key + " value=" + value);
		String ret = value;
		try {
			while (ret.indexOf('$') != -1) {
				int s = ret.indexOf('$');
				int e = ret.indexOf('}');
				String insert = ret.substring(s + 2, e);
				String before = ret.substring(0, s);
				String after = ret.substring(e + 1);
				ret = before + getValue(insert) + after;
				// if (logger.isDebugEnabled()) {
				// logger.debug("before='" + before + "' after='" + after + "'
				// insert='" + insert
				// + "' ret='" + ret + '\'');
				// }
			}
		} catch (StringIndexOutOfBoundsException ex) {
			logger.error("Value with uncorrect reference (${key}):" + key + '=' + value, ex);
		}
		return ret;
	}

	protected Image loadImage(String fileName) {
		Image image = null;
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		try {
			image = toolkit.getImage(fileName);
			if (image.getWidth(null) < 0) {
				URL url = getClass().getResource(fileName);
				if (url != null)
					image = toolkit.getImage(url);
				if (image.getWidth(null) < 0) {
					url = getClass().getResource("/" + fileName);
					if (url != null)
						image = toolkit.getImage(url);
				}
			}
		} catch (Exception ex) {
			logger.error("Error loading image: " + fileName, ex);
		}
		return image;
	}

}
