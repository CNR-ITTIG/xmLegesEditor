package it.cnr.ittig.xmleges.core.blocks.panes.source;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.PaneActivatedEvent;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.panes.source.SourcePane;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.clipboard.UtilClipboard;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneLayout;
import javax.swing.text.Style;

import org.bounce.text.xml.XMLEditorKit;
import org.bounce.text.xml.XMLFoldingMargin;
import org.bounce.text.xml.XMLStyleConstants;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.panes.source.SourcePane</code>.</h1>
 * <h1>Descrizione</h1>
 * Servizio per la visualizzazione del testo sorgente del file XML.
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.document.DocumentManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.event.EventManager:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li><code>panes.source</code>: Nome del pannello.</li>
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class SourcePaneImpl implements SourcePane, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	Frame frame;

	DocumentManager documentManager;

	EventManager eventManager;
	
	UtilUI utilUi;

	// TODO mettere nella configurazione del componente
	SourceTextPane textPane = new SourceTextPane(true);
	
	JPanel panel;

	Style tagStyle;

	XMLEditorKit kit = new XMLEditorKit();	
	
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		utilUi = (UtilUI) serviceManager.lookup(UtilUI.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, PaneActivatedEvent.class);
		panel = new JPanel(new BorderLayout());
				
		textPane.setEditable(true);		       
        //tagStyle = textPane.addStyle("rif", textPane.getStyle("default"));
		//StyleConstants.setForeground(tagStyle, Color.BLUE);		
		
		textPane.setEditorKit(kit);
		
		 // Enable auto indentation.
        kit.setAutoIndentation(true);
        // Enable tag completion.
        kit.setTagCompletion(true); 
        // Enable error highlighting.
        textPane.getDocument().putProperty(XMLEditorKit.ERROR_HIGHLIGHTING_ATTRIBUTE, new Boolean(true));
        // Set a style
        kit.setStyle(XMLStyleConstants.ATTRIBUTE_NAME, new Color(255, 0, 0), 
                      Font.BOLD);        

		JToolBar bar = new JToolBar();
		bar.add(utilUi.applyI18n("panes.xslteditor.open", new OpenAction()));
		bar.addSeparator();
		bar.add(utilUi.applyI18n("panes.xslteditor.save", new SaveAction()));
		bar.add(utilUi.applyI18n("panes.xslteditor.saveas", new SaveAsAction()));
			
		
		panel.add(bar,BorderLayout.NORTH);
		
		JPanel subpanel = new JPanel(new BorderLayout());
		subpanel.add(new XMLFoldingMargin(textPane), BorderLayout.WEST);
		subpanel.add(textPane, BorderLayout.CENTER);
		
		JScrollPane scroll = new JScrollPane(subpanel);
		
		
		panel.add(scroll, BorderLayout.CENTER);
		
		frame.addPane(this, false);

		
	}

	// ////////////////////////////////////////////////////////// Pane Interface
	public String getName() {
		return "panes.source";
	}

	public Component getPaneAsComponent() {
		return panel;
	}

	public boolean canCut() {
		return false;
	}

	public void cut() throws PaneException {
		throw new PaneException("Cannot cut");
	}

	public boolean canCopy() {
		return !documentManager.isEmpty() && textPane.getSelectedText() != null;
	}

	public void copy() throws PaneException {
		textPane.copy();
	}

	public boolean canPaste() {
		return
			false;
	}

	public void paste() throws PaneException {
		throw new PaneException("Cannot paste");
	}

	public boolean canPasteAsText() {
		return UtilClipboard.hasString();
	}

	public void pasteAsText() throws PaneException {
		textPane.replaceSelection(UtilClipboard.getAsString());
		//throw new PaneException("Cannot paste as text");
	}

	public boolean canDelete() {
		return false;
	}

	public void delete() throws PaneException {
		throw new PaneException("Cannot delete");
	}

	public boolean canPrint() {
		return true;
	}

	public Component getComponentToPrint() {
		SourceTextPane printPane = new SourceTextPane(true);
		String text = UtilDom.domToString(documentManager.getDocumentAsDom(), true, "  ");
		text = text.replaceAll("\r", "");
		printPane.setText(text);
		highlightTags(printPane);
		return printPane;
	}

	public boolean canFind() {
		// TODO canFind
		return false;
	}

	public FindIterator getFindIterator() {
		// TODO getFindIterator
		return null;
	}

	public void reload() {
		System.out.println("RICARIXOOOOOOOOOO");
		if (documentManager.isEmpty())
			textPane.setText("");
		else {
			String text = UtilDom.domToString(documentManager.getDocumentAsDom(), true, "    ");
			text = text.replaceAll("\r", "");
			textPane.setText(text);
			highlightTags(textPane);
		}
	}

	protected void highlightTags(SourceTextPane pane) {
		String text = pane.getText();
		int e = -1;
		for (int s = 0; s != -1;) {
			s = text.indexOf("<", e + 1);
			if (s != -1) {
				e = text.indexOf(">", s);
				pane.select(s, e + 1);
				//pane.setCharacterAttributes(tagStyle, true);
			}
		}
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof PaneActivatedEvent && this.equals(((PaneActivatedEvent) event).getPane())) {
			reload();
		}
	}
	
	public class OpenAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			
			
		}
	}

	public class SaveAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			
		}
	}

	public class SaveAsAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
		
		}
	}

}
