package it.cnr.ittig.xmleges.core.blocks.dom.extracttext;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dom.extracttext.ExtractText;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.extracttext.ExtractText</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>document-manager</li>
 * </ul>
 * <h1>I18n</h1>
 * Nessuna
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
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */

public class ExtractTextImpl implements ExtractText, Loggable, Serviceable {
	Logger logger;

	DocumentManager documentManager;

	RulesManager rulesManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
	}

	public boolean canExtractText(Node node, int start, int end) {
		if (node == null || start == end || node.getNodeValue() == null || !isChildTextNode(node))
			return false;
		Node container = node.getParentNode(); // contenitore del testo
		if (start == 0 && end < node.getNodeValue().length()) { // all'inizio del testo
			if (null != container.getPreviousSibling() && UtilDom.isTextNode(container.getPreviousSibling()))
				return true;
			else {
				try {
					if (null != container.getParentNode() && rulesManager.queryInsertableBefore(container.getParentNode(), container).contains("#PCDATA"))
						return true;
				} catch (RulesManagerException ex) {
					return false;
				}
			}
			return false;
		} else if (end == node.getNodeValue().length()) { // alla fine del testo
			if (null != container.getNextSibling() && UtilDom.isTextNode(container.getNextSibling()))
				return true;
			else {
				try {
					if (null != container.getParentNode() && rulesManager.queryInsertableAfter(container.getParentNode(), container).contains("#PCDATA"))
						return true;
				} catch (RulesManagerException ex) {
					return false;
				}
			}
			return false;
		} else { // in mezzo al testo
			logger.debug("manca testo selezionato in mezzo");
			return false;
		}
	}

	public Node extractText(Node node, int start, int end) {
		if (!canExtractText(node, start, end))
			return null;
		Node ret = null;
		try {
			EditTransaction tr = documentManager.beginEdit();
			if((ret=extractTextDOM(node,start,end))!=null)
				documentManager.commitEdit(tr);
			else
				documentManager.rollbackEdit(tr);
			return ret;
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}	
	}
	
	public Node extractTextDOM(Node node, int start, int end) {
		
		if (!canExtractText(node, start, end))
			return null;
		
		Node container = node.getParentNode(); 					// contenitore del testo
		if (start == 0 && end < node.getNodeValue().length()) { // all'inizio del testo
			if (null != container.getPreviousSibling() && UtilDom.isTextNode(container.getPreviousSibling())) {
				container.getPreviousSibling().setNodeValue(container.getPreviousSibling().getNodeValue() + " " + node.getNodeValue().substring(start, end));
				node.setNodeValue(node.getNodeValue().substring(end));
				return container.getPreviousSibling();
			} else {
				if (null != container.getParentNode()) {
					Node newText = node.getOwnerDocument().createTextNode(node.getNodeValue().substring(start, end));
					node.setNodeValue(node.getNodeValue().substring(end));
					container.getParentNode().insertBefore(newText, container);
					return newText;
				}
			}
			return null;
		} else if (end == node.getNodeValue().length()) { // alla fine del testo
			if (null != container.getNextSibling() && UtilDom.isTextNode(container.getNextSibling())) {
				container.getNextSibling().setNodeValue(node.getNodeValue().substring(start, end) + " " + container.getNextSibling().getNodeValue());
				try {
					if (start == 0 && rulesManager.queryCanDelete(container, node))
						container.removeChild(node);
				} catch (RulesManagerException ex) {
				}
				if (start > 0)
					node.setNodeValue(node.getNodeValue().substring(0, start));
				return container.getNextSibling();
			} else {
				if (null != container.getParentNode()) {
					Node newText = node.getOwnerDocument().createTextNode(node.getNodeValue().substring(start, end));
					try {
						if (start == 0 && rulesManager.queryCanDelete(container, node))
							container.removeChild(node);
					} catch (RulesManagerException ex) {
					}
					if (start > 0)
						node.setNodeValue(node.getNodeValue().substring(0, start));
					UtilDom.insertAfter(newText, container);
					return newText;
				}
			}
			return null;
		} else { // in mezzo al testo
			logger.debug("manca testo selezionato in mezzo");
			return null;
		}
	}

	protected boolean isChildTextNode(Node node) {
		if (node == null || null == node.getParentNode())
			return false;
		try {
			// ?
			// if(!rulesManager.queryTextContainers(node.getParentNode(),node).contains(node.getParentNode().getNodeName()))
			if (!rulesManager.queryTextContent(node.getParentNode()))
				return false;
			return true;
		} catch (RulesManagerException ex) {
			return false;
		}
	}

}
