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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

	private Node modified = null;

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

	//////////////  METODI VECCHI DEL DOM DEI RINVII
	
	public Node insert(Node node, int start, int end, Urn urn) {
		return insert(node, start, end, urn.toString(), urn.getFormaTestuale().trim());
	}

	public Node insert(Node node, int start, int end, String id, String text) {

		Document doc = documentManager.getDocumentAsDom();

		try {
			EditTransaction tr = documentManager.beginEdit();
			if (insertDOM(node, start, end, id, text)) {
				rinumerazione.aggiorna(doc);
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

	private boolean insertDOM(Node node, int start, int end, String id, String text){
		return false;
	}
	
	public boolean canInsert(Node node) {
		if (node != null && node.getParentNode() != null && UtilDom.findParentByName(node, "mrif") == null && UtilDom.findParentByName(node, "rif") == null) {
			try {
				return (dtdRulesManager.queryAppendable(node).contains("rif")
						|| dtdRulesManager.queryInsertableInside(node.getParentNode(), node).contains("rif")
						|| dtdRulesManager.queryInsertableAfter(node.getParentNode(), node).contains("rif") || dtdRulesManager.queryInsertableBefore(
						node.getParentNode(), node).contains("rif"));
			} catch (DtdRulesManagerException ex) {
				return false;
			}
		}
		return false;
	}

    //////////////////////////////////////////////////////

	public boolean canFix(Node node) {
		// TODO Auto-generated method stub
		return false;
	}
	

	public Node setRif(Node node, int start, int end, Urn urn) {
		// TODO Auto-generated method stub
		return null;
	}

	public Node setPlainText(Node node, int start, int end, String plainText) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUrn(Node node) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
