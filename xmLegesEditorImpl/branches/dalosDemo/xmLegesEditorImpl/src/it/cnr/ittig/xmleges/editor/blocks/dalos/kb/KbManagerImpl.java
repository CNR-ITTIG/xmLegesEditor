package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.dalos.kb.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntDocumentManager;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.dalos.kbmanager.KbManager</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
 */

public class KbManagerImpl 
implements KbManager, Loggable, Serviceable, Initializable {
	
	Logger logger;

	//Maps from language to KbContainer objects.
	private Map langToContainer;
	
	//Maps pivot language synsets to foreign languages synsets
	//TreeOntoClass.getURI() -> Collection<Synset.getURI()>
	private Map pivotToForeign;
	
	I18n i18n;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}
	
	
	
	private void copyDalosInTemp(){
		
		//   COMMON FILES  //////////////////////
		String[] commonFiles = new String[] { 		
				"common/concepts.owl", "common/consumer-law.owl","common/consumer-law-merge.owl","common/language-properties-full.owl","common/metasources.owl", 
				"common/owns.owl", "common/owns-full.owl"
	    };
		
		for (int i = 0; i < commonFiles.length; i++) {
			UtilFile.copyFileInTempDir(getClass().getResourceAsStream(commonFiles[i]),"dalos", commonFiles[i]);
		}	
		/////////////////////////////////////////
		
		
		
		//   IT   ///////////////////////////////
		String[] it = new String[]{"IT/individuals.owl", "IT/individuals-word.owl", "IT/ind-to-consumer.owl", "IT/sources.owl",
				"IT/types.owl"};
		
		for (int i = 0; i < it.length; i++) {
			UtilFile.copyFileInTempDir(getClass().getResourceAsStream(it[i]), "dalos/IT", it[i]);
		}
				
		UtilFile.copyDirectoryInTemp(getClass().getResource("IT/segment/lexical").getFile(),"dalos/IT/segment/lexical");
		
		/////////////////////////////////////////
		
				
		
		//	  EN   ///////////////////////////////
		String[] en = new String[]{"EN/individuals.owl", "EN/individuals-word.owl", "EN/ind-to-consumer.owl", "EN/sources.owl",
				"EN/types.owl"};
		
		for (int i = 0; i < en.length; i++) {
			UtilFile.copyFileInTempDir(getClass().getResourceAsStream(en[i]), "dalos/EN", en[i]);
		}	
		
		UtilFile.copyDirectoryInTemp(getClass().getResource("EN/segment/lexical").getFile(),"dalos/EN/segment/lexical");
		
		/////////////////////////////////////////		
	}
	
	public void initialize() throws Exception {	
		
	    copyDalosInTemp();
		
		langToContainer = new HashMap();
		pivotToForeign = null;
		
		if(KbConf.MERGE_DOMAIN) {
			KbConf.DOMAIN_ONTO = 
			"http://turing.ittig.cnr.it/jwn/ontologies/consumer-law-merge.owl";
		}

		File file = null;
		OntDocumentManager odm = KbModelFactory.getOdm();
		if(KbConf.MERGE_DOMAIN) {
			file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_DOMAIN_MERGE_ONTO);
		} else {
			file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_DOMAIN_ONTO);
		}
		String fileStr = "file:///";
		odm.addAltEntry(KbConf.DOMAIN_ONTO, fileStr + file.getAbsolutePath());
		System.out.println("ALTERNATIVE ENTRY: " + KbConf.DOMAIN_ONTO + " --> file://" + file.getAbsolutePath());
		file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_METALEVEL_ONTO);
		odm.addAltEntry(KbConf.METALEVEL_ONTO, fileStr + file.getAbsolutePath());
		System.out.println("ALTERNATIVE ENTRY: " + KbConf.METALEVEL_ONTO + " --> file://" + file.getAbsolutePath());
		file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_METALEVEL_PROP);
		odm.addAltEntry(KbConf.METALEVEL_PROP, fileStr + file.getAbsolutePath());
		System.out.println("ALTERNATIVE ENTRY: " + KbConf.METALEVEL_PROP + " --> file://" + file.getAbsolutePath());
		file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_METALEVEL_FULL);
		odm.addAltEntry(KbConf.METALEVEL_FULL, fileStr + file.getAbsolutePath());
		System.out.println("ALTERNATIVE ENTRY: " + KbConf.METALEVEL_FULL + " --> file://" + file.getAbsolutePath());
		file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_SOURCE_SCHEMA);
		odm.addAltEntry(KbConf.SOURCE_SCHEMA, fileStr + file.getAbsolutePath());
		System.out.println("ALTERNATIVE ENTRY: " + KbConf.SOURCE_SCHEMA + " --> file://" + file.getAbsolutePath());		
	}

	public void addLanguage(String lang) {
		
		langToContainer.put(lang, new KbContainer(lang, i18n));
	}

	public void addLexicalProperties(Synset syn) {
		
		if(syn!=null && syn.getLanguage()!=null){
			KbContainer kbc = getContainer(syn.getLanguage());
			kbc.addLexicalProperties(syn);
		}else
			logger.error("Synset not found in selected lang");
	}
	
	public void addSemanticProperties(Synset syn) {
		
		//UNDER CONSTRUCTION, DON'T GO ON!
		if(true) return;
		
		KbContainer kbc = getContainer(syn.getLanguage());
		kbc.addSemanticProperties(syn);
		//kbc.compute("dp");
	}
	
	public void addSources(Synset syn) {
		
		//UNDER CONSTRUCTION, DON'T GO ON!
		if(true) return;

		String lang = syn.getLanguage();
		KbContainer kbc = getContainer(lang);
		kbc.addSources(syn);
	}
	
	public boolean isLangSupported(String lang){
		
		KbContainer kbc = getContainer(lang);
		return kbc.isConcreteContainer();
	}
	
	public Collection getSynsets(String lang) {

		KbContainer kbc = getContainer(lang);
		return kbc.sortedSynsets;
	}

	//Ridondante con getForeignSynset() ?
	//A che serve la lingua qui?
	public Synset getSynset(String uri, String lang) {
		
		KbContainer kbc = getContainer(lang);
		return kbc.getSynset(uri);
	}
	
	public Synset getForeignSynset(String uri, String lang) {
		
		//Gets foreign KB container:
		if(getContainer(lang) == null) {
			System.out.println("## ERROR ## getForeignSynset() " +
					" - language not initialized: " + lang);
			return null;
		}

		//Gets pivot to foreign synsets mapping
		if(pivotToForeign == null) {
			initForeignLanguages();
		}
		
		Collection foreignSyns = (Collection) pivotToForeign.get(uri);
		
		if(foreignSyns == null) {
			return null;
		}
		
		for(Iterator i = foreignSyns.iterator(); i.hasNext();) {
			Synset item = (Synset) i.next();
			if(item.getLanguage().equalsIgnoreCase(lang)) {
				return item;
			}
		}
		
		return null;
	}
	
	public SynsetTree getTree(String lang) {
		
		KbContainer kbc = getContainer(lang);
		if(pivotToForeign == null) {
			initPivotMapping(kbc);
		}
		
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
	
	public boolean setTreeSelection(Synset syn) {
		
		KbContainer kbc = getContainer(syn.getLanguage());
		return kbc.setTreeSelection(syn);
	}
	
	private void initForeignLanguages() {
		
		pivotToForeign = new HashMap(2048, 0.70f);
	
		//Leggi il concepts.owl e crea le classi pivot ??!?!
		//oppure parti direttamente dai types.owl ?
	}	
	
	private void initPivotMapping(KbContainer kbc) {
		
		kbc.initPivotMapping(pivotToForeign);
	}

	private KbContainer getContainer(String lang) {
		
		KbContainer kbc = (KbContainer) langToContainer.get(lang.toUpperCase());
		if(kbc == null) {
			System.out.println(">> ADDING LANGUAGE SUPPORT FOR: " + lang);
			addLanguage(lang.toUpperCase());
			kbc = (KbContainer) langToContainer.get(lang.toUpperCase());
		}

		return kbc;
	}
		
}
