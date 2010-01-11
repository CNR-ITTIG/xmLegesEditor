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
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

public class SourceEditActionImpl implements SourceEditAction, EventManagerListener, Loggable, Serviceable, Initializable {

	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	SelectionManager selectionManager;
	
	DocumentManager documentManager;
	
	SourcePanelForm sourcePanelForm;

	EditXMLAction editXMLAction;
	
	UtilMsg utilMsg;
	

	
	public void service(ServiceManager serviceManager) throws ServiceException {
		sourcePanelForm = (SourcePanelForm) serviceManager.lookup(SourcePanelForm.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
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
	
	
	protected class EditXMLAction extends AbstractAction {

		public void actionPerformed(ActionEvent arg0) {
			doEditXML();	
		}

		private void doEditXML() {
			if (!documentManager.isEmpty()){

				String text = UtilDom.domToString(documentManager.getDocumentAsDom(), true, "    ");
				text = text.replaceAll("\r", "");
				sourcePanelForm.setSourceText(text);
				
				if(sourcePanelForm.openForm()){
/*					UtilFile.copyFileInTemp(new ByteArrayInputStream(sourcePanelForm.getSourceText().getBytes()), "temp.xml");
					documentManager.getDocFromText(sourcePanelForm.getSourceText());
					if(documentManager.hasErrors()){
						if(utilMsg.msgWarning("Documento non valido. Sovrascrivere comunque?")){
							documentManager.openSource(sourcePanelForm.getSourceText(),true);
							return;
						}
						else
							return;
					}
					documentManager.openSource(sourcePanelForm.getSourceText(),true);	
*/
				}
			}
		}
		
		
	}
	
	
}
