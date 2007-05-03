package it.cnr.ittig.xmleges.core.blocks.panes.xsltutil;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentClosedEvent;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.panes.xsltutil.XsltUtil;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;


import java.util.EventObject;
import java.util.Hashtable;
import javax.swing.JEditorPane;


import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XsltUtilImpl implements XsltUtil, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	
	//NON TUTTO è UTILIZZATO
	
	/** Event Manager per gli eventi. */
	EventManager eventManager = null;

	/** Document manager per creare nodi appartenenti al documento aperto. */
	DocumentManager documentManager = null;

	/** URN verificate */
	Hashtable urnTest = new Hashtable(100);
	
	/** Istanza della classe per l'utilizzo dall'xsl */
	static XsltUtilImpl instance = null;

	Hashtable i18nNodeName = new Hashtable(100);

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, DocumentClosedEvent.class);
		String[] tpls = new String[] { "xsltutil-1.0.xsl" };
		for (int i = 0; i < tpls.length; i++)
			// FIXME if (!UtilFile.fileExistInTemp(tpls[i]))
			UtilFile.copyFileInTemp(getClass().getResourceAsStream(tpls[i]), tpls[i]);

		instance = this;
	}

	// ////////////////////////////////////////// EventManagerListener Interface
	public synchronized void manageEvent(EventObject event) {
		if (event instanceof DocumentClosedEvent) {
			clear();
		}
	}

	// //////////////////////////////////////////////////// XsltMapper Interface
	public void clear() {
		urnTest.clear();
	}


	// /////////////////////////////////////////////// Xalan extension functions
	public static synchronized String getTestUrn(NodeList nodeList) {

		if (instance == null)
			return "black";
		if (nodeList == null || nodeList.getLength() == 0)
			return "black";
		Node node = nodeList.item(0);
		if (node!=null) {
			String test = UtilDom.getAttributeValueAsString(node, "xlink:href");
			if (test!=null)
				if (instance.urnTest.containsKey(test)) 
					return (String) instance.urnTest.get(test);  //return "Red" o "Green"
		}
		return "black"; //URN mai verificata
	}
	
	public void setUrn(String urn, String colore) {
		urnTest.put(urn, colore);
	}
	
	public String getUrn(String urn) {
		if (instance.urnTest.containsKey(urn)) 
			return (String) urnTest.get(urn);
		else
			return null;
	}
	
	public void clearUrn() {
		urnTest.clear();
	}

}
