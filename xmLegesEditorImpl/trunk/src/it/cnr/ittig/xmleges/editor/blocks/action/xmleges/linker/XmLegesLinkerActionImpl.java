package it.cnr.ittig.xmleges.editor.blocks.action.xmleges.linker;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.xmleges.marker.XmLegesLinkerAction;
import it.cnr.ittig.xmleges.editor.services.dom.xmleges.linker.XmLegesLinker;
import it.cnr.ittig.xmleges.editor.services.form.xmleges.linker.XmLegesLinkerForm;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.AbstractAction;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.parser.riferimenti.RiferimentiAction</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class XmLegesLinkerActionImpl implements XmLegesLinkerAction, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	DocumentManager documentManager;

	SelectionManager selectionManager;

	ParserAction parserAction;

	XmLegesLinkerForm parserRiferimentiForm;

	XmLegesLinker parserRiferimenti;

	UtilMsg utilMsg;

	Node activeNode;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		actionManager = (ActionManager) serviceManager.lookup(ActionManager.class);
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		parserRiferimentiForm = (XmLegesLinkerForm) serviceManager.lookup(XmLegesLinkerForm.class);
		parserRiferimenti = (XmLegesLinker) serviceManager.lookup(XmLegesLinker.class);
		selectionManager = (SelectionManager) serviceManager.lookup(SelectionManager.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		parserAction = new ParserAction();
		actionManager.registerAction("editor.xmleges.linker", parserAction);
		parserAction.setEnabled(false);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		enableActions();
	}

	protected void enableActions() {
		parserAction.setEnabled(!documentManager.isEmpty());
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		parserAction.setEnabled(!documentManager.isEmpty());
		enableActions();
	}

	private void callParser() {

		String text, prima, selected, dopo;
		boolean done = false;
		Node modified = null;

		parserRiferimentiForm.setParseAll(false);

		if (selectionManager.isTextSelected() && selectionManager.getTextSelectionStart() != selectionManager.getTextSelectionEnd()) { // parse
			// del
			// testo
			// selezionato
			logger.debug("[ParserRiferimentiAction: isTextSelected True]");
			Node node = selectionManager.getActiveNode();
			text = node.getNodeValue().trim();
			prima = text.substring(0, selectionManager.getTextSelectionStart()).trim();
			selected = text.substring(selectionManager.getTextSelectionStart(), selectionManager.getTextSelectionEnd()).trim();
			dopo = text.substring(selectionManager.getTextSelectionEnd()).trim();
			logger.debug("testo prima: " + prima + " length " + prima.length());
			logger.debug("testo selected: " + selected + " length " + selected.length());
			logger.debug("testo dopo: " + dopo + " length " + dopo.length());

			if (UtilDom.findParentByName(node, "rif") != null || UtilDom.findParentByName(node, "mrif") != null) {
				utilMsg.msgInfo("editor.form.xmleges.link.msg.warning.insiderif", "editor.form.xmleges.link.text");
				parserRiferimentiForm.setParseAll(true);
			}

			if (parserRiferimentiForm.openForm(selected)) { // openForm selected
				if (parserRiferimentiForm.isParseAll()) {
					if(parserRiferimentiForm.getResultForAll()!=null){
						modified = parserRiferimenti.setParsedDocument(parserRiferimentiForm.getResultForAll());
						done = modified != null;
					}
					else{
						utilMsg.msgInfo("editor.form.xmleges.link.msg.warning.analizza", "editor.form.xmleges.link.text");
						done = true;
					}
				} else {
					if(parserRiferimentiForm.getResultForText()!=null){
						modified = parserRiferimenti.setParsedText(node, parserRiferimentiForm.getResultForText(), prima, dopo);
						done = modified != null;
						logger.debug("modified: " + modified.getNodeName());
					}
					else{
						utilMsg.msgInfo("editor.form.xmleges.link.msg.warning.analizza", "editor.form.xmleges.link.text");
						done = true;
					}
				}
			} else
				done = true;
		} else { // parse dei nodi selezionati
			if (selectionManager.getSelectedNodes().length > 1) {
				logger.debug("[ParserRiferimentiAction: nodi selezionati]");
				if (parserRiferimentiForm.openForm(selectionManager.getSelectedNodes())) {
					if (parserRiferimentiForm.isParseAll()) {
						if(parserRiferimentiForm.getResultForAll()!=null){
							modified = parserRiferimenti.setParsedDocument(parserRiferimentiForm.getResultForAll());
							done = modified != null;
						}
						else{
							utilMsg.msgInfo("editor.form.xmleges.link.msg.warning.analizza", "editor.form.xmleges.link.text");
							done = true;
						}
					} else {
						if(parserRiferimentiForm.getResultForNodes()!=null){
							modified = parserRiferimenti.setParsedNodes(selectionManager.getSelectedNodes(), parserRiferimentiForm.getResultForNodes());
							done = modified != null;
						}
						else{
							utilMsg.msgInfo("editor.form.xmleges.link.msg.warning.analizza", "editor.form.xmleges.link.text");
							done = true;
						}
					}
				} else
					done = true;
			} else { // parse del nondo attivo
				Node node = selectionManager.getActiveNode();
				if (node != null) {
					logger.debug("[ParserRiferimentiAction: openForm con active node]");
					if (UtilDom.findParentByName(node, "rif") != null || UtilDom.findParentByName(node, "mrif") != null) {
						utilMsg.msgInfo("editor.form.xmleges.link.msg.warning.insiderif", "editor.form.xmleges.link.text");
						parserRiferimentiForm.setParseAll(true);
					}
					if (parserRiferimentiForm.openForm(node)) {
						if (parserRiferimentiForm.isParseAll()) {
							if(parserRiferimentiForm.getResultForAll()!=null){
								modified = parserRiferimenti.setParsedDocument(parserRiferimentiForm.getResultForAll());
								done = modified != null;
								logger.debug("modified: " + modified.getNodeName());
							}
							else{
								utilMsg.msgInfo("editor.form.xmleges.link.msg.warning.analizza", "editor.form.xmleges.link.text");
								done = true;
							}
						} else {
							if(parserRiferimentiForm.getResultForNode()!=null){
								modified = parserRiferimenti.setParsedNode(node, parserRiferimentiForm.getResultForNode());
								done = modified != null;
								logger.debug("modified: " + modified.getNodeName());
							}
							else{
								utilMsg.msgInfo("editor.form.xmleges.link.msg.warning.analizza", "editor.form.xmleges.link.text");
								done = true;
							}
						}
					} else
						done = true;
				} else { // forza il parse dell'intero documento
					logger.debug("[ParserRiferimentiAction: nessuna selezione; forza parse intero documento]");
					parserRiferimentiForm.setParseAll(true);
					if (parserRiferimentiForm.openForm(node)) {
						if(parserRiferimentiForm.getResultForAll()!=null){
							modified = parserRiferimenti.setParsedDocument(parserRiferimentiForm.getResultForAll());
							done = modified != null;
						}
						else{
							utilMsg.msgInfo("editor.form.xmleges.link.msg.warning.analizza", "editor.form.xmleges.link.text");
							done = true;
						}
					} else
						done = true;
				}
			}
		}
		if (!done) {
			utilMsg.msgError("editor.form.xmleges.link.msg.error.parser", "editor.form.xmleges.link.text");
		} else {
			if (modified != null) {
				logger.debug("modified: " + modified.getNodeName());
				selectionManager.setActiveNode(this, modified);
				enableActions();
			}
		}
	}

	protected class ParserAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {

			callParser();

		}
	}

}
