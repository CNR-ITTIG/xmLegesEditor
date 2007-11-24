package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
	
	public OntModel om;
	
	public Set allowedNS;
	
	public SPEngine(OntModel om) {
		
		this.om = om;
		allowedNS = new HashSet();
	}

	public void addNS(String ns) {
		
		allowedNS.add(ns);
	}
	
	public void compute(String type) {
		
		Collection data = null;
		
		if(type.equalsIgnoreCase("dp")) {
			data = computeDP();
		} else {
			return;
		}
		
		save(type, data);
	}
	
	private void save(String type, Collection data) {
		
		if(type.equalsIgnoreCase("dp")) {
			
			ModelMaker maker = ModelFactory.createMemModelMaker();
			OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_MEM );
			spec.setImportModelMaker(maker);
			OntModel om = ModelFactory.createOntologyModel(spec, null);

			for(Iterator i = data.iterator(); i.hasNext();) {
				Vector item = (Vector) i.next();
				OntResource source = (OntResource) item.get(0);
				OntProperty prop = (OntProperty) item.get(1);
				OntResource dest = (OntResource) item.get(2);
				om.add(source, prop, dest);
			}
			
			String fileStr = KbConf.dalosRepository + "IT/inferences.owl";
			File file = UtilFile.getFileFromTemp(fileStr);

			write(om, file.getAbsolutePath());
			
		} else {
			return;
		}
		
	}
	
	private Collection computeDP() {
		/*
		 * Calculate inferences about declared properties.
		 */
		
		Collection results = new HashSet();
		OntClass synClass = om.getOntClass(KbConf.METALEVEL_ONTO_NS + "Synset");
		
		//for(Iterator i = om.listIndividuals(); i.hasNext();) {
		for(Iterator i = synClass.listInstances(false); i.hasNext();) {
			OntResource ores = (OntResource) i.next();
			if(ores.isAnon()) {
				continue;
			}
			if(allowedNS.size() > 0 &&
					!allowedNS.contains(ores.getNameSpace()) ) {
				continue;
			}
			
			results.addAll(singleDP(ores));
			
			//Fermati raggiunto un certo numero di risultati:
			if(results.size() > 10) {
				break;
			}
		}
		
		return results;
	}
	
	private Collection singleDP(OntResource ores) {
		
		Collection singleResults = new HashSet();
		
		Individual ind = om.getIndividual(ores.getURI());
		System.out.println("@@@@@@@@@@@@@@@ Analyzing " + ind + " semantics...");
		for(Iterator i = ind.listRDFTypes(false); i.hasNext();) {
			Resource res = (Resource) i.next();
			if(res.isAnon()) {
				continue;
			}
			if(res.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
				System.out.println("@ RDF TYPE: " + res);
				OntClass oc = (OntClass) res.as(OntClass.class);
				System.out.println("@@ ONTCLASS: " + oc);
				for(Iterator p = oc.listDeclaredProperties(false); p.hasNext();) {
					OntProperty op = (OntProperty) p.next();
					if(op.isDatatypeProperty() || !op.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
						continue;
					}
					System.out.println("@@@ PROP: " + op);
					for(Iterator r = op.listRange(); r.hasNext();) {
						OntClass range = (OntClass) r.next();
						if(range.isAnon() || 
							range.toString().equalsIgnoreCase(
									"http://www.w3.org/2002/07/owl#Thing") ||
							range.toString().equalsIgnoreCase(
									"http://www.w3.org/2000/01/rdf-schema#Resource") ||
							!range.getNameSpace().equalsIgnoreCase(KbConf.DOMAIN_ONTO_NS)) {
							continue;
						}
						System.out.println("@@@@ RANGE: " + range);
						for(Iterator ist = range.listInstances(false); ist.hasNext();) {
							Resource obj = (Resource) ist.next();
							System.out.println("@@@@@ OBJECT: " + obj);
							Vector result = new Vector(3);
							result.add(ores);
							result.add(op);
							result.add(obj);
							singleResults.add(result);
						}
					}
				}
			}
		}
		
		return singleResults;
	}
	
	private static void write(OntModel om, String outputFile) {
		
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
