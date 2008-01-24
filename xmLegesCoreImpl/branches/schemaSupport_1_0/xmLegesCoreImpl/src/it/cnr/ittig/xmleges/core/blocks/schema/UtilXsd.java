package it.cnr.ittig.xmleges.core.blocks.schema;


import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDContentTypeCategory;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.XSDParticle.DFA.State;
import org.eclipse.xsd.XSDParticle.DFA.Transition;
import org.eclipse.xsd.impl.XSDComplexTypeDefinitionImpl;
import org.eclipse.xsd.impl.XSDDiagnosticImpl;
import org.eclipse.xsd.impl.XSDParticleImpl;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Node;


public class UtilXsd{

	protected String targetNameSpace;
	protected Map prefixToNamespace;

	protected XSDSchema schema;

	List nonTopElements;


	protected SchemaRulesManagerImpl schemaRules = null;
	protected Logger logger;


	public UtilXsd(SchemaRulesManagerImpl schemaRulesManager){	
		schemaRules = schemaRulesManager;
		logger = schemaRules.getLogger();
	}


	/**
	 * 
	 * @param schemaURL
	 */
	public void loadRules(String schemaURL){

		schema = null;
		List allElements;

		// SCHEMA PRINCIPALE

		try{
			schema = loadSchemaUsingResourceSet(schemaURL);
			if (null == schema){
				logger.debug("ERROR: Could not load a XSDSchema object!");
				return;
			}
			targetNameSpace = schema.getTargetNamespace();
			prefixToNamespace = schema.getQNamePrefixToNamespaceMap();

			logger.debug("LOGGING:   @@@@@@@@@@@@@@@@@        PrefixToNameSpace = "+prefixToNamespace.toString());


			// CERCA TUTTI GLI ELEMENTI ///////////
			allElements= schema.getElementDeclarations();

			nonTopElements = new Vector();
			// va a cercare elementi NON TOP LEVEL definiti dentro gli element 
			for (Iterator iter = allElements.iterator(); iter.hasNext(); /* no-op */){
				XSDElementDeclaration elemDecl = (XSDElementDeclaration)iter.next();
				getNonTopLevelElementsFromElements(elemDecl);
			}

			// va a cercare elementi NON TOP LEVEL nei complexType 
			List topTypes = schema.getTypeDefinitions();

			for (Iterator iter = topTypes.iterator(); iter.hasNext(); /* no-op */){
				XSDTypeDefinition typedef = (XSDTypeDefinition)iter.next();
				getNonTopLevelElementsFromTypes(typedef);
			}
			///////////////////////////////////////

			allElements.addAll(nonTopElements);

			// ora ha TUTTI gli elementi  TOP e NON TOP
			for (Iterator iter = allElements.iterator(); iter.hasNext(); /* no-op */){
				XSDElementDeclaration elemDecl = (XSDElementDeclaration)iter.next();
				createRuleForElement(elemDecl);
				createRuleForAttributes(elemDecl);
				createAlternativeContents(elemDecl);       
			}			  
		}catch(Exception e){
			logger.error(e.getMessage());
		}


		logger.debug("loaded Main schema");
		logger.debug("TARGET NAMESPACE: "+schema.getTargetNamespace());

		// IMPORT
		logger.debug("************  ANALYZING IMPORTED **************");
		String[] importList = getImportUri(schemaURL);
		for(int i=0; i<importList.length; i++){
			logger.debug(i+" ITERATE OVER :"+importList[i]);
			try{
				schema = loadSchemaUsingResourceSet(importList[i]);
				if (null == schema){
					logger.debug("ERROR: Could not load a XSDSchema object!");
				} 
			}catch(Exception e){
				logger.error(e.getMessage());
			}


			// CERCA TUTTI GLI ELEMENTI ///////////
			allElements = schema.getElementDeclarations();


			nonTopElements = new Vector();
			// va a cercare elementi NON TOP LEVEL definiti dentro gli element 
			for (Iterator iter = allElements.iterator(); iter.hasNext(); /* no-op */){
				XSDElementDeclaration elemDecl = (XSDElementDeclaration)iter.next();
				getNonTopLevelElementsFromElements(elemDecl);
			}

			// va a cercare elementi NON TOP LEVEL definiti nei complexType 
			List topTypes = schema.getTypeDefinitions();

			for (Iterator iter = topTypes.iterator(); iter.hasNext(); /* no-op */){
				XSDTypeDefinition typedef = (XSDTypeDefinition)iter.next();
				getNonTopLevelElementsFromTypes(typedef);
			}
			///////////////////////////////////////

			allElements.addAll(nonTopElements);

			for (Iterator iter = allElements.iterator(); iter.hasNext(); /* no-op */){
				XSDElementDeclaration elemDecl = (XSDElementDeclaration)iter.next();
				createRuleForElement(elemDecl);  
				createAlternativeContents(elemDecl);
				createRuleForAttributes(elemDecl); 
			}

		}

		printRules();
		printAttrRules();

		logger.debug("@@@@@@    Creating Rules DONE");           
	}




