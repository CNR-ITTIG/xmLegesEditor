package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Source;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
	
	Set langProperties = null;
	
	private KbTree kbt;

	public KbContainer(String lang, I18n i) {
		
		i18n = i;

		LANGUAGE = lang;

		localRepository = KbConf.dalosRepository + LANGUAGE + "/";
		indFile = localRepository + KbConf.IND;
		indwFile = localRepository + KbConf.INDW;
		indcFile = localRepository + KbConf.INDC;
		typesFile = localRepository + KbConf.TYPES;
		sourcesFile = localRepository + KbConf.SOURCES;
		conceptsFile = KbConf.dalosRepository + KbConf.CONCEPTS;
		
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
		
		File file = null;
		
		//Remote ontologies are locally cached...
		//Aggiungere una funzione che, se on-line, scarica le ontologie remote
		//in modo da avere sempre l'ultima versione?
		OntDocumentManager odm = OntDocumentManager.getInstance();		
		file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_DOMAIN_ONTO);
		odm.addAltEntry(KbConf.DOMAIN_ONTO, "file://" + file.getAbsolutePath());
		file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_METALEVEL_ONTO);
		odm.addAltEntry(KbConf.METALEVEL_ONTO, "file://" + file.getAbsolutePath());
		file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_METALEVEL_PROP);
		odm.addAltEntry(KbConf.METALEVEL_PROP, "file://" + file.getAbsolutePath());
		file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_METALEVEL_FULL);
		odm.addAltEntry(KbConf.METALEVEL_FULL, "file://" + file.getAbsolutePath());
		file = UtilFile.getFileFromTemp(KbConf.dalosRepository + KbConf.LOCAL_SOURCE_SCHEMA);
		odm.addAltEntry(KbConf.SOURCE_SCHEMA, "file://" + file.getAbsolutePath());

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
			om.read(KbConf.SOURCE_SCHEMA); //Ci vuole questo metalivello??
			readData(om, indFile);
			readData(om, sourcesFile);			
		}
		
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
	
	boolean setTreeSelection(Synset syn) {
		
		return kbt.setSelection(syn);
	}
	
	private void initSynsets() {
		
		OntModel ind_m = getModel("individual", "micro");
		
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
		
		OntModel om = getModel("source", "micro");
		
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

		System.out.println("Adding lexical properties...");
		if(syn.isLexicalPropCached()) {
			System.out.println("..already cached!");
			return;
		}

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
		
		if(syn.isSemanticPropCached()) {
			return;
		}

		OntModel om = getModel("full", "micro");
		
		Individual ind = om.getIndividual(syn.getURI());
		for(Iterator i = ind.listRDFTypes(false); i.hasNext();) {
			Resource res = (Resource) i.next();
			if(!res.isAnon() && res.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
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
		
		syn.setSemanticPropCached(true);
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
