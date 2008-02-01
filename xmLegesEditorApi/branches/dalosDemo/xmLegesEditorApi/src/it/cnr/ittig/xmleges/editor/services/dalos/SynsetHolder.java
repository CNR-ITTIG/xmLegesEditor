package it.cnr.ittig.xmleges.editor.services.dalos;

import it.cnr.ittig.services.manager.Service;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

public interface SynsetHolder extends Service {
	
	public void setSynset(Synset syn);
	
	public Synset getSynset();
}
