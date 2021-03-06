package it.cnr.ittig.xmleges.editor.blocks.dom.meta.descrittori;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittori;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Pubblicazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittori</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class MetaDescrittoriImpl implements MetaDescrittori, Loggable, Serviceable {

	Logger logger;

	NirUtilDom nirUtilDom;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	UtilMsg utilMsg;

	UtilRulesManager utilRulesManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
	}

	// ///////////////////////////////////////////////////// MetaDescrittori
	// Interface

	public Pubblicazione getPubblicazione() {
		Document doc = documentManager.getDocumentAsDom();
		String tipo = null;
		String num = null;
		String data = null;

		NodeList pubList = doc.getElementsByTagName("pubblicazione");
		if (pubList.getLength() > 0) {
			Node n = pubList.item(0);
			tipo = n.getAttributes().getNamedItem("tipo") != null ? n.getAttributes().getNamedItem("tipo").getNodeValue() : null;
			num = n.getAttributes().getNamedItem("num") != null ? n.getAttributes().getNamedItem("num").getNodeValue() : null;
			data = n.getAttributes().getNamedItem("norm") != null ? n.getAttributes().getNamedItem("norm").getNodeValue() : null;
		}
		return (new Pubblicazione("pubblicazione", tipo, num, data));
	}

	public void setPubblicazione(Pubblicazione pubblicazione) {

		Document doc = documentManager.getDocumentAsDom();
		Node descrittoriNode = doc.getElementsByTagName("descrittori").item(0);

		if (pubblicazione.getNum() != null || pubblicazione.getNorm() != null) {
			Node oldTag = doc.getElementsByTagName("pubblicazione").item(0);
			Element pubTag;
			pubTag = doc.createElement("pubblicazione");
			pubTag.setAttribute("tipo", pubblicazione.getTipo());
			pubTag.setAttribute("norm", pubblicazione.getNorm());
			if (pubblicazione.getNum() != null)
				pubTag.setAttribute("num", pubblicazione.getNum());
			descrittoriNode.replaceChild(pubTag, oldTag);
		}
	}

	public Pubblicazione[] getAltrePubblicazioni() {

		Document doc = documentManager.getDocumentAsDom();
		String tag, num, data, tipo;
		Vector altrePubblicazioniVect = new Vector();

		NodeList altrePubNode = doc.getElementsByTagName("altrepubblicazioni");
		if (altrePubNode.getLength() > 0) {
			NodeList altreList = altrePubNode.item(0).getChildNodes();
			for (int i = 0; i < altreList.getLength(); i++) {
				Node pubNode = altreList.item(i);
				if (pubNode.getNodeType() == Node.ELEMENT_NODE) {
					tag = pubNode.getNodeName();
					tipo = pubNode.getAttributes().getNamedItem("tipo") != null ? pubNode.getAttributes().getNamedItem("tipo").getNodeValue() : null;
					num = pubNode.getAttributes().getNamedItem("num") != null ? pubNode.getAttributes().getNamedItem("num").getNodeValue() : null;
					data = pubNode.getAttributes().getNamedItem("norm") != null ? pubNode.getAttributes().getNamedItem("norm").getNodeValue() : null;
					altrePubblicazioniVect.add(new Pubblicazione(tag, tipo, num, data));
				}
			}
		}
		Pubblicazione[] altrePubblicazioni = new Pubblicazione[altrePubblicazioniVect.size()];
		altrePubblicazioniVect.copyInto(altrePubblicazioni);

		return altrePubblicazioni;
	}

	public void setAltrePubblicazioni(Pubblicazione[] pubblicazioni) {
		Document doc = documentManager.getDocumentAsDom();
		Node descrittoriNode = doc.getElementsByTagName("descrittori").item(0);

		
		removeTagByName("altrepubblicazioni");
		if (pubblicazioni.length > 0) {
			Node altrepubNode = doc.createElement("altrepubblicazioni");
			for (int i = 0; i < pubblicazioni.length; i++) {
				Element altrapub = doc.createElement(pubblicazioni[i].getTag());
				altrapub.setAttribute("tipo", pubblicazioni[i].getTipo());
				altrapub.setAttribute("num", pubblicazioni[i].getNum());
				altrapub.setAttribute("norm", pubblicazioni[i].getNorm());
				altrepubNode.appendChild(altrapub);
			}

			NodeList oldTag = doc.getElementsByTagName("altrepubblicazioni");
			if (oldTag.getLength() > 0) // c'era gia' un nodo altrepubblicazioni
				descrittoriNode.replaceChild(altrepubNode, oldTag.item(0));
			else {
				Node child = descrittoriNode.getFirstChild();
				boolean inserted = false;
				do {
					try {
						if (dtdRulesManager.queryCanInsertBefore(descrittoriNode, child, altrepubNode)) {
							UtilDom.insertAfter(altrepubNode, child.getPreviousSibling());
							inserted = true;
						}
						child = child.getNextSibling();
					} catch (DtdRulesManagerException ex) {
						logger.error(ex.getMessage(), ex);
					}
				} while (!inserted && child != null);
				try {
					if (!inserted && dtdRulesManager.queryCanAppend(descrittoriNode, altrepubNode))
						descrittoriNode.appendChild(altrepubNode);
				} catch (DtdRulesManagerException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}
	}

	public String[] getAlias() {
		Document doc = documentManager.getDocumentAsDom();

		NodeList aliasList = doc.getElementsByTagName("alias");
		Vector aliasVect = new Vector();

		for (int i = 0; i < aliasList.getLength(); i++)
			aliasVect.add(UtilDom.getAttributeValueAsString(aliasList.item(i),"value"));

		String[] alias = new String[aliasVect.size()];
		aliasVect.copyInto(alias);
		return alias;
	}

	public void setAlias(String[] alias) {
		Document doc = documentManager.getDocumentAsDom();
		Node descrittoriNode = doc.getElementsByTagName("descrittori").item(0);

		removeTagByName("alias");
		for (int i = 0; i < alias.length; i++) {
			Element aliasTag;
			aliasTag = doc.createElement("alias");
			UtilDom.setAttributeValue(aliasTag,"value",alias[i]);
			Node child = descrittoriNode.getFirstChild();
			boolean inserted = false;
			do {
				try {
					if (dtdRulesManager.queryCanInsertBefore(descrittoriNode, child, aliasTag)) {
						UtilDom.insertAfter(aliasTag, child.getPreviousSibling());
						inserted = true;
					}
					child = child.getNextSibling();
				} catch (DtdRulesManagerException ex) {
					logger.error(ex.getMessage(), ex);
				}
			} while (!inserted && child != null);
			try {
				if (!inserted && dtdRulesManager.queryCanAppend(descrittoriNode, aliasTag))
					descrittoriNode.appendChild(aliasTag);
			} catch (DtdRulesManagerException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		
	}


	


	/**
	 * Rimuove i tag con un determinato nome
	 */
	private void removeTagByName(String nome) {
		Document doc = documentManager.getDocumentAsDom();
		NodeList list;
		int listLen;
		do {
			list = doc.getElementsByTagName(nome);
			listLen = list.getLength();
			if (listLen > 0) {
				Node currNode = list.item(0);
				currNode.getParentNode().removeChild(currNode);
			}
		} while (listLen > 0);
	}

}
