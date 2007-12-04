package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * The Semantic Proximity Engine.
 * 
 * @author lorenzo
 *
 */
public class SPEngine {
	
	private String lang;	
	
	private OntModel om;
	
	private OntModel dpModel;
	private OntModel dpExtModel;
	private OntModel resModel;
	private OntModel resExtModel;
	
	private Set allowedNS;
	
	private String infRepo;
	
//	private Map directTypes;
//	private Map indirectTypes;
	private Map directResources;
	private Map indirectResources;
	
	private Collection dpInf;
	private Collection dpExtInf;
	private Collection resInf;
	private Collection resExtInf;
	
	public SPEngine(OntModel om, String lang) {
		
		this.om = om;
		this.lang = lang;
		allowedNS = new HashSet();
		allowedNS.add(KbConf.DOMAIN_ONTO_NS);
		
		infRepo = KbConf.dalosRepository + lang + 
				File.separatorChar + KbConf.inferenceDir;
		
		initModels();

		initData();
	}
	
	private void initModels() {
		
		
		
		ModelMaker maker = ModelFactory.createMemModelMaker();
		OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_MEM );
		spec.setImportModelMaker(maker);
		dpModel = ModelFactory.createOntologyModel(spec, null);
		dpExtModel = ModelFactory.createOntologyModel(spec, null);
		resModel = ModelFactory.createOntologyModel(spec, null);
		resExtModel = ModelFactory.createOntologyModel(spec, null);
	}

	public void addNS(String ns) {
		
		allowedNS.add(ns);
	}
	
	public void compute(String type) {
		
		Collection data = null;
		
		if(type.equalsIgnoreCase("dp")) {
			computeDP();
		} else {
			return;
		}
		
		save(type);
	}
	
	private void initData() {
		
		dpInf = new HashSet(256);
		dpExtInf = new HashSet(256);
		resInf = new HashSet(256);
		resExtInf = new HashSet(256);
		
//		directTypes = new HashMap(1000);
//		indirectTypes = new HashMap(1000);
//		directResources = new HashMap(100);
//		indirectResources = new HashMap(100);
		
		//computeData();
	}
	
	private void computeData() {
		
		System.out.println("Init maps...");		

		OntClass synClass = om.getOntClass(KbConf.METALEVEL_ONTO_NS + "Synset");
		
		for(Iterator i = synClass.listInstances(false); i.hasNext();) {
			OntResource ores = (OntResource) i.next();
			if(ores.isAnon()) {
				continue;
			}
			System.out.println("> " + ores.getLocalName() + "...");
			for(Iterator t = ores.listRDFTypes(false); t.hasNext();) {
				Resource res = (Resource) t.next();
				OntClass oc = (OntClass) res.as(OntClass.class);
				if(!oc.isAnon() && 
						!allowedNS.contains(oc.getNameSpace())) {
					continue;
				}
				if(ores.hasRDFType(oc, true)) {
					//addDirectType(ores, oc);
					addDirectResource(ores, oc);
				} else {
					//addIndirectType(ores, oc);
					addIndirectResource(ores, oc);
				}
			}
		}
		
		System.out.println("...maps initialized.");
	}
	
