package it.cnr.ittig.xmleges.editor.blocks.dom.meta.ciclodivita;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.CiclodiVita;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
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
public class CiclodiVitaImpl implements CiclodiVita, Loggable, Serviceable {

	Logger logger;

	NirUtilDom nirUtilDom;

	//DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	//UtilMsg utilMsg;

	UtilRulesManager utilRulesManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		//dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		//utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
	}

	// ///////////////////////////////////////////////////// MetaDescrittori
	// Interface

	

	// dtd 1.1
	//<!ELEMENT descrittori   (pubblicazione, altrepubblicazioni?, urn+, alias*, 
	//                         vigenza+, relazioni?,keywords*) >
	
	
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
				    effetto=relazioneNode.getAttributes().getNamedItem("effetto") != null ? relazioneNode.getAttributes().getNamedItem("effetto").getNodeValue() : null;
					// FIXME gestire l'effetto (per il caso giurisprudenza)
				    relVect.add(new Relazione(tag, id, link));
				}
			}
		}
		Relazione[] rel = new Relazione[relVect.size()];
		relVect.copyInto(rel);
		return rel;
	}

	public void setRelazioni(Relazione[] relazioni) {

		Document doc = documentManager.getDocumentAsDom();
		Node ciclodivitaNode = doc.getElementsByTagName("ciclodivita").item(0);
		
		boolean missingciclodivita = false;
		
		if (ciclodivitaNode==null){
			ciclodivitaNode = doc.createElement("ciclodivita");
			missingciclodivita = true;
		}
		
		if (relazioni.length > 0) {
			Node relazioniNode = doc.createElement("relazioni");
			for (int i = 0; i < relazioni.length; i++) {
				if (relazioni[i].getId() != null && relazioni[i].getLink() != null && !(relazioni[i].getLink().trim().equals(""))) {
					Element relazione = doc.createElement(relazioni[i].getTagTipoRelazione());
					UtilDom.setIdAttribute(relazione, relazioni[i].getId());
					relazione.setAttribute("xlink:href", relazioni[i].getLink());
					utilRulesManager.orderedInsertChild(relazioniNode,relazione);
					// FIXME settare l'attributo "effetto" se c'e'
				}
			}

			NodeList oldTag = doc.getElementsByTagName("relazioni");
			if (oldTag.getLength() > 0) // c'era gia' un nodo relazioni
				ciclodivitaNode.replaceChild(relazioniNode, oldTag.item(0));
			else {
				
				utilRulesManager.orderedInsertChild(ciclodivitaNode,relazioniNode);
				
//				Node child = ciclodivitaNode.getFirstChild();
//				boolean inserted = false;
//				do {
//					try {
//						if (dtdRulesManager.queryCanInsertBefore(ciclodivitaNode, child, relazioniNode)) {
//							UtilDom.insertAfter(relazioniNode, child.getPreviousSibling());
//							inserted = true;
//						}
//						child = child.getNextSibling();
//					} catch (DtdRulesManagerException ex) {
//						logger.error(ex.getMessage(), ex);
//					}
//				} while (!inserted && child != null);
//				try {
//					if (!inserted && dtdRulesManager.queryCanAppend(ciclodivitaNode, relazioniNode))
//						ciclodivitaNode.appendChild(relazioniNode);
//				} catch (DtdRulesManagerException ex) {
//					logger.error(ex.getMessage(), ex);
//				}
			}
		}
		if(missingciclodivita && relazioni.length>0){
			Node metaNode = doc.getElementsByTagName("meta").item(0);
			utilRulesManager.orderedInsertChild(metaNode,ciclodivitaNode);
		}		
	}

	
	public Evento[] getEventi() {
		Document doc = documentManager.getDocumentAsDom();
		NodeList eventiList = doc.getElementsByTagName("evento");
		Vector eventiVect = new Vector();
		String id, data;

		for (int i = 0; i < eventiList.getLength();i++) {
			Node eventoNode = eventiList.item(i);
			id = eventoNode.getAttributes().getNamedItem("id") != null ? eventoNode.getAttributes().getNamedItem("id").getNodeValue() : null;
			data = eventoNode.getAttributes().getNamedItem("data") != null ? eventoNode.getAttributes().getNamedItem("data").getNodeValue() : null;
			Node nodeFonte = eventoNode.getAttributes().getNamedItem("fonte");
			if (nodeFonte != null) {
				Relazione fonte = getRelazioneById(nodeFonte.getNodeValue());
				eventiVect.add(new Evento(id, data, fonte));
			}
		}
		Evento[] eventi = new Evento[eventiVect.size()];
		eventiVect.copyInto(eventi);
		return eventi;
	}

	
	public void setEventi(Evento[] eventi) {

		Document doc = documentManager.getDocumentAsDom();
		Node ciclodivitaNode = doc.getElementsByTagName("ciclodivita").item(0);

		boolean missingciclodivita = false;
		
		if (ciclodivitaNode==null){
			ciclodivitaNode = utilRulesManager.getNodeTemplate("ciclodivita");
			missingciclodivita = true;
		}
		
		if (eventi.length > 0) {
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
			utilRulesManager.orderedInsertChild(ciclodivitaNode,eventiTag);		
		}
		
		if(missingciclodivita && eventi.length>0){
			Node metaNode = doc.getElementsByTagName("meta").item(0);
			utilRulesManager.orderedInsertChild(metaNode,ciclodivitaNode);
		}
		
	}
	

	private Relazione getRelazioneById(String relId) {

		Document doc = documentManager.getDocumentAsDom();

		// FIXME Node relNode = doc.getElementById(relId);
		// Questa chiamata a getElementById non funziona bene... infatti, la
		// prima volta che viene chiamata
		// (ad es. con un Document fresco fresco caricato dall'XML) restituisce
		// correttamente il nodo, mentre
		// le volte successive restituisce null, e questo malgrado debuggando si
		// veda che nel Document il
		// nodo con quell'id c'?. Per ovviare a questo problema prendiamo la
		// lista delle relazioni e le
		// scandiamo una per una, cercando quella che ha l'id desiderato.

		Node relazioniNode = doc.getElementsByTagName("relazioni").item(0);
		NodeList relazioni = relazioniNode.getChildNodes();
		for (int i = 0; i < relazioni.getLength(); i++) {
			Node relNode = relazioni.item(i);
			if (relNode.getAttributes().getNamedItem("id").getNodeValue().equals(relId)) {
				String tag = relNode.getNodeName();
				String id = relNode.getAttributes().getNamedItem("id") != null ? relNode.getAttributes().getNamedItem("id").getNodeValue() : null;
				String link = relNode.getAttributes().getNamedItem("xlink:href") != null ? relNode.getAttributes().getNamedItem("xlink:href").getNodeValue()
						: null;
				return (new Relazione(tag, id, link));
			}
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

}
