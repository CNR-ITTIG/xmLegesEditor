package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.editor.services.dalos.kb.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dalos.kbmanager.KbManager</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 */

public class KbManagerImpl implements KbManager, Loggable, Serviceable, Initializable {
	
	Logger logger;

	//Map from language string to KbContainer objects.
	private Map langToContainer;
	
	I18n i18n;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}
	
	
	public void initialize() throws Exception {	
		
		langToContainer = new HashMap();
		//La lingua italiana dovrebbe essere inizializzata da qualche altra parte...
		addLanguage("IT");
	}

	public void addLanguage(String lang) {
		
		langToContainer.put(lang, new KbContainer(lang, i18n));
	}

	public void addSemanticProperties(Synset syn) {
		
		String lang = syn.getLanguage();
		KbContainer kbc = getContainer(lang);
		kbc.addSemanticProperties(syn);
	}
	
	public void addLexicalProperties(Synset syn) {
		
		String lang = syn.getLanguage();
		KbContainer kbc = getContainer(lang);
		kbc.addLexicalProperties(syn);
	}
	
	public void addSources(Synset syn) {
		
		String lang = syn.getLanguage();
		KbContainer kbc = getContainer(lang);
		kbc.addSources(syn);
	}
	
	public Collection getSynsets(String lang) {

		KbContainer kbc = getContainer(lang);
		return kbc.sortedSynsets;
	}
	
	public Synset getSynset(String uri, String lang) {
		
		KbContainer kbc = getContainer(lang);
		return kbc.getSynset(uri);
	}
	
	public SynsetTree getTree(String lang) {

		KbContainer kbc = getContainer(lang);
		return kbc.getTree();
	}
		
	public Collection search(String search) {
		//Dovrebbe prendere la lingua dalle Preference?	
		//Dovrebbe prendere il tipo di ricerca dalle Preference? (ultima usata)		
		return search(search, "contains", "IT");
	}
	
	public Collection search(String search, String type, String lang) {
		
		KbContainer kbc = getContainer(lang);
		return kbc.search(search, type);
	}
	
	private KbContainer getContainer(String lang) {
		
		KbContainer kbc = (KbContainer) langToContainer.get(lang);
		if(kbc == null) {
			System.err.println("## ERROR ## kbContainer not found for language: " + lang);
		}

		return kbc;
	}	
}
