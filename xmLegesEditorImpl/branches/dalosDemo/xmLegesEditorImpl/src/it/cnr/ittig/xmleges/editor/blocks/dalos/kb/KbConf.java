package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

import java.io.File;

public class KbConf {

	public static String dalosRepository = "dalos" + File.separatorChar;
	
	public static String mapSegmentFileName = "segmap.dat";
	public static String segmentDirName = "segment";
	public static String lexicalSegmentName = "lexical";
	public static String sourceSegmentName = "source";
	public static String semanticSegmentName = "semantic";
		
	public static String DOMAIN_ONTO = 
		"http://turing.ittig.cnr.it/jwn/ontologies/consumer-law.owl";
	public static String METALEVEL_ONTO =
		"http://turing.ittig.cnr.it/jwn/ontologies/owns.owl";
	public static String METALEVEL_PROP =
		"http://turing.ittig.cnr.it/jwn/ontologies/owns-full.owl";
	public static String SOURCE_SCHEMA = 
		"http://turing.ittig.cnr.it/jwn/ontologies/metasources.owl";
	public static String METALEVEL_FULL =
		"http://turing.ittig.cnr.it/jwn/ontologies/language-properties-full.owl";
	public static String CONCEPT_SCHEMA = 
		"http://turing.ittig.cnr.it/jwn/ontologies/metaconcepts.owl";
	
	public static String DOMAIN_ONTO_NS = "http://turing.ittig.cnr.it/jwn/ontologies/consumer-law.owl#";
	public static String METALEVEL_ONTO_NS = KbConf.METALEVEL_ONTO + "#";	
	public static String METALEVEL_PROP_NS = KbConf.METALEVEL_PROP + "#";
	public static String SOURCESCHEMA_NS = KbConf.SOURCE_SCHEMA + "#";
	public static String CONCEPTSCHEMA_NS = KbConf.CONCEPT_SCHEMA + "#";

	public static String ROOT_CLASS = "http://www.w3.org/2002/07/owl#Thing";
	
	//Specific language
	public static String IND = "individuals.owl";
	public static String INDW = "individuals-word.owl";
	//public static String TYPES = "types.owl";
	public static String LEXICALIZATIONS = "lexicalizations.owl";
	public static String SOURCES = "sources.owl";
	public static String INTERCONCEPTS = "interconcepts.owl";
	
	//Common
	//public static String CONCEPTS = "concepts.owl";
	public static String LINKS = "links.owl";
	public static String LOCAL_DOMAIN_ONTO = "consumer-law.owl";
	public static String LOCAL_METALEVEL_ONTO = "owns.owl";
	public static String LOCAL_METALEVEL_PROP = "owns-full.owl";
	public static String LOCAL_SOURCE_SCHEMA = "metasources.owl";
	public static String LOCAL_METALEVEL_FULL = "language-properties-full.owl";
	
	public static String DALOS_NS = "http://localhost/dalos/";
	
	public static String DALOS_LINKS_NS = DALOS_NS + "concepts.owl#";

	public static String SYNSET_CLASS = METALEVEL_ONTO_NS + "Synset";
	//public static String conceptClassName = "Concept";
	
	//New
	public static String CONCEPT_CLASS = CONCEPTSCHEMA_NS + "Concept";
	public static String LEXICALIZATION_PROP = CONCEPTSCHEMA_NS + "hasLexicalization";
	public static String SOURCE_PROP = SOURCESCHEMA_NS + "source";
	public static String SYNID_PROP = METALEVEL_ONTO_NS + "synsetId";
	public static String GLOSS_PROP = METALEVEL_ONTO_NS + "gloss";
	public static String WORDSENSE_PROP = METALEVEL_ONTO_NS + "containsWordSense";
	public static String WORD_PROP = METALEVEL_ONTO_NS + "word";
	public static String LEXFORM_PROP = METALEVEL_ONTO_NS + "lexicalForm";
	public static String PROTOFORM_PROP = METALEVEL_ONTO_NS + "protoForm";
	
	public static String NOUN_CLASS = METALEVEL_ONTO_NS + "NounSynset";
	public static String VERB_CLASS = METALEVEL_ONTO_NS + "VerbSynset";
	public static String ADVERB_CLASS = METALEVEL_ONTO_NS + "AdverbSynset";
	public static String ADJECTIVE_CLASS = METALEVEL_ONTO_NS + "AdjectiveSynset";
	
	//Partial Matching relations
	public static String MATCH_EQ = "equivalent";
	public static String MATCH_NARROW = "narrow";
	public static String MATCH_BROADER = "broader";
	public static String MATCH_FUZZY = "related to";
	public static String MATCH_COHYPO = "co-hyponym";
	public static String MATCH_EQSYN = "eq-synonym";
}
