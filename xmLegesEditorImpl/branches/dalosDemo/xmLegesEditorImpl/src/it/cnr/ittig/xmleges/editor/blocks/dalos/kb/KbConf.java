package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

public class KbConf {

	public static String dalosRepository = "dalos/";
	
	public static String DOMAIN_ONTO = 
		"http://turing.ittig.cnr.it/jwn/ontologies/consumer-law-merge.owl";
	public static String METALEVEL_ONTO =
		"http://turing.ittig.cnr.it/jwn/ontologies/owns.owl";
	public static String METALEVEL_PROP =
		"http://turing.ittig.cnr.it/jwn/ontologies/owns-full.owl";
	public static String SOURCE_SCHEMA = 
		"http://turing.ittig.cnr.it/jwn/ontologies/metasources.owl";
	public static String METALEVEL_FULL =
		"http://turing.ittig.cnr.it/jwn/ontologies/language-properties-full.owl";
	
	public static String DOMAIN_ONTO_NS = "http://turing.ittig.cnr.it/jwn/ontologies/consumer-law.owl#";
	public static String METALEVEL_ONTO_NS = KbConf.METALEVEL_ONTO + "#";	
	public static String METALEVEL_PROP_NS = KbConf.METALEVEL_PROP + "#";
	public static String SOURCESCHEMA_NS = KbConf.SOURCE_SCHEMA + "#";

	public static String ROOT_CLASS = "http://www.w3.org/2002/07/owl#Thing";
	
	public static String IND = "individuals.owl";
	public static String INDW = "individuals-word.owl";
	public static String INDC = "ind-to-consumer.owl";
	public static String TYPES = "types.owl";
	public static String CONCEPTS = "concepts.owl";
	public static String SOURCES = "sources.owl";
	
	public static String LOCAL_DOMAIN_ONTO =  "consumer-law.owl";
	public static String LOCAL_METALEVEL_ONTO = "owns.owl";
	public static String LOCAL_METALEVEL_PROP = "owns-full.owl";
	public static String LOCAL_SOURCE_SCHEMA = "metasources.owl";
	public static String LOCAL_METALEVEL_FULL = "language-properties-full.owl";
	
}
