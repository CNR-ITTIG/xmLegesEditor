package it.cnr.ittig.xmleges.editor.blocks.dom.meta.descrittori;

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
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittoriMaterie;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Vocabolario;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class MetaDescrittoriMaterieImpl implements MetaDescrittoriMaterie , Loggable, Serviceable {
	Logger logger;

	DocumentManager documentManager;
	
	DtdRulesManager dtdRulesManager;

	UtilRulesManager utilRulesManager;
	
	Rinumerazione rinumerazione; 
	
	public void enableLogging(Logger logger) {
		this.logger = logger;
		
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		
		
	}

	public void setVocabolari(Vocabolario[] vocabolari) {
		
		Document doc = documentManager.getDocumentAsDom();
		try {
			EditTransaction tr = documentManager.beginEdit();
			
			Node descrittoriNode = doc.getElementsByTagName("descrittori").item(0);
			
			removeTagByName("materie");
			
			for (int i = 0; i < vocabolari.length; i++) {
				
				Node vocabTag;
				vocabTag = utilRulesManager.getNodeTemplate("materie");
				
				
				UtilDom.setAttributeValue(vocabTag,"vocabolario",vocabolari[i].getNome());
				String[] materieVocab=vocabolari[i].getMaterie();
				if(materieVocab!=null && materieVocab.length>0){
					vocabTag.removeChild(vocabTag.getChildNodes().item(0));
					for (int j = 0; j < materieVocab.length; j++) {
						Element materiaTag;
						materiaTag = doc.createElement("materia");
						UtilDom.setAttributeValue(materiaTag,"value",vocabolari[i].getMaterie()[j]);
						utilRulesManager.orderedInsertChild(vocabTag,materiaTag);
						
					}
				}else{
					UtilDom.setAttributeValue(vocabTag.getChildNodes().item(0),"value",null);
				}
				utilRulesManager.orderedInsertChild(descrittoriNode,vocabTag);

			}
			documentManager.commitEdit(tr);
			rinumerazione.aggiorna(doc);
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
			return;
		}
												
		
		

		
	}

	public Vocabolario[] getVocabolari() {
		Vocabolario[] vocabolariOnDoc;
		Document doc = documentManager.getDocumentAsDom();
				
		NodeList vocabolariList=doc.getElementsByTagName("materie");
		if(vocabolariList==null || vocabolariList.getLength()==0)
			return null;
		
		vocabolariOnDoc=new Vocabolario[vocabolariList.getLength()];
		for(int i=0;i<vocabolariList.getLength();i++){
			vocabolariOnDoc[i]=new Vocabolario();
			String nomeVocabolario=vocabolariList.item(i).getAttributes().getNamedItem("vocabolario").getNodeValue();
			vocabolariOnDoc[i].setNome(nomeVocabolario);
			NodeList materieList=vocabolariList.item(i).getChildNodes();
			boolean isEmpty=(materieList.getLength()==1 && (materieList.item(0).getAttributes().getNamedItem("value")==null 
					|| 
					materieList.item(0).getAttributes().getNamedItem("value").getNodeValue().equals("")));
			if(materieList!=null && !isEmpty){
				String[] materieVocabolario=new String[materieList.getLength()];
				for(int j=0;j<materieList.getLength();j++){
					materieVocabolario[j]=materieList.item(j).getAttributes().getNamedItem("value").getNodeValue();			
				}
				vocabolariOnDoc[i].setMaterie(materieVocabolario);
				
			}
			
		}
		
		return vocabolariOnDoc;
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
