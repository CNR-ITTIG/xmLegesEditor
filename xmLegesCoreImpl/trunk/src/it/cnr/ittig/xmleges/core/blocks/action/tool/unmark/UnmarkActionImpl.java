package it.cnr.ittig.xmleges.core.blocks.action.tool.unmark;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.tool.unmark.UnmarkAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dom.extracttext.ExtractText;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.testo</code>.</h1>
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
 * Tecniche dell'informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class UnmarkActionImpl implements UnmarkAction, EventManagerListener, Loggable, Serviceable, Initializable {

	Logger logger;

	int onlyTag = 0;

	int tree = 0;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;
	
	DocumentManager documentManager;
	
	DtdRulesManager dtdRulesManager;
	
	ExtractText extractText;

	Node activeNode;
	
	String selectedText="";

	doUnmarkAction unmarkaction = new doUnmarkAction ();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		extractText = (ExtractText) serviceManager.lookup(ExtractText.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		
		actionManager.registerAction("edit.unmark", unmarkaction);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		enableAction();
	}

	public void enableAction() {
		if (activeNode == null) {
				unmarkaction.setEnabled(false);
		} else {
			logger.debug("START enableUnmark");
			unmarkaction.setEnabled(extractText.canExtractText(activeNode,0,selectedText.length()));
			logger.debug("END enableUnmark");
		}
	}


	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {

		if (event instanceof SelectionChangedEvent) {
			if (((SelectionChangedEvent) event).isActiveNodeChanged()) {
				logger.debug("selectionChangedEvent: " + ((SelectionChangedEvent) event).toString());
				activeNode = ((SelectionChangedEvent) event).getActiveNode();
				
				if (activeNode != null) {
					
					logger.debug("active node" + activeNode + " node type " + activeNode.getNodeType());
					
					//   CERCA  SELECTEDTEXT  ////////////////////////////////////////
					if(activeNode.getNodeValue()==null){
						if(UtilDom.getTextNode(activeNode)==null || UtilDom.getTextNode(activeNode).trim().equals(""))
							selectedText=activeNode.getNodeName();
						else
							selectedText=UtilDom.getTextNode(activeNode);
					}
					else				
						selectedText=activeNode.getNodeValue();
					//////////////////////////////////////////////////////////////////
					
					
					//  ENABLE ACTION  /////////////////////////////////////////////////
					if ((UtilDom.isTextNode(activeNode))) {     // AZIONE SU NODO TESTO
						logger.debug("azione sul nodo testo");
						enableAction();
					} else 
						unmarkaction.setEnabled(false);
					// /////////////////////////////////////////////////////////////////
					
				} else 
					unmarkaction.setEnabled(false);
			}
		} else if (event instanceof DocumentClosedEvent || event instanceof DocumentOpenedEvent) {
			unmarkaction.setEnabled(false);
		}
	}

	public void doUnmark() {	
		Node modificato = activeNode;
		//	appiattisce il testo				
		Node extractedNode;
		extractedNode = extractText.extractText(modificato,0,selectedText.length());
		try{
			EditTransaction tr = documentManager.beginEdit();
			if(extractedNode!=null && extractedNode.getPreviousSibling()!=null){
				
				try{
					if(extractedNode.getPreviousSibling().getChildNodes().getLength()==0  &&
					   dtdRulesManager.queryCanDelete(extractedNode.getParentNode(),extractedNode.getPreviousSibling()))
							extractedNode.getParentNode().removeChild(extractedNode.getPreviousSibling());
				}
				catch(DtdRulesManagerException ex){	
				}
				
				Node toSelect=extractedNode.getParentNode();
				UtilDom.mergeTextNodes(extractedNode.getParentNode());
				documentManager.commitEdit(tr);
				selectionManager.setActiveNode(this, toSelect);
				
			}else{
				documentManager.rollbackEdit(tr);
			}
			
		}catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return;
		
		}					
	}

	public class doUnmarkAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doUnmark();
		}
	}

	
	}