	/**
	 * * @param schemaURL
	 * @return
	 * @throws Exception
	 */
	private  XSDSchema loadSchemaUsingResourceSet(String schemaURL) throws Exception{

		System.out.println("--> Loading Schema Using Resource Set @ url "+schemaURL);

		XSDResourceFactoryImpl resourceFactory = new XSDResourceFactoryImpl();
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",resourceFactory);

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//
		//prende il path assoluto di schemaURL
		//
		File file = new File(schemaURL);
		if (file.isFile())
			schemaURL = URI.createFileURI(file.getCanonicalFile().toString()).toString();

		//Create a resource set and load the main schema file into it.
		ResourceSet resourceSet = new ResourceSetImpl();     

		//resourceSet.getURIConverter().getURIMap().put(URI.createURI("./xlink.xsd"),URI.createURI("file:/home/tommaso/schemaWorkspace/xmLegesEditor/xsdData/NIR_XSD_completo/xlink.xsd"));//,URI.createURI("./xlink.xsd"));
		//resourceSet.getURIConverter().getURIMap().put(URI.createURI("http://www.w3.org/HTML/1998/html4/"),URI.createURI("./h.xsd"));//,URI.createURI("file:/home/tommaso/schemaWorkspace/xmLegesEditor/xsdData/NIR_XSD_completo/h.xsd"));//,URI.createURI("./h.xsd"));
		//resourceSet.getURIConverter().getURIMap().put(URI.createURI("./dsp.xsd"),URI.createURI("file:/home/tommaso/schemaWorkspace/xmLegesEditor/xsdData/NIR_XSD_completo/dsp.xsd"));//,URI.createURI("./dsp.xsd"));
		//System.err.println("URIMAP: "+resourceSet.getURIConverter().getURIMap().entrySet().toString());

		resourceSet.getLoadOptions().put(XSDResourceImpl.XSD_TRACK_LOCATION, Boolean.TRUE);

		XSDResourceImpl xsdSchemaResource = null;
		try
		{
			xsdSchemaResource = (XSDResourceImpl)resourceSet.getResource(URI.createURI(schemaURL),true);
		}
		catch(Exception e)
		{
			logger.error("Exception caught in method 'load schema using ResourceSet' message = " + e.getMessage() + " xsdSchemaResource: " +
					xsdSchemaResource);  
		}


		Resource resource = (Resource)xsdSchemaResource;
		if (resource instanceof XSDResourceImpl)
		{
			XSDResourceImpl xsdResource = (XSDResourceImpl)resource;
			//Iterate over the schema's content's looking for directives.
			XSDSchema xsdSchema = xsdResource.getSchema();



			for (Iterator contents = xsdSchema.getContents().iterator(); contents.hasNext(); ){

				XSDSchemaContent xsdSchemaContent = (XSDSchemaContent)contents.next();

				if (xsdSchemaContent instanceof XSDImport)
				{
					XSDImport xsdImport = (XSDImport)xsdSchemaContent;
					xsdImport.resolveModelGroupDefinition(xsdImport.getNamespace(), "");
					xsdImport.resolveElementDeclaration(xsdImport.getNamespace(), "");
					xsdImport.resolveTypeDefinition(xsdImport.getNamespace(), "");
					xsdImport.resolveComplexTypeDefinition(xsdImport.getNamespace(), "");
				}


				if (xsdSchemaContent instanceof XSDSchemaDirective){              
					XSDSchemaDirective xsdSchemaDirective = (XSDSchemaDirective)xsdSchemaContent;
					if (xsdSchemaDirective.getResolvedSchema() == null){
						logger.debug("Unresolved schema "+ xsdSchemaDirective.getSchemaLocation() +" in " + xsdResource.getURI());

						if(xsdSchemaDirective instanceof XSDImport){
							XSDImport xsdSchemaImport = (XSDImport)xsdSchemaDirective;
							logger.debug(" IMPORT: namespace --> "+xsdSchemaImport.getNamespace());
							logger.debug(" IMPORT: location  --> "+xsdSchemaImport.getSchemaLocation());
						}
					}
					else{
						logger.debug("Resolved schema "+ xsdSchemaDirective.getSchemaLocation() +" in " + xsdResource.getURI());
						if(xsdSchemaDirective instanceof XSDImport){
							XSDImport xsdSchemaImport = (XSDImport)xsdSchemaDirective;
							logger.debug(" IMPORT: namespace --> "+xsdSchemaImport.getNamespace());
							logger.debug(" IMPORT: location  --> "+xsdSchemaImport.getSchemaLocation());
						}
					}             
				}	
			}		

			return xsdResource.getSchema();
		}
		logger.debug("loadSchemaUsingResourceSet(" + schemaURL + ") did not contain any schemas!");
		return null;
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////   ABBOZZO   DI  ALTERNATIVE CONTENTS     	///////////////////////////////////////

	/**
	 * 
	 * @param elemDecl
	 * @return
	 * @throws RulesManagerException 
	 */
	public Collection createAlternativeContents(XSDElementDeclaration elemDecl){


		Vector contents_strings=null;
		try {
			contents_strings = schemaRules.getAlternativeContents(elemDecl.getAliasName());
		} catch (RulesManagerException e) {
			// TODO Auto-generated catch block
			System.err.println("exception in createAlternativecontents..");
			e.printStackTrace();
		}
		schemaRules.alternative_contents.put(elemDecl.getQName(), contents_strings);

		return contents_strings;		

	}




	//////////////////////////////////// FINE ALTERNATIVE CONTENTS ///////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////





	/**
	 * 
	 * @param elemDecl
	 * @param dfa
	 */
	private void mergedAddRule(XSDElementDeclaration elemDecl, XSDParticle.DFA dfa){

		if(schemaRules.rules.get(elemDecl.getQName())==null){
			schemaRules.rules.put(elemDecl.getQName(), dfa);
			schemaRules.elemDeclNames.put(elemDecl.getQName(), elemDecl);
		}
		else{
			if(elemDecl.getType() instanceof XSDComplexTypeDefinition && elemDecl.getType().getComplexType()!=null){
				schemaRules.rules.put(elemDecl.getQName(), dfa);
				schemaRules.elemDeclNames.put(elemDecl.getQName(), elemDecl);
				logger.debug("--------------->  replacing rule for "+elemDecl.getQName());
			}
		}
	}



	/**
	 * 
	 * @param elemDecl
	 */
	private void createRuleForElement(XSDElementDeclaration elemDecl){

		XSDTypeDefinition typedef = elemDecl.getType();

		if (typedef instanceof XSDSimpleTypeDefinition){

			XSDParticle xsdParticle;
			if (typedef.getContainer() instanceof XSDParticle){
				xsdParticle = (XSDParticle)typedef.getContainer(); 
			} else {
				xsdParticle = XSDFactory.eINSTANCE.createXSDParticle();
			}

			XSDParticleImpl.XSDNFA dfa = (XSDParticleImpl.XSDNFA)xsdParticle.getDFA();
			if(dfa.getStates().size()==1){
				((XSDParticleImpl.XSDNFA.StateImpl)dfa.getStates().get(0)).setAccepting(true);
			}
			mergedAddRule(elemDecl, dfa);

		}
		else if (typedef instanceof XSDComplexTypeDefinition){
			if(typedef.getComplexType()!=null){
				XSDParticle.DFA dfa = (XSDParticle.DFA) typedef.getComplexType().getDFA();
				mergedAddRule(elemDecl, dfa);

			}
			else{
				typedef = schema.resolveComplexTypeDefinition(typedef.getName());

				XSDParticle xsdParticle;
				if (typedef.getContainer() instanceof XSDParticle)
				{
					xsdParticle = (XSDParticle)typedef.getContainer();

				}
				else
				{
					xsdParticle = XSDFactory.eINSTANCE.createXSDParticle();

				}

				XSDParticleImpl.XSDNFA dfa = (XSDParticleImpl.XSDNFA)xsdParticle.getDFA();
				if(dfa.getStates().size()==1){
					((XSDParticleImpl.XSDNFA.StateImpl)dfa.getStates().get(0)).setAccepting(true);
				}
				mergedAddRule(elemDecl, dfa);
			}
		}

		else{
			logger.debug("no rule for "+elemDecl.getQName());
		}   

	}


	/**
	 * 
	 * @param term
	 */
	private void getNonTopLevelElementForTerm(XSDTerm term){

		if(term instanceof XSDElementDeclaration){				 			// ELEMENT
			XSDElementDeclaration elemDecl=(XSDElementDeclaration)term;	

			if(!(((XSDConcreteComponent)elemDecl).getContainer() instanceof XSDSchema)){
				nonTopElements.add(elemDecl);
				logger.debug("found NON TOP LEVEL "+elemDecl.getQName());	
				// sull'element che trovo chiamo a sua volta il getNonTopLevel ????   (inizio ricorsione dentro ricorsione)
				logger.debug("*****************************       RE   -   RECURSE    !!!!");
				// l'elemento trovato potrebbe contenere a sua volta un anonymousType con dentro la definizione di altri elementi
				getNonTopLevelElementsFromElements(elemDecl);			
			}
			else
				logger.debug("found REF = "+elemDecl.getQName());

		}
		else if(term instanceof XSDModelGroup){                  			// CHOICE, SEQUENCE                         
			XSDModelGroup modelGroup=(XSDModelGroup)term;
			List list = modelGroup.getParticles();
			for (Iterator i = list.iterator();i.hasNext();){
				XSDParticleImpl particle=(XSDParticleImpl) i.next();
				getNonTopLevelElementForTerm(particle.getTerm());
			}	
		}else if(term instanceof XSDWildcard){
			logger.debug("XSDWildcard");
		}else{
			logger.debug("XSDTermIMpl");
		}	
	}




	// qui chiamarlo su elemDecl.getAnonymousTypeDefinition() o su elemDecl.getTypeDefinition()   ?

	/**
	 * 
	 * @param typedef
	 */
	private void getNonTopLevelElementsFromElements(XSDElementDeclaration elemDecl){
		logger.debug("---------> 		 ANALYZING elemDecl " + 	elemDecl.getQName());
		if(elemDecl.getAnonymousTypeDefinition()!=null){
			logger.debug("---------> 		 elemDecl   " + 	elemDecl.getQName()+" contains ANONYMOUSTYPES");
			getNonTopLevelElementsFromTypes(elemDecl.getAnonymousTypeDefinition());        
		}
	}



	/**
	 * 
	 * @param typedef
	 */
	private void getNonTopLevelElementsFromTypes(XSDTypeDefinition typedef){

		if (typedef instanceof XSDSimpleTypeDefinition){
			//System.err.println("SIMPLE TYPE  cannot contain element");
		}
		else if (typedef instanceof XSDComplexTypeDefinition){

			if(typedef.getComplexType()!=null){     
				XSDComplexTypeDefinitionImpl typedefimpl= (XSDComplexTypeDefinitionImpl)typedef;
				if (typedefimpl.getContentTypeCategory()==XSDContentTypeCategory.ELEMENT_ONLY_LITERAL
						|| typedefimpl.getContentTypeCategory()==XSDContentTypeCategory.MIXED_LITERAL){

					XSDTerm term = ((XSDParticleImpl)typedefimpl.getContentType()).getTerm();
					getNonTopLevelElementForTerm(term);
				}	        	    
			}
		}        
	}





	/**
	 * 
	 * @param elemDecl
	 */
	private void createRuleForAttributes(XSDElementDeclaration elemDecl){

		String attrName=""; //nome dell'attributo analizzato
		String type=""; //type dell'attributo
		String defaultValue=""; //literal dell'uso dell'attributo
		String value = ""; //lexical value dell'uso dell'attributo

		if( elemDecl.getType() instanceof XSDComplexTypeDefinition){
			List allAttributeUses = ((XSDComplexTypeDefinition)elemDecl.getType()).getAttributeUses();

			for (Iterator iterA = allAttributeUses.iterator();iterA.hasNext();/* no-op */){
				XSDAttributeUse attruse = (XSDAttributeUse)iterA.next();
				attrName="";
				type="";
				defaultValue="";
				value="";

				attrName = attruse.getAttributeDeclaration().getQName();

				XSDSimpleTypeDefinition typeDef=attruse.getAttributeDeclaration().getTypeDefinition();
				String simpleType="";//nome del tipo dell'attributo
				String baseType=""; //tipo finale dell'attributo (string, id, idref)
				if (typeDef!=null){
					if(typeDef.getSimpleType()!=null){
						simpleType=typeDef.getSimpleType().getAliasName();
					}
					if(typeDef.getBaseTypeDefinition()!=null){
						baseType=/*", cioè"+*/typeDef.getBaseTypeDefinition().getAliasName();
					}
					simpleType+=baseType;



					defaultValue=attruse.getUse().getLiteral();
					value=attruse.getAttributeDeclaration().getLexicalValue();
					// prima era: (non trova il fixed ma trova il default)
					//value = attruse.getLexicalValue()!=null?attruse.getLexicalValue():null;
					if(!defaultValue.equals("required")){
						defaultValue=attruse.getAttributeDeclaration().getConstraint().getLiteral();
						if(!defaultValue.equals("fixed")){
							defaultValue="implied";                       
						}
					}   

					List allTypeFacets = typeDef.getSimpleType().getFacetContents();
					if(allTypeFacets.size()>0){ //è una enumeration
						type="(";
						for (Iterator iterB = allTypeFacets.iterator();iterB.hasNext();/* no-op */){
							XSDConstrainingFacet restriction = (XSDConstrainingFacet)iterB.next();
							String facetName=restriction.getFacetName();
							if(facetName.equals("enumeration")){
								type+=restriction.getLexicalValue()+"|";
							}
						}
						if(!type.equals("("))
							type=type.substring(0, type.length()-1)+")";
						else
							type="";
					}

					//System.out.println(elemDecl.getQName()+" -attr: "+attrName+"["+simpleType+"], type= "+type+", defaultValue = "+defaultValue+"value = "+(value!=null?value:" null"));
					HashMap att_hash = (HashMap) schemaRules.attributes.get(elemDecl.getQName());
					if (att_hash == null) {
						att_hash = new HashMap();
						schemaRules.attributes.put(elemDecl.getQName(), att_hash);
					}
					// add a new attribute definition
					AttributeDeclaration attrDecl = new AttributeDeclaration(type,defaultValue,value);
					mergedAddAttribute(elemDecl,att_hash,attrName,attrDecl);
				}else{
					simpleType="no type --> no name";
					logger.debug("typeDef of"+attrName+" of elem "+elemDecl.getQName()+" is null");
				}
			}
		}
	}



	/**
	 * 
	 * @param elemDecl
	 * @return
	 */
	public boolean isResolvedElement(XSDElementDeclaration elemDecl){
		if(elemDecl!=null && elemDecl.getType() instanceof XSDComplexTypeDefinition && elemDecl.getType().getComplexType()!=null)
			return true;
		return false;
	}



	/**
	 * 
	 * @param att_hash
	 * @param attrName
	 * @param attrDecl
	 */
	public void mergedAddAttribute(XSDElementDeclaration elemDecl, HashMap att_hash, String attrName, AttributeDeclaration attrDecl){
		if(att_hash.get(attrName)==null){
			att_hash.put(attrName, attrDecl);
		}else{
			if(isResolvedElement(elemDecl)){
				att_hash.put(attrName, attrDecl);    
			}
		}
	}





	/**
	 * 
	 * @param elemName
	 * @return
	 */         
	private String getDFAType(String elemName){
		XSDTypeDefinition typedef = ((XSDElementDeclaration)schemaRules.elemDeclNames.get(elemName)).getType();
		if (typedef instanceof XSDSimpleTypeDefinition)
			return " SIMPLE ";
		if (typedef instanceof XSDComplexTypeDefinition){
			if (typedef.getComplexType()!=null)
				return " COMPLEX ";
			else
				return "  SPECIAL ";
		}
		return " UNDEFINED ";
	}

	
	
	/**
	 * 
	 *
	 */
	private void printRules(){
		for(Iterator it = schemaRules.rules.keySet().iterator(); it.hasNext();){
			String elemName = (String) it.next();
			System.err.println("********** AUTOMA  "+getDFAType(elemName)+" DI "+elemName+" ************");
			printDFA((XSDParticle.DFA)schemaRules.rules.get(elemName));
		}
	}


	/**
	 * 
	 * @param dfa
	 */
	protected static void printDFA(XSDParticle.DFA dfa)
	{	
		((XSDParticleImpl.XSDNFA)dfa).dump(System.err);
	}




	/**
	 * 
	 *
	 */
	public void printAttrRules(){
		for(Iterator it = schemaRules.attributes.keySet().iterator(); it.hasNext();){
			String elemName = (String) it.next();
			logger.debug("- ********** MAPPA attributi	"+/*getDFAType(attrName)+*/" DI "+elemName+" ************");
			for(Iterator it2 = ((HashMap)schemaRules.attributes.get(elemName)).keySet().iterator(); it2.hasNext();){
				String attrName = (String)it2.next();
				HashMap att_hash = ((HashMap)schemaRules.attributes.get(elemName));
				logger.debug("- ATTRIBUTE: elem= "+elemName+" name="+attrName+" "+((AttributeDeclaration)att_hash.get(attrName)).toString());
			}
		}
	}



	/**
	 * 
	 * @param schemaURL
	 * @return
	 */
	private String getFolderPath(String schemaURL){
		return schemaURL.substring(0, schemaURL.lastIndexOf("/"));
	}



	/**
	 * 
	 * @param schemaURL
	 * @return
	 */
	public String[] getImportUri(String schemaURL){

		XSDResourceFactoryImpl resourceFactory = new XSDResourceFactoryImpl();
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",resourceFactory);

		Vector ret = new Vector();

		// prende il path assoluto di schemaURL

		String folder = "";
		File file = new File(schemaURL);
		if (file.isFile()){
			try{
				schemaURL = URI.createFileURI(file.getCanonicalFile().toString()).toString();
				folder = getFolderPath(schemaURL);        
			}catch(Exception e){	
			}
		}

		System.err.println("----------------------------- folder "+folder);

		// Create a resource set and load the main schema file into it.
		ResourceSet resourceSet = new ResourceSetImpl();    
		resourceSet.getLoadOptions().put(XSDResourceImpl.XSD_TRACK_LOCATION, Boolean.TRUE);

		XSDResourceImpl xsdSchemaResource = null;
		try{
			xsdSchemaResource = (XSDResourceImpl)resourceSet.getResource(URI.createURI(schemaURL),true);
		}catch(Exception e){
			logger.error("Exception caught in method 'load schema using ResourceSet' message = " + e.getMessage() + " xsdSchemaResource: " +xsdSchemaResource);  
		}

		Resource resource = (Resource)xsdSchemaResource;
		if (resource instanceof XSDResourceImpl){

			XSDResourceImpl xsdResource = (XSDResourceImpl)resource;
			// Iterate over the schema's content's looking for directives.
			XSDSchema xsdSchema = xsdResource.getSchema();

			for (Iterator contents = xsdSchema.getContents().iterator(); contents.hasNext(); ){

				XSDSchemaContent xsdSchemaContent = (XSDSchemaContent)contents.next();

				if (xsdSchemaContent instanceof XSDSchemaDirective){              
					XSDSchemaDirective xsdSchemaDirective = (XSDSchemaDirective)xsdSchemaContent;
					if(xsdSchemaDirective instanceof XSDImport){
						XSDImport xsdSchemaImport = (XSDImport)xsdSchemaDirective;
						String location = xsdSchemaImport.getSchemaLocation().startsWith(".")?xsdSchemaImport.getSchemaLocation().substring(1):File.separator+xsdSchemaImport.getSchemaLocation();
						ret.add(folder+location);
						logger.debug(" IMPORT: namespace --> "+xsdSchemaImport.getNamespace());
						logger.debug(" IMPORT: location  --> "+xsdSchemaImport.getSchemaLocation());
					}
				}             
			}	
		}
		String[] list = new String[ret.size()];
		ret.copyInto(list);
		return list;
	}


	//
	//
	//									FINE METODI CARICAMENTO
	//
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////










	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////
	////
	//// 								METODI  di VALIDAZIONE 
	////
	////

	/**
	 * 
	 * @param elem_name
	 * @param elem_children
	 * @return
	 */
	public boolean isValid(String elem_name, Collection elem_children)  throws RulesManagerException{
		return isValid(elem_name, elem_children, false);
	}


	/**
	 * 
	 * @param elem_name
	 * @param elem_children
	 * @return
	 */
	public boolean isValid(String elem_name, Collection elem_children, boolean withgaps) throws RulesManagerException{
		// text elements must have no children
		if (elem_name.startsWith("#"))
			return (elem_children.size() == 0);
		// get automata
		XSDParticle.DFA rule = (XSDParticle.DFA) schemaRules.rules.get(elem_name);
		if (rule == null)
			throw new RulesManagerException("No rule for element <" + elem_name + ">");
		return align(elem_name,elem_children,withgaps);		
	}

	/**
	 * 
	 * @param elem_name
	 * @param elem_children
	 * @param choice_point
	 * @return
	 */
	public Collection getAlternatives(String elem_name, Collection elem_children, int choice_point) throws RulesManagerException{
		// text elements must have no children
		if (elem_name.startsWith("#"))
			return new Vector();
		// get automata
		XSDParticle.DFA rule = (XSDParticle.DFA) schemaRules.rules.get(elem_name);
		if (rule == null)
			throw new RulesManagerException("No rule for element <" + elem_name + ">");
		return alignAlternatives(elem_children, elem_name, choice_point);
	}


	/**
	 * 
	 * @param node
	 * @param attributeName
	 * @return
	 */
	public boolean assessAttribute(Node node, String attributeName){
		boolean isvalid=false;
		String diagnosticMessages="";
		XSDElementDeclaration elemDecl = (XSDElementDeclaration)schemaRules.elemDeclNames.get(node.getNodeName());       
		String toTest=UtilDom.getAttributeValueAsString(node, attributeName);
		XSDTypeDefinition typedef = elemDecl.getType ();        


		// FIXME te non servi !
		if (typedef instanceof XSDSimpleTypeDefinition){
			XSDSimpleTypeDefinition simpleTypeDef=(XSDSimpleTypeDefinition) typedef;

			System.out.println("\n ----> assess "+toTest+ " su "+attributeName+" dell'elemento: "+simpleTypeDef.getAliasName());
			if(simpleTypeDef.assess(toTest).getDiagnostics().size()==0){
				//    System.out.println(toTest+" è valido su "+simpleTypeDef.getAliasName());
				isvalid=true;
			}
			else{
				for (Iterator i = simpleTypeDef.assess(toTest).getDiagnostics().iterator(); i.hasNext(); ) {

					XSDDiagnosticImpl diagnostic = (XSDDiagnosticImpl) i.next();
					isvalid=false;
					//            System.out.println(toTest+" non valido: "+diagnostic.getMessage());
					diagnosticMessages+=diagnostic.getMessage()+"\n";

				}
			}
			return isvalid;
		}else{
			List allAttributeUses = ((XSDComplexTypeDefinition)elemDecl.getType()).getAttributeUses();

			for (Iterator iterA = allAttributeUses.iterator();iterA.hasNext();/* no-op */){
				XSDAttributeUse attruse = (XSDAttributeUse)iterA.next();

				if(attruse.getAttributeDeclaration().getQName().equals(attributeName)){
					XSDSimpleTypeDefinition simpleTypeDef=attruse.getAttributeDeclaration().getTypeDefinition();
//					XSDSimpleTypeDefinition typeDef=attruse.getAttributeDeclaration().getTypeDefinition();
//					XSDSimpleTypeDefinition simpleTypeDef=(XSDSimpleTypeDefinition) typedef;

					System.out.println("\n ----> assess "+toTest+ " su "+attributeName+" dell'elemento: "+simpleTypeDef.getAliasName());
					if(simpleTypeDef.assess(toTest).getDiagnostics().size()==0){
						System.out.println(toTest+" è valido su "+simpleTypeDef.getAliasName());
						isvalid=true;
					}
					else{
						for (Iterator i = simpleTypeDef.assess(toTest).getDiagnostics().iterator(); i.hasNext(); ) {

							XSDDiagnosticImpl diagnostic = (XSDDiagnosticImpl) i.next();
							isvalid=false;
							System.out.println(toTest+" non valido perchè : "+diagnostic.getMessage());
							diagnosticMessages+=diagnostic.getMessage()+"\n";

						}
					}
					return isvalid;

				}
			}
			System.out.println("attribute not found!");
			return false;


//			System.out.println("assess di "+ node.getNodeName()+"solo su simpletype!");
//			return false;
		}
	}


	/**
	 * 
	 * @param node
	 * @return
	 */
	public boolean assess(Node node){
		boolean isvalid=false;
		String diagnosticMessages="";
		XSDElementDeclaration elemDecl = (XSDElementDeclaration)schemaRules.elemDeclNames.get(node.getNodeName());

		String toTest=UtilDom.getTextNode(node);
		XSDTypeDefinition typedef = elemDecl.getType ();

		if (typedef instanceof XSDSimpleTypeDefinition){
			XSDSimpleTypeDefinition simpleTypeDef=(XSDSimpleTypeDefinition) typedef;

			System.out.println("\n ----> assess "+toTest+ " su "+simpleTypeDef.getAliasName());
			if(simpleTypeDef.assess(toTest).getDiagnostics().size()==0){
				//    System.out.println(toTest+" è valido su "+simpleTypeDef.getAliasName());
				isvalid=true;
			}
			else{
				for (Iterator i = simpleTypeDef.assess(toTest).getDiagnostics().iterator(); i.hasNext(); ) {

					XSDDiagnosticImpl diagnostic = (XSDDiagnosticImpl) i.next();
					isvalid=false;
					//            System.out.println(toTest+" non valido: "+diagnostic.getMessage());
					diagnosticMessages+=diagnostic.getMessage()+"\n";

				}
			}
			return isvalid;
		}else{
			System.out.println("assess solo su simpletype!");
			return false;
		}
	}


	//
	//
	//
	//
	//////////////////////////////////////////////////////////////////////////////////////////
	//
	//				METODI MANIPOLAZIONE       XSDFA
	//
	//

	/**
	 * 
	 * @param fromState
	 * @param label
	 * @return
	 */
	private XSDParticle.DFA.State getNextState(String ruleName, XSDParticle.DFA.State fromState, String label){

		if(label.equalsIgnoreCase("#PCDATA"))     
			return isMixed(ruleName)?fromState:null;       // se l'elemento ha mixed content accetta testo

		return getAcceptingTransition(fromState,label)!=null?getAcceptingTransition(fromState,label).getState():null;

	}


	/**
	 * 
	 * @param label
	 * @return
	 */
	private String getPrefix(String label){
		if(label.indexOf(":")==-1)
			return null;
		return label.substring(0,label.indexOf(":"));
	}


	/**
	 * 
	 * @param label
	 * @return
	 */
	private String getUnqualifiedName(String label){
		if(label.indexOf(":")==-1)
			return label;
		return label.substring(label.indexOf(":")+1);
	}


	/**
	 * 
	 * @param fromState
	 * @param label
	 * @return
	 */
	private XSDParticle.DFA.Transition getAcceptingTransition(XSDParticle.DFA.State fromState, String label){

		String nameSpaceUri = (String) prefixToNamespace.get(getPrefix(label));
		label = getUnqualifiedName(label);



		// gli automi di contentModel di tipo Any sono fatti   cosi    [0]---http://www.normeinrete.it/nir/2.2---->[1*]
		// per crearne il contentGraph abbiamo bisogno di visitarne tutti gli stati
		// a quellarco li' viene data l'etichetta #EPS
		// dall'arco si passa se   lo stato di partenza accetta il namespace specificato; manca l'informazione su ANY, NOT, SET (namespaceConstraintCategory)
		// che per NIR e'   "##OTHER"   corrispondente a NOT;   questa servirebbe per settare il namespaceUri giusto
		if(label.startsWith("#")){        
			nameSpaceUri = "";         // Questo nameSpace FUNZIONA PER ANY e PER NOT ; Non funziona per SET (vedi sotto, metodo allows)
		}


		// nel rulesManager DFSA  si faceva
		//		/**
		//		 * Applica la funzione di transizione ad un simbolo
		//		 * 
		//		 * @param value simbolo da cercare sugli archi del DFSA
		//		 * @return il nodo destinazione della transizione,
		//		 *         <code>null</null> se il simbolo non fa parte del
		//		 * vocabolario del nodo
		//		 */
		//		public Node getNext(String value) {
		//			if (value.compareTo("#ANY") == 0)
		//				return this;
		//			return (Node) transition_table.get(value);
		//		}


		XSDParticle.DFA.Transition accepting = fromState.accept(nameSpaceUri,label);
		return  accepting;
	}




//	public boolean allows(String namespace)
//	{
//	switch (getNamespaceConstraintCategory().getValue())
//	{
//	case XSDNamespaceConstraintCategory.ANY:
//	{
//	return true;
//	}
//	case XSDNamespaceConstraintCategory.NOT:
//	{
//	return namespace != null && !getNamespaceConstraint().contains(namespace);
//	}
//	case XSDNamespaceConstraintCategory.SET:
//	{
//	return getNamespaceConstraint().contains(namespace);
//	}
//	default:
//	{
//	return false;
//	}
//	}
//	}



	//   questo ?    E' il metodo accept di XSDParticleImpl

//	public XSDParticle.DFA.Transition accept(String namespaceURI, String localName)
//	{
//	for (Iterator transitions = getTransitions().iterator(); transitions.hasNext(); )
//	{
//	Transition transition = (Transition)transitions.next();
//	XSDParticle xsdParticle = transition.getParticle();
//	XSDTerm xsdTerm = xsdParticle.getTerm();
//	if (xsdTerm instanceof XSDElementDeclaration)
//	{
//	XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration)xsdTerm;
//	if ((namespaceURI == null ? 
//	xsdElementDeclaration.getTargetNamespace() == null : 
//	namespaceURI.equals(xsdElementDeclaration.getTargetNamespace())) &&
//	localName.equals(xsdElementDeclaration.getName()))
//	{
//	return transition;
//	}
//	}
//	else if (xsdTerm instanceof XSDWildcard)
//	{
//	XSDWildcard xsdWildcard = (XSDWildcard)xsdTerm;
//	if (xsdWildcard.allows(namespaceURI))
//	{
//	return transition;
//	}
//	}
//	}

//	return null;
//	}
//	}




	/**
	 * Restituisce l'insieme delle label degli archi uscenti da uno stato del DFA
	 * @param node
	 * @return
	 */
	private Collection getVocabulary(State node){
		Vector v=new Vector();
		List allTransitions = node.getTransitions();

		for(Iterator  it=allTransitions.iterator(); it.hasNext(); ){
			XSDParticle xsdParticle = ((Transition)(it.next())).getParticle();
			XSDTerm xsdTerm = xsdParticle.getTerm();
			if (xsdTerm instanceof XSDElementDeclaration)
			{
				XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration)xsdTerm;
				v.add(xsdElementDeclaration.getQName());
			}else if (xsdTerm instanceof XSDWildcard){
				XSDWildcard xsdWildcard = (XSDWildcard)xsdTerm;
				v.add("#EPS");   // [QUESTO RICOMPARE NEL RIGHT CLICK E NON CI PIACE] ok per la creazione del ContentGraph, meno per i tag inseribili da tasto destro

				//da dtdRulesManagerImpl:
				//  if (model == "ANY")
				//	// rule.addTransition(start,end,"#ANY");
				//	rule.addTransition(start, end, "#PCDATA");
				System.err.println("---------------------------------------------");
				System.err.println("xsdWildCard     NameSpaceConstraint: "+xsdWildcard.getStringNamespaceConstraint());
				System.err.println("xsdWildCard:    LexicalNameSpaceConstraint "+xsdWildcard.getStringLexicalNamespaceConstraint());
				System.err.println("---------------------------------------------");
			}          			
		}
		return v;
	}



