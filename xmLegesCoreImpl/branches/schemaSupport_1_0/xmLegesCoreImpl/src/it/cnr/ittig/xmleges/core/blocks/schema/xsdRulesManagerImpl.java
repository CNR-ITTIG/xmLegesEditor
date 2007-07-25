package it.cnr.ittig.xmleges.core.blocks.schema;


import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDImport;
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
import org.eclipse.xsd.impl.XSDParticleImpl;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;


public class xsdRulesManagerImpl{

	
	public HashMap rules;
	protected HashMap elemDeclNames;
	protected HashMap alternative_contents;
	public HashMap attributes;
	protected String targetNameSpace;
	protected XSDSchema schema;
	
	
	public xsdRulesManagerImpl(){		
		rules = new HashMap();
		elemDeclNames = new HashMap();
		alternative_contents = new HashMap();
		attributes = new HashMap();
	}

		
	/**
	 * 
	 * @param schemaURL
	 */
	public void loadRules(String schemaURL){	
        schema = null;
        
        // SCHEMA PRINCIPALE
        
        try{
 		   schema = loadSchemaUsingResourceSet(schemaURL);
 		   if (null == schema){
 	            System.err.println("ERROR: Could not load a XSDSchema object!");
 	            return;
 	       }
 		   
 		  List allElements = schema.getElementDeclarations();
 		  for (Iterator iter = allElements.iterator(); iter.hasNext(); /* no-op */){
 	            XSDElementDeclaration elemDecl = (XSDElementDeclaration)iter.next();
 	            createRuleForElement(elemDecl);
 	            createAlternativeContents(elemDecl);
 	            createRuleForAttributes(elemDecl);
 	            
 	      }
 		}catch(Exception e){
 			System.err.println("exception");
 		}
        
 		
 		targetNameSpace = schema.getTargetNamespace();
		
		System.err.println("loaded schema");
		System.err.println("TARGET NAMESPACE: "+schema.getTargetNamespace());
 		
 		//listElements(schema);
 		
        // IMPORT
        
        String[] importList = getImportUri(schemaURL);
        for(int i=0; i<importList.length; i++){
        	System.err.println(i+" ITERATE OVER :"+importList[i]);
        	try{
        		schema = loadSchemaUsingResourceSet(importList[i]);
        		if (null == schema){
        			System.err.println("ERROR: Could not load a XSDSchema object!");
        		} 
        	}catch(Exception e){
			System.err.println("exception");
        	}
        	
        	List allElements = schema.getElementDeclarations();
   		  	for (Iterator iter = allElements.iterator(); iter.hasNext(); /* no-op */){
   	            XSDElementDeclaration elemDecl = (XSDElementDeclaration)iter.next();
   	            createRuleForElement(elemDecl);  
   	            createAlternativeContents(elemDecl);
   	            createRuleForAttributes(elemDecl); 
   		  	}
        }
        
        //createRuleForPCDATA();
		
		printRules();
		printAttrRules();
	
		System.err.println("");
        System.err.println("------------------------ END ELEMENTS -------------------");
        System.err.println("");
        
		  
        System.err.println("Creating Rules DONE");
	}
	
	
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////   ABBOZZO   DI  ALTERNATIVE CONTENTS     	///////////////////////////////////////
	
	
	
	
	/**
	 * 
	 * @param elemDecl
	 * @return
	 */
	public Collection createAlternativeContents(XSDElementDeclaration elemDecl){
		
		
		ContentGraph cg = createContentGraph(elemDecl.getQName());
		visitContentGraphPaths(cg);
		
		// secondo me:
		// i contents_strings per ogni elemento dovrebbero essere  come minimo la lista di tutti i possibili cammini minimi
		// ancora piu' utile, la lista di tutti i cammini senza cicli da start ad end nel grafo. 
		// il formato dell'output dovrebbe essere un Vector in cui ogni elemento e' fatto da una string contenente 
		// la lista delle label delle transizioni separate da virgole, tipo:
		// meta,intestazione,formulainiziale,articolato,formulafinale,conclusione   (cammino per l'elemento DocArticolato)
		// in questi due casi posso riempire la tabella alternativecontents una volta per tutte e consultare quella.
		
		// altrimenti si puo' fare in modo di richiedere un "cammino" (sequenza di label) che contiene una o piu' label in particolare.
		// in questo caso pero' deve essere fatto in linea e non si puo' backuppare sulla hashtable
		
		Vector contents_strings = readAlternativeContents(elemDecl.getTypeDefinition());
		alternative_contents.put(elemDecl.getQName(), contents_strings);
		
		return contents_strings;
		
		
		
	}
	
	
	
//	 USO nel caso di template minimale; nel caso degli alternative contents ci bastano i cammini composti da archi "complessi" e non bisogna esplodere gli archi:
//	 while (true) {
//		 if (visitContentGraph(graph) < Integer.MAX_VALUE){
//			 //System.err.println("finite path");
//			 return getXMLContent(graph);
//		 }
//		 //System.err.println("!   infinite path:  exploding");
//		 // no default content: substitute each element with its ContentGraph
//		 explodeContentGraph(graph);
//	 }
	
	
	static protected int visitContentGraphPaths(ContentGraph graph) {
		ContentGraph.Node first = graph.getFirst();
		ContentGraph.Node last = graph.getLast();

		// init visit
		Vector queue = new Vector();
		for (Iterator i = graph.visitNodes(); i.hasNext();)
			((ContentGraph.Node) i.next()).resetVisit();
		first.setVisit(0, "", null);
		queue.add(first);

		// visit the graph
		while (queue.size() > 0) {
			// pop first element from queue
			ContentGraph.Node tovisit = (ContentGraph.Node) queue.elementAt(0);
			queue.remove(0);

			// for all the outgoing edges
			for (int i = 0; i < tovisit.getNoEdges(); i++) {
				// check if the edge is visitable

				int step = Integer.MAX_VALUE;
				Object edge = tovisit.getEdge(i);
				String edgename = tovisit.getEdgeName(i);

				if (edge instanceof ContentGraph)
					step = visitContentGraphPaths((ContentGraph) edge);
				else if (edgename.compareTo("#PCDATA") == 0)
					step = 1;
				else if (edgename.compareTo("#EPS") == 0)
					step = 0;

				if (step < Integer.MAX_VALUE) {
					// if this path is shorter set the new path
					ContentGraph.Node destination = tovisit.getDestination(i);
					int visit_length = tovisit.getVisitLength() + step;
					if (destination.getVisitLength() > visit_length) {
						// push the next node of the path in the queue
						destination.setVisit(visit_length, edge, tovisit);
						queue.add(destination);
					}
				}
			}
		}

		return last.getVisitLength();
	}
	
	
	//////////////////////////////////// FINE ALTERNATIVE CONTENTS ///////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * 
	 * @param elemDecl
	 * @return
	 */
	public Vector readAlternativeContents(XSDTypeDefinition typeDef) {
		return null;
	}
	
	
	
	
	
	
	/**
	 * 
	 * @param elemDecl
	 * @param dfa
	 */
	public void mergedAddRule(XSDElementDeclaration elemDecl, XSDParticle.DFA dfa){
	    	    	
	    	if(rules.get(elemDecl.getQName())==null){
	    		//System.err.println("rule for "+name+" NOT present");
	    		rules.put(elemDecl.getQName(), dfa);
	        	elemDeclNames.put(elemDecl.getQName(), elemDecl);
	    	}
	    	else{
	    		if(elemDecl.getType() instanceof XSDComplexTypeDefinition && elemDecl.getType().getComplexType()!=null){
	    			rules.put(elemDecl.getQName(), dfa);
	    			elemDeclNames.put(elemDecl.getQName(), elemDecl);
	    			//System.out.println("--------------->  replacing rule for "+elemDecl.getQName());
	    		}
	    	}
	 }
	  
	
	/**
	 * 
	 * @param elemName
	 * @return
	 */         
	public String getDFAType(String elemName){
			XSDTypeDefinition typedef = ((XSDElementDeclaration)elemDeclNames.get(elemName)).getType();
			 if (typedef instanceof XSDSimpleTypeDefinition)
				 return " SIMPLE ";
			 if (typedef instanceof XSDComplexTypeDefinition){
				 if (typedef.getComplexType()!=null)
					 return " COMPLEX ";
				 else
					 return "  SPECIALE ";
			 }
			return " UNDEFINED ";
	}
	
