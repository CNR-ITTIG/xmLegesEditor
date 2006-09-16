package it.cnr.ittig.xmleges.editor.blocks.panes.attributi;

import it.cnr.ittig.xmleges.core.services.panes.attributes.AttributeEditor;
import it.cnr.ittig.xmleges.editor.services.form.urn.UrnForm;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.awt.Component;

import org.w3c.dom.Node;

public class UrnAttributeEditor implements AttributeEditor {

	UrnForm urnForm;

	public UrnAttributeEditor(UrnForm urnForm) {
		this.urnForm = urnForm;
		urnForm.setOpenFormVisible(false);
	}

	public boolean canEdit(Node node, Node attrib) {
		return ("xlink:href".equals(attrib.getNodeName()) && !attrib.getNodeValue().startsWith("#") && node.getNodeName().startsWith("rif"))||
		       ("value".equals(attrib.getNodeName()) && node.getNodeName().startsWith("urn")) ;
	}

	public Component getEditor(Node node, Node attrib) {
		try {
			urnForm.setUrn(new Urn(attrib.getNodeValue()));
		} catch (Exception ex) {

		}
		return urnForm.getAsComponent();
	}

	public boolean hasForm(Node node, Node attrib) {
		return true;
	}

	public void showEditorForm() {
		urnForm.openForm();
	}

	public String getValueChanged(Node node, Node attrib) {
		try {
			return urnForm.getUrn().toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return "N/A";
		}
	}
}
