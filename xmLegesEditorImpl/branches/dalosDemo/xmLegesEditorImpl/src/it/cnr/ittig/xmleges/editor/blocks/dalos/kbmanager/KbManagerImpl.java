package it.cnr.ittig.xmleges.editor.blocks.dalos.kbmanager;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.editor.blocks.dalos.kbmanager.KBConf;
import it.cnr.ittig.xmleges.editor.services.dalos.kbmanager.KbManager;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.OntoClass;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.SynsetTree;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

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
import com.hp.hpl.jena.vocabulary.RDF;

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
	
	private String LANGUAGE = ""; //Usare un KBManager diverso per ogni lingua???
	public String KB_REPOSITORY = "file://" + KBConf.LOCAL_REPOSITORY; // + "/IT";
	public String IND_FILE = KB_REPOSITORY + "/individuals.owl";
	public String INDW_FILE = KB_REPOSITORY + "/individuals-word.owl";
	public String IND_CLAW = KB_REPOSITORY + "/ind-to-consumer.owl";
	public String TYPES = KB_REPOSITORY + "/types.owl";
	public String CONCEPT_ONTO = KB_REPOSITORY + "/concepts.owl";
	public String SOURCE_FILE = KB_REPOSITORY + "/sources.owl";
	
	//Dati
	public Map synsets = null;
	public Vector sortedSynsets = null;
	
	private SynsetTree tree = null;
	
	private Set topClasses = null;
	private Map linked = null; 
	
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
		
		LANGUAGE = "IT";
		synsets = new HashMap(2048, 0.70f);
		sortedSynsets = new Vector(512);
		topClasses = new HashSet();
		
		long t1 = System.currentTimeMillis();		
		System.out.println("Init synsets...");
		initSynsets();
		long t2 = System.currentTimeMillis();
		System.out.println("...synsets loaded! (" + Long.toString(t2 - t1) + " ms)\n");	
	}
	
	
	private void initSynsets() {
		
		OntModel ind_m = getModel("individual", "micro");
		
		OntProperty contains = ind_m.getOntProperty(KBConf.METALEVEL_ONTO_NS + "containsWordSense");
		OntProperty word = ind_m.getOntProperty(KBConf.METALEVEL_ONTO_NS + "word");
		OntProperty lexical = ind_m.getOntProperty(KBConf.METALEVEL_ONTO_NS + "lexicalForm");
		OntClass synClass = ind_m.getOntClass(KBConf.METALEVEL_ONTO_NS + "Synset");
		if(contains == null || word == null || lexical == null) {
			System.out.println("ERROR: null properties.");
			return;
		}
		if(synClass == null) {
			System.out.println("ERROR: null synClass.");
			return;
		}
		
		for(Iterator i = synClass.listInstances(false); i.hasNext();) {
			
			OntResource ores = (OntResource) i.next();			
			OntResource ws = (OntResource) ores.getPropertyValue(contains);
			if(ws == null) {
				System.out.println("No wordsense for " + ores);
				continue;
			}
		
			OntResource w = (OntResource) ws.getPropertyValue(word);			
			RDFNode lexNode = (RDFNode) w.getPropertyValue(lexical);			
			String lex = ((Literal) lexNode).getString();
			
			Synset syn = new Synset(lex);
			syn.setLanguage(LANGUAGE);
			syn.setURI(ores.getNameSpace() + ores.getLocalName());
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
	
	private OntModel getModel(String type) {
		
		return getModel(type, "");
	}
	
	private OntModel getModel(String type, String reasoner) {
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
			//om.read(METALEVEL_ONTO); //basta importare il PROP...?
			om.read(KBConf.METALEVEL_PROP);
			om.read(KBConf.DOMAIN_ONTO);
			om.read(CONCEPT_ONTO);
			om.read(IND_FILE);
			om.read(INDW_FILE);
			om.read(IND_CLAW);		
			om.read(TYPES);
		}
		if(type.equalsIgnoreCase("domain")) {
			om.read(KBConf.DOMAIN_ONTO);
		}
		if(type.equalsIgnoreCase("mapping")) {
			om.read(IND_CLAW);		
			om.read(KBConf.METALEVEL_ONTO);			
		}
		if(type.equalsIgnoreCase("individual")) {
			om.read(KBConf.METALEVEL_ONTO);
			om.read(KBConf.METALEVEL_PROP);
			om.read(IND_FILE);
			om.read(INDW_FILE);
			om.read(TYPES);
		}
		if(type.equalsIgnoreCase("source")) {
			om.read(IND_FILE);
			om.read(SOURCE_FILE);			
		}
		OntDocumentManager odm = OntDocumentManager.getInstance();
		odm.setProcessImports(true);
		odm.loadImports(om);
		
		return om;
	}
	

	public void addProperties(Synset syn) {
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
		OntProperty glossProp = om.getOntProperty(KBConf.METALEVEL_ONTO_NS + "gloss");
		//OntProperty sourceProp = om.getOntProperty(METALEVEL_ONTO_NS + "source");  //fonte		
		OntProperty langProp = om.getOntProperty(KBConf.METALEVEL_PROP_NS + "language-property");
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
			if(res.getNameSpace().equalsIgnoreCase(KBConf.DOMAIN_ONTO_NS)) {
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
	
	public void addSources(Synset syn) {
		
		if(syn.isSourceCached()) {
			return;
		}
		
		System.out.println(">> adding sources to " + syn + "...");
		
		OntModel om = getModel("source", "micro");
		
		Individual ind = om.getIndividual(syn.getURI());

		OntProperty sourceProp = om.getOntProperty(KBConf.SOURCESCHEMA_NS + "source");
		OntProperty involvesProp = om.getOntProperty(KBConf.SOURCESCHEMA_NS + "source");
		OntProperty belongsProp = om.getOntProperty(KBConf.SOURCESCHEMA_NS + "source");
		OntProperty linkProp = om.getOntProperty(KBConf.SOURCESCHEMA_NS + "source");
		
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


	
	// da InitHierarchy
	
	public Collection getSynsets() {
		return ((HashMap)synsets).values();
	}
	
	
	public SynsetTree getTree() {
		
		long t1 = System.currentTimeMillis();		
		System.out.println("Init tree...");

     	DefaultMutableTreeNode tmpRoot = new DefaultMutableTreeNode(" - ");
     	DefaultTreeModel tmpModel = new DefaultTreeModel(tmpRoot);     	
		tree = new SynsetTree(tmpModel,i18n);
	
		tree.setRootUserObject("CONSUMER LAW");
		tree.setRootVisible(true);
		
		OntClass oc = getModel("domain", "micro").getOntClass(KBConf.ROOT_CLASS);
		
		expandClass(oc, null);
		
		addSynsets();
		
		adjustTree();
		
		long t2 = System.currentTimeMillis();
		System.out.println("...tree loaded! (" + Long.toString(t2 - t1) + " ms)\n");
		
		return tree;
	}
	
	
	private void expandClass(OntClass oc, DefaultMutableTreeNode node) {
		//Problema: se una classe non ha sotto-classi e non ha synset
		//viene visualizzata come foglia!
		//Aggiungere dei children fittizi? (empty) ?
		
		//System.out.println("Expanding " + oc.getNameSpace() + oc.getLocalName() + "...");
		DefaultMutableTreeNode newNode = null;
		
		for(Iterator i = oc.listSubClasses(true); i.hasNext();) {
			OntClass c = (OntClass) i.next();

			//escludi le classi al di fuori della consumer law
			if(!c.isAnon() && !c.getNameSpace().equalsIgnoreCase(KBConf.DOMAIN_ONTO_NS)) {
				continue;
			}
			
			if(!c.isAnon() && !c.isRestriction()) {
				OntoClass cl = new OntoClass(c.getLocalName());
				newNode = new DefaultMutableTreeNode(cl);
				if(node == null) {
					if(!topClasses.contains(cl)) {
						topClasses.add(cl);
						tree.addNode(newNode);
					}
				} else {
					node.add(newNode);
				}
				expandClass(c, newNode);
			} else {
				expandClass(c, node);
			}			
		}		
	}
	
	private void addSynsets() {
		
		System.out.println("Adding synsets to tree...");
		OntModel mapModel = getModel("mapping", "micro");
		
		linked = new HashMap(256, 0.70f);
		
		Resource subj = null;
		RDFNode obj = null;
		for(Iterator i = mapModel.listStatements(subj, RDF.type, obj); i.hasNext();) {
			Statement stm = (Statement) i.next();
			Resource thisSubj = stm.getSubject();
			RDFNode thisObjNode = stm.getObject();
			if(!thisObjNode.isResource()) {
				continue;				
			}
			Resource thisObj = (Resource) thisObjNode;
			if(thisObj.isAnon() ||
					!thisObj.getNameSpace().equalsIgnoreCase(KBConf.DOMAIN_ONTO_NS)) {
				continue;
			}
//			System.out.println("Adding to linked: " + thisObj.getLocalName() +
//					" --> " + thisSubj.getLocalName());
			addLink(thisObj, thisSubj);
		}
		
		TreeModel model = tree.getModel();
		Object root = model.getRoot();
		walk(model, root);
		
		addRemainingSynsets();
	}
	
	private void addLink(Resource oc, Resource syn) {
	
		Vector syns = null;
		String key = oc.getLocalName();
		syns = (Vector) linked.get(key);
		if(syns == null) {
			syns = new Vector();
			linked.put(key, syns);
		}
		syns.add(syn.getLocalName());
	}
	
	private void walk(TreeModel model, Object o) {
		
		int  cc = model.getChildCount(o);
		for( int i = 0; i < cc; i++) {
			Object child = model.getChild(o, i);
			Object data = ((DefaultMutableTreeNode) child).getUserObject();
			boolean isOntoClass = false;
			if(data instanceof OntoClass) {
				isOntoClass = true;
			} else  {
				//Sono i synset che vengono aggiunti??
				if(data instanceof Synset) {
					continue;
				}
				System.out.println(">>>>> " + data + " - " + isOntoClass);
				return;
			}
			
			Vector syns = (Vector) linked.get(data.toString());
			if(syns != null) {
				for(int k = 0; k < syns.size(); k++) {
					//System.out.println("Asking object mapped by " + syns.get(k));
					Synset syn = (Synset) synsets.get(syns.get(k));
					//System.out.println("++ Adding " + syn + " to " + child.toString());
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(syn);
					((DefaultMutableTreeNode) child).add(newNode);
				}
			}
			walk(model, child);
		}
	} 

	private void addRemainingSynsets() {
		
		Set addedSyns = new HashSet();
		Collection values = linked.values();
		for(Iterator i = values.iterator(); i.hasNext();) {
			Vector item = (Vector) i.next();
			for(int k = 0; k < item.size(); k++) {
				addedSyns.add(item.get(k));
			}
		}
		
		Collection syns = synsets.values(); 
		System.out.println("# synsets: " + syns.size() + 
				" (already classified: " + addedSyns.size() + ")");
		for(Iterator i = syns.iterator(); i.hasNext();) {
			Synset syn = (Synset) i.next();
			String localName = syn.getURI().substring(syn.getURI().lastIndexOf('#') + 1);
			if(!addedSyns.contains(localName)) {
				//System.out.println("++ Adding synset to root node: " + syn );
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(syn);
				//((DefaultMutableTreeNode) root).add(newNode); //Usare se il nodo non � la root
				tree.addNode(newNode);
			}
		}
	}
	
	private void adjustTree() {
		
		System.out.println("Adjusting tree...");
		TreeModel model = tree.getModel();
		Object root = model.getRoot();
		adjustLeaf(model, root);
		
		System.out.println("Sorting tree...");
		//sort() ??
	}
	
	private void adjustLeaf(TreeModel model, Object o) {
			
		int  cc = model.getChildCount(o);
		for(int i = 0; i < cc; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) model.getChild(o, i);
			Object data = child.getUserObject();
//			if(!(data instanceof Synset)) {
//				System.out.println("@@ " + child + " " + model.isLeaf(child) +
//						" " + (data instanceof OntoClass));
//			}
			if(model.isLeaf(child) && data instanceof OntoClass) {
				//Aggiungi un nodo fittizio
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode("(empty)");
				child.add(newNode);
			} else {
				adjustLeaf(model, (Object) child);
			}
		}
	} 		
	
	
}
