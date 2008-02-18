package it.cnr.ittig.xmleges.editor.services.dalos;

import it.cnr.ittig.services.manager.Service;

public interface SynsetHolder extends Service {
	
	public void setSynset(Object synObject);
	
	public Object getSynset();
}
