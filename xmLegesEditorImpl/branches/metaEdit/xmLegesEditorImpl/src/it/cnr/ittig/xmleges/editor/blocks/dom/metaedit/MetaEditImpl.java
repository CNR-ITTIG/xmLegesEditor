package it.cnr.ittig.xmleges.editor.blocks.dom.metaedit;

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
import it.cnr.ittig.xmleges.editor.services.dom.metaedit.MetaEdit;
import it.cnr.ittig.xmleges.editor.services.dom.rinumerazione.Rinumerazione;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;
import it.cnr.ittig.xmleges.editor.services.util.metaedit.ModelloDA;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dom.metaedit.MetaEdit</code>.</h1>
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
public class MetaEditImpl implements MetaEdit, Loggable, Serviceable {
	Logger logger;

	NirUtilDom nirUtilDom;

	DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;
	
	ModelloDA modelloDA;

	UtilRulesManager utilRulesManager;

	Rinumerazione rinumerazione;
	
	private Vector disposizioniNodes;

	static String TAG_POS ="dsp:pos";
	static String ATTR_POS_XLINK_HREF ="xlink:href";
	static String ATTR_POS_XLINK_TYPE ="xlink:type";
	static String ATTR_POS_XLINK_TYPE_VALUE ="simple";
	
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	
	
	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		modelloDA = (ModelloDA) serviceManager.lookup(ModelloDA.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		rinumerazione = (Rinumerazione) serviceManager.lookup(Rinumerazione.class);
	}

	
	
	//	 ///////////////////////////////////////////////// MetaEdit Interface
	public Node[] getDAFromDocument(String idPartition) {
		
		if(idPartition == null)
			return null;
		
		Vector ret = new Vector();
		Node disposizioni = ((Document)documentManager.getDocumentAsDom()).getElementsByTagName("disposizioni").item(0);
		disposizioniNodes = new Vector(); // vettore contenente come elementi nodi
		Vector disposizioniList = modelloDA.getDisposizioniList();
		getDisposizioniFromDoc(disposizioni, disposizioniList);
		
		for(int i=0; i<disposizioniNodes.size();i++){
			// controlla se la disposizione si riferisce alla partizione con id "idPartition"
			String pos = UtilDom.getAttributeValueAsString(UtilDom.findDirectChild((Node)disposizioniNodes.get(i),"dsp:pos"),"xlink:href");
			pos=pos.startsWith("#")?pos.substring(1):pos;
			if(pos.equalsIgnoreCase(idPartition))
				ret.add(createInterchangeNode((Node)disposizioniNodes.get(i)));
		}
		
		
		Node[] result = new Node[ret.size()];
		ret.copyInto(result);
		
		return result;
	}
	
	
	// copia e converte un nodo disposizione del documento in un nodo DOM di interscambio contenente le informazioni per il popolamento della form 
	private Node createInterchangeNode(Node node){
				
		// qui si arriva con la disposizione; prendere solo gli argomenti el le keywords  (argomenti in profondità) + dsp:pos
		
		Node root = documentManager.getDocumentAsDom().createElement(node.getNodeName());
		NodeList figli = node.getChildNodes();
		Vector argomentiList = modelloDA.getArgomentiList();
		
		for(int i=0;i<figli.getLength();i++){
			if(argomentiList.contains(figli.item(i).getNodeName()) || figli.item(i).getNodeName().startsWith("dsp:pos")){
				root.appendChild(figli.item(i).cloneNode(true));
			}
		}
		return root;
	}
	
	
	
	// mette nel Vector disposizioniNodes  tutti i nodi disposizione
	private void getDisposizioniFromDoc(Node node, Vector disposizioniList){	
		
		if(node!=null){
			if (disposizioniList.contains(node.getNodeName()))
				disposizioniNodes.add(node);
			NodeList list = node.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				getDisposizioniFromDoc(list.item(i), disposizioniList);
			}
		}	
	}
	
	
	
	public void setDAOnDocument(Node[] DAList, String idPartition) {
		try {
			EditTransaction tr = documentManager.beginEdit();
			if (setDADom(DAList, idPartition)) {
				//rinumerazione.aggiorna(doc);
				documentManager.commitEdit(tr);
			} else
				documentManager.rollbackEdit(tr);
		} catch (DocumentManagerException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}
	
	
	
	private boolean setDADom(Node[] DAList, String idPartition) {
		
		
		Document doc = (Document)documentManager.getDocumentAsDom();
		NodeList disposizioniNL = doc.getElementsByTagName("disposizioni");//.item(0);
		
		Node disposizioni = null;
		NodeList templateToRemove = null;
		
		if(disposizioniNL.getLength()>0)
			disposizioni = disposizioniNL.item(0);
		else{
			disposizioni = utilRulesManager.getNodeTemplate("disposizioni");
			// qui da' un template per disposizioni da svuotare !!
			Node meta = nirUtilDom.findActiveMeta(doc, doc.getDocumentElement());
			utilRulesManager.orderedInsertChild(meta,disposizioni);
			templateToRemove = disposizioni.getChildNodes();
		}
		
		// ammesso che ci sia il nodo disposizioni; 
		//  1:  se non c'e'  va creato 
		//  2: nella rimozione per rimpiazzo chiedere al rulesMgr; se rimuovendo creo un nodo non valido poi non reinserisce piu'
		// ci sono un po'troppi nodi clonati
		
		
		disposizioniNodes = new Vector(); // vettore contenente come elementi nodi
		Vector disposizioniList = modelloDA.getDisposizioniList();
		getDisposizioniFromDoc(disposizioni, disposizioniList);
		
		for(int i=0; i<disposizioniNodes.size();i++){
			// controlla se la disposizione si riferisce alla partizione con id "idPartition"
			String pos = UtilDom.getAttributeValueAsString(UtilDom.findDirectChild((Node)disposizioniNodes.get(i),"dsp:pos"),"xlink:href");
			pos=pos.startsWith("#")?pos.substring(1):pos;
			// da controllare con rulesManager.queryCanDelete()
			if(pos.equalsIgnoreCase(idPartition))
				disposizioni.removeChild((Node)disposizioniNodes.get(i));			
		}
		
				
		for(int i=0; i<DAList.length;i++) {
		
		  try{
			  System.err.println("in disposizioni: VALID ? "+dtdRulesManager.queryIsValid(disposizioni));
			  System.err.println("tryying to insert "+UtilDom.domToString(DAList[i], true)+" is Valid ?  "+dtdRulesManager.queryIsValid(DAList[i]));	  
		  }catch(Exception e){
			  e.printStackTrace();
		  }
			
		  //boolean t1 = utilRulesManager.orderedInsertChild(DAList[i],nodePos); //DAList[i].appendChild(nodePos);
		  boolean t2 = utilRulesManager.orderedInsertChild(disposizioni,DAList[i].cloneNode(true)); // disposizioni.appendChild(DAList[i].cloneNode(true));
		  System.err.println("inserted disposizione: "+t2);
		  
		  
//		  // da controllare con rulesManager.queryCanDelete()
//		  for(int j=0; j<templateToRemove.getLength(); j++){
//			  disposizioni.removeChild(templateToRemove.item(j));
//		  }
		  
		  // se il nodo disposizioni rimane vuoto va eliminato
		  
		}
		
		
		
		return true;
		
		
	}
	
	
}
