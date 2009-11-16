package it.cnr.ittig.xmleges.core.blocks.panes.problems;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.frame.PaneStatusChangedEvent;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.panes.problems.Problem;
import it.cnr.ittig.xmleges.core.services.panes.problems.ProblemsPane;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.clipboard.UtilClipboard;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.problems.ProblemsPane</code>.</h1>
 * <h1>Descrizione</h1>
 * Servizio per la visualizzazione del pannello dei problemi.
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.core.services.selection.SelectionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.blocks.util.ui.UtilUI:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.bars.Bars:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.i18n.I18n:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>panes.problems</code>: Indica il nome del pannello;</li>
 * <li><code>panes.problems.action.goto</code>: messaggio per l'azione vai al nodo;</li>
 * <li><code>panes.problems.action.resolve</code>: messaggio per l'azione risolvi problema.</li>
 * </ul>
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @see it.cnr.ittig.xmleges.core.services.util.ui.UtilUI
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ProblemsPaneImpl implements ProblemsPane, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	Frame frame;

	EventManager eventManager;

	SelectionManager selectionManager;

	UtilUI utilUI;

	I18n i18n;

	Bars bars;

	DefaultListModel listModel = new DefaultListModel();

	JList list = new JList(listModel);

	GoToAction goToAction = new GoToAction();

	ResolveAction resolveAction = new ResolveAction();

	SortAction sortTypeAction = new SortAction(true);

	SortAction sortTextAction = new SortAction(false);

	ProblemsFindIterator problemsFindIterator = new ProblemsFindIterator(list);

	JPopupMenu popupMenu;

	MouseAdapter mouseAdapter = new MouseAdapter() {
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				Problem problem = (Problem) list.getSelectedValue();
				goToAction.setNode(problem != null ? problem.getNode() : null);
				resolveAction.setProblem(problem);
				sortTextAction.setEnabled(listModel.getSize() != 0);
				sortTypeAction.setEnabled(listModel.getSize() != 0);
				firePaneStatusChangedEvent();
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	};

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		eventManager.addListener(this, DocumentClosedEvent.class);
		// TODO icona panello
		popupMenu = bars.getPopup(false);
		popupMenu.addSeparator();
		popupMenu.add(utilUI.applyI18n("panes.problems.action.goto", goToAction));
		popupMenu.add(utilUI.applyI18n("panes.problems.action.resolve", resolveAction));
		// popupMenu.addSeparator();
		// popupMenu.add(utilUI.applyI18n("panes.problems.action.sort.type",
		// sortTypeAction));
		// popupMenu.add(utilUI.applyI18n("panes.problems.action.sort.text",
		// sortTextAction));

		list.addMouseListener(mouseAdapter);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new ProblemCellRenderer(i18n));
		frame.addPane(this, true);
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof DocumentClosedEvent)
			listModel.removeAllElements();
	}

	// ////////////////////////////////////////////////// ProblemsPane Interface
	public void addProblem(Problem problem) {
		if (!listModel.contains(problem)) {
			listModel.addElement(problem);
			frame.highlightPane(this, true);
			frame.setSelectedPane(this);
		}
	}

	public void removeProblem(Problem problem) {
		listModel.removeElement(problem);
	}

	// ////////////////////////////////////////////////////////// Pane Interface
	public String getName() {
		return "panes.problems";
	}

	public Component getPaneAsComponent() {
		return list;
	}

	public boolean canCut() {
		return canCopy() && canDelete();
	}

	public void cut() throws PaneException {
		copy();
		delete();
	}

	public boolean canCopy() {
		return list.getSelectedValue() != null;
	}

	public void copy() throws PaneException {
		UtilClipboard.set(((Problem) list.getSelectedValue()).getText());
	}

	public boolean canPaste() {
		return false;
	}

	public void paste() throws PaneException {
		throw new PaneException("Cannot paste");
	}

	public boolean canPasteAsText() {
		return false;
	}

	public void pasteAsText() throws PaneException {
		throw new PaneException("Cannot paste as text");
	}

	public boolean canDelete() {
		Problem problem = (Problem) list.getSelectedValue();
		return problem != null && problem.canRemoveByUser();
	}

	public void delete() throws PaneException {
		listModel.removeElement(list.getSelectedValue());
	}

	public boolean canPrint() {
		return listModel.getSize() > 0;
	}

	public Component getComponentToPrint() {
		JTextPane toPrint = new JTextPane();
		toPrint.setContentType("text/html");
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<head><style type=\"text/css\">body { font-family: Arial; font-size: 11pt; }</style></head>");
		sb.append("<body>");
		sb.append("<table border=\"1\">");
		sb.append("<tr>");
		sb.append("<td><center><b>Type</b></center></td>");
		sb.append("<td><center><b>Text</b></center></td>");
		sb.append("</tr>");
		for (Enumeration en = listModel.elements(); en.hasMoreElements();) {
			Problem problem = (Problem) en.nextElement();
			sb.append("<tr>");
			sb.append("<td valign=\"top\"><b>");
			switch (problem.getType()) {
			case Problem.FATAL_ERROR:
				sb.append("FATAL");
				break;
			case Problem.ERROR:
				sb.append("ERROR");
				break;
			case Problem.WARNING:
				sb.append("WARNING");
				break;
			case Problem.INFO:
				sb.append("INFO");
				break;
			}
			sb.append("</b></td>");
			sb.append("<td align=\"left\">");
			sb.append(problem.getText());
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
		return listModel.getSize() > 0;
	}

	public FindIterator getFindIterator() {
		return problemsFindIterator;
	}

	public void reload() {
		list.updateUI();
	}

	protected void firePaneStatusChangedEvent() {
		eventManager.fireEvent(new PaneStatusChangedEvent(this, this));
	}

	// //////////////////////////////////////////////////////////////// Actions
	/**
	 * Azione per posizionarsi sul nodo che ha il problema.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	class GoToAction extends AbstractAction {
		Node node;

		public void setNode(Node node) {
			this.node = node;
			setEnabled(node != null);
		}

		public void actionPerformed(ActionEvent e) {
			selectionManager.setActiveNode(this, node);
		}

	}

	/**
	 * Azione per posizionarsi sul nodo che ha il problema.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	class ResolveAction extends AbstractAction {
		Problem problem;

		public void setProblem(Problem problem) {
			this.problem = problem;
			setEnabled(problem != null && problem.canResolveProblem());
		}

		public void actionPerformed(ActionEvent e) {
			if (problem.resolveProblem())
				listModel.removeElement(problem);
		}

	}

	/**
	 * Azione per ordinare la lista dei problemi.
	 * 
	 * <p>
	 * <dl>
	 * <dt><b>Copyright &copy;: </b></dt>
	 * <dd>2003 - 2004</dd>
	 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
	 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
	 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
	 * <dt><b>License: </b></dt>
	 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General
	 * Public License </a></dd>
	 * </dl>
	 * 
	 * @version 1.0
	 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
	 */
	class SortAction extends AbstractAction {
		boolean type;

		public SortAction(boolean type) {
			this.type = type;
		}

		public void actionPerformed(ActionEvent e) {
			if (type)
				; // TODO sort per tipe
			else
				; // TODO sort alfabetico
		}

	}

}
