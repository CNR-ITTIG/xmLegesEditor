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
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
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
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class VigenzaImpl implements Vigenza, Loggable, Serviceable {
	Logger logger;

	RulesManager rulesManager;

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
		rulesManager = (RulesManager) serviceManager.lookup(RulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		extractText = (ExtractText) serviceManager.lookup(ExtractText.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
	}

	
	public boolean canSetVigenza(Node node) {
		if (node != null && node.getParentNode() != null) {
			try {
				return (node.getNodeName()!=null && 
						(rulesManager.queryIsValidAttribute(node.getNodeName(), "iniziovigore")
								|| UtilDom.isTextNode(node)) 
						);
			} catch (RulesManagerException e) {
				return UtilDom.isTextNode(node);
			}
		}
		return false;
	}
	

	public Node setVigenza(Node node, String selectedText, int start, int end, VigenzaEntity vigenza) {
		Node ret = null;
		try {
			EditTransaction tr = documentManager.beginEdit();
			if ((ret=setDOMVigenza(node, selectedText, start, end, vigenza))!=null) {
				rinumerazione.aggiorna(documentManager.getDocumentAsDom());
				documentManager.commitEdit(tr);
			} else
				documentManager.rollbackEdit(tr);
		} catch (DocumentManagerException ex) {
			logger.error(ex.toString() + " DocumentManagerException in SetVigenza");
			return null;
		}
		return ret;
	}
	
	public Node setDOMVigenza(Node node, String selectedText, int start, int end, VigenzaEntity vigenza) {
		
		selectedNode=node;
		
		if(node==null) 
			return null;	
		
		if(node.getNodeValue()==null){
			if(UtilDom.getTextNode(node)==null || UtilDom.getTextNode(node).trim().equals(""))
				//caso di selzione solo su nodo generici (articolato, formulainiziale, formulafinale ecc..)
				selectedText=node.getNodeName();
			else
				//caso di selezione su nodo paragrafo o sottoscrivente o visto (perche'?)
				selectedText=UtilDom.getTextNode(node);
		}
//		else				
//			selectedText=node.getNodeValue();

		if(UtilDom.isTextNode(node)){
			// solo nodi finali di testo (con matitina verde)
			Element span;
			if(node.getNodeValue().equals(selectedText) || (start==-1 && end==-1)){
				//il testo selezionato coincide con tutto il nodo
				span = (Element) node.getParentNode();
				selectedText=node.getNodeValue();
			}
			else{        // racchiude il testo in uno span e lo riestrae ????
            // il testo selezionato è una sottoparte del nodo di testo (va creato lo span)				
				
				//qui crea uno span dal testo selezionato 
				span = (Element) utilRulesManager.encloseTextInTag(node, start, end,"h:span","h");
				if(vigenza.getEInizioVigore()==null && vigenza.getEFineVigore()==null && vigenza.getStatus()==null){
					Node padre=span.getParentNode();
					//	appiattisce lo span
					extractText.extractTextDOM(node,0,selectedText.length());
					padre.removeChild(span);
					UtilDom.mergeTextNodes(padre);
					return padre;
				}
				
			}
			
//			 Assegnazione attributi di vigenza allo span creato
			if(vigenza.getEInizioVigore()!=null){
				UtilDom.setAttributeValue(span,"iniziovigore",vigenza.getEInizioVigore().getId());
				if(vigenza.getStatus()!=null && !vigenza.getStatus().equals("--"))
					span.setAttribute("status", vigenza.getStatus());
			}
			else{//inizio obbligatorio, se non presente si elimina la vigenza e si esce	
				
			// ?   A CHE SERVONO QUESTE OPERAZIONI SE ELIMINO LO SPAN ? 	
				
//				try{
//					if(!rulesManager.queryIsRequiredAttribute(span.getNodeName(),"iniziovigore"))
//					    span.removeAttribute("iniziovigore");
//					else 
//						UtilDom.setAttributeValue(span,"iniziovigore","");
//					if(!rulesManager.queryIsRequiredAttribute(span.getNodeName(),"finevigore"))
//					    span.removeAttribute("finevigore");
//					else 
//						UtilDom.setAttributeValue(span,"finevigore","");
//					if(!rulesManager.queryIsRequiredAttribute(span.getNodeName(),"status"))
//					    span.removeAttribute("status");
//					else 
//						UtilDom.setAttributeValue(span,"status","");
//				}
//				catch(RulesManagerException ex){}
				
				Node padre=span.getParentNode();
				//	appiattisce lo span
				extractText.extractTextDOM(node,0,selectedText.length());
				padre.removeChild(span);
				UtilDom.mergeTextNodes(padre);
				return padre;
			}
			
			if(vigenza.getEFineVigore()!=null)
				UtilDom.setAttributeValue(span,"finevigore",vigenza.getEFineVigore().getId());				
			else{
				try{
					if(!rulesManager.queryIsRequiredAttribute(span.getNodeName(),"finevigore"))
					    span.removeAttribute("finevigore");
					else 
						UtilDom.setAttributeValue(span,"finevigore","");
				}
				catch(RulesManagerException ex){}
			}	
		    return span;
		
		}else{   
			//non è un nodo di testo
		    NamedNodeMap nnm = node.getAttributes();
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
			return node;		
		}
	}


public VigenzaEntity getVigenza(Node node, int start, int end) {

	Document doc = documentManager.getDocumentAsDom();
	
	if(node==null)
		return null;
	
	if(UtilDom.isTextNode(node) && (node.getParentNode().getAttributes()== null || node.getParentNode().getAttributes().getNamedItem("iniziovigore")==null) )
		return null;
	
	if(!UtilDom.isTextNode(node) && (node.getAttributes()==null || node.getAttributes().getNamedItem("iniziovigore")==null))
		return null;
	
	Evento e_iniziovig=null;
	Evento e_finevig=null;
	Node iniziovig=null;
	Node finevig=null;
	Node status=null;
	selectedText="";
	
	if(!UtilDom.isTextNode(node)){
		//non c'è selezione di testo sono su nodo generico
		if(node.getNodeValue()==null){
			if(UtilDom.getTextNode(node)==null || UtilDom.getTextNode(node).trim().equals(""))
				//	caso di selzione solo su nodo generici (articolato, formulainiziale, formulafinale ecc..)
				selectedText=node.getNodeName();
			else
				// caso di selezione su nodo paragrafo o sottoscrivente o visto (perche'?)
				selectedText=UtilDom.getTextNode(node);
		}
		else				
			selectedText=node.getNodeValue();
		
	}
	//else	siamo su nodo di testo (matita verde) 
	else {

		Node parentNode=node.getParentNode();
		
	   // Recupero contenuto Nodo
		selectedText=UtilDom.getTextNode(parentNode);

		//se il testo selezionato non coincide con quello dello span di cui è figlio
		//si crea una nuova vigenza		
		if(start!=end && selectedText.substring(start,end).length()<selectedText.length()){
				selectedText=selectedText.substring(start,end);
				return null;
		}
		
		
		
		node=parentNode;

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
		
		//	qui rappresenta la fonte dell'evento di inizio
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
				
	
		//		qui rappresenta la fonte dell'evento di fine
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
	
	return new VigenzaEntity(node, e_iniziovig,e_finevig, status!=null?status.getNodeValue():null,selectedText);
	
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

	// T: aggiunto il controllo di non riaggiornare il tipo se e' gia' settato: risparmia molto tempo sull'update dei pannelli
	public void setTipoDocVigenza() {
		Document doc = documentManager.getDocumentAsDom();
		if (isVigente()){
			if(!UtilDom.getAttributeValueAsString(doc.getDocumentElement(),"tipo").equalsIgnoreCase("multivigente"))
				UtilDom.setAttributeValue(doc.getDocumentElement(),"tipo","multivigente");
		}else{
			if(!UtilDom.getAttributeValueAsString(doc.getDocumentElement(),"tipo").equalsIgnoreCase("originale"))
				UtilDom.setAttributeValue(doc.getDocumentElement(),"tipo","originale");	
		}
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
							extractText.extractTextDOM(node.getFirstChild(),0,UtilDom.getText(node.getFirstChild()).length());
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
