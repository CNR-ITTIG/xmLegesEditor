package it.cnr.ittig.xmleges.editor.blocks.dom.link;

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
import it.cnr.ittig.xmleges.editor.services.dom.link.Link;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.link</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
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
 * @see
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */

public class LinkImpl implements Link, Loggable, Serviceable {

	Logger logger;

	NirUtilDom nirUtilDom;

	NirUtilUrn nirutilurn;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	UtilMsg utilMsg;

	UtilRulesManager utilRulesManager;
	
	SelectionManager selectionManager;


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
		nirutilurn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
	}
			
	public boolean canSetPlainText(Node node) {
		if (node == null) return false;
		if (node.getParentNode() == null) return false;
		return true;
	}
	
	public Node setPlainText(Node node, String plainText) {

		if (!canSetPlainText(node))
			return null;
		Node container = node.getParentNode(); // contenitore del testo
		Node ritorno = null;
 	    if (null != node.getPreviousSibling() && UtilDom.isTextNode(node.getPreviousSibling()) && null != node.getNextSibling() && UtilDom.isTextNode(node.getNextSibling())) {
			node.getPreviousSibling().setNodeValue(node.getPreviousSibling().getNodeValue() + " " + plainText + " " + node.getNextSibling().getNodeValue());
			ritorno = node.getPreviousSibling();
			container.removeChild(node.getNextSibling());
			container.removeChild(node);			
			return ritorno;
		} else { //Ho solo il fratello DX di tipo text
		  if (null != node.getNextSibling() && UtilDom.isTextNode(node.getNextSibling())) {
				node.getNextSibling().setNodeValue(plainText + " " + node.getNextSibling().getNodeValue());
				ritorno = node.getNextSibling();
				container.removeChild(node);			
				return ritorno;							
		  } else { //Ho solo il fratello SX di tipo text
			if (null != node.getPreviousSibling() && UtilDom.isTextNode(node.getPreviousSibling())) {
				node.getPreviousSibling().setNodeValue(node.getPreviousSibling().getNodeValue() + " " + plainText);
				ritorno = node.getPreviousSibling();
				container.removeChild(node);			
				return ritorno;							
			} else { //Non ho fratelli e/o ho fratelli non text
				     node.setNodeValue(plainText);
				     return node;
			  }
			}
		 }			
	}
	
	public String getText(Node node) {
		
		//se sono su un nodo h:a seleziono comunque tutto il testo
		if (node.getNodeType()==Node.TEXT_NODE) {
			if (node.getParentNode().getNodeName().equals("h:a"))
				return node.getNodeValue();
		}	
		else 	
			if (node.getNodeName().equals("h:a"))
				return node.getFirstChild().getNodeValue();
			
		
		if (node.getNodeType()==Node.TEXT_NODE) {
			if (selectionManager.getTextSelectionStart()!=-1)
				return selectionManager.getActiveNode().getNodeValue().substring(selectionManager.getTextSelectionStart(),selectionManager.getTextSelectionEnd());
			else
				if (node==selectionManager.getActiveNode())
					return selectionManager.getActiveNode().getNodeValue(); 
				else	
					return "";
		}
		else {
			if (selectionManager.getActiveNode().getFirstChild()!=null)
				return selectionManager.getActiveNode().getFirstChild().getNodeValue();
			else
				return ""; //Sono su un nodo vuoto
		}
	}

	public String getUrl(Node node) {
		
		if (node.getNodeType()==Node.TEXT_NODE && node.getParentNode()!=null)
			return UtilDom.getAttributeValueAsString(node.getParentNode(),"xlink:href");
		else	
			return UtilDom.getAttributeValueAsString(node,"xlink:href");
	}	

	
	
	
	public Node setUrl(Node node, String url) {
	    try {
	    	UtilDom.setAttributeValue(node, "xlink:href", url);
	    	return node;
	    } catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public Node setText(Node node, String text) {
	    try {
	    	node.getFirstChild().setNodeValue(text);
	    	return node;
	    } catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}	    
	}

	public Node setType(Node node, String type) {
	    try {
	    	UtilDom.setAttributeValue(node, "xlink:type", type);
	    	return node;
	    } catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public String getType(Node node) {
		
		return UtilDom.getAttributeValueAsString(node,"xlink:type");
	}


	
	
	
	public Node insert(Node node, int start, int end, String testo, String url, String type) {

		Document doc = documentManager.getDocumentAsDom();
		
		if (node.getNodeType()==Node.TEXT_NODE && node.getParentNode()!=null)
			node = node.getParentNode();
		
		//gestire la selezione!?!?
		
		Element newLink = doc.createElement("h:a");
		UtilDom.setAttributeValue(newLink, "xlink:href", url);
		UtilDom.setAttributeValue(newLink, "xlink:type", type);
		newLink.setNodeValue(""+testo.trim());
		newLink.appendChild(doc.createTextNode(""+testo.trim()));
		
		if (utilRulesManager.insertNodeInText(node, start, end, newLink, true)) {
			return newLink;
		}
		return null;
	}

	public boolean canInsert(Node node) {
		
		//Non capisco perch� ma mi permette di inserire h:a nei rif (e non credo sia giusto)
		if (node != null && !isRif(node) && node.getParentNode() != null) {
			try {
				return (dtdRulesManager.queryAppendable(node).contains("h:a")
						|| dtdRulesManager.queryInsertableInside(node.getParentNode(), node).contains("h:a")
						|| dtdRulesManager.queryInsertableAfter(node.getParentNode(), node).contains("h:a") || dtdRulesManager.queryInsertableBefore(
						node.getParentNode(), node).contains("h:a"));
			} catch (DtdRulesManagerException ex) {
				return false;
			}
		}
		return false;
	}

	private boolean isRif(Node node) {
		
    	if (node.getNodeName().equals("#text"))
    	    node = node.getParentNode();
    		
    	if (node != null && node.getNodeName()!=null)
    		if (node.getNodeName().equals("rif"))  
    			return true;

	    return false;
	}
	
}
