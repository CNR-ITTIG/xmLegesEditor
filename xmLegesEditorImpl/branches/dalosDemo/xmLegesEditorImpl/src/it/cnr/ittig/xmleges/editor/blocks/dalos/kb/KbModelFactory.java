package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;


public class KbModelFactory {

	private static OntDocumentManager odm = OntDocumentManager.getInstance(); //null;

	private static Map localDocuments = new HashMap();	
	private static Map uriToLexSeg = new HashMap(256, 0.70f);
	private static Map uriToSourceSeg = new HashMap(256, 0.70f);
	private static Map uriToSemSeg = new HashMap(256, 0.70f);
	
	public static void addDocument(String fileCode, String lang, String fileName) {
		
		String key = lang + fileCode;
		System.out.println("@@ addDocument(): " + key + " -> " + fileName);
		localDocuments.put(key, fileName);
	}
	
	public static void addSegment(String resourceUri, String fileName, String segType) {
		
		if(segType.equalsIgnoreCase(KbConf.lexicalSegmentName)) {
			uriToLexSeg.put(resourceUri, fileName);			
		} else if(segType.equalsIgnoreCase(KbConf.sourceSegmentName)) {
			uriToSourceSeg.put(resourceUri, fileName);			
		} else if(segType.equalsIgnoreCase(KbConf.semanticSegmentName)) {
			uriToSemSeg.put(resourceUri, fileName);			
		} else {
			System.err.println("addSegment() - type not found: " + segType);
		}

	}
	
//	public static void setOdm(OntDocumentManager docm) {
//		
//		odm = docm;
//	}
	
	public static OntDocumentManager getOdm() {
		
		return odm;
	}

	public static OntModel getModel(String type) {
		
		return getModel(type, "", "", null);
	}
	
	public static OntModel getModel(String type, String reasoner) {
		
		return getModel(type, reasoner, "", null);
	}
	
	public static OntModel getModel(String type, String reasoner, String lang) {
		
		return getModel(type, reasoner, lang, null);
	}
	
	public static OntModel getModel(String type, String reasoner, Synset syn) {
		
		return getModel(type, reasoner, "", syn);
	}
			
	public static OntModel getModel(String type, String reasoner, 
			String lang, Synset syn) {
		/*
		 * Ritorna un OntModel in base a varie configurazioni.
		 * type: sceglie i moduli ontologici da caricare
		 * reasoner: sceglie il reasoner da utilizzare nel modello
		 */
		
		//Remote ontologies are locally cached...
		//Aggiungere una funzione che, se on-line, scarica le ontologie remote
		//in modo da avere sempre l'ultima versione?
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
				//può servire pellet ? oppure un external reasoner?
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
			readSchema(om, KbConf.METALEVEL_PROP);
			readSchema(om, KbConf.DOMAIN_ONTO);
			readDocument(om, lang, KbConf.CONCEPTS);
			readDocument(om, lang, KbConf.IND);
			readDocument(om, lang, KbConf.INDW);
			//readDocument(om, lang, indclawFile);		
			readDocument(om, lang, KbConf.TYPES);
		}
		if(type.equalsIgnoreCase("domain")) {
			readSchema(om, KbConf.DOMAIN_ONTO);
		}
		if(type.equalsIgnoreCase("mapping")) {
			readSchema(om, KbConf.METALEVEL_ONTO);	
			//readDocument(om, lang, indclawFile);		
		}
		if(type.equalsIgnoreCase("concepts")) {
			readDocument(om, lang, KbConf.CONCEPTS);
		}
		if(type.equalsIgnoreCase("concept.mapping")) {
			readDocument(om, lang, KbConf.CONCEPTS);
			readDocument(om, lang, KbConf.TYPES);
		}
		if(type.equalsIgnoreCase("individual")) {
			readSchema(om, KbConf.METALEVEL_ONTO);
			readSchema(om, KbConf.METALEVEL_PROP);
			readDocument(om, lang, KbConf.IND);
			readDocument(om, lang, KbConf.INDW);
			readDocument(om, lang, KbConf.TYPES);
		}
		if(type.equalsIgnoreCase("source")) {
			readSchema(om, KbConf.SOURCE_SCHEMA); //Ci vuole questo metalivello??
			readDocument(om, lang, KbConf.IND);
			readDocument(om, lang, KbConf.SOURCES);			
		}
//		if(type.equalsIgnoreCase("dpinf")) {
//			readDocument(om, lang, infDpFile);
//		}
//		if(type.equalsIgnoreCase("dpextinf")) {
//			readDocument(om, lang, infDpFile);
//			readDocument(om, lang, infDpExtFile);
//		}
		if(type.equalsIgnoreCase("seg.lex")) {
			readSchema(om, KbConf.METALEVEL_ONTO);
			readSchema(om, KbConf.METALEVEL_PROP);
			
			if(uriToLexSeg != null) {
				if(syn == null) {
					System.out.println("Segmentation: synset is null!");
				}
				Object segObj = uriToLexSeg.get(syn.getURI());
				if(segObj == null) {
					System.err.println(
							"Segmentation is active, but cannot resolve "
							+ syn.getURI());
					return null;
				}
//				String segFileName = segRepository + 
//					KbConf.lexicalSegmentName + File.separatorChar +
//					segObj.toString();
				String segFileName = segObj.toString();
				//System.out.println("Segmentation: retrieving data from " + segFileName);
				readDocument(om, lang, segFileName);
			} else {						
				readDocument(om, lang, KbConf.IND);
				readDocument(om, lang, KbConf.TYPES);
			}
		}			
		
		odm.setProcessImports(true);
		odm.loadImports(om);
		
		return om;
	}
	
	private static void readSchema(OntModel om, String url) {
		
		readSchema(om, url, false);
	}
			
	private static void readSchema(OntModel om, String url, boolean useRemote) {
		
		if(useRemote) {
			URL u = null;
			try {
				u = new URL(url);			
				System.out.println("URL: " + u.toString());
				om.read(u.openStream(), null);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("#### URL unreachable! Trying to load local data...");
				String localFile = odm.doAltURLMapping(url);
				System.out.println("localFile: " + localFile);
				om.read(localFile);
			}
		} else {
			String localFile = odm.doAltURLMapping(url);
			//System.out.println("localFile: " + localFile);
			om.read(localFile);			
		}
	}
	
//	private static void readData(OntModel om, String fileStr) {
//		
//		File file = UtilFile.getFileFromTemp(fileStr);
//		om.read("file:///" + file.getAbsolutePath());
//	}

	private static void readDocument(OntModel om, String lang, String fileCode) {
		
		String key = lang + fileCode;
		Object fileName = (localDocuments.get(key));
		if(fileName == null) {
			System.err.println("readDocument() - doc not found! key: " + key);
			return;
		}
		File file = UtilFile.getFileFromTemp((String) fileName);
		om.read("file:///" + file.getAbsolutePath());
	}

}
