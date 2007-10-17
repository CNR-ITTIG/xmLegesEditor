package it.cnr.ittig.xmleges.core.blocks.schema;

import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;

public class Test {

	public static void main(String[] args) {
		
		//String schemaURL = "data/NIR_XSD_completo/nirstrict.xsd";	
        //String schemaURL = "data/NIR_XSD_base/nirlight.xsd";

		//String schemaURL = "../xmLegesEditor/xsdData/AkomaNtoso/akomantoso10.xsd";
		//String schemaURL = "../xmLegesEditor/xsdData/NIR_XSD_base/nirlight.xsd";
		//String schemaURL = "../xmLegesEditor/xsdData/NIR_XSD_base/purchaseOrderSchema.xsd";
		//String schemaURL = "../xmLegesEditor/xsdData/NIR_XSD_completo/nirstrict.xsd";
		//String schemaURL = "../xmLegesEditor/xsdData/MetaLex/metalex.xsd";
		//String schemaURL = "../xmLegesEditor/xsdData/MetaLexCEN/metalex.xsd";
		//String schemaURL = "../xmLegesEditor/xsdData/MetaLexLatest/metalex.xsd";
		//String schemaURL = "../xmLegesEditor/xsdData/NIR_XSD_completo/meta.xsd";
		//String schemaURL = "../xmLegesEditor/xsdData/NIR_XSD_completo/h.xsd";
		
		
		//String schemaURL = "../xmLegesEditor/xsdData/csfiML/Strada.xsd";
		
		String schemaURL = "../xmLegesEditor/xsdData/restriction/restriction.xsd";
		
		
		
		
		SchemaRulesManagerImpl sRM = new SchemaRulesManagerImpl();
		sRM.loadRules(schemaURL);

		try {
			sRM.getAlternativeContents("dsp:comunicazione");
		} catch (DtdRulesManagerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//FIXME: cnr:meta si pianta!!
		//FIXME: dsp:comunicazione non ha tutte le soluz
	}

}