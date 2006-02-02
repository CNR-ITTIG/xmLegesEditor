package it.cnr.ittig.xmleges.editor.blocks.dom.annessi;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;
import it.cnr.ittig.xmleges.editor.services.dom.annessi.Annessi;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.io.File;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmlegis.editor.services.dom.annessi.Annessi</code>.</h1>
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
public class AnnessiImpl implements Annessi, Loggable, Serviceable {
	Logger logger;

	NirUtilDom nirUtilDom;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	UtilMsg utilMsg;

	UtilRulesManager utilRulesManager;

	Rinumerazione rinumerazione;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
	}

	public void setAnnessoInterno(String denAnnesso, String titAnnesso, String preAnnesso, File annesso) {

		Document doc = documentManager.getDocumentAsDom();
		if (annesso != null && isAllegabile(annesso)) {
			Node testata = creaTestata(doc, denAnnesso, titAnnesso, preAnnesso);
			String urnAnnesso = getUrnAnnesso(doc, denAnnesso, titAnnesso, preAnnesso);
			try {
				EditTransaction tr = documentManager.beginEdit();
				Node annessoInterno = creaAnnessoInterno(doc, urnAnnesso, testata, annesso);
				if (insertAnnesso(doc, annessoInterno)) {
					rinumerazione.aggiorna(doc);
					documentManager.commitEdit(tr);
				} else
					documentManager.rollbackEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.toString() + " DocumentManagerException in Annessi");
			}
		} else {
			logger.error("Impossibile inserire l'annesso");
			utilMsg.msgError("editor.dom.annessi.errore.dtdnoncompatibile");
		}
	}

	public void setAnnessoEsterno(String denAnnesso, String titAnnesso, String preAnnesso) {

		Document doc = documentManager.getDocumentAsDom();

		Node testata = creaTestata(doc, denAnnesso, titAnnesso, preAnnesso);
		String ref = getUrnAnnesso(doc, denAnnesso, titAnnesso, preAnnesso);

		if (ref != null) {
			try {
				EditTransaction tr = documentManager.beginEdit();
				Node annessoEsterno = creaAnnessoEsterno(doc, ref, testata);
				if (insertAnnesso(doc, annessoEsterno)) {
					rinumerazione.aggiorna(doc);
					documentManager.commitEdit(tr);
				} else
					documentManager.rollbackEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.toString() + " DocumentManagerException in Annessi");
			}
		}
		logger.debug("annesso esterno inserito");
	}

	private String getUrnAnnesso(Document documento, String denAnnesso, String titAnnesso, String preAnnesso) {

		String ref = null;
		logger.debug("denAnnesso " + denAnnesso);
		logger.debug("titAnnesso " + titAnnesso);
		logger.debug("preAnnesso " + preAnnesso);

		String denAnnessoUrn = getAnnessoUrn(denAnnesso);
		String titAnnessoUrn = getAnnessoUrn(titAnnesso);
		if (titAnnessoUrn != null)
			denAnnessoUrn = denAnnessoUrn + ";" + titAnnessoUrn;
		String urnBase = getUrnBase(documento).trim();
		if (urnBase != null) {
			ref = urnBase + ":" + denAnnessoUrn;
			logger.info("urn allegato " + ref);
		} else {
			utilMsg.msgError("Specificare la Urn del documento");
		}
		return (ref);
	}

	private String getAnnessoUrn(String annesso) {
		java.util.StringTokenizer tokenizer;
		String urnAnnesso = "";

		if (annesso != null && annesso.trim().length() > 0) {
			tokenizer = new StringTokenizer(annesso.toLowerCase(), " ");
			while (tokenizer.hasMoreTokens()) {
				try {
					urnAnnesso = urnAnnesso + "." + tokenizer.nextToken();
				} catch (Exception ex) {
					logger.error(ex.toString());
				}
			}
			return (urnAnnesso.substring(1).trim());
		}
		return (null);
	}

	private Node creaTestata(Document documento, String denAnnesso, String titAnnesso, String preAnnesso) {

		Node testata = null;

		if (denAnnesso != null || titAnnesso != null || preAnnesso != null) { // c'e'
			// una
			// testata
			testata = utilRulesManager.getNodeTemplate("testata");
			if (denAnnesso != null) {
				Node denAnnessoNode = utilRulesManager.getNodeTemplate("denAnnesso");
				if (utilRulesManager.isDtdBase())
					UtilDom.setTextNode(denAnnessoNode.getFirstChild(), denAnnesso);
				else
					UtilDom.setTextNode(denAnnessoNode, denAnnesso);
				testata.appendChild(denAnnessoNode);
			}
			if (titAnnesso != null && titAnnesso.trim().length() != 0) {
				Node titAnnessoNode = utilRulesManager.getNodeTemplate("titAnnesso");
				if (utilRulesManager.isDtdBase())
					UtilDom.setTextNode(titAnnessoNode.getFirstChild(), titAnnesso);
				else
					UtilDom.setTextNode(titAnnessoNode, titAnnesso);
				testata.appendChild(titAnnessoNode);
			}
			if (preAnnesso != null && preAnnesso.trim().length() != 0) {
				Node preAnnessoNode = utilRulesManager.getNodeTemplate("preAnnesso");
				if (utilRulesManager.isDtdBase())
					UtilDom.setTextNode(preAnnessoNode.getFirstChild(), preAnnesso);
				else
					UtilDom.setTextNode(preAnnessoNode, preAnnesso);
				testata.appendChild(preAnnessoNode);
			}
		}
		return (testata);
	}

	private Node creaAnnessoEsterno(Document documento, String ref, Node testata) {

		Node annesso = utilRulesManager.getNodeTemplate("annesso");
		UtilDom.setAttributeValue(annesso, "id", "ann" + getIdAnnesso(documento));

		NodeList figli = annesso.getChildNodes();
		for (int j = 0; j < figli.getLength(); j++) {
			if (figli.item(j).getNodeName().equals("rifesterno")) {
				UtilDom.setAttributeValue(((Element) figli.item(j)), "xlink:href", ref);
				// ATTENZIONE il contenuto di rifesterno deve essere EMPTY:
				// nessun testo
				// if(ref.startsWith("urn"))
				// figli.item(j).appendChild(documento.createTextNode(UtilityURN.formaSemplif(ref)));
				// else
				// figli.item(j).appendChild(documento.createTextNode(ref));
			}
		}
		Node rifesterno = annesso.getFirstChild();
		if (testata != null)
			annesso.insertBefore(testata, rifesterno);
		return (annesso);
	}

	private Node creaAnnessoInterno(Document documento, String urnAnnesso, Node testata, File fileAnnesso) {

		Node annesso = documento.createElement("annesso");// utilRulesManager.getNodeTemplate("annesso");
		try {
			Document annettere = UtilXml.readXML(fileAnnesso, true);
			if (annettere == null) {
				logger.error("impossibile aprire documento annesso");
				return (null);
			} else {
				// setta la urn dell'annesso
				NodeList urnNodes = annettere.getElementsByTagName("urn");
				UtilDom.setTextNode(urnNodes.item(0), urnAnnesso);
				String idAnnesso = "ann" + getIdAnnesso(documento);
				// annettere.getElementById()
				Node annettere_node = documento.importNode(nirUtilDom.getTipoAtto(annettere), true);
				// FIXME errore nel rulesManager ? canReplace rifEsterno con
				// Legge ?
				// if(utilRulesManager.queryCanReplaceWith(annesso,annesso.getFirstChild(),annettere_node)){
				if (dtdRulesManager.queryCanAppend(annesso, annettere_node)) {
					annesso.appendChild(annettere_node);
					UtilDom.setAttributeValue(annesso, "id", idAnnesso);
				} else {
					return null;
				}
			}
		} catch (Exception exc) {
			logger.error(exc.toString());
		}
		if (testata != null)
			annesso.insertBefore(testata, annesso.getFirstChild());

		return (annesso);

	}

	private boolean isAllegabile(File fileAnnesso) {
		if (documentManager.getDtdName().toLowerCase().startsWith("nirflessibile"))
			return (true);
		Document annettere = UtilXml.readXML(fileAnnesso);
		if (annettere != null) {
			String dtdPath = annettere.getDoctype().getSystemId();
			String[] pathChunks = dtdPath.split("/");
			String currentDTD = pathChunks[pathChunks.length - 1];
			if (currentDTD.equalsIgnoreCase(documentManager.getDtdName()))
				return (true);
		}
		return (false);
	}

	private String getUrnBase(Document documento) {

		NodeList urnNodes = documento.getElementsByTagName("urn");

		for (int i = 0; i < urnNodes.getLength(); i++) {
			// UtilityDOM.getTextNode(urnNodes.item(i)).matches(regex) &&
			if (!isInAnnesso(urnNodes.item(i)))
				return (UtilDom.getTextNode(urnNodes.item(i)));
		}
		return (null);
	}

	private boolean isInAnnesso(Node nodo) {

		while (nodo.getParentNode() != null) {
			if (nodo.getNodeName().equalsIgnoreCase("annesso"))
				return (true);
			nodo = nodo.getParentNode();
		}
		return (false);
	}

	private int getIdAnnesso(Document documento) {
		NodeList annessiList = documento.getElementsByTagName("annesso");
		return (annessiList.getLength() + 1);
	}

	private boolean insertAnnesso(Document documento, Node annesso) {
		if (annesso != null) {
			try {
				NodeList annessiList = documento.getElementsByTagName("annessi");
				if (annessiList.getLength() == 0) {
					Node annessi = utilRulesManager.getNodeTemplate("annessi");
					if (utilRulesManager.queryCanReplaceWith(annessi, annessi.getFirstChild(), annesso)) {
						annessi.replaceChild(annesso, annessi.getFirstChild());
						nirUtilDom.getTipoAtto(documento).appendChild(annessi);
						return true;
					}
					return false;
				} else {
					if (dtdRulesManager.queryCanAppend(annessiList.item(0), annesso)) {
						annessiList.item(0).appendChild(annesso);
						return true;
					}
					return false;
				}
			} catch (DtdRulesManagerException ex) {
				logger.error(ex.getMessage(), ex);
				return false;
			}
		} else
			logger.error("errore nell'apertura del file da allegare");
		return false;
	}

}
