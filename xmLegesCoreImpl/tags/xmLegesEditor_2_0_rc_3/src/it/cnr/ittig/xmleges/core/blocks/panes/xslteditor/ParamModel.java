package it.cnr.ittig.xmleges.core.blocks.panes.xslteditor;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class ParamModel extends AbstractTableModel {

	Vector keys = new Vector();

	Vector values = new Vector();

	public void addParam() {
		keys.addElement("key");
		values.addElement("value");
		fireTableDataChanged();
	}

	/**
	 * Aggiunge un paramatro alla tabella.
	 * 
	 * @param key chiave
	 * @param value valore
	 */
	public void addParam(String key, String value) {
		keys.addElement(key);
		values.addElement(value);
		fireTableDataChanged();
	}

	/**
	 * Rimuove il parametro di indice <code>index</code>.
	 * 
	 * @param index indice del parametro da rimuovere
	 */
	public void removeParam(int index) {
		if (index < 0 || index >= keys.size())
			return;
		keys.remove(index);
		values.remove(index);
		fireTableRowsDeleted(index, index);
	}

	public Hashtable getParamsAsHashtable() {
		Hashtable hash = new Hashtable();
		for (int i = 0; i < keys.size(); i++)
			hash.put(keys.get(i), values.get(i));
		return hash;
	}

	// //////////////////////////////////////////// AbstractTableModel Extends
	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return keys.size();
	}

	public Object getValueAt(int row, int col) {
		return col == 0 ? keys.get(row) : values.get(row);
	}

	public void setValueAt(Object value, int row, int col) {
		Vector v = col == 0 ? keys : values;
		v.remove(row);
		v.insertElementAt(value, row);
	}

	public String getColumnName(int col) {
		return col == 0 ? "panes.xslteditor.param.name" : "panes.xslteditor.param.value";
	}
}
