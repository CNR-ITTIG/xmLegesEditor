package it.cnr.ittig.xmleges.editor.blocks.panes.attributi;

import it.cnr.ittig.xmleges.core.services.panes.attributes.AttributeEditor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.AbstractAction;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

import org.w3c.dom.Node;

/**
 */
public class DataAttributeEditor implements AttributeEditor {

	JFormattedTextField textField;

	SimpleDateFormat sdfEdit = new SimpleDateFormat("dd/MM/yyyy");

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 
	 */
	public DataAttributeEditor() {
		try {
			MaskFormatter mf = new MaskFormatter("##/##/####");
			mf.setPlaceholderCharacter('_');
			textField = new JFormattedTextField(mf);
			textField.setColumns(10);
			textField.setAction(new CommitAction());
		} catch (Exception ex) {
		}
	}

	public boolean canEdit(Node node, Node attrib) {
		if ((node.getNodeName().equals("dataeluogo") && attrib.getNodeName().equals("norm"))
				|| (node.getNodeName().equals("dataDoc") && attrib.getNodeName().equals("norm")))
			return true;
		return false;
	}

	public Component getEditor(Node node, Node attrib) {
		try {
			textField.setText(sdfEdit.format(sdf.parse(attrib.getNodeValue())));
		} catch (Exception ex) {
		}
		return textField;
	}

	public boolean hasForm(Node node, Node attrib) {
		return false;
	}

	public void showEditorForm() {
	}

	public String getValueChanged(Node node, Node attrib) {
		try {
			return sdf.format(sdfEdit.parse(textField.getText()));
		} catch (ParseException ex) {
			ex.printStackTrace();
			return "N/A";
		}
	}

	public class CommitAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			try {
				textField.commitEdit();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
