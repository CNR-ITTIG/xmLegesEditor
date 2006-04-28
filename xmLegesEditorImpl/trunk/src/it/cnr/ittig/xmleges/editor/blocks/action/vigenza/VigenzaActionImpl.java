package it.cnr.ittig.xmleges.editor.blocks.action.vigenza;

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
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.editor.services.action.vigenza.VigenzaAction;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.Vigenza;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.vigenza.VigenzaForm;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

public class VigenzaActionImpl implements VigenzaAction, Loggable, EventManagerListener, Serviceable, Initializable {
	
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	DocumentManager documentManager;
	
	UtilRulesManager utilRulesManager;

	SelectionManager selectionManager;

	Vigenza vigenza;

	VigenzaForm vigenzaForm;
	
	UtilMsg utilMsg;

	AbstractAction vigenzaAction = new vigenzaAction();
	
	Node activeNode;

	int start, end;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		vigenza = (Vigenza) serviceManager.lookup(Vigenza.class);
		vigenzaForm = (VigenzaForm) serviceManager.lookup(VigenzaForm.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("editor.partizioni.vigenza", vigenzaAction);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		vigenzaAction.setEnabled(false);
	}

	public void manageEvent(EventObject event) {
		
		if (event instanceof SelectionChangedEvent && !documentManager.isEmpty() && !utilRulesManager.isDtdBase()) {
			activeNode = ((SelectionChangedEvent) event).getActiveNode();
			logger.debug("enable vigenza");
			start = ((SelectionChangedEvent) event).getTextSelectionStart();
			end = ((SelectionChangedEvent) event).getTextSelectionEnd();

			vigenzaAction.setEnabled(vigenza.canSetVigenza(activeNode, start, end));//||vigenza.canSetVigenzaSpan(activeNode, start, end));

		}
		if (event instanceof DocumentClosedEvent || event instanceof DocumentOpenedEvent)
			vigenzaAction.setEnabled(false);
	}

	public void doNewVigenza(Node active) {
		
		if(active!=null){

			VigenzaEntity vig = vigenza.getVigenza(active,start,end);
			Evento[] eventi_vig=new Evento[2];
			if(vig!=null){
				eventi_vig[0]=vig.getEInizioVigore();
				eventi_vig[1]=vig.getEFineVigore();
				vigenzaForm.setInizioVigore(eventi_vig[0]);
				vigenzaForm.setFineVigore(eventi_vig[1]);
				vigenzaForm.setStatus(vig.getStatus());
				
				
			}else{
				eventi_vig=null;
				vigenzaForm.setInizioVigore(null);
				vigenzaForm.setFineVigore(null);
				vigenzaForm.setStatus(null);				
			}
			vigenzaForm.setTestoselezionato(vigenza.getSelectedText());
	
			if(vigenzaForm.openForm(active)){
				
				vigenza.setVigenza(active,start,end, vigenzaForm.getVigenza());
								
			}
		}
		
	}

	// /////////////////////////////////////////////// Azioni
	public class vigenzaAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			if(activeNode!=null)
				doNewVigenza(activeNode);
		}
	}

}
