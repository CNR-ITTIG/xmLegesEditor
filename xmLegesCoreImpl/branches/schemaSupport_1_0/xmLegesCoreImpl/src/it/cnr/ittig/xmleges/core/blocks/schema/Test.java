package it.cnr.ittig.xmleges.core.blocks.schema;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class Test {

	public static void main(String[] args) {
		
		//String schemaURL = "data/NIR_XSD_completo/nirstrict.xsd";	
        //String schemaURL = "data/NIR_XSD_base/nirlight.xsd";

        
		String schemaURL = "../xmLegesEditor/xsdData/NIR_XSD_base/nirlight.xsd";
		//String schemaURL = "../xmLegesEditor/xsdData/NIR_XSD_completo/nirstrict.xsd";
		//String schemaURL = "../xmLegesEditor/temp/nirstrict.xsd";
		
		
		
		SchemaRulesManagerImpl sRM = new SchemaRulesManagerImpl();
		
		
		xsdRulesManagerImpl xsdRM = new xsdRulesManagerImpl();
		xsdRM.loadRules(schemaURL);
		
		
//		System.err.println("----------------- TEST ISVALID ----------------------");
		Vector v = new Vector();		

		
//		String figlio1="num";
//		String figlio2="rubrica";
//		String figlio3="comma";
//		String figlio1="articolo";
//		String figlio2="articolo";
//		
//		String figlio1="nome";
//		String figlio2="email";
//		String figlio3="link";
		String figlio1="articolo";
		String figlio2="articolo";
		
		
		v.add(figlio1);
		v.add(figlio2);
//		v.add(figlio3);
		
////		String element="articolo";
//		String element="capo";
////		String element="persona";
//		if(xsdRM.isValid(element,v,false))
//			System.err.println(element+" is VALID with "+ figlio1+" "+figlio2);
//		else
//			System.err.println(element+" is NOT VALID with "+ figlio1+" "+figlio2);
		
//		
//		if(xsdRM.isValid("persona",v))
//			System.err.println("persona is VALID with nome email link");
//		else
//			System.err.println("person IS not VALID with nome email link");
		
		Vector vv=new Vector();
//		vv.add("num");
//		vv.add("rubrica");
//		vv.add("comma");
		
//		vv.add("meta");
//		vv.add("intestazione");
//		vv.add("formulainiziale");
//		vv.add("articolato");
//		vv.add("formulafinale");
//		vv.add("conclusione");
		
		
		vv.add("#PCDATA");
//		vv.add("h:p");
//		vv.add("h:p");
//		vv.add("h:p");

		System.out.println(xsdRM.isValid("h:p", vv));
	
//		Collection results = xsdRM.getAlternatives("h:p", vv, 0);
//		System.err.println("alternatives");
//		for(Iterator i=results.iterator(); i.hasNext(); ){
//			System.err.println(i.next().toString());
//		}
//		System.err.println("end");
		
//		String DTDdefaultContent = "";
//		try{
//			DTDdefaultContent=sRM.getDTDDefaultContent("titoloDoc");
//		}
//		catch(Exception e){
//			System.err.println("exc in getDTDDefaultContent "+e.getMessage());
//		}
		
//		System.out.println("       **DTD** DEFAULT CONTENT FOR ARTICOLO "+DTDdefaultContent);
		
		
		String defaultContent = "";
		try{
			defaultContent=sRM.getDefaultContent("meta");
		}
		catch(Exception e){
			System.err.println("exc in getDefaultContent "+e.getMessage());
		}
		
		System.out.println("DEFAULT CONTENT FOR ARTICOLO "+defaultContent);
		
	}

}