	/**
	 * Allinea una sequenza di stringhe con l'automa
	 * 
	 * @param sequence la sequenza da allineare
	 * @param with_gaps <code>true</code> se l'allineamento pu&ograve essere fatto con
	 *            gaps
	 * @return <code>true</code> se la sequenza di nodi allinea con l'automa
	 * @throws RulesManagerException
	 */
	private boolean align(String ruleName, Collection sequence, boolean with_gaps) throws RulesManagerException {
		XSDParticle.DFA rule = (XSDParticle.DFA) schemaRules.rules.get(ruleName);
		if (with_gaps){
			Vector startfrom = new Vector();
			startfrom.add(rule.getInitialState());			
			return alignWithGaps(ruleName, sequence.iterator(), startfrom, rule.getStates());
		}
		return align(ruleName, sequence);
	}



	/**
	 * 
	 * @param rule
	 * @param sequence
	 * @return
	 */
	private boolean align (String ruleName, Collection sequence) throws RulesManagerException{

		XSDParticle.DFA rule = (XSDParticle.DFA) schemaRules.rules.get(ruleName);
		XSDParticle.DFA.State initialState = rule.getInitialState();
		XSDParticle.DFA.State finalState = initialState;

		for (Iterator i = sequence.iterator(); i.hasNext() && finalState != null;) {
			String childName = (String) i.next();
			finalState = getNextState(ruleName, finalState,childName);			
		}
		return (finalState != null && finalState.isAccepting());
	}




