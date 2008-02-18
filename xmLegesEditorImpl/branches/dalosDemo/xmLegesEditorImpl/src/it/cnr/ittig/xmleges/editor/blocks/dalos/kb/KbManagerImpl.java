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
		
		if(KbConf.MERGE_DOMAIN) {
			KbConf.DOMAIN_ONTO = 
			"http://turing.ittig.cnr.it/jwn/ontologies/consumer-law-merge.owl";
		}
		
		loadCommonDocuments();
		
		initPivotMapping();

		loadLanguages();
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

	public Collection getSynset(TreeOntoClass toc) {
		
		return null;
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
		System.out.println("getSynset - poc: " + poc + " - fsyn: " + fsyn +
				" fsUri: " + fsyn.getURI());
		if(fsyn == null) {
			//No aligment!
			return null;
		}
		
		if(!fsyn.isConcreteSynset()) {
			//Make it concrete!
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
		return search(search, "contains", "IT");
	}
	
	public Collection search(String search, String type, String lang) {
		
		KbContainer kbc = getContainer(lang);
		return kbc.search(search, type);
	}
	
	public void setTreeSelection(Synset syn) {
		
//		KbContainer kbc = getContainer(syn.getLanguage());
//		return kbc.setTreeSelection(syn);
		kbt.setSelection(syn);
	}
	
	public PivotOntoClass getPivotClass(String uri) {

		return (PivotOntoClass) uriToPivotClass.get(uri);
	}
	
	public TreeOntoClass getTreeClass(String uri) {

		return (TreeOntoClass) uriToTreeClass.get(uri);
	}

	/**
	 * Find which concepts are classified under 'toc' class
	 * 
	 * @param toc
	 * @return a Collection of PivotOntoClass
	 */
	public Collection getPivotClasses(TreeOntoClass toc) {
		
		Collection pocs = new HashSet();
		
		Collection pivots = uriToPivotClass.values();
		for(Iterator i = pivots.iterator(); i.hasNext(); ) {
			PivotOntoClass pitem = (PivotOntoClass) i.next();
			Collection links = pitem.getLinks();
			for(Iterator k = links.iterator(); k.hasNext(); ) {
				TreeOntoClass titem = (TreeOntoClass) k.next();
				if(titem.equals(toc)) {
					pocs.add(pitem);
				}
			}
		}
		
		return pocs;
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
				"ERRROR! initPivotMapping() - conceptClass is null");
			return;
		}

		for(Iterator i = conceptClass.listSubClasses(); i.hasNext(); ) {
			OntResource ores = (OntResource) i.next();
			PivotOntoClass poc = new PivotOntoClass();
			String puri = ores.getNameSpace() + ores.getLocalName();
			poc.setURI(puri);
			uriToPivotClass.put(puri, poc);
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
				
				TreeOntoClass toc = new TreeOntoClass(objName);
				String turi = objNS + objName;
				toc.setURI(turi);
				poc.addLink(toc);
				
				uriToTreeClass.put(turi, toc);
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
		
		// common
		UtilFile.copyDirectoryInTemp(getClass().getResource("common").getFile(),"dalos");
		
		// IT
		if(!UtilFile.copyDirectoryInTemp(getClass().getResource("IT").getFile(),"dalos/IT"))
			logger.error("FAILED TO COPY IT");
		
		// EN
		if(!UtilFile.copyDirectoryInTemp(getClass().getResource("EN").getFile(),"dalos/EN"))
			logger.error("FAILED TO COPY EN");
		
	}	

}
