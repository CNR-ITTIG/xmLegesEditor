package it.cnr.ittig.xmleges.editor.blocks.dom.meta.descrittori;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Evento;
import it.cnr.ittig.xmleges.editor.services.dom.meta.ciclodivita.Relazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittori;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Pubblicazione;
import it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.Redazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori.MetaDescrittori</code>.
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
public class MetaDescrittoriImpl implements MetaDescrittori, Loggable, Serviceable {

	Logger logger;

	DtdRulesManager dtdRulesManager;
	DocumentManager documentManager;
	NirUtilDom nirUtilDom;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
	}

	// ///////////////////////////////////////////////////// MetaDescrittori
	// Interface
		

	public Pubblicazione getPubblicazione(Node node) {
		Document doc = documentManager.getDocumentAsDom();
		String tipo = null;
		String num = null;
		String data = null;

		// cerca il nodo pubblicazione del documento a cui appartiene il nodo attivo
		
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		Node n = UtilDom.findRecursiveChild(activeMeta,"pubblicazione");
		
		if(n!=null){
			tipo = n.getAttributes().getNamedItem("tipo") != null ? n.getAttributes().getNamedItem("tipo").getNodeValue() : "GU";
			num = n.getAttributes().getNamedItem("num") != null ? n.getAttributes().getNamedItem("num").getNodeValue() : null;
			data = n.getAttributes().getNamedItem("norm") != null ? n.getAttributes().getNamedItem("norm").getNodeValue() : null;
		}
		return (new Pubblicazione("pubblicazione", tipo, num, data));
	}

	public void setPubblicazione(Node node, Pubblicazione pubblicazione) {

		Document doc = documentManager.getDocumentAsDom();
		
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		Node descrittoriNode = UtilDom.findRecursiveChild(activeMeta,"descrittori");
		
		if (pubblicazione.getNum() != null || pubblicazione.getNorm() != null) {
			Node oldTag =  UtilDom.findRecursiveChild(activeMeta,"pubblicazione");
			Element pubTag;
			pubTag = doc.createElement("pubblicazione");
			pubTag.setAttribute("tipo", pubblicazione.getTipo());
			pubTag.setAttribute("norm", pubblicazione.getNorm());
			if (pubblicazione.getNum() != null)
				pubTag.setAttribute("num", pubblicazione.getNum());
			descrittoriNode.replaceChild(pubTag, oldTag);
		}
	}

	

	public String[] getAlias(Node node) {
		Document doc = documentManager.getDocumentAsDom();

		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		Node[] aliasList = UtilDom.getElementsByTagName(doc,activeMeta,"alias");
		
		Vector aliasVect = new Vector();

		for (int i = 0; i < aliasList.length; i++)
			aliasVect.add(UtilDom.getAttributeValueAsString(aliasList[i],"valore"));

		String[] alias = new String[aliasVect.size()];
		aliasVect.copyInto(alias);
		return alias;
	}

	public void setAlias(Node node, String[] alias) {
		
		Document doc = documentManager.getDocumentAsDom();
		
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		Node descrittoriNode = UtilDom.findRecursiveChild(activeMeta,"descrittori");

		removeMetaByName("alias", node);
		for (int i = 0; i < alias.length; i++) {
			Element aliasTag;
			aliasTag = doc.createElement("alias");
			UtilDom.setAttributeValue(aliasTag,"valore",alias[i]);
			Node child = descrittoriNode.getFirstChild();
			boolean inserted = false;
			do {
				try {
					if (dtdRulesManager.queryCanInsertBefore(descrittoriNode, child, aliasTag)) {
						UtilDom.insertAfter(aliasTag, child.getPreviousSibling());
						inserted = true;
					}
					child = child.getNextSibling();
				} catch (DtdRulesManagerException ex) {
					logger.error(ex.getMessage(), ex);
				}
			} while (!inserted && child != null);
			try {
				if (!inserted && dtdRulesManager.queryCanAppend(descrittoriNode, aliasTag))
					descrittoriNode.appendChild(aliasTag);
			} catch (DtdRulesManagerException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
		
	}


	public Redazione[] getRedazioni(Node node) {
		
		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		Node[] redazioniList = UtilDom.getElementsByTagName(doc,activeMeta,"redazione");
		Vector redazioniVect = new Vector();
		String data = null;
		String nome = null;
		String url = null;
		String contributo = null;
				

		for (int i = 0; i < redazioniList.length;i++) {
			Node redazioneNode = redazioniList[i];
			if (redazioneNode!=null) {
				Node n = redazioneNode;
				data = n.getAttributes().getNamedItem("norm") != null ? n.getAttributes().getNamedItem("norm").getNodeValue() : null;
				nome = n.getAttributes().getNamedItem("nome") != null ? n.getAttributes().getNamedItem("nome").getNodeValue() : null;
				url = n.getAttributes().getNamedItem("url") != null ? n.getAttributes().getNamedItem("url").getNodeValue() : null;
				contributo = n.getAttributes().getNamedItem("contributo") != null ? n.getAttributes().getNamedItem("contributo").getNodeValue() : null;
				if ( (data!=null&&!data.trim().equals("")) 
						|| (nome!=null&&!nome.trim().equals("")) 
						|| (url!=null&&!url.trim().equals("")) 
						|| (contributo!=null&&!contributo.trim().equals("")) )
					redazioniVect.add(new Redazione(data,nome,url,contributo));
			}
			
		}
		Redazione[] redazioni = new Redazione[redazioniVect.size()];
		redazioniVect.copyInto(redazioni);
		
		return redazioni;
		
		
//		return (new String[]{data,nome, url,contributo});
		
	}
	
	
	public void setRedazioni(Node node, Redazione[] redazioni) {
//		
		Document doc = documentManager.getDocumentAsDom();
		Node activeMeta = nirUtilDom.findActiveMeta(doc,node);
		Node descrittoriNode = UtilDom.findRecursiveChild(activeMeta,"descrittori");
		
		removeMetaByName("redazione", node);
		
		if(redazioni!=null && redazioni.length>0){
			
			for (int i = 0; i < redazioni.length; i++) {
				Element redazioneTag;
				redazioneTag = doc.createElement("redazione");
				redazioneTag.setAttribute("norm", redazioni[i].getData());
				redazioneTag.setAttribute("nome", redazioni[i].getNome());
				redazioneTag.setAttribute("url", redazioni[i].getUrl());
				redazioneTag.setAttribute("contributo", redazioni[i].getContributo());
				Node child = descrittoriNode.getFirstChild();
				boolean inserted = false;
				do {
					try {
						if (dtdRulesManager.queryCanInsertBefore(descrittoriNode, child, redazioneTag)) {
							UtilDom.insertAfter(redazioneTag, child.getPreviousSibling());
							inserted = true;
						}
						child = child.getNextSibling();
					} catch (DtdRulesManagerException ex) {
						logger.error(ex.getMessage(), ex);
					}
				} while (!inserted && child != null);
				try {
					if (!inserted && dtdRulesManager.queryCanAppend(descrittoriNode, redazioneTag))
						descrittoriNode.appendChild(redazioneTag);
				} catch (DtdRulesManagerException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}else{
				Element redazioneTag;
				redazioneTag = doc.createElement("redazione");
				
				Node child = descrittoriNode.getFirstChild();
				boolean inserted = false;
				do {
					try {
						if (dtdRulesManager.queryCanInsertBefore(descrittoriNode, child, redazioneTag)) {
							UtilDom.insertAfter(redazioneTag, child.getPreviousSibling());
							inserted = true;
						}
						child = child.getNextSibling();
					} catch (DtdRulesManagerException ex) {
						logger.error(ex.getMessage(), ex);
					}
				} while (!inserted && child != null);
				try {
					if (!inserted && dtdRulesManager.queryCanAppend(descrittoriNode, redazioneTag))
						descrittoriNode.appendChild(redazioneTag);
				} catch (DtdRulesManagerException ex) {
					logger.error(ex.getMessage(), ex);
				}
				
			}
		
		
//		if (redazioni != null) {
//			Node redazioneNode = doc.createElement("redazione");
//			for (int i = 0; i < redazioni.length; i++) {
//				
//				Node oldTag = UtilDom.findRecursiveChild(activeMeta,"redazione");
//				Element redTag;
//				redTag = doc.createElement("redazione");			
//				redTag.setAttribute("norm", redazioni[i].getData());
//				redTag.setAttribute("nome", redazioni[i].getNome());
//				redTag.setAttribute("url", redazioni[i].getUrl());
//				redTag.setAttribute("contributo", redazioni[i].getContributo());
//				
//				descrittoriNode.replaceChild(redTag, oldTag);
//			}
//		}
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