	/**
	 * Allinea una sequenza di string con l'automa, con la possibilita' di inserire dei
	 * gaps fra i token
	 * 
	 * @param nav iteratore per navigare sulla sequenza di string
	 * @param startFrom collezione di nodi da cui iniziare l'allineamento
	 * @param nodes_table Lista dei nodi indirizzati dal loro nome
	 * @return <code>true</code> se la sequenza di nodi allinea con l'automa
	 * @throws RulesManagerException
	 */
	private boolean alignWithGaps(String ruleName, Iterator nav, Vector startFrom, List nodes) throws RulesManagerException{

		if (!nav.hasNext())
			return true;
		String token = (String) nav.next();

		// cerca tutti i nodi che riconoscono il token

		Vector new_startFrom = new Vector();
		for (Iterator id = nodes.iterator(); id.hasNext();) {
			State src = (State) id.next();
			State dest = getNextState(ruleName, src,token);
			if (dest != null) {
				// verifica che il nodo attuale sia raggiungible
				// dall'insieme dei nodi di partenza
				for (Iterator is = startFrom.iterator(); is.hasNext();) {
					State start = (State) is.next();
					if (canReach(start, src)) {
						// aggiunge il nodo al nuovo insieme dei nodi di partenza
						new_startFrom.addElement(dest);
					}
				}
			}
		}

		if (new_startFrom.size() == 0)
			return false;
		return alignWithGaps(ruleName, nav, new_startFrom, nodes);
	}



