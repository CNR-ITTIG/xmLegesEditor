package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;

import java.util.EventObject;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.form.disposizioni.attive.VirgolettaForm;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.partizioni.PartizioniForm;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.w3c.dom.Node;

public class VirgolettaFormImpl implements VirgolettaForm, Loggable, Serviceable, Initializable, EventManagerListener {
	
	Logger logger;
	EventManager eventManager;
	DocumentManager documentManager;
	SelectionManager selectionManager;
	NirUtilDom nirUtilDom;

	Form form;
	
	JLabel riferimentoEti;
	JTextField riferimento;
	
	public void service(ServiceManager serviceManager) throws ServiceException {
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);		
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		form = (Form) serviceManager.lookup(Form.class);
	}
	
	public void initialize() throws Exception {
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
		form.setMainComponent(this.getClass().getResourceAsStream("Virgoletta.jfrm"));
		form.setName("editor.form.disposizioni.virgoletta");
		form.setHelpKey("help.contents.form.disposizioniattive.virgoletta");
		riferimentoEti = (JLabel) form.getComponentByName("editor.disposizioni.attive.riferimento.eti");
		riferimento = (JTextField) form.getComponentByName("editor.disposizioni.attive.riferimento");
	}

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void openForm(FormClosedListener listener) {
		
		updateContent();
		form.setSize(300, 150);
		form.showDialog(listener);
	}
	
	public String getVirgoletta() {
		if (form.isOk())
			return riferimento.getText();
		return null;
	}
	
	private void updateContent() {
		
		try {
			Node virgoletta = UtilDom.findParentByName(selectionManager.getActiveNode(), "virgolette");
			if (virgoletta!=null) {
				riferimento.setText(UtilDom.getAttributeValueAsString(virgoletta,"id"));
				return;	
			}
		} catch (Exception e) {}			
		riferimento.setText("");
	}
	
	public void manageEvent(EventObject event) {
		if (form.isDialogVisible()) {
			if (event instanceof DocumentClosedEvent)			
				form.close();
			else
				updateContent();
		}
		
		
//		SelectionChangedEvent e = (SelectionChangedEvent) event;
//		activeNode = ((SelectionChangedEvent) event).getActiveNode();
//		if (activeNode!=null) {
//			start = ((SelectionChangedEvent) event).getTextSelectionStart();
//			end = ((SelectionChangedEvent) event).getTextSelectionEnd();
//		}
	}

}
