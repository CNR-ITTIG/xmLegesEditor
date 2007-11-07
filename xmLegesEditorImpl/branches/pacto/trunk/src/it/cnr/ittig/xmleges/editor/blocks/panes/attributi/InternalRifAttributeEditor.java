package it.cnr.ittig.xmleges.editor.blocks.panes.attributi;

import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.form.FormClosedListener;
import it.cnr.ittig.xmleges.core.services.panes.attributes.AttributeEditor;
import it.cnr.ittig.xmleges.core.services.panes.attributes.AttributesPane;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.editor.services.form.rinvii.interni.RinviiInterniForm;

import java.awt.Component;

import javax.swing.JTextField;

import org.w3c.dom.Node;

public class InternalRifAttributeEditor implements AttributeEditor, FormClosedListener {

	RinviiInterniForm rinviiInterni;

	SelectionManager selectionManager;

	DocumentManager documentManager;

	JTextField textField = new JTextField();

	String valueChanged = "";

	AttributesPane attributesPane;

	Node callingNode = null;

	public InternalRifAttributeEditor(RinviiInterniForm rinviiInterni, DocumentManager documentManager, SelectionManager selectionManager,
			AttributesPane attributesPane) {
		this.rinviiInterni = rinviiInterni;
		this.documentManager = documentManager;
		this.selectionManager = selectionManager;
		this.attributesPane = attributesPane;
	}

	public void formClosed() {
		if (rinviiInterni.isOk()) {
			valueChanged = "#" + rinviiInterni.getId();
			textField.setText(valueChanged);
		}
		rinviiInterni.setAutoUpdate(true);
		selectionManager.setActiveNode(this, callingNode);
		attributesPane.setIgnoreInteractions(false);
	}

	public boolean canEdit(Node node, Node attrib) {
		callingNode = selectionManager.getActiveNode();
		valueChanged = attrib.getNodeValue();
		return "xlink:href".equals(attrib.getNodeName()) && attrib.getNodeValue().startsWith("#");
	}

	public Component getEditor(Node node, Node attrib) {
		textField.setEditable(false);
		try {
			textField.setText(attrib.getNodeValue());
		} catch (Exception ex) {
		}
		return textField;
	}

	public boolean hasForm(Node node, Node attrib) {
		return true;
	}

	public void showEditorForm() {
		attributesPane.setIgnoreInteractions(true);
		String id = valueChanged.replaceAll("#", "");
		Node nodeID = documentManager.getDocumentAsDom().getElementById(id);
		selectionManager.setActiveNode(this, nodeID);
		rinviiInterni.setCallingNode(nodeID);
		rinviiInterni.openForm(this, false);
	}

	public String getValueChanged(Node node, Node attrib) {
		try {
			return valueChanged;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "N/A";
		}
	}
}
