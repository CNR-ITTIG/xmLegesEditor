package it.cnr.ittig.xmleges.editor.blocks.util.metaedit;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;
import it.cnr.ittig.xmleges.editor.services.util.metaedit.ModelloDA;

import java.util.Vector;
import java.util.Collections;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom</code>.</h1>
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
 * 
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class ModelloDAImpl implements  ModelloDA, Initializable, Loggable, Serviceable{
	
	Logger logger;
	
	Document modelloDA;
	
	Vector disposizioniList;
	
	Vector argomentiList;
	
	static String F_XML_MODELLO_DA = "DisposizioniModello.xml";
	
	
	//	 ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		this.modelloDA = UtilXml.readXML(this.getClass().getResourceAsStream(F_XML_MODELLO_DA));
		UtilDom.trimTextNode((Node)modelloDA, true);
		createDisposizioniList();
		createArgomentiList();
	}

	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
	}

	public Document getModelloDA() {		
		return this.modelloDA;
	}

	
	public Node[] getClassificationPathFor(String disposizione) {		
		Node target = getClassificationFor(disposizione);
		Vector v = new Vector();
		while(target.getParentNode()!=null){
			v.add(target);
			target = target.getParentNode();
		}
		Collections.reverse(v);
		Node[] ret = new Node[v.size()];
		v.copyInto(ret);
		return ret;
	}

	public Node getClassificationFor(String disposizione) {
			NodeList disposizioni = modelloDA.getElementsByTagName("disposizione");
			
			for(int i=0; i< disposizioni.getLength(); i++){
				if(UtilDom.getAttributeValueAsString(disposizioni.item(i), "value").equalsIgnoreCase(disposizione))
					return disposizioni.item(i);
			}
			return null;
	}
	
	public Vector getDisposizioniList(){		
		return this.disposizioniList;
	}
	
	public Vector getArgomentiList(){		
		return this.argomentiList;
	}
	
	
	private void createArgomentiList(){
		
		argomentiList = new Vector();
		
		NodeList argomenti = modelloDA.getElementsByTagName("argomento");
		
		for(int i=0; i< argomenti.getLength(); i++){
			argomentiList.add(UtilDom.getAttributeValueAsString(argomenti.item(i), "value"));	
		}
	}
	
	private void createDisposizioniList(){
		
		disposizioniList = new Vector();
		
		NodeList disposizioni = modelloDA.getElementsByTagName("disposizione");
		
		for(int i=0; i< disposizioni.getLength(); i++){
			disposizioniList.add(UtilDom.getAttributeValueAsString(disposizioni.item(i), "value"));	
		}
	}
	
	
	public boolean isDisposizione(String label){
		if(label == null)
			return false;
		return disposizioniList.contains(label);
	}
	
	public boolean isArgomento(String label){
		if(label == null)
			return false;
		return argomentiList.contains(label);
	}
	

}
