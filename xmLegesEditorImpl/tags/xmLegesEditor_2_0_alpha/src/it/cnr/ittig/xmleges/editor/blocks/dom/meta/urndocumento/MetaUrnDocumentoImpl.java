package it.cnr.ittig.xmleges.editor.blocks.dom.meta.urndocumento;


import java.text.ParseException;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.date.UtilDate;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.autorita.Autorita;
import it.cnr.ittig.xmleges.editor.services.autorita.Istituzione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.urndocumento.MetaUrnDocumento;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.Provvedimenti;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.ProvvedimentiItem;
import it.cnr.ittig.xmleges.editor.services.provvedimenti.TemplateItem;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.Urn;

public class MetaUrnDocumentoImpl implements MetaUrnDocumento, Loggable, Serviceable {

	Logger logger;

	DocumentManager documentManager;

	UtilRulesManager utilRulesManager;
	
	Rinumerazione rinumerazione; 
	
	NirUtilDom nirUtilDom;	
	
	RulesManager rulesManager;
	
	Autorita regAutorita;
	
	Provvedimenti provvedimenti;

	public void enableLogging(Logger logger) {
		this.logger = logger;

	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);		
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		provvedimenti = (Provvedimenti) serviceManager.lookup(Provvedimenti.class);
		regAutorita = (Autorita) serviceManager.lookup(Autorita.class);	
	}
	
	
	public boolean setUrnOnDocument(Document doc, Urn[] urn, boolean updateIntestazione, boolean updateEmananti) {

		EditTransaction tr = null;
		
		//sulle urn lasciare ancora che si riferisca al documento principale  [item(0)]
		Node descrittori = doc.getElementsByTagName("descrittori").item(0);
		
		// TODO chiedere qualcosa al rulesManager ?
		if (descrittori != null) {
			try {
				tr = documentManager.beginEdit();
				Node urnNode = UtilDom.checkAndCreate(descrittori, "urn");
				UtilDom.setAttributeValue(urnNode,"valore", urn[0].toString());

				Node[] otherUrn = UtilDom.getAllNextSibling(urnNode);
				for (int i = 0; i < otherUrn.length; i++) {
					if (otherUrn[i].getNodeName().equals("urn"))
						descrittori.removeChild(otherUrn[i]);
				}

				Node next = urnNode;
				for (int i = 1; i < urn.length; i++) {
					urnNode = UtilDom.createElement(doc,"urn");
					UtilDom.setAttributeValue(urnNode,"valore", urn[i].toString());
					UtilDom.insertAfter(urnNode, next);
					next = urnNode;
				}

				Node intestazione = null;
				Node atto = nirUtilDom.getTipoAtto(doc);
				if (atto != null)
					intestazione = UtilDom.checkAndCreate(atto, "intestazione");

				// AGGIORNAMENTO DELL'INTESTAZIONE
				if (intestazione != null && updateIntestazione)
					updateIntestazione(intestazione,urn);
				
				// AGGIORNAMENTO DEGLI EMANANTI ////////////////
				if (intestazione != null && updateEmananti) 
					updateEmananti(doc,intestazione,urn);

				documentManager.commitEdit(tr);
				return true;
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				documentManager.rollbackEdit(tr);
				return false;
			}
		}
		return false;
	}
	
	
	
	private void updateIntestazione(Node intestazione, Urn[] urn){
		String dataDoc = null;
		String extendedDataDoc = null;
		String numDoc = null;
		if (null != urn[0].getDate() && urn[0].getDate().size() > 0) {
			dataDoc = UtilDate.urnFormatToNorm((String) urn[0].getDate().get(0));
			extendedDataDoc = (String) urn[0].getExtendedDate().get(0);
		}

		if (null != urn[0].getNumeri() && urn[0].getNumeri().size() > 0)
			numDoc = (String) urn[0].getNumeri().get(0);

		if (intestazione != null) {
			Node numDocNode = UtilDom.checkAndCreate(intestazione, "numDoc");
			Node dataDocNode = UtilDom.checkAndCreate(intestazione, "dataDoc");
			if (dataDoc != null && extendedDataDoc != null) {
				UtilDom.setAttributeValue(dataDocNode, "norm", dataDoc);
				UtilDom.setTextNode(dataDocNode, extendedDataDoc);
			}
			if (numDoc != null)
				UtilDom.setTextNode(numDocNode, numDoc);
		}
	}
	
	// TODO raffinare qui la selezione degli emananti eliminando doc.getElementsByTagName("emanante"); 
	
	private void updateEmananti(Document doc, Node intestazione, Urn[] urn){
		boolean needsEmanante = false;
		ProvvedimentiItem prov = identifyProvvedimento(doc);
		if (prov != null)
			needsEmanante = prov.getEmananti();

		if (needsEmanante) {
			Node newEmanante = null;
			Node emanante = UtilDom.findDirectChild(intestazione, "emanante");
			if (emanante == null) { // non ci sono emananti
				emanante = UtilDom.checkAndCreate(intestazione, "emanante");
			} else { // ci sono gia' degli emananti
				NodeList emanantiList = doc.getElementsByTagName("emanante");
				emanante = emanantiList.item(emanantiList.getLength() - 1);
				newEmanante = UtilDom.createElement(doc,"emanante");
				UtilDom.insertAfter(newEmanante, emanante);
				emanante = newEmanante;
			}

			for (int i = 0; i < urn.length; i++) {
				Vector auto = urn[i].getAutorita();
				for (int j = 0; j < auto.size(); j++) {
					// inserirlo solo se non e' presente
					String newAutorita = regAutorita.getNomeIstituzioneFromUrnIstituzione((String) auto.get(j));
					if (!findEmanante(doc, newAutorita)) {
						UtilDom.setTextNode(emanante, newAutorita);
						newEmanante = UtilDom.createElement(doc,"emanante");
						UtilDom.insertAfter(newEmanante, emanante);
						emanante = newEmanante;
					}
				}
			}
			intestazione.removeChild(emanante);
			removeEmananteTemplate(intestazione);
		}
	}

	
	// rimuove dal documento l'emanante eventualmente presente nel template del documento
	
	private void removeEmananteTemplate(Node emananteParent) {
		NodeList children = emananteParent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals("emanante") && UtilDom.getTextNode(children.item(i)).indexOf("[") != -1) {
				try {
					if (rulesManager.queryCanDelete(emananteParent, children.item(i)))
						emananteParent.removeChild(children.item(i));
				} catch (RulesManagerException e) {
				}
			}
		}
	}

	private boolean findEmanante(Document doc, String autorita) {
		if (doc != null) {
			// FIXME andrebbe cercato solo nel documento principale
			NodeList emanantiList = doc.getElementsByTagName("emanante");
			for (int i = 0; i < emanantiList.getLength(); i++) {
				if (null != UtilDom.getTextNode(emanantiList.item(i)) && UtilDom.getTextNode(emanantiList.item(i)).trim().equalsIgnoreCase(autorita.trim()))
					return true;
			}
		}
		return false;
	}

	

	private ProvvedimentiItem identifyProvvedimento(Document doc) {

		String tipoDoc = UtilDom.getTextNode(doc.getElementsByTagName("tipoDoc").item(0));
		String tag = nirUtilDom.getTipoAtto(doc).getNodeName();

		ProvvedimentiItem prov = null;
		if (tipoDoc != null && tipoDoc.trim().length() > 0) {
			prov = provvedimenti.getProvvedimentoByName(tipoDoc);
			if (prov == null)   // se non capisce il tipo di provvedimento dal tipoDoc lo cerca in base alla urn
				prov = provvedimenti.getProvvedimentoByUrn(tipoDoc);
		}
        // se non lo capisce dal tipoDoc lo cerca in base al tag
		if (prov == null && provvedimenti.getProvvedimentiByTag(tag).length > 0)
			prov = provvedimenti.getProvvedimentiByTag(tag)[0];
		return prov;
	}

	
	private boolean isRequiredNumero(String tag) {

		ProvvedimentiItem[] prList = provvedimenti.getProvvedimentiByTag(tag);
		boolean ret = false;
		if (prList.length > 0) {
			TemplateItem[] tempList = prList[0].getTemplateList();
			for (int i = 0; i < tempList.length; i++) {
				if (tempList[i].isNumerato())
					ret = true;
			}
		}
		return ret;
	}
	
	///////////////////////////////////////////////
	//                GET
	///////////////////////////////////////////////
	/**
	 * Restituisce le urn presenti nel documento
	 * 
	 * @param doc
	 * @return urn del documento
	 */
	public Urn[] getUrnFromDocument(Document doc) {

		Vector v = new Vector();
		Urn urnDoc, urnFromDoc;

		urnFromDoc = createUrnFromDocument(doc);

		NodeList urn = doc.getElementsByTagName("urn");
		for (int i = 0; i < urn.getLength(); i++) {
			try {
				if (UtilDom.findParentByName(urn.item(i), "annessi") == null) {
					urnDoc = new Urn(UtilDom.getAttributeValueAsString(urn.item(i),"valore"));

					if (null != urnDoc.getDate() && urnDoc.getDate().size() > 0 && ((String) urnDoc.getDate().get(0)).startsWith("aaaa"))
						if (urnFromDoc.getDate() != null)
							urnDoc.setUniqueData((String) urnFromDoc.getDate().get(0));

					if (null != urnDoc.getNumeri() && urnDoc.getNumeri().size() > 0 && ((String) urnDoc.getNumeri().get(0)).startsWith("nn"))
						if (urnFromDoc.getNumeri() != null)
							urnDoc.setUniqueNumero((String) urnFromDoc.getNumeri().get(0));

					if (urnDoc.isValid())
						v.add(urnDoc);
				}
			} catch (ParseException e) {
				logger.error("urn parse error in getUrnFromDocument");
			}
		}
		if (v.size() == 0) // se non trova nessuna urn valida nel documento cerca di crearla sulla base degli altri dati
			v.add(urnFromDoc);
		Urn[] ret = new Urn[v.size()];
		v.copyInto(ret);
		return ret;
	}
	
	/**
	 * Restituisce la urn del documento costruita in base alle informazioni
	 * recuperabili dal documento: tipo, numero, data
	 * 
	 * @param doc
	 * @return urn del documento
	 */
	private Urn createUrnFromDocument(Document doc) {

		String dataDoc = doc.getElementsByTagName("dataDoc").getLength() > 0 ? UtilDom.getAttributeValueAsString(doc.getElementsByTagName("dataDoc").item(0),
				"norm").trim() : null;
		String numDoc = doc.getElementsByTagName("numDoc").getLength() > 0 ? UtilDom.getTextNode(doc.getElementsByTagName("numDoc").item(0)) : null;
		String tag = nirUtilDom.getTipoAtto(doc).getNodeName();

		String data;
		String provvedimento = " ";
		String autorita = " ";
		String numero = " ";

		Urn ret = null;

		// //////////////////// RECUPERO DATA
		if (dataDoc != null && dataDoc.trim().length() > 0) {
			data = dataDoc;
		} else
			data = UtilDate.dateToNorm(UtilDate.getCurrentDate());
        
		ProvvedimentiItem prov = identifyProvvedimento(doc);

		if (prov != null) {
			provvedimento = prov.getUrnAtto()!=null?prov.getUrnAtto():provvedimento;
			Istituzione[] ist = regAutorita.getIstituzioniValideFromProvvedimenti(data, prov.getUrnAutorita());
			if (ist!=null && ist.length == 1) // una sola possibile autorita emenante
				autorita = ist[0].getUrn();
		}

		// ////////////////////RECUPERO NUMDoc
		if (numDoc != null && numDoc.trim().length() > 0) {
			numero = numDoc;
		} else {
			if (isRequiredNumero(tag))
				numero = "da assegnare";
			else
				numero = "nir-da assegnare";
		}

		try {
			ret = new Urn("urn:nir:" + autorita + ":" + provvedimento + ":" + UtilDate.normToUrnFormat(data) + ";" + numero);
		} catch (ParseException e) {
			return null;
		}
		return ret;
	}

}
