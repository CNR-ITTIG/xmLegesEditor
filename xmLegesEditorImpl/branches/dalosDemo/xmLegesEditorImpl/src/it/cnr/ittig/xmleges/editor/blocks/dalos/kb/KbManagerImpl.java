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
import it.cnr.ittig.xmleges.editor.services.dalos.objects.PivotOntoClass;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.TreeOntoClass;
import it.cnr.ittig.xmleges.editor.services.dalos.util.UtilDalos;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;

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
	
	UtilDalos utilDalos;

	//Maps from language to KbContainer objects.
	private Map langToContainer;
	
	private Set treeClasses;
	private Set pivotClasses;
	
	I18n i18n;
	
	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		i18n = (I18n) serviceManager.lookup(I18n.class);
		utilDalos = (UtilDalos) serviceManager.lookup(UtilDalos.class);
	}

	public void initialize() throws Exception {	
		
	    copyDalosInTemp();
		
		langToContainer = new HashMap();
		pivotClasses = null;
		treeClasses = null;
		
		if(KbConf.MERGE_DOMAIN) {
			KbConf.DOMAIN_ONTO = 
			"http://turing.ittig.cnr.it/jwn/ontologies/consumer-law-merge.owl";
		}
		
		loadCommonDocuments();
		
		loadLanguages();

		initPivotMapping();
	}
	
	private void loadLanguages() {		
		
		String[] languages = utilDalos.getDalosLang();
		for(int i = 0; i < languages.length; i++) {
			String lang = (String) languages[i];
			addLanguage(lang);
		}
	}
	
	private void loadCommonDocuments() {
		
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
		
		//Concepts Model
		String conceptsFile = KbConf.dalosRepository + KbConf.CONCEPTS;		
		if(!UtilFile.fileExistInTemp(conceptsFile)) {
			System.err.println("KbManager - concepts file does not exist!!");
		} else {
			KbModelFactory.addDocument(KbConf.CONCEPTS, "", conceptsFile);
		}
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
	
	public Collection getSynsetsList(String lang) {

		KbContainer kbc = getContainer(lang);
		return kbc.getSynsets(true);
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
//		if(pivotToForeign == null) {
//			initForeignLanguages();
//		}
//		
//		Collection foreignSyns = (Collection) pivotToForeign.get(uri);
//		
//		if(foreignSyns == null) {
//			return null;
//		}
//		
//		for(Iterator i = foreignSyns.iterator(); i.hasNext();) {
//			Synset item = (Synset) i.next();
//			if(item.getLanguage().equalsIgnoreCase(lang)) {
//				return item;
//			}
//		}
		
		return null;
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
	
	public boolean setTreeSelection(Synset syn) {
		
		KbContainer kbc = getContainer(syn.getLanguage());
		return kbc.setTreeSelection(syn);
	}
	
	private void initPivotMapping() {
		
		System.out.println("Initializing Pivot Classes...");
		OntModel mod = KbModelFactory.getModel("concepts");
		
		OntClass conceptClass = mod.getOntClass(KbConf.conceptClassName);
		if(conceptClass == null) {
			System.err.println(
				"ERRROR! initPivotMapping() - conceptClass is null");
			return;
		}

		for(Iterator i = conceptClass.listInstances(); i.hasNext(); ) {
			OntResource ores = (OntResource) i.next();
			PivotOntoClass poc = new PivotOntoClass();
			poc.setURI(ores.getNameSpace() + ores.getLocalName());
			pivotClasses.add(poc);
			for(Iterator k = mod.listStatements(
					(Resource) ores, RDFS.subClassOf, (RDFNode) null);
					k.hasNext();) {
				Statement stm = (Statement) i.next();
				OntResource obj = (OntResource) stm.getObject();
				String objNS = obj.getNameSpace();
				String objName = obj.getLocalName();

				if(!objNS.equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
					System.err.println("initPivotMapping() - objNS: " + objNS);
					continue;
				}
				
				TreeOntoClass toc = new TreeOntoClass(objName);
				toc.setURI(objNS + objName);
				poc.addClass(toc);
				treeClasses.add(toc);
			}
		}
	}
		
	private KbContainer getContainer(String lang) {
		
		KbContainer kbc = (KbContainer) langToContainer.get(lang);
		if(kbc == null) {
			System.out.println(">> ADDING LANGUAGE SUPPORT FOR: " + lang);
			addLanguage(lang);
			kbc = (KbContainer) langToContainer.get(lang);
		}

		return kbc;
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

}
