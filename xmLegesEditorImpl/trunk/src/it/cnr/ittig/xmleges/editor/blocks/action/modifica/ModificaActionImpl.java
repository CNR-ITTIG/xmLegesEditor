package it.cnr.ittig.xmleges.editor.blocks.action.modifica;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.modifica.ModificaAction;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.meta.MetaAction</code>.</h1>
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ModificaActionImpl implements ModificaAction, EventManagerListener, Loggable, Serviceable, Initializable {

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	DocumentManager documentManager;

	AbstractAction modificaAction = new modificaAction();
	
	AbstractAction virgoletteAction = new virgoletteAction();
	
	AbstractAction mmodAction = new mmodAction();

	Rinumerazione rinumerazione;
	
	SelectionManager selectionManager;

	UtilRulesManager utilRulesManager;

	NirUtilDom nirUtilDom;
	
	RulesManager rulesManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		
		actionManager.registerAction("editor.meta.modifica", modificaAction);
		actionManager.registerAction("editor.meta.virgolette", virgoletteAction);
		actionManager.registerAction("editor.meta.mmod", mmodAction);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
		// aggiungere un listener su DocumentChanged ????
		eventManager.addListener(this, DocumentClosedEvent.class);
				
		modificaAction.setEnabled(false);
		virgoletteAction.setEnabled(false);
		mmodAction.setEnabled(false);
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		
		modificaAction.setEnabled(!documentManager.isEmpty() && canSetMod(selectionManager.getActiveNode()));
		virgoletteAction.setEnabled(!documentManager.isEmpty() && canSetVirgolette(selectionManager.getActiveNode()));
		mmodAction.setEnabled(!documentManager.isEmpty() && canSetMmod(selectionManager.getActiveNode()));
	}
	
	
	
	/////////////////////////////////////////
	//////////////////////////////  MODIFICA 
	
	public void doModifica() {
		Document doc = documentManager.getDocumentAsDom();
		Node node = selectionManager.getActiveNode();
		int start = selectionManager.getTextSelectionStart();
		int end = selectionManager.getTextSelectionEnd();
		
		try {
			EditTransaction tr = documentManager.beginEdit();
			//qua metto la modifica
			//Node parent = node.getParentNode();
			//Node child = "#PCDATA".equals("mod") ? parent.getOwnerDocument().createTextNode(xsltMapper.getI18nNodeText(parent)) : utilRulesManager.getNodeTemplate("mod");					
			Node child = utilRulesManager.getNodeTemplate("mod");
			
			utilRulesManager.insertNodeInText(node, start, end, child, true);
			rinumerazione.aggiorna(doc);
			documentManager.commitEdit(tr);
			setModified(child);
			
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
		}
		
		
	}
	
	protected void setModified(Node modified) {
		if (modified != null) {
			selectionManager.setActiveNode(this, modified);
			
			modificaAction.setEnabled(canSetMod(modified));
			logger.debug(" set modified " + UtilDom.getPathName(modified));
		} else
			logger.debug(" modified null in set modified ");
	}

	public boolean canSetMod(Node n) {

		if (n == null)
			return false;

		try {
			if (n.getParentNode() != null)
				return (rulesManager.queryAppendable(n).contains("mod") || rulesManager.queryInsertableInside(n.getParentNode(), n).contains("mod")
						|| rulesManager.queryInsertableAfter(n.getParentNode(), n).contains("mod") || rulesManager.queryInsertableBefore(
						n.getParentNode(), n).contains("mod"));
			return false;
		} catch (RulesManagerException ex) {
			return false;
		}
	}


	public boolean canSetVirgolette(Node n) {

		if (n == null)
			return false;

		try {
			if (n.getParentNode() != null)
				return (rulesManager.queryAppendable(n).contains("virgolette") || rulesManager.queryInsertableInside(n.getParentNode(), n).contains("virgolette")
						|| rulesManager.queryInsertableAfter(n.getParentNode(), n).contains("virgolette") || rulesManager.queryInsertableBefore(
						n.getParentNode(), n).contains("virgolette"));
			return false;
		} catch (RulesManagerException ex) {
			return false;
		}
	}
	
	public boolean canSetMmod(Node n) {

		if (n == null)
			return false;

		try {
			if (n.getParentNode() != null){
				return (rulesManager.queryAppendable(n).contains("mmod") || rulesManager.queryInsertableInside(n.getParentNode(), n).contains("mmod")
						|| rulesManager.queryInsertableAfter(n.getParentNode(), n).contains("mmod") || rulesManager.queryInsertableBefore(
						n.getParentNode(), n).contains("mmod"));
				
			}
			return false;
		} catch (RulesManagerException ex) {
			return false;
		}
	}

	
	public class modificaAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doModifica();
		}
	}

	public class virgoletteAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doVirgolette();
		}
	}
	
	public void doVirgolette() {
		Document doc = documentManager.getDocumentAsDom();
		Node node = selectionManager.getActiveNode();
		int start = selectionManager.getTextSelectionStart();
		int end = selectionManager.getTextSelectionEnd();
		
		try {
			EditTransaction tr = documentManager.beginEdit();
								
			Node child = utilRulesManager.getNodeTemplate("virgolette");
			
			utilRulesManager.insertNodeInText(node, start, end, child, true);
			rinumerazione.aggiorna(doc);
			documentManager.commitEdit(tr);
			setModified(child);
			
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
		}
		
	}

	public class mmodAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			doMmod();
		}
	}
	
	public void doMmod() {
		Document doc = documentManager.getDocumentAsDom();
		Node node = selectionManager.getActiveNode();
		int start = selectionManager.getTextSelectionStart();
		int end = selectionManager.getTextSelectionEnd();
		
		try {
			EditTransaction tr = documentManager.beginEdit();
								
			Node child = utilRulesManager.getNodeTemplate("mmod");
			
			utilRulesManager.insertNodeInText(node, start, end, child, true);
			rinumerazione.aggiorna(doc);
			documentManager.commitEdit(tr);
			setModified(child);
			
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
		}
		
	}

	
	
	
	

	

}