	/**
	 * 
	 *
	 */
	public void printRules(){
	    	for(Iterator it = rules.keySet().iterator(); it.hasNext();){
	    		String elemName = (String) it.next();
	    		System.err.println("********** AUTOMA  "+getDFAType(elemName)+" DI "+elemName+" ************");
	              printDFA((XSDParticle.DFA)rules.get(elemName));
	    	}
	}
	    
	
	/**
	 * 
	 * @param elemDecl
	 */
	public void createRuleForElement(XSDElementDeclaration elemDecl){

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
	        	
	            //System.err.println("********** AUTOMA simple DI "+elemDecl.getQName()+" ************");
	            //printDFA(dfa);
	        	
	         }
	        else if (typedef instanceof XSDComplexTypeDefinition){
	            
	        	// logger
	        	//System.err.println(elemDecl.getQName()+" : complex Type");
	            
	            
	            if(typedef.getComplexType()!=null){
	            	//System.err.println("----------------> typedef for "+elemDecl.getQName()+" instanceof XSDComplexTypeDefinition;   getComplexType NOT NULL");
	            	
	            	//if(elemDecl.getQName().equals("h:p"))
	            	//	System.out.println("---------------------------------> h:p is Complex");


	                XSDParticle.DFA dfa = (XSDParticle.DFA) typedef.getComplexType().getDFA();
	                
	                //rules.put(elemDecl.getQName(), dfa);
	                mergedAddRule(elemDecl, dfa);
	        	    
	            }
	            else{
	            	//System.err.println("----------------> typedef for "+elemDecl.getQName()+" instanceof XSDComplexTypeDefinition;   getComplexType NULL");
	            	
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
	                //rules.put(elemDecl.getQName(), dfa);
	                mergedAddRule(elemDecl, dfa);
	                
	               
	            }
	        }
			
