package it.cnr.ittig.xmleges.core.blocks.panes.attributes;

import it.cnr.ittig.xmleges.core.services.frame.FindIterator;

import java.util.Vector;

/**
 * Classe per effettuare la ricerca negli attributi del nodo visualizzato nel pannello
 * degli attributi.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class AttributeFindIterator implements FindIterator {

	AttributesPaneImpl attributesPaneImpl;

	String toFind;

	String toFindLower;

	boolean caseSensitive;

	Vector find = new Vector();

	int last = -1;

	public AttributeFindIterator(AttributesPaneImpl attributesPaneImpl) {
		this.attributesPaneImpl = attributesPaneImpl;
	}

	public void initFind(String text) {
		this.toFind = text;
		this.toFindLower = text;
		last = -1;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean next() {
		for (int i = last + 1; i < attributesPaneImpl.tableModel.getRowCount(); i++) {
			String v = attributesPaneImpl.tableModel.getValueAt(i, 1).toString();
			if ((caseSensitive && v.indexOf(toFind) != -1) || (!caseSensitive && v.toLowerCase().indexOf(toFindLower) != -1)) {
				attributesPaneImpl.table.getSelectionModel().setSelectionInterval(i, i);
				last = i;
				return true;
			}
		}
		return false;
	}

	public boolean canReplace(String text) {
		return last >= 0 && last < attributesPaneImpl.tableModel.getRowCount();
	}

	public void replace(String text) {
		attributesPaneImpl.tableModel.setValueAt(text, last, 1);
	}

}
