package it.cnr.ittig.xmleges.editor.blocks.dom.link;

import java.net.MalformedURLException;
import java.net.URL;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.link.Link;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;

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
 * @version 1.0
 * @author <a href="mailto:g.giardiello@gmail.com">Gerardo Giardiello</a>
 */

public class LinkImpl implements Link, Loggable, Serviceable {

	Logger logger;

	NirUtilDom nirUtilDom;

	NirUtilUrn nirutilurn;

	RulesManager rulesManager;

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
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		nirutilurn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
	}
			
	public String getText(Node node) {
		
		//se sono su un nodo h:a seleziono comunque tutto il testo
		if (node.getNodeType()==Node.TEXT_NODE) {
			if (node.getParentNode().getNodeName().equals("h:a"))
				return node.getNodeValue();
		}	
		else 	
			if (node.getNodeName().equals("h:a"))
				if (node.getFirstChild()!=null)
					return node.getFirstChild().getNodeValue();
				else 
					return "";
			
		if (node.getNodeType()==Node.TEXT_NODE) {
			if (selectionManager.getTextSelectionStart()!=-1)
				return selectionManager.getActiveNode().getNodeValue().substring(selectionManager.getTextSelectionStart(),selectionManager.getTextSelectionEnd());
			else
				return "";
		}
		else
			return ""; //Sono su un nodo vuoto
	}

	public String getUrl(Node node) {
		
		if (node.getNodeType()==Node.TEXT_NODE && node.getParentNode()!=null)
			return UtilDom.getAttributeValueAsString(node.getParentNode(),"xlink:href");
		else	
			return UtilDom.getAttributeValueAsString(node,"xlink:href");
	}	
	
	public Node setUrl(Node node, String url) {
		url = setProtocollo(url);
	    try {
	    	if (node.getNodeType()==Node.TEXT_NODE && node.getParentNode()!=null)
	    		UtilDom.setAttributeValue(node.getParentNode(), "xlink:href", url);
	    	else
	    		UtilDom.setAttributeValue(node, "xlink:href", url);
	    	return node;
	    } catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	private String setProtocollo(String url) {
		
		//il controllo � un p� troppo banale. Se l'url non � valida
		//aggiunge il protocollo http 
		try {
			new URL(url);
			return url;
		} catch (MalformedURLException e) {
			logger.debug("Aggiungo il protocollo http all'url " + url);
			return "http://"+url;
		}
	}
	
	public Node setText(Node node, String text) {
	    try {
	    	if (node.getNodeType()==Node.TEXT_NODE && node.getParentNode()!=null)
	    		node.getParentNode().setNodeValue(text);
	    	else
	    		node.setNodeValue(text);
	    	return node;
	    } catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}	    
	}

	public Node setType(Node node, String type) {
	    try {
	    	if (node.getNodeType()==Node.TEXT_NODE && node.getParentNode()!=null)
	    		UtilDom.setAttributeValue(node.getParentNode(), "xlink:type", type);
	    	else
	    		UtilDom.setAttributeValue(node, "xlink:type", type);
	    	return node;
	    } catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public String getType(Node node) {
		if (node.getNodeType()==Node.TEXT_NODE && node.getParentNode()!=null)
			return UtilDom.getAttributeValueAsString(node.getParentNode(),"xlink:type");
		else
			return UtilDom.getAttributeValueAsString(node,"xlink:type");
	}

	
	public Node insert(Node node, int start, int end, String testo, String url, String type) {

		url = setProtocollo(url);
		Element newLink = (Element) utilRulesManager.encloseTextInTag(testo, "h:a", "h");
		if (newLink != null) {
			newLink.setAttribute("xlink:href", url);
			newLink.setAttribute("xlink:type", type);
			if (utilRulesManager.insertNodeInText(node, start, end, newLink, true))
				return newLink;
		}
		return null;
	}

	public boolean canInsert(Node node) {
		
		//Non capisco perch� ma mi permette di inserire h:a nei rif (e non credo sia giusto)
		if (node != null && !isRif(node) && node.getParentNode() != null) {
			try {
				return (rulesManager.queryAppendable(node).contains("h:a") || 
						rulesManager.queryInsertableInside(node.getParentNode(), node).contains("h:a") || 
						//rulesManager.queryInsertableAfter(node.getParentNode(), node).contains("h:a") || 
						rulesManager.queryInsertableBefore(node.getParentNode(), node).contains("h:a"));
			} catch (RulesManagerException ex) {
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
