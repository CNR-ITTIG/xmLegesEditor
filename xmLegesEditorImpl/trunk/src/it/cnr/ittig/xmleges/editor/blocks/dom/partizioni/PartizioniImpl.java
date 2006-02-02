package it.cnr.ittig.xmleges.editor.blocks.dom.partizioni;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.editor.services.dom.partizioni.Partizioni;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.partizioni.Partizioni</code>.
 * </h1>
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
 * 
 * @see
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class PartizioniImpl implements Partizioni, Loggable, Serviceable, Initializable {
	Logger logger;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	NirUtilDom nirUtilDom;

	NodeInserter nodeInserter;

	UtilRulesManager utilRulesManager;

	Rinumerazione rinumerazione;

	UtilMsg utilMsg;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		nodeInserter = new NodeInserter(this);
	}

	// //////////////////////////////////////////////////// Partizioni Interface
	public Node nuovaPartizione(Node node, Node partizione) {
		return (nuovaPartizione(node, partizione, -1));
	}

	public Node nuovaPartizione(Node node, String elemName) {
		return (nuovaPartizione(node, elemName, -1));
	}

	public Node nuovaPartizione(Node node, Node partizione, int action) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (!nodeInserter.insertNewNode(partizione, node.getOwnerDocument(), node, dtdRulesManager, action)) {
				documentManager.rollbackEdit(tr);
				utilMsg.msgError("editor.partizioni.errore." + partizione.getNodeName());
				return node;
			} else {
				rinumerazione.aggiorna(node.getOwnerDocument());
				documentManager.commitEdit(tr);
				return (nodeInserter.getModificato());
			}
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public Node nuovaPartizione(Node node, String elemName, int action) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (!nodeInserter.insertNewNode(elemName, node.getOwnerDocument(), node, dtdRulesManager, action)) {
				documentManager.rollbackEdit(tr);
				utilMsg.msgError("editor.partizioni.errore." + elemName);
				return node;
			} else {
				rinumerazione.aggiorna(node.getOwnerDocument());
				documentManager.commitEdit(tr);
				return (nodeInserter.getModificato());
			}
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public int canInsertNuovaPartizione(Node node, String elemName) {
		return (canInsertNuovaPartizione(node, utilRulesManager.getNodeTemplate(node.getOwnerDocument(), elemName)));
	}

	public int canInsertNuovaPartizione(Node node, Node partizione) {
		return (nodeInserter.canInsertNewNode(partizione, node.getOwnerDocument(), node, dtdRulesManager));

	}

	public void aggregaInPartizione(String elemName, Node[] node) {
		// TODO Auto-generated method stub
	}

	protected Logger getLogger() {
		return this.logger;
	}

	protected NirUtilDom getNirUtilDom() {
		return this.nirUtilDom;
	}

	protected UtilRulesManager getUtilRulesManager() {
		return this.utilRulesManager;
	}
}
