package it.cnr.ittig.xmleges.core.blocks.util.ui;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;

import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.util.ui.UtilUI</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * Nessuna.
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.i18n.I18n:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * Nessuno.
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
public class UtilUIImpl implements UtilUI, Loggable, Serviceable {
	Logger logger;

	I18n i18n;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	// //////////////////////////////////////////////////////// UtilUI Interface
	public JMenu createMenu(String name) {
		JMenu menu = new JMenu();
		applyI18n(name, menu);
		return menu;
	}

	public JMenuItem createMenuItem(String action) {
		JMenuItem mi = new JMenuItem();
		applyI18n(action, mi);
		mi.setActionCommand(action);
		return mi;
	}

	public JMenuItem createMenuItem(String action, ActionListener al) {
		JMenuItem mi = createMenuItem(action);
		mi.addActionListener(al);
		return mi;
	}

	public JCheckBoxMenuItem createCheckBoxMenuItem(String action) {
		JCheckBoxMenuItem mi = new JCheckBoxMenuItem();
		applyI18n(action, mi);
		mi.setActionCommand(action);
		return mi;
	}

	public JCheckBoxMenuItem createCheckBoxMenuItem(String action, ActionListener al) {
		JCheckBoxMenuItem mi = createCheckBoxMenuItem(action);
		mi.addActionListener(al);
		return mi;
	}

	public JButton createButton(String action) {
		String text = i18n.getTextFor(action);
		JButton b = new JButton(i18n.getTextFor(action));
		if (text.length() == 0)
			b.setMargin(new Insets(1, 1, 1, 1));
		b.setIcon(new ImageIcon(i18n.getImageFor(action)));
		b.setActionCommand(action);
		return b;
	}

	public JButton createButton(String action, ActionListener al) {
		JButton b = createButton(action);
		b.addActionListener(al);
		return b;
	}

	public JButton createButtonForToolbar(String action) {
		JButton b = createButton(action);
		if (b.getIcon().getIconWidth() > 0)
			b.setText("");
		b.setMargin(new Insets(0, 0, 0, 0));
		b.setRolloverEnabled(true);
		return b;
	}

	public JButton createButtonForToolbar(String action, ActionListener al) {
		JButton b = createButtonForToolbar(action);
		b.addActionListener(al);
		return b;
	}

	public JToggleButton createToggleButton(String action) {
		JToggleButton b = null;

		Icon icon = new ImageIcon(i18n.getImageFor(action));
		b = (icon.getIconWidth() <= 0) ? new JToggleButton(action) : new JToggleButton(icon);

		// b.setToolTipText(I18nManager.getTooltipFor(action));
		b.setActionCommand(action);
		b.setMargin(new Insets(0, 0, 0, 0));

		return b;
	}

	public JToggleButton createToggleButton(String action, ActionListener al) {
		JToggleButton b = createToggleButton(action);
		b.addActionListener(al);
		return b;
	}

	public JComponent applyI18n(JComponent comp) {
		String name = comp.getName();
		if (name == null)
			name = "";
		if (i18n.hasNotEmptyKey(name + ".tooltip"))
			comp.setToolTipText(i18n.getTextFor(name + ".tooltip"));
		if (comp instanceof JLabel)
			applyI18n(name, (JLabel) comp);
		else if (comp instanceof AbstractButton)
			applyI18n(name, (AbstractButton) comp);
		else if (comp instanceof JTabbedPane)
			applyI18n((JTabbedPane) comp);
		else if (comp instanceof JTextComponent)
			applyI18n(name, (JTextComponent) comp);
		return comp;
	}

	protected void applyI18n(String name, JLabel comp) {
		// logger.debug("apply i18n to " + name);
		if (i18n.hasNotEmptyKey(name + ".text"))
			comp.setText(i18n.getTextFor(name + ".text"));
		if (i18n.hasNotEmptyKey(name + ".icon"))
			comp.setIcon(i18n.getIconFor(name + ".icon"));
	}

	protected void applyI18n(String name, AbstractButton comp) {
		// logger.debug("apply i18n to " + name);
		if (i18n.hasKey(name + ".text"))
			comp.setText(i18n.getTextFor(name + ".text"));
		if (i18n.hasNotEmptyKey(name + ".icon"))
			comp.setIcon(i18n.getIconFor(name + ".icon"));
		if (i18n.hasNotEmptyKey(name + ".icon.selected"))
			comp.setSelectedIcon(i18n.getIconFor(name + ".icon.selected"));
		if (i18n.hasNotEmptyKey(name + ".mnemonic"))
			comp.setMnemonic(i18n.getTextFor(name + ".mnemonic").charAt(0));
	}

	protected void applyI18n(JTabbedPane tabbed) {
		// logger.debug("apply i18n to " + tabbed.getName());
		for (int i = 0; i < tabbed.getTabCount(); i++) {
			String name = tabbed.getTitleAt(i);
			if (name == null || name.length() == 0)
				name = tabbed.getComponentAt(i).getName();
			if (i18n.hasKey(name + ".text")) {
				tabbed.setTitleAt(i, i18n.getTextFor(name + ".text"));
				tabbed.setIconAt(i, i18n.getIconFor(name + ".icon"));
				tabbed.setToolTipTextAt(i, i18n.getTextFor(name + ".tooltip"));
			}
		}
	}

	protected void applyI18n(String name, JTextComponent comp) {
		// logger.debug("apply i18n to " + name);
		if (i18n.hasNotEmptyKey(name + ".text"))
			comp.setText(i18n.getTextFor(name + ".text"));
	}

	public Action applyI18n(String actionName, Action action) {
		// logger.debug("apply i18n to " + actionName);
		action.putValue(Action.ACTION_COMMAND_KEY, actionName);
		String i18nName;
		i18nName = actionName + '.' + "text";
		if (action.getValue(Action.NAME) == null)
			action.putValue(Action.NAME, i18n.getTextFor(i18nName));
		i18nName = actionName + '.' + "mnemonic";
		if (actionValueSettable(action, Action.MNEMONIC_KEY, i18nName))
			action.putValue(Action.MNEMONIC_KEY, new Integer(i18n.getTextFor(i18nName).charAt(0)));
		i18nName = actionName + '.' + "icon";
		if (actionValueSettable(action, Action.SMALL_ICON, i18nName))
			action.putValue(Action.SMALL_ICON, i18n.getIconFor(i18nName));
		i18nName = actionName + '.' + "accelerator";
		if (actionValueSettable(action, Action.ACCELERATOR_KEY, i18nName))
			action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(i18n.getTextFor(i18nName)));
		i18nName = actionName + '.' + "tooltip";
		if (actionValueSettable(action, Action.SHORT_DESCRIPTION, i18nName))
			action.putValue(Action.SHORT_DESCRIPTION, i18n.getTextFor(i18nName));
		return action;
	}

	protected boolean actionValueSettable(Action action, String actionType, String i18nName) {
		return action.getValue(actionType) == null && i18n.hasNotEmptyKey(i18nName);
	}

	public DefaultTreeModel createDefaultTreeModel(Node node) {
		return new DefaultTreeModel(createDefaultMutableTreeNode(node, true));
	}

	public DefaultMutableTreeNode createDefaultMutableTreeNode(Node node, boolean deep) {
		DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();
		treeNode.setUserObject(node);
		if (deep) {
			NodeList nl = node.getChildNodes();
			int len = nl.getLength();
			for (int i = 0; i < len; i++)
				treeNode.add(createDefaultMutableTreeNode(nl.item(i), deep));
		}
		return treeNode;
	}
}
