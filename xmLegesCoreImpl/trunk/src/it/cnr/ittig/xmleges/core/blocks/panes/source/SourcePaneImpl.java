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
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.awt.Color;
import java.awt.Component;
import java.util.EventObject;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 * Implementazione del servizio it.cnr.ittig.xmleges.editor.services.panes.source.SourcePane.
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
public class SourcePaneImpl implements SourcePane, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	Frame frame;

	DocumentManager documentManager;

	EventManager eventManager;

	// TODO mettere nella configurazione del componente
	SourceTextPane textPane = new SourceTextPane(true);

	Style tagStyle;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		frame = (Frame) serviceManager.lookup(Frame.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, PaneActivatedEvent.class);
		frame.addPane(this, true);
		textPane.setEditable(false);
		tagStyle = textPane.addStyle("rif", textPane.getStyle("default"));
		StyleConstants.setForeground(tagStyle, Color.BLUE);

	}

	// ////////////////////////////////////////////////////////// Pane Interface
	public String getName() {
		return "panes.source";
	}

	public Component getPaneAsComponent() {
		return textPane;
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
				pane.setCharacterAttributes(tagStyle, true);
			}
		}
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof PaneActivatedEvent && this.equals(((PaneActivatedEvent) event).getPane())) {
			reload();
		}
	}
}
