package it.cnr.ittig.xmleges.editor.blocks.dom.rinvii;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.dom.rinvii.Rinvii;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.rinvii.Rinvii</code>.</h1>
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
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */

public class RinviiImpl implements Rinvii, Loggable, Serviceable {

	Logger logger;

	NirUtilDom nirUtilDom;

	NirUtilUrn nirutilurn;

	RulesManager rulesManager;

	DocumentManager documentManager;
	
	UtilRulesManager utilRulesManager;

	Rinumerazione rinumerazione;

	private Node modified = null;
	
	String nirNS = "http://www.normeinrete.it/nir/2.2/";

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		nirutilurn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
	}

	public Node insert(Node node, int start, int end, Urn urn) {
		return insert(node, start, end, urn.toString(), urn.getFormaTestuale().trim());
	}

	public Node insert(Node node, int start, int end, Vector urn, Vector descrizioneMRif) {

		Document doc = documentManager.getDocumentAsDom();

		try {
			EditTransaction tr = documentManager.beginEdit();
			if (insertDOM(node, start, end, urn, descrizioneMRif)) {
				rinumerazione.aggiorna(doc);
				documentManager.commitEdit(tr);
				return modified;
			} else {
				documentManager.rollbackEdit(tr);
				return null;
			}
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public Node insert(Node node, int start, int end, String id, String text) {

		Document doc = documentManager.getDocumentAsDom();

		try {
			EditTransaction tr = documentManager.beginEdit();
			if (insertDOM(node, start, end, id, text)) {
				rinumerazione.aggiorna(doc);
				documentManager.commitEdit(tr);
				return modified;
			} else {
				documentManager.rollbackEdit(tr);
				return null;
			}
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public Node insert(Node node, int start, int end, String[] id, String[] descrizioneMRif) {

		Document doc = documentManager.getDocumentAsDom();

		try {
			EditTransaction tr = documentManager.beginEdit();
			if (insertDOM(node, start, end, id, descrizioneMRif)) {
				rinumerazione.aggiorna(doc);
				documentManager.commitEdit(tr);
				return modified;
			} else {
				documentManager.rollbackEdit(tr);
				return null;
			}
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public Node change(Node node, Urn urn, boolean updateText) {
		return change(node, urn.toString(), urn.getFormaTestuale(),updateText);
	}

	public Node change(Node node, String id, String text, boolean updateText) {
		Document doc = documentManager.getDocumentAsDom();
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (changeDOM(node, id, text,updateText)) {
				rinumerazione.aggiorna(doc);
				documentManager.commitEdit(tr);
				return modified;
			} else {
				documentManager.rollbackEdit(tr);
				return null;
			}
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public Node change(Node node, Vector urn, Vector descrizioneMRif) {

		Urn[] urnArray = new Urn[urn.size()];
		urn.copyInto(urnArray);
		String[] descrizioneArray = new String[descrizioneMRif.size()];
		descrizioneMRif.copyInto(descrizioneArray);

		Document doc = documentManager.getDocumentAsDom();

		try {
			EditTransaction tr = documentManager.beginEdit();
			if (changeDOM(node, urnArray, descrizioneArray)) {
				rinumerazione.aggiorna(doc);
				documentManager.commitEdit(tr);
				return modified;
			} else {
				documentManager.rollbackEdit(tr);
				return null;
			}
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public Node change(Node node, String[] id, String[] descrizioneMRif) {
		Document doc = documentManager.getDocumentAsDom();
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (changeDOM(node, id, descrizioneMRif)) {
				rinumerazione.aggiorna(doc);
				documentManager.commitEdit(tr);
				return modified;
			} else {
				documentManager.rollbackEdit(tr);
				return null;
			}
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		}
	}

	public boolean canInsert(Node node) {
		if (node != null && node.getParentNode() != null && UtilDom.findParentByName(node, "mrif") == null && UtilDom.findParentByName(node, "rif") == null) {
			try {
				return (rulesManager.queryAppendable(node).contains("rif")
						|| rulesManager.queryInsertableInside(node.getParentNode(), node).contains("rif")
						|| rulesManager.queryInsertableAfter(node.getParentNode(), node).contains("rif") || rulesManager.queryInsertableBefore(
						node.getParentNode(), node).contains("rif"));
			} catch (RulesManagerException ex) {
				return false;
			}
		}
		return false;
	}

	public boolean canChange(Node node) {
		if (node != null && node.getParentNode() != null) {
			if (node.getNodeName().equalsIgnoreCase("rif") || node.getNodeName().equalsIgnoreCase("mrif") || UtilDom.findParentByName(node, "rif") != null
					|| UtilDom.findParentByName(node, "mrif") != null) {
				return true;
			}
		}
		return false;
	}

	private Node getInternalMRif(String selectedText, String[] id, String[] descrizioneMRif) {

		int Descrizioneindex = 0;
		String testoMRif = "";

		if (selectedText != null) {
			testoMRif = selectedText;
			for (int i = 0; i < id.length; i++)
				descrizioneMRif[i] = "";
		}
		
		Document document = documentManager.getDocumentAsDom();

		Node newMultiCitazione = UtilDom.createElement(document, "mrif");

		for (int i = 0; i < id.length; i++) {
			Node newCitazione = UtilDom.createElement(document, "rif");
			UtilDom.setAttributeValue(newCitazione, "xlink:href", (id[i]));
			if (descrizioneMRif[Descrizioneindex].trim().length() > 0)
				newCitazione.appendChild(document.createTextNode(descrizioneMRif[Descrizioneindex]));
			Descrizioneindex++;
			if ((Descrizioneindex < descrizioneMRif.length) && (descrizioneMRif[Descrizioneindex].compareTo(",") == 0)) {
				newMultiCitazione.appendChild(newCitazione);
				if (descrizioneMRif[Descrizioneindex].trim().length() > 0)
					newMultiCitazione.appendChild(document.createTextNode(descrizioneMRif[Descrizioneindex]));
				Descrizioneindex++;
			} else if (Descrizioneindex < descrizioneMRif.length) {
				newMultiCitazione.appendChild(newCitazione);
				if (selectedText == null) // se selectedText!=null la forma
											// testuale ? il testo selezionato
											// dal pannello di testo
					newMultiCitazione.appendChild(document.createTextNode(" e "));
			} else
				newMultiCitazione.appendChild(newCitazione);
		}
		if (testoMRif.trim().length() > 0)
			newMultiCitazione.appendChild(document.createTextNode("" + testoMRif));
		return newMultiCitazione;
	}

	private Node getExternalMRif(String selectedText, Urn[] urn, String[] descrizioneMRif) {

		int Descrizioneindex = 0;
		String testoMRif = "";

		String partizionebase = urn[0].getPartizione();
		urn[0].setPartizione(null);
		testoMRif = nirutilurn.getFormaTestuale(urn[0]).trim();
		String urnProvvedimento = urn[0].getProvvedimento();
		if (urnProvvedimento != null) {
			if (urnProvvedimento.startsWith("decreto") || urnProvvedimento.startsWith("codice"))
				testoMRif = "del " + testoMRif;
			else
				testoMRif = "della " + testoMRif;
		}

		urn[0].setPartizione(partizionebase);

		// nel caso di testo selezionato diventa testo dell'Mrif
		if (selectedText != null) {
			testoMRif = selectedText;
			for (int i = 0; i < urn.length; i++)
				descrizioneMRif[i] = "";
		}

		Document document = documentManager.getDocumentAsDom();

		Node newMultiCitazione = UtilDom.createElement(document, "mrif");
		for (int i = 0; i < urn.length; i++) {
			Node newCitazione = UtilDom.createElement(document, "rif");
			UtilDom.setAttributeValue(newCitazione, "xlink:href", urn[i].toString());
			if (descrizioneMRif[Descrizioneindex].trim().length() > 0)
				newCitazione.appendChild(document.createTextNode(descrizioneMRif[Descrizioneindex]));
			Descrizioneindex++;
			if ((Descrizioneindex < descrizioneMRif.length) && (descrizioneMRif[Descrizioneindex].compareTo(",") == 0)) {
				newMultiCitazione.appendChild(newCitazione);
				if (descrizioneMRif[Descrizioneindex].trim().length() > 0)
					newMultiCitazione.appendChild(document.createTextNode(descrizioneMRif[Descrizioneindex]));
				Descrizioneindex++;
			} else if (Descrizioneindex < descrizioneMRif.length) {
				newMultiCitazione.appendChild(newCitazione);
				if (selectedText == null) // se selectedText!=null la forma
											// testuale ? il testo selezionato
											// dal pannello di testo
					newMultiCitazione.appendChild(document.createTextNode(" e "));
			} else
				newMultiCitazione.appendChild(newCitazione);
		}
		if (testoMRif.trim().length() > 0)
			newMultiCitazione.appendChild(document.createTextNode("" + testoMRif));
		return newMultiCitazione;
	}

	private boolean insertDOM(Node node, int start, int end, Vector urn, Vector descrizioneMRif) {

		String testoMRif = null;

		Urn[] urnArray = new Urn[urn.size()];
		urn.copyInto(urnArray);
		String[] descrizioneArray = new String[descrizioneMRif.size()];
		descrizioneMRif.copyInto(descrizioneArray);

		if (node.getNodeValue() != null)
			if (start != end)
				testoMRif = node.getNodeValue().trim().substring(start, end);

		Node newMultiCitazione = getExternalMRif(testoMRif, urnArray, descrizioneArray);

		if (utilRulesManager.insertNodeInText(node, start, end, newMultiCitazione, true)) {
			modified = newMultiCitazione;
			return true;
		}
		return false;
	}



	private boolean insertDOM(Node node, int start, int end, String id, String text) {
		Document doc = documentManager.getDocumentAsDom();

		Node NewCitazione = UtilDom.createElement(doc, "rif");
		UtilDom.setAttributeValue(NewCitazione, "xlink:href", id);
		NewCitazione.appendChild(doc.createTextNode("" + text));

		if (utilRulesManager.insertNodeInText(node, start, end, NewCitazione, true)) {
			modified = NewCitazione;
			return true;
		}
		return false;
	}

	private boolean insertDOM(Node node, int start, int end, String[] id, String[] descrizioneMRif) {

		String testoMRif = null;

		if (node.getNodeValue() != null)
			if (start != end)
				testoMRif = node.getNodeValue().trim().substring(start, end);

		Node newMultiCitazione = getInternalMRif(testoMRif, id, descrizioneMRif);

		if (utilRulesManager.insertNodeInText(node, start, end, newMultiCitazione, true)) {
			modified = newMultiCitazione;
			return true;
		}
		return false;
	}

	private boolean changeDOM(Node node, String id, String text, boolean updateText) {

		Node newCitazione = UtilDom.createElement(documentManager.getDocumentAsDom(), "rif");
		UtilDom.setAttributeValue(newCitazione, "xlink:href", id);
		if(! updateText)
			text = UtilDom.getTextNode(node);
 		UtilDom.setTextNode(newCitazione, text);
		
		node.getParentNode().replaceChild(newCitazione, node);
		modified = newCitazione;

		return true;
	}
	
	private boolean changeDOM(Node node, Urn[] urn, String[] descrizioneMRif) {

		Node newMultiCitazione = getExternalMRif(null, urn, descrizioneMRif);

		node.getParentNode().replaceChild(newMultiCitazione, node);
		modified = newMultiCitazione;

		return true;
	}

	private boolean changeDOM(Node node, String[] id, String[] descrizioneMRif) {

		Node newMultiCitazione = getInternalMRif(null, id, descrizioneMRif);

		node.getParentNode().replaceChild(newMultiCitazione, node);
		modified = newMultiCitazione;

		return true;
	}

}
