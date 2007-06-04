package it.cnr.ittig.xmleges.editor.blocks.dom.immagini;

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
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.immagini.Immagini;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.immagini.Immagini</code>.</h1>
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
 */
public class ImmaginiImpl implements Immagini, Loggable, Serviceable {

	Logger logger;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;
	
	UtilRulesManager utilRulesManager;

	private Node modified = null;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
	}

	public Node insert(Node node, String src, int width, String tipoWidth, int height, String tipoHeight, String alt) {

	//	Document doc = documentManager.getDocumentAsDom();

		try {
			EditTransaction tr = documentManager.beginEdit();
			if (insertDOM(node, src, width, tipoWidth, height, tipoHeight, alt)) {
				documentManager.commitEdit(tr);
				return modified;
			} else {
				documentManager.rollbackEdit(tr);
				return null;
			}
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public boolean canInsert(Node node) {
		if (node != null && node.getParentNode() != null) {
			try {
				return (node.getNodeName().equals("h:img")
						|| dtdRulesManager.queryAppendable(node).contains("h:img")
						|| dtdRulesManager.queryInsertableInside(node.getParentNode(), node).contains("h:img")
						|| dtdRulesManager.queryInsertableAfter(node.getParentNode(), node).contains("h:img") 
						|| dtdRulesManager.queryInsertableBefore(node.getParentNode(), node).contains("h:img"));			
			} catch (DtdRulesManagerException ex) {
				return false;
			}
		}
		return false;
	}

	private boolean insertDOM(Node node, String src, int width, String tipoWidth, int height, String tipoHeight, String alt) {
		Document doc = documentManager.getDocumentAsDom();

		Element New = doc.createElementNS(UtilDom.getNameSpaceURIforElement(doc.getDocumentElement(), "h"), "h:img");
		//Element New = doc.createElement("h:img");
		UtilDom.setAttributeValue(New, "src", src);
		if (width>=0)
			if (tipoWidth.equals("px"))
				UtilDom.setAttributeValue(New, "width", width+"px");
			else
				UtilDom.setAttributeValue(New, "width", width+"%");
		if (height>=0)
			if (tipoHeight.equals("px"))
				UtilDom.setAttributeValue(New, "height", height+"px");
			else
				UtilDom.setAttributeValue(New, "height", height+"%");
		if (!alt.equals(""))
			UtilDom.setAttributeValue(New, "alt", alt);		
		try {
			if (node.getNodeName().equals("h:img"))
				node.getParentNode().replaceChild(New, node);
			else	
				utilRulesManager.insertNodeInText(node, 0, 0, New, false);
			modified = New;
			return true;
		} catch (Exception ex) {
			return false;
		}	
	}

}
