package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
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
import com.hp.hpl.jena.vocabulary.RDFS;

public class KbContainer {

	private String LANGUAGE;
	
	public String localRepository;
	public String infRepository;
	public String segRepository;

//	public String indFile;
//	public String indwFile;
//	//public String indclawFile;
//	public String typesFile;
//	public String conceptsFile;
//	public String sourcesFile;
//	public String infDpFile;
//	public String infDpExtFile;
	
//	public static String INDIVIDUAL_NS = KbConf.DALOS_NS + "individuals.owl" + "#";
//	public static String INDIVIDUAL_WORDS_NS = KbConf.DALOS_NS + "individuals-word.owl" + "#";
//	public static String INDIVIDUAL_CLAW_NS = KbConf.DALOS_NS + "ind-to-consumer.owl" + "#";
//	public static String TYPES_NS = KbConf.DALOS_NS + "types.owl" + "#";
//	public static String SOURCES_NS = KbConf.DALOS_NS + "sources.owl" + "#";

	private I18n i18n;
	
	private boolean concreteContainer = false;

	//Dati
	Map synsets = null;
	Vector sortedSynsets = null; //meglio un TreeSet ??
	
//	Map uriToLexSeg = null;
//	Map uriToSourceSeg = null;
//	Map uriToSemSeg = null;

	Set langProperties = null;
	
	private KbTree kbt;

	public KbContainer(String lang, I18n i) {
		
		i18n = i;

		LANGUAGE = lang;

		if(!checkFiles()) {
			System.err.println("## ERROR ## KbContainer - Data files not found! Repo: " +
					localRepository);
		} else {			
			concreteContainer = true;
		}
	}
	
