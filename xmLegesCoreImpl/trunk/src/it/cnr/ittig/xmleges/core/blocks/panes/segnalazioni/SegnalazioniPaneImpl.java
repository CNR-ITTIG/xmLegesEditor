package it.cnr.ittig.xmleges.core.blocks.panes.segnalazioni;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.blocks.bugreport.BugReportAppender;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.bugreport.BugReport;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.panes.segnalazioni.SegnalazioniPane;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

/**						
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.segnalazioni.SegnalazioniPane</code>.</h1>
 * <h1>Descrizione</h1>
 * Servizio per la visualizzazione del pannello delle segnalazioni.
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.core.services.bars.Bars:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.blocks.util.ui.UtilUI:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.bugreport.BugReport:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>panes.segnalazioni</code>: chiave che contiene il nome del pannello;</li>
 * <li><code>panes.segnalazioni.open</code>: messaggio di apertura della maschera del bugreport;</li>
 * <li><code>panes.segnalazioni.clear</code>: messaggio di cancellazione delle segnalazioni.</li>
 * </ul>
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
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @see it.cnr.ittig.xmleges.core.services.util.ui.UtilUI
 * @version 1.0
 * @author <a href="mailto:g.giardiello@gmail.com">Gerardo Giardiello</a>
 */

public class SegnalazioniPaneImpl implements SegnalazioniPane, EventManagerListener, Loggable, Serviceable, Initializable, Startable {

	Logger logger;
	
	BugReport bugTracer; 
	
	Frame frame;

	EventManager eventManager;

	UtilUI utilUI;

	Bars bars;

	JPanel panel = new JPanel(new BorderLayout());
	
	JScrollPane scrollPane = new JScrollPane();

	JList list = new JList();

	OpenAction openAction = new OpenAction();
	
	ClearAction clearAction = new ClearAction();

	JPopupMenu popupMenu;

	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}
	
	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		bugTracer = (BugReport) serviceManager.lookup(BugReport.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		popupMenu = bars.getPopup(false);
		JToolBar bar = new JToolBar();
		bar.add(utilUI.applyI18n("panes.segnalazioni.open", openAction));
		panel.add(bar, BorderLayout.NORTH);
		bar.add(utilUI.applyI18n("panes.segnalazioni.clear", clearAction));
		panel.add(bar, BorderLayout.NORTH);
		scrollPane.setViewportView(list);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		list.setModel(BugReportAppender.getListModel());
		eventManager.addListener(this, DocumentClosedEvent.class);
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof DocumentClosedEvent)
			bugTracer.clearBugReport();
	}
	
	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
		frame.addPane(this, false);
	}

	public void stop() throws Exception {
	}

	// ///////////////////////////////////////////////////// SegnalazioniPane Interface
	public String getName() {
		return "panes.segnalazioni";
	}

	public Component getPaneAsComponent() {
		return panel;
	}
	
	// ///////////////////////////////////////////////////////// Toolbar Actions
	/**
	 * Azione per l'apertura del BugReport.
	 */
	class OpenAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			//Apro il BugReport
			bugTracer.openForm();
		}
	}
	
	protected class ClearAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			bugTracer.clearBugReport();
			logger.info("log cleared");
		}
	}

	public boolean canCut() {
		return false;
	}

	public void cut() throws PaneException {
	}

	public boolean canCopy() {
		return false;
	}

	public void copy() throws PaneException {
	}

	public boolean canPaste() {
		return false;
	}

	public void paste() throws PaneException {
	}

	public boolean canPasteAsText() {
		return false;
	}

	public void pasteAsText() throws PaneException {
	}

	public boolean canDelete() {
		return false;
	}

	public void delete() throws PaneException {
	}

	public boolean canPrint() {
		return false;
	}

	public Component getComponentToPrint() {
		return null;
	}

	public boolean canFind() {
		return false;
	}

	public FindIterator getFindIterator() {
		return null;
	}

	public void reload() {
	}
	
}
