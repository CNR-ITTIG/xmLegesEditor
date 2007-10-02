package it.cnr.ittig.xmleges.editor.blocks.dalos.kbmanager;

public class KBConf {

	public static String LOCAL_REPOSITORY = "/home/tommaso/.MetaLeveler";
	
	public static String DOMAIN_ONTO = 
		"http://turing.ittig.cnr.it/jwn/ontologies/consumer-law.owl";
	public static String METALEVEL_ONTO =
		"http://turing.ittig.cnr.it/jwn/ontologies/owns.owl";
	public static String METALEVEL_PROP =
		"http://turing.ittig.cnr.it/jwn/ontologies/owns-full.owl";
	
	public static String DOMAIN_ONTO_NS = KBConf.DOMAIN_ONTO + "#";		
	public static String METALEVEL_ONTO_NS = KBConf.METALEVEL_ONTO + "#";	
	public static String METALEVEL_PROP_NS = KBConf.METALEVEL_PROP + "#";

	public static String ROOT_CLASS = "http://www.w3.org/2002/07/owl#Thing";
	
}
