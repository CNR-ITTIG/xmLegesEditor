package it.cnr.ittig.xmleges.editor.blocks.dom.vigenza;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.Vigenza;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.urn.NirUtilUrn;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
	
	NirUtilUrn nirUtilUrn;
	NirUtilDom nirUtilDom;
	
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
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		nirUtilUrn = (NirUtilUrn) serviceManager.lookup(NirUtilUrn.class);
	}

//	private boolean isDocMultivigente(Document doc) {
//		return (
//						UtilDom.getAttributeValueAsString(doc.getDocumentElement(), "tipo") != null 
//				&& 
//						(
//						UtilDom.getAttributeValueAsString(doc.getDocumentElement(), "tipo").equalsIgnoreCase("monovigente") 
//						|| 
//						UtilDom.getAttributeValueAsString(doc.getDocumentElement(), "tipo").equalsIgnoreCase("multivigente")
//						)
//				
//				);
//	}

	
	public boolean canSetVigenza(Node node, int start, int end) {
		if (node != null && node.getParentNode() != null) {//&& UtilDom.findParentByName(node, "vigenza") == null) {
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
	

	public boolean setVigenza(Node node, int start, int end, VigenzaEntity vigenza) {
		
		selectedNode=node;

		if(node==null) return false;
		
		if(node.getNodeValue()==null){
			if(UtilDom.getTextNode(node)==null || UtilDom.getTextNode(node).trim().equals(""))
				selectedText=node.getNodeName();
			else
				selectedText=UtilDom.getTextNode(node);
		}
		else				
			selectedText=node.getNodeValue();

		
		Document doc = documentManager.getDocumentAsDom();
		
		String text = node.getNodeValue();
		if(UtilDom.isTextNode(node)&&(start!=-1)&&(end!=-1)){
			if(start==end && start!=-1){
				String substr1=selectedText;
				String substr2=selectedText;
				selectedText=substr1.substring(0,start)+"[...]"+substr2.substring(start);
				end=start+5;
			}
			// Creazione Nodo h:span con contenuto
			Element span = doc.createElementNS(UtilDom.getNameSpaceURIforElement(node, "h"), "h:span");
			span.appendChild(doc.createTextNode(selectedText.substring(start,end)));
	
			// Assegnazione attributi di vigenza allo span creato
			span.setAttribute("iniziovigore", vigenza.getEInizioVigore().getId());
			span.setAttribute("finevigore", vigenza.getEFineVigore().getId());
			span.setAttribute("status", vigenza.getStatus());
	
			Node padre = node.getParentNode();
	
			
			try {
				if (start == 0 && end == text.length()) {
					if (vigenza.getEInizioVigore() != null && vigenza.getEFineVigore()!=null) {
						EditTransaction tr = documentManager.beginEdit();
						padre.replaceChild(span, node);
						documentManager.commitEdit(tr);
						return true;
					} 
					
				} else if (start == 0 && end < text.length()) {
					if (vigenza.getEInizioVigore() != null && vigenza.getEFineVigore()!=null) {
						EditTransaction tr = documentManager.beginEdit();
						padre.insertBefore(span, node);
						padre.replaceChild(doc.createTextNode(selectedText.substring(end)), node);
						documentManager.commitEdit(tr);
						return true;
					}
				} else if (start > 0 && end < text.length()) {
					if (vigenza.getEInizioVigore() != null && vigenza.getEFineVigore()!=null) {
						EditTransaction tr = documentManager.beginEdit();
						padre.insertBefore(doc.createTextNode(selectedText.substring(0,start)), node);
						padre.insertBefore(span, node);
						padre.replaceChild(doc.createTextNode(selectedText.substring(end)), node);
						documentManager.commitEdit(tr);
						return true;
					}
				} else { // start > 0 && end = text.length
					if (vigenza.getEInizioVigore() != null && vigenza.getEFineVigore()!=null) {
						EditTransaction tr = documentManager.beginEdit();
						padre.insertBefore(doc.createTextNode(selectedText.substring(0,start)), node);
						padre.replaceChild(span, node);
						documentManager.commitEdit(tr);
						return true;
					}
				}
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				return false;
			}
			
		}else{
			try {
				if (vigenza.getEInizioVigore() != null && vigenza.getEFineVigore()!=null) {
					EditTransaction tr = documentManager.beginEdit();
//					 Assegnazione attributi di vigenza al nodo
					UtilDom.setAttributeValue(node, "iniziovigore", vigenza.getEInizioVigore().getId());
					UtilDom.setAttributeValue(node, "finevigore", vigenza.getEFineVigore().getId());
					UtilDom.setAttributeValue(node, "status", vigenza.getStatus());
										
					documentManager.commitEdit(tr);
					return true;
				} 
			} catch (DocumentManagerException ex) {
				logger.error(ex.getMessage(), ex);
				return false;
			}

		}
		return false;

	}


	public VigenzaEntity getVigenza(Node node, int start, int end) {

		Document doc = documentManager.getDocumentAsDom();
		
		if(node==null)
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
			

			if(node.getAttributes()!=null){
				iniziovig=node.getAttributes().getNamedItem("iniziovigore");
				finevig=node.getAttributes().getNamedItem("finevigore");
				status=node.getAttributes().getNamedItem("status");
			}
				
				if(iniziovig!=null && finevig!=null){

						Element evento_ie_Tag = doc.getElementById(iniziovig.getNodeValue());
						Element evento_fe_Tag = doc.getElementById(finevig.getNodeValue());
						Element evento_fonte_Tag = doc.getElementById(evento_ie_Tag.getAttribute("fonte"));
						
						String tag, id, link, effetto;
						Relazione rel = null;
		
						if (evento_fonte_Tag.getNodeType() == Node.ELEMENT_NODE) {
							tag = evento_fonte_Tag.getNodeName();
							id = evento_fonte_Tag.getAttributes().getNamedItem("id") != null ? evento_fonte_Tag.getAttributes().getNamedItem("id").getNodeValue() : null;
							link = evento_fonte_Tag.getAttributes().getNamedItem("xlink:href") != null ? evento_fonte_Tag.getAttributes().getNamedItem("xlink:href")
									.getNodeValue() : null;
						    if(tag.equals("giurisprudenza")){
						    	effetto=evento_fonte_Tag.getAttributes().getNamedItem("effetto") != null ? evento_fonte_Tag.getAttributes().getNamedItem("effetto").getNodeValue() : null;
							    rel=new Relazione(tag, id, link,effetto);
						    }else 
						    	rel=new Relazione(tag, id, link);
						}
						
			
						e_iniziovig=new Evento(iniziovig.getNodeValue(),
								evento_ie_Tag.getAttribute("data"),//				
								rel,
								evento_ie_Tag.getAttribute("tipo"));
					
						evento_fonte_Tag = doc.getElementById(evento_fe_Tag.getAttribute("fonte"));
						
						if (evento_fonte_Tag.getNodeType() == Node.ELEMENT_NODE) {
							tag = evento_fonte_Tag.getNodeName();
							id = evento_fonte_Tag.getAttributes().getNamedItem("id") != null ? evento_fonte_Tag.getAttributes().getNamedItem("id").getNodeValue() : null;
							link = evento_fonte_Tag.getAttributes().getNamedItem("xlink:href") != null ? evento_fonte_Tag.getAttributes().getNamedItem("xlink:href")
									.getNodeValue() : null;
						    if(tag.equals("giurisprudenza")){
						    	effetto=evento_fonte_Tag.getAttributes().getNamedItem("effetto") != null ? evento_fonte_Tag.getAttributes().getNamedItem("effetto").getNodeValue() : null;
							    rel=new Relazione(tag, id, link,effetto);
						    }else 
						    	rel=new Relazione(tag, id, link);
						}
						e_finevig=new Evento(finevig.getNodeValue(),
								evento_fe_Tag.getAttribute("data"),
								rel,
								evento_fe_Tag.getAttribute("tipo"));
						
						
					
						return new VigenzaEntity(e_iniziovig,
							e_finevig, status.getNodeValue(),selectedText);
				}else{
					return null;
				}
		}else{//c'è lo span
			
//					 Recupero Nodo h:span con contenuto
			if(node.getNodeValue()==null){
				if(UtilDom.getTextNode(node)==null || UtilDom.getTextNode(node).trim().equals(""))
					selectedText=node.getNodeName();					
				else
					selectedText=UtilDom.getTextNode(node);		
				
			}
			else				
				selectedText=node.getNodeValue();
			
			
			
			if(start==end && start!=-1){
				String substr1=selectedText;
				String substr2=selectedText;
				selectedText=substr1.substring(0,start)+"[...]"+substr2.substring(start);
				end=start+5;
			}
			if(start!=end)
				selectedText=selectedText.substring(start,end);
			
							
			
			if(node.getParentNode()!=null && node.getParentNode().getNodeName().equals("h:span")){
					Node span=node.getParentNode();

					iniziovig=span.getAttributes().getNamedItem("iniziovigore");
					finevig=span.getAttributes().getNamedItem("finevigore");
					status=span.getAttributes().getNamedItem("status");
					
					for(int i=0;i<span.getChildNodes().getLength();i++){
						//il figlio trovato ha il testo							
						if(span.getChildNodes().item(i).getNodeValue().equals(selectedText)){
							Element evento_ie_Tag = doc.getElementById(iniziovig.getNodeValue());
							Element evento_fe_Tag = doc.getElementById(finevig.getNodeValue());
							Element evento_fonte_Tag = doc.getElementById(evento_ie_Tag.getAttribute("fonte"));
							
							String tag, id, link, effetto;
							Relazione rel = null;
			
							if (evento_fonte_Tag.getNodeType() == Node.ELEMENT_NODE) {
								tag = evento_fonte_Tag.getNodeName();
								id = evento_fonte_Tag.getAttributes().getNamedItem("id") != null ? evento_fonte_Tag.getAttributes().getNamedItem("id").getNodeValue() : null;
								link = evento_fonte_Tag.getAttributes().getNamedItem("xlink:href") != null ? evento_fonte_Tag.getAttributes().getNamedItem("xlink:href")
										.getNodeValue() : null;
							    if(tag.equals("giurisprudenza")){
							    	effetto=evento_fonte_Tag.getAttributes().getNamedItem("effetto") != null ? evento_fonte_Tag.getAttributes().getNamedItem("effetto").getNodeValue() : null;
								    rel=new Relazione(tag, id, link,effetto);
							    }else 
							    	rel=new Relazione(tag, id, link);
							}
							
				
							e_iniziovig=new Evento(iniziovig.getNodeValue(),
									evento_ie_Tag.getAttribute("data"),				
									rel,
									evento_ie_Tag.getAttribute("tipo"));
						
							evento_fonte_Tag = doc.getElementById(evento_fe_Tag.getAttribute("fonte"));
							
							if (evento_fonte_Tag.getNodeType() == Node.ELEMENT_NODE) {
								tag = evento_fonte_Tag.getNodeName();
								id = evento_fonte_Tag.getAttributes().getNamedItem("id") != null ? evento_fonte_Tag.getAttributes().getNamedItem("id").getNodeValue() : null;
								link = evento_fonte_Tag.getAttributes().getNamedItem("xlink:href") != null ? evento_fonte_Tag.getAttributes().getNamedItem("xlink:href")
										.getNodeValue() : null;
							    if(tag.equals("giurisprudenza")){
							    	effetto=evento_fonte_Tag.getAttributes().getNamedItem("effetto") != null ? evento_fonte_Tag.getAttributes().getNamedItem("effetto").getNodeValue() : null;
								    rel=new Relazione(tag, id, link,effetto);
							    }else 
							    	rel=new Relazione(tag, id, link);
							}
							e_finevig=new Evento(finevig.getNodeValue(),
									evento_fe_Tag.getAttribute("data"),
									rel,
									evento_fe_Tag.getAttribute("tipo"));
						
						return new VigenzaEntity(e_iniziovig,
								e_finevig, 
								status.getNodeValue(),
								selectedText);
						
						}//if									
								
					}//for
			}//if
				
					
		}//else		
		
		return null;
	}//metodo

	public String getSelectedText() {
		return selectedText;
	}

}
