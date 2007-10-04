package it.cnr.ittig.xmleges.editor.blocks.dalos.kb;

public class KbConf {

	public static String dalosRepository = "dalos/";
	
	public static String DOMAIN_ONTO = 
		"http://turing.ittig.cnr.it/jwn/ontologies/consumer-law.owl";
	public static String METALEVEL_ONTO =
		"http://turing.ittig.cnr.it/jwn/ontologies/owns.owl";
	public static String METALEVEL_PROP =
		"http://turing.ittig.cnr.it/jwn/ontologies/owns-full.owl";
	public static String SOURCE_SCHEMA = 
		"http://turing.ittig.cnr.it/jwn/ontologies/metasources.owl";
	
	public static String DOMAIN_ONTO_NS = KbConf.DOMAIN_ONTO + "#";		
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
	
}
