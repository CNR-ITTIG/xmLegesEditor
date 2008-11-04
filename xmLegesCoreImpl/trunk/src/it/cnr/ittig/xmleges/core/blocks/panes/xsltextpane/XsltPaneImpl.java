package it.cnr.ittig.xmleges.core.blocks.panes.xsltextpane;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
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
import it.cnr.ittig.xmleges.core.services.document.DocumentSavedEvent;
import it.cnr.ittig.xmleges.core.services.document.DomEdit;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
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
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.KeyTypedAction;
import it.cnr.ittig.xmleges.core.services.panes.xsltpane.XsltPane;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheck;
import it.cnr.ittig.xmleges.core.services.threads.ThreadManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLEditorKit;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.services.xsltpane.XsltPane</code></h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>.
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
 * @version 1.0
 * @author <a href="mailto:m.taddei@ittig.cnr.it">Mirco Taddei </a>
 */
public class XsltPaneImpl implements XsltPane, EventManagerListener, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	XsltMapper xsltMapper;

	DocumentManager documentManager;

	RulesManager rulesManager;

	EventManager eventManager;

	Frame frame;

	Bars bars;

	I18n i18n;

	SelectionManager selectionManager;

	UtilRulesManager utilRulesManager;

	DomSpellCheck domSpellCheck;

	ThreadManager threadManager;

	JPanel panel = new JPanel(new BorderLayout());

	AntiAliasedTextPane textPane;

	InsertBreakAction insertBreakAction = null;
	
	KeyTypedAction keyTypedAction = null;

	DeleteNextPrevAction deleteNextPrevAction = null;

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

	long timerTaskPeriod;

	String[] browsers;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		xsltMapper = (XsltMapper) serviceManager.lookup(XsltMapper.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		frame = (Frame) serviceManager.lookup(Frame.class);
		bars = (Bars) serviceManager.lookup(Bars.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		domSpellCheck = (DomSpellCheck) serviceManager.lookup(DomSpellCheck.class);
		threadManager = (ThreadManager) serviceManager.lookup(ThreadManager.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration conf) throws ConfigurationException {
		// FIXME calibrare tempo di attesa di UpdateTask o eliminarlo del tutto  era 5000
		timerTaskPeriod = conf.getAttributeAsLong("updateperiod", 30000);
		Configuration bs = conf.getChild("browsers");
		if (bs != null) {
			Configuration[] b = bs.getChildren("browser");
			browsers = new String[b.length];
			for (int i = 0; i < b.length; i++)
				browsers[i] = b[i].getValue();
		}

	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentChangedEvent.class);
		eventManager.addListener(this, PaneActivatedEvent.class);
		eventManager.addListener(this, DocumentSavedEvent.class);
		//eventManager.addListener(this, DomSpellCheckEvent.class);
		textPane = new AntiAliasedTextPane(this);

		panel.add(new JScrollPane(textPane), BorderLayout.CENTER);
		xsltFindIterator = new XsltFindIterator(this);
		popupMenu = bars.getPopup(false);
		panel.setDoubleBuffered(true);

		Timer timer = new Timer();
		// FIXME provare a toglierlo ? allungato a 30 secondi !!   TOLTO
		//timer.scheduleAtFixedRate(new UpdateTask(), timerTaskPeriod, timerTaskPeriod);
	}

	boolean updated = false;

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		
		if (event instanceof DocumentChangedEvent) {
			if (!textPane.isShowing()) {
				updated = false;
				return;
			}
			
			if (!updated) {
				updateTextPane();
				return;
			}
			
			DocumentChangedEvent e = (DocumentChangedEvent) event;
			boolean all = false;
			DomEdit[] edits = getEditsToUpdate(e.getTransaction().getEdits(), e.isUndo());	
			boolean globalUpdateAllowed = !isTextualUpdate(edits);
			
			Vector updatedNode = new Vector();
			for (int i = 0; i < edits.length; i++) {
				if (logger.isDebugEnabled())
					logger.debug(edits[i].toString());
				Node node = edits[i].getNode();	
				if (!UtilDom.isInSubtrees(node, updatedNode.iterator())) {
					Node u = textPane.update(edits[i].getNode());
					if (u == null) {
					    if(logger.isDebugEnabled())
					    	logger.debug("return null updating "+edits[i].getNode().getNodeName()+" starting global update IN "+this.getName());
						all = true;
						break;
					} else{
						updatedNode.addElement(u);
					}
				}
			}
			updated = !all;
			
			if(!globalUpdateAllowed){ // prevents global update of panes if not strictly necessary [modified text not visible on the active pane]
				updated = true;
			}
			
			if (!updated){ 
				System.err.println("STARTING GLOBAL UPDATE FOR "+this.getName());
				updateTextPane();
				updated = true;
			}
			
			textPane.selectNode(new Node[] { selectionManager.getActiveNode() });
			
			if(logger.isDebugEnabled()){
				logger.debug("setted ActiveNode in DocumentChangedEvent di core/xsltPane: "+selectionManager.getActiveNode().getNodeValue());
				logger.debug("updated = "+updated);
			}
			
		} else if (event instanceof SelectionChangedEvent) {
			if (!textPane.isShowing()) {
				updated = false;
				return;
			}
			if (!updated) {
				updateTextPane();
			}
			SelectionChangedEvent e = (SelectionChangedEvent) event;
			if (e.isActiveNodeChanged()) {
				textPane.selectNode(new Node[] { e.getActiveNode() });
			} else if (e.isSelectedNodesChanged()) {
				textPane.selectNode(e.getSelectedNodes());
			} else if (e.isTextSelectedChanged()) {
				textPane.selectNode(new Node[] { e.getActiveNode() });
				Element currElem = textPane.getHTMLDocument().getElement(xsltMapper.getIdByDom(e.getActiveNode()));
				if (textPane.isMappedElement(currElem)) {
					int startOff = currElem.getStartOffset() + 1;
					textPane.selectText(startOff + e.getTextSelectionStart(), startOff + e.getTextSelectionEnd());
				}
			}
		} else if (event instanceof PaneActivatedEvent) {
			if (!updated)
				updateTextPane();
		} else if (event instanceof DocumentOpenedEvent) {
			DocumentOpenedEvent e = (DocumentOpenedEvent) event;
			if (logger.isDebugEnabled())
				logger.debug("DOM=" + UtilDom.domToString(e.getDocument()));
			textPane.setDom(e.getDocument());
			
			// inserisco il parametro del BaseURL
			Hashtable hashtable = new Hashtable(1);
			hashtable.put("base", "file:///"+UtilFile.getFolderPath(documentManager.getSourceName()));
			textPane.setParameter(hashtable);
			
			
			if (textPane.isShowing()) {
				updateTextPane();
			} else
				updated = false;
		} else if (event instanceof DocumentClosedEvent) {
			textPane.setDom((Document) null);
			updateTextPane();
		} else if (event instanceof DocumentSavedEvent) {

			// inserisco il parametro del BaseURL
			Hashtable hashtable = new Hashtable(1);
			hashtable.put("base", "file:///"+UtilFile.getFolderPath(documentManager.getSourceName()));			
			textPane.setParameter(hashtable);

		} 
		//	else if (event instanceof DomSpellCheckEvent) {
		//	textPane.viewDomSpellCheckEvent((DomSpellCheckEvent)event);
		//	//updateTextPane();
		//	}
	}

	
	private boolean isTextualUpdate(DomEdit[] edits){
		boolean isTextualUpdate = true;
		for (int i = 0; i < edits.length; i++) {
			if(!UtilDom.isTextNode(edits[i].getNode()))
					return false;
		}
		return isTextualUpdate;
	}
	
	// ////////////////////////////////////////////////////// XsltPane Interface
	public void set(File xslt, File css, Hashtable param) {
		textPane.setXslt(xslt);
		textPane.setStyleSheet(css);
		textPane.setParameter(param);
		textPane.update();
	}

	public void addStyleSheet(File css) {
		textPane.addStyleSheet(css);
		textPane.update();
	}

	public void setParameters(Hashtable param) {
		textPane.setParameter(param);
		textPane.update();
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
		return textPane.canCut();
	}

	public void cut() {
		textPane.cut();
	}

	public boolean canCopy() {
		return textPane.canCopy();
	}

	public void copy() {
		textPane.copy();
	}

	public boolean canPaste() {
		return textPane.canPaste();
	}

	public void paste() {
		textPane.paste();
	}

	public boolean canDelete() {
		return textPane.canDelete();
	}

	public void delete() throws PaneException {
		textPane.delete();
	}

	public boolean canPasteAsText() {
		return textPane.canPasteAsText();
	}

	public void pasteAsText() throws PaneException {
		textPane.pasteAsText();
	}

	public boolean canPrint() {
		return textPane.isEnabled();
	}

	public Component getComponentToPrint() {
		JTextPane toPrint = new JTextPane();
		toPrint.setEditorKit(new HTMLEditorKit());
		toPrint.setText(textPane.getText());
		return toPrint;
	}

	public boolean canFind() {
		return textPane.isEnabled();
	}

	public FindIterator getFindIterator() {
		return this.xsltFindIterator;
	}

	public void setInsertBreakAction(InsertBreakAction insertBreakAction) {
		this.insertBreakAction = insertBreakAction;
	}

	public void setDeleteNextPrevAction(DeleteNextPrevAction deleteNextPrevAction) {
		this.deleteNextPrevAction = deleteNextPrevAction;
	}
	
	public void setKeyTypedAction(KeyTypedAction keyTypedAction){
		this.keyTypedAction = keyTypedAction;
	}

	public void reload() {
		textPane.update();
		textPane.selectNode(new Node[] { selectionManager.getActiveNode() });
	}

	/* Update text of a whole dom node. mm. */
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
			} else if (node!=null && node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
				if (text != null) {
					node.setNodeValue(text);
				}
			} else if (node!=null && node.getNodeType() == Node.COMMENT_NODE) {
				if (text != null) {
					node.setNodeValue(text);
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
		selectionManager.setActiveNode(this, node);
	}

	private Node[] removeDuplicate(Node[] nodes) {
		Vector v = new Vector();
		for (int i = 0; i < nodes.length; i++) {
			if (v.indexOf(nodes[i]) == -1) {
				v.add(nodes[i]);
				logger.debug("added node");
			} else {
				logger.debug("duplicate node");
			}
		}
		if (v.size() > 0) {
			Node[] ret = new Node[v.size()];
			v.copyInto(ret);
			return ret;
		}
		return null;
	}

	public synchronized void fireSelectedNodesChanged(Node[] nodes) {
		// FIXME patchato con rimozione nodi duplicati
		logger.debug("fired Selected Nodes Changed");
		selectionManager.setSelectedNodes(this, removeDuplicate(nodes));
	}

	public synchronized void fireSelectionChanged(Node node, int start, int end) {
		logger.debug("fired Selection Changed: new selection:" + start + "," + end);
		selectionManager.setSelectedText(this, node, start, end);
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

	public RulesManager getRulesManager() {
		return this.rulesManager;
	}

	public Logger getLogger() {
		return this.logger;
	}

	I18n getI18n() {
		return this.i18n;
	}

	public SelectionManager getSelectionManager() {
		return this.selectionManager;
	}

	EventManager getEventManager() {
		return this.eventManager;
	}

	UtilRulesManager getUtilRulesManager() {
		return this.utilRulesManager;
	}

	public InsertBreakAction getInsertBreakAction() {
		return insertBreakAction;
	}

	public DeleteNextPrevAction getDeleteNextPrevAction() {
		return deleteNextPrevAction;
	}
	
	public KeyTypedAction getKeyTypedAction(){
		return keyTypedAction;
	}

	public AntiAliasedTextPane getTextPane() {
		return textPane;
	}

	private DomEdit[] getEditsToUpdate(DomEdit[] eds, boolean undo) {
		Vector subTrees = new Vector();
		if (undo) {
			for (int i = eds.length - 1; i >= 0; i--)
				if (eds[i].getType() == DomEdit.SUBTREE_MODIFIED)
					subTrees.addElement(eds[i]);
		} else {
			for (int i = 0; i < eds.length; i++)
				if (eds[i].getType() == DomEdit.SUBTREE_MODIFIED)
					subTrees.addElement(eds[i]);
		}
		DomEdit[] ret = new DomEdit[subTrees.size()];
		subTrees.copyInto(ret);
		return ret;
	}

	private synchronized void updateTextPane() {
		textPane.update();
		updated = true;
	}

	public class UpdateTask extends TimerTask {

		public void run() {
			if (textPane.isShowing() && !updated){
				updateTextPane();
				// FIXME Tommaso: l'ho aggiunto io perche' partiva il thread 
				// di aggiornamento del pannello e perdeva il nodo (?)
				// textPane.selectNode(new Node[] { selectionManager.getActiveNode() });
			}
		}

	}

	public String[] getBrowsers() {
		return browsers;
	}
}
