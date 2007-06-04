package it.cnr.ittig.xmleges.editor.blocks.action.revisioni;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.action.ActionManager;
import it.cnr.ittig.xmleges.core.services.action.file.save.FileSaveAction;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentOpenedEvent;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.selection.SelectionChangedEvent;
import it.cnr.ittig.xmleges.core.services.selection.SelectionManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.action.file.export.NirFileExportAction;
import it.cnr.ittig.xmleges.editor.services.action.revisioni.RevisioniAction;
import it.cnr.ittig.xmleges.editor.services.dom.partizioni.Partizioni;
import it.cnr.ittig.xmleges.editor.services.dom.revisioni.Revisioni;
import it.cnr.ittig.xmleges.editor.services.form.filenew.FileNewForm;
import it.cnr.ittig.xmleges.editor.services.template.Template;
import it.cnr.ittig.xmleges.editor.services.template.TemplateException;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.EventObject;
import java.util.Properties;

import javax.swing.AbstractAction;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.action.revisioni.RevisioniAction</code>.</h1>
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

public class RevisioniActionImpl implements RevisioniAction, Loggable, EventManagerListener, Serviceable, Initializable {
	Logger logger;

	ActionManager actionManager;

	EventManager eventManager;

	DocumentManager documentManager;

	SelectionManager selectionManager;

	UtilRulesManager utilRulesManager;

	Template template;

	Partizioni partizioni;

	NirFileExportAction export;

	FileNewForm fileNewForm;

	FileSaveAction fileSaveAction;

	Node activeNode;

	UtilMsg utilMsg;

	Revisioni revisioni;

	AbstractAction inserisciAction = new inserisciAction();

	AbstractAction sopprimiAction = new sopprimiAction();

	AbstractAction testoafronteAction = new testoafronteAction();

	AbstractAction passaggioAction = new passaggioAction();

	AbstractAction emendamentiAction = new emendamentiAction();

	int start, end;

	String canInserisci;

	String canSopprimi;

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
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		revisioni = (Revisioni) serviceManager.lookup(Revisioni.class);
		export = (NirFileExportAction) serviceManager.lookup(NirFileExportAction.class);
		fileNewForm = (FileNewForm) serviceManager.lookup(FileNewForm.class);
		fileSaveAction = (FileSaveAction) serviceManager.lookup(FileSaveAction.class);
		template = (Template) serviceManager.lookup(Template.class);
		partizioni = (Partizioni) serviceManager.lookup(Partizioni.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		actionManager.registerAction("editor.revisioni.inserisci", inserisciAction);
		actionManager.registerAction("editor.revisioni.sopprimi", sopprimiAction);
		actionManager.registerAction("editor.revisioni.testoafronte", testoafronteAction);
		actionManager.registerAction("editor.revisioni.messaggio", passaggioAction);
		actionManager.registerAction("editor.revisioni.emendamenti", emendamentiAction);
		eventManager.addListener(this, SelectionChangedEvent.class);
		eventManager.addListener(this, DocumentOpenedEvent.class);
		eventManager.addListener(this, DocumentClosedEvent.class);
		inserisciAction.setEnabled(false);
		sopprimiAction.setEnabled(false);
		testoafronteAction.setEnabled(false);
		passaggioAction.setEnabled(false);
		emendamentiAction.setEnabled(false);
	}

