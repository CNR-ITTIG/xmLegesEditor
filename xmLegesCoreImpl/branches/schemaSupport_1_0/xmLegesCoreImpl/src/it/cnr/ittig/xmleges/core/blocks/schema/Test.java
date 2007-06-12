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
		
		
		
		
		//SchemaRulesManagerImpl sRM = new SchemaRulesManagerImpl();
		
		
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
		
		//Vector vv=new Vector();
//		vv.add("num");
//		vv.add("rubrica");
//		vv.add("comma");
		
//		vv.add("meta");
//		vv.add("intestazione");
//		vv.add("formulainiziale");
//		vv.add("articolato");
//		vv.add("formulafinale");
//		vv.add("conclusione");
		
		Vector vv=new Vector();
		vv.add("#PCDATA");
////		vv.add("h:p");
////		vv.add("h:p");
////		vv.add("h:p");
//
		System.out.println(xsdRM.isValid("h:p", vv));
		
		//vv.add("articolo");

		
//		Vector vv=new Vector();
//		vv.add("num");
//		vv.add("rubrica");
//		vv.add("comma");
//		
//		Collection results = xsdRM.getAlternatives("articolo", vv, 2);
//		System.err.println("alternatives");
//		for(Iterator i=results.iterator(); i.hasNext(); ){
//			System.err.println(i.next().toString());
//		}
		
		
//		deve venire 
//		
//		rif
//		ndr
//		#PCDATA
//		mrif
//		mod
		
		
//      con schema manca #PCDATA

		

		
		// default CONTENT
		
//		String defaultContent = "";
//		try{
//			defaultContent=sRM.getDefaultContent("capo");
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			System.err.println("exc in getDefaultContent "+e.getStackTrace());
//		}
//		
//		System.out.println("DEFAULT CONTENT FOR ARTICOLO "+defaultContent);
//		
	}

}