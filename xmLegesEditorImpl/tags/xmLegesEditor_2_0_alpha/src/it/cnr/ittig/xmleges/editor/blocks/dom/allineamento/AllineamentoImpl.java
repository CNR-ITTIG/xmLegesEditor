package it.cnr.ittig.xmleges.editor.blocks.dom.allineamento;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.allineamento.Allineamento;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.allineamento.Allineamento</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 */

public class AllineamentoImpl implements Allineamento, Loggable, Serviceable {
	Logger logger;

	Node tabella;

	RulesManager rulesManager;

	DocumentManager documentManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	// /////////////////////////////////////////////////////// Allineamento
	// Interface

	public boolean canAlignText(Node node) {
		try {
			if (node != null) {
				if (UtilDom.isTextNode(node)) {
					Node parent = node.getParentNode();
					if (rulesManager.queryIsValidAttribute(parent.getNodeName(), "h:style")
							|| rulesManager.queryIsValidAttribute(parent.getNodeName(), "style"))
						return true;
				} else if (rulesManager.queryIsValidAttribute(node.getNodeName(), "h:style")
						|| rulesManager.queryIsValidAttribute(node.getNodeName(), "style"))
					return true;
			}
		} catch (RulesManagerException ex) {
			logger.warn(ex.getMessage());
			return false;
		}
		return false;
	}

	public void alignText(Node pos, String allinea) {

		if (UtilDom.isTextNode(pos)) {
			Node parent = pos.getParentNode();
			try {
				if (rulesManager.queryIsValidAttribute(parent.getNodeName(), "h:style"))
					UtilDom.setAttributeValue(parent, "h:style", "text-align: " + allinea + ";");
				else if (rulesManager.queryIsValidAttribute(parent.getNodeName(), "style"))
					UtilDom.setAttributeValue(parent, "style", "text-align: " + allinea + ";");
			} catch (RulesManagerException ex) {
				logger.warn(ex.getMessage());
			}
		} else {
			try {
				if (rulesManager.queryIsValidAttribute(pos.getNodeName(), "h:style"))
					UtilDom.setAttributeValue(pos, "h:style", "text-align: " + allinea + ";");
				else if (rulesManager.queryIsValidAttribute(pos.getNodeName(), "style"))
					UtilDom.setAttributeValue(pos, "style", "text-align: " + allinea + ";");
			} catch (RulesManagerException ex) {
				logger.warn(ex.getMessage());
			}
		}

	}

}