package it.cnr.ittig.xmleges.core.blocks.panes.attributes;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;

import javax.swing.table.AbstractTableModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Classe che permette di popolare la tabella con gli attributi di un nodo DOM.
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
public class AttributesTableModel extends AbstractTableModel {
	Logger logger;

	DocumentManager documentManager;

	DtdRulesManager rulesManager;

	I18n i18n;

	AttributeFilter filter;

	Node node;

	/**
	 * Costruttore di <code>AttributesTableModel</code>.
	 * 
	 * @param logger per i log
	 * @param documentManager per attivare le transazioni per la modifica degli attributi
	 * @param rulesManager per verificare la correttezza delle modifiche degli attributi
	 */
	public AttributesTableModel(Logger logger, DocumentManager documentManager, DtdRulesManager rulesManager, I18n i18n, AttributeFilter filter) {
		this.logger = logger;
		this.documentManager = documentManager;
		this.rulesManager = rulesManager;
		this.i18n = i18n;
		this.filter = filter;
	}

	/**
	 * Imposta il nodo di cui si intende visualizzare e/o modificare gli attributi
	 * 
	 * @param node nodo
	 */
	public void setNode(Node node) {
		this.node = node;
		fireTableDataChanged();
	}

	/**
	 * Aggiunge l'attributo <code>name</code> alla lista degli attributi.
	 * 
	 * @param name nome dell'attributo
	 */
	public void addAttribute(String name) {
		try {
			EditTransaction t = documentManager.beginEdit();
			node.getAttributes().setNamedItem(node.getOwnerDocument().createAttribute(name));
			documentManager.commitEdit(t);
			fireTableDataChanged();
		} catch (DocumentManagerException ex) {
			logger.error(ex.toString(), ex);

		}
	}

	/**
	 * Rimuove l'attributo alla riga <code>row</code>.
	 * 
	 * @param row riga dell'attributo
	 */
	public void delAttribute(int row) {
		try {
			EditTransaction t = documentManager.beginEdit();
			node.getAttributes().removeNamedItem(node.getAttributes().item(row).getNodeName());
			documentManager.commitEdit(t);
			fireTableDataChanged();
		} catch (DocumentManagerException ex) {
			logger.error(ex.toString(), ex);
		}

	}

	// //////////////////////////////////////////// AbstractTableModel Extends
	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		try {
			int c = 0;
			NamedNodeMap nnm = node.getAttributes();
			if (nnm != null)
				for (int i = 0; i < nnm.getLength(); i++) {
					String attrName = nnm.item(i).getNodeName();
					if (!filter.isHidden(attrName))
						c++;
				}
			return c;
		} catch (Exception ex) {
			return 0;
		}
	}

	public Object getValueAt(int row, int col) {
		try {
			NamedNodeMap nnm = node.getAttributes();
			Node attrib = nnm.item(row);
			return col == 0 ? attrib.getNodeName() : attrib.getNodeValue();
		} catch (Exception ex) {
			return "N/A";
		}
	}

	public void setValueAt(Object aValue, int row, int col) {
		if (col == 1) {
			EditTransaction t = null;
			try {
				t = documentManager.beginEdit();
				NamedNodeMap nnm = node.getAttributes();
				nnm.item(row).setNodeValue((String) aValue);
				documentManager.commitEdit(t);
			} catch (Exception e) {
				documentManager.rollbackEdit(t);
			}
		}
	}

	public boolean isCellEditable(int row, int col) {
		if (col == 0)
			return false;

		try {
			String attrName = node.getAttributes().item(row).getNodeName();
			return !rulesManager.queryIsFixedAttribute(node.getNodeName(), attrName) && !filter.isReadonly(attrName);
		} catch (Exception ex) {
			// logger.error(ex.getMessage(), ex);
		}
		return false;
	}

	public String getColumnName(int col) {
		String s = col == 0 ? "panes.attributes.column.name.text" : "panes.attributes.column.value.text";
		return i18n.getTextFor(s);
	}
}
