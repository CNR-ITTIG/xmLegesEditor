package it.cnr.ittig.xmleges.core.blocks.document;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentBeforeInitUndoAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentChangedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.document.DomEdit;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.panes.problems.Problem;
import it.cnr.ittig.xmleges.core.services.panes.problems.ProblemsPane;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.domwriter.DOMWriter;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.events.MutationEventImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * <h1>Implementazione del servizio <code>document-manager</code>.</h1> (<code>it.cnr.ittig.xmleges.editor.services.document.DocumentManager</code>)
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>xerces-2.7.1</li>
 * <li>xml-apis-2.7.1</li>
 * <li>event-manager</li>
 * <li>dtd-rules-manager</li>
 * <li>selection-manager</li>
 * </ul>
 * <h1>I18n</h1>
 * Nessuno.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class DocumentManagerImpl implements DocumentManager, EventListener, Loggable, Serviceable, Initializable, Configurable, ErrorHandler {

	Logger logger;

	DtdRulesManager rulesManager = null;

	EventManager eventManager = null;

	SelectionManager selectionManager = null;

	UtilMsg utilMsg = null;

	ProblemsPane problemsPane = null;

	/** Sorgente del documento. */
	String source = null;

	/** Documento DOM relativo alla sorgente <code>source</code> */
	Document document = null;

	/** Flag per nuovo documento */
	boolean newDoc = true;

	/** Vettore delle modifiche */
	Vector undos;

	/** Indice ultimo undo/modifica */
	int lastUndo;

	/** Indice ultimo salvataggio (setChanged(false)) */
	int lastNotChanged;

	/** Indica se si &egrave; nello stato di undo/redo */
	boolean inUndoRedo;

	/** Dimensione massima del vettore delle modifiche */
	int maxUndos = 100;

	Vector errors = new Vector();

	Vector beforeInitActions = new Vector();

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		rulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		problemsPane = (ProblemsPane) serviceManager.lookup(ProblemsPane.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		initUndo();
	}

	// ///////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		Configuration c = configuration.getChild("maxundos");
		if (c != null) {
			maxUndos = c.getValueAsInteger();
		}
	}

	// /////////////////////////////////////////////// DocumentManager Interface
	public String getSource() {
		return this.source;
	}

	public boolean openSource(String source) {
		return openSource(source, false);
	}

	public boolean openSource(String source, boolean isNew) {
		if (this.document != null)
			close();
		Document doc = open(source);
		if (doc != null) {
			this.document = doc;
			DOMWriter.setDefaultEncoding(getEncoding());

			for (Enumeration en = beforeInitActions.elements(); en.hasMoreElements();) {
				DocumentBeforeInitUndoAction action = (DocumentBeforeInitUndoAction) en.nextElement();
				boolean cont = action.beforeInitUndo(this.document);
				if (cont == false) {
					this.document = null;
					setSourceName(null);
					return false;
				}

			}

			setSourceName(source);
			initUndo();
			NodeImpl n = (NodeImpl) this.document;
			n.addEventListener(MutationEventImpl.DOM_ATTR_MODIFIED, this, true);
			n.addEventListener(MutationEventImpl.DOM_CHARACTER_DATA_MODIFIED, this, true);
			n.addEventListener(MutationEventImpl.DOM_NODE_INSERTED, this, true);
			n.addEventListener(MutationEventImpl.DOM_NODE_REMOVED, this, true);
			n.addEventListener(MutationEventImpl.DOM_SUBTREE_MODIFIED, this, true);
			setNew(isNew);
			eventManager.fireEvent(new DocumentOpenedEvent(this, document));
			if (logger.isDebugEnabled())
				logger.debug("doc opened:" + UtilDom.domToString(document));
			return true;
		} else
			return false;
	}

	public boolean hasErrors() {
		return errors != null && errors.size() > 0;
	}

	public String[] getErrors() {
		String[] ret = new String[errors.size()];
		errors.copyInto(ret);
		return ret;
	}

	public void setSourceName(String source) {
		this.source = source;
	}

	public String getSourceName() {
		return source;
	}

	public void close() {
		if (!isEmpty()) {
			NodeImpl n = (NodeImpl) this.document;
			n.removeEventListener(MutationEventImpl.DOM_ATTR_MODIFIED, this, true);
			n.removeEventListener(MutationEventImpl.DOM_CHARACTER_DATA_MODIFIED, this, true);
			n.removeEventListener(MutationEventImpl.DOM_NODE_INSERTED, this, true);
			n.removeEventListener(MutationEventImpl.DOM_NODE_REMOVED, this, true);
			n.removeEventListener(MutationEventImpl.DOM_SUBTREE_MODIFIED, this, true);
			this.source = null;
			this.document = null;
			eventManager.fireEventSerially(new DocumentClosedEvent(this, document));
		}
	}

	public String getEncoding() {
		DeferredDocumentImpl ddi = (DeferredDocumentImpl) document;
		return ddi != null ? ddi.getXmlEncoding() : null;
	}

	
	
	// FIXME    vedere tutti i posti dove viene chiamato getDtdName()  e correggere; fare un refactor del nome del metodo
	public String getDtdName() {
		Document doc = this.document;
		if (doc != null) {
			String dtdPath = getGrammarPath(doc);//doc.getDoctype().getSystemId();
			String[] pathChunks = dtdPath.split("/");
			String currentDTD = pathChunks[pathChunks.length - 1];
			return (currentDTD);
		}
		return null;
	}

	private String getGrammarPath(Document doc) {
		try{
		if (doc != null){
			return (doc.getDoctype().getSystemId());
		}
		}catch(Exception e){
			return(getSchemaLocation(doc));
		}
		return null;
	}
	
	
		
	private String getSchemaLocation(Document doc){		
		String schemaLoc = UtilDom.getAttributeValueAsString((Node)doc.getDocumentElement(), "xsi:schemaLocation");
		if(schemaLoc==null)
			return null;
	
		String[] elems = schemaLoc.split(" ");
		if(elems.length>0)
			return elems[elems.length-1];
	  
		return null;
	}


	public Document getDocumentAsDom() {
		return this.document;
	}

	public Node getRootElement() {
		return this.document.getDocumentElement();
	}

	public boolean isEmpty() {
		return (this.document == null);
	}

	public void setChanged(boolean changed) {
		if (!changed)
			lastNotChanged = lastUndo;
	}

	public boolean isChanged() {
		return lastNotChanged != lastUndo;
	}

	public void setNew(boolean newDoc) {
		this.newDoc = newDoc;
	}

	public boolean isNew() {
		return this.newDoc;
	}

	// //////////////////////////////////////////////////// GESTIONE UNDO/REDO
	protected void initUndo() {
		this.undos = new Vector(maxUndos);
		this.lastUndo = -1;
		this.lastNotChanged = -1;
		this.inUndoRedo = false;
	}

	public synchronized boolean canUndo() {
		return !isEmpty() && lastUndo >= 0 && undos.size() != 0;
	}

	public synchronized void undo() {
		inUndoRedo = true;
		logger.debug("UNDO: lastUndo=" + lastUndo);
		DomTransactionImpl t = (DomTransactionImpl) undos.get(lastUndo);
		restore(t, true);
		lastUndo--;
		eventManager.fireEvent(new DocumentChangedEvent(this, t, this.document, true));
		inUndoRedo = false;
		setSelection(t);
	}

	public synchronized boolean canRedo() {
		return !isEmpty() && lastUndo + 1 < undos.size();
	}

	public synchronized void redo() {
		inUndoRedo = true;
		lastUndo++;
		logger.debug("REDO: lastUndo=" + lastUndo);
		DomTransactionImpl t = (DomTransactionImpl) undos.get(lastUndo);
		restore(t, false);
		eventManager.fireEvent(new DocumentChangedEvent(this, t, this.document, false));
		inUndoRedo = false;
		setSelection(t);
	}

	protected void setSelection(DomTransactionImpl t) {
		if (t.isTextSelection())
			selectionManager.setSelectedText(this, t.getActiveNode(), t.getTextSelectionStart(), t.getTextSelectionEnd());
		else if (t.getActiveNode() != null)
			selectionManager.setActiveNode(this, t.getActiveNode());
		else if (t.getSelectedNodes() != null)
			selectionManager.setSelectedNodes(this, t.getSelectedNodes());
	}

	public int getUndoSize() {
		return this.maxUndos;
	}

	protected void restore(DomTransactionImpl edits, boolean undo) {
		if (undo)
			for (int i = edits.size() - 1; i >= 0; i--)
				restore((DomEditImpl) edits.get(i), undo);
		else
			for (Enumeration en = edits.elements(); en.hasMoreElements();)
				restore((DomEditImpl) en.nextElement(), undo);
	}

	protected void restore(DomEditImpl edit, boolean undo) {
		switch (edit.getType()) {
		case DomEdit.CHAR_NODE_MODIFIED:
			if (undo)
				edit.getNode().setNodeValue(edit.getTextPrev());
			else
				edit.getNode().setNodeValue(edit.getTextNew());
			break;
		case DomEdit.NODE_INSERTED:
			if (undo)
				remove(edit);
			else
				insert(edit);
			break;
		case DomEdit.NODE_REMOVED:
			if (undo)
				insert(edit);
			else
				remove(edit);
			break;
		case DomEdit.ATTR_NODE_MODIFIED:
			NamedNodeMap attributes = edit.getNode().getAttributes();
			if (undo)
				attributes.getNamedItem(edit.getAttribute()).setNodeValue(edit.getTextPrev());
			else
				attributes.getNamedItem(edit.getAttribute()).setNodeValue(edit.getTextNew());
			break;
		}
	}

	protected void insert(DomEditImpl edit) {
		if (edit.getNextSibling() != null)
			edit.getParentNode().insertBefore(edit.getNode(), edit.getNextSibling());
		else{
			try{
			   edit.getParentNode().appendChild(edit.getNode());
			}
			catch(DOMException ex){
				logger.error(ex.getMessage(),ex);				
			}
		}		
	}

	protected void remove(DomEditImpl edit) {
		try {
			edit.getParentNode().removeChild(edit.getNode());
		} catch (DOMException ex) {
			logger.error(ex.getMessage(),ex);
		}
	}

	// //////////////////////////////////////////////////// GESTIONE TRANSAZIONI
	DomTransactionImpl transaction = null;

	public synchronized EditTransaction beginEdit() throws DocumentManagerException {
		return beginEdit(null);
	}

	public synchronized EditTransaction beginEdit(Object source) throws DocumentManagerException {
		transaction = new DomTransactionImpl(source, selectionManager.getActiveNode(), selectionManager.getSelectedNodes(), selectionManager.isTextSelected(),
				selectionManager.getTextSelectionStart(), selectionManager.getTextSelectionEnd());
		lastUndo++;
		if (lastUndo > maxUndos - 1) {
			undos.remove(0);
			lastUndo--;
			lastNotChanged--;
		}
		undos.add(lastUndo, this.transaction);
		undos.setSize(lastUndo + 1);
		return this.transaction;
	}

	public synchronized void commitEdit(EditTransaction transaction) throws DocumentManagerException {
		logger.debug("commitEDIT  " + transaction.toString());
		// pippo((DomTransactionImpl) transaction);
		logger.debug("commitEDIT  " + transaction.toString());
		Object source = ((DomTransactionImpl) transaction).getSource();
		if (source == null)
			source = this;
		eventManager.fireEvent(new DocumentChangedEvent(source, transaction, this.document, false));
		this.transaction = null;
	}

	public synchronized void rollbackEdit(EditTransaction transaction) {
		logger.debug("rollbacking...");
		if (transaction != this.transaction && canUndo())
			try {
				commitEdit(transaction);
				undo();
				logger.debug("rollbacking OK");
			} catch (DocumentManagerException ex) {
				logger.error(ex.toString(), ex);
			}
		logger.debug("rollbacking KO");
	}

	public void addBeforeInitUndoAction(DocumentBeforeInitUndoAction action) {
		beforeInitActions.addElement(action);
	}

	public void removeBeforeInitUndoAction(DocumentBeforeInitUndoAction action) {
		beforeInitActions.removeElement(action);
	}

	// ///////////////////////////// org.w3c.dom.events.EventListener Interface
	public void handleEvent(Event evt) {
		logger.debug("DOM EVENT START");
		if (inUndoRedo) {
			logger.debug("IN UNDO REDO");
		} else
			try {
				boolean internal = false;
				if (this.transaction == null) {
					beginEdit();
					internal = true;
				}
				DomTransactionImpl edits = (DomTransactionImpl) undos.get(undos.size() - 1);
				edits.addElement(new DomEditImpl((MutationEventImpl) evt));
				if (internal)
					commitEdit(this.transaction);
				if (logger.isDebugEnabled()) {
					logger.debug("added event "+edits.get(edits.size()-1).toString());
					logger.debug("lastUndo=" + lastUndo + " size=" + undos.size());
					logger.debug("bubbling event: " + evt.getBubbles());
					logger.debug("logger debug on DomTransaction: " + edits.toString());
				}
			} catch (DocumentManagerException ex) {
				logger.error(ex.toString(), ex);
				rollbackEdit(this.transaction);
			}
		logger.debug("DOM EVENT END");
	}



	protected Document open(String filename) {
		errors.clear();
		logger.info("Reading file: " + filename);

		File file = new File(filename);
		if (!file.exists() || !file.isFile() || !file.canRead()) {
			logger.warn("Cannot read file: '" + filename + '\'');
			return null;
		}

		Document doc = UtilXml.readXML(file, true, this, true);
		// if (doc == null || errors.size() != 0) {
		if (doc == null) {
			logger.info("Reading file KO");
			// return null;
		} else {
			UtilDom.trimTextNode(doc, true);
			logger.info("Reading rules from DTDs...");
			System.err.println("----------> GRAMMAR PATH:   "+getGrammarPath(doc));
			rulesManager.loadRules(filename, getGrammarPath(doc));
			logger.info("Reading rules OK");
		}
		logger.info("Reading file Ok");
		return doc;
	}

	// ----------- Interface ErrorHandler ------------

	public void warning(SAXParseException ex) throws SAXException {
		errors.add("WARNIG: " + ex.getLocalizedMessage());
		problemsPane.addProblem(new DocumentProblemImpl(Problem.WARNING, ex.getLocalizedMessage()));
		//Gerardo: non esegui il log visto che è già stato segnalato nel pannello dei problemi
		//logger.warn(ex.getMessage());
	}

	public void error(SAXParseException ex) throws SAXException {
		errors.add("ERROR: " + ex.getLocalizedMessage());
		problemsPane.addProblem(new DocumentProblemImpl(Problem.ERROR, ex.getLocalizedMessage()));
		//Gerardo: non esegui il log visto che è già stato segnalato nel pannello dei problemi		
		//logger.error(ex.getMessage());
	}

	public void fatalError(SAXParseException ex) throws SAXException {
		errors.add("FATAL ERROR: " + ex.getLocalizedMessage());
		problemsPane.addProblem(new DocumentProblemImpl(Problem.FATAL_ERROR, ex.getLocalizedMessage()));
		//Gerardo: non esegui il log visto che è già stato segnalato nel pannello dei problemi
		//logger.error(ex.getMessage());
	}

}
