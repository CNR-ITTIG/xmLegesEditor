package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;

public class KbContainer {

	private String LANGUAGE;
	
	public String localRepository;
	public String indFile;
	public String indwFile;
	public String indcFile;
	public String typesFile;
	public String conceptsFile;
	public String sourcesFile;
	
	private I18n i18n;

	//Dati
	Map synsets = null;
	Vector sortedSynsets = null; //meglio un TreeSet ??
	
	private KbTree kbt;

	public KbContainer(String lang, I18n i) {
		
		i18n = i;

		LANGUAGE = lang;

		localRepository = KbConf.dalosRepository + LANGUAGE + "/";
		indFile = localRepository + KbConf.IND;
		indwFile = localRepository + KbConf.INDW;
		indcFile = localRepository + KbConf.INDC;
		typesFile = localRepository + KbConf.TYPES;
		conceptsFile = localRepository + KbConf.CONCEPTS;
		sourcesFile = localRepository + KbConf.SOURCES;
		
		if(!checkFiles()) {
			System.err.println("## ERROR ## KbContainer - Data files not found! Repo: " +
					localRepository);			
		} else {			
			synsets = new HashMap(2048, 0.70f);
			sortedSynsets = new Vector(512);
			
			kbt = new KbTree(this, i18n);
	
			long t1 = System.currentTimeMillis();		
			System.out.println("Init synsets...");
			initSynsets();
			long t2 = System.currentTimeMillis();
			System.out.println("...synsets loaded! (" + Long.toString(t2 - t1) + " ms)\n");
		}
	}
	
	private boolean checkFiles() {
		
		if(!UtilFile.fileExistInTemp(indFile)) {
			return false;
		}
		if(!UtilFile.fileExistInTemp(indwFile)) {
			return false;
		}
		if(!UtilFile.fileExistInTemp(indcFile)) {
			return false;
		}
		if(!UtilFile.fileExistInTemp(typesFile)) {
			return false;
		}
		if(!UtilFile.fileExistInTemp(conceptsFile)) {
			return false;
		}
		if(!UtilFile.fileExistInTemp(sourcesFile)) {
			return false;
		}
		
		return true;
	}

	OntModel getModel(String type) {
		
		return getModel(type, "");
	}
	
	OntModel getModel(String type, String reasoner) {
		/*
		 * Ritorna un OntModel in base a varie configurazioni.
		 * type: sceglie i moduli ontologici da caricare
		 * reasoner: sceglie il reasoner da utilizzare nel modello
		 */
		
		ModelMaker maker = ModelFactory.createMemModelMaker();

		OntModelSpec spec = null;
		if(reasoner.length() > 0) {
			Reasoner r = null;
			if(reasoner.equalsIgnoreCase("rdf")) {
				r = ReasonerRegistry.getRDFSReasoner();
			}
			if(reasoner.equalsIgnoreCase("micro")) {
				r = ReasonerRegistry.getOWLMicroReasoner();
			}
			if(reasoner.equalsIgnoreCase("mini")) {
				r = ReasonerRegistry.getOWLMiniReasoner();
			}
			if(reasoner.equalsIgnoreCase("owl")) {
				r = ReasonerRegistry.getOWLReasoner();
			}
			if(reasoner.equalsIgnoreCase("pellet")) {
				//pu� servire pellet ? oppure un external reasoner?
			}
			if(r == null) {
				System.err.println("getModel() - Reasoner type not found: " + type);
				return null;
			}
			spec =  OntModelSpec.OWL_MEM ;
			spec.setReasoner(r);
		} else {
			spec = new OntModelSpec( OntModelSpec.OWL_MEM );
		}
		spec.setImportModelMaker(maker);
		OntModel om = ModelFactory.createOntologyModel(spec, null);
		
		if(type.equalsIgnoreCase("full")) {			
			om.read(KbConf.METALEVEL_PROP);
			om.read(KbConf.DOMAIN_ONTO);
			readData(om, conceptsFile);
			readData(om, indFile);
			readData(om, indwFile);
			readData(om, indcFile);		
			readData(om, typesFile);
		}
		if(type.equalsIgnoreCase("domain")) {
			om.read(KbConf.DOMAIN_ONTO);
		}
		if(type.equalsIgnoreCase("mapping")) {
			om.read(KbConf.METALEVEL_ONTO);			
			readData(om, indcFile);		
		}
		if(type.equalsIgnoreCase("individual")) {
			om.read(KbConf.METALEVEL_ONTO);
			om.read(KbConf.METALEVEL_PROP);
			readData(om, indFile);
			readData(om, indwFile);
			readData(om, typesFile);
		}
		if(type.equalsIgnoreCase("source")) {
			//om.read(KbConf.SOURCE_SCHEMA); //Ci vuole questo metalivello??
			readData(om, indFile);
			readData(om, sourcesFile);			
		}
		OntDocumentManager odm = OntDocumentManager.getInstance();
		odm.setProcessImports(true);
		odm.loadImports(om);
		
		return om;
	}
	
	private void readData(OntModel om, String fileStr) {
		
		File file = UtilFile.getFileFromTemp(fileStr);
		om.read("file://" + file.getAbsolutePath());
	}
	
	SynsetTree getTree() {
		
		return kbt.getTree();
	}
	
