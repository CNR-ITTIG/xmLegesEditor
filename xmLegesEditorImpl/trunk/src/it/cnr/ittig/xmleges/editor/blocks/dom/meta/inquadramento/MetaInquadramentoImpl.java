package it.cnr.ittig.xmleges.editor.blocks.dom.meta.inquadramento;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.InfoMancanti;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.Infodoc;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.MetaInquadramento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.Oggetto;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MetaInquadramentoImpl implements MetaInquadramento, Loggable, Serviceable {

	Logger logger;

	NirUtilDom nirUtilDom;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	//UtilMsg utilMsg;

	UtilRulesManager utilRulesManager;
	
	Rinumerazione rinumerazione; 

	

	public void setInfodoc(Infodoc infodoc) {
		if(infodoc!=null){
			Document doc = documentManager.getDocumentAsDom();
			try {
				EditTransaction tr = documentManager.beginEdit();
				if (setDOMInfodoc(infodoc)) {
					rinumerazione.aggiorna(doc);
					documentManager.commitEdit(tr);
				} else
					documentManager.rollbackEdit(tr);
				} catch (DocumentManagerException ex) {
					logger.error(ex.toString() + " DocumentManagerException in CiclodiVita");
				}
		}
	}
		
		

	private boolean setDOMInfodoc(Infodoc infodoc) {
		Document doc = documentManager.getDocumentAsDom();
		Node inquadramentoNode = doc.getElementsByTagName("inquadramento").item(0);
		
		boolean missingInquadramento = false;
		
		if (inquadramentoNode==null){
			inquadramentoNode = doc.createElement("inquadramento");
			missingInquadramento = true;
		}
		
		Element infodoc_element = doc.createElement("infodoc");
		if((infodoc.getNatura()!=null)&&(!infodoc.getNatura().trim().equals("")))
			infodoc_element.setAttribute("natura", infodoc.getNatura());
		if((infodoc.getNormativa()!=null)&&(!infodoc.getNormativa().trim().equals("")))
			infodoc_element.setAttribute("normativa", infodoc.getNormativa());
		if((infodoc.getFunzione()!=null)&&(!infodoc.getFunzione().trim().equals("")))
			infodoc_element.setAttribute("funzione", infodoc.getFunzione());
		if(infodoc.getFonte()!=null)
			infodoc_element.setAttribute("fonte", infodoc.getFonte());
			
		Node infodocNode = (Node)infodoc_element;
		
		NodeList oldTag = doc.getElementsByTagName("infodoc");
		if (oldTag.getLength() > 0) // c'era gia' un nodo infodoc
			inquadramentoNode.replaceChild(infodocNode, oldTag.item(0));
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,infodocNode);
		
		
		if(missingInquadramento){
			Node metaNode = doc.getElementsByTagName("meta").item(0);
			utilRulesManager.orderedInsertChild(metaNode,inquadramentoNode);
		}
		
		return true;

		
	}



	public void setInfomancanti(InfoMancanti infomancanti) {
		if(infomancanti!=null){
			Document doc = documentManager.getDocumentAsDom();
			try {
				EditTransaction tr = documentManager.beginEdit();
				if (setDOMInfomancanti(infomancanti)) {
					rinumerazione.aggiorna(doc);
					documentManager.commitEdit(tr);
				} else
					documentManager.rollbackEdit(tr);
				} catch (DocumentManagerException ex) {
					logger.error(ex.toString() + " DocumentManagerException in CiclodiVita");
				}
		}


	}

	private boolean setDOMInfomancanti(InfoMancanti infomancanti) {
		Document doc = documentManager.getDocumentAsDom();
		Node inquadramentoNode = doc.getElementsByTagName("inquadramento").item(0);
		
		boolean missingInquadramento = false;
		
		if (inquadramentoNode==null){
			inquadramentoNode = doc.createElement("inquadramento");
			missingInquadramento = true;
		}
		
		Node infomancantiNode = doc.createElement("infomancanti");

		//////////////////
		Element infomancanti_element=null;
		if(infomancanti.getMTitolodoc()!= null && !infomancanti.getMTitolodoc().trim().equals("")){
			infomancanti_element = doc.createElement("mTitolodoc");
			UtilDom.setTextNode(infomancanti_element,infomancanti.getMTitolodoc() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		if(infomancanti.getMTipodoc()!= null && !infomancanti.getMTipodoc().trim().equals("")){
			infomancanti_element = doc.createElement("mTipodoc");
			UtilDom.setTextNode(infomancanti_element,infomancanti.getMTipodoc() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		if(infomancanti.getMDatadoc()!= null && !infomancanti.getMDatadoc().trim().equals("")){
			infomancanti_element = doc.createElement("mDatadoc");
			UtilDom.setTextNode(infomancanti_element,infomancanti.getMDatadoc() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		if(infomancanti.getMNumdoc()!= null && !infomancanti.getMNumdoc().trim().equals("")){
			infomancanti_element = doc.createElement("mNumdoc");
			UtilDom.setTextNode(infomancanti_element,infomancanti.getMNumdoc() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		if(infomancanti.getMEmanante()!= null && !infomancanti.getMEmanante().trim().equals("")){
			infomancanti_element = doc.createElement("mEmanante");
			UtilDom.setTextNode(infomancanti_element,infomancanti.getMEmanante() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		/////////////////////
		
		NodeList oldTag = doc.getElementsByTagName("infomancanti");
		if (oldTag.getLength() > 0) // c'era gia' un nodo infomancanti
			inquadramentoNode.replaceChild(infomancantiNode, oldTag.item(0));
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,infomancantiNode);
		
		
		if(missingInquadramento){
			Node metaNode = doc.getElementsByTagName("meta").item(0);
			utilRulesManager.orderedInsertChild(metaNode,inquadramentoNode);
		}
		
		return true;
	}



	public void setOggetto(Oggetto oggetto) {
		if(oggetto!=null){
			Document doc = documentManager.getDocumentAsDom();
			try {
				EditTransaction tr = documentManager.beginEdit();
				if (setDOMOggetto(oggetto)) {
					rinumerazione.aggiorna(doc);
					documentManager.commitEdit(tr);
				} else
					documentManager.rollbackEdit(tr);
				} catch (DocumentManagerException ex) {
					logger.error(ex.toString() + " DocumentManagerException in CiclodiVita");
				}
		}


	}

	private boolean setDOMOggetto(Oggetto oggetto) {
		Document doc = documentManager.getDocumentAsDom();
		Node inquadramentoNode = doc.getElementsByTagName("inquadramento").item(0);
		
		boolean missingInquadramento = false;
		
		if (inquadramentoNode==null){
			inquadramentoNode = doc.createElement("inquadramento");
			missingInquadramento = true;
		}
		
		Node oggettoNode = doc.createElement("oggetto");
		
		Element oggetto_element=null;
		if(oggetto.getFinalita()!= null && !oggetto.getFinalita().trim().equals("")){
			oggetto_element = doc.createElement("finalita");
			UtilDom.setTextNode(oggetto_element,oggetto.getFinalita() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		if(oggetto.getDestinatario()!= null && !oggetto.getDestinatario().trim().equals("")){
			oggetto_element = doc.createElement("destinatario");
			UtilDom.setTextNode(oggetto_element,oggetto.getDestinatario() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		if(oggetto.getTerritorio()!= null && !oggetto.getTerritorio().trim().equals("")){
			oggetto_element = doc.createElement("territorio");
			UtilDom.setTextNode(oggetto_element,oggetto.getTerritorio() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		if(oggetto.getAttivita()!= null && !oggetto.getAttivita().trim().equals("")){
			oggetto_element = doc.createElement("attivita");
			UtilDom.setTextNode(oggetto_element,oggetto.getAttivita() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		
		
		NodeList oldTag = doc.getElementsByTagName("oggetto");
		if (oldTag.getLength() > 0) // c'era gia' un nodo oggetto
			inquadramentoNode.replaceChild(oggettoNode, oldTag.item(0));
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,oggettoNode);
		
		
		if(missingInquadramento){
			Node metaNode = doc.getElementsByTagName("meta").item(0);
			utilRulesManager.orderedInsertChild(metaNode,inquadramentoNode);
		}
		
		return true;
		
		
	}



	public void setProponenti(String[] proponenti) {
		if((proponenti!=null) && (proponenti.length >0)){
			Document doc = documentManager.getDocumentAsDom();
			try {
				EditTransaction tr = documentManager.beginEdit();
				if (setDOMProponenti(proponenti)) {
					rinumerazione.aggiorna(doc);
					documentManager.commitEdit(tr);
				} else
					documentManager.rollbackEdit(tr);
				} catch (DocumentManagerException ex) {
					logger.error(ex.toString() + " DocumentManagerException in CiclodiVita");
			}
		}


	}

	private boolean setDOMProponenti(String[] proponenti) {
			
		Document doc = documentManager.getDocumentAsDom();
		Node inquadramentoNode = doc.getElementsByTagName("inquadramento").item(0);

		boolean missingInquadramento = false;
		
//		 FIXME: se cancello gli eventi devo cancellare anche le relazioni linkate !!
		
		if (inquadramentoNode==null){
			inquadramentoNode = utilRulesManager.getNodeTemplate("inquadramento");
			missingInquadramento = true;
		}
		Node proponentiNode = doc.createElement("proponenti");
		
		Element proponenti_element=null;
		if(proponenti!= null && proponenti.length>0 ){
			for (int i = 0; i < proponenti.length; i++) {
				proponenti_element = doc.createElement("proponente");
				UtilDom.setTextNode(proponenti_element,proponenti[i] );
				utilRulesManager.orderedInsertChild(proponentiNode,proponenti_element);
			}
		}
		NodeList oldTag = doc.getElementsByTagName("proponenti");
		if (oldTag.getLength() > 0) // c'era gia' un nodo proponenti
			inquadramentoNode.replaceChild(proponentiNode, oldTag.item(0));
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,proponentiNode);
		
		
		if(missingInquadramento){
			Node metaNode = doc.getElementsByTagName("meta").item(0);
			utilRulesManager.orderedInsertChild(metaNode,inquadramentoNode);
		}
		
		return true;
	
	}



	public Infodoc getInfodoc() {
		Document doc = documentManager.getDocumentAsDom();
		String natura=null;		
		String normativa=null;		
		String funzione=null;		
		String fonte=null;

		NodeList infodocList = doc.getElementsByTagName("infodoc");
		if (infodocList.getLength() > 0) {
			Node n = infodocList.item(0);
			natura = n.getAttributes().getNamedItem("natura") != null ? n.getAttributes().getNamedItem("natura").getNodeValue() : null;
			normativa = n.getAttributes().getNamedItem("normativa") != null ? n.getAttributes().getNamedItem("normativa").getNodeValue() : null;
			funzione = n.getAttributes().getNamedItem("funzione") != null ? n.getAttributes().getNamedItem("funzione").getNodeValue() : null;
			fonte = n.getAttributes().getNamedItem("fonte") != null ? n.getAttributes().getNamedItem("fonte").getNodeValue() : null;
		}
		return (new Infodoc(natura, normativa, funzione, fonte));

	}

	public InfoMancanti getInfomancanti() {
		Document doc = documentManager.getDocumentAsDom();
		String mTitolodoc=null;		
		String mTipodoc=null;		
		String mDatadoc=null;		
		String mNumdoc=null;		
		String mEmanante=null;

		NodeList infomancList = doc.getElementsByTagName("infomancanti");
		if (infomancList.getLength() > 0) {
			
			Node n = infomancList.item(0);
			
			NodeList infomanc_elementList = n.getChildNodes();
			for (int i = 0; i < infomanc_elementList.getLength();i++) {
				
				if(infomanc_elementList.item(i).getNodeName().equals("mTitolodoc"))
					mTitolodoc = UtilDom.getTextNode(infomanc_elementList.item(i)) != null ? UtilDom.getTextNode(infomanc_elementList.item(i)) : null;  
				if(infomanc_elementList.item(i).getNodeName().equals("mTipodoc"))
					mTipodoc = UtilDom.getTextNode(infomanc_elementList.item(i)) != null ? UtilDom.getTextNode(infomanc_elementList.item(i)) : null;
				if(infomanc_elementList.item(i).getNodeName().equals("mDatadoc"))
					mDatadoc = UtilDom.getTextNode(infomanc_elementList.item(i)) != null ? UtilDom.getTextNode(infomanc_elementList.item(i)) : null;
				if(infomanc_elementList.item(i).getNodeName().equals("mNumdoc"))
					mNumdoc = UtilDom.getTextNode(infomanc_elementList.item(i)) != null ? UtilDom.getTextNode(infomanc_elementList.item(i)) : null;
				if(infomanc_elementList.item(i).getNodeName().equals("mEmanante"))
					mEmanante = UtilDom.getTextNode(infomanc_elementList.item(i)) != null ? UtilDom.getTextNode(infomanc_elementList.item(i)) : null;
					
			}
			return (new InfoMancanti(mTitolodoc, mTipodoc, mDatadoc, mNumdoc,mEmanante));
		}else return null;
		
		
	}

	public Oggetto getOggetto() {
		Document doc = documentManager.getDocumentAsDom();
		String finalita=null;		
		String destinatario=null;		
		String territorio=null;		
		String attivita=null;


		NodeList oggettoList = doc.getElementsByTagName("oggetto");
		if (oggettoList.getLength() > 0) {
			
			Node n = oggettoList.item(0);
			
			NodeList oggetto_elementList = n.getChildNodes();
			for (int i = 0; i < oggetto_elementList.getLength();i++) {				
				if(oggetto_elementList.item(i).getNodeName().equals("finalita"))
					finalita = UtilDom.getTextNode(oggetto_elementList.item(i)) != null ? UtilDom.getTextNode(oggetto_elementList.item(i)) : null;  
				if(oggetto_elementList.item(i).getNodeName().equals("destinatario"))
					destinatario = UtilDom.getTextNode(oggetto_elementList.item(i)) != null ? UtilDom.getTextNode(oggetto_elementList.item(i)) : null;
				if(oggetto_elementList.item(i).getNodeName().equals("territorio"))
					territorio = UtilDom.getTextNode(oggetto_elementList.item(i)) != null ? UtilDom.getTextNode(oggetto_elementList.item(i)) : null;
				if(oggetto_elementList.item(i).getNodeName().equals("attivita"))
					attivita = UtilDom.getTextNode(oggetto_elementList.item(i)) != null ? UtilDom.getTextNode(oggetto_elementList.item(i)) : null;
									
			}
			
			return (new Oggetto(finalita, destinatario, territorio, attivita));
		}else
			return null;
		
		
		

	}

	public String[] getProponenti() {
		
		Document doc = documentManager.getDocumentAsDom();
		Vector proponentiVect = new Vector();
		NodeList proponentiList = doc.getElementsByTagName("proponenti");
		
		if (proponentiList.getLength() > 0) {
					
			Node n = proponentiList.item(0);			
			NodeList proponenti_elementList = n.getChildNodes();
			for (int i = 0; i < proponenti_elementList.getLength();i++) {
				
				if(proponenti_elementList.item(i).getNodeName().equals("proponente"))
					proponentiVect.add(UtilDom.getTextNode(proponenti_elementList.item(i)) != null ? UtilDom.getTextNode(proponenti_elementList.item(i)) : null);
				}
		}
		
		
		String[] proponenti = new String[proponentiVect.size()];
		proponentiVect.copyInto(proponenti);
		return proponenti;

	}

	public void enableLogging(Logger logger) {
		this.logger = logger;

	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		//utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		
		
	}
	
	/**
	 * Rimuove i tag con un determinato nome
	 */
	private void removeTagByName(String nome) {
		Document doc = documentManager.getDocumentAsDom();
		NodeList list;
		int listLen;
		do {
			list = doc.getElementsByTagName(nome);
			listLen = list.getLength();
			if (listLen > 0) {
				Node currNode = list.item(0);
				currNode.getParentNode().removeChild(currNode);
			}
		} while (listLen > 0);
	}



}
