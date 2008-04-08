package it.cnr.ittig.xmleges.editor.blocks.dom.xmleges.linker;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.dom.xmleges.linker.XmLegesLinker;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.xmleges.linker.XmLegesLinker</code>.</h1>
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
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class XmLegesLinkerImpl implements XmLegesLinker, Loggable, Serviceable {
	Logger logger;

	RulesManager rulesManager;

	DocumentManager documentManager;

	NirUtilDom nirUtilDom;

	Rinumerazione rinumerazione;

	Frame frame;

	Node modified = null;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		frame = (Frame) serviceManager.lookup(Frame.class);
	}

	// /////////////////////////////////////////// ParserRiferimenti Interface

	public Node setParsedText(Node nodeSel, String parsedText, String testoPrima, String testoDopo) {

		logger.debug("[ParserRiferimentiImpl: setParsedText" + parsedText);
		if (parsedText == null || parsedText.length() == 0) {
			logger.warn("Lunghezza del risultato da inserire: 0");
			return null;
		}
		try {
			EditTransaction tr = documentManager.beginEdit();

			logger.debug("nodo selezionato:  " + UtilDom.getNodeSummary(nodeSel));
			Document doc = documentManager.getDocumentAsDom();
			parsedText = "<?xml version=\"1.0\" encoding=\""+documentManager.getEncoding()+"\"?>"
					+ "<ris  xmlns:h=\"http://www.w3.org/HTML/1998/html4\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" + parsedText.trim() + "</ris>";

			Node parsedNodeSel = UtilXml.textToXML(parsedText, true).getDocumentElement();
			
			if(logger.isDebugEnabled())
				logger.debug("parsed: " + UtilDom.domToString(parsedNodeSel));

			NodeList nl = parsedNodeSel.getChildNodes();
			Node padre = nodeSel.getParentNode();
			// Modifica Enrico
			// Prendo il nodo successivo al nodo che contiene la selezione
			// per poi inserire al posto del nodo selezione la successione dei
			// nodi creati
			// Node nextSelNode = nodeSel.getNextSibling();
			
			if(logger.isDebugEnabled()){
				logger.debug("------Inizio---------");
				logger.debug(UtilDom.domToString(padre));
			}
			
			if (testoPrima.length() > 0)
				padre.insertBefore(doc.createTextNode(testoPrima), nodeSel);
			
			if(logger.isDebugEnabled()){
				logger.debug("------Dopo inserimento testo prima---------");
				logger.debug(UtilDom.domToString(padre));
			}
			
			UtilDom.trimTextNode(parsedNodeSel, true);

			while (nl.getLength() > 0) {
				Node n = parsedNodeSel.removeChild(nl.item(0));
				Node imp = doc.importNode(n, true);
				padre.insertBefore(imp, nodeSel);
			}

			if(logger.isDebugEnabled()){
				logger.debug("------Dopo inserimento lista nodi risultato---------");
				logger.debug(UtilDom.domToString(padre));
			}
			
			if (testoDopo.length() > 0)
				padre.insertBefore(doc.createTextNode(testoDopo), nodeSel);
			padre.removeChild(nodeSel);

			if(logger.isDebugEnabled()){
				logger.debug("-----Dopo inserimento testo dopo----------");
				logger.debug(UtilDom.domToString(padre));
			}
			
			UtilXml.concatenaTextNode(padre);

			modified = padre;
			
			if(logger.isDebugEnabled()){
				logger.debug("----Dopo concatenaTextNode--------");
				logger.debug(UtilDom.domToString(padre));
			}
			
			// rinumerazione.aggiorna(doc);
			logger.debug("committed edit in setParsedText");
			documentManager.commitEdit(tr);

			return modified;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public Node setParsedDocument(String parsedText) {
		parsedText = "<?xml version=\"1.0\" encoding=\""+documentManager.getEncoding()+"\"?>" + parsedText;
		logger.debug("[ParserRiferimentiImpl: setParsedDocument" + parsedText);
		Document doc = documentManager.getDocumentAsDom();
		try {
			Document d = UtilXml.textToXML(parsedText, true);
			Node imp = doc.importNode(nirUtilDom.getTipoAtto(d),true);
			UtilDom.trimTextNode(imp, true);
			EditTransaction tr = documentManager.beginEdit();
			nirUtilDom.getNIRElement(doc).replaceChild(imp, nirUtilDom.getTipoAtto(doc));
			modified = nirUtilDom.getTipoAtto(doc);
			rinumerazione.aggiorna(doc);
			logger.debug("committed edit in setParsedDocument");
			documentManager.commitEdit(tr);
			frame.reloadAllPanes();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
		return modified;
	}
	


	public Node setParsedNode(Node node, String textRis) {
		logger.debug("[ParserRiferimentiImpl: setParsedNode" + textRis);
		Document doc = documentManager.getDocumentAsDom();
		try {
			EditTransaction tr = documentManager.beginEdit();
			Node parent = node.getParentNode();
			String text = textRis;
			text = "<?xml version=\"1.0\" encoding=\""+documentManager.getEncoding()+"\"?>"
					+ "<ris  xmlns:h=\"http://www.w3.org/HTML/1998/html4\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" + text + "</ris>";
			Node root = UtilXml.textToXML(text, true).getDocumentElement(); 
			// va
			// sotto
			// il
			// tag
			// <ris></ris>
			UtilDom.trimTextNode(root, true);

			NodeList nl = root.getChildNodes();
			Node imp = doc.importNode(root.getFirstChild(), true);
			modified = imp;
			parent.replaceChild(imp, node);
			Node insAfter = imp;
			for (int i = 1; i < nl.getLength(); i++) {
				imp = doc.importNode(nl.item(i), true);
				parent.insertBefore(imp, insAfter.getNextSibling());
				insAfter = imp;
				if (UtilDom.findParentByName(imp, "rif") != null)
					modified = imp;
				// logger.debug("setParsedNode: nl.getLength "+nl.getLength());
			}
			logger.debug("committed edit in setParsedNode");
			documentManager.commitEdit(tr);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return modified;
	}

	// public boolean setParsedNodes(Node[] nodes, String[] textRis){
	// for (int i = 0; i < nodes.length; i++) {
	// if(! setParsedNode(nodes[i], textRis[i]))
	// return(false);
	// }
	// return true;
	// }
	//    
	public Node setParsedNodes(Node[] nodes, String[] textRis) {
		Document doc = documentManager.getDocumentAsDom();
		try {
			EditTransaction tr = documentManager.beginEdit();
			for (int i = 0; i < nodes.length; i++) {
				Node node = nodes[i];
				Node parent = node.getParentNode();
				String text = textRis[i];
				text = "<?xml version=\"1.0\" encoding=\""+documentManager.getEncoding()+"\"?>"
						+ "<ris  xmlns:h=\"http://www.w3.org/HTML/1998/html4\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">" + text + "</ris>";
				Node root = UtilXml.textToXML(text, true).getDocumentElement();

				NodeList nl = root.getChildNodes();
				UtilDom.trimTextNode(root, true);
				parent.removeChild(node);
				for (int j = 0; j < nl.getLength(); j++) {
					Node imp = doc.importNode(nl.item(j), true);
					parent.appendChild(imp);
				}
				modified = parent;
			}

			// rinumerazione.aggiorna(doc);
			documentManager.commitEdit(tr);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return modified;
	}

}
