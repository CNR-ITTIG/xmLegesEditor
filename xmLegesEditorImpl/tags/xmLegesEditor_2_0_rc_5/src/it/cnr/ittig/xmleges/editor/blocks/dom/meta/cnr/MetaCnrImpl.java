package it.cnr.ittig.xmleges.editor.blocks.dom.meta.cnr;

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
import it.cnr.ittig.xmleges.editor.services.dom.meta.cnr.MetaCnr;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MetaCnrImpl implements MetaCnr, Loggable, Serviceable {

	Logger logger;
	
	DocumentManager documentManager;

	UtilRulesManager utilRulesManager;
	
	Rinumerazione rinumerazione; 
	
	NirUtilDom nirUtilDom;

	
	public void enableLogging(Logger logger) {
		this.logger = logger;

	}

	public void service(ServiceManager serviceManager) throws ServiceException {		
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);				
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
	}
	
	public String[] getProprietario(Node node) {

		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		String strutturaEmanante=null;	
		String autoritaEmanante=null;		
		String tipoDestinatario=null;		
		String tagDiscipline=null;	
		String strutturaDestinataria=null;
		String tipo_provvedimento=null;
		
		Node n = UtilDom.findRecursiveChild(activeMeta,"cnr:meta");
		
		if (n!=null) {
						
			NodeList cnrMeta_elementList = n.getChildNodes();
			for (int i = 0; i < cnrMeta_elementList.getLength();i++) {
				String valore=UtilDom.getAttributeValueAsString(cnrMeta_elementList.item(i),"value");
				
				if(cnrMeta_elementList.item(i).getNodeName().equals("cnr:strutturaEmanante"))
					strutturaEmanante=valore;					
				if(cnrMeta_elementList.item(i).getNodeName().equals("cnr:autoritaEmanante"))					
					autoritaEmanante=valore;
				if(cnrMeta_elementList.item(i).getNodeName().equals("cnr:tipoDestinatario"))
					tipoDestinatario = valore;
				if(cnrMeta_elementList.item(i).getNodeName().equals("cnr:areaScientifica"))
					tagDiscipline = valore;
				if(cnrMeta_elementList.item(i).getNodeName().equals("cnr:strutturaDestinataria"))
					strutturaDestinataria = valore;
				if(cnrMeta_elementList.item(i).getNodeName().equals("cnr:tipoProvvedimento"))
					tipo_provvedimento = valore;
			}
			return (new String[]{strutturaEmanante, autoritaEmanante, tipoDestinatario, tagDiscipline, strutturaDestinataria, tipo_provvedimento});
		}else return null;
		
	}

	public void setProprietario(Node node, String[] metadati) {
		if(metadati!=null && metadati.length==6){
			Document doc = documentManager.getDocumentAsDom();
			try {
				EditTransaction tr = documentManager.beginEdit();
				if (setDOMCnr(node, metadati)) {
					rinumerazione.aggiorna(doc);
					documentManager.commitEdit(tr);
				} else
					documentManager.rollbackEdit(tr);
				} catch (DocumentManagerException ex) {
					logger.error(ex.toString() + " DocumentManagerException in MetaCnr");
				}
		}
		
	}

	private boolean setDOMCnr(Node node, String[] metadati) {
		
			Document doc = documentManager.getDocumentAsDom();
			Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
			Node proprietarioNode = UtilDom.findRecursiveChild(activeMeta,"proprietario");
			
			
			boolean missingProprietario = false;
			
			if (proprietarioNode==null){
				proprietarioNode = doc.createElementNS("http://www.cnr.it/provvedimenti/2.1","proprietario");
				missingProprietario = true;
			}
			
			Node cnrNode = UtilDom.findRecursiveChild(activeMeta,"cnr:meta");
			if (cnrNode==null)
			    cnrNode = utilRulesManager.getNodeTemplate("cnr:meta");
			
			
			
			utilRulesManager.orderedInsertChild(proprietarioNode,cnrNode);
						
			String[] elementsName=new String[]{"cnr:strutturaEmanante","cnr:autoritaEmanante","cnr:tipoDestinatario","cnr:areaScientifica","cnr:strutturaDestinataria","cnr:tipoProvvedimento"};
			 
			for(int i=0;i<elementsName.length;i++){
				Element toInsertElement = doc.createElement(elementsName[i]);
				if((metadati[i]!=null)&&(!metadati[i].trim().equals("")))
					UtilDom.setAttributeValue(toInsertElement,"value",metadati[i]);
				
				Node toInsert_Node = (Node)toInsertElement;

				Node oldTag = UtilDom.findDirectChild(cnrNode,elementsName[i]);//doc.getElementsByTagName(elementsName[i]);
				if (oldTag!=null) // c'era gia' un nodo elementsName[i]
					cnrNode.replaceChild(toInsert_Node, oldTag);
				else 
					utilRulesManager.orderedInsertChild(cnrNode,toInsert_Node);
			    	
			}
			
			if(missingProprietario){
				Node metaNode = doc.getElementsByTagName("meta").item(0);
				utilRulesManager.orderedInsertChild(metaNode,proprietarioNode);
			}
			return true;
		

	}



}
