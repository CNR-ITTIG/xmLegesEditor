package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.dalos.kb.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.PivotOntoClass;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Source;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.TreeOntoClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class KbContainer {

	private String LANGUAGE;
	
	public String localRepository;
	public String infRepository;
	public String segRepository;

	private I18n i18n;
	
	private boolean concreteContainer = false;

	//Dati
	private Map synsets;
	private Set sortedSynsets;

	private Set langProperties = null;
	
	private KbManagerImpl kbm;
	private KbTree kbt;
	
	OntProperty contains = null;
	OntProperty word = null;
	OntProperty lexical = null;
	OntProperty proto = null;
	OntProperty glossProperty = null;
	OntClass nSynClass = null;
	OntClass vSynClass = null;
	OntClass ajSynClass = null;
	OntClass avSynClass = null;


	public KbContainer(String lang, KbManagerImpl kbm, I18n i) {
		
		this.kbm = kbm;
		
		i18n = i;

		LANGUAGE = lang;
		
		synsets = new HashMap(2048, 0.70f);
		sortedSynsets = new TreeSet();

		if(!checkFiles()) {
			System.err.println(">> WARNING - KbContainer: Data files not found! Repo: " +
					localRepository);
		} else {			
			concreteContainer = true;
			
			//init segment map
			try {
				initMaps();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getLanguage() {
		
		return LANGUAGE;
	}
	
	public Collection getSynsets() {
		
		return getSynsets(false);
	}
	
	public Collection getSynsets(boolean sorted) {
	
		if(!concreteContainer) {
			System.err.println(
					"KbContainer Error - This is an empty container!");
			return null;
		}
		
		init();
		
		if(sorted) {
			return sortedSynsets;
		} else {
			return synsets.values();
		}		
	}
	
	//Using this method means that this container
	//now holds the global language. <--- ??
	boolean init() {		
		
		if(!concreteContainer) {
			return false;
		}
		
		System.out.println("KbContainer - Initializing data...");
		
		kbt = new KbTree(this, kbm, i18n);
		kbm.setKbTree(kbt);

		long t1 = System.currentTimeMillis();		
		initSynsets();
		
		//prepare sorted synsets
		for(Iterator i = synsets.values().iterator(); i.hasNext(); ) {
			sortedSynsets.add((Synset) i.next());
		}
		long t2 = System.currentTimeMillis();
		System.out.println("..." + synsets.size() + " synsets loaded! (" +
				Long.toString(t2 - t1) + " ms)\n");

		return true;
	}
	
	boolean isConcreteContainer() {
		
		return concreteContainer;
	}
	
	private void initMap(String segmentName) throws 
		FileNotFoundException, IOException, ClassNotFoundException {
		
		String mapFileName = segRepository + segmentName +
					File.separatorChar + KbConf.mapSegmentFileName;

		File mapFile = UtilFile.getFileFromTemp(mapFileName);
		if(mapFile != null && mapFile.exists()) {
			FileInputStream fis = new FileInputStream(mapFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Map segMap = new HashMap();
			segMap = (Map) ois.readObject();
			ois.close();
			fis.close();
			
			Collection keys = segMap.keySet();
			for(Iterator k = keys.iterator(); k.hasNext();) {
				String key = (String) k.next();
				String value = (String) segMap.get(key);
				String segFileName = segRepository + segmentName 
						+ File.separatorChar + value;
				KbModelFactory.addSegment(key, segFileName, segmentName);
			}
		} else {
			System.err.println("Segmentation type: \"" + segmentName +
					"\" for language \"" + this.LANGUAGE + 
					"\" ...SEGMAP file not found!");
		}
	}
	
	private void initMaps() 
		throws FileNotFoundException, IOException, ClassNotFoundException {
		
		System.out.println("KbContainer: checking KB segmentation support...");
		
		initMap(KbConf.lexicalSegmentName);
		initMap(KbConf.sourceSegmentName);
		initMap(KbConf.semanticSegmentName);
	}

	Collection getTopClasses() {
		/*
		 * Return a set of String that represent the URI
		 * of direct sub classes of Thing in DOMAIN ontology.
		 */
		
		Set classes = new HashSet();
		OntModel om = KbModelFactory.getModel("domain");

		for(Iterator i = om.listClasses(); i.hasNext();) {
			OntClass oc = (OntClass) i.next();
			if(oc.isAnon() ||
			!oc.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
				continue;
			}
			boolean top = true;
			for(Iterator k = oc.listSuperClasses(true); k.hasNext();) {
				OntClass sup = (OntClass) k.next();
				if(sup.isAnon()) {
					//che si fa se � anonima?? significa che non � top?
				} else {
					if(sup.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
						top = false;
						break;
					}
				}
			}
			if(top) {
				String ocUri = oc.getNameSpace() + oc.getLocalName();
				classes.add(ocUri);
				System.out.println("++ TOP CLASS: " + ocUri);
			}
		}		
		
		return classes;
	}
		
	private boolean checkFiles() {
		
		localRepository = KbConf.dalosRepository + LANGUAGE + File.separatorChar;
		segRepository = KbConf.dalosRepository + LANGUAGE + File.separatorChar +
			KbConf.segmentDirName + File.separatorChar;
		
		String indFile = localRepository + KbConf.IND;
		String indwFile = localRepository + KbConf.INDW;
		String typesFile = localRepository + KbConf.LEXICALIZATIONS;
		String sourcesFile = localRepository + KbConf.SOURCES;
		
		if(!UtilFile.fileExistInTemp(indFile)) {			
			return false;
		} else {
			KbModelFactory.addDocument(KbConf.IND, LANGUAGE, indFile);
		}
		
		if(!UtilFile.fileExistInTemp(indwFile)) {
			return false;
		} else {
			KbModelFactory.addDocument(KbConf.INDW, LANGUAGE, indwFile);
		}
		
		if(!UtilFile.fileExistInTemp(typesFile)) {
			return false;
		} else {
			KbModelFactory.addDocument(KbConf.LEXICALIZATIONS, LANGUAGE, typesFile);
		}
		
		return true;
	}
	
	/*
	 * Adds empty synsets to KbManager pivot classes.
	 */
	void addAlignments() {
		
		if(!concreteContainer) {
			return;
		}
		
		System.out.println(">> Adding language alignments for: "
				+ LANGUAGE + "...");
		OntModel mod = KbModelFactory.getModel("types", "", LANGUAGE);
		System.out.println("mod size: " + mod.size());
		
		OntProperty lexProp = mod.getOntProperty(KbConf.LEXICALIZATION_PROP);
		if(lexProp == null) {
			System.err.println("addAlignments() error - lexProp is null!");
			return;
		}
		
		for(StmtIterator i = mod.listStatements(
				null, lexProp, (RDFNode) null); i.hasNext(); ) {
			Statement stm = i.nextStatement();
			Resource subj = stm.getSubject();
			Resource obj = (Resource) stm.getObject();
			String subjNs = subj.getNameSpace();			
			String subjName = subj.getLocalName();
			String objNs = obj.getNameSpace();			
			String objName = obj.getLocalName();
			
			String suri = objNs + objName;
			Synset syn = (Synset) synsets.get(suri);
			if(syn == null) {
				//Create an empty synset
				syn = new Synset(LANGUAGE);
				syn.setURI(suri);
				synsets.put(suri, syn);
				//sortedSynsets.add(syn);
			} else {
				System.err.println("Synset syn already here!? uri: " + suri);
			}			
			
			//Retrieve matching concept
			PivotOntoClass poc = kbm.getPivotClass(subjNs + subjName);
			
//			if(objName.indexOf("price") > 0) {
//				System.out.println("_-^-_ addAlignment() poc:" + poc +
//						" sUri:" + syn.getURI() + " cUri:" + poc.getURI());
//			}
			
			poc.addTerm(syn);
			syn.setPivotClass(poc);
		}
		
		System.out.println("ENDOF addAlignment() - synsets size: " + synsets.size());
	}

	SynsetTree getTree() {
		
		return kbt.getTree();
	}

	private void initOntResources(OntModel om) {
		
		contains = om.getOntProperty(KbConf.METALEVEL_ONTO_NS + "containsWordSense");
		word = om.getOntProperty(KbConf.METALEVEL_ONTO_NS + "word");
		lexical = om.getOntProperty(KbConf.METALEVEL_ONTO_NS + "lexicalForm");
		proto = om.getOntProperty(KbConf.METALEVEL_ONTO_NS + "protoForm");
		glossProperty = om.getOntProperty(KbConf.METALEVEL_ONTO_NS + "gloss");
		nSynClass = om.getOntClass(KbConf.METALEVEL_ONTO_NS + "NounSynset");
		vSynClass = om.getOntClass(KbConf.METALEVEL_ONTO_NS + "VerbSynset");
		ajSynClass = om.getOntClass(KbConf.METALEVEL_ONTO_NS + "AdjectiveSynset");
		avSynClass = om.getOntClass(KbConf.METALEVEL_ONTO_NS + "AdverbSynset");
	}
	
	private void initSynsets() {
		
		OntModel ind_m = KbModelFactory.getModel("individual", "", LANGUAGE);
		initOntResources(ind_m);
		
		if(contains == null || word == null || lexical == null) {
			System.out.println("ERROR: null properties.");
			return;
		}
		if(nSynClass == null || vSynClass == null || ajSynClass == null || avSynClass == null ) {
			System.out.println("ERROR: null synset class!");
			return;
		}
		
		for(Iterator i = nSynClass.listInstances(false); i.hasNext();) {
			analyzeSynsetResource((OntResource) i.next());
		}

		for(Iterator i = vSynClass.listInstances(false); i.hasNext();) {
			analyzeSynsetResource((OntResource) i.next());
		}

		for(Iterator i = ajSynClass.listInstances(false); i.hasNext();) {
			analyzeSynsetResource((OntResource) i.next());
		}

		for(Iterator i = avSynClass.listInstances(false); i.hasNext();) {
			analyzeSynsetResource((OntResource) i.next());
		}		
	}
	
	/**
	 * Makes syn a concrete synset
	 * 
	 * @param syn
	 */
	void initSynset(Synset syn) {
		
		OntModel om = KbModelFactory.getModel(
				"seg.lex", "micro", LANGUAGE, syn.getURI());
		
		initOntResources(om);
		OntResource ores = (OntResource) om.getOntResource(syn.getURI());
		analyzeSynsetResource(ores, syn);
	}
	
	void initSynset(String uri) {
		
		OntModel om = KbModelFactory.getModel(
				"seg.lex", "", LANGUAGE, uri);
		
		initOntResources(om);
		Resource res = om.getResource(uri);
		if(res == null) {
			return;
		}
		OntResource ores = om.getOntResource(res);
		analyzeSynsetResource(ores);
	}
	
	private void analyzeSynsetResource(OntResource ores) {
		
		String suri = ores.getNameSpace() + ores.getLocalName();
		analyzeSynsetResource(ores, (Synset) synsets.get(suri));
	}
	
	private void analyzeSynsetResource(OntResource ores, Synset syn) {
		/*
		 * Add gloss and lexical properties.
		 */
		
		//If syn != null, make it a concrete synset
		
		boolean newSynset = false;
		
		if(syn == null) {
			newSynset = true;
			syn = new Synset(LANGUAGE);
			String suri = ores.getNameSpace() + ores.getLocalName();
			if(synsets.get(suri) != null) {
				System.err.println("analyzeSynsetResource() - not null value for: " + suri);
			}
			syn.setURI(suri);
			synsets.put(suri, syn);
			//sortedSynsets.add(syn);
		}
				
		RDFNode glossNode = ores.getPropertyValue(glossProperty);
		if(glossNode != null) {			
			syn.setDef( ((Literal) glossNode).getString());
		}

		for(Iterator k = ores.listPropertyValues(contains); k.hasNext();) {
			OntResource ws = (OntResource) k.next();
			OntResource w = (OntResource) ws.getPropertyValue(word);
			RDFNode protoNode = (RDFNode) w.getPropertyValue(proto);
			if(protoNode != null) {
				String protoForm = ((Literal) protoNode).getString();
				syn.setLexicalForm(protoForm);					
			} else {
				System.err.println("protoNode is null for " + w.getLocalName());
			}
			for(Iterator l = w.listPropertyValues(lexical); l.hasNext(); ) {
				RDFNode lexNode = (RDFNode) l.next();
				String lexForm = ((Literal) lexNode).getString();
				syn.addVariant(lexForm);
			}
		}
		
//		String key = ores.getNameSpace() + ores.getLocalName();
//		synsets.put(key, syn);
		//sortedSynsets.add(syn);

		//Fill pivot classes (only if syn is a new synset)
		//XXX
		if(newSynset) {
			OntModel om = ores.getOntModel();
			OntProperty hasLexProp = om.getOntProperty(KbConf.LEXICALIZATION_PROP);
			if(hasLexProp == null) {
				return;
			}

			System.out.println("analyzeSynsetResource() - new synset: " + ores.getLocalName());
			
			for(StmtIterator i = om.listStatements(
					null, hasLexProp, ores); i.hasNext(); ) {
				Statement stm = i.nextStatement();
				Resource subj = stm.getSubject();
				String puri = subj.getNameSpace() + subj.getLocalName();
				PivotOntoClass poc = kbm.getPivotClass(puri);
				if(poc == null) {
					System.err.println("ERROR! Pivot Class not found: " + puri);
					continue;
				} else {
					System.err.println(" ---------- \"UNREACHABLE\" ZONE REACHED !!?!");
					poc.addTerm(syn);
					syn.setPivotClass(poc);
				}				
			}
		} else {
			syn.setConcreteSynset(true);
		}
	}
	
	Synset getSynset(String uri) {
		
		Synset syn = (Synset) synsets.get(uri);
		if(syn == null) {			
			initSynset(uri);
		}
		
		return syn; 
	}
	
	void addSources(Synset syn) {
		
		if(syn.isSourceCached()) {
			return;
		}
		
		//System.out.println(">> adding sources to " + syn + "...");
		
		//OntModel om = KbModelFactory.getModel("seg.source", "micro", LANGUAGE, syn.getURI());
		OntModel om = KbModelFactory.getModel("seg.source", "", LANGUAGE, syn.getURI());
		
		Individual ind = om.getIndividual(syn.getURI());
		if(ind == null) {
			System.err.println("addSources() - ind is null!");
			return;
		}

		OntProperty sourceProp = om.getOntProperty(KbConf.SOURCESCHEMA_NS + "source");
		OntProperty involvesProp = om.getOntProperty(KbConf.SOURCESCHEMA_NS + "involvesPartition");
		OntProperty belongsProp = om.getOntProperty(KbConf.SOURCESCHEMA_NS + "belongsTo");
		OntProperty linkProp = om.getOntProperty(KbConf.SOURCESCHEMA_NS + "link");
		OntProperty contentProp = om.getOntProperty(KbConf.SOURCESCHEMA_NS + "content");
		OntProperty idProp = om.getOntProperty(KbConf.SOURCESCHEMA_NS + "partitionCode");
		
		for(Iterator i = ind.listPropertyValues(sourceProp); i.hasNext();) {
			OntResource ores = (OntResource) i.next();
			OntResource part = (OntResource) ores.getPropertyValue(involvesProp);
			OntResource doc = (OntResource) part.getPropertyValue(belongsProp);
			
			RDFNode idNode = part.getPropertyValue(idProp);			
			if(idNode == null) {
				System.out.println("## ERROR ## partitionCode is null!! ind: " + ind);
				return;
			}
			
			Source source = new Source(((Literal) idNode).getString());
			
			RDFNode linkNode = doc.getPropertyValue(linkProp);			
			if(linkNode != null) {
				source.setLink(((Literal) linkNode).getString());
			}
			
			RDFNode contentNode = ores.getPropertyValue(contentProp);
			if(contentNode != null) {
				if(!contentNode.isLiteral()) {
					System.out.println("## ERROR ## CONTENT IS " 
							+ contentNode.getClass() + " : " + contentNode );					
				} else {
					String content = ((Literal) contentNode).getString();
					source.setContent(content);
				}
			} else {
				System.err.println("Null content for source " + source);
			}

			syn.addSource(source);
		}
		
		syn.setSourceCached(true);
	}

	void addLexicalProperties(Synset syn) {		
		//Aggiunge le propriet� generiche e quelle lessicali

//		System.out.println("Adding lexical properties to " 
//				+ syn.getLexicalForm() + "...");
		if(syn.isLexicalPropCached()) {
//			System.out.println("..already cached!");
			return;
		}

		//OntModel om = KbModelFactory.getModel("seg.lex", "micro", LANGUAGE, syn.getURI());
		OntModel om = KbModelFactory.getModel("seg.lex", "", LANGUAGE, syn.getURI());
		
		Individual ind = om.getIndividual(syn.getURI());
		OntProperty glossProp = om.getOntProperty(KbConf.METALEVEL_ONTO_NS + "gloss");
		//OntProperty sourceProp = om.getOntProperty(METALEVEL_ONTO_NS + "source");  //fonte		
		OntProperty langProp = om.getOntProperty(KbConf.METALEVEL_PROP_NS + "language-property");
		if(glossProp == null) {
			System.err.println("gloss property is null!");
			return;
		}
		if(langProp == null) {
			System.err.println("lang property is null!");
			return;
		}
		
		//Prendi in considerazione solo le propriet� che sono subProperty di language-property
		//(cos� aggiunge anche language-property come relazione, che mappa verso l'unione
		//dei synset mappati da tutte le propriet� lessicali... pu� essere utile?)
		if(langProperties == null) {
			langProperties = new HashSet();			
			for(Iterator i = langProp.listSubProperties(false); i.hasNext(); ) {
				OntProperty prop = (OntProperty) i.next();
				langProperties.add(prop.getNameSpace() + prop.getLocalName());
			}
//			System.out.println("Lex properties:");
//			for(Iterator r = langProperties.iterator(); r.hasNext();) {
//				String ores = (String) r.next();
//				System.out.println("lang: " + ores);
//			}			
		}
		
		RDFNode glossNode = ind.getPropertyValue(glossProp);
		if(glossNode != null) {
			syn.setDef(((Literal) glossNode).getString());
		}
		
		for(StmtIterator i = ind.listProperties(); i.hasNext();) {
			Statement stm = (Statement) i.next();
			//System.out.println("@@ STMT @@: " + stm);
			Property prop = stm.getPredicate();
			String propUri = prop.getNameSpace() + prop.getLocalName();
			if(langProperties.contains(propUri)) {
				addLinguisticProperty(syn, prop, stm.getObject());
			}
		}
		
		syn.setLexicalPropCached(true);
	}
	
	//TODO
	//MODIFICARE !!!!	
	void addSemanticProperties(Synset syn) {
		//Aggiunge le propriet� semantiche
		
//		System.out.println("Adding semantic properties to " 
//		+ syn.getLexicalForm() + "...");
		if(syn.isSemanticPropCached()) {
//			System.out.println("..already cached!");
			return;
		}
		
		OntModel om = KbModelFactory.getModel("dpinf");
		
		Individual ind = om.getIndividual(syn.getURI());
		
		//Questo funziona per le declared properties:
		for(StmtIterator si = om.listStatements(
				ind, (Property) null, (RDFNode) null); 
				si.hasNext();) {
			Statement stm = si.nextStatement();
			Property op = stm.getPredicate();
			RDFNode obj = stm.getObject();
			addSemanticProperty(syn, op, obj);
		}
		
		/*
		 * Occorre una schema ontologico per le semantic property,
		 * che deve essere caricato, dal quale prendere i link
		 * semantici verso gli altri synset e i nomi dei link.
		 */		

		syn.setSemanticPropCached(true);
	}	
	
	private void addLinguisticProperty(Synset syn, Property prop, RDFNode obj) {
		
		String propName = prop.getLocalName();
		String resName = ((Resource) obj).getLocalName();
		String resNS = ((Resource) obj).getNameSpace();
		Synset objSynset = (Synset) synsets.get(resNS + resName);
		//System.out.println("addLingProp: prop:" + propName + " resName:" + resName + " obj:" + objSynset);
		if(objSynset == null) {
			//System.err.println("addLinguisticProperty() - objSynset is null!");
			//The obj synset has to be initialized...
			initSynset(resNS + resName);
			objSynset = (Synset) synsets.get(resNS + resName);
			//return;
		}
		Collection mappedSynsets = (Collection) syn.lexicalToSynset.get(propName);
		if(mappedSynsets == null) {
			mappedSynsets = new HashSet();
			syn.lexicalToSynset.put(propName, mappedSynsets);
		}
		//Se viene implementato un Set non possono esserci doppioni (elementi disordinati!)
		mappedSynsets.add(objSynset);		
	}
	
	
	private void addSemanticProperty(Synset syn, Property prop, RDFNode obj) {
		
		String propName = prop.getLocalName();
		String resUri = ((Resource) obj).getNameSpace() +
			((Resource) obj).getLocalName();
		Synset objSynset = (Synset) synsets.get(resUri);
		if(objSynset == null) {
			System.err.println("Unknown instance found: " + objSynset);
			return;
		}
		Collection mappedSynsets = (Collection) syn.semanticToSynset.get(propName);
		if(mappedSynsets == null) {
			mappedSynsets = new HashSet();
			syn.semanticToSynset.put(propName, mappedSynsets);
		}
		//Se viene implementato un Set non possono esserci doppioni (elementi disordinati!)
		mappedSynsets.add(objSynset);		
	}	
	
	Collection search(String search, String type) {
		//Compie ricerche lessicali nell'insieme dei synset
		
		if(search.length() < 1) {
			return sortedSynsets;
		}
		
		search = search.toLowerCase();
		
		Vector results = new Vector();
		for(Iterator i = sortedSynsets.iterator(); i.hasNext(); ) {
			Synset syn = (Synset) i.next();
			Collection variants = syn.getVariants();
			for(Iterator k = variants.iterator(); k.hasNext();) {
				String lemma = (String) k.next();
				if(checkLemma(lemma, search, type)) {
					results.add(syn);
					break;
				}
			}
		}
		
		return results;
	}
	
	private boolean checkLemma(String lemma, String search, String type) {
		//ricerca full-text con ranking...??
		
		if(type.equalsIgnoreCase(KbManager.CONTAINS)) {
			if(lemma.toLowerCase().indexOf(search) > -1) {
				return true;
			}
		}
		if(type.equalsIgnoreCase(KbManager.STARTSWITH)) {
			if(lemma.toLowerCase().startsWith(search)) {
				return true;
			}			
		}
		if(type.equalsIgnoreCase(KbManager.ENDSWITH)) {
			if(lemma.toLowerCase().endsWith(search)) {
				return true;
			}
		}
		if(type.equalsIgnoreCase(KbManager.MATCHES)) {
			if(lemma.equalsIgnoreCase(search)) {
				return true;
			}	
		}
		
		return false;
	}
	
	/*
	 * 
	 * FUNZIONI SPERIMENTALI
	 * 
	 */
	
	void saveOwlClasses() {
		
		SynsetTree tree = getTree();
		
		ModelMaker maker = ModelFactory.createMemModelMaker();
		OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_MEM );
		spec.setImportModelMaker(maker);
		OntModel om = ModelFactory.createOntologyModel(spec, null);
		
		TreeModel model = tree.getModel();
		Object root = model.getRoot();
		walkClass(model, root, null, om, 0);

		write(om, "/home/lorenzo/ontologies/consumer-law-classes.owl");

	}
	
	private void walkClass(TreeModel model, Object o, 
			OntClass oc, OntModel om, int indent) {
		
		int  cc = model.getChildCount(o);
		for( int i = 0; i < cc; i++) {
			Object child = model.getChild(o, i);
			Object data = ((DefaultMutableTreeNode) child).getUserObject();
			if(data instanceof TreeOntoClass) {
				OntClass newOc = om.createClass((
						"file://home/lorenzo/ontologies/consumer-law-classes.owl#" +
							((TreeOntoClass) data).name));
				if(oc != null) {
					oc.addSubClass(newOc);
				}
				String ind = " ";
				for(int c = 0; c < indent; c++) {
					ind += "   ";
				}
				System.out.println(ind + "+ " + newOc.getLocalName());
				walkClass(model, child, newOc, om, ++indent);
			}
		}
	}
	
	private static void write(OntModel om, String outputFile) {
		
		System.out.println("Serializing ontology model to " + outputFile + "...");

		RDFWriter writer = om.getWriter("RDF/XML-ABBREV"); //faster than RDF/XML-ABBREV
		
		//RDFWriter configuration
		
		//More info about properties and error handler (RDFWriter/RDFReader) at:
		//http://jena.sourceforge.net/IO/iohowto.html
		//http://jena.sourceforge.net/javadoc/com/hp/hpl/jena/xmloutput/RDFXMLWriterI.html
		
		//Get relative file name and use it as base..
		String relativeOutputFileName = "file:consumer-law-classes.owl";
		
		//set base property
		writer.setProperty("xmlbase", relativeOutputFileName);
		
		try {
			OutputStream out = new FileOutputStream(outputFile);
			//Write down the BASE model only (don't follow imports...)
			writer.write(om.getBaseModel(), out, relativeOutputFileName);
			out.close();
		} catch(Exception e) {
			System.err.println("Exception serializing model:" + e.getMessage());
			e.printStackTrace();
		}
	}

	void checkFrameRepresentation() {
		
		long t1 = System.currentTimeMillis();
		
		Set properties = new HashSet();

		OntModel om = KbModelFactory.getModel("domain", "micro", LANGUAGE);
		
		System.out.println("\n\n\n\n@@@@@@@@@@ KB - FRAME REPRESENTATION @@@@@@@@");
		for(Iterator i = om.listClasses(); i.hasNext();) { 
			OntClass oc = (OntClass) i.next();
			if(!oc.isAnon() && 
					!oc.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
				continue;
			}

			System.out.println("\n\n@ OntClass: " + oc.getLocalName());
			for(Iterator p = oc.listDeclaredProperties(false); p.hasNext(); ) {
				OntProperty op = (OntProperty) p.next();
				if(properties.contains(op)) {
					continue;
				} else {
					properties.add(op);
				}
				if(!op.isAnon() &&
						!op.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
					continue;
				}
				System.out.println("@@@ OntProperty: " + op.getLocalName());
				int anons = 0;
				for(Iterator r = op.listRange(); r.hasNext();) {
					OntClass range = (OntClass) r.next();
					if(!range.isAnon() && 
							!range.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
						continue;
					}
					if(!range.isAnon()) {
						System.out.println("@@@@@ Range: " + range.getLocalName());
					} else {
						if(range.isRestriction()) {
							System.out.println("@@@@@ Range Restriction: " +
									range.toString());
						}
						anons++;
					}
				}
				System.out.println("@@@@@ Range # Anon Classes: " + anons);
			}
		}	

		long t2 = System.currentTimeMillis();
		System.out.println("...Done with Frame Representation! (" 
				+ Long.toString(t2 - t1) + " ms)\n");
	}
	
	void compute(String type) {
		
		SPEngine spe = new SPEngine(KbModelFactory.getModel("full", "micro", LANGUAGE), this.LANGUAGE);
		spe.compute(type);
		System.out.println("COMPUTE processing done.");
	}
}