	public void manageEvent(EventObject event) {
		logger.debug("manage Event di revisioniAction");
		if (event instanceof SelectionChangedEvent) {
			logger.debug("selectionChangedEvent in revisioniAction");
			activeNode = ((SelectionChangedEvent) event).getActiveNode();
			start = ((SelectionChangedEvent) event).getTextSelectionStart();
			end = ((SelectionChangedEvent) event).getTextSelectionEnd();
			canInserisci = revisioni.canSetModifica(activeNode, Revisioni.INSERITO, start, end);
			canSopprimi = revisioni.canSetModifica(activeNode, Revisioni.SOPPRESSO, start, end);
			sopprimiAction.setEnabled(canSopprimi != null);
			inserisciAction.setEnabled(canInserisci != null);
			testoafronteAction.setEnabled(revisioni.canTestoaFronte());
			passaggioAction.setEnabled(revisioni.canPassaggio());
			emendamentiAction.setEnabled(revisioni.canEmendamenti());
		} else if (event instanceof DocumentClosedEvent || event instanceof DocumentOpenedEvent) {
			inserisciAction.setEnabled(false);
			sopprimiAction.setEnabled(false);
			if (event instanceof DocumentClosedEvent) {
				testoafronteAction.setEnabled(false);
				passaggioAction.setEnabled(false);
				emendamentiAction.setEnabled(false);
			} else {
				testoafronteAction.setEnabled(revisioni.canTestoaFronte());
				passaggioAction.setEnabled(revisioni.canPassaggio());
				emendamentiAction.setEnabled(revisioni.canEmendamenti());
			}
		}
	}

	protected void setModified(Node modified) {
		if (modified != null) {
			selectionManager.setActiveNode(this, modified);
			activeNode = modified;
			canInserisci = revisioni.canSetModifica(activeNode, Revisioni.INSERITO, -1, -1);
			canSopprimi = revisioni.canSetModifica(activeNode, Revisioni.SOPPRESSO, -1, -1);
			sopprimiAction.setEnabled(canSopprimi != null);
			inserisciAction.setEnabled(canInserisci != null);
		} else
			logger.debug(" modified null in set modified ");
	}

	public void doInserisci() {
		logger.debug("doInserisci: " + canInserisci);
		setModified(revisioni.setModifica(activeNode, start, end, canInserisci));
		testoafronteAction.setEnabled(revisioni.canTestoaFronte());
		emendamentiAction.setEnabled(revisioni.canEmendamenti());
	}

	public void doSopprimi() {
		logger.debug("doSopprimi: " + canSopprimi);
		setModified(revisioni.setModifica(activeNode, start, end, canSopprimi));
		testoafronteAction.setEnabled(revisioni.canTestoaFronte());
		emendamentiAction.setEnabled(revisioni.canEmendamenti());
	}

	public void doTestoaFronte() {
		logger.debug("doTestoAFronte");
		export.doTestoAFronte();
	}