	private void initSynsets() {
		
		OntModel ind_m = getModel("individual", "micro");
		
		OntProperty contains = ind_m.getOntProperty(KbConf.METALEVEL_ONTO_NS + "containsWordSense");
		OntProperty word = ind_m.getOntProperty(KbConf.METALEVEL_ONTO_NS + "word");
		OntProperty lexical = ind_m.getOntProperty(KbConf.METALEVEL_ONTO_NS + "lexicalForm");
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
			for(Iterator k = ores.listProperties(contains); i.hasNext();) {
				OntResource ws = (OntResource) i.next();			
				OntResource w = (OntResource) ws.getPropertyValue(word);			
				RDFNode lexNode = (RDFNode) w.getPropertyValue(lexical);			
				String lex = ((Literal) lexNode).getString();
				if(syn == null) {
					syn = new Synset(lex);
					syn.setLanguage(LANGUAGE);
					syn.setURI(ores.getNameSpace() + ores.getLocalName());
					//System.out.println("Adding " + ores.getLocalName() + " --> " + syn);			
					synsets.put(ores.getLocalName(), syn); //meglio questa come chiave??
					addSortedSynset(syn);
				} else {
					syn.variants.add(lex);
				}
			}
			
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
	
	void addSources(Synset syn) {
		
		if(syn.isSourceCached()) {
			return;
		}
		
		System.out.println(">> adding sources to " + syn + "...");
		
		OntModel om = getModel("source", "micro");
		
		Individual ind = om.getIndividual(syn.getURI());

		OntProperty sourceProp = om.getOntProperty(KbConf.SOURCESCHEMA_NS + "source");
		OntProperty involvesProp = om.getOntProperty(KbConf.SOURCESCHEMA_NS + "source");
		OntProperty belongsProp = om.getOntProperty(KbConf.SOURCESCHEMA_NS + "source");
		OntProperty linkProp = om.getOntProperty(KbConf.SOURCESCHEMA_NS + "source");
		
		for(Iterator i = ind.listProperties(sourceProp); i.hasNext();) {
			OntResource ores = (OntResource) i.next();
			OntResource part = (OntResource) ores.getPropertyValue(involvesProp);
			OntResource doc = (OntResource) part.getPropertyValue(belongsProp);
			RDFNode val = doc.getPropertyValue(linkProp);
			
			String link = "http://somewhere.over.the.rainbow/documents/doc.html";
			if(val != null) {
				link = ((Literal) val).getString();
			}
			syn.addSource(link);
		}
		
		syn.setSourceCached(true);
	}

	void addProperties(Synset syn) {
		if(syn.isPropCached()) {
			return;
		}

		System.out.println(">> adding properties to " + syn + "...");
		
		addLinguisticProperties(syn);

		addSemanticProperties(syn);
		
		syn.setPropCached(true);	
	}
	

	private void addLinguisticProperties(Synset syn) {
		//Aggiunge le propriet� generiche e quelle lessicali

		OntModel om = getModel("individual", "micro");
		
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
		Set langProperties = new HashSet();
		for(Iterator i = langProp.listSubProperties(false); i.hasNext(); ) {
			OntProperty prop = (OntProperty) i.next();
			langProperties.add(prop.getNameSpace() + prop.getLocalName());
		}
		
		RDFNode glossNode = ind.getPropertyValue(glossProp);
		if(glossNode != null) {
			syn.setDef(((Literal) glossNode).getString());
		}
		
		for(StmtIterator i = ind.listProperties(); i.hasNext();) {
			Statement stm = (Statement) i.next();
			Property prop = stm.getPredicate();
			String propUri = prop.getNameSpace() + prop.getLocalName();
			if(langProperties.contains(propUri)) {
				addLinguisticProperty(syn, prop, stm.getObject());
			}
		}
	}
	
	
	private void addSemanticProperties(Synset syn) {
		//Aggiunge le propriet� semantiche
		
		OntModel om = getModel("full", "micro");
		
		Individual ind = om.getIndividual(syn.getURI());
		for(Iterator i = ind.listRDFTypes(false); i.hasNext();) {
			Resource res = (Resource) i.next();
			if(res.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
				OntClass oc = (OntClass) res.as(OntClass.class);
				for(Iterator p = oc.listDeclaredProperties(false); p.hasNext();) {
					OntProperty op = (OntProperty) p.next();
					if(op.isDatatypeProperty()) {
						continue;
					}
					for(Iterator r = op.listRange(); r.hasNext();) {
						OntClass range = (OntClass) r.next();
						for(Iterator ist = range.listInstances(false); ist.hasNext();) {
							Resource obj = (Resource) ist.next();
							addSemanticProperty(syn, op, obj);
						}
					}
				}
			}
		}		
	}
	
	
	private void addLinguisticProperty(Synset syn, Property prop, RDFNode obj) {
		
		String propName = prop.getLocalName();
		Synset objSynset = (Synset) synsets.get(((Resource) obj).getLocalName());
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
		
		Vector results = new Vector();
		for(int i = 0; i < sortedSynsets.size(); i++) {
			Synset syn = (Synset) sortedSynsets.get(i);
			for(int k = 0; k < syn.variants.size(); k++) {
				String lemma = (String) syn.variants.get(k);
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
			if(lemma.toLowerCase().indexOf(search) > 1) {
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
}
