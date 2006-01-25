package it.cnr.ittig.xmleges.core.blocks.bars;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.ActionRegisteredEvent;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.bars.StatusBar;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.bars.Bars</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * Nessuna.
 * <h1>I18n</h1>
 * Nessuna.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class BarsImpl implements Bars, EventManagerListener, Loggable, Configurable, Serviceable, Initializable, Startable {
	Configuration conf;

	Logger logger;

	UtilUI utilUI;

	ActionManager actionManager;

	EventManager eventManager;

	PreferenceManager preferenceManager;

	I18n i18n;

	JMenuBar menubar;

	ToolbarPanel toolbarPanel;

	StatusBarImpl statusbar;

	Hashtable actionToMenu = new Hashtable(50);

	Hashtable actionToToolbar = new Hashtable(50);

	Hashtable actionToConf = new Hashtable(50);

	Hashtable actionToCheck = new Hashtable();

	Properties prefs;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		this.conf = configuration;
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		prefs = preferenceManager.getPreferenceAsProperties(getClass().getName());
		toolbarPanel = new ToolbarPanel();
		menubar = new JMenuBar();
		statusbar = new StatusBarImpl(logger, i18n);
		buildToolbarPanel(conf.getChild("tools"), toolbarPanel);
		buildStatusBar();
		buildMenu(conf.getChild("menus"), menubar);
		eventManager.addListener(this, ActionRegisteredEvent.class);
	}

	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws java.lang.Exception {
	}

	public void stop() throws java.lang.Exception {
		// salvataggio stato delle toolbar
		Component[] cs = toolbarPanel.getComponents();
		for (int i = 0; i < cs.length; i++)
			if (cs[i] instanceof JToolBar)
				prefs.setProperty(cs[i].getName(), Boolean.toString(cs[i].isVisible()));
		for (Enumeration en = actionToCheck.keys(); en.hasMoreElements();) {
			Object name = en.nextElement();
			JCheckBoxMenuItem item = (JCheckBoxMenuItem) actionToCheck.get(name);
			prefs.setProperty("check." + name.toString(), Boolean.toString(item.isSelected()));
		}
		// salvataggio stato statusbar
		prefs.setProperty("view.statusbar", Boolean.toString(statusbar.getComponent().isVisible()));
		preferenceManager.setPreference(getClass().getName(), prefs);
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		ActionRegisteredEvent e = (ActionRegisteredEvent) event;
		String actionName = e.getActionName();
		Action action = e.getAction();
		Vector v = (Vector) actionToMenu.get(actionName);
		if (v != null) {
			for (Iterator i = v.iterator(); i.hasNext();) {
				JComponent item = (JComponent) i.next();
				if (item instanceof JMenuItem) {
					((JMenuItem) item).setAction(action);
					((JMenuItem) item).setToolTipText("");
				}
			}
		}
		JButton btn = (JButton) actionToToolbar.get(actionName);
		if (btn != null) {
			btn.setAction(action);
			// TODO in config
			btn.setMargin(new Insets(1, 3, 1, 3));
			btn.setText("");
		}
		logger.debug("action registered:" + e.toString());
	}

	// ////////////////////////////////////////////////////////// Bars Interface
	public JMenuBar getMenuBar() {
		return menubar;
	}

	public JPanel getToolBarPanel() {
		return toolbarPanel;
	}

	public StatusBar getStatusBar() {
		return statusbar;
	}

	public JPopupMenu getPopup(boolean activeOnly) {
		PopupMenu popup = new PopupMenu(activeOnly);
		try {
			buildMenu(conf.getChild("popup"), popup);
		} catch (ConfigurationException ex) {
		}
		return popup;
	}

	protected void buildStatusBar() {
		try {
			statusbar.create(conf.getChild("status"), actionManager);
		} catch (Exception ex) {
			logger.warn("No status bar defined");
		}
		String actionName = "view.statusbar";
		statusbar.getComponent().setVisible(checkPreference(actionName));
		ViewBarAction viewBarAction = new ViewBarAction(statusbar.getComponent());
		actionManager.registerAction("view.statusbar", viewBarAction);
	}

	protected void buildMenu(Configuration conf, JComponent comp) {
		if (conf != null && conf.hasAttribute("action"))
			actionToConf.put(conf.getAttribute("action", ""), conf);

		Configuration[] c = conf.getChildren();
		for (int i = 0; i < c.length; i++) {
			String name = c[i].getName();
			String actionName = c[i].getAttribute("action", null);
			String ref = c[i].getAttribute("ref", null);
			JComponent item = null;
			if (name.equals("menu")) {
				if (ref != null) {
					Configuration refConf = (Configuration) actionToConf.get(ref);
					item = utilUI.createMenu(ref);
					buildMenu(refConf, item);
				} else {
					item = utilUI.createMenu(actionName);
					buildMenu(c[i], item);
				}
				((JMenu) item).setDelay(0);
			} else if (name.equals("separator"))
				item = new JSeparator();
			else {
				Action action = getAction(actionName);
				if (name.equals("item"))
					item = new JMenuItem(action);
				else if (name.equals("check")) {
					item = new JCheckBoxMenuItem(action);
					((JCheckBoxMenuItem) item).setSelected(checkPreference("check." + actionName));
					actionToCheck.put(actionName, item);
				}
			}
			if (item != null) {
				if (actionName != null) {
					Vector v = (Vector) actionToMenu.get(actionName);
					if (v == null) {
						v = new Vector();
						actionToMenu.put(actionName, v);
					}
					v.addElement(item);
				}
				// if (actionName != null && !actionToMenu.contains(actionName)) {
				// actionToMenu.put(actionName, item);
				// }
				comp.add(item);
			}
		}
	}

	protected void buildToolbarPanel(Configuration conf, ToolbarPanel panel) {
		Configuration[] c = conf.getChildren();
		for (int i = 0; i < c.length; i++) {
			String name = c[i].getAttribute("name", "undeclared");
			String actionName = "view.toolbar." + name;
			JToolBar toolbar = buildToolbar(c[i]);
			toolbar.setName(actionName);
			panel.add(toolbar);
			toolbar.setVisible(checkPreference(actionName));
			BarsImpl.ViewBarAction viewAction = new BarsImpl.ViewBarAction(toolbar);
			actionManager.registerAction(actionName, viewAction);
			logger.debug("Toolbar builded: " + name);
		}
	}

	protected JToolBar buildToolbar(Configuration conf) {
		JToolBar toolbar = new JToolBar();
		toolbar.setRollover(conf.getAttributeAsBoolean("rollover", true));
		toolbar.setMargin(new Insets(0, 0, 0, 0));
		toolbar.setFloatable(conf.getAttributeAsBoolean("floatable", false));
		Configuration[] c = conf.getChildren();
		for (int i = 0; i < c.length; i++) {
			String name = c[i].getName();
			String actionName = c[i].getAttribute("action", null);
			Action action = getAction(actionName);
			if (name.equals("item")) {
				JButton btn = toolbar.add(action);
				actionToToolbar.put(actionName, btn);
			} else if (name.equals("separator"))
				toolbar.addSeparator();
		}
		return toolbar;
	}

	protected Action getAction(String actionName) {
		Action action = actionManager.getAction(actionName);
		if (action == null)
			action = new NullAction(actionName);
		return action;
	}

	protected boolean checkPreference(String name) {
		return (!prefs.containsKey(name) || prefs.getProperty(name).equalsIgnoreCase("true"));
	}

	/**
	 * Azione del menu per la visualizzazione di una toolbar.
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
	protected class ViewBarAction extends AbstractAction {
		Component comp;

		public ViewBarAction(Component comp) {
			this.comp = comp;
		}

		public void actionPerformed(ActionEvent e) {
			comp.setVisible(!comp.isVisible());
		}
	}

	/**
	 * Azione di default per le azione non ancora presenti nell'ActionManager
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
	protected class NullAction extends AbstractAction {
		public NullAction(String name) {
			putValue(Action.NAME, name);
			putValue(Action.SHORT_DESCRIPTION, name);
		}

		public void actionPerformed(ActionEvent e) {
			logger.warn("Null Action for:" + getValue(Action.NAME));
		}
	}

}
