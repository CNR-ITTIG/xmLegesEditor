package it.cnr.ittig.services.manager;

import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe per incapsulare la configurazione di un componente.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class Configuration {

	Node conf;

	/**
	 * Costruttuore del nodo di configurazione.
	 * 
	 * @param node nodo dom interno a <code>configuration</code>
	 */
	public Configuration(Node node) {
		this.conf = node;
	}

	/**
	 * Restituisce il nome del nodo.
	 * 
	 * @return nome del nodo
	 */
	public String getName() {
		return conf.getNodeName();
	}

	/**
	 * Restituisce testo del nodo.
	 * 
	 * @return testo del nodo
	 */
	public String getValue() {
		Node n = conf.getFirstChild();
		return n != null ? n.getNodeValue() : null;
	}

	/**
	 * Restituisce il testo del nodo. In caso di errore restituisce il valore di
	 * default <code>def</code>.
	 * 
	 * @param def valore di default
	 * @return testo del nodo
	 */
	public String getValue(String def) {
		String ret = getValue();
		return ret != null ? ret : def;
	}

	/**
	 * Restituisce il testo del nodo come intero.
	 * 
	 * @return testo del nodo
	 */
	public int getValueAsInteger() throws ConfigurationException {
		try {
			return Integer.parseInt(getValue());
		} catch (NumberFormatException ex) {
			throw new ConfigurationException("Value is not integer:" + getValue(), ex);
		}
	}

	/**
	 * Restituisce il testo del nodo come intero. In caso di errore restituisce
	 * il valore di default <code>def</code>.
	 * 
	 * @param def valore di default
	 * @return testo del nodo
	 */
	public int getValueAsInteger(int def) {
		try {
			return getValueAsInteger();
		} catch (Exception ex) {
			return def;
		}
	}

	/**
	 * Restituisce il testo del nodo come boolean.
	 * 
	 * @return <code>true</code>/<code>false</code>
	 */
	public boolean getValueAsBoolean() throws ConfigurationException {
		try {
			return Boolean.valueOf(getValue()).booleanValue();
		} catch (NumberFormatException ex) {
			throw new ConfigurationException("Value is not boolean:" + getValue(), ex);
		}
	}

	/**
	 * Restituisce il testo del nodo come boolean. In caso di errore restituisce
	 * il valore di default <code>def</code>.
	 * 
	 * @param def valore di default
	 * @return <code>true</code>/<code>false</code>
	 */
	public boolean getValueAsBoolean(boolean def) {
		try {
			return getValueAsBoolean();
		} catch (Exception ex) {
			return def;
		}
	}

	// ///////////////////////////////////////////////////////////// ATTRIBUTES
	public boolean hasAttribute(String name) {
		return UtilDom.getAttributeValueAsString(conf, name) != null;
	}

	public String getAttribute(String name) throws ConfigurationException {
		String ret = UtilDom.getAttributeValueAsString(conf, name);
		if (ret != null)
			return ret;
		else
			throw new ConfigurationException("Attribute not found:" + name + " for node:" + conf);
	}

	public String getAttribute(String name, String def) {
		try {
			return getAttribute(name);
		} catch (ConfigurationException ex) {
			return def;
		}

	}

	public int getAttributeAsInteger(String name) throws ConfigurationException {
		try {
			return Integer.parseInt(getAttribute(name));
		} catch (NumberFormatException ex) {
			throw new ConfigurationException("Attribute is not integer: '" + name + '\'', ex);
		}
	}

	public int getAttributeAsInteger(String name, int def) {
		try {
			return getAttributeAsInteger(name);
		} catch (Exception ex) {
			return def;
		}
	}

	public long getAttributeAsLong(String name) throws ConfigurationException {
		try {
			return Long.parseLong(getAttribute(name));
		} catch (NumberFormatException ex) {
			throw new ConfigurationException("Attribute is not long: '" + name + '\'', ex);
		}
	}

	public long getAttributeAsLong(String name, long def) {
		try {
			return getAttributeAsLong(name);
		} catch (Exception ex) {
			return def;
		}
	}

	public float getAttributeAsFloat(String name) throws ConfigurationException {
		try {
			return Float.parseFloat(getAttribute(name));
		} catch (NumberFormatException ex) {
			throw new ConfigurationException("Attribute is not float: '" + name + '\'', ex);
		}
	}

	public float getAttributeAsFloat(String name, float def) {
		try {
			return getAttributeAsFloat(name);
		} catch (Exception ex) {
			return def;
		}

	}

	/**
	 * Restituisce l'attributo <code>name</code> come <code>boolean</code>.
	 * 
	 * @param name nome dell'attributo
	 * @return valore dell'attributo
	 * @throws ConfigurationException se l'attributo non esiste
	 */
	public boolean getAttributeAsBoolean(String name) throws ConfigurationException {
		return Boolean.valueOf(getAttribute(name)).booleanValue();
	}

	public boolean getAttributeAsBoolean(String name, boolean def) {
		try {
			return getAttributeAsBoolean(name);
		} catch (ConfigurationException ex) {
			return def;
		}
	}

	/**
	 * Indica se il nodo della configurazione ha almeno un nodo con nome
	 * <code>name</code>.
	 * 
	 * @param name nome del nodo figlio
	 * @return <code>true</code> se ha almeno un nodo figlio con nome
	 *         <code>name</code>
	 */
	public boolean hasChild(String name) {
		return UtilDom.findDirectChild(conf, name) != null;
	}

	public Configuration getChild(String name) throws ConfigurationException {
		try {
			Node n = UtilDom.findDirectChild(conf, name);
			if (n != null)
				return new Configuration(n);
		} catch (Exception ex) {
		}
		throw new ConfigurationException("no child" + name);
	}

	public Configuration[] getChildren() {
		return getChildren(null);
	}

	/**
	 * Restituisce tutti i figli con nome <code>name</code>.
	 * 
	 * @param name nome del nodo
	 * @return tutti i figli con nome <code>name</code>
	 */
	public Configuration[] getChildren(String name) {
		if (conf == null)
			return new Configuration[0];
		NodeList nl = conf.getChildNodes();
		int len = nl.getLength();
		Vector v = new Vector();
		for (int i = 0; i < len; i++) {
			Node n = nl.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE)
				if (name == null || n.getNodeName().equals(name))
					v.addElement(new Configuration(n));
		}
		Configuration[] cs = new Configuration[v.size()];
		v.copyInto(cs);
		return cs;
	}
}
