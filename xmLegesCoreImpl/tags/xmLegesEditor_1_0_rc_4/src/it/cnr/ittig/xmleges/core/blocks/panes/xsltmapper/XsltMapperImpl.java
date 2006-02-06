package it.cnr.ittig.xmleges.core.blocks.panes.xsltmapper;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.panes.xsltmapper.XsltMapper;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.util.EventObject;
import java.util.Hashtable;

import org.apache.xpath.NodeSet;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.xsltmapper.XsltMapper</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0 (per gli eventi di modifica e
 * chiusura del documento)</li>
 * <li>it.cnr.ittig.xmleges.editor.services.dtd.DtdRulesManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.i18n.I18n:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
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
 * @see it.cnr.ittig.xmleges.core.services.panes.xsltpane.XsltPane
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @author <a href="mailto:m.bruni@onetech.it">Matteo Bruni </a>
 */
public class XsltMapperImpl implements XsltMapper, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	/** Event Manager per gli eventi. */
	EventManager eventManager = null;

	/** Document manager per creare nodi appartenenti al documento aperto. */
	DocumentManager documentManager = null;

	/** Rules Manager per i nodi vuoti. */
	DtdRulesManager rulesManager = null;

	/** Internazionalizzazione */
	I18n i18n;

	/** Corrispondenza id leaf - dom */
	Hashtable id2dom = new Hashtable(3000);

	/** Corrispondenza dom - id leaf */
	Hashtable dom2id = new Hashtable(3000);

	/** Corrispondenza figlio - padre per i nodi testo generati */
	Hashtable gen2parent = new Hashtable(3000);

	/** Corrispondenza padre - figlio per i nodi testo generati */
	Hashtable parent2gen = new Hashtable(3000);

	/** Contatore per generare gli ID dei nodi XSLT */
	int idCounter = 0;

	/** Istanza della classe per l'utilizzo dall'xsl */
	static XsltMapperImpl instance = null;

	Hashtable i18nNodeName = new Hashtable(100);

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentClosedEvent.class);
		String[] tpls = new String[] { "xsltmapper-1.0.xsl" };
		for (int i = 0; i < tpls.length; i++)
			// FIXME if (!UtilFile.fileExistInTemp(tpls[i]))
			UtilFile.copyFileInTemp(getClass().getResourceAsStream(tpls[i]), tpls[i]);

		instance = this;
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public synchronized void manageEvent(EventObject event) {
		if (event instanceof DocumentClosedEvent) {
			clear();
		}
	}

	// //////////////////////////////////////////////////// XsltMapper Interface
	public void clear() {
		id2dom.clear();
		dom2id.clear();
		gen2parent.clear();
		parent2gen.clear();
		i18nNodeName.clear();
	}

	public Node getDomById(String id) {
		return getDomById(id, false);
	}

	public Node getParentByGen(Node gen) {
		try {
			return (Node) gen2parent.get(gen);
		} catch (Exception ex) {
			return null;
		}
	}

	public Node getGenByParent(Node parent) {
		try {
			return (Node) parent2gen.get(parent);
		} catch (Exception ex) {
			return null;
		}
	}

	public void removeGen2Parent(Node node) {
		synchronized (gen2parent) {
			Node parent = getParentByGen(node);
			if (parent != null) {
				gen2parent.remove(node);
				parent2gen.remove(parent);
			} else {
				Node genChild = getGenByParent(node);
				if (genChild != null) {
					gen2parent.remove(genChild);
					parent2gen.remove(node);
				}
			}
		}
	}

	public String getIdByDom(Node node) {
		try {
			return (String) dom2id.get(node);
		} catch (Exception ex) {
			return null;
		}
	}

	// /////////////////////////////////////////////// Xalan extension functions
	public static String getTextStringIfEmpty(Node node) {
		if (instance == null)
			return null;

		if (node == null)
			return null;

		return instance.getI18nNodeText(node);
	}

	public static NodeList getTextNodeIfEmpty(NodeList nodeList) {
		if (instance == null)
			return null;

		if (nodeList == null || nodeList.getLength() == 0)
			return null;

		Node node = nodeList.item(0);

		NodeSet retVal = new NodeSet();

		try {
			if (node != null && instance.rulesManager.queryTextContent(node)) {
				if (!node.hasChildNodes()) {
					Node newNode = instance.getGenByParent(node);
					if (newNode == null) {
						newNode = instance.documentManager.getDocumentAsDom().createTextNode(instance.getI18nNodeText(node));
						// newNode =
						// node.getOwnerDocument().createTextNode(instance.getI18nNodeText(node));
						instance.mapGen2Parent(newNode, node);
					}
					Node parentNode = newNode.getOwnerDocument().createElement(node.getNodeName());
					parentNode.appendChild(newNode);
					retVal.addNode(newNode);
					if (instance.logger.isDebugEnabled()) {
						instance.logger.debug("Node with empty text: " + node);
						instance.logger.debug("New empty text node: " + newNode.getNodeValue());
					}
				}
			}
		} catch (DtdRulesManagerException ex) {
			instance.logger.error(ex.toString(), ex);
			return null;
		}

		return retVal;
	}

	public String getI18nNodeText(Node node) {
		String nodeName = node.getNodeName();
		String ret = (String) i18nNodeName.get(nodeName);
		if (ret == null) {
			ret = "[" + i18n.getTextFor("dom." + node.getNodeName()) + "]";
			i18nNodeName.put(nodeName, ret);
		}
		return ret;
	}

	public void mapGen2Parent(Node newNode, Node parentNode) {
		synchronized (gen2parent) {
			gen2parent.put(newNode, parentNode);
			parent2gen.put(parentNode, newNode);
		}
	}

	public static synchronized String getUniqueId(NodeList nodeList) {
		if (instance == null)
			return null;

		if (nodeList == null || nodeList.getLength() == 0)
			return null;

		Node node = nodeList.item(0);

		String currId = "map";

		if (instance.dom2id.containsKey(node)) {
			currId = (String) instance.getIdByDom(node);
		} else {
			currId += Integer.toString(++instance.idCounter);

			instance.id2dom.put(currId, node);
			instance.dom2id.put(node, currId);
		}

		return currId;
	}

	public Node[] getDomById(String[] ids) {
		return getDomById(ids, false);
	}

	public Node getDomById(String id, boolean replaceGenNodes) {
		if (id == null)
			return null;

		Node retVal = (Node) id2dom.get(id);

		if (replaceGenNodes && getParentByGen(retVal) != null) {
			retVal = getParentByGen(retVal);
		}

		return retVal;
	}

	public Node[] getDomById(String[] ids, boolean replaceGenNodes) {
		if (ids == null)
			return null;

		Node[] retVal = new Node[ids.length];

		for (int i = 0; i < ids.length; i++) {
			retVal[i] = getDomById(ids[i], replaceGenNodes);
		}

		return retVal;
	}
}