	public void doPassaggio() {

		// TODO: testare un po'meglio, verificare; vanno importate altre info
		// oltre all'articolato? (titolo, legislatura, altro)
		// TODO: nell'esempio alle partizioni soppresse e' stato dato id
		// art12soppresso

		Document oldDoc = documentManager.getDocumentAsDom();

		Node oldTitoloDoc = oldDoc.getElementsByTagName("titoloDoc").getLength() > 0 ? oldDoc.getElementsByTagName("titoloDoc").item(0).cloneNode(true) : null;
		Node oldLegislatura = oldDoc.getElementsByTagName("legislatura").getLength() > 0 ? oldDoc.getElementsByTagName("legislatura").item(0).cloneNode(true)
				: null;
		Node oldNumDoc = oldDoc.getElementsByTagName("numDoc").getLength() > 0 ? oldDoc.getElementsByTagName("numDoc").item(0).cloneNode(true) : null;
		Node oldTipoDoc = oldDoc.getElementsByTagName("tipoDoc").getLength() > 0 ? oldDoc.getElementsByTagName("tipoDoc").item(0).cloneNode(true) : null;
		Node oldEmanante = oldDoc.getElementsByTagName("emanante").getLength() > 0 ? oldDoc.getElementsByTagName("emanante").item(0).cloneNode(true) : null;

		Node oldArticolato = oldDoc.getElementsByTagName("articolato").item(0);
		Node finalArticolato = revisioni.getFinalVersion(oldArticolato.cloneNode(true));

		String dtdName = documentManager.getDtdName();
		int reply = 0;

		if (!documentManager.isEmpty() && documentManager.isChanged() && (reply = utilMsg.msgYesNoCancel("action.file.open.save")) == 1) {
			fileSaveAction.doSaveAs();
			// documentManager.close();
		}
		if (reply != 2) { // non ? stato premuto cancel;
			File templatefile;
			fileNewForm.openForm(dtdName);
			if (fileNewForm.isOKClicked()) {
				try {
					Properties p = fileNewForm.getSelectedDTD();
					p.put("ENCODING","<?xml version=\"1.0\" encoding=\""+documentManager.getEncoding()+"\"?>");

					templatefile = template.getNirTemplate(fileNewForm.getSelectedTemplate(), p);
					documentManager.openSource(templatefile.getAbsolutePath(), true);
					Document newDoc = documentManager.getDocumentAsDom();

					Node titoloDoc = newDoc.getElementsByTagName("titoloDoc").getLength() > 0 ? newDoc.getElementsByTagName("titoloDoc").item(0) : null;
					Node legislatura = newDoc.getElementsByTagName("legislatura").getLength() > 0 ? newDoc.getElementsByTagName("legislatura").item(0) : null;
					Node numDoc = newDoc.getElementsByTagName("numDoc").getLength() > 0 ? newDoc.getElementsByTagName("numDoc").item(0) : null;
					Node tipoDoc = newDoc.getElementsByTagName("tipoDoc").getLength() > 0 ? newDoc.getElementsByTagName("tipoDoc").item(0) : null;
					Node emanante = newDoc.getElementsByTagName("emanante").getLength() > 0 ? newDoc.getElementsByTagName("emanante").item(0) : null;

					if (titoloDoc != null && oldTitoloDoc != null) {
						titoloDoc.getParentNode().replaceChild(newDoc.importNode(oldTitoloDoc, true), titoloDoc);
					}
					if (legislatura != null && oldLegislatura != null) {
						legislatura.getParentNode().replaceChild(newDoc.importNode(oldLegislatura, true), legislatura);
					}
					if (numDoc != null && oldNumDoc != null) {
						numDoc.getParentNode().replaceChild(newDoc.importNode(oldNumDoc, true), numDoc);
					}
					if (tipoDoc != null && oldTipoDoc != null) {
						tipoDoc.getParentNode().replaceChild(newDoc.importNode(oldTipoDoc, true), tipoDoc);
					}
					if (emanante != null && oldEmanante != null) {
						emanante.getParentNode().replaceChild(newDoc.importNode(oldEmanante, true), emanante);
					}

					Node articolato = newDoc.getElementsByTagName("articolato").item(0);
					articolato.getParentNode().replaceChild(newDoc.importNode(finalArticolato, true), articolato);
					documentManager.setChanged(true);
					// return true;
				} catch (TemplateException e) {
					logger.error("Unable to open template file " + fileNewForm.getSelectedTemplate());
					// return false;
				}
			}
		}
		logger.debug("doMessaggio");
	}

