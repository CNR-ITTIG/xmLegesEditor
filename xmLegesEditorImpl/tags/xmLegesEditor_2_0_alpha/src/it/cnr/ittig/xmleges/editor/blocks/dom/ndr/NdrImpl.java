package it.cnr.ittig.xmleges.editor.blocks.dom.ndr;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.ndr.Ndr;
import it.cnr.ittig.xmleges.editor.services.dom.ndr.Nota;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.ndr.Ndr</code>.</h1>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */

public class NdrImpl implements Ndr, Loggable, Serviceable {
	Logger logger;

	NirUtilDom nirUtilDom;

	RulesManager rulesManager;

	DocumentManager documentManager;

	UtilMsg utilMsg;

	UtilRulesManager utilRulesManager;

	Rinumerazione rinumerazione;

	Node modified = null;

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
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
	}

	public boolean canSetNdr(Node n) {

		if (n == null)
			return false;

		try {
			if (n.getParentNode() != null && UtilDom.findParentByName(n, "ndr") == null)
				return (rulesManager.queryAppendable(n).contains("ndr") || rulesManager.queryInsertableInside(n.getParentNode(), n).contains("ndr")
						|| rulesManager.queryInsertableAfter(n.getParentNode(), n).contains("ndr") || rulesManager.queryInsertableBefore(
						n.getParentNode(), n).contains("ndr"));
			return false;
		} catch (RulesManagerException ex) {
			return false;
		}
	}

	public Nota[] getNotesFromDocument() {
		Document doc = documentManager.getDocumentAsDom();
		NodeList noteNodes = doc.getElementsByTagName("nota");
		Vector noteVect = new Vector();
		Nota nota;
		for (int i = 0; i < noteNodes.getLength(); i++) {
			nota = new Nota(noteNodes.item(i));
			noteVect.addElement(nota);
		}
		Nota[] ret = new Nota[noteVect.size()];
		noteVect.copyInto(ret);
		return (ret);
	}

	public Node setNdr(Node node, int start, int end, String id, String testo) {

		Document doc = documentManager.getDocumentAsDom();
		boolean insertedTesta = true;
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (id == null) { // creazione di una nuova nota
				Node nota = createNota(testo);
				insertedTesta = insTesta(doc, nota);
			
				id = UtilDom.getAttributeValueAsString(nota, "id");
				logger.debug("insertedTesta " + insertedTesta + " nota nuova " + id);
			}
			if (inserisciNdr(doc, node, start, end, id) && insertedTesta) {
				rinumerazione.aggiorna(doc);
				documentManager.commitEdit(tr);
			} else
				documentManager.rollbackEdit(tr);
			return modified;
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	
	private boolean insTesta(Document doc, Node nota) {

		Node red = nirUtilDom.checkAndCreateMeta(doc,selectionManager.getActiveNode(),"redazionale"); 
		red.appendChild(nota);
		return true;
	}

	private boolean inserisciNdr(Document doc, Node node, int start, int end, String id) {

		String value = "";

		modified = null;

		// Preparazione elemento ndr
	
		Node ndr = UtilDom.createElement(doc,"ndr");
		UtilDom.setAttributeValue(ndr, "num", "#"+id);
		UtilDom.setAttributeValue(ndr, "valore", value);

		try {
			if (rulesManager.queryTextContent(ndr)) { 
				UtilDom.setTextNode((Node) ndr, "(" + value + ")");
				logger.debug(" (1) mette il testo nella nota ... ");
			} else
				logger.debug("non mette il testo");
		} catch (Exception ex) {
			logger.debug(ex.getMessage(), ex);
			return false;
		}

		if (utilRulesManager.insertNodeInText(node, start, end, ndr, false)) {
			modified = ndr;
			return true;
		}
		return false;
	}

	private Node createNota(String testo) {
		Document doc = documentManager.getDocumentAsDom();

		Node par = utilRulesManager.getNodeTemplate("h:p");
		UtilDom.setTextNode(par, testo);
		Node nota = UtilDom.createElement(doc,"nota");
		UtilDom.setIdAttribute(nota, "n" + getNotNum(doc));
		nota.appendChild(par);

		return nota;
	}

	private int getNotNum(Document doc) {
		NodeList nodelistNota = doc.getElementsByTagName("nota");
		return (nodelistNota.getLength() + 1);
	}

}
