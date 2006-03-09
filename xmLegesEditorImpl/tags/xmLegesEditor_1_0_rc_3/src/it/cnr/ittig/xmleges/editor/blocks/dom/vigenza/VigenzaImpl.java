package it.cnr.ittig.xmleges.editor.blocks.dom.vigenza;

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
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.Vigenza;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.vigenza.Vigenza</code>.</h1>
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
public class VigenzaImpl implements Vigenza, Loggable, Serviceable {
	Logger logger;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	private boolean isDocMultivigente(Document doc) {
		return (!(null == UtilDom.getAttributeValueAsString(doc.getDocumentElement(), "tipo")
				|| UtilDom.getAttributeValueAsString(doc.getDocumentElement(), "tipo").equalsIgnoreCase("vigente") || UtilDom.getAttributeValueAsString(
				doc.getDocumentElement(), "tipo").equalsIgnoreCase("originale")));
	}

	public boolean canSetVigenza(Node node, int start, int end) {
		Document doc = documentManager.getDocumentAsDom();

		if (isDocMultivigente(doc) && start != end) {
			try {
				return (null != node.getParentNode() && dtdRulesManager.queryInsertableInside(node.getParentNode(), node).contains("h:span"));
			} catch (DtdRulesManagerException ex) {
				return (false);
			}
		}
		return (false);
	}

	public boolean setVigenza(Node node, int start, int end, String inizio, String fine) {

		Document doc = documentManager.getDocumentAsDom();
		String text = node.getNodeValue().trim();

		// Creazione Nodo h:span con contenuto
		Element span = doc.createElementNS(UtilDom.getNameSpaceURIforElement(node, "h"), "h:span");
		span.appendChild(doc.createTextNode(text.substring(start, end)));

		// Assegnazione attributi di vigenza allo span creato
		span.setAttribute("inizio", inizio);
		span.setAttribute("fine", fine);

		Node padre = node.getParentNode();

		try {
			if (start == 0 && end == text.length()) {
				EditTransaction tr = documentManager.beginEdit();
				padre.replaceChild(span, node);
				documentManager.commitEdit(tr);
				return true;
			} else if (start == 0 && end < text.length()) {
				EditTransaction tr = documentManager.beginEdit();
				padre.insertBefore(span, node);
				padre.replaceChild(doc.createTextNode(text.substring(end)), node);
				documentManager.commitEdit(tr);
				return true;
			} else if (start > 0 && end < text.length()) {
				EditTransaction tr = documentManager.beginEdit();
				padre.insertBefore(doc.createTextNode(text.substring(0, start)), node);
				padre.insertBefore(span, node);
				padre.replaceChild(doc.createTextNode(text.substring(end)), node);
				documentManager.commitEdit(tr);
				return true;
			} else { // start > 0 && end = text.length
				EditTransaction tr = documentManager.beginEdit();
				padre.insertBefore(doc.createTextNode(text.substring(0, start)), node);
				padre.replaceChild(span, node);
				documentManager.commitEdit(tr);
				return true;
			}
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return false;
		}
	}

}
