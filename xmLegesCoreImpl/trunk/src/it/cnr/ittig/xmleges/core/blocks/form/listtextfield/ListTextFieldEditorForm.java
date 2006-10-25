package it.cnr.ittig.xmleges.core.blocks.form.listtextfield;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.xmleges.core.services.form.CommonForm;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.form.FormVerifier;
import it.cnr.ittig.xmleges.core.services.form.listtextfield.ListTextFieldEditor;

import java.awt.Component;
import java.awt.Dimension;

public class ListTextFieldEditorForm implements Service, CommonForm, FormVerifier {

	Form form;

	ListTextFieldEditor listEditor;

	public ListTextFieldEditorForm(ServiceManager serviceManager) {
		// FIXME togliere try catch ??
		try {
			form = (Form) serviceManager.lookup(Form.class);
			form.setMainComponent(getClass().getResourceAsStream("ListTextFieldEditor.jfrm"));
			form.setName("form.listtextfieldeditor");
			form.addFormVerifier(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getErrorMessage() {
		return listEditor.getErrorMessage();
	}

	public boolean verifyForm() {
		return listEditor.checkData();
	}

	public void setEditor(ListTextFieldEditor listEditor) {
		this.listEditor = listEditor;
		try {
			form.replaceComponent("form.listtextfieldeditor.editor", listEditor.getAsComponent());
		} catch (Exception ex) {
			// logger.error("error setting ListTextFieldEditor", ex);
		}
	}

	public ListTextFieldEditor getEditor() {
		return listEditor;
	}

	public Component getAsComponent() {
		return form.getAsComponent();
	}

	public boolean openForm(Component owner) {
		Dimension d = listEditor.getPreferredSize();
		if (d != null) {
			form.setSize(d.width, d.height);
		}		
		form.showDialog(owner);
		return form.isOk();
	}
}
