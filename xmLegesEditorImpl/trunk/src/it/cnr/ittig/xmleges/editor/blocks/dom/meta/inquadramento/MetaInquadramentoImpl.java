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
		Node inquadramentoNode = UtilDom.findRecursiveChild(activeMeta,"inquadramento");
		
		boolean missingInquadramento = false;
		
		if (inquadramentoNode==null){
			inquadramentoNode = UtilDom.createElement(doc, "inquadramento");
			missingInquadramento = true;
		}
					
		Node infodoc_element = UtilDom.createElement(doc, "infodoc");
		if((infodoc.getNatura()!=null)&&(!infodoc.getNatura().trim().equals("")))
			UtilDom.setAttributeValue(infodoc_element, "natura", infodoc.getNatura());
		if((infodoc.getNormativa()!=null)&&(!infodoc.getNormativa().trim().equals("")))
			UtilDom.setAttributeValue(infodoc_element,"normativa", infodoc.getNormativa());
		if((infodoc.getFunzione()!=null)&&(!infodoc.getFunzione().trim().equals("")))
			UtilDom.setAttributeValue(infodoc_element,"funzione", infodoc.getFunzione());
		if(infodoc.getFonte()!=null)
			UtilDom.setAttributeValue(infodoc_element,"fonte", infodoc.getFonte());
		if(infodoc.getRegistrazione()!=null)
			UtilDom.setAttributeValue(infodoc_element,"registrazione", infodoc.getRegistrazione());
			
		Node infodocNode = (Node)infodoc_element;
		
		Node oldTag = UtilDom.findRecursiveChild(activeMeta,"infodoc");
		if (oldTag!=null) // c'era gia' un nodo infodoc
			inquadramentoNode.replaceChild(infodocNode, oldTag);
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,infodocNode);
		
		
		if(missingInquadramento){			
			utilRulesManager.orderedInsertChild(activeMeta,inquadramentoNode);
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
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		
		Node inquadramentoNode = UtilDom.findRecursiveChild(activeMeta,"inquadramento");
		boolean missingInquadramento = false;
		
		if (inquadramentoNode==null){
			inquadramentoNode = UtilDom.createElement(doc,"inquadramento");
			missingInquadramento = true;
		}
		
		Node infomancantiNode = utilRulesManager.getNodeTemplate("infomancanti");

		
		Element infomancanti_element=null;
		if(infomancanti.getMTitolodoc()!= null && !infomancanti.getMTitolodoc().trim().equals("")){
			infomancanti_element = (Element) utilRulesManager.getNodeTemplate("mTitolodoc");
			UtilDom.setAttributeValue(infomancanti_element,"valore",infomancanti.getMTitolodoc());			
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
	
		}
		if(infomancanti.getMTipodoc()!= null && !infomancanti.getMTipodoc().trim().equals("")){
			infomancanti_element = (Element) utilRulesManager.getNodeTemplate("mTipodoc");
			UtilDom.setAttributeValue(infomancanti_element,"valore",infomancanti.getMTipodoc() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		if(infomancanti.getMDatadoc()!= null && !infomancanti.getMDatadoc().trim().equals("")){
			infomancanti_element = (Element) utilRulesManager.getNodeTemplate("mDatadoc");
			UtilDom.setAttributeValue(infomancanti_element,"valore",infomancanti.getMDatadoc() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		if(infomancanti.getMNumdoc()!= null && !infomancanti.getMNumdoc().trim().equals("")){
			infomancanti_element = (Element) utilRulesManager.getNodeTemplate("mNumdoc");
			UtilDom.setAttributeValue(infomancanti_element,"valore",infomancanti.getMNumdoc() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		if(infomancanti.getMEmanante()!= null && !infomancanti.getMEmanante().trim().equals("")){
			infomancanti_element = (Element) utilRulesManager.getNodeTemplate("mEmanante");
			UtilDom.setAttributeValue(infomancanti_element,"valore",infomancanti.getMEmanante() );
			utilRulesManager.orderedInsertChild(infomancantiNode,infomancanti_element);
		}
		/////////////////////
		Node oldTag = UtilDom.findRecursiveChild(activeMeta,"infomancanti");
		if (oldTag!=null) // c'era gia' un nodo infomancanti
			inquadramentoNode.replaceChild(infomancantiNode, oldTag);
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,infomancantiNode);
		
		if(missingInquadramento){
			
			utilRulesManager.orderedInsertChild(activeMeta,inquadramentoNode);
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
		Node inquadramentoNode = UtilDom.findRecursiveChild(activeMeta,"inquadramento");
		boolean missingInquadramento = false;
		
		if (inquadramentoNode==null){
			inquadramentoNode = UtilDom.createElement(doc,"inquadramento");
			missingInquadramento = true;
		}
		
		Node oggettoNode = utilRulesManager.getNodeTemplate("oggetto");
		
		Element oggetto_element=null;
		if(oggetto.getFinalita()!= null && !oggetto.getFinalita().trim().equals("")){
			oggetto_element = (Element) utilRulesManager.getNodeTemplate("finalita");
			UtilDom.setAttributeValue(oggetto_element,"valore",oggetto.getFinalita() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		if(oggetto.getDestinatario()!= null && !oggetto.getDestinatario().trim().equals("")){
			oggetto_element = (Element) utilRulesManager.getNodeTemplate("destinatario");
			UtilDom.setAttributeValue(oggetto_element,"valore",oggetto.getDestinatario() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		if(oggetto.getTerritorio()!= null && !oggetto.getTerritorio().trim().equals("")){
			oggetto_element = (Element) utilRulesManager.getNodeTemplate("territorio");
			UtilDom.setAttributeValue(oggetto_element,"valore",oggetto.getTerritorio() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		if(oggetto.getAttivita()!= null && !oggetto.getAttivita().trim().equals("")){
			oggetto_element = (Element) utilRulesManager.getNodeTemplate("attivita");
			UtilDom.setAttributeValue(oggetto_element,"valore",oggetto.getAttivita() );
			utilRulesManager.orderedInsertChild(oggettoNode,oggetto_element);
		}
		
		Node oldTag = UtilDom.findRecursiveChild(activeMeta,"oggetto");
		if (oldTag!=null) // c'era gia' un nodo oggetto
			inquadramentoNode.replaceChild(oggettoNode, oldTag);
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,oggettoNode);
		
		
		if(missingInquadramento){
			
			utilRulesManager.orderedInsertChild(activeMeta,inquadramentoNode);
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
		Node inquadramentoNode = UtilDom.findRecursiveChild(activeMeta,"inquadramento");
		boolean missingInquadramento = false;
		
		if (inquadramentoNode==null){
			inquadramentoNode = UtilDom.createElement(doc,"inquadramento");
			missingInquadramento = true;
		}
		
		Node proponentiNode = utilRulesManager.getNodeTemplate("proponenti");
		
		Element proponenti_element=null;
		if(proponenti!= null && proponenti.length>0 ){
			for (int i = 0; i < proponenti.length; i++) {
				proponenti_element =  (Element) utilRulesManager.getNodeTemplate("proponente");
				UtilDom.setAttributeValue(proponenti_element,"valore",proponenti[i] );
				utilRulesManager.orderedInsertChild(proponentiNode,proponenti_element);
			}
		}
		
		Node oldTag = UtilDom.findRecursiveChild(activeMeta,"proponenti");
		if (oldTag!=null) // c'era gia' un nodo proponenti
			inquadramentoNode.replaceChild(proponentiNode, oldTag);
		else 
			utilRulesManager.orderedInsertChild(inquadramentoNode,proponentiNode);
		
		
		if(missingInquadramento){
			
			utilRulesManager.orderedInsertChild(activeMeta,inquadramentoNode);
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
		String registrazione=null;

		
		Node infodocNode = UtilDom.findRecursiveChild(activeMeta,"infodoc");
		if (infodocNode!=null) {
			Node n = infodocNode;
			natura = n.getAttributes().getNamedItem("natura") != null ? n.getAttributes().getNamedItem("natura").getNodeValue() : null;
			normativa = n.getAttributes().getNamedItem("normativa") != null ? n.getAttributes().getNamedItem("normativa").getNodeValue() : null;
			funzione = n.getAttributes().getNamedItem("funzione") != null ? n.getAttributes().getNamedItem("funzione").getNodeValue() : null;
			fonte = n.getAttributes().getNamedItem("fonte") != null ? n.getAttributes().getNamedItem("fonte").getNodeValue() : null;
			registrazione = n.getAttributes().getNamedItem("registrazione") != null ? n.getAttributes().getNamedItem("registrazione").getNodeValue() : null;
		}
		return (new Infodoc(natura, normativa, funzione, fonte, registrazione));

	}

	public InfoMancanti getInfomancanti() {
		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		String mTitolodoc=null;		
		String mTipodoc=null;		
		String mDatadoc=null;		
		String mNumdoc=null;		
		String mEmanante=null;

		Node infomancNode = UtilDom.findRecursiveChild(activeMeta,"infomancanti");
		if (infomancNode!=null) {
			
			Node n = infomancNode;
			
			NodeList infomanc_elementList = n.getChildNodes();
			for (int i = 0; i < infomanc_elementList.getLength();i++) {
				String valore=UtilDom.getAttributeValueAsString(infomanc_elementList.item(i),"valore");
				
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


		Node oggettoNode = UtilDom.findRecursiveChild(activeMeta,"oggetto");
		if (oggettoNode!=null) {
			
			Node n = oggettoNode;
			
			NodeList oggetto_elementList = n.getChildNodes();
			for (int i = 0; i < oggetto_elementList.getLength();i++) {
				String valore=UtilDom.getAttributeValueAsString(oggetto_elementList.item(i),"valore");
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
		Node proponentiNode = UtilDom.findRecursiveChild(activeMeta,"proponenti");
		
		if (proponentiNode!=null) {
					
			Node n = proponentiNode;			
			NodeList proponenti_elementList = n.getChildNodes();
			for (int i = 0; i < proponenti_elementList.getLength();i++) {
				String valore=UtilDom.getAttributeValueAsString(proponenti_elementList.item(i),"valore");
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