	/**
	 * Allinea una sequenza di stringhe con l'automa, esplorando tutte le possibili
	 * alternative nella posizione specificata
	 * 
	 * @param sequence la sequenza da allineare
	 * @param choice_point posizione in cui enumerare le alternative (-1 indica il nodo
	 *            inizio)
	 * @return la collezione di alternative
	 * @throws RulesManagerException
	 */
	private Collection alignAlternatives(Collection sequence, String ruleName, int choice_point) throws RulesManagerException{

		XSDParticle.DFA rule = (XSDParticle.DFA) schemaRules.rules.get(ruleName);

		if (rule.getStates().size() == 0)
			System.out.println("Empty automata");

		XSDParticle.DFA.State start_node = rule.getInitialState();
		XSDParticle.DFA.State last_node = start_node;

		Object[] seq_array = sequence.toArray();

		// align until the choice point
		//verifica allineamento della sequenza di partenza sequence
		for (int i = 0; i <= choice_point && i < seq_array.length && last_node != null; i++) 
			last_node = getNextState(ruleName, last_node, (String) seq_array[i]);
		if (last_node == null){
			System.out.println("The sequence "+sequence.toString()+" does not align with "+ruleName +" ,cannot find alternatives");
			return null;
		}

		// try all the alternatives
		XSDParticle.DFA.State choice_node = last_node;
		Vector alternatives = new Vector();
		Collection vocabulary = getVocabulary(choice_node);

		for (Iterator v = vocabulary.iterator(); v.hasNext();) {
			String symbol = (String) v.next();

			// align from the choice point
			last_node = getNextState(ruleName, choice_node, symbol);
			for (int i = choice_point + 1; i < seq_array.length && last_node != null; i++)
				last_node = getNextState(ruleName, last_node, (String) seq_array[i]);

			// valid alternative
			if (last_node != null && last_node.isAccepting())
				alternatives.add(symbol);
		}
		// se l'elemento ha mixed content aggiungi testo (#PCDATA) fra le possibili alternative
		if(isMixed(ruleName))
			alternatives.add("#PCDATA");

		return alternatives;
	}