	boolean initData() {		
		
		if(!concreteContainer) {
			return false;
		}
		
		synsets = new HashMap(2048, 0.70f);
		sortedSynsets = new Vector(512);
		
		kbt = new KbTree(this, i18n);

		long t1 = System.currentTimeMillis();		
		System.out.println("Init synsets...");
		initSynsets();
		long t2 = System.currentTimeMillis();
		System.out.println("...synsets loaded! (" +
				Long.toString(t2 - t1) + " ms)\n");

		//init segment map
		try {
			initMaps();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
	
	boolean isConcreteContainer() {
		
		return concreteContainer;
	}
	
	void initPivotMapping(Map pivotToForeign) {
		
		System.out.println("Initializing Pivot Classes...");
		OntModel mod = KbModelFactory.getModel("concepts");
		
		Set pivots = pivotToForeign.keySet();
		
		//Assumptions: no reasoning, no anonymous resources
		//and objects are OntResources
		for(Iterator i = mod.listStatements(
				(Resource) null, RDFS.subClassOf, (RDFNode) null);
				i.hasNext();) {
			Statement stm = (Statement) i.next();
			Resource subj = stm.getSubject();
			OntResource obj = (OntResource) stm.getObject();

			String subjNS = subj.getNameSpace();
			String objNS = obj.getNameSpace();
			String subjName = subj.getLocalName();
			String objName = obj.getLocalName();
			
			if(!subjNS.equalsIgnoreCase(KbConf.DALOS_CONCEPTS_NS)) {
				System.err.println("initPivotMapping() - subjNS: " + subjNS);
				continue;
			}
			if(!objNS.equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
				System.err.println("initPivotMapping() - subjNS: " + subjNS);
				continue;
			}
			
			PivotOntoClass poc = new PivotOntoClass();
			poc.setURI(subjName + subjNS);
			if(pivots.contains(poc)) {
				//Retrieve the original pivot class...
				for(Iterator pi = pivots.iterator(); pi.hasNext();) {
					Object item = pi.next();
					if(poc.equals(item)) {
						poc = (PivotOntoClass) item;
						break;
					}
				}
			}
			
		}
	}
	
	private void initMaps() 
		throws FileNotFoundException, IOException, ClassNotFoundException {
		
		System.out.println("KbContainer: checking KB segmentation support...");
		
		String mapFileName = segRepository + KbConf.lexicalSegmentName +
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
				String segFileName = segRepository + KbConf.lexicalSegmentName 
						+ File.separatorChar + value;
				KbModelFactory.addSegment(key, segFileName,
						KbConf.lexicalSegmentName);
			}
		}
		
		mapFileName = segRepository + KbConf.sourceSegmentName +
			File.separatorChar + KbConf.mapSegmentFileName;
		mapFile = UtilFile.getFileFromTemp(mapFileName);
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
				String segFileName = segRepository + KbConf.sourceSegmentName 
						+ File.separatorChar + value;
				KbModelFactory.addSegment(key, segFileName,
						KbConf.sourceSegmentName);
			}
		}
		
		mapFileName = segRepository + KbConf.semanticSegmentName +
		File.separatorChar + KbConf.mapSegmentFileName;
		mapFile = UtilFile.getFileFromTemp(mapFileName);
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
				String segFileName = segRepository + KbConf.semanticSegmentName 
						+ File.separatorChar + value;
				KbModelFactory.addSegment(key, segFileName,
						KbConf.semanticSegmentName);
			}
		}
	}

	Collection getTopClasses() {
		/*
		 * Return a set of String that represent the local names
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
				classes.add(oc.getLocalName());
				System.out.println("++ TOP CLASS: " 
						+ oc.getNameSpace() + oc.getLocalName());
			}
		}		
		
		return classes;
	}
		
	private boolean checkFiles() {
		
		localRepository = KbConf.dalosRepository + LANGUAGE + File.separatorChar;
		infRepository = KbConf.dalosRepository + LANGUAGE + File.separatorChar +
							KbConf.inferenceDir + File.separatorChar;
		segRepository = KbConf.dalosRepository + LANGUAGE + File.separatorChar +
			KbConf.segmentDirName + File.separatorChar;
		
		String indFile = localRepository + KbConf.IND;
		String indwFile = localRepository + KbConf.INDW;
		//indclawFile = localRepository + KbConf.INDCLAW;
		String typesFile = localRepository + KbConf.TYPES;
		String sourcesFile = localRepository + KbConf.SOURCES;
//		String infDpFile = infRepository + KbConf.DP_INF;
//		String infDpExtFile = infRepository + KbConf.DPEXT_INF;
		
		if(!UtilFile.fileExistInTemp(indFile)) {
			KbModelFactory.addDocument(KbConf.IND, LANGUAGE, indFile);
			return false;
		}
		if(!UtilFile.fileExistInTemp(indwFile)) {
			KbModelFactory.addDocument(KbConf.INDW, LANGUAGE, indwFile);
			return false;
		}
//		if(!UtilFile.fileExistInTemp(indclawFile)) {
//			return false;
//		}
		if(!UtilFile.fileExistInTemp(typesFile)) {
			KbModelFactory.addDocument(KbConf.TYPES, LANGUAGE, typesFile);
			return false;
		}
//		if(!UtilFile.fileExistInTemp(conceptsFile)) {
//			return false;
//		}
		if(!UtilFile.fileExistInTemp(sourcesFile)) {
			KbModelFactory.addDocument(KbConf.SOURCES, LANGUAGE, sourcesFile);
			return false;
		}
		
		return true;
	}

	SynsetTree getTree() {
		
		return kbt.getTree();
	}
	
	boolean setTreeSelection(Synset syn) {
		
		return kbt.setSelection(syn);
	}
	
	private void initSynsets() {
		
		OntModel ind_m = KbModelFactory.getModel("individual", "micro");
		
		OntProperty contains = ind_m.getOntProperty(KbConf.METALEVEL_ONTO_NS + "containsWordSense");
		OntProperty word = ind_m.getOntProperty(KbConf.METALEVEL_ONTO_NS + "word");
		OntProperty lexical = ind_m.getOntProperty(KbConf.METALEVEL_ONTO_NS + "lexicalForm");
		OntProperty proto = ind_m.getOntProperty(KbConf.METALEVEL_ONTO_NS + "protoForm");
		OntClass synClass = ind_m.getOntClass(KbConf.METALEVEL_ONTO_NS + "Synset");
		if(contains == null || word == null || lexical == null) {
			System.out.println("ERROR: null properties.");
			return;
		}
		if(synClass == null) {
			System.out.println("ERROR: null synClass.");
			return;
		}
		
		for(Iterator i = synClass.listInstances(false); i.hasNext();) {

			Synset syn = null;
			OntResource ores = (OntResource) i.next();		

			syn = new Synset();
			syn.setLanguage(LANGUAGE);
			syn.setURI(ores.getNameSpace() + ores.getLocalName());

			for(Iterator k = ores.listPropertyValues(contains); k.hasNext();) {
				OntResource ws = (OntResource) k.next();
				OntResource w = (OntResource) ws.getPropertyValue(word);
				RDFNode protoNode = (RDFNode) w.getPropertyValue(proto);
				if(protoNode != null) {
					String protoForm = ((Literal) protoNode).getString();
					syn.setLexicalForm(protoForm);					
				}
				for(Iterator l = w.listPropertyValues(lexical); l.hasNext(); ) {
					RDFNode lexNode = (RDFNode) l.next();
					String lexForm = ((Literal) lexNode).getString();
					((Collection) syn.getVariants()).add(lexForm);
				}
			}
			
			//System.out.println("Adding " + ores.getLocalName() + " --> " + syn);			
			synsets.put(ores.getLocalName(), syn); //meglio questa come chiave??
			addSortedSynset(syn);
		}
	}
	
	private void addSortedSynset(Synset syn) {
		
		boolean ins = false;
		for(int i = 0; i < sortedSynsets.size(); i++) {
			Synset item = (Synset) sortedSynsets.get(i);
			if(item.toString().compareToIgnoreCase(syn.toString()) < 0) continue;
			if(item.toString().compareToIgnoreCase(syn.toString()) > 0) {
				sortedSynsets.add(i, syn);
				ins = true;
				break;
			}
		}
		if(!ins) {
			//Inserisci alla fine del vettore
			sortedSynsets.add(syn);
		}
	}
	
	Synset getSynset(String uri) {
		
		if(uri.indexOf("#") > -1) {
			uri = uri.substring(uri.indexOf("#") + 1);
		}
		
		return (Synset) synsets.get(uri);
	}
	
	void addSources(Synset syn) {
		
		if(syn.isSourceCached()) {
			return;
		}
		
		System.out.println(">> adding sources to " + syn + "...");
		
		OntModel om = KbModelFactory.getModel("source", "micro");
		
		Individual ind = om.getIndividual(syn.getURI());

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
					source.setContent(((Literal) contentNode).getString());
				}
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

		//OntModel om = KbModelFactory.getModel("individual", "micro");
		OntModel om = KbModelFactory.getModel("seg.lex", "micro", syn);
		
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
		Synset objSynset = (Synset) synsets.get(resName);
		//System.out.println("addLingProp: prop:" + propName + " resName:" + resName + " obj:" + objSynset);
		if(objSynset == null) {
			System.err.println("addLinguisticProperty() - objSynset is null!");
			return;
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
		Synset objSynset = (Synset) synsets.get(((Resource) obj).getLocalName());
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
		for(int i = 0; i < sortedSynsets.size(); i++) {
			Synset syn = (Synset) sortedSynsets.get(i);
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
		
		if(type.equalsIgnoreCase("contains")) {
			if(lemma.toLowerCase().indexOf(search) > -1) {
				return true;
			}
		}
		if(type.equalsIgnoreCase("startsWith")) {
			if(lemma.toLowerCase().startsWith(search)) {
				return true;
			}			
		}
		if(type.equalsIgnoreCase("endsWith")) {
			if(lemma.toLowerCase().endsWith(search)) {
				return true;
			}
		}
		if(type.equalsIgnoreCase("exact")) {
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

		OntModel om = KbModelFactory.getModel("domain", "micro");
		
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
		
		SPEngine spe = new SPEngine(KbModelFactory.getModel("full", "micro"), this.LANGUAGE);
		spe.compute(type);
		System.out.println("COMPUTE processing done.");
	}
}
