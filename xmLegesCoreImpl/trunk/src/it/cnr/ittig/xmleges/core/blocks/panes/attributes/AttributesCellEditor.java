package it.cnr.ittig.xmleges.core.blocks.panes.attributes;

import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.panes.attributes.AttributeEditor;

import java.awt.Component;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.w3c.dom.Node;

/**
 * Factory che restituisce un'instanza di <code>AttributeCellEditor</code> per
 * effettuare la modifica di una cella della tabella degli attributi.
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
public class AttributesCellEditor extends DefaultCellEditor {
	DtdRulesManager rulesManager;

	Vector editors = new Vector();

	AttributeCellEditor cellEditor;

	DefaultAttributeEditor defaultEditor;

	JComboBox comboBox;

	boolean onFixed = true;

	Node node;

	Node attrib;

	public AttributesCellEditor(DtdRulesManager rulesManager) {
		super(new JTextField());
		this.rulesManager = rulesManager;
		defaultEditor = new DefaultAttributeEditor();
		cellEditor = new AttributeCellEditor(this);
		comboBox = new JComboBox();
		comboBox.setEditable(false);
	}

	public void addEditor(AttributeEditor editor) {
		if (!editors.contains(editor))
			editors.add(editor);
	}

	public void removeEditor(AttributeEditor editor) {
		editors.remove(editor);
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
		String name = table.getModel().getValueAt(row, 0).toString();
		attrib = node.getAttributes().getNamedItem(name);
		try {
			if (col == 1) {
				Collection c = rulesManager.queryGetAttributePossibleValues(node.getNodeName(), attrib.getNodeName());
				if (c != null) {
					comboBox.setModel(new DefaultComboBoxModel(c.toArray()));
					onFixed = true;
					return comboBox;
				}
			}
		} catch (Exception ex) {
		}

		onFixed = false;
		Enumeration en = editors.elements();
		while (en.hasMoreElements())
			try {
				AttributeEditor editor = (AttributeEditor) en.nextElement();
				if (editor.canEdit(node, attrib)) {
					cellEditor.setEditor(editor, node, attrib);
					return cellEditor;
				}
			} catch (Exception ex) {
			}
		cellEditor.setEditor(defaultEditor, node, attrib);
		return cellEditor;
	}

	public Object getCellEditorValue() {
		String value;
		if (onFixed)
			value = comboBox.getSelectedItem().toString();
		else
			value = cellEditor.getValueChanged();
		String oldValue = attrib.getNodeValue();
		if ((oldValue != null && !oldValue.equals(value)) || oldValue == null && value.trim().length() > 0)
			attrib.setNodeValue(value);
		return value;
	}

}
