package it.cnr.ittig.services.manager;

import it.cnr.ittig.xmleges.core.util.xml.UtilXml;

import java.io.InputStream;

import org.w3c.dom.Document;

/**
 * Classe di utilit&agrave; per memorizzare i servizi presenti
 * nell'applicazione.
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

public class ServiceContainer {

	public final static String LIFESTYLE_SINGLETON = "singleton";

	public final static String LIFESTYLE_POOL = "pool";

	public final static String ACTIVATION_STARTUP = "startup";

	public final static String ACTIVATION_LAZY = "lazy";

	public final static String serviceClassName = Service.class.getName();

	String Impl;

	Configuration configuration;

	String configurationResource;

	Object lifestyle;

	Object activation;

	Object instance;

	protected Object checkActivation(Object o) {
		return ACTIVATION_STARTUP.equals(o) ? ACTIVATION_STARTUP : ACTIVATION_LAZY;
	}

	protected Object checkLifeStyle(Object o) {
		return LIFESTYLE_POOL.equals(o) ? LIFESTYLE_POOL : LIFESTYLE_SINGLETON;
	}

	public Class getService() {
		try {
			return getServiceInterface(Class.forName(getImpl()));
		} catch (ClassNotFoundException ex) {
			return null;
		}
	}

	private Class getServiceInterface(Class cl) {
		Class[] interfaces = cl.getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
			if (serviceClassName.equals(interfaces[i].getName()))
				return cl;
			else {
				Class c = getServiceInterface(interfaces[i]);
				if (c != null)
					return c;
			}
		return null;
	}

	public String getImpl() {
		return this.Impl;
	}

	public void setImpl(String className) {
		this.Impl = className;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setConfiguration(String configurationResource) {
		this.configurationResource = configurationResource;
	}

	public Configuration getConfiguration() {
		if (configuration == null && instance != null) {
			InputStream is = instance.getClass().getResourceAsStream(configurationResource);
			Document d = UtilXml.readXML(is, false);
			if (d != null)
				setConfiguration(new Configuration(d.getDocumentElement()));
		}
		return this.configuration;
	}

	public Object getActivation() {
		return this.activation;
	}

	public void setActivation(Object activation) {
		this.activation = checkActivation(activation);
	}

	public Object getLifeStyle() {
		return this.lifestyle;
	}

	public void setLifeStyle(Object lifeStyle) {
		this.lifestyle = checkLifeStyle(lifeStyle);
	}

	public Object getInstance() {
		return this.instance;
	}

	public void setInstance(Object instance) {
		this.instance = instance;
	}

	public boolean isActivationStartup() {
		return ACTIVATION_STARTUP.equals(activation);
	}

	public boolean isActivationLazy() {
		return ACTIVATION_LAZY.equals(activation);
	}

	public boolean isLifeStyleSingleton() {
		return LIFESTYLE_SINGLETON.equals(lifestyle);
	}

	public boolean isLifeStylePool() {
		return LIFESTYLE_POOL.equals(lifestyle);
	}

	public String toString() {
		return "service=" + getService() + " class=" + getImpl() + " activation=" + getActivation() + " lifestyle=" + getLifeStyle();
	}
}
