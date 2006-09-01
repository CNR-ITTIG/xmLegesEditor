package it.cnr.ittig.xmleges.core.blocks.panes.segnalazioni;

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
import it.cnr.ittig.xmleges.core.blocks.bugreport.BugReportAppender;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.tool.bugreport.BugReportAction;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.bugreport.BugReport;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.frame.PaneStatusChangedEvent;
import it.cnr.ittig.xmleges.core.services.panes.segnalazioni.SegnalazioniPane;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.DefaultListModel;

/**						
 * Implementazione del servizio it.cnr.ittig.xmleges.editor.services.panes.segnalazioni.SegnalazioniPane.
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
 */

public class SegnalazioniPaneImpl implements SegnalazioniPane, Loggable, Serviceable, Initializable, Startable {

	Logger logger;
	BugReport bugTracer;
	ActionManager actionManager;
	
	Frame frame;

	PreferenceManager preferenceManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	UtilUI utilUI;

	Bars bars;

	JPanel panel = new JPanel(new BorderLayout());
	JScrollPane scrollPane = new JScrollPane();

	JList list = new JList();

	OpenAction openAction = new OpenAction();

	JPopupMenu popupMenu;

	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}
	
	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		preferenceManager = (PreferenceManager) serviceManager.lookup(PreferenceManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		//aggiunti da me... i sopra vanno ancora controllati
		bugTracer = (BugReport) serviceManager.lookup(BugReport.class);
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		popupMenu = bars.getPopup(false);

		//Barra con il bottone per aprire il BugReport
		JToolBar bar = new JToolBar();
		bar.add(utilUI.applyI18n("panes.segnalazioni.open", openAction));
		panel.add(bar, BorderLayout.NORTH);
		

		scrollPane.setViewportView(list);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPane);
		list.setModel(BugReportAppender.getListModel());
	    
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

////???????????????????
//	protected void firePaneStatusChangedEvent() {
//		eventManager.fireEvent(new PaneStatusChangedEvent(this, this));
//	}
////???????????????????
//	protected void fireSelectionNode() {
//		selectionManager.setActiveNode(this, null);
//	}

	public boolean canCut() {
		// TODO Auto-generated method stub
		return false;
	}

	public void cut() throws PaneException {
		// TODO Auto-generated method stub
	}

	public boolean canCopy() {
		// TODO Auto-generated method stub
		return false;
	}

	public void copy() throws PaneException {
		// TODO Auto-generated method stub
	}

	public boolean canPaste() {
		// TODO Auto-generated method stub
		return false;
	}

	public void paste() throws PaneException {
		// TODO Auto-generated method stub
	}

	public boolean canPasteAsText() {
		// TODO Auto-generated method stub
		return false;
	}

	public void pasteAsText() throws PaneException {
		// TODO Auto-generated method stub
	}

	public boolean canDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	public void delete() throws PaneException {
		// TODO Auto-generated method stub
	}

	public boolean canPrint() {
		// TODO Auto-generated method stub
		return false;
	}

	public Component getComponentToPrint() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canFind() {
		// TODO Auto-generated method stub
		return false;
	}

	public FindIterator getFindIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	public void reload() {
		// TODO Auto-generated method stub
	}


}