	public void doEmendamenti() {

		Document oldDoc = documentManager.getDocumentAsDom();
		File templatefile;

		Node oldTitoloDoc = oldDoc.getElementsByTagName("titoloDoc").getLength() > 0 ? oldDoc.getElementsByTagName("titoloDoc").item(0).cloneNode(true) : null;
		Node oldLegislatura = oldDoc.getElementsByTagName("legislatura").getLength() > 0 ? oldDoc.getElementsByTagName("legislatura").item(0).cloneNode(true)
				: null;
		Node oldEmanante = oldDoc.getElementsByTagName("emanante").getLength() > 0 ? oldDoc.getElementsByTagName("emanante").item(0).cloneNode(true) : null;
		Node oldNumDoc = oldDoc.getElementsByTagName("numDoc").getLength() > 0 ? oldDoc.getElementsByTagName("numDoc").item(0).cloneNode(true) : null;
		Node oldTipoDoc = oldDoc.getElementsByTagName("tipoDoc").getLength() > 0 ? oldDoc.getElementsByTagName("tipoDoc").item(0).cloneNode(true) : null;

		String numDoc = oldNumDoc != null && UtilDom.getText(oldNumDoc) != null ? "n. " + UtilDom.getText(oldNumDoc) : "n.";
		String tipoDoc = oldTipoDoc != null && UtilDom.getText(oldTipoDoc) != null ? UtilDom.getText(oldTipoDoc) : "";
		String leg = oldLegislatura != null && UtilDom.getText(oldLegislatura) != null ? UtilDom.getText(oldLegislatura) : "";

		
		Properties prop = new Properties();
		prop.put("DOCTYPE", "<!DOCTYPE NIR SYSTEM \"" + documentManager.getDtdName() + "\">");
		prop.put("ENCODING","<?xml version=\"1.0\" encoding=\""+documentManager.getEncoding()+"\"?>");

		int reply = 0;

		if (!documentManager.isEmpty() && documentManager.isChanged() && (reply = utilMsg.msgYesNoCancel("action.file.open.save")) == 1) {
			fileSaveAction.doSave();
		}

		if (reply != 2) { // Non è stato premuto Cancel
			try {
				templatefile = template.getNirTemplate("DDLEmendamenti.xml", prop);
				documentManager.openSource(templatefile.getAbsolutePath(), true);
				Document newDoc = documentManager.getDocumentAsDom();
				Node[] emendamenti = revisioni.getEmendamenti(oldDoc, newDoc);

				Node titoloDoc = newDoc.getElementsByTagName("titoloDoc").getLength() > 0 ? newDoc.getElementsByTagName("titoloDoc").item(0) : null;
				Node legislatura = newDoc.getElementsByTagName("legislatura").getLength() > 0 ? newDoc.getElementsByTagName("legislatura").item(0) : null;
				Node emanante = newDoc.getElementsByTagName("emanante").getLength() > 0 ? newDoc.getElementsByTagName("emanante").item(0) : null;

				if (titoloDoc != null && oldTitoloDoc != null) {
					titoloDoc.getParentNode().replaceChild(newDoc.importNode(oldTitoloDoc, true), titoloDoc);
					titoloDoc = newDoc.getElementsByTagName("titoloDoc").item(0);
					UtilDom.setTextNode(titoloDoc, "Emendamenti al " + tipoDoc + " " + numDoc + " " + leg + " \"" + UtilDom.getTextNode(titoloDoc));
					Node lastChild = titoloDoc.getLastChild();
					if (UtilDom.isTextNode(lastChild)) {
						lastChild.setNodeValue(lastChild.getNodeValue() + "\"");
					} else {
						lastChild = newDoc.createTextNode("\"");
						titoloDoc.appendChild(lastChild);
					}
				}
				if (legislatura != null && oldLegislatura != null) {
					legislatura.getParentNode().replaceChild(newDoc.importNode(oldLegislatura, true), legislatura);
				}
				if (emanante != null && oldEmanante != null) {
					emanante.getParentNode().replaceChild(newDoc.importNode(oldEmanante, true), emanante);
				}

				if (null != emendamenti && emendamenti.length > 0) {
					// inserimento degli emendamenti come commi del nuovo
					// documento;
					Node firstComma = newDoc.getElementsByTagName("comma").item(0);
					Node firstCorpo = UtilDom.findDirectChild(firstComma, "corpo");
					firstCorpo.getParentNode().replaceChild(UtilDom.findDirectChild(emendamenti[0], "corpo"), firstCorpo);
					Node prev = firstComma;

					for (int i = 1; i < emendamenti.length; i++) {
						partizioni.nuovaPartizione(prev, emendamenti[i]);
						prev = emendamenti[i];
					}
					documentManager.setChanged(true);
				}
			} catch (TemplateException e) {
				logger.error("Unable to open template file ");
			}
		}
		logger.debug("doEmendamenti");
	}

	// /////////////////////////////////////////////// Azioni
	public class inserisciAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doInserisci();
		}
	}

	// /////////////////////////////////////////////// Azioni
	public class sopprimiAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doSopprimi();
		}
	}

	public class testoafronteAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doTestoaFronte();
		}
	}

	public class passaggioAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doPassaggio();
		}
	}

	public class emendamentiAction extends AbstractAction {

		public void actionPerformed(ActionEvent e) {
			doEmendamenti();
		}
	}

}
