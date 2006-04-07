package it.cnr.ittig.xmleges.editor.blocks.dom.meta.inquadramento;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.document.DocumentManager;
import it.cnr.ittig.xmleges.core.services.util.rulesmanager.UtilRulesManager;
import it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento.MetaInquadramento;
import it.cnr.ittig.xmleges.editor.services.util.dom.NirUtilDom;

public class MetaInquadramentoImpl implements MetaInquadramento, Loggable, Serviceable {

	Logger logger;

	NirUtilDom nirUtilDom;

	//DtdRulesManager dtdRulesManager;

	DocumentManager documentManager;

	//UtilMsg utilMsg;

	UtilRulesManager utilRulesManager;

	
	
	public void setInfodoc() {
		// TODO Auto-generated method stub

	}

	public void setInfomancanti() {
		// TODO Auto-generated method stub

	}

	public void setOggetto() {
		// TODO Auto-generated method stub

	}

	public void setProponenti() {
		// TODO Auto-generated method stub

	}

	public void getInfodoc() {
		// TODO Auto-generated method stub

	}

	public void getInfomancanti() {
		// TODO Auto-generated method stub

	}

	public void getOggetto() {
		// TODO Auto-generated method stub

	}

	public void getProponenti() {
		// TODO Auto-generated method stub

	}

	public void enableLogging(Logger logger) {
		this.logger = logger;

	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		//dtdRulesManager = (DtdRulesManager) serviceManager.lookup(DtdRulesManager.class);
		documentManager = (DocumentManager) serviceManager.lookup(DocumentManager.class);
		nirUtilDom = (NirUtilDom) serviceManager.lookup(NirUtilDom.class);
		//utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
		utilRulesManager = (UtilRulesManager) serviceManager.lookup(UtilRulesManager.class);
		
		
	}
}
