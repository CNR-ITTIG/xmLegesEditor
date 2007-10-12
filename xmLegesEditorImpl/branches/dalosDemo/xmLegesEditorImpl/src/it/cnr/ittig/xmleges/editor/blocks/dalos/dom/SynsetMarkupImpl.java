package it.cnr.ittig.xmleges.editor.blocks.dalos.dom;

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
import it.cnr.ittig.xmleges.editor.services.dalos.dom.SynsetMarkup;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
public class SynsetMarkupImpl implements SynsetMarkup, Loggable, Serviceable {
	

	Logger logger;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;
	
	UtilRulesManager utilRulesManager;
		
	Rinumerazione rinumerazione;
		
	Node selectedNode;
		

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
		
	}

	
	
	public boolean canSetSynset(Node node) {
		if (node != null && node.getParentNode() != null) {
				return (node.getNodeName()!=null &&  UtilDom.isTextNode(node));
		}
		return false;
	}
	

	

	public Node setSynset(Node node,  int start, int end, Synset synset) {
		return(setSynset(node,start,end,synset,synset.getLexicalForm()));
	}
	
	
	public Node setSynset(Node node, int start, int end, Synset synset, String variant) {
		Node ret = null;
		if(canSetSynset(node)){
			try {
				EditTransaction tr = documentManager.beginEdit();
				if ((ret=setDOMSynset(node, start, end, synset,variant))!=null) {
					rinumerazione.aggiorna(documentManager.getDocumentAsDom());
					documentManager.commitEdit(tr);
				} else
					documentManager.rollbackEdit(tr);
			} catch (DocumentManagerException ex) {
				logger.error(ex.toString() + " DocumentManagerException in setSynset");
				return null;
			}
		}
		return ret;
	}
	
	
	// correggere::   se sono dentro uno span dalos; sostituisco tutto lo span indip dal testo selezionato
	private Node setDOMSynset(Node node,  int start, int end, Synset synset, String variant) {

		if(UtilDom.isTextNode(node)){
			Element span;
			if(node.getNodeValue().equals(node.getNodeValue().substring(start,end))){
				//il testo selezionato coincide con tutto il nodo
				span = (Element) node.getParentNode();
			}
			else{       
				// il testo selezionato e' una sottoparte del nodo di testo (va creato lo span)								
				//qui crea uno span dal testo selezionato 
				span = (Element) utilRulesManager.encloseTextInTag(node, start, end,"h:span","h");	
			}
			UtilDom.setTextNode(span, variant);
			UtilDom.setAttributeValue(span,"h:style","dalos");
			UtilDom.setAttributeValue(span, "h:class", synset.getURI().substring(synset.getURI().indexOf("#")+1));
		return (Node) span;
	}
		return null;
	}	

}
