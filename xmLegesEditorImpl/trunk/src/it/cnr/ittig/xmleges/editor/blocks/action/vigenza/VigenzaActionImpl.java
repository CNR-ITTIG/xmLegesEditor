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
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.vigenza.VigenzaAction;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.MetaCiclodivita;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.Vigenza;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.form.vigenza.VigenzaForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Document;
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

public class VigenzaActionImpl implements VigenzaAction, Loggable, EventManagerListener, Serviceable, Initializable {
	
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	DocumentManager documentManager;
	
	UtilRulesManager utilRulesManager;

	SelectionManager selectionManager;

	Vigenza vigenza;
	
	MetaCiclodivita ciclodivita;

	VigenzaForm vigenzaForm;
	
	UtilMsg utilMsg;
	
	NirUtilUrn nirUtilUrn;

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
		ciclodivita = (MetaCiclodivita) serviceManager.lookup(MetaCiclodivita.class);
		vigenza = (Vigenza) serviceManager.lookup(Vigenza.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
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

			vigenzaAction.setEnabled(vigenza.canSetVigenza(activeNode));//||vigenza.canSetVigenzaSpan(activeNode, start, end));

		}
		if (event instanceof DocumentClosedEvent || event instanceof DocumentOpenedEvent)
			vigenzaAction.setEnabled(false);
	}

	public void doNewVigenza(Node active) {
		if(active!=null){
			VigenzaEntity vig = vigenza.getVigenza(active,start,end);
			Evento[] eventi_vig=new Evento[2];
			String testo_sel;
			// modifica di una vigenza esistente
			if(vig!=null){
				testo_sel=vigenza.getSelectedText();
				eventi_vig[0]=vig.getEInizioVigore();
				eventi_vig[1]=vig.getEFineVigore();
				vigenzaForm.setInizioVigore(eventi_vig[0]);
				vigenzaForm.setFineVigore(eventi_vig[1]);
				vigenzaForm.setStatus(vig.getStatus());
				vigenzaForm.setTestoselezionato(testo_sel);
			}else{   // inserimento di una nuova vigenza: su nodo o su testo
				eventi_vig=null;
				testo_sel=getMarkedText();
				vigenzaForm.setInizioVigore(null);
				vigenzaForm.setFineVigore(null);
				vigenzaForm.setStatus(null);
				vigenzaForm.setTestoselezionato(testo_sel);
			}
			
			
			//Node ciclodiVitaSaved=getCiclodiVitaNode()!=null?getCiclodiVitaNode().cloneNode(true):null;
			//ciclodiVitaSaved = UtilDom.setRecursiveIdAttribute(ciclodiVitaSaved);
			
			Evento[] oldEventi = ciclodivita.getEventi();
			Relazione[] oldRelazioni = ciclodivita.getRelazioni();
			
			if(vigenzaForm.openForm(active)){	
				Node toselect = vigenza.setVigenza(active,testo_sel, start,end, vigenzaForm.getVigenza());
				//vigenza.updateVigenzaOnDoc(vigenzaForm.getVigenza());
				if(toselect!=null)
					vigenza.setTipoDocVigenza();
				setModified(toselect);
			}
			else{//se preme annulla ripristina il vecchio ciclodivita
				ciclodivita.setEventi(oldEventi);
	   		    ciclodivita.setRelazioni(oldRelazioni);
			}
		}
	}
	
	private String getMarkedText(){
		String selectedText = "";
		if(activeNode.getNodeValue()==null){
			if(UtilDom.getTextNode(activeNode)==null || UtilDom.getTextNode(activeNode).trim().equals("")){
				selectedText=nirUtilUrn.getFormaTestualeById(UtilDom.getAttributeValueAsString(activeNode,"id"));
				if(selectedText.trim().length()==0)
					selectedText = activeNode.getNodeName();
			}
			else 
				selectedText=UtilDom.getTextNode(activeNode);
		}
		else{	
			if(start!=end)
				selectedText=activeNode.getNodeValue().substring(start,end);
			else
				selectedText="...";
		}
		return selectedText;
	}


	// /////////////////////////////////////////////// Azioni
	public class vigenzaAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			if(activeNode!=null)
				doNewVigenza(activeNode);
		}
	}
	
	protected void setModified(Node modified) {
		if (modified != null) {
			selectionManager.setActiveNode(this, modified);
			activeNode = modified;
			vigenzaAction.setEnabled(vigenza.canSetVigenza(activeNode));
			logger.debug(" set modified " + UtilDom.getPathName(modified));
		} else
			logger.debug(" modified null in set modified ");
	}




}