	        else{
	        	System.out.println("niente rule per "+elemDecl.getQName());
	        }        
	}

	/**
	 * 
	 * @param elemDecl
	 */
	public void createRuleForAttributes(XSDElementDeclaration elemDecl){

        String attrName=""; //nome dell'attributo analizzato
        String type=""; //type dell'attributo
        String defaultValue=""; //literal dell'uso dell'attributo
        String value = ""; //lexical value dell'uso dell'attributo

        if( elemDecl.getType() instanceof XSDComplexTypeDefinition){
            List allAttributeUses = ((XSDComplexTypeDefinition)elemDecl.getType()).getAttributeUses();

            for (Iterator iterA = allAttributeUses.iterator();iterA.hasNext();/* no-op */){
                XSDAttributeUse attruse = (XSDAttributeUse)iterA.next();
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
                }else
                    simpleType="no type-->no name";


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
                // FIXME tommaso    type = baseType al posto di "NO TYPE"
                type=baseType;//"NO TYPE";
                if(allTypeFacets.size()>0){ //è una enumeration
                    type="(";
                    for (Iterator iterB = allTypeFacets.iterator();iterB.hasNext();/* no-op */){
                        XSDConstrainingFacet restriction = (XSDConstrainingFacet)iterB.next();
//                        System.out.println("     facet name: "+restriction.getFacetName()); //c'è scritto enumeration
//                        System.out.println("         lexical value:    "+restriction.getLexicalValue());
                        type+=restriction.getLexicalValue()+"|";
                    }
                    type=type.substring(0, type.length()-1)+")";

                }
           
                //System.out.println(elemDecl.getQName()+" -attr: "+attrName+"["+simpleType+"], type= "+type+", defaultValue = "+defaultValue+"value = "+(value!=null?value:" null"));
                HashMap att_hash = (HashMap) attributes.get(elemDecl.getQName());
                if (att_hash == null) {
                    att_hash = new HashMap();
                    attributes.put(elemDecl.getQName(), att_hash);
                }
                // add a new attribute definition
                AttributeDeclaration attrDecl = new AttributeDeclaration(type,defaultValue,value);
                mergedAddAttribute(elemDecl,att_hash,attrName,attrDecl);
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
				//System.out.println("----------------------------------> replacing attribute "+attrName+"for element "+elemDecl.getQName());
			}
		}
	}
	

	
	/**
	 * 
	 *
	 */
	public void printAttrRules(){
	       for(Iterator it = attributes.keySet().iterator(); it.hasNext();){
	            String elemName = (String) it.next();
	            System.err.println("- ********** MAPPA attributi	"+/*getDFAType(attrName)+*/" DI "+elemName+" ************");
	            for(Iterator it2 = ((HashMap)attributes.get(elemName)).keySet().iterator(); it2.hasNext();){
	            	String attrName = (String)it2.next();
	            	HashMap att_hash = ((HashMap)attributes.get(elemName));
	            	System.err.println("- ATTRIBUTE: elem= "+elemName+" name="+attrName+" "+((AttributeDeclaration)att_hash.get(attrName)).toString());
	            }
	       }
	}

			
	
	
		
	// FIXME serve ?
	public void createRuleForANY(){
		        
	        XSDParticle xsdParticle; 
	        xsdParticle = XSDFactory.eINSTANCE.createXSDParticle();
	        
	        XSDParticleImpl.XSDNFA dfa = (XSDParticleImpl.XSDNFA)xsdParticle.getDFA();
	        
	    	rules.put("#ANY", dfa);
	    	
	        
	        System.err.println("********** AUTOMA DI #ANY ************");
	        printDFA(dfa);
	      
	}

		
	// FIXME serve ?
	public void createRuleForPCDATA(){
	        
	        XSDParticle xsdParticle; 
	        xsdParticle = XSDFactory.eINSTANCE.createXSDParticle();
	        
	        XSDParticleImpl.XSDNFA dfa = (XSDParticleImpl.XSDNFA)xsdParticle.getDFA();
	        
	    	rules.put("#PCDATA", dfa);
	    	XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration)xsdParticle.getTerm();  // e' null (il term)
	    	elemDeclNames.put("#PCDATA", xsdElementDeclaration);
	        
