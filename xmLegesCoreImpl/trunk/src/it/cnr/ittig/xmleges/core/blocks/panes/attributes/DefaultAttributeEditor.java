package it.cnr.ittig.xmleges.core.blocks.panes.attributes;

import it.cnr.ittig.xmleges.core.services.panes.attributes.AttributeEditor;

import java.awt.Component;

import javax.swing.JTextField;

import org.w3c.dom.Node;

/**
 * Editor di default per gli attributi.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class DefaultAttributeEditor implements AttributeEditor {

	JTextField textField = new JTextField();

	public boolean canEdit(Node node, Node attrib) {
		return true;
	}

	public boolean hasForm(Node node, Node attrib) {
		return false;
	}

	public Component getEditor(Node node, Node attrib) {
		textField.setText(attrib.getNodeValue());
		return textField;
	}

	public void showEditorForm() {
	}

	public String getValueChanged(Node node, Node attrib) {
		return textField.getText();
	}

}
