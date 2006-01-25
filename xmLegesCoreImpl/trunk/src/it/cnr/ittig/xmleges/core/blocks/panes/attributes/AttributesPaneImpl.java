package it.cnr.ittig.xmleges.core.blocks.panes.attributes;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.PaneStatusChangedEvent;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.panes.attributes.AttributeEditor;
import it.cnr.ittig.xmleges.core.services.panes.attributes.AttributesPane;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.clipboard.UtilClipboard;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.attributes.AttributesPane</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * La configurazione ha i tag opzionali:
 * <ul>
 * <li>readonly: regexp per rendere l'attributo di solo lettura;</li>
 * <li>hidden: regexp per rendere l'attributo non visibile.</li>
 * </ul>
 * i tag possono essere multipli; il primo match interrompe la sequenza di match. <br>
 * Esempio:
 * 
 * <pre>
 *       &lt;hidden&gt;id&lt;/hidden&gt;
 *       &lt;readonly&gt;xlink:.*&lt;/readonly&gt;
 * </pre>
 * 
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
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
 * @see
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class AttributesPaneImpl implements AttributesPane, EventManagerListener, ListSelectionListener, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	EventManager eventManager;

	DocumentManager documentManager;

	DtdRulesManager rulesManager;

	SelectionManager selectionManager;

	UtilUI utilUi;

	I18n i18n;

	String name;

	boolean ignoreInteractions;

	JPanel panel;

	JTable table;

	AttributesTableModel tableModel;

	AttributesCellEditor cellEditor;

	AttributeFilter attributeFilter;

	AddAttribute addAttribute = new AddAttribute();

	DelAttribute delAttribute = new DelAttribute();

	DefaultComboBoxModel comboModel = new DefaultComboBoxModel();

	JComboBox comboBox = new JComboBox(comboModel);

	AttributeFindIterator attributeFindIterator;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		attributeFilter = new AttributeFilter();
		Configuration[] cs = configuration.getChildren();
		for (int i = 0; i < cs.length; i++) {
			if ("readonly".equals(cs[i].getName())) {
				attributeFilter.addReadonly(cs[i].getValue());
				logger.debug("Add pattern " + cs[i].getName() + "=" + cs[i].getValue());
			} else if ("hidden".equals(cs[i].getName())) {
				attributeFilter.addHidden(cs[i].getValue());
				logger.debug("Add pattern " + cs[i].getName() + "=" + cs[i].getValue());
			}
		}
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		utilUi = (UtilUI) serviceManager.lookup(UtilUI.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		tableModel = new AttributesTableModel(logger, documentManager, rulesManager, i18n, attributeFilter);
		table = new JTable(tableModel);
		table.getSelectionModel().addListSelectionListener(this);
		cellEditor = new AttributesCellEditor(rulesManager);
		table.getColumn(table.getColumnName(1)).setCellEditor(cellEditor);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setShowVerticalLines(false);

		GridBagConstraints cons = new GridBagConstraints();
		JPanel btnPanel = new JPanel(new GridBagLayout());
		comboBox.setEnabled(false);
		cons.fill = GridBagConstraints.HORIZONTAL;
		cons.weightx = 1;
		btnPanel.add(comboBox, cons);
		cons.weightx = 0;
		JButton btn = new JButton(utilUi.applyI18n("panes.attributes.button.add", addAttribute));
		btn.setMargin(new Insets(0, 0, 0, 0));
		btnPanel.add(btn, cons);
		btn = new JButton(utilUi.applyI18n("panes.attributes.button.del", delAttribute));
		btn.setMargin(new Insets(0, 0, 0, 0));
		btnPanel.add(btn, cons);

		panel = new JPanel(new BorderLayout());
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(table);
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(btnPanel, BorderLayout.SOUTH);

		ignoreInteractions = false;

		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentChangedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);

		attributeFindIterator = new AttributeFindIterator(this);
	}

	// ///////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (!ignoreInteractions)
			reload();
	}

	protected void updateComboBox(Node node) {
		try {
			comboModel.removeAllElements();
			if (node != null) {
				Collection c = rulesManager.queryGetAttributes(node.getNodeName());
				Iterator it = c.iterator();
				while (it.hasNext()) {
					String toIns = it.next().toString();
					if (node.getAttributes().getNamedItem(toIns) == null && !attributeFilter.isHidden(toIns))
						comboModel.addElement(toIns);
				}
			}
			comboBox.setEnabled(comboModel.getSize() != 0);
			addAttribute.setEnabled(comboModel.getSize() != 0);
		} catch (Exception ex) {
		}
	}

	// ///////////////////////////////////////// ListSelectionListener Interface
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;
		int row = table.getSelectedRow();
		if (row != -1) {
			Node node = selectionManager.getActiveNode();
			String nodeName = node.getNodeName();
			String attrName = node.getAttributes().item(row).getNodeName();
			try {
				String def = rulesManager.queryGetAttributeDefaultValue(nodeName, attrName);
				delAttribute.setEnabled(def.length() == 0 && !rulesManager.queryIsRequiredAttribute(nodeName, attrName)
						&& !rulesManager.queryIsFixedAttribute(nodeName, attrName));
			} catch (DtdRulesManagerException ex) {
				delAttribute.setEnabled(false);
			}
		} else
			delAttribute.setEnabled(false);
		eventManager.fireEvent(new PaneStatusChangedEvent(this, this));
	}

	// //////////////////////////////////////////////// AttributesPane Interface

	public void setIgnoreInteractions(boolean ignore) {
		this.ignoreInteractions = ignore;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addEditor(AttributeEditor editor) {
		cellEditor.addEditor(editor);
	}

	public void removeEditor(AttributeEditor editor) {
		cellEditor.removeEditor(editor);
	}

	// ////////////////////////////////////////////////////////// Pane Interface
	public String getName() {
		return this.name;
	}

	public Component getPaneAsComponent() {
		return panel;
	}

	public boolean canCut() {
		return canCopy() && canDelete();
	}

	public void cut() {
		copy();
		delete();
	}

	public boolean canCopy() {
		return table.getSelectedRow() != -1;
	}

	public void copy() {
		UtilClipboard.set(tableModel.getValueAt(table.getSelectedRow(), 1).toString());
	}

	public boolean canPaste() {
		return UtilClipboard.hasString() && canPasteAsText();
	}

	public void paste() {
		tableModel.setValueAt(UtilClipboard.getAsString(), table.getSelectedRow(), 1);
	}

	public boolean canPasteAsText() {
		int row = table.getSelectedRow();
		return (UtilClipboard.hasString() || UtilClipboard.hasString()) && row != -1 && tableModel.isCellEditable(row, 1);
	}

	public void pasteAsText() {
		if (UtilClipboard.hasString())
			tableModel.setValueAt(UtilClipboard.getAsString(), table.getSelectedRow(), 1);
		else {
			tableModel.setValueAt(UtilDom.getRecursiveTextNode(UtilClipboard.getAsNode()), table.getSelectedRow(), 1);
		}
	}

	public boolean canDelete() {
		return delAttribute.isEnabled();
	}

	public void delete() {
		tableModel.delAttribute(table.getSelectedRow());
	}

	public boolean canPrint() {
		return tableModel.getRowCount() > 0;
	}

	public Component getComponentToPrint() {
		JTextPane toPrint = new JTextPane();
		toPrint.setContentType("text/html");
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<head><style type=\"text/css\">body { font-family: Arial; font-size: 11pt; }</style></head>");
		sb.append("<body>");
		sb.append("<b>Attribute of //");
		String s = null;
		for (Node node = selectionManager.getActiveNode(); node.getNodeType() != Node.DOCUMENT_NODE; node = node.getParentNode()) {
			Enumeration en = UtilDom.getChildElements(node.getParentNode()).elements();
			int index = 0;
			int n = 0;
			boolean found = false;
			while (en.hasMoreElements()) {
				Node curr = (Node) en.nextElement();
				if (curr.getNodeName().equals(node.getNodeName())) {
					n++;
					if (curr.equals(node))
						found = true;
					else {
						if (!found)
							index++;
					}
				}
			}
			s = node.getNodeName() + (n != 1 ? "[" + index + "]" : "") + (s != null ? "/" + s : "");
		}

		sb.append(s);
		sb.append("</b>");
		sb.append("<table border=\"1\" align=\"center\">");
		sb.append("<tr>");
		sb.append("<td><center><b>");
		sb.append(tableModel.getColumnName(0));
		sb.append("</b></center></td>");
		sb.append("<td><center><b>");
		sb.append(tableModel.getColumnName(1));
		sb.append("</b></center></td>");
		sb.append("</tr>");
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			sb.append("<tr>");
			sb.append("<td valign=\"top\" align=\"left\">");
			sb.append(tableModel.getValueAt(i, 0).toString());
			sb.append("</td>");
			sb.append("<td valign=\"top\" align=\"left\">");
			sb.append(tableModel.getValueAt(i, 1).toString());
			sb.append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");
		toPrint.setText(sb.toString());
		return toPrint;
	}

	public boolean canFind() {
		return tableModel.getRowCount() > 0;
	}

	public FindIterator getFindIterator() {
		return this.attributeFindIterator;
	}

	public void reload() {
		Node activeNode = selectionManager.getActiveNode();
		updateComboBox(activeNode);
		cellEditor.setNode(activeNode);
		tableModel.setNode(activeNode);
	}

	/**
	 * Azione su pressione del pulsante Add (+) per aggiungere l'attributo selezionato nel
	 * combobox.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>Lincense: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	public class AddAttribute extends AbstractAction {

		public AddAttribute() {
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				tableModel.addAttribute(comboModel.getSelectedItem().toString());
				reload();
			} catch (NullPointerException ex) {
			}
		}
	}

	/**
	 * Azione su pressione del pulsante Del (-) per rimuovere l'attributo selezionato
	 * nella tabella.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>Lincense: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	public class DelAttribute extends AbstractAction {

		public DelAttribute() {
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			tableModel.delAttribute(table.getSelectedRow());
			reload();
		}
	}
}
