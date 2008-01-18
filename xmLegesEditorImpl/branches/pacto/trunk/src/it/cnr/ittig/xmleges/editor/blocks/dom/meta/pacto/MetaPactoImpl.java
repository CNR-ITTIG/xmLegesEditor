package it.cnr.ittig.xmleges.editor.blocks.dom.meta.pacto;

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
import it.cnr.ittig.xmleges.editor.services.dom.meta.pacto.MetaPacto;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MetaPactoImpl implements MetaPacto, Loggable, Serviceable {

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
		String aproposta=null;
		String nproposta=null;	
		String ufficio=null;		
		String relatore=null;		
	
		Node n = UtilDom.findRecursiveChild(activeMeta,"pacto:meta");
		
		if (n!=null) {
						
			NodeList pactoMeta_elementList = n.getChildNodes();
			for (int i = 0; i < pactoMeta_elementList.getLength();i++) {
				if(pactoMeta_elementList.item(i).getNodeName().equals("pacto:proposta")) {
					aproposta=UtilDom.getAttributeValueAsString(pactoMeta_elementList.item(i),"anno");
					nproposta=UtilDom.getAttributeValueAsString(pactoMeta_elementList.item(i),"numero");
				}	
				if(pactoMeta_elementList.item(i).getNodeName().equals("pacto:ufficio"))					
					ufficio=UtilDom.getAttributeValueAsString(pactoMeta_elementList.item(i),"valore");
				if(pactoMeta_elementList.item(i).getNodeName().equals("pacto:relatore"))
					relatore=UtilDom.getAttributeValueAsString(pactoMeta_elementList.item(i),"valore");
			}
			return (new String[]{aproposta, nproposta, ufficio, relatore});
		}else return null;
	}

	public void setProprietario(Node node, String[] metadati) {
		if(metadati!=null){
			Document doc = documentManager.getDocumentAsDom();
			try {
				EditTransaction tr = documentManager.beginEdit();
				if (setDOMPacto(node, metadati)) {
					rinumerazione.aggiorna(doc);
					documentManager.commitEdit(tr);
				} else
					documentManager.rollbackEdit(tr);
				} catch (DocumentManagerException ex) {
					logger.error(ex.toString() + " DocumentManagerException in MetaPacto");
				}
		}
		
	}

	private boolean setDOMPacto(Node node, String[] metadati) {
		
			Document doc = documentManager.getDocumentAsDom();
			Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
			Node proprietarioNode = UtilDom.findRecursiveChild(activeMeta,"proprietario");
			
			
			boolean missingProprietario = false;
			
			if (proprietarioNode==null){
				proprietarioNode = utilRulesManager.getNodeTemplate("proprietario");
				UtilDom.setAttributeValue(proprietarioNode,"soggetto","PACTO");
						
				missingProprietario = true;
			}
			
			Node pactoNode = UtilDom.findRecursiveChild(activeMeta,"pacto:meta");
			if (pactoNode==null)
			    pactoNode =  doc.createElementNS("http://www.pacto.it/norme/1.0","pacto:meta");
	 
			UtilDom.removeAllChildren(pactoNode);
			
			Element toInsertElement = doc.createElement("pacto:proposta");
			if((metadati[0]!=null)&&(!metadati[0].trim().equals("")))				
				UtilDom.setAttributeValue(toInsertElement,"anno",metadati[0]);
			if((metadati[1]!=null)&&(!metadati[1].trim().equals("")))				
				UtilDom.setAttributeValue(toInsertElement,"numero",metadati[1]);
			pactoNode.appendChild((Node)toInsertElement);
			
			toInsertElement = doc.createElement("pacto:ufficio");
			if((metadati[2]!=null)&&(!metadati[2].trim().equals("")))				
				UtilDom.setAttributeValue(toInsertElement,"valore",metadati[2]);
			pactoNode.appendChild((Node)toInsertElement);
			
			toInsertElement = doc.createElement("pacto:relatore");
			if((metadati[3]!=null)&&(!metadati[3].trim().equals("")))				
				UtilDom.setAttributeValue(toInsertElement,"valore",metadati[3]);
			pactoNode.appendChild((Node)toInsertElement);
			
			proprietarioNode.appendChild(pactoNode);
			
			if(missingProprietario){
				Node metaNode = doc.getElementsByTagName("meta").item(0);
				utilRulesManager.orderedInsertChild(metaNode,proprietarioNode);
			}
			return true;
	}



}
