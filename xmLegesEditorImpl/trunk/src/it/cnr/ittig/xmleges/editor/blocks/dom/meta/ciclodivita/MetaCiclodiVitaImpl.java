package it.cnr.ittig.xmleges.editor.blocks.dom.meta.ciclodivita;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.document.DocumentManagerException;
import it.cnr.ittig.xmleges.core.services.document.EditTransaction;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.services.util.ui.UtilUI;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.MetaCiclodivita;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.dom.vigenza.VigenzaEntity;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.CiclodiVita</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class MetaCiclodiVitaImpl implements MetaCiclodivita, Loggable, Serviceable {

	Logger logger;

	NirUtilDom nirUtilDom;

	DocumentManager documentManager;
	
	Rinumerazione rinumerazione; 

	UtilRulesManager utilRulesManager;
	
	UtilUI utilUI;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		utilUI = (UtilUI) serviceManager.lookup(UtilUI.class);
	}

	// /////////////////////////////////////////////// CiclodiVita Interface

	public Relazione[] getRelazioni() {

		Document doc = documentManager.getDocumentAsDom();

		String tag, id, link, effetto_tipoall;
		Vector relVect = new Vector();

		NodeList relazioni = doc.getElementsByTagName("relazioni");
		if (relazioni.getLength() > 0) {
			NodeList relazioniList = relazioni.item(0).getChildNodes();
			for (int i = 0; i < relazioniList.getLength(); i++) {
				Node relazioneNode = relazioniList.item(i);
				if (relazioneNode.getNodeType() == Node.ELEMENT_NODE) {
					tag = relazioneNode.getNodeName();
					id = relazioneNode.getAttributes().getNamedItem("id") != null ? relazioneNode.getAttributes().getNamedItem("id").getNodeValue() : null;
					link = relazioneNode.getAttributes().getNamedItem("xlink:href") != null ? relazioneNode.getAttributes().getNamedItem("xlink:href")
							.getNodeValue() : null;
				    if(tag.equals("giurisprudenza")){
				    	effetto_tipoall=relazioneNode.getAttributes().getNamedItem("effetto") != null ? relazioneNode.getAttributes().getNamedItem("effetto").getNodeValue() : null;
					    relVect.add(new Relazione(tag, id, link,effetto_tipoall));
				    }else if(tag.equals("haallegato")||tag.equals("allegatodi")){
				    	effetto_tipoall=relazioneNode.getAttributes().getNamedItem("tipo") != null ? relazioneNode.getAttributes().getNamedItem("tipo").getNodeValue() : null;
					    relVect.add(new Relazione(tag, id, link,effetto_tipoall));
				    }else
				    	relVect.add(new Relazione(tag, id, link));
				}
			}
		}
		Relazione[] rel = new Relazione[relVect.size()];
		relVect.copyInto(rel);
		return rel;
	}
	
	public Relazione[] mergeRelazioni(Evento[] eventi, Relazione[] relazioniUlteriori){	
		//		 Ricomponi le relazioni eliminando quelle duplicate (caso di +eventi linkati ad 1 relazione)
		Vector relazioniVect = new Vector();
		boolean duplicated;
		
		for (int i = 0; i < eventi.length; i++) {
			duplicated = false;
			
			for(int j=0; j<relazioniVect.size();j++){
		        if(((Relazione)relazioniVect.get(j)).getId().equalsIgnoreCase(eventi[i].getFonte().getId()))
		        	duplicated = true;
			}
			if(!duplicated)
				relazioniVect.add(eventi[i].getFonte());		
		}
		for (int i = 0; i < relazioniUlteriori.length; i++) {
			relazioniVect.add(relazioniUlteriori[i]);
		}

		Relazione[] newRelazioni = new Relazione[relazioniVect.size()];
		relazioniVect.copyInto(newRelazioni);
		
		return newRelazioni;
	}
	
	public Relazione[] getRelazioniUlteriori(Evento[] eventi, Relazione[] relazioni){
		
		// Dividi le relazioni in relazioni legate agli eventi e relazioni
		// ulteriori.
		// Le relazioni legate agli eventi vengono passate all'interno degli eventi stessi,
		// mentre le relazioni ulteriori con setRelazioni.

		Vector relazioniUlterioriVect = new Vector();

		for (int i = 0; i < relazioni.length; i++) {
			boolean found = false;
			for (int j = 0; j < eventi.length; j++) {
				if (relazioni[i].getId().equals(eventi[j].getFonte().getId())) {
					found = true;
					break;
				}
			}
			if (!found) {
				relazioniUlterioriVect.add(relazioni[i]);
			}
		}

		Relazione[] relazioniUlteriori = new Relazione[relazioniUlterioriVect.size()];
		relazioniUlterioriVect.copyInto(relazioniUlteriori);
		
		return relazioniUlteriori;
	}
	
	public void setRelazioni(Relazione[] relazioni) {
		
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (setDOMRelazioni(relazioni)) {
				//rinumerazione.aggiorna(documentManager.getDocumentAsDom());
				documentManager.commitEdit(tr);
			} else
				documentManager.rollbackEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.toString() + " DocumentManagerException in CiclodiVita");
			}
	}
	
	private boolean setDOMRelazioni(Relazione[] relazioni) {

		Document doc = documentManager.getDocumentAsDom();
		Node ciclodivitaNode = doc.getElementsByTagName("ciclodivita").item(0);
		
		boolean missingciclodivita = false;
		
		if (ciclodivitaNode==null){
			ciclodivitaNode = doc.createElement("ciclodivita");
			missingciclodivita = true;
		}
		
		Node relazioniNode = doc.createElement("relazioni");
		for (int i = 0; i < relazioni.length; i++) {
			if (relazioni[i].getId() != null && relazioni[i].getLink() != null && !(relazioni[i].getLink().trim().equals(""))) {
				Element relazione = doc.createElement(relazioni[i].getTagTipoRelazione());
				UtilDom.setIdAttribute(relazione, relazioni[i].getId());
				relazione.setAttribute("xlink:href", relazioni[i].getLink());				
				if( (relazioni[i].getEffetto_tipoall()!=null)&&(!relazioni[i].getEffetto_tipoall().equals(""))){
					if(relazioni[i].getTagTipoRelazione().equals("giurisprudenza"))
						relazione.setAttribute("effetto", relazioni[i].getEffetto_tipoall());
					else //caso allegato
						relazione.setAttribute("tipo", relazioni[i].getEffetto_tipoall());
				}
				if(!utilRulesManager.orderedInsertChild(relazioniNode,relazione))
					System.out.println("relazione non inserita");				
			}
		}		
		// se ho svuotato il nodo relazioni lo rimetto vuoto perchè è obbligatorio
		if(relazioniNode.getChildNodes().getLength()==0)
			relazioniNode = utilRulesManager.getNodeTemplate("relazioni");

		NodeList oldTag = doc.getElementsByTagName("relazioni");
		if (oldTag.getLength() > 0) // c'era gia' un nodo relazioni
			ciclodivitaNode.replaceChild(relazioniNode, oldTag.item(0));
		else 
			utilRulesManager.orderedInsertChild(ciclodivitaNode,relazioniNode);
		
		
		if(missingciclodivita && relazioni.length>0){
			Node metaNode = doc.getElementsByTagName("meta").item(0);
			utilRulesManager.orderedInsertChild(metaNode,ciclodivitaNode);
		}
		
		return true;
	}

	
	public Evento[] getEventi() {
		Document doc = documentManager.getDocumentAsDom();
		NodeList eventiList = doc.getElementsByTagName("evento");
		Vector eventiVect = new Vector();
		String id, data, tipo;
		Relazione fonte;
		

		for (int i = 0; i < eventiList.getLength();i++) {
			Node eventoNode = eventiList.item(i);
			id = eventoNode.getAttributes().getNamedItem("id") != null ? eventoNode.getAttributes().getNamedItem("id").getNodeValue() : null;
			data = eventoNode.getAttributes().getNamedItem("data") != null ? eventoNode.getAttributes().getNamedItem("data").getNodeValue() : null;
			tipo = eventoNode.getAttributes().getNamedItem("tipo") != null ? eventoNode.getAttributes().getNamedItem("tipo").getNodeValue() : null;
			Node nodeFonte = eventoNode.getAttributes().getNamedItem("fonte");
			if (nodeFonte != null) {
				fonte = getRelazioneById(nodeFonte.getNodeValue())!=null ? getRelazioneById(nodeFonte.getNodeValue()) : null;
				eventiVect.add(new Evento(id, data, fonte,tipo));
			}
		}
		Evento[] eventi = new Evento[eventiVect.size()];
		eventiVect.copyInto(eventi);
		
		return eventi;
	}

	public String[] getEventiOnVigenza() {
		Vector totali=new Vector();
		Document doc = documentManager.getDocumentAsDom();
		
		NodeList lista = doc.getElementsByTagName("*");
		for(int i=0; i<lista.getLength();i++){
			NamedNodeMap attributo=lista.item(i).getAttributes();
			Node inizio=attributo.getNamedItem("iniziovigore");
			Node fine=attributo.getNamedItem("finevigore");
		
			if(inizio!=null){
				if(!totali.contains(inizio.getNodeValue()))							
					totali.add(inizio.getNodeValue());
			}
			if(fine!=null)
				if(!totali.contains(fine.getNodeValue()))
					totali.add(fine.getNodeValue());		
			
		}
	
		String[] id_trovati=new String[totali.size()];
		totali.copyInto(id_trovati);
		return id_trovati;
					
	}
	
	
	
	public void setEventi(Evento[] eventi) {
		
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (setDOMEventi(eventi)) {
				//rinumerazione.aggiorna(documentManager.getDocumentAsDom());
				documentManager.commitEdit(tr);
			} else
				documentManager.rollbackEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.toString() + " DocumentManagerException in CiclodiVita");
			}
	}
	
	private boolean setDOMEventi(Evento[] eventi) {

		Document doc = documentManager.getDocumentAsDom();
		Node ciclodivitaNode = doc.getElementsByTagName("ciclodivita").item(0);

		boolean missingciclodivita = false;
		
		
		if (ciclodivitaNode==null){
			ciclodivitaNode = utilRulesManager.getNodeTemplate("ciclodivita");
			missingciclodivita = true;
		}
		
		removeTagByName("eventi");
		Element eventiTag = doc.createElement("eventi");
		for (int i = 0; i < eventi.length; i++) {
				Element eventoTag = doc.createElement("evento");
				UtilDom.setIdAttribute(eventoTag, eventi[i].getId());
				UtilDom.setAttributeValue(eventoTag, "data", eventi[i].getData());
				UtilDom.setAttributeValue(eventoTag, "fonte", eventi[i].getFonte().getId());
				UtilDom.setAttributeValue(eventoTag, "tipo", eventi[i].getTipoEvento());
				utilRulesManager.orderedInsertChild(eventiTag,eventoTag);
		}
		if (eventi.length > 0)
		  utilRulesManager.orderedInsertChild(ciclodivitaNode,eventiTag);
		if(missingciclodivita && eventi.length>0){
			Node metaNode = doc.getElementsByTagName("meta").item(0);
			utilRulesManager.orderedInsertChild(metaNode,ciclodivitaNode);
		}
		
		return true;
	}
	
	
	private Relazione getRelazioneById(String relId) {


		Document doc = documentManager.getDocumentAsDom();
		Node relNode = doc.getElementById(relId);
		if(relNode != null){
		    String tag = relNode.getNodeName();
			String id = relNode.getAttributes().getNamedItem("id") != null ? relNode.getAttributes().getNamedItem("id").getNodeValue() : null;
			String link = relNode.getAttributes().getNamedItem("xlink:href") != null ? relNode.getAttributes().getNamedItem("xlink:href").getNodeValue():null;
			if(tag.equals("giurisprudenza")){
		    	String effetto=relNode.getAttributes().getNamedItem("effetto") != null ? relNode.getAttributes().getNamedItem("effetto").getNodeValue() : null;
		    	return (new Relazione(tag, id, link,effetto));
		    }else if(tag.equals("haallegato")||tag.equals("allegatodi")){
		    	String tipo=relNode.getAttributes().getNamedItem("tipo") != null ? relNode.getAttributes().getNamedItem("tipo").getNodeValue() : null;
		    	return (new Relazione(tag, id, link,tipo));
		    }else
		    	return (new Relazione(tag, id, link));			
		}
		return null;
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

	public VigenzaEntity[] getVigenze() {
		
		Vector vigenze_totali=new Vector();
		Vector totali=new Vector();
		Document doc = documentManager.getDocumentAsDom();
		//caricamento nodi con attributi di vigenza
		NodeList lista = doc.getElementsByTagName("*");
		for(int i=0; i<lista.getLength();i++){
			if(lista.item(i).getAttributes()!=null){
				if(lista.item(i).getAttributes().getNamedItem("iniziovigore")!=null){
					
					totali.add(lista.item(i));
				}
													
			}
		
		}
		
		Evento e_iniziovig=null;
		Evento e_finevig=null;
		Node iniziovig=null;
		Node finevig=null;
		Node status=null;
		
		
		
		for(int i=0; i<totali.size();i++){
			Node node=(Node)totali.elementAt(i);
				
			iniziovig=node.getAttributes().getNamedItem("iniziovigore");
			finevig=node.getAttributes().getNamedItem("finevigore");
			status=node.getAttributes().getNamedItem("status");			
				
			Element evento_ie_Tag = (iniziovig==null?null:doc.getElementById(iniziovig.getNodeValue()));
			Element evento_fe_Tag = (finevig==null?null:doc.getElementById(finevig.getNodeValue()));
			
			//qui rappresenta la fonte dell'evento di inizio
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
			    }else if(tag.equals("haallegato")||tag.equals("allegatodi")){
			    	tipo=evento_fonte_Tag.getAttributes().getNamedItem("tipo") != null ? evento_fonte_Tag.getAttributes().getNamedItem("tipo").getNodeValue() : null;
				    rel=new Relazione(tag, id, link,tipo);
			    }else  
			    	rel=new Relazione(tag, id, link);
			}
			e_iniziovig=( 
					iniziovig==null?null:new Evento(iniziovig!=null?iniziovig.getNodeValue():"",
					evento_ie_Tag!=null?evento_ie_Tag.getAttribute("data"):null,//				
					rel,
					evento_ie_Tag!=null?evento_ie_Tag.getAttribute("tipo"):null)
			);
			
			//qui rappresenta la fonte dell'evento di fine
			evento_fonte_Tag = (evento_fe_Tag==null?null:doc.getElementById(evento_fe_Tag.getAttribute("fonte")));
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
			e_finevig=(
					finevig==null?null:new Evento(finevig!=null?finevig.getNodeValue():"",
					evento_fe_Tag!=null?evento_fe_Tag.getAttribute("data"):null,
					rel,
					evento_fe_Tag!=null?evento_fe_Tag.getAttribute("tipo"):null)
			);
			
			vigenze_totali.add(new VigenzaEntity(node, e_iniziovig,
						e_finevig, status!=null?status.getNodeValue():null,""));
					
							
		}//for	
						//////////////////////
		VigenzaEntity[] vig_trovate=new VigenzaEntity[vigenze_totali.size()];
		vigenze_totali.copyInto(vig_trovate);
		return vig_trovate;
					
	}

}
