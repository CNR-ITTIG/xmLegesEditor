package it.cnr.ittig.xmleges.editor.blocks.panes.xsltpane;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.bars.Bars;
import it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.frame.PaneActivatedEvent;
import it.cnr.ittig.xmleges.core.services.frame.PaneException;
import it.cnr.ittig.xmleges.core.services.frame.PaneStatusChangedEvent;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.panes.xsltmapper.XsltMapper;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.DeleteNextPrevAction;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.InsertBreakAction;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.XsltPane;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Implementazione del servizio it.cnr.ittig.xmleges.editor.services.xsltpane.XsltPane;
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class XsltPaneImpl implements XsltPane, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	XsltMapper xsltMapper;

	DocumentManager documentManager;

	DtdRulesManager rulesManager;

	EventManager eventManager;

	Frame frame;

	Bars bars;

	I18n i18n;

	SelectionManager selectionMananger;

	UtilRulesManager utilRulesManager;

	JPanel panel = new JPanel(new BorderLayout());

	XsltTextPane xsltTextPane;

	XsltDocument xsltDocument;

	XsltFindIterator xsltFindIterator;

	JPopupMenu popupMenu;

	JMenu prependableMenu;

	JMenu appendableMenu;

	JMenu insertableMenu;

	JMenu insertBeforeSelMenu;

	JMenu insertAfterSelMenu;

	File xslt;

	File css;

	Hashtable param;

	String name = "xsltpane";

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		xsltMapper = (XsltMapper) serviceManager.lookup(XsltMapper.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		frame = (Frame) serviceManager.lookup(Frame.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		selectionMananger = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentChangedEvent.class);
		eventManager.addListener(this, PaneActivatedEvent.class);
		xsltTextPane = new XsltTextPane(this);
		xsltDocument = xsltTextPane.getXsltDocument();
		xsltTextPane.setTransferHandler(new XsltTransferHandler(this));
		xsltTextPane.setDragEnabled(true);

		panel.add(new JScrollPane(xsltTextPane), BorderLayout.CENTER);
		xsltFindIterator = new XsltFindIterator(this, xsltTextPane);
		popupMenu = bars.getPopup(false);
		xsltTextPane.addMouseListener(new XsltPaneMouseListener());
		panel.setDoubleBuffered(true);
	}

	boolean updated = false;

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (event instanceof DocumentChangedEvent) {
			if (xsltTextPane.isShowing())
				// if (updated) {
				xsltTextPane.update();
			xsltDocument.setActiveNode(selectionMananger.getActiveNode());
			// xsltDocument.manageEvent((DocumentChangedEvent) event);
			// xsltTextPane.repaint();
			// updated = true;
			// } else
			// updated = false;
		} else if (event instanceof SelectionChangedEvent) {
			SelectionChangedEvent e = (SelectionChangedEvent) event;
			if (e.isActiveNodeChanged())
				xsltDocument.setActiveNode(e.getActiveNode());
			else if (e.isSelectedNodesChanged())
				xsltDocument.setSelectedNodes(e.getSelectedNodes());
			xsltTextPane.updateSelections(false);
		} else if (event instanceof PaneActivatedEvent) {
			PaneActivatedEvent e = (PaneActivatedEvent) event;
			if (this.equals(e.getPane()) && !updated) {
				xsltTextPane.update();
				updated = true;
			}
		} else if (event instanceof DocumentOpenedEvent) {
			DocumentOpenedEvent e = (DocumentOpenedEvent) event;
			if (logger.isDebugEnabled())
				logger.debug("DOM=" + UtilDom.domToString(e.getDocument()));
			xsltTextPane.setDocument(e.getDocument());
			if (xsltTextPane.isShowing()) {
				xsltTextPane.update();
				updated = true;
			} else
				updated = false;
		} else if (event instanceof DocumentClosedEvent) {
			xsltTextPane.setDocument((Document) null);
			xsltTextPane.update();
			updated = true;
		}
	}

	// ////////////////////////////////////////////////////// XsltPane Interface
	public void set(File xslt, File css, Hashtable param) {
		xsltTextPane.setXslt(xslt);
		xsltTextPane.setStyleSheet(css, false);
		xsltTextPane.setParameter(param);
		xsltTextPane.update();
	}

	public void addStyleSheet(File css) {
		xsltTextPane.setStyleSheet(css, true);
		xsltTextPane.update();
	}

	public void setParameters(Hashtable param) {
		xsltTextPane.setParameter(param);
		xsltTextPane.update();
	}

	public void addParameterPanel(Component paramPanel, Object pos) {
		panel.add(paramPanel, pos);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public Component getPaneAsComponent() {
		return panel;
	}

	public boolean canCut() {
		return xsltTextPane.canCut();
	}

	public void cut() {
		xsltTextPane.cut();
	}

	public boolean canCopy() {
		return xsltTextPane.canCopy();
	}

	public void copy() {
		xsltTextPane.copy();
	}

	public boolean canPaste() {
		return xsltTextPane.canPaste();
	}

	public void paste() {
		xsltTextPane.paste();
	}

	public boolean canDelete() {
		return xsltTextPane.canDelete();
	}

	public void delete() throws PaneException {
		throw new PaneException("Unsupported action: delete");
	}

	public boolean canPasteAsText() {
		return xsltTextPane.canPasteAsText();
	}

	public void pasteAsText() throws PaneException {
		throw new PaneException("Unsupported action: pasteAsText");
	}

	public boolean canPrint() {
		return xsltTextPane.isEnabled();
	}

	public Component getComponentToPrint() {
		JTextPane toPrint = new JTextPane();
		toPrint.setEditorKit(new HTMLEditorKit());
		// TODO IMPOSTARE CSS
		toPrint.setText(xsltTextPane.getText());
		toPrint.setEditable(false);
		toPrint.setDoubleBuffered(false);
		return toPrint;
	}

	public boolean canFind() {
		return xsltTextPane.isEnabled();
	}

	public FindIterator getFindIterator() {
		return this.xsltFindIterator;
	}

	public void reload() {
		// TODO reload
	}

	public synchronized void updateNode(Node node, String text) {
		logger.debug("UPDATING NODE BEGIN");
		EditTransaction editTrans = null;
		try {
			editTrans = documentManager.beginEdit(this);

			if (logger.isDebugEnabled()) {
				logger.debug("node:" + node);
				logger.debug("text:" + text);
			}
			Node parentNode = xsltMapper.getParentByGen(node);
			if (parentNode != null) // nodo testo creato dall'xslt
			{
				parentNode.appendChild(node); // collegalo al suo padre
				xsltMapper.removeGen2Parent(node); // rimuovili dalle tabelle di mapping
				fireActiveNodeChanged(node);
			}

			if (UtilDom.isTextNode(node)) {
				if (text != null) {
					node.setNodeValue(text);
				} else {
					parentNode = node.getParentNode();
					xsltMapper.mapGen2Parent(node, parentNode);
					parentNode.removeChild(node);
					node.setNodeValue(xsltMapper.getI18nNodeText(parentNode));
					fireActiveNodeChanged(parentNode);
				}
			} else if (node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
				if (text != null) {
					node.setNodeValue(text);
				} else {
					node.getParentNode().removeChild(node);
				}
			}
			documentManager.commitEdit(editTrans);
		} catch (DocumentManagerException ex) {
			logger.error(ex.toString(), ex);
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			documentManager.rollbackEdit(editTrans);
		}
		logger.debug("UPDATING NODE END");
	}

	public synchronized void fireActiveNodeChanged(Node node) {
		selectionMananger.setActiveNode(this, node);
	}

	public synchronized void fireSelectedNodesChanged(Node[] nodes) {
		selectionMananger.setSelectedNodes(this, nodes);
	}

	public synchronized void fireSelectionChanged(Node node, int start, int end) {
		logger.debug("new selection:" + start + "," + end);
		selectionMananger.setSelectedText(this, node, start, end);
	}

	public synchronized void firePaneStatusChanged() {
		eventManager.fireEvent(new PaneStatusChangedEvent(this, this));
	}

	// /////////////////////////////////////////// methods for component classes
	XsltMapper getXsltMapper() {
		return this.xsltMapper;
	}

	DocumentManager getDocumentManager() {
		return this.documentManager;
	}

	public DtdRulesManager getRulesManager() {
		return this.rulesManager;
	}

	Logger getLogger() {
		return this.logger;
	}

	I18n getI18n() {
		return this.i18n;
	}

	public SelectionManager getSelectionManager() {
		return this.selectionMananger;
	}

	EventManager getEventManager() {
		return this.eventManager;
	}

	XsltTextPane getXsltTextPane() {
		return this.xsltTextPane;
	}

	XsltDocument getXsltDocument() {
		return this.xsltDocument;
	}

	UtilRulesManager getUtilRulesManager() {
		return this.utilRulesManager;
	}

	void updateSelections(boolean local) {
		xsltTextPane.updateSelections(local);
	}

	public void setInsertBreakAction(InsertBreakAction insertBreakAction) {
		// TODO Auto-generated method stub

	}

	public class XsltPaneMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
			xsltTextPane.getXsltDocument().setActiveLeaf(xsltTextPane.getCaretPosition());
			xsltTextPane.updateSelections(false);
			if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {

				Node active = selectionMananger.getActiveNode();
				logger.info(active.toString());

				if (prependableMenu != null & appendableMenu != null & insertableMenu != null) {
					popupMenu.remove(appendableMenu);
					popupMenu.remove(prependableMenu);
					popupMenu.remove(insertableMenu);
				}
				logger.info("primi due menu");
				appendableMenu = utilRulesManager.createMenuInsertAfter(active);
				prependableMenu = utilRulesManager.createMenuInsertBefore(active);
				// insertableMenu = utilRulesManager.createMenuInsertNode(active,
				// selectionMananger
				// .getTextSelectionStart(), selectionMananger.getTextSelectionEnd());
				popupMenu.add(prependableMenu);
				popupMenu.add(appendableMenu);
				popupMenu.add(insertableMenu);

				popupMenu.show(xsltTextPane, e.getX(), e.getY());
			}

		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			xsltTextPane.getXsltDocument().setActiveLeaf(xsltTextPane.getCaretPosition());
			xsltTextPane.getHighlighter().removeAllHighlights();
		}

		public void mouseReleased(MouseEvent e) {
			xsltTextPane.updateSelections(true);
		}
	}

	public void setDeleteNextPrevAction(DeleteNextPrevAction deleteNextPrevAction) {
		// TODO Auto-generated method stub

	}
}
