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
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
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
	
	private Map uriToPivotClass;
	private Map uriToTreeClass;
	
	private KbTree kbt;
	
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
		uriToPivotClass = new HashMap(256, 0.70f);
		uriToTreeClass = new HashMap(128, 0.70f);
		
		kbt = null;
		
		loadCommonDocuments();
		
		initPivotMapping();

		loadLanguages();
		
		initSemPaths();
	}
	
	private void loadLanguages() {		
		
		String[] languages = utilDalos.getDalosLang();
		for(int i = 0; i < languages.length; i++) {
			String lang = (String) languages[i];
			addLanguage(lang);
		}
	}
	
	private void loadCommonDocuments() {
		
		OntDocumentManager odm = KbModelFactory.getOdm();
		File file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_DOMAIN_ONTO);
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
		
		langToContainer.put(lang, new KbContainer(lang, this, i18n));
		
		//Add new language alignements
		KbContainer kbc = getContainer(lang);
		kbc.addAlignments();
	}

	public void addLexicalProperties(Synset syn) {
		
		if(syn == null) { return; }

		KbContainer kbc = getContainer(syn.getLanguage());
		kbc.addLexicalProperties(syn);
	}
	
	public void addSemanticProperties(Synset syn) {
		
		if(syn == null) { return; }
		
		//UNDER CONSTRUCTION, DON'T GO ON!
		if(true) return;
		
		KbContainer kbc = getContainer(syn.getLanguage());
		kbc.addSemanticProperties(syn);
		//kbc.compute("dp");
	}
	
	public void addSources(Synset syn) {
		
		if(syn == null) { return; }
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

	public Collection getSynsets(TreeOntoClass toc, String lang) {
		
		Collection terms = new TreeSet();
		Collection concepts = toc.getConcepts();
		
		for(Iterator i = concepts.iterator(); i.hasNext();) {
			PivotOntoClass poc = (PivotOntoClass) i.next();
			Synset syn = poc.getTerm(lang);
			if(syn != null) {
				terms.add(syn);				
			}
		}
		
		return terms;
	}
	
	public Synset getSynset(String uri) {
		
		System.out.println("%%% getSynset() uri:" + uri);
		String lang = extractLangFromUri(uri);
		KbContainer kbc = getContainer(lang);
		return kbc.getSynset(uri);
	}
	
	public Synset getSynset(Synset syn, String lang) {
		
		System.out.println("%%% getSynset() syn:" + syn + " - lang:" + lang);
		
		KbContainer kbc = getContainer(lang);

		PivotOntoClass poc = syn.getPivotClass();
		if(poc == null) {
			System.err.println("ERROR getSynset() - poc is null for " + syn);
			return null;
		}
		
		Synset fsyn = poc.getTerm(lang);
		//System.out.println("getSynset - poc: " + poc + " - fsyn: " + fsyn);
		if(fsyn == null) {
			//No alignment!
			return null;
		}
		
		if(!fsyn.isConcreteSynset()) {
			//Make it concrete!
			//System.out.println("Not concrete! Initializing...");
			kbc.initSynset(fsyn);
		}

		return fsyn;		
	}
	
	void setKbTree(KbTree kbt) {
		
		this.kbt = kbt;
	}
	
	public SynsetTree getTree(String lang) {
		
		KbContainer kbc = getContainer(lang);
		return kbc.getTree();
	}
		
	public Collection search(String search) {
		//Dovrebbe prendere la lingua dalle Preference?	
		//Dovrebbe prendere il tipo di ricerca dalle Preference? (ultima usata)		
		return search(search, KbManager.CONTAINS, utilDalos.getGlobalLang());
	}
	
	
	public Collection search(String search, String type, String lang) {
		KbContainer kbc = getContainer(lang);
		return kbc.search(search, type);
	}
	
	
	public void setTreeSelection(Synset syn) {
		
		//Disabled!
		//kbt.setSelection(syn);
	}
	
	public PivotOntoClass getPivotClass(String uri) {

		PivotOntoClass poc = (PivotOntoClass) uriToPivotClass.get(uri);
		if(poc == null) {
			System.err.println("Error! Unable to get pivot for " + uri);
		}
		return poc;
	}
	
	public TreeOntoClass getTreeClass(String uri) {

		return (TreeOntoClass) uriToTreeClass.get(uri);
	}
	
	TreeOntoClass addTreeClass(String uri, String name) {
		
		TreeOntoClass toc = (TreeOntoClass) uriToTreeClass.get(uri);
		if(toc != null) {
			System.err.println("addTreeClass() - already here: " + uri);
		} else {
			toc = new TreeOntoClass(name);
			toc.setURI(uri);
			uriToTreeClass.put(uri, toc);
		}
		
		return toc;		
	}
	
	private void initPivotMapping() {
		
		System.out.println("Initializing Pivot Classes...");
		OntModel mod = KbModelFactory.getModel("concepts");
		
		OntClass conceptClass = mod.getOntClass(KbConf.conceptClassName);
		if(conceptClass == null) {
			System.err.println(
				"ERROR! initPivotMapping() - conceptClass is null");
			return;
		}

		for(Iterator i = conceptClass.listSubClasses(); i.hasNext(); ) {
			OntResource ores = (OntResource) i.next();
			PivotOntoClass poc = new PivotOntoClass();
			String puri = ores.getNameSpace() + ores.getLocalName();
			poc.setURI(puri);
			uriToPivotClass.put(puri, poc);
//			if(puri.indexOf("artconc-0000000611") > 0) {
//				System.out.println("[][][] POC: " + puri + " poc:" + poc);
//			}
			for(StmtIterator k = mod.listStatements(
					(Resource) ores, RDFS.subClassOf, (RDFNode) null);
					k.hasNext();) {
				Statement stm = k.nextStatement();
				Resource obj = (Resource) stm.getObject();
				String objNS = obj.getNameSpace();
				String objName = obj.getLocalName();

				if(!objNS.equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
					continue;
				}
				
				String turi = objNS + objName;
				TreeOntoClass toc = (TreeOntoClass) 
										uriToTreeClass.get(turi); 
				if(toc == null) {
					toc = new TreeOntoClass(objName);					
				}
				toc.setURI(turi);
				poc.addLink(toc);
				toc.addConcept(poc);
//				System.out.println("Adding pivot class " + poc + 
//						" to tree class " + toc + " turi:" + turi);
				
				uriToTreeClass.put(turi, toc);
			}
		}
	}
	
	private void initSemPaths() {
		
		
		
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
	
	private String extractLangFromUri(String uri) {
		//Esempio:
		//http://localhost/dalos/IT/individuals.owl#synset-acqua-noun-1
		
		if(uri.indexOf(KbConf.DALOS_NS) != 0) {
			System.err.println("extractLangFromUri() - invalid uri: " + uri);
			return "";
		}
		
		int start = KbConf.DALOS_NS.length();
		return uri.substring(start, start + 2);		
	}

	
	
	private void copyDalosInTemp(){
		String commonDirName = "common";
		
		
		
		// COMMONS
		
		String[] commonFiles = new String[] {
				KbConf.CONCEPTS, 
				KbConf.LOCAL_DOMAIN_ONTO,
				KbConf.LOCAL_METALEVEL_FULL,
				KbConf.LOCAL_SOURCE_SCHEMA, 
				KbConf.LOCAL_METALEVEL_ONTO, 
				KbConf.LOCAL_METALEVEL_PROP
	    };
		
		for (int i = 0; i < commonFiles.length; i++) {
			if(!UtilFile.fileExistInTemp(KbConf.dalosRepository+File.separator+commonFiles[i])){
				UtilFile.copyFileInTempDir(getClass().getResourceAsStream(commonDirName+"/"+commonFiles[i]),KbConf.dalosRepository, commonFiles[i]);
			}else{
				System.err.println("common: already in temp");
			}
			
		}	
		
		
		
		// LANG DEPENDENT
		String[] langFiles = new String[] {
				KbConf.IND, 
				KbConf.INDW,
				KbConf.TYPES,
				KbConf.SOURCES, 
				KbConf.segmentDirName+".zip"
	    };
		
		
		
		String[] dalosLang = utilDalos.getDalosLang();
		
		for(int i=0; i<dalosLang.length; i++){
			for(int j=0; j < langFiles.length; j++){
				if(!UtilFile.fileExistInTemp(KbConf.dalosRepository+File.separator+dalosLang[i]+File.separator+langFiles[j])){
					UtilFile.copyFileInTempDir(getClass().getResourceAsStream(dalosLang[i]+"/"+langFiles[j]),KbConf.dalosRepository+"/"+dalosLang[i],langFiles[j]);
					if(langFiles[j].endsWith("zip")){
						UtilFile.unZip(UtilFile.getFileFromTemp(KbConf.dalosRepository+"/"+dalosLang[i]+"/"+langFiles[j]).getAbsolutePath(),UtilFile.getTempDirName()+"/"+KbConf.dalosRepository+"/"+dalosLang[i]);
					}
				}else{
					System.err.println("lang: already in temp");
				}
			}
		}
		
	}
	

}
