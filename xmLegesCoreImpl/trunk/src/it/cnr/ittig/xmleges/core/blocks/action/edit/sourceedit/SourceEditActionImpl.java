package it.cnr.ittig.xmleges.core.blocks.action.edit.sourceedit;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.sourceedit.SourceEditAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.sourcePanel.SourcePanelForm;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;


import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SourceEditActionImpl implements SourceEditAction, EventManagerListener, Loggable, Serviceable, Initializable {

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;
	
	DocumentManager documentManager;
	
	UtilRulesManager utilRulesManager;
		
	SourcePanelForm sourcePanelForm;
	
	EditXMLAction editXMLAction;
	
	UtilMsg utilMsg;
	

	
	public void service(ServiceManager serviceManager) throws ServiceException {
		sourcePanelForm = (SourcePanelForm) serviceManager.lookup(SourcePanelForm.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	
	public void initialize() throws Exception {
		
		editXMLAction = new EditXMLAction();
		editXMLAction.setEnabled(false);
		actionManager.registerAction("edit.editXML", editXMLAction);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);	
		
	}

	
	public void manageEvent(EventObject event) {
		
		if (event instanceof SelectionChangedEvent) {
			SelectionChangedEvent evt = (SelectionChangedEvent) event;
			if (evt.isActiveNodeChanged()) {
				Node n = selectionManager.getActiveNode();
				editXMLAction.setEnabled(true);
			}
		} else if (event instanceof DocumentOpenedEvent){
			editXMLAction.setEnabled(true);
		} else if  (event instanceof DocumentClosedEvent) {
			editXMLAction.setEnabled(false);
		}	
	}
	

	public void enableLogging(Logger logger) {
		this.logger = logger;
		
	}
	
	
	private Node getRootElement(Document doc) {
		NodeList nl = doc.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++)
			if (nl.item(i).getNodeType() == Node.ELEMENT_NODE)
				return nl.item(i);
		return null;
	}
	
	
	
	protected class EditXMLAction extends AbstractAction {

		public void actionPerformed(ActionEvent arg0) {
			doEditXML();	
		}

//		private void doEditXML2() {
//			if (!documentManager.isEmpty()){
//
//				String text = UtilDom.domToString(documentManager.getDocumentAsDom(), true, "    ", true, false);//UtilDom.domToString(documentManager.getDocumentAsDom(), true, "    ");
//				text = text.replaceAll("\r", "");
//				sourcePanelForm.setSourceText(text);
//				
//				if(sourcePanelForm.openForm()){
//					//in temp c'è quello nuovo
//					UtilFile.copyFileInTemp(new ByteArrayInputStream(sourcePanelForm.getSourceText().getBytes()), "temp.xml");
//					documentManager.getDocFromText(sourcePanelForm.getSourceText());
//					if(documentManager.hasErrors()){
//						if(utilMsg.msgWarning("Documento non valido. Sovrascrivere comunque?")){
//							documentManager.openSource(UtilFile.getFileFromTemp("temp.xml").getPath(),true);
//							return;
//						}
//						else
//							return;
//					}
//					documentManager.openSource(UtilFile.getFileFromTemp("temp.xml").getPath(),true);	
//
//				}
//			}
//		}
		private void doEditXML() {
			
			if (!documentManager.isEmpty()){
			
				Node oldNode = selectionManager.getActiveNode();
				Document doc = documentManager.getDocumentAsDom();
				Node currentNode = ( ((oldNode==null) || (oldNode.equals(getRootElement(doc))))? doc: oldNode);
				String text = UtilDom.domToString(currentNode, true, "    ", true, false);
				text = text.replaceAll("\r", "");
				sourcePanelForm.setSourceText(text);
							
				if(sourcePanelForm.openForm()){
					if(currentNode.equals(doc)){  //  SONO SULLA ROOT						
						UtilFile.copyFileInTemp(new ByteArrayInputStream(sourcePanelForm.getSourceText().getBytes()), "temp.xml");
						
						documentManager.getDocFromText(sourcePanelForm.getSourceText());
						if(documentManager.hasErrors()){
							if(utilMsg.msgWarning("Documento non valido. Sovrascrivere comunque?")){
								documentManager.openSource(UtilFile.getFileFromTemp("temp.xml").getPath(),true);
								return;
							}
							else
								return;
						}
						documentManager.openSource(UtilFile.getFileFromTemp("temp.xml").getPath(),true);
					}
					else{ //sono su un nodo generico
						String nameSpaceDecl = utilRulesManager.getNameSpaceDecl();
						String toParse = "<?xml version=\"1.0\" encoding=\""+documentManager.getEncoding()+"\"?>"+ 
						"<ris "+nameSpaceDecl+">" + sourcePanelForm.getSourceText().trim() + "</ris>";
	
						Node newNode = UtilXml.textToXML(toParse, doc).getFirstChild();
						if(newNode==null){
							utilMsg.msgWarning("Documento non valido");
							return;
						}
						UtilDom.trimTextNode(newNode, true);
						UtilDom.replace(currentNode, newNode);
						//	UtilFile.copyFileInTemp(new ByteArrayInputStream(UtilDom.domToString(documentManager.getDocumentAsDom(), true, "    ", true, false).getBytes()), "temp.xml");
						documentManager.getDocFromText(UtilDom.domToString(documentManager.getDocumentAsDom(), true, "    ", true, false));
						if(documentManager.hasErrors()){
							if(utilMsg.msgWarning("Documento non valido. Sovrascrivere comunque?")){
								//	documentManager.openSource(UtilFile.getFileFromTemp("temp.xml").getPath(),true);
								return;
							}
							else{
								UtilDom.replace(newNode, oldNode);
								UtilFile.copyFileInTemp(new ByteArrayInputStream(UtilDom.domToString(documentManager.getDocumentAsDom(), true, "    ", true, false).getBytes()), "temp.xml");
								documentManager.openSource(UtilFile.getFileFromTemp("temp.xml").getPath(),true);
								return;
							}
						}
						//documentManager.openSource(UtilFile.getFileFromTemp("temp.xml").getPath(),true);
					}
				}
				
			}
			// problemi aperti:
//								se scarto il cambiamento non fa il refresh del pannello errori
//								il semaforo resta sempre verde anche con errori di validazione
//								non gestisce errori sintattici
			
		}
	

		

		
	}
	
	
}
