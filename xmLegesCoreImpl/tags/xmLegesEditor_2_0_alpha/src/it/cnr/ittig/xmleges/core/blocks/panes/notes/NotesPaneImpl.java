package it.cnr.ittig.xmleges.core.blocks.panes.notes;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.frame.PaneStatusChangedEvent;
import it.cnr.ittig.xmleges.core.services.panes.notes.NotesPane;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.clipboard.UtilClipboard;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

/**
 * <h1>Implementazione del servizio
 * <code>servizio it.cnr.ittig.xmleges.editor.services.panes.notes.NotesPane</code>.</h1>
 * <h1>Descrizione</h1>
 * Servizio che gestisce il pannello delle note
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * <ul> 	
 * <li>it.cnr.ittig.xmleges.core.services.bars.Bars:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.preference.PreferenceManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.event.EventManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.selection.SelectionManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.blocks.util.ui.UtilUI:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>panes.notes.open</code>: Messaggio di apertura della nota;</li>
 * <li><code>panes.notes.save</code>: Messaggio di salvataggio della nota;</li>
 * <li><code>panes.notes.saveas</code>: Messaggio di salvataggio con nome della nota;</li>
 * <li><code>panes.notes.add</code>: Messaggio di aggiunta della nota;</li>
 * <li><code>panes.notes.del</code>: Messaggio di cancellazione della nota.</li>
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
 * @see it.cnr.ittig.xmleges.core.services.preference.PreferenceManager
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @see it.cnr.ittig.xmleges.core.services.util.ui.UtilUI
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class NotesPaneImpl implements NotesPane, Loggable, Serviceable, Initializable, Startable {
	Logger logger;

	Frame frame;

	PreferenceManager preferenceManager;

	EventManager eventManager;

	SelectionManager selectionManager;

	UtilUI utilUI;

	Bars bars;

	JPanel panel = new JPanel(new BorderLayout());

	JTabbedPane tabbedPane = new JTabbedPane();

	OpenAction openAction = new OpenAction();

	SaveAction saveAction = new SaveAction();

	SaveAsAction saveAsAction = new SaveAsAction();

	TabAddAction tabAddAction = new TabAddAction();

	TabDelAction tabDelAction = new TabDelAction();

	JFileChooser fileChooser = new JFileChooser();

	TextFindIterator textFindIterator = new TextFindIterator();

	JPopupMenu popupMenu;

	MouseAdapter mouseAdapter = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			fireSelectionNode();
		}

		public void mouseReleased(MouseEvent e) {
			firePaneStatusChangedEvent();
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3)
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	};

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
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		fileChooser.setMultiSelectionEnabled(false);
		popupMenu = bars.getPopup(false);

		JToolBar bar = new JToolBar();
		bar.add(utilUI.applyI18n("panes.notes.open", openAction));
		bar.addSeparator();
		bar.add(utilUI.applyI18n("panes.notes.save", saveAction));
		bar.add(utilUI.applyI18n("panes.notes.saveas", saveAsAction));
		bar.addSeparator();
		bar.add(utilUI.applyI18n("panes.notes.add", tabAddAction));
		bar.add(utilUI.applyI18n("panes.notes.del", tabDelAction));
		panel.add(bar, BorderLayout.NORTH);

		panel.add(tabbedPane, BorderLayout.CENTER);
		new File("notes").mkdir();
	}

	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
		load();
		frame.addPane(this, false);
	}

	public void stop() throws Exception {
		int n = tabbedPane.getTabCount();
		Properties p = new Properties();
		p.setProperty("n", Integer.toString(n));
		for (int i = 0; i < n; i++) {
			NotePanel notePanel = (NotePanel) tabbedPane.getComponentAt(i);
			p.setProperty("note." + i + ".date", Long.toString(notePanel.getDate().getTime()));
			if (notePanel.isMemory()) {
				File file = new File("notes/notes-" + i);
				p.setProperty("note." + i + ".file", file.getAbsolutePath());
				UtilFile.stringToFile(notePanel.getText(), file);
			} else
				p.setProperty("note." + i + ".file", notePanel.getFile().getAbsolutePath());
			p.setProperty("note." + i + ".memory", Boolean.toString(notePanel.isMemory()));
		}
		preferenceManager.setPreference(getClass().getName(), p);
	}

	// ///////////////////////////////////////////////////// NotesPane Interface
	public String getName() {
		return "panes.notes";
	}

	public Component getPaneAsComponent() {
		return panel;
	}

	public boolean canCut() {
		return getSelectetNotePanel() != null && getSelectetNotePanel().hasSelection();
	}

	public void cut() throws PaneException {
		getSelectetNotePanel().cut();
	}

	public boolean canCopy() {
		return getSelectetNotePanel() != null && getSelectetNotePanel().hasSelection();
	}

	public void copy() throws PaneException {
		getSelectetNotePanel().copy();
	}

	public boolean canPaste() {
		return getSelectetNotePanel() != null && UtilClipboard.hasString();
	}

	public void paste() throws PaneException {
		getSelectetNotePanel().paste();
	}

	public boolean canPasteAsText() {
		// controllare clipboard vuota mettere metodi in UtilClipboard
		return getSelectetNotePanel() != null;
	}

	public void pasteAsText() throws PaneException {
		if (UtilClipboard.hasNode())
			getSelectetNotePanel().replaceSelection(UtilDom.getText(UtilClipboard.getAsNode()));
		else if (UtilClipboard.hasString())
			getSelectetNotePanel().replaceSelection(UtilClipboard.getAsString());
	}

	public boolean canDelete() {
		return getSelectetNotePanel() != null && getSelectetNotePanel().hasSelection();
	}

	public void delete() throws PaneException {
		getSelectetNotePanel().replaceSelection("");
	}

	public boolean canPrint() {
		return getSelectetNotePanel() != null;
	}

	public Component getComponentToPrint() {
		JTextPane toPrint = new JTextPane();
		toPrint.setText(getSelectetNotePanel().getText());
		return toPrint;
	}

	public boolean canFind() {
		return getSelectetNotePanel() != null;
	}

	public FindIterator getFindIterator() {
		textFindIterator.set(getSelectetNotePanel().textPane);
		return textFindIterator;
	}

	public void reload() {
		//Niente: non sono un pannello che deve essere aggiornato se è aggiornato il documento
	}
	
	private void load() {
		Properties p = preferenceManager.getPreferenceAsProperties(getClass().getName());
		int n = Integer.parseInt(p.getProperty("n", "0"));
		for (int i = 0; i < n; i++) {
			Date date = new Date(Long.parseLong(p.getProperty("note." + i + ".date")));
			File file = new File(p.getProperty("note." + i + ".file"));
			boolean memory = Boolean.valueOf(p.getProperty("note." + i + ".memory")).booleanValue();
			NotePanel notePanel = new NotePanel(date, file, memory);
			if (file.exists() && file.canRead()) {
				// TODO lettura in threads
				notePanel.setFile(file);
				addNotePanel(notePanel);
			}
		}
		updateButtons();
	}

	// ///////////////////////////////////////////////////////// Toolbar Actions
	/**
	 * Azione per l'apertura di un file in un nuovo appunto.
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
	class OpenAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			int ret = fileChooser.showOpenDialog(SwingUtilities.getRoot(tabbedPane));
			if (ret == JFileChooser.APPROVE_OPTION) {
				File f = fileChooser.getSelectedFile();
				NotePanel notePanel = new NotePanel();
				notePanel.setFile(f);
				notePanel.setMemory(false);
				addNotePanel(notePanel);
			}
		}

	}

	/**
	 * Azione per salvare l'appunto corrente.
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
	class SaveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			NotePanel notePanel = getSelectetNotePanel();
			if (notePanel.isMemory())
				saveAsAction.actionPerformed(e);
			else
				try {
					UtilFile.stringToFile(notePanel.getText(), notePanel.getFile());
				} catch (Exception ex) {
					logger.error(ex.toString(), ex);
				}
		}
	}

	/**
	 * Azione per salvare con nome l'appunto corrente.
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
	class SaveAsAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			NotePanel notePanel = getSelectetNotePanel();
			int ret = fileChooser.showSaveDialog(SwingUtilities.getRoot(tabbedPane));
			if (ret == JFileChooser.APPROVE_OPTION)
				try {
					File f = fileChooser.getSelectedFile();
					UtilFile.stringToFile(notePanel.getText(), f);
					notePanel.setFileWithoutRead(f);
					notePanel.setMemory(false);
				} catch (Exception ex) {
					logger.error(ex.toString(), ex);
				}
		}
	}

	/**
	 * Azione per aggiungere un nuovo appunto vuoto.
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
	class TabAddAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			addNotePanel(new NotePanel());
			updateButtons();
		}
	}

	/**
	 * Azione per rimuovere l'appunto corrente.
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
	class TabDelAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			int n = tabbedPane.getSelectedIndex();
			if (n != -1) {
				// TODO messaggio di conferma
				tabbedPane.remove(n);
			}
			updateButtons();
		}
	}

	protected void addNotePanel(NotePanel notePanel) {
		DateFormat df = DateFormat.getDateTimeInstance();
		notePanel.addMouseListener(mouseAdapter);
		tabbedPane.addTab(df.format(notePanel.getDate()), notePanel);
		frame.updateFocusListener(this);
		// tabbedPane.grabFocus();
		if (!notePanel.isMemory())
			tabbedPane.setToolTipTextAt(tabbedPane.getTabCount() - 1, notePanel.getFile().getAbsolutePath());
	}

	protected NotePanel getSelectetNotePanel() {
		try {
			return (NotePanel) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
		} catch (Exception ex) {
			return null;
		}
	}

	protected void firePaneStatusChangedEvent() {
		eventManager.fireEvent(new PaneStatusChangedEvent(this, this));
	}

	protected void fireSelectionNode() {
		selectionManager.setActiveNode(this, null);
	}

	protected void updateButtons() {
		boolean enab = tabbedPane.getTabCount() != 0;
		saveAction.setEnabled(enab);
		saveAsAction.setEnabled(enab);
		tabDelAction.setEnabled(enab);
	}
}