//	private void addDirectType(OntResource ores, OntClass oc) {
//		
//		Object types = directTypes.get(ores);
//		
//		if(types == null) {
//			types = new HashSet();
//			directTypes.put(ores, types);
//		} 
//
//		((Set) types).add(oc);
//	}
//	
//	private void addIndirectType(OntResource ores, OntClass oc) {
//		
//		Object types = indirectTypes.get(ores);
//		
//		if(types == null) {
//			types = new HashSet();
//			indirectTypes.put(ores, types);
//		} 
//
//		((Set) types).add(oc);
//	}
	
	private void addDirectResource(OntResource ores, OntClass oc) {
		
		Object resources = directResources.get(oc);
		
		if(resources == null) {
			resources = new HashSet();
			directResources.put(oc, resources);
		} 

		((Set) resources).add(ores);
	}
	
	private void addIndirectResource(OntResource ores, OntClass oc) {
		
		Object resources = indirectResources.get(oc);
		
		if(resources == null) {
			resources = new HashSet();
			indirectResources.put(oc, resources);
		} 

		((Set) resources).add(ores);
	}
	
	private void computeDPclasses() {
		
		/*
		 * Calculate inferences about declared properties.
		 */
		
		for(Iterator i = om.listClasses(); i.hasNext();) {
			OntClass oc = (OntClass) i.next();
			if(!oc.isAnon() && 
					!allowedNS.contains(oc.getNameSpace())) {
				continue;
			}
			singleDPclass(oc);
		}		
	}
	
	private void singleDPclass(OntClass oc) {
		
		//Collection singleResults = new HashSet();
		Set props = new HashSet();
		
		System.out.println("@ Analyzing ONTCLASS: " + oc.getLocalName());
		for(Iterator p = oc.listDeclaredProperties(true); p.hasNext();) {
			OntProperty op = (OntProperty) p.next();
			System.out.println("op: " + op);
			if(op.isDatatypeProperty() || 
					!allowedNS.contains(op.getNameSpace())) {
				continue;
			}
			
			props.add(op);
			System.out.println("@@@ Direct Declared PROPERTY: " + op.getLocalName());
			
			for(Iterator r = op.listRange(); r.hasNext();) {
				OntClass range = (OntClass) r.next();
				if(!range.isAnon() &&
						!allowedNS.contains(range.getNameSpace())) {
					continue;
				}					
				
				System.out.println("@@@@ RANGE: " + range);
				addDpLink(oc, range, op);
			}
				
		}
		
		for(Iterator p = oc.listDeclaredProperties(false); p.hasNext();) {
			OntProperty op = (OntProperty) p.next();
			System.out.println("op: " + op);
			if(op.isDatatypeProperty() || 
					!allowedNS.contains(op.getNameSpace())) {
				continue;
			}
			
			if(props.contains(op)) {
				continue;
			}
			System.out.println("@@@ Indirect Declared PROPERTY: " + op.getLocalName());
			
			for(Iterator r = op.listRange(); r.hasNext();) {
				OntClass range = (OntClass) r.next();
				if(!range.isAnon() &&
						!allowedNS.contains(range.getNameSpace())) {
					continue;
				}					
				
				System.out.println("@@@@ RANGE: " + range);
				addDpExtLink(oc, range, op);
			}
		}
	}
	
	private void addDpLink(OntClass d, OntClass r, OntProperty op) {
		/*
		 * Data una classe dominio e una classe range,
		 * collega le risorse e genera le inferenze.
		 * 
		 * Differenziare tra dirette/indirette nel range?
		 */
		
		Set dirResD = (Set) directResources.get(d);
		Set indirResD = (Set) indirectResources.get(d);
		
		Set rangeRes = (Set) directResources.get(r);
		Set indirResR = (Set) indirectResources.get(r);
		rangeRes.addAll(indirResR);
		
		for(Iterator i = dirResD.iterator(); i.hasNext();) {
			OntResource ores = (OntResource) i.next();
			for(Iterator k = rangeRes.iterator(); k.hasNext();) {
				OntResource obj = (OntResource) k.next();
				addSingleResult(dpInf, ores, obj, op);
			}
		}
		
		for(Iterator i = indirResD.iterator(); i.hasNext();) {
			OntResource ores = (OntResource) i.next();
			for(Iterator k = rangeRes.iterator(); k.hasNext();) {
				OntResource obj = (OntResource) k.next();
				addSingleResult(dpExtInf, ores, obj, op);
			}
		}		
	}
	
	private void addDpExtLink(OntClass d, OntClass r, OntProperty op) {
		
		Set domainRes = (Set) directResources.get(d);
		Set indirResD = (Set) indirectResources.get(d);
		domainRes.addAll(indirResD);
		Set rangeRes = (Set) directResources.get(r);
		Set indirResR = (Set) indirectResources.get(r);
		rangeRes.addAll(indirResR);
		
		for(Iterator i = domainRes.iterator(); i.hasNext();) {
			OntResource ores = (OntResource) i.next();
			for(Iterator k = rangeRes.iterator(); k.hasNext();) {
				OntResource obj = (OntResource) k.next();
				addSingleResult(dpInf, ores, obj, op);
			}
		}		
	}
	
	private void addSingleResult(Collection results, 
			OntResource a, OntResource b, OntProperty p) {
		
		if(results == null || a == null || b == null || p == null) {
			System.err.println("## ERROR ## addSingleResult() - null value!");
			return;
		}
		
		Vector v = new Vector(3);
		v.add(a);
		v.add(p);
		v.add(b);
		results.add(v);
	}
	
	private void computeDP() {
	/*
	 * Calculate inferences about declared properties.
	 */
	
		OntClass synClass = om.getOntClass(KbConf.METALEVEL_ONTO_NS + "Synset");
		
		final int writer_size = 100;
		int writer_counter = 1;
		int resources_counter = 0;
		
		for(Iterator i = synClass.listInstances(false); i.hasNext();) {
			OntResource ores = (OntResource) i.next();
			if(ores.isAnon()) {
				continue;
			}
//			if(!allowedNS.contains(ores.getNameSpace())) {
//				continue;
//			}
			
			resources_counter++;			
			singleDP(ores);
			
			//Salva ogni volta che si supera un multiplo di 'writer_size'
			if( dpExtInf.size() > (writer_counter * writer_size) ) {
				writer_counter++;
				System.out.println(">>>>>>>>>> WRITING <<<<<<<<<<");
				System.out.println(">>>>>>>>>> Actual size: " + dpExtInf.size());
				System.out.println(">>>>>>>>>> Processed resources: " + resources_counter);
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				save("dp");
			}
		}
		
	}

	private void singleDP(OntResource ores) {
		
		Individual ind = om.getIndividual(ores.getURI());
		System.out.println("@@@@@@@@@@@@@@@ Analyzing " + ind + " semantics...");
		for(Iterator i = ind.listRDFTypes(false); i.hasNext();) {
			Resource res = (Resource) i.next();
			if(res.isAnon() ||
					!allowedNS.contains(res.getNameSpace())) {
				continue;
			}
			System.out.println("@ RDF TYPE: " + res);
			OntClass oc = (OntClass) res.as(OntClass.class);
			System.out.println("@@ ONTCLASS: " + oc);
			boolean directClass = false;
			if(ores.hasRDFType(oc, true)) {
				directClass = true;
			}
			Set props = new HashSet();
			for(Iterator p = oc.listDeclaredProperties(true); p.hasNext();) {
				OntProperty op = (OntProperty) p.next();
				if(op.isDatatypeProperty() || 
						!allowedNS.contains(op.getNameSpace())) {
					continue;
				}
				props.add(op);
				System.out.println("@@@ Direct Property: " + op);
				for(Iterator r = op.listRange(); r.hasNext();) {
					OntClass range = (OntClass) r.next();
					if(!range.isAnon() && 
						!allowedNS.contains(range.getNameSpace())) {
						continue;
					}
					System.out.println("@@@@ RANGE: " + range);
					for(Iterator ist = range.listInstances(false); ist.hasNext();) {
						OntResource obj = (OntResource) ist.next();
						System.out.println("@@@@@ OBJECT: " + obj);
						if(directClass) {
							addSingleResult(dpInf, ores, obj, op);
						} else {
							addSingleResult(dpExtInf, ores, obj, op);
						}
					}
				}
			}
			
			for(Iterator p = oc.listDeclaredProperties(false); p.hasNext();) {
				OntProperty op = (OntProperty) p.next();
				if(op.isDatatypeProperty() || 
						!allowedNS.contains(op.getNameSpace())) {
					continue;
				}
				if(props.contains(op)) {
					continue;
				}
				System.out.println("@@@ Indirect Property: " + op);
				for(Iterator r = op.listRange(); r.hasNext();) {
					OntClass range = (OntClass) r.next();
					if(!range.isAnon() && 
						!allowedNS.contains(range.getNameSpace())) {
						continue;
					}
					System.out.println("@@@@ RANGE: " + range);
					for(Iterator ist = range.listInstances(false); ist.hasNext();) {
						OntResource obj = (OntResource) ist.next();
						System.out.println("@@@@@ OBJECT: " + obj);
						addSingleResult(dpExtInf, ores, obj, op);
					}
				}
			}
		}
	}
	
	private void save(String type) {
		
		if(type.equalsIgnoreCase("dp")) {
			
			for(Iterator i = dpInf.iterator(); i.hasNext();) {
				Vector item = (Vector) i.next();
				OntResource source = (OntResource) item.get(0);
				OntProperty prop = (OntProperty) item.get(1);
				OntResource dest = (OntResource) item.get(2);
				dpModel.add(source, prop, dest);
			}						
						
			checkAndWrite(dpModel, KbConf.DP_INF);

			for(Iterator i = dpExtInf.iterator(); i.hasNext();) {
				Vector item = (Vector) i.next();
				OntResource source = (OntResource) item.get(0);
				OntProperty prop = (OntProperty) item.get(1);
				OntResource dest = (OntResource) item.get(2);
				dpExtModel.add(source, prop, dest);
			}						
						
			checkAndWrite(dpExtModel, KbConf.DPEXT_INF);

			flushCollections();
			
		} else {
			return;
		}
		
	}
	
	private void flushCollections() {
		
		dpInf.clear();
		dpExtInf.clear();
	}
	
	private void checkAndWrite(OntModel om, String outName) {
		
		String tempFileName = infRepo + File.separatorChar + outName;
		
		try {
			
			UtilFile.createDirInTemp(infRepo);
			if(!UtilFile.fileExistInTemp(tempFileName)) {
				UtilFile.createTemp(tempFileName);
			}

			File outFile = UtilFile.getFileFromTemp(tempFileName);
			write(om, outFile.getAbsolutePath());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void write(OntModel om, String outputFile) {
		
		System.out.println("Saving some inference in " + outputFile + "...");

		RDFWriter writer = om.getWriter("RDF/XML-ABBREV"); //faster than RDF/XML-ABBREV
		
		//RDFWriter configuration
		
		//More info about properties and error handler (RDFWriter/RDFReader) at:
		//http://jena.sourceforge.net/IO/iohowto.html
		//http://jena.sourceforge.net/javadoc/com/hp/hpl/jena/xmloutput/RDFXMLWriterI.html
		
		//Get relative file name and use it as base...
		//String relativeOutputFileName = "file:consumer-law-classes.owl";
		String base = "http://localhost/dalos/IT/inferences.owl";
		
		//set base property
		//writer.setProperty("xmlbase", relativeOutputFileName);
		writer.setProperty("xmlbase", base);
		
		try {
			OutputStream out = new FileOutputStream(outputFile);
			//Write down the BASE model only (don't follow imports...)
			writer.write(om.getBaseModel(), out, base);
			out.close();
		} catch(Exception e) {
			System.err.println("Exception serializing model:" + e.getMessage());
			e.printStackTrace();
		}
	}

}
