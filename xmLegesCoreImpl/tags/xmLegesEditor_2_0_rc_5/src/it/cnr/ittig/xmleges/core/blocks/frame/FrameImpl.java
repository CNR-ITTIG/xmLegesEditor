package it.cnr.ittig.xmleges.core.blocks.frame;

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
import it.cnr.ittig.xmleges.core.services.action.file.exit.FileExitAction;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.bars.StatusBar;
import it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentSavedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.Pane;
import it.cnr.ittig.xmleges.core.services.frame.PaneActivatedEvent;
import it.cnr.ittig.xmleges.core.services.frame.PaneDeactivatedEvent;
import it.cnr.ittig.xmleges.core.services.frame.PaneFocusGainedEvent;
import it.cnr.ittig.xmleges.core.services.frame.PaneFocusLostEvent;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.frame.Frame</code>.</h1>
 * <h1>Descrizione</h1>
 * Servizio per la gestione della finestra principale dell'applicazione. <br>
 * Ogni pannello di modifica che intende essere visualizzato deve implementare
 * l'interfaccia <code>Pane</code> e registrarsi sul frame attraverso il
 * metodo <code>addPane</code>. Le aree sul quale il pannello pu&ograve;
 * essere visualizzato sono espresse tramite i valori:
 * <ul>
 * <li><code>Frame.TOP_LEFT</code>;</li>
 * <li><code>Frame.TOP_CENTER</code>;</li>
 * <li><code>Frame.BOTTOM_LEFT</code>;</li>
 * <li><code>Frame.BOTTOM_CENTER</code>.</li>
 * </ul>
 * che rappresentano le seguenti posizioni:
 * 
 * <pre>
 *         
 *          +-------------+---------------+
 *          :  TOP_LEFT   :  TOP_CENTER   :
 *          +-------------+---------------+
 *          : BOTTOM_LEFT : BOTTOM_CENTER :
 *          +-------------+---------------+
 *          
 * </pre>
 * 
 * <br>
 * Il componente che implementa questo servizio deve preoccuparsi della gestione
 * dell'attivazione e disattivazione dei pannelli ed emettere l'opportuno evento (
 * <code>PaneFocusGainedEvent</code>,<code>PaneFocusLostEvent</code>,
 * <code>PaneActivatedEvent</code> e <code>PaneDeactivatedEvent</code>)
 * tramite il componente <code>EventManager</code>.
 * <h1>Configurazione</h1>
 * La configurazione pu&ograve; avere i seguenti tag:
 * <ul>
 * <li><code>&lt;title&gt;</code>: il titolo dell'applicazione;</li>
 * <li><code>&lt;panes&gt;</code>: pane dell'applicazione, contiene i tag:
 * <ul>
 * <li><code>&lt;pane&gt;</code>: descrizione del pane che specifica name, where e index del pane;</li>
 * </ul>
 * </li>
 * </ul>
 * Esempio: <br>
 * 
 * <pre>
 *     &lt;title&gt;xmLegesEditor&lt;/title&gt;
 *     &lt;panes&gt;
 *       &lt;pane name="editor.panes.strutturaxml"     where="top-left"    	index="0" / &gt;
 *       &lt;pane name="editor.panes.documento"     where="top-center"    	index="0" / &gt;
 *     &lt;/panes&gt;
 * </pre>
 * 
 * <h1>Dipendenze</h1>
 * <ul>		
 * <li>it.cnr.ittig.xmleges.editor.services.action.ActionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.preference.PreferenceManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.bars.Bars:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.form.filetextfield.FileTextField:1.0</li>
 * </ul>
 * <h1>i18n</h1>
 * <ul>
 * <li><code>frame.pane.maximize</code>: descrizione dell'azione come specificato nell'ActionManager; </li>
 * <li><code>frame.pane.restore</code>: descrizione dell'azione come specificato nell'ActionManager; </li>
 * <li><code>frame.pane.close</code>: descrizione dell'azione come specificato nell'ActionManager; </li>
 * <li><code>frame.pane.reload</code>: descrizione dell'azione come specificato nell'ActionManager; </li>
 * <li><code>view.pane.'paneName'</code>: descrizione dell'azione come specificato nell'ActionManager; </li>
 * </ul>
 * @see it.cnr.ittig.xmleges.core.services.action.ActionManager
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @see it.cnr.ittig.xmleges.core.services.preference.PreferenceManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>, Valentina Billi
 */
public class FrameImpl implements Frame, Loggable, Serviceable, Configurable, Initializable, Startable, EventManagerListener {
	final static boolean DOUBLE_BUFFER = true;

	Logger logger;

	ActionManager actionManager;

	FileExitAction fileExitAction;

	EventManager eventManager;

