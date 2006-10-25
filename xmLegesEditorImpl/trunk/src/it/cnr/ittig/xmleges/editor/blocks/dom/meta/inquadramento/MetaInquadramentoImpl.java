package it.cnr.ittig.xmleges.editor.blocks.dom.meta.inquadramento;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
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

	DocumentManager documentManager;

	UtilRulesManager utilRulesManager;
	
	Rinumerazione rinumerazione; 
	
	NirUtilDom nirUtilDom;	
	
	Node node=null;

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
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		Node[] inquadramentoNodes = UtilDom.getElementsByTagName(doc,activeMeta,"inquadramento");
		Node inquadramentoNode=inquadramentoNodes.length>0?inquadramentoNodes[0]:null;
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
		
		Node[] oldTags = UtilDom.getElementsByTagName(doc,activeMeta,"infodoc");
		if (oldTags.length > 0) // c'era gia' un nodo infodoc
			inquadramentoNode.replaceChild(infodocNode, oldTags[0]);
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,infodocNode);
		
		
		if(missingInquadramento){
			Node metaNode = UtilDom.getElementsByTagName(doc,activeMeta,"meta")[0];
			utilRulesManager.orderedInsertChild(metaNode,inquadramentoNode);
		}
		
		return true;


//FIXME: chiedere a tommaso la differenza fra findRecursiveChild e getElementsByTagName
//inquadramento non scrive piu i dati sul dom!!!!da finire di correggere
//FIXME: se metto il getnodetemplate AL POSTO DI DOC.CREATE non funziona piu perche?		
		
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
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		
		Node[] inquadramentoNodes = UtilDom.getElementsByTagName(doc,activeMeta,"inquadramento");
		Node inquadramentoNode=inquadramentoNodes.length>0?inquadramentoNodes[0]:null;
		boolean missingInquadramento = false;
		
		if (inquadramentoNode==null){
			inquadramentoNode = doc.createElement("inquadramento");
			missingInquadramento = true;
		}
		
		Node infomancantiNode = utilRulesManager.getNodeTemplate("infomancanti");

		//////////////////
		Element infomancanti_element=null;
		if(infomancanti.getMTitolodoc()!= null && !infomancanti.getMTitolodoc().trim().equals("")){
			infomancanti_element = (Element) utilRulesManager.getNodeTemplate("mTitolodoc");
			UtilDom.setAttributeValue(infomancanti_element,"value",infomancanti.getMTitolodoc());			
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
	
		}
		if(infomancanti.getMTipodoc()!= null && !infomancanti.getMTipodoc().trim().equals("")){
			infomancanti_element = (Element) utilRulesManager.getNodeTemplate("mTipodoc");
			UtilDom.setAttributeValue(infomancanti_element,"value",infomancanti.getMTipodoc() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		if(infomancanti.getMDatadoc()!= null && !infomancanti.getMDatadoc().trim().equals("")){
			infomancanti_element = (Element) utilRulesManager.getNodeTemplate("mDatadoc");
			UtilDom.setAttributeValue(infomancanti_element,"value",infomancanti.getMDatadoc() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		if(infomancanti.getMNumdoc()!= null && !infomancanti.getMNumdoc().trim().equals("")){
			infomancanti_element = (Element) utilRulesManager.getNodeTemplate("mNumdoc");
			UtilDom.setAttributeValue(infomancanti_element,"value",infomancanti.getMNumdoc() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		if(infomancanti.getMEmanante()!= null && !infomancanti.getMEmanante().trim().equals("")){
			infomancanti_element = (Element) utilRulesManager.getNodeTemplate("mEmanante");
			UtilDom.setAttributeValue(infomancanti_element,"value",infomancanti.getMEmanante() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		/////////////////////
		Node[] oldTags = UtilDom.getElementsByTagName(doc,activeMeta,"infomancanti");
		if (oldTags.length > 0) // c'era gia' un nodo infomancanti
			inquadramentoNode.replaceChild(infomancantiNode, oldTags[0]);
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,infomancantiNode);
		
		if(missingInquadramento){
			Node metaNode = UtilDom.getElementsByTagName(doc,activeMeta,"meta")[0];
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
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		Node[] inquadramentoNodes = UtilDom.getElementsByTagName(doc,activeMeta,"inquadramento");
		Node inquadramentoNode=inquadramentoNodes.length>0?inquadramentoNodes[0]:null;
		boolean missingInquadramento = false;
		
		if (inquadramentoNode==null){
			inquadramentoNode = doc.createElement("inquadramento");
			missingInquadramento = true;
		}
		
		Node oggettoNode = utilRulesManager.getNodeTemplate("oggetto");
		
		Element oggetto_element=null;
		if(oggetto.getFinalita()!= null && !oggetto.getFinalita().trim().equals("")){
			oggetto_element = (Element) utilRulesManager.getNodeTemplate("finalita");
			UtilDom.setAttributeValue(oggetto_element,"value",oggetto.getFinalita() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		if(oggetto.getDestinatario()!= null && !oggetto.getDestinatario().trim().equals("")){
			oggetto_element = (Element) utilRulesManager.getNodeTemplate("destinatario");
			UtilDom.setAttributeValue(oggetto_element,"value",oggetto.getDestinatario() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		if(oggetto.getTerritorio()!= null && !oggetto.getTerritorio().trim().equals("")){
			oggetto_element = (Element) utilRulesManager.getNodeTemplate("territorio");
			UtilDom.setAttributeValue(oggetto_element,"value",oggetto.getTerritorio() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		if(oggetto.getAttivita()!= null && !oggetto.getAttivita().trim().equals("")){
			oggetto_element = (Element) utilRulesManager.getNodeTemplate("attivita");
			UtilDom.setAttributeValue(oggetto_element,"value",oggetto.getAttivita() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		
		Node[] oldTags = UtilDom.getElementsByTagName(doc,activeMeta,"oggetto");
		if (oldTags.length > 0) // c'era gia' un nodo oggetto
			inquadramentoNode.replaceChild(oggettoNode, oldTags[0]);
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,oggettoNode);
		
		
		if(missingInquadramento){
			Node metaNode = UtilDom.getElementsByTagName(doc,activeMeta,"meta")[0];
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
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		Node[] inquadramentoNodes = UtilDom.getElementsByTagName(doc,activeMeta,"inquadramento");
		Node inquadramentoNode=inquadramentoNodes.length>0?inquadramentoNodes[0]:null;
		boolean missingInquadramento = false;
		
		if (inquadramentoNode==null){
			inquadramentoNode = doc.createElement("inquadramento");
			missingInquadramento = true;
		}
		
		Node proponentiNode = utilRulesManager.getNodeTemplate("proponenti");
		
		Element proponenti_element=null;
		if(proponenti!= null && proponenti.length>0 ){
			for (int i = 0; i < proponenti.length; i++) {
				proponenti_element =  (Element) utilRulesManager.getNodeTemplate("proponente");
				UtilDom.setAttributeValue(proponenti_element,"value",proponenti[i] );
				utilRulesManager.orderedInsertChild(proponentiNode,proponenti_element);
			}
		}
		
		Node[] oldTags = UtilDom.getElementsByTagName(doc,activeMeta,"proponenti");
		if (oldTags.length > 0) // c'era gia' un nodo proponenti
			inquadramentoNode.replaceChild(proponentiNode, oldTags[0]);
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,proponentiNode);
		
		
		if(missingInquadramento){
			Node metaNode = UtilDom.getElementsByTagName(doc,activeMeta,"meta")[0];
			utilRulesManager.orderedInsertChild(metaNode,inquadramentoNode);
		}
		
				
		return true;
	
	}



	public Infodoc getInfodoc() {
		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		
		String natura=null;		
		String normativa=null;		
		String funzione=null;		
		String fonte=null;

		
		Node[] infodocList = UtilDom.getElementsByTagName(doc,activeMeta,"infodoc");
		if (infodocList.length > 0) {
			Node n = infodocList[0];
			natura = n.getAttributes().getNamedItem("natura") != null ? n.getAttributes().getNamedItem("natura").getNodeValue() : null;
			normativa = n.getAttributes().getNamedItem("normativa") != null ? n.getAttributes().getNamedItem("normativa").getNodeValue() : null;
			funzione = n.getAttributes().getNamedItem("funzione") != null ? n.getAttributes().getNamedItem("funzione").getNodeValue() : null;
			fonte = n.getAttributes().getNamedItem("fonte") != null ? n.getAttributes().getNamedItem("fonte").getNodeValue() : null;
		}
		return (new Infodoc(natura, normativa, funzione, fonte));

	}

	public InfoMancanti getInfomancanti() {
		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		String mTitolodoc=null;		
		String mTipodoc=null;		
		String mDatadoc=null;		
		String mNumdoc=null;		
		String mEmanante=null;

		Node[] infomancList = UtilDom.getElementsByTagName(doc,activeMeta,"infomancanti");
		if (infomancList.length > 0) {
			
			Node n = infomancList[0];
			
			NodeList infomanc_elementList = n.getChildNodes();
			for (int i = 0; i < infomanc_elementList.getLength();i++) {
				String valore=UtilDom.getAttributeValueAsString(infomanc_elementList.item(i),"value");
				
				if(infomanc_elementList.item(i).getNodeName().equals("mTitolodoc"))
					mTitolodoc=valore;					
				if(infomanc_elementList.item(i).getNodeName().equals("mTipodoc"))					
					mTipodoc=valore;
				if(infomanc_elementList.item(i).getNodeName().equals("mDatadoc"))
					mDatadoc = valore;
				if(infomanc_elementList.item(i).getNodeName().equals("mNumdoc"))
					mNumdoc = valore;
				if(infomanc_elementList.item(i).getNodeName().equals("mEmanante"))
					mEmanante = valore;
					
			}
			return (new InfoMancanti(mTitolodoc, mTipodoc, mDatadoc, mNumdoc,mEmanante));
		}else return null;
		
		
	}

	public Oggetto getOggetto() {
		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		String finalita=null;		
		String destinatario=null;		
		String territorio=null;		
		String attivita=null;


		Node[] oggettoList = UtilDom.getElementsByTagName(doc,activeMeta,"oggetto");
		if (oggettoList.length > 0) {
			
			Node n = oggettoList[0];
			
			NodeList oggetto_elementList = n.getChildNodes();
			for (int i = 0; i < oggetto_elementList.getLength();i++) {
				String valore=UtilDom.getAttributeValueAsString(oggetto_elementList.item(i),"value");
				if(oggetto_elementList.item(i).getNodeName().equals("finalita"))
					finalita = valore;  
				if(oggetto_elementList.item(i).getNodeName().equals("destinatario"))
					destinatario = valore;
				if(oggetto_elementList.item(i).getNodeName().equals("territorio"))
					territorio = valore;
				if(oggetto_elementList.item(i).getNodeName().equals("attivita"))
					attivita = valore;
									
			}
			
			return (new Oggetto(finalita, destinatario, territorio, attivita));
		}else
			return null;
		
		
		

	}

	public String[] getProponenti() {
		
		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		Vector proponentiVect = new Vector();
		Node[] proponentiList = UtilDom.getElementsByTagName(doc,activeMeta,"proponenti");
		
		if (proponentiList.length > 0) {
					
			Node n = proponentiList[0];			
			NodeList proponenti_elementList = n.getChildNodes();
			for (int i = 0; i < proponenti_elementList.getLength();i++) {
				String valore=UtilDom.getAttributeValueAsString(proponenti_elementList.item(i),"value");
				if(proponenti_elementList.item(i).getNodeName().equals("proponente"))
					proponentiVect.add(valore);
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
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);		
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		
	}
	
	/**
	 * Rimuove i tag con un determinato nome
	 */
	private void removeMetaByName(String nome, Node node) {
		Document doc = documentManager.getDocumentAsDom();
		Node toRemove;
		
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
	
		do {
			toRemove = UtilDom.findRecursiveChild(activeMeta,nome); 
			if (toRemove != null) {
				toRemove.getParentNode().removeChild(toRemove);
			}
		} while (toRemove != null);
	}



	public void setActiveNode(Node node) {
		this.node=node;
		
	}


}
