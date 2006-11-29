package it.cnr.ittig.xmleges.core.blocks.panes.xslteditor;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.Pane;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.panes.xslteditor.XsltEditor;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.XsltPane;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.xslt.UtilXslt;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.xslteditor.XsltEditor</code>.</h1>
 * <h1>Descrizione</h1>
 * Servizio che gestisce il pannello per le trasformazioni xslt
 * <h1>Configurazione</h1>
 * La configurazione ha il seguente tag:
 * <ul>
 * <li><code>&lt;name&gt;</code>: che specifica la chiave i18n per il nome del pane; </li>
 * </ul>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.core.services.bars.Bars:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.panes.xsltpane.XsltPane:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.blocks.util.ui.UtilUI:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>panes.xslteditor.open</code>: Messaggio di apertura xslt o css;</li>
 * <li><code>panes.xslteditor.save</code>: Messaggio di salvataggio xslt o css;</li>
 * <li><code>panes.xslteditor.saveas</code>: Messaggio di salvataggio con nome xslt o css;</li>
 * <li><code>panes.xslteditor.apply</code>: Messaggio di apply;</li>
 * <li><code>panes.xslteditor.tab.xslt</code>: Nome della scheda trasformazione del documento corrente;</li>
 * <li><code>panes.xslteditor.tab.param</code>: Nome della scheda parametri;</li>
 * <li><code>panes.xslteditor.tab.conv</code>: Nome della scheda conversione documento;</li>
 * <li><code>panes.xslteditor.tab.view</code>: Nome della scheda view;</li>
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
 * @see it.cnr.ittig.xmleges.core.services.util.ui.UtilUI
 * @see it.cnr.ittig.xmleges.core.services.panes.xsltpane.XsltPane
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class XsltEditorImpl implements XsltEditor, Pane, Loggable, Serviceable, Initializable {
	Logger logger;

	Frame frame;

	XsltPane xsltPane;

	DocumentManager documentManager;

	UtilUI utilUi;

	JPanel panel;

	JTabbedPane tabbedPane = new JTabbedPane();

	JTextArea xsltTextArea = new JTextArea();

	JTextArea cssTextArea = new JTextArea();

	JTextArea convTextArea = new JTextArea();

	ParamPanel paramPanel;

	File xsltFile;

	File cssFile;

	JFileChooser fileChooser = new JFileChooser();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		xsltPane = (XsltPane) serviceManager.lookup(XsltPane.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilUi = (UtilUI) serviceManager.lookup(UtilUI.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		panel = new JPanel(new BorderLayout());
		panel.add(utilUi.applyI18n(tabbedPane), BorderLayout.CENTER);

		JToolBar bar = new JToolBar();
		bar.add(utilUi.applyI18n("panes.xslteditor.open", new OpenAction()));
		bar.addSeparator();
		bar.add(utilUi.applyI18n("panes.xslteditor.save", new SaveAction()));
		bar.add(utilUi.applyI18n("panes.xslteditor.saveas", new SaveAsAction()));
		bar.addSeparator();
		bar.add(utilUi.applyI18n("panes.xslteditor.apply", new ApplyAction()));
		panel.add(bar, BorderLayout.NORTH);

		tabbedPane.add("panes.xslteditor.tab.xslt", new JScrollPane(xsltTextArea));
		tabbedPane.add("panes.xslteditor.tab.css", new JScrollPane(cssTextArea));
		paramPanel = new ParamPanel(utilUi);
		tabbedPane.add("panes.xslteditor.tab.param", paramPanel);
		convTextArea.setWrapStyleWord(true);
		convTextArea.setEditable(false);
		tabbedPane.add("panes.xslteditor.tab.conv", new JScrollPane(convTextArea));
		tabbedPane.add("panes.xslteditor.tab.view", xsltPane.getPaneAsComponent());
		utilUi.applyI18n(tabbedPane);

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
		frame.addPane(this, false);
	}

	public class OpenAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (fileChooser.showOpenDialog(SwingUtilities.getRoot(tabbedPane)) == JFileChooser.APPROVE_OPTION)
				try {
					File[] files = fileChooser.getSelectedFiles();
					for (int i = 0; i < files.length; i++) {
						String name = files[i].getName();
						String ext = name.substring(name.lastIndexOf('.') + 1);
						logger.debug("ext=" + ext);
						if (ext.equalsIgnoreCase("xsl") || ext.equalsIgnoreCase("xslt")) {
							xsltTextArea.setText(UtilFile.fileToString(files[i]));
							xsltFile = files[i];
							tabbedPane.setSelectedIndex(0);
						} else if (ext.equalsIgnoreCase("css")) {
							cssTextArea.setText(UtilFile.fileToString(files[i]));
							cssFile = files[i];
							tabbedPane.setSelectedIndex(1);
						} else
							; // MSG
					}
				} catch (Exception ex) {
					logger.error(ex.toString(), ex);
				}
		}
	}

	public class SaveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			save(false);
		}
	}

	public class SaveAsAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			save(true);
		}
	}

	protected void save(boolean saveAs) {
		int idx = tabbedPane.getSelectedIndex();
		if (idx != 0 && idx != 1)
			// TODO MESSAGGIO
			return;

		File file = idx == 0 ? xsltFile : cssFile;
		if (saveAs || file == null)
			if (fileChooser.showSaveDialog(SwingUtilities.getRoot(tabbedPane)) == JFileChooser.APPROVE_OPTION)
				file = fileChooser.getSelectedFile();
			else
				return;

		if (file != null)
			try {
				// TODO file exist -> messaggio
				UtilFile.stringToFile(xsltTextArea.getText(), file);
				if (idx == 0)
					xsltFile = file;
				else
					cssFile = file;
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
	}

	public class ApplyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			UtilFile.copyFileInTemp(new ByteArrayInputStream(xsltTextArea.getText().getBytes()), "xslteditor.xsl");
			UtilFile.copyFileInTemp(new ByteArrayInputStream(cssTextArea.getText().getBytes()), "xslteditor.css");
			File xslt = UtilFile.getFileFromTemp("xslteditor.xsl");
			File css = UtilFile.getFileFromTemp("xslteditor.css");
			xsltPane.set(xslt, css, paramPanel.getParams());
			try {
				Node node = UtilXslt.applyXslt(documentManager.getDocumentAsDom(), xslt);
				convTextArea.setText(UtilDom.domToString(node));
			} catch (Exception ex) {
				// TODO MSG
				logger.error(ex.toString(), ex);
			}
			tabbedPane.setSelectedIndex(4);
		}
	}

	// ////////////////////////////////////////////////////////// Pane Interface
	public boolean canCopy() {
		return xsltPane.canCopy();
	}

	public boolean canCut() {
		return xsltPane.canCut();
	}

	public boolean canDelete() {
		return xsltPane.canDelete();
	}

	public boolean canFind() {
		return xsltPane.canFind();
	}

	public boolean canPaste() {
		return xsltPane.canPaste();
	}

	public boolean canPasteAsText() {
		return xsltPane.canPasteAsText();
	}

	public boolean canPrint() {
		return xsltPane.canPrint();
	}

	public void copy() throws PaneException {
		xsltPane.copy();
	}

	public void cut() throws PaneException {
		xsltPane.cut();
	}

	public void delete() throws PaneException {
		xsltPane.delete();
	}

	public Component getComponentToPrint() {
		return xsltPane.getComponentToPrint();
	}

	public FindIterator getFindIterator() {
		return xsltPane.getFindIterator();
	}

	public String getName() {
		return "panes.xslteditor";
	}

	public Component getPaneAsComponent() {
		return panel;
	}

	public void paste() throws PaneException {
		xsltPane.paste();
	}

	public void pasteAsText() throws PaneException {
		xsltPane.pasteAsText();
	}

	public void reload() {
		xsltPane.reload();
	}
}