	PreferenceManager preferenceManager;

	DocumentManager documentManager;

	I18n i18n;

	Bars bars;

	StatusBar statusBar;

	Properties props;

	Configuration configuration;

	JFrame frame = new JFrame();

	PaneFrame activePaneFrame = null;

	Hashtable pos2Panes = new Hashtable();

	Hashtable name2PaneFrames = new Hashtable();

	AutoSplitPane splitMain;

	AutoSplitPane splitLeft;

	AutoSplitPane splitRight;

	AutoTabbedPane tLeftPane;

	AutoTabbedPane tCenterPane;

	AutoTabbedPane bLeftPane;

	AutoTabbedPane bCenterPane;

	String appName;

	String frameTitle;

	boolean alreadyChanged = false;

	MaximizeRestoreAction maximizeAction = new MaximizeRestoreAction();

	MaximizeRestoreAction restoreAction = new MaximizeRestoreAction();

	CloseAction closeAction = new CloseAction();

	ReloadAction reloadAction = new ReloadAction();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		fileExitAction = (FileExitAction) serviceManager.lookup(FileExitAction.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		pos2Panes.put(TOP_LEFT, new Vector());
		pos2Panes.put(TOP_CENTER, new Vector());
		pos2Panes.put(BOTTOM_CENTER, new Vector());
		pos2Panes.put(BOTTOM_LEFT, new Vector());

		try {
			Configuration[] c = configuration.getChild("panes").getChildren();
			for (int i = 0; i < c.length; i++) {
				String name = c[i].getAttribute("name");
				String pos = c[i].getAttribute("where", TOP_CENTER);
				int index = c[i].getAttributeAsInteger("index", 100);
				logger.debug("pane: name=" + name + " pos=" + pos + " index=" + index);
				Vector v = (Vector) pos2Panes.get(pos);
				if (v != null) {
					PaneFrame paneFrame = new PaneFrame(this, pos, index);
					v.addElement(paneFrame);
					name2PaneFrames.put(name, paneFrame);
				}
			}
			appName = configuration.getChild("title").getValue("App");
		} catch (ConfigurationException ex) {
			logger.error(ex.toString(), ex);
		}

	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("frame.pane.maximize", maximizeAction);
		actionManager.registerAction("frame.pane.restore", restoreAction);
		actionManager.registerAction("frame.pane.close", closeAction);
		actionManager.registerAction("frame.pane.reload", reloadAction);
		updateActions();

		props = preferenceManager.getPreferenceAsProperties(getClass().getName());
		logger.debug("preference=" + props.toString());
		String s = props.getProperty("size");
		if (s != null) {
			Dimension dim = new Dimension();
			StringTokenizer st = new StringTokenizer(s, ",");
			if (st.hasMoreTokens())
				dim.width = Integer.parseInt(st.nextToken());
			if (st.hasMoreTokens())
				dim.height = Integer.parseInt(st.nextToken());
			frame.setSize(dim);
		} else {
			Dimension dim = new Dimension();
			dim.width = 700;
			dim.height = 500;
			frame.setSize(dim);
		}

		frame.getContentPane().setLayout(new BorderLayout());
		statusBar = bars.getStatusBar();
		frame.getContentPane().add(statusBar.getComponent(), BorderLayout.SOUTH);
		frame.setJMenuBar(bars.getMenuBar());
		frame.getContentPane().add(bars.getToolBarPanel(), BorderLayout.NORTH);

		frame.setTitle(appName);

		splitMain = new AutoSplitPane(this, JSplitPane.HORIZONTAL_SPLIT);
		splitLeft = new AutoSplitPane(this, JSplitPane.VERTICAL_SPLIT);
		splitRight = new AutoSplitPane(this, JSplitPane.VERTICAL_SPLIT);
		tLeftPane = new AutoTabbedPane(this, "tab-top-left");
		tCenterPane = new AutoTabbedPane(this, "tab-top-center");
		bLeftPane = new AutoTabbedPane(this, "tab-bottom-left");
		bCenterPane = new AutoTabbedPane(this, "tab-bottom-center");

		tLeftPane.set((Vector) pos2Panes.get(TOP_LEFT));
		tCenterPane.set((Vector) pos2Panes.get(TOP_CENTER));
		bLeftPane.set((Vector) pos2Panes.get(BOTTOM_LEFT));
		bCenterPane.set((Vector) pos2Panes.get(BOTTOM_CENTER));
		splitLeft.add(tLeftPane, bLeftPane);
		splitRight.add(tCenterPane, bCenterPane);
		splitMain.add(splitLeft, splitRight);
		frame.getContentPane().add(splitMain, BorderLayout.CENTER);
		setDividerLocation(splitMain, props.getProperty("split.main"));
		setDividerLocation(splitLeft, props.getProperty("split.left"));
		setDividerLocation(splitRight, props.getProperty("split.right"));

		eventManager.addListener(this, DocumentChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentSavedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				fileExitAction.doExit();
			}
		});

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	protected void setDividerLocation(AutoSplitPane split, String location) {
		try {
			split.setDividerLocation(Integer.parseInt(location));
		} catch (Exception ex) {
			split.setDividerLocation(250);
		}
	}

	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws java.lang.Exception {
	}

	public void stop() throws java.lang.Exception {
		props.put("size", "" + frame.getSize().width + "," + frame.getSize().height);
		props.put("split.main", "" + splitMain.getDividerLocation());
		props.put("split.left", "" + splitLeft.getDividerLocation());
		props.put("split.right", "" + splitRight.getDividerLocation());
		for (Enumeration en = name2PaneFrames.keys(); en.hasMoreElements();) {
			Object name = en.nextElement();
			PaneFrame paneFrame = (PaneFrame) name2PaneFrames.get(name);
			props.put("show." + name.toString(), Boolean.toString(paneFrame.isShow()));
		}
		preferenceManager.setPreference(getClass().getName(), props);
	}

	// ///////////////////////////////////////////////////////// Frame Interface
	public Component getComponent() {
		return this.frame;
	}

	public Pane getActivePane() {
		if (activePaneFrame != null)
			return activePaneFrame.getPane();
		else
			return null;
	}

	public void show() {
		frame.show();
		//TODO Internazionalizzare 
		statusBar.setText("Editor ready.");
	}

	public void refresh() {
		frame.repaint();
	}

	public void reloadAllPanes() {
		Enumeration en = name2PaneFrames.keys();
		while (en.hasMoreElements()) {
			String name = en.nextElement().toString();
			try {
				PaneFrame pane = (PaneFrame) name2PaneFrames.get(name);
				pane.getPane().reload();
			} catch (Throwable tr) {
				logger.error("Error reloading: " + name);
			}
		}
	}

	public void addPane(Pane pane, boolean scrollable) {
		if (pane == null) {
			logger.error("Null pane not added.");
			return;
		}
		PaneFrame paneFrame = (PaneFrame) name2PaneFrames.get(pane.getName());
		if (paneFrame != null) {
			ViewPaneAction paneAction = new ViewPaneAction(paneFrame);
			actionManager.registerAction("view.pane." + pane.getName(), paneAction);
			paneFrame.setPane(pane, scrollable);
			boolean show = Boolean.valueOf(props.getProperty("show." + pane.getName(), "true")).booleanValue();
			if (show)
				paneFrame.setShow(true);
			else
				paneAction.actionPerformed(null);

			updateTab();
		} else
			logger.warn("cannot add pane: " + pane.getName());
	}

	public void extractPane(Pane pane) {
		// TODO extractPane
	}

	public void restorePane(Pane pane) {
		PaneFrame paneFrame = (PaneFrame) name2PaneFrames.get(pane.getName());
		paneFrame.setPane(pane);
	}

	public void updateFocusListener(Pane pane) {
		PaneFrame paneFrame = (PaneFrame) name2PaneFrames.get(pane.getName());
		paneFrame.updateFocusListeners();
	}

	public void highlightPane(Pane pane, boolean highlight) {
		PaneFrame paneFrame = (PaneFrame) name2PaneFrames.get(pane.getName());
		paneFrame.setHighlighted(highlight);
		updateTab();
	}

	// /////////////////////////////////////////////////////////// Local Methods
	public void focusGained(PaneFrame paneFrame) {
		PaneFrame oldActive = this.activePaneFrame;
		activePaneFrame = paneFrame;
		eventManager.fireEvent(new PaneFocusGainedEvent(this, activePaneFrame.getPane()));
		if (oldActive != activePaneFrame) {
			if (oldActive != null) {
				eventManager.fireEvent(new PaneDeactivatedEvent(this, oldActive.getPane()));
				oldActive.setPaneBorder(false);
			}
			eventManager.fireEvent(new PaneActivatedEvent(this, activePaneFrame.getPane()));
			activePaneFrame.setPaneBorder(true);
			activePaneFrame.setHighlighted(false);
		}
		// tCenterPane.updateTabColor(activePaneFrame, oldActive);
		// tLeftPane.updateTabColor(activePaneFrame, oldActive);
		// bCenterPane.updateTabColor(activePaneFrame, oldActive);
		// bLeftPane.updateTabColor(activePaneFrame, oldActive);
		updateActions();
	}

	public void focusLost(PaneFrame paneFrame) {
		eventManager.fireEvent(new PaneFocusLostEvent(this, paneFrame.getPane()));
	}

	public void setInteraction(boolean interaction) {
		if (interaction)
			frame.getGlassPane().setVisible(false);
		else {
			Component glassPane = frame.getGlassPane();
			glassPane.addMouseListener(new MouseAdapter() {
			});
			glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			glassPane.setVisible(true);
		}
	}

	Component expanded = null;

	protected void expand() {
		if (expanded != null) {
			frame.getContentPane().remove(expanded);
			if (expanded == tLeftPane || expanded == bLeftPane)
				splitLeft.add(tLeftPane, bLeftPane);
			else
				splitRight.add(tCenterPane, bCenterPane);
			frame.getContentPane().add(splitMain, BorderLayout.CENTER);
			expanded = null;
		} else {
			expanded = getAutoTabbedPane(activePaneFrame);
			frame.getContentPane().remove(splitMain);
			frame.getContentPane().add(expanded, BorderLayout.CENTER);
		}
		updateActions();
		frame.validate();
		frame.repaint();
	}

	protected void unexpand() {
		if (expanded != null)
			expand();
	}

	protected void updateActions() {
		if (activePaneFrame != null) {
			if (expanded == null) {
				maximizeAction.setEnabled(true);
				restoreAction.setEnabled(false);
			}
			closeAction.setEnabled(true);
			reloadAction.setEnabled(true);
		} else {
			maximizeAction.setEnabled(false);
			restoreAction.setEnabled(false);
			closeAction.setEnabled(false);
			reloadAction.setEnabled(false);
		}
	}

	Logger getLogger() {
		return this.logger;
	}

	I18n getI18n() {
		return this.i18n;
	}

	public Color getSelectedColor() {
		return Color.BLUE;
	}

	void updateTab() {
		tLeftPane.update();
		tCenterPane.update();
		bLeftPane.update();
		bCenterPane.update();
		splitLeft.update();
		splitRight.update();
		splitMain.update();
	}

	public void manageEvent(EventObject event) {
		if (event instanceof DocumentChangedEvent && !alreadyChanged) {
			frame.setTitle(frameTitle + "*");
			alreadyChanged = true;
			return;
		} else if (event instanceof DocumentOpenedEvent || event instanceof DocumentSavedEvent) {
			alreadyChanged = false;
			if (documentManager.isNew()) {
				frameTitle = appName + " - " + i18n.getTextFor("frame.msg.untitled");
			} else {
				frameTitle = appName + " - " + documentManager.getSourceName();
			}
			frame.setTitle(frameTitle);
		} else if (event instanceof DocumentClosedEvent) {
			frame.setTitle(appName);
		}
	}

	protected AutoTabbedPane getAutoTabbedPane(PaneFrame paneFrame) {
		AutoTabbedPane tab = null;
		tab = (AutoTabbedPane) SwingUtilities.getAncestorNamed("tab-top-center", paneFrame);
		if (tab == null)
			tab = (AutoTabbedPane) SwingUtilities.getAncestorNamed("tab-top-left", paneFrame);
		if (tab == null)
			tab = (AutoTabbedPane) SwingUtilities.getAncestorNamed("tab-bottom-center", paneFrame);
		if (tab == null)
			tab = (AutoTabbedPane) SwingUtilities.getAncestorNamed("tab-bottom-left", paneFrame);
		return tab;
	}

	/**
	 * Azione per allargare o ripristinare la dimensione del panello attivo a tutto
	 * schermo.
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	public class MaximizeRestoreAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			expand();
		}
	}

	/**
	 * Azione per chiudere il panello attivo.
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class CloseAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (activePaneFrame != null) {
				if (expanded != null)
					expand();
				activePaneFrame.setShow(!activePaneFrame.isShow());
				updateTab();
			}
		}
	}

	/**
	 * Azione per aggiornare il panello attivo.
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class ReloadAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (activePaneFrame != null && activePaneFrame.getPane() != null)
				activePaneFrame.getPane().reload();
		}
	}

	/**
	 * Azione per visualizzare un pannelo.
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	protected class ViewPaneAction extends AbstractAction {
		PaneFrame paneFrame;

		public ViewPaneAction(PaneFrame paneFrame) {
			this.paneFrame = paneFrame;
		}

		public void actionPerformed(ActionEvent e) {
			if (e != null) {
				unexpand();
				paneFrame.setShow(!paneFrame.isShow());
				updateTab();
				if (paneFrame.isShow()) {
					AutoTabbedPane tab = getAutoTabbedPane(paneFrame);
					tab.setSelected(paneFrame);
				}
			}
		}
	}

	protected class MyTimerTask extends TimerTask {
		public void run() {
			setInteraction(true);
		}
	}
}
