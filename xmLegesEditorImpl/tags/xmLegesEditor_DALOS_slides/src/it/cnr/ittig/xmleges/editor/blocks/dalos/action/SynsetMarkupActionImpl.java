package it.cnr.ittig.xmleges.editor.blocks.dalos.action;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dalos.action.SynsetMarkupAction;
import it.cnr.ittig.xmleges.editor.services.dalos.dom.SynsetMarkup;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.vigenza.VigenzaAction</code>.</h1>
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a> <a
 *         href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */

public class SynsetMarkupActionImpl implements SynsetMarkupAction, Loggable, EventManagerListener, Serviceable, Initializable {
	
	Logger logger;

//	ActionManager actionManager;

//	EventManager eventManager;

	DocumentManager documentManager;
	
	SelectionManager selectionManager;

	SynsetMarkup synsetMarkup;
	
	NirUtilDom nirUtilDom;
	
	
	// FIXME     ?????? null, null
	AbstractAction synsetMarkupAction = new synsetMarkupAction(null, null);
	
	Node activeNode;
	
	UtilMsg utilMsg;

	int start, end;
	
	

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
//		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
//		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		synsetMarkup = (SynsetMarkup) serviceManager.lookup(SynsetMarkup.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
//		actionManager.registerAction("editor.partizioni.vigenza", synsetMarkupAction);
//		eventManager.addListener(this, SelectionChangedEvent.class);
//		eventManager.addListener(this, DocumentOpenedEvent.class);
//		eventManager.addListener(this, DocumentClosedEvent.class);
//		synsetMarkupAction.setEnabled(false);
	}

	public void manageEvent(EventObject event) {
		
		if (event instanceof SelectionChangedEvent && !documentManager.isEmpty() && !nirUtilDom.isDtdBase()) {
			activeNode = ((SelectionChangedEvent) event).getActiveNode();
			logger.debug("enable vigenza");
			start = ((SelectionChangedEvent) event).getTextSelectionStart();
			end = ((SelectionChangedEvent) event).getTextSelectionEnd();
		
			synsetMarkupAction.setEnabled(synsetMarkup.canSetSynset(activeNode));//||vigenza.canSetVigenzaSpan(activeNode, start, end));

		}
		if (event instanceof DocumentClosedEvent || event instanceof DocumentOpenedEvent)
			synsetMarkupAction.setEnabled(false);
	}

	
	public void doSynsetMarkup(Synset synset, String variant) {
		activeNode = selectionManager.getActiveNode();
		int start = selectionManager.getTextSelectionStart();
		int end = selectionManager.getTextSelectionEnd();
	
        if(synsetMarkup.canSetSynset(activeNode)){
        	Node toselect = synsetMarkup.setSynset(activeNode, start, end, synset, variant);
        	setModified(toselect);
        }else{
        	utilMsg.msgError("editor.dalos.error.msg", "editor.dalos.error.markup");
        }
	}
	


	// /////////////////////////////////////////////// Azioni
	public class synsetMarkupAction extends AbstractAction {
		
		private Synset synset;
		private String variant;
		
		public synsetMarkupAction(Synset synset, String variant) {
			this.synset = synset;
			this.variant = variant;
		}

		public void actionPerformed(ActionEvent e) {
			doSynsetMarkup(synset,variant);
		}
	}
	
	
	protected void setModified(Node modified) {
		if (modified != null) {
			selectionManager.setActiveNode(this, modified);
			activeNode = modified;
			synsetMarkupAction.setEnabled(synsetMarkup.canSetSynset(activeNode));
			logger.debug(" set modified " + UtilDom.getPathName(modified));
		} else
			logger.debug(" modified null in set modified ");
	}




}