	/**
	 * Verifica che vi sia un path fra i due nodi
	 */
	private boolean canReach(State from, State to) {
		HashSet visited = new HashSet();
		Stack tovisit = new Stack();

		tovisit.push(from);
		while (!tovisit.empty()) {
			// visit the top of the stack
			State src = (State) tovisit.pop();
			visited.add(src.toString());

			// check if the final destination has been reached
			if (src == to)
				return true;

			// schedule the following nodes for a visit
			Collection dests = src.getTransitions();
			for (Iterator id = dests.iterator(); id.hasNext();) {
				Transition dest = (Transition) id.next();
				// if the next node has not been visited and is not in the stack
				// schedule it for a visit
				if (!visited.contains(dest.getState().toString()) && tovisit.search(dest) == -1) {
					tovisit.push(dest.getState());
				}
			}
		}
		return false;
	}





	/**
	 * 
	 * @param elemName
	 * @return
	 */
	//FIXME riguardare meglio; da' eccezione sui simpleType (es. h:hr) ; messo un try-catch
	private boolean isMixed(String elemName){
		XSDElementDeclaration elemDecl = (XSDElementDeclaration) schemaRules.elemDeclNames.get(elemName);
		XSDTypeDefinition elemType = elemDecl.getType();
		try{
			return((elemType.getComplexType()!=null && ((XSDComplexTypeDefinition)elemType).isMixed()));
			//		||(elemDecl.getType().getBaseType().getComplexType()!=null && ((XSDComplexTypeDefinition)elemDecl.getType().getBaseType()).isMixed()));
		}catch(Exception e){
			return false;
		}
		// eredita mixed anche dal tipo base che estende [vedi ad esempio dataDoc, numDoc] !! 
		// la seconda condizione del'or: non andare fino alla root ! (anyType)
	}



