package it.cnr.ittig.xmleges.editor.blocks.form.disposizioni.attive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.w3c.dom.Node;

public class VirgolettaFormImpl implements VirgolettaForm, Loggable, Serviceable, Initializable, EventManagerListener, ActionListener {
	
	Logger logger;
	EventManager eventManager;
	DocumentManager documentManager;
	SelectionManager selectionManager;
	NirUtilDom nirUtilDom;

	Form form;
	Node modNode;
	Node mmodNode;
	
	JLabel riferimentoEti;
	JTextField riferimento;
	JComboBox sceltaRiferimento;
	
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
		sceltaRiferimento = (JComboBox) form.getComponentByName("editor.disposizioni.attive.riferimento.scelta");
		sceltaRiferimento.addActionListener(this);
	}

	private void popolaControlli() {
		sceltaRiferimento.removeAllItems();
		sceltaRiferimento.addItem(" ");
		Node[] virgolette;
		if (mmodNode==null)
			virgolette = UtilDom.getElementsByTagName(documentManager.getDocumentAsDom(), modNode, "virgolette");
		else
			virgolette = UtilDom.getElementsByTagName(documentManager.getDocumentAsDom(), mmodNode, "virgolette");
		if (virgolette!=null) {
			for (int i = 0; i < virgolette.length; i++) {
				String valore = UtilDom.getText(virgolette[i]);
				String id = UtilDom.getAttributeValueAsString(virgolette[i], "id")+": ";
				if (valore.length()>44)
					sceltaRiferimento.addItem(id+valore.substring(0, 40) + " ...");
				else
					sceltaRiferimento.addItem(id+valore);
			}
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sceltaRiferimento) {
			String sel = (String) sceltaRiferimento.getSelectedItem();
			if (sel!=null)
				if (" ".equals(sel))
					riferimento.setText("");
				else 
					riferimento.setText(sel.substring(0, sel.indexOf(":")));
		}
			
	}
	
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void openForm(FormClosedListener listener, Node modNode) {
		
		this.modNode = modNode;
		if ("mmod".equals(modNode.getParentNode().getNodeName()))
			mmodNode = modNode.getParentNode();
		else
			mmodNode = null;
		updateContent();
		popolaControlli();
		form.setSize(300, 200);
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
			if (virgoletta!=null && 
					((modNode==UtilDom.findParentByName(selectionManager.getActiveNode(), "mod"))
						|| (mmodNode==UtilDom.findParentByName(selectionManager.getActiveNode(), "mmod")))) {
				riferimento.setText(UtilDom.getAttributeValueAsString(virgoletta,"id"));
				for (int i=0; i<sceltaRiferimento.getItemCount(); i++)
					if (((String) sceltaRiferimento.getItemAt(i)).startsWith(riferimento.getText()))
						sceltaRiferimento.setSelectedIndex(i);
				return;	
			}
		} catch (Exception e) {}
		sceltaRiferimento.setSelectedItem(" ");
		riferimento.setText("");
	}
	
	public void manageEvent(EventObject event) {
		if (form.isDialogVisible()) {
			if (event instanceof DocumentClosedEvent)			
				form.close();
			else
				updateContent();
		}
	}

}