//	        System.err.println("********** AUTOMA DI #PCDATA ************");
//	        printDFA(dfa);
	      
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
		//
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
			System.err.println("Exception caught in method 'load schema using ResourceSet' message = " + e.getMessage() + " xsdSchemaResource: " +xsdSchemaResource);  
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
							ret.add(folder+xsdSchemaImport.getSchemaLocation().substring(1));
							System.err.println(" IMPORT: namespace --> "+xsdSchemaImport.getNamespace());
							System.err.println(" IMPORT: location  --> "+xsdSchemaImport.getSchemaLocation());
					}
				}             
			}	
		}
		String[] list = new String[ret.size()];
		ret.copyInto(list);
		return list;
	}
	
	
	
	/**
	 * 
	 * @param schema
	 */
	public void listElements(XSDSchema schema){
		
		System.err.println("");
		System.err.println("-----------------------------       *       --------------------------------");
		System.err.println("");
		
		List allElements = schema.getElementDeclarations(); 
        System.err.println("----------------------> elements found");
        for(Iterator ite = allElements.iterator(); ite.hasNext();){
        	Object ob = ite.next();
        	System.err.println(ob.toString());
        	System.err.println("               Type "+((XSDElementDeclaration)ob).getType().toString());
        }
		
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
    /**
    * * @param schemaURL
    * @return
    * @throws Exception
    */
	public static XSDSchema loadSchemaUsingResourceSet(String schemaURL) throws Exception{

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
			System.err.println("Exception caught in method 'load schema using ResourceSet' message = " + e.getMessage() + " xsdSchemaResource: " +
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
						System.err.println("Unresolved schema "+ xsdSchemaDirective.getSchemaLocation() +" in " + xsdResource.getURI());

						if(xsdSchemaDirective instanceof XSDImport){
							XSDImport xsdSchemaImport = (XSDImport)xsdSchemaDirective;
							System.err.println(" IMPORT: namespace --> "+xsdSchemaImport.getNamespace());
							System.err.println(" IMPORT: location  --> "+xsdSchemaImport.getSchemaLocation());
						}
					}
					else{
						System.err.println("Resolved schema "+ xsdSchemaDirective.getSchemaLocation() +" in " + xsdResource.getURI());
						if(xsdSchemaDirective instanceof XSDImport){
							XSDImport xsdSchemaImport = (XSDImport)xsdSchemaDirective;
							System.err.println(" IMPORT: namespace --> "+xsdSchemaImport.getNamespace());
							System.err.println(" IMPORT: location  --> "+xsdSchemaImport.getSchemaLocation());
						}
					}             
				}	
			}		
			
			return xsdResource.getSchema();
		}
		System.err.println("loadSchemaUsingResourceSet(" + schemaURL + ") did not contain any schemas!");
		return null;
	}
   
     
	
	////////////////////////////////////////////
	////
	//// 	Metodi di validazione  
    ////
	////

	/**
	 * 
	 * @param elem_name
	 * @param elem_children
	 * @return
	 */
	public boolean isValid(String elem_name, Collection elem_children)  {
		return isValid(elem_name, elem_children, false);
	}
	
	
	/**
	 * 
	 * @param elem_name
	 * @param elem_children
	 * @return
	 */
	public boolean isValid(String elem_name, Collection elem_children, boolean withgaps){
		// text elements must have no children
		if (elem_name.startsWith("#"))
			return (elem_children.size() == 0);
		// get automata
		XSDParticle.DFA rule = (XSDParticle.DFA) rules.get(elem_name);
		if (rule == null)
			System.err.println("No rule for element <" + elem_name + ">");

		return align(elem_name,elem_children,withgaps);
		
	}
	
	/**
	 * 
	 * @param elem_name
	 * @param elem_children
	 * @param choice_point
	 * @return
	 */
	public Collection getAlternatives(String elem_name, Collection elem_children, int choice_point){
		// text elements must have no children
		if (elem_name.startsWith("#"))
			return new Vector();
		// get automata
		XSDParticle.DFA rule = (XSDParticle.DFA) rules.get(elem_name);
		if (rule == null)
			System.err.println("No rule for element <" + elem_name + ">");
		return alignAlternatives(elem_children, elem_name, choice_point);
	}
	
	
	
	/**
	 * 
	 * @param fromState
	 * @param label
	 * @return
	 */
	public XSDParticle.DFA.State getNextState(String ruleName, XSDParticle.DFA.State fromState, String label){
		if(isMixed(ruleName) && label.equalsIgnoreCase("#PCDATA")){
			return fromState;
		}
		return getAcceptingTransition(fromState,label)!=null?getAcceptingTransition(fromState,label).getState():null;		
	}
	

	/**
	 * 
	 * @param fromState
	 * @param label
	 * @return
	 */
	public XSDParticle.DFA.Transition getAcceptingTransition(XSDParticle.DFA.State fromState, String label){
		
		// FIXME     sistemare prefissi e namespace
		String nameSpaceUri;
		if(label.startsWith("h:")){
		    nameSpaceUri = "http://www.w3.org/HTML/1998/html4";
		    label = label.substring(label.indexOf(":")+1);
		}
		else if(label.startsWith("dsp:")){
			nameSpaceUri = "http://www.normeinrete.it/nir/disposizioni/2.2/";
			label = label.substring(label.indexOf(":")+1);
		//FIXME verifica se va bene su xsd:any	
		}else if(label.startsWith("#")){
			nameSpaceUri = "";
		}else 
			nameSpaceUri = targetNameSpace;
		
		XSDParticle.DFA.Transition accepting = fromState.accept(nameSpaceUri,label);
		return  accepting;
	}
	
	
	
