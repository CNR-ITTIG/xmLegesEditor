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
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.MetaCiclodivita;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
	}

	// /////////////////////////////////////////////// CiclodiVita Interface

	public Relazione[] getRelazioni() {

		Document doc = documentManager.getDocumentAsDom();

		String tag, id, link, effetto;
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
				    	effetto=relazioneNode.getAttributes().getNamedItem("effetto") != null ? relazioneNode.getAttributes().getNamedItem("effetto").getNodeValue() : null;
					    relVect.add(new Relazione(tag, id, link,effetto));
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
		Document doc = documentManager.getDocumentAsDom();
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (setDOMRelazioni(relazioni)) {
				rinumerazione.aggiorna(doc);
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
				if( (relazioni[i].getEffetto()!=null)&&(!relazioni[i].getEffetto().equals(""))){
					relazione.setAttribute("effetto", relazioni[i].getEffetto());
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

		for (int i = 0; i < eventiList.getLength();i++) {
			Node eventoNode = eventiList.item(i);
			id = eventoNode.getAttributes().getNamedItem("id") != null ? eventoNode.getAttributes().getNamedItem("id").getNodeValue() : null;
			data = eventoNode.getAttributes().getNamedItem("data") != null ? eventoNode.getAttributes().getNamedItem("data").getNodeValue() : null;
			tipo = eventoNode.getAttributes().getNamedItem("tipo") != null ? eventoNode.getAttributes().getNamedItem("tipo").getNodeValue() : null;
			Node nodeFonte = eventoNode.getAttributes().getNamedItem("fonte");
			if (nodeFonte != null) {
				Relazione fonte = getRelazioneById(nodeFonte.getNodeValue());
				eventiVect.add(new Evento(id, data, fonte,tipo));
			}
		}
		Evento[] eventi = new Evento[eventiVect.size()];
		eventiVect.copyInto(eventi);
		
		return eventi;
	}

	
	public void setEventi(Evento[] eventi) {
		Document doc = documentManager.getDocumentAsDom();
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (setDOMEventi(eventi)) {
				rinumerazione.aggiorna(doc);
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
		
//		 FIXME: se cancello gli eventi devo cancellare anche le relazioni linkate !!
		
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
		if (eventi.length > 0){
		  utilRulesManager.orderedInsertChild(ciclodivitaNode,eventiTag);
		  UtilDom.setAttributeValue(doc.getDocumentElement(),"tipo","monovigente");
		}else
			UtilDom.setAttributeValue(doc.getDocumentElement(),"tipo","originale");
		
		
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
		    }else
		    	return (new Relazione(tag, id, link));			
		}
		return null;
	}

	
//	private Relazione getRelazioneById(String relId) {
//
//		Document doc = documentManager.getDocumentAsDom();
//
//		// FIXME Node relNode = doc.getElementById(relId);
//		// Questa chiamata a getElementById non funziona bene... infatti, la
//		// prima volta che viene chiamata
//		// (ad es. con un Document fresco fresco caricato dall'XML) restituisce
//		// correttamente il nodo, mentre
//		// le volte successive restituisce null, e questo malgrado debuggando si
//		// veda che nel Document il
//		// nodo con quell'id c'?. Per ovviare a questo problema prendiamo la
//		// lista delle relazioni e le
//		// scandiamo una per una, cercando quella che ha l'id desiderato.
//
//		Node relazioniNode = doc.getElementsByTagName("relazioni").item(0);
//		NodeList relazioni = relazioniNode.getChildNodes();
//		for (int i = 0; i < relazioni.getLength(); i++) {
//			Node relNode = relazioni.item(i);
//			if (relNode.getAttributes().getNamedItem("id").getNodeValue().equals(relId)) {
//				String tag = relNode.getNodeName();
//				String id = relNode.getAttributes().getNamedItem("id") != null ? relNode.getAttributes().getNamedItem("id").getNodeValue() : null;
//				String link = relNode.getAttributes().getNamedItem("xlink:href") != null ? relNode.getAttributes().getNamedItem("xlink:href").getNodeValue()
//						: null;
//				return (new Relazione(tag, id, link));
//			}
//		}
//		return null;
//	}
	
	
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
