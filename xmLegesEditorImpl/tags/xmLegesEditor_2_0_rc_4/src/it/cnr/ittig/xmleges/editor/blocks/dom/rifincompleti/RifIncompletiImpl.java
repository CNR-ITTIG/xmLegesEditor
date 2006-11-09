package it.cnr.ittig.xmleges.editor.blocks.dom.rifincompleti;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.rifincompleti.RifIncompleti;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.util.Vector;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;




/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.rinvii.Rinvii</code>.</h1>
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

public class RifIncompletiImpl implements RifIncompleti, Loggable, Serviceable {

	Logger logger;

	NirUtilDom nirUtilDom;

	NirUtilUrn nirutilurn;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	UtilMsg utilMsg;

	UtilRulesManager utilRulesManager;

	Rinumerazione rinumerazione;


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
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
	}
		

	public boolean canFix(Node node) {
		
		if(node.getNodeType()==Node.PROCESSING_INSTRUCTION_NODE && ((ProcessingInstruction)node).getTarget().equals("rif"))
		   return true;
		
		
		return false;
	}
	

	public Node setRif(Node node, String text, Urn urn) { 
	    
		Node parent = node.getParentNode();
		Document doc = documentManager.getDocumentAsDom();

		Node tmp = doc.createElement("rif");
		UtilDom.setAttributeValue(tmp, "xlink:href", urn.toString());
		UtilDom.setTextNode(tmp, text);
     	try{
	     		parent.replaceChild(tmp, node);	
		}
		catch(DOMException e){return null;}	
				
	    rinumerazione.aggiorna(doc);
		return node;
			
	}

	public Node setRif(Node node, String text, String rif){ 
	    
		Node parent = node.getParentNode();
		Document doc = documentManager.getDocumentAsDom();

		Node tmp = doc.createElement("rif");
		UtilDom.setAttributeValue(tmp, "xlink:href", rif);
		UtilDom.setTextNode(tmp, text);
     	try{
	     		parent.replaceChild(tmp, node);	
		}
		catch(DOMException e){return null;}	
				
	    rinumerazione.aggiorna(doc);
		return node;
			
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
		
        String temp = ((ProcessingInstruction)node).getData().substring(((ProcessingInstruction)node).getData().indexOf(">"),((ProcessingInstruction)node).getData().length());
		return (temp.substring(1,temp.indexOf("<")));
		
	}

	public String getUrn(Node node) {
		String temp = ((ProcessingInstruction)node).getData().substring(((ProcessingInstruction)node).getData().indexOf("\"")+1,((ProcessingInstruction)node).getData().length());
		return (temp.substring(0,temp.indexOf("\"")));
		
	}
	
public Node[] getList(){
		
		NodeIterator nI = ((DocumentTraversal)documentManager.getDocumentAsDom()).createNodeIterator(documentManager.getDocumentAsDom().getDocumentElement(),NodeFilter.SHOW_PROCESSING_INSTRUCTION,null,true);	
		Vector v = new Vector();
	  
		Node node;
		
		while ((node = nI.nextNode()) != null ) {
			if(node.getNodeType()==Node.PROCESSING_INSTRUCTION_NODE && ((ProcessingInstruction)node).getTarget().equals("rif"))
			v.add(node);
	    }
		
		Node[] ret = new Node[v.size()];
		for(int i=0; i<v.size();i++)
			ret[i]=(Node)v.get(i);
		return  ret;
	}

}