//	public XSDParticle.DFA.Transition accept(String namespaceURI, String localName)
//    {
//      for (Iterator transitions = getTransitions().iterator(); transitions.hasNext(); )
//      {
//        Transition transition = (Transition)transitions.next();
//        XSDParticle xsdParticle = transition.getParticle();
//        XSDTerm xsdTerm = xsdParticle.getTerm();
//        if (xsdTerm instanceof XSDElementDeclaration)
//        {
//          XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration)xsdTerm;
//          if ((namespaceURI == null ? 
//                 xsdElementDeclaration.getTargetNamespace() == null : 
//                 namespaceURI.equals(xsdElementDeclaration.getTargetNamespace())) &&
//                localName.equals(xsdElementDeclaration.getName()))
//          {
//            return transition;
//          }
//        }
//        else if (xsdTerm instanceof XSDWildcard)
//        {
//          XSDWildcard xsdWildcard = (XSDWildcard)xsdTerm;
//          if (xsdWildcard.allows(namespaceURI))
//          {
//            return transition;
//          }
//        }
//      }
//
//      return null;
//    }
//  }
	
	
	
	/**
	 * 
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
	            v.add("#EPS");
	            System.err.println("xsdWildCard: "+xsdWildcard.getStringNamespaceConstraint());
	          }          
//            da dtdRulesManagerImpl:
	          
//	          if (model == "ANY")
//	  			// rule.addTransition(start,end,"#ANY");
//	  			rule.addTransition(start, end, "#PCDATA");
	          
//           ________________________________________________________________________________	          
	          
//	          da XSDParticleImpl .accept    NOTA andra' modificato anche il nostro getNext ?
	          
//	          else if (xsdTerm instanceof XSDWildcard)
//	          {
//	            XSDWildcard xsdWildcard = (XSDWildcard)xsdTerm;
//	            if (xsdWildcard.allows(namespaceURI))
//	            {
//	              return transition;
//	            }
//	          }
	          
	          
			
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
	 * @throws DtdRulesManagerException
	 */
	public boolean align(String ruleName, Collection sequence, boolean with_gaps) {
		XSDParticle.DFA rule = (XSDParticle.DFA) rules.get(ruleName);
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
	public boolean align (String ruleName, Collection sequence){
	
		XSDParticle.DFA rule = (XSDParticle.DFA) rules.get(ruleName);
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
	 * @throws DtdRulesManagerException
	 */
	public boolean alignWithGaps(String ruleName, Iterator nav, Vector startFrom, List nodes){

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
	 * @throws DtdRulesManagerException
	 */
	public Collection alignAlternatives(Collection sequence, String ruleName, int choice_point) {
		
		XSDParticle.DFA rule = (XSDParticle.DFA) rules.get(ruleName);
		
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
		// ?
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
     * @param dfa
     */
    protected static void printDFA(XSDParticle.DFA dfa)
    {	
      ((XSDParticleImpl.XSDNFA)dfa).dump(System.err);
    }
    

	/**
	 * 
	 * @param elemName
	 * @return
	 */    
    public ContentGraph createContentGraph(String elemName) {
    	XSDParticle.DFA rule = (XSDParticle.DFA)rules.get(elemName);
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
                        	System.out.println("null destination for content "+elemName);
                        	System.out.println("dfsa_edge = "+dfsa_edge);
                        }
                        ContentGraph.Node graph_destination = (ContentGraph.Node)graph_nodes_table.get(dfsa_destination.toString());
                        graph_node.addEdge(graph_edge, graph_destination);
                       
                }
                               
//                // FIXME    ci vuole o no ? confronto col DFSA e XSDNFA
//                if(isMixed(elemName))
//                    graph_node.addEdge("#PCDATA", last);
                
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
    
    
    
    /**
     * 
     * @param elemName
     * @return
     */
    public boolean isMixed(String elemName){
    	XSDElementDeclaration elemDecl = (XSDElementDeclaration) elemDeclNames.get(elemName);
		XSDTypeDefinition elemType = elemDecl.getType();
		return((elemType.getComplexType()!=null && ((XSDComplexTypeDefinition)elemType).isMixed())
				||(elemDecl.getType().getBaseType().getComplexType()!=null && ((XSDComplexTypeDefinition)elemDecl.getType().getBaseType()).isMixed()));
		// eredita mixed anche dal tipo base che estende [vedi ad esempio dataDoc, numDoc]
    }
    
    
}
