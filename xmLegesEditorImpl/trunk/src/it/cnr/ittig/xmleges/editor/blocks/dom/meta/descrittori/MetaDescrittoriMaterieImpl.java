package it.cnr.ittig.xmleges.editor.blocks.dom.meta.descrittori;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittoriMaterie;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Vocabolario;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class MetaDescrittoriMaterieImpl implements MetaDescrittoriMaterie , Loggable, Serviceable {
	Logger logger;

	DocumentManager documentManager;
	
	RulesManager rulesManager;

	UtilRulesManager utilRulesManager;
	
	Rinumerazione rinumerazione; 
	
	NirUtilDom nirUtilDom;
	
	public void enableLogging(Logger logger) {
		this.logger = logger;
		
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		
	}

	public void setVocabolari(Node node, Vocabolario[] vocabolari) {
		
		Document doc = documentManager.getDocumentAsDom();
		try {
			EditTransaction tr = documentManager.beginEdit();
			Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
			Node descrittoriNode = UtilDom.findRecursiveChild(activeMeta,"descrittori");
						
			removeMetaByName("materie",node);
			
			for (int i = 0; i < vocabolari.length; i++) {
				
				Node vocabTag;
				vocabTag = utilRulesManager.getNodeTemplate("materie");
				
				
				UtilDom.setAttributeValue(vocabTag,"vocabolario",vocabolari[i].getNome());
				String[] materieVocab=vocabolari[i].getMaterie();
				if(materieVocab!=null && materieVocab.length>0){
					vocabTag.removeChild(vocabTag.getChildNodes().item(0));
					for (int j = 0; j < materieVocab.length; j++) {
						Node materiaTag = UtilDom.createElement(doc, "materia");
						UtilDom.setAttributeValue(materiaTag,"valore",vocabolari[i].getMaterie()[j]);
						utilRulesManager.orderedInsertChild(vocabTag,materiaTag);
						
					}
				}else{
					UtilDom.setAttributeValue(vocabTag.getChildNodes().item(0),"valore",null);
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

	public Vocabolario[] getVocabolari(Node node) {
		
		Vocabolario[] vocabolariOnDoc;
		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		
		Node[] vocabolariList = UtilDom.getElementsByTagName(doc,activeMeta,"materie");
		if(vocabolariList==null || vocabolariList.length==0)
			return null;

		vocabolariOnDoc=new Vocabolario[vocabolariList.length];
		for(int i=0;i<vocabolariList.length;i++){
			vocabolariOnDoc[i]=new Vocabolario();
			String nomeVocabolario=vocabolariList[i].getAttributes().getNamedItem("vocabolario").getNodeValue();
			if (nomeVocabolario.equals(""))
				nomeVocabolario = "-- name absent --";
			vocabolariOnDoc[i].setNome(nomeVocabolario);
			NodeList materieList=vocabolariList[i].getChildNodes();
			boolean isEmpty=(materieList.getLength()==1 && (materieList.item(0).getAttributes().getNamedItem("valore")==null 
					|| 
					materieList.item(0).getAttributes().getNamedItem("valore").getNodeValue().equals("")));
			if(materieList!=null && !isEmpty){
				String[] materieVocabolario=new String[materieList.getLength()];
				for(int j=0;j<materieList.getLength();j++){
					materieVocabolario[j]=materieList.item(j).getAttributes().getNamedItem("valore").getNodeValue();			
				}
				vocabolariOnDoc[i].setMaterie(materieVocabolario);
				
			}
			
		}
		
		return vocabolariOnDoc;
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

	

}