	/**
	 * 
	 * @param elemName
	 * @return
	 */
	public boolean isSimpleType(String elemName){
		XSDElementDeclaration elemDecl = (XSDElementDeclaration) schemaRules.elemDeclNames.get(elemName);
		XSDTypeDefinition elemType = elemDecl.getType();
		return (elemType instanceof XSDSimpleTypeDefinition);
	}








	/////////////////////////////////////////////////////////////////////////////////////////////
	//
	//				   METODI DI CREAZIONE E MANIPOLAZIONE DI CONTENTGRAPH
	//
	//

	/**
	 * 
	 * @param elemName
	 * @return
	 */    
	public ContentGraph createContentGraph(String elemName) {
		XSDParticle.DFA rule = (XSDParticle.DFA)schemaRules.rules.get(elemName);
		ContentGraph graph = new ContentGraph(elemName);
		ContentGraph.Node first = graph.getFirst();
		ContentGraph.Node last = graph.getLast();

		// aggiunge un nodo al grafo per ogni nodo del DFSA
		HashMap graph_nodes_table = new HashMap();
		for (Iterator i = rule.getStates().iterator(); i.hasNext();) {
			graph_nodes_table.put(((State)i.next()).toString(), graph.addNode());
		}

		// per ogni nodo del DFSA
		for (Iterator i = rule.getStates().iterator(); i.hasNext();) {
			State dfsa_node = ((State)i.next());
			String node_name = dfsa_node.toString();
			ContentGraph.Node graph_node = (ContentGraph.Node)graph_nodes_table.get(node_name);

			// aggiunge un arco per ogni transizione
			for (Iterator j = getVocabulary(dfsa_node).iterator(); j.hasNext();) {
				String dfsa_edge = (String) j.next();
				String graph_edge = dfsa_edge;
				State dfsa_destination = getNextState(elemName, dfsa_node, dfsa_edge);
				if(dfsa_destination == null){
					System.err.println("+++++++++++++++ null destination for content "+elemName);
					System.err.println("+++++++++++++++ dfsa_edge = "+dfsa_edge);
				}
				ContentGraph.Node graph_destination = (ContentGraph.Node)graph_nodes_table.get(dfsa_destination.toString());
				graph_node.addEdge(graph_edge, graph_destination);

			}

//			// FIXME    ci vuole o no ? confronto col DFSA e XSDNFA
//			if(isMixed(elemName))
//			graph_node.addEdge("#PCDATA", last);

			// se il nodo e' un nodo iniziale del DFSA aggiunge una transizione vuota
			// dal nodo iniziale del ContentGraph
			if (rule.getInitialState().equals(dfsa_node))
				first.addEdge("#EPS", graph_node);

			// se il nodo e' un nodo finale del DFSA aggiunge una transizione vuota verso il
			// nodo finale del ContentGraph
			if (dfsa_node.isAccepting()) {
				if (rule.getInitialState().equals(dfsa_node)) {
					// in questo modo si previene che esistano elementi con il contenuto vuoto
					graph_node.addEdge("#PCDATA", last);
				} else
					graph_node.addEdge("#EPS", last);
			}
		}
		return graph;
	}



}
