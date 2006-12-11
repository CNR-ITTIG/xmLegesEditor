package it.cnr.ittig.xmleges.editor.blocks.dom.vigenza;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dom.extracttext.ExtractText;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.Vigenza;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.vigenza.Vigenza</code>.</h1>
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
 * @see
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class VigenzaImpl implements Vigenza, Loggable, Serviceable {
	Logger logger;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;
	
	UtilRulesManager utilRulesManager;
	
	ExtractText extractText;
	
	Rinumerazione rinumerazione;
		
	Node selectedNode;
	
	String selectedText;
	
	
		
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		extractText = (ExtractText) serviceManager.lookup(ExtractText.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
	}

	
	public boolean canSetVigenza(Node node) {
		if (node != null && node.getParentNode() != null) {
			try {
				return (node.getNodeName()!=null && 
						(dtdRulesManager.queryIsValidAttribute(node.getNodeName(), "iniziovigore")
								|| UtilDom.isTextNode(node)) 
						);
			} catch (DtdRulesManagerException e) {
				return UtilDom.isTextNode(node);
			}
		}
		return false;
	}
	

	public Node setVigenza(Node node, String selectedText, int start, int end, VigenzaEntity vigenza) {
		
		selectedNode=node;
		if(node==null) return null;		
		if(node.getNodeValue()==null){
			if(UtilDom.getTextNode(node)==null || UtilDom.getTextNode(node).trim().equals(""))
				selectedText=node.getNodeName();
			else
				selectedText=UtilDom.getTextNode(node);
		}
		else				
			selectedText=node.getNodeValue();

		
		if(UtilDom.isTextNode(node)){
			Element span;
			if(node.getParentNode().getNodeName().equals("h:span"))
				span = (Element) node.getParentNode();
			else{
				if(vigenza.getEInizioVigore()==null && vigenza.getEFineVigore()==null && vigenza.getStatus()==null){
					return null;
				}
				span = (Element) utilRulesManager.encloseTextInTag(node, start, end,"h:span","h");
			}
			// Assegnazione attributi di vigenza allo span creato
			if(vigenza.getEInizioVigore()!=null){
				UtilDom.setAttributeValue(span,"iniziovigore",vigenza.getEInizioVigore().getId());
				if(vigenza.getStatus()!=null && !vigenza.getStatus().equals("--"))
					span.setAttribute("status", vigenza.getStatus());
			}
			else{//inizio obbligatorio, se non presente si elimina la vigenza e si esce				
				span.removeAttribute("iniziovigore");
				span.removeAttribute("finevigore");
				span.removeAttribute("status");
			//	if(start!=end){
	//				appiattisce lo span				
					Node padre=span.getParentNode();				
					extractText.extractText(node,0,selectedText.length());
					padre.removeChild(span);
					UtilDom.mergeTextNodes(padre);
					return padre;
//				}else{
//					return span;
//				}
			}
			//FIXME: va controllato che esista sul dom prima di rimuoverli?
			if(vigenza.getEFineVigore()!=null)
				UtilDom.setAttributeValue(span,"finevigore",vigenza.getEFineVigore().getId());				
			else
				span.removeAttribute("finevigore");
            
			// setta gli id degli span
			rinumerazione.aggiorna(documentManager.getDocumentAsDom());
			
			return span;
			
		}else{//non c'è span
			try {	
				NamedNodeMap nnm = node.getAttributes();
				EditTransaction tr = documentManager.beginEdit();
					
					// Assegnazione attributi di vigenza al nodo
					if(vigenza.getEInizioVigore()!=null)
						UtilDom.setAttributeValue(node, "iniziovigore", vigenza.getEInizioVigore().getId());
					else{						
						if(nnm.getNamedItem("iniziovigore")!=null)
							nnm.removeNamedItem("iniziovigore");
						if(nnm.getNamedItem("finevigore")!=null)
							nnm.removeNamedItem("finevigore");
						if(nnm.getNamedItem("status")!=null)
							nnm.removeNamedItem("status");
						documentManager.commitEdit(tr);
						return node;
					}
					if(vigenza.getEFineVigore()!=null)
						UtilDom.setAttributeValue(node, "finevigore", vigenza.getEFineVigore().getId());
					else{						
						if(nnm.getNamedItem("finevigore")!=null)
							nnm.removeNamedItem("finevigore");
					}	
					
					if(vigenza.getStatus()!=null)
							UtilDom.setAttributeValue(node, "status", vigenza.getStatus());
					else{
						if(nnm.getNamedItem("status")!=null)
							nnm.removeNamedItem("status");
					}
					

					documentManager.commitEdit(tr);
					return node;
				
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				return null;
			}

		}
		

	}


	public VigenzaEntity getVigenza(Node node, int start, int end) {

		Document doc = documentManager.getDocumentAsDom();
		
		if(node==null)
			return null;
		if(UtilDom.isTextNode(node) && node.getParentNode().getAttributes()==null)
			return null;
		if(!UtilDom.isTextNode(node) && node.getAttributes()==null)
			return null;
		
		Evento e_iniziovig=null;
		Evento e_finevig=null;
		Node iniziovig=null;
		Node finevig=null;
		Node status=null;
		if(!UtilDom.isTextNode(node)&&(start==-1)&&(end==-1)){
			if(node.getNodeValue()==null){
				if(UtilDom.getTextNode(node)==null || UtilDom.getTextNode(node).trim().equals(""))
					selectedText=node.getNodeName();
				else
					selectedText=UtilDom.getTextNode(node);
			}
			else				
				selectedText=node.getNodeValue();
		}else if(node.getParentNode()!=null && node.getParentNode().getNodeName().equals("h:span")){		
			Node span=node.getParentNode();
		//			 Recupero Nodo h:span con contenuto
			if(span.getNodeValue()==null){
				if(UtilDom.getTextNode(span)==null || UtilDom.getTextNode(span).trim().equals(""))
					selectedText=span.getNodeName();					
				else
					selectedText=UtilDom.getTextNode(span);		
				
			}
			else				
				selectedText=span.getNodeValue();
			
			node=span;
		}else{
			if(node.getNodeValue()==null){
				if(UtilDom.getTextNode(node)==null || UtilDom.getTextNode(node).trim().equals(""))
					selectedText=node.getNodeName();
				else
					selectedText=UtilDom.getTextNode(node);
			}
			else				
				selectedText=node.getNodeValue();
			return null;
		}
					
		
			iniziovig=node.getAttributes().getNamedItem("iniziovigore");
			finevig=node.getAttributes().getNamedItem("finevigore");
			status=node.getAttributes().getNamedItem("status");
		

			Element evento_ie_Tag = (iniziovig==null?null:doc.getElementById(iniziovig.getNodeValue()));
			Element evento_fe_Tag = (finevig==null?null:doc.getElementById(finevig.getNodeValue()));
			
			if(evento_ie_Tag==null){
				//evento inizio non presente quindi vigenza vuota
				return null;
			}
			
	//			qui rappresenta la fonte dell'evento di inizio
			Element evento_fonte_Tag = (evento_ie_Tag==null?null:doc.getElementById(evento_ie_Tag.getAttribute("fonte")));
			
			String tag, id, link, effetto, tipo;
			Relazione rel = null;
	
			if (evento_fonte_Tag!=null && evento_fonte_Tag.getNodeType() == Node.ELEMENT_NODE) {
				tag = evento_fonte_Tag.getNodeName();
				id = evento_fonte_Tag.getAttributes().getNamedItem("id") != null ? evento_fonte_Tag.getAttributes().getNamedItem("id").getNodeValue() : null;
				link = evento_fonte_Tag.getAttributes().getNamedItem("xlink:href") != null ? evento_fonte_Tag.getAttributes().getNamedItem("xlink:href")
						.getNodeValue() : null;
			    if(tag.equals("giurisprudenza")){
			    	effetto=evento_fonte_Tag.getAttributes().getNamedItem("effetto") != null ? evento_fonte_Tag.getAttributes().getNamedItem("effetto").getNodeValue() : null;
				    rel=new Relazione(tag, id, link,effetto);
			    }else if(tag.equals("haallegato") || tag.equals("allegatodi")){
			    	tipo=evento_fonte_Tag.getAttributes().getNamedItem("tipo") != null ? evento_fonte_Tag.getAttributes().getNamedItem("tipo").getNodeValue() : null;
				    rel=new Relazione(tag, id, link,tipo);
			    }else 						    	
			    	rel=new Relazione(tag, id, link);
			}
			
	
			e_iniziovig=iniziovig==null?null:new Evento(iniziovig!=null?iniziovig.getNodeValue():"",
					evento_ie_Tag!=null?evento_ie_Tag.getAttribute("data"):null,//				
							rel,
							evento_ie_Tag!=null?evento_ie_Tag.getAttribute("tipo"):null);
					
		
	//			qui rappresenta la fonte dell'evento di fine
			evento_fonte_Tag = evento_fe_Tag==null?null:doc.getElementById(evento_fe_Tag.getAttribute("fonte"));
			rel=null;
				
			if (evento_fonte_Tag!=null && evento_fonte_Tag.getNodeType() == Node.ELEMENT_NODE) {
				tag = evento_fonte_Tag.getNodeName();
				id = evento_fonte_Tag.getAttributes().getNamedItem("id") != null ? evento_fonte_Tag.getAttributes().getNamedItem("id").getNodeValue() : null;
				link = evento_fonte_Tag.getAttributes().getNamedItem("xlink:href") != null ? evento_fonte_Tag.getAttributes().getNamedItem("xlink:href")
						.getNodeValue() : null;
			    if(tag.equals("giurisprudenza")){
			    	effetto=evento_fonte_Tag.getAttributes().getNamedItem("effetto") != null ? evento_fonte_Tag.getAttributes().getNamedItem("effetto").getNodeValue() : null;
				    rel=new Relazione(tag, id, link,effetto);
			    }else if(tag.equals("haallegato")||tag.equals("allegatodi")){
			    	tipo=evento_fonte_Tag.getAttributes().getNamedItem("tipo") != null ? evento_fonte_Tag.getAttributes().getNamedItem("tipo").getNodeValue() : null;
				    rel=new Relazione(tag, id, link,tipo);
			    }else
			    	rel=new Relazione(tag, id, link);
			}
			e_finevig=(finevig==null)?null:new Evento(finevig!=null?finevig.getNodeValue():"",
					evento_fe_Tag!=null?evento_fe_Tag.getAttribute("data"):null,
					rel,
					evento_fe_Tag!=null?evento_fe_Tag.getAttribute("tipo"):null);
		
		
		if(e_iniziovig==null && e_finevig==null) return null;
		
		return new VigenzaEntity(node, e_iniziovig,e_finevig, status!=null?status.getNodeValue():null,"");
		
	}//metodo

	public String getSelectedText() {
		return selectedText;
	}

	public boolean isVigente() {
		Document doc = documentManager.getDocumentAsDom();
		
		NodeList lista = doc.getElementsByTagName("*");
		
		for(int i=0; i<lista.getLength();i++){
			if(lista.item(i).getAttributes().getNamedItem("iniziovigore")!=null){
				return true;
			}
//			if(lista.item(i).getAttributes().getNamedItem("finevigore")!=null){
//				return true;
//			}
		}	
		return false;	
	}

	public void setTipoDocVigenza() {
		Document doc = documentManager.getDocumentAsDom();
		if (isVigente()){
			UtilDom.setAttributeValue(doc.getDocumentElement(),"tipo","multivigente");
		}else
			UtilDom.setAttributeValue(doc.getDocumentElement(),"tipo","originale");				
	}
	
	public void updateVigenzaOnDoc(VigenzaEntity vig){
		
		Node node=vig.getOnNode();
		if(node==null)
			return;
			try{
				NamedNodeMap nnm = node.getAttributes();
				if(nnm!=null){
					EditTransaction tr = documentManager.beginEdit();
					
					//se esisteva già l'evento inizio sul dom si aggiorna al nuovo o si elimina se il nuovo è null
					if(nnm.getNamedItem("iniziovigore")!=null){
						if(vig.getEInizioVigore()!=null){
//							UtilDom.setAttributeValue(node, "iniziovigore", vig.getEInizioVigore().getId());
						}

						else
							nnm.removeNamedItem("iniziovigore");	

					}
	//				se esisteva già l'evento fine sul dom si aggiorna al nuovo o si elimina se il nuovo è null	
					if(nnm.getNamedItem("finevigore")!=null){
						if(vig.getEFineVigore()!=null){
//							UtilDom.setAttributeValue(node, "finevigore", vig.getEFineVigore().getId());
						}

						else
							nnm.removeNamedItem("finevigore");

					}
					if(nnm.getNamedItem("iniziovigore")==null && nnm.getNamedItem("finevigore")==null){
						if(nnm.getNamedItem("status")!=null)
							nnm.removeNamedItem("status");
						if(node.getNodeName().equals("h:span")){
//							appiattisce lo span
							Node padre=node.getParentNode();				
							extractText.extractText(node.getFirstChild(),0,UtilDom.getText(node.getFirstChild()).length());
							padre.removeChild(node);
							UtilDom.mergeTextNodes(padre);
						}
				
					}
					documentManager.commitEdit(tr);	
				}
				
			}catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				return;
			}

		
	}

	

}
