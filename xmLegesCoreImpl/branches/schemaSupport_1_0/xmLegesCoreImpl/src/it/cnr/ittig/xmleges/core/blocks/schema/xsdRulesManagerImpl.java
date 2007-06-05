package it.cnr.ittig.xmleges.core.blocks.schema;


import it.cnr.ittig.xmleges.core.blocks.schema.ContentGraph;

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
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeDefinition;
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
import org.eclipse.xsd.XSDParticle.DFA.State;
import org.eclipse.xsd.XSDParticle.DFA.Transition;
import org.eclipse.xsd.impl.XSDParticleImpl;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;


public class xsdRulesManagerImpl {

	
	protected HashMap rules;
	protected HashMap elemDeclNames;
	protected HashMap alternative_contents;
	protected HashMap attributes;
	protected String targetNameSpace;
	
	
	public xsdRulesManagerImpl(){		
		rules = new HashMap();
		elemDeclNames = new HashMap();
		alternative_contents = new HashMap();
		attributes = new HashMap();
	}

	
	
	public void createRuleForElement(XSDElementDeclaration elemDecl){
		
		// ?? come gestire: i simpleType
		//				  : i complexType con typedef.getComplexType NULL
		//				  : i complexType con typedef.getName NULL (anonymous)
		
//		System.out.println("sto creando la regola per: "+elemDecl.getName());
		XSDTypeDefinition typedef = elemDecl.getType();
		
		
        
        if (typedef instanceof XSDSimpleTypeDefinition){
            System.err.println(elemDecl.getQName()+" : simple Type");
            
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
            
        	rules.put(elemDecl.getQName(), dfa);
        	elemDeclNames.put(elemDecl.getQName(), elemDecl);
        	
            
            System.err.println("********** AUTOMA simple DI "+elemDecl.getQName()+" ************");
            printDFA(dfa);
            
        }
        
        else if (typedef instanceof XSDComplexTypeDefinition){
            System.err.println(elemDecl.getQName()+" : complex Type");
            
            if(((XSDComplexTypeDefinition)typedef).isMixed())
            	System.err.println("------------------------>          MIXED");
            
            
            if(typedef.getComplexType()!=null){

            	XSDParticle xsdParticle;
                if (typedef.getContainer() instanceof XSDParticle)
                {
                  xsdParticle = (XSDParticle)typedef.getContainer();
                  
                }
                else
                {
                  xsdParticle = XSDFactory.eINSTANCE.createXSDParticle();
                  
                }

                XSDParticle.DFA dfa = (XSDParticle.DFA) typedef.getComplexType().getDFA();
                rules.put(elemDecl.getQName(), dfa);
                elemDeclNames.put(elemDecl.getQName(), elemDecl);

              
                System.err.println("********** AUTOMA DI "+elemDecl.getQName()+" ************");
                printDFA(dfa);

        	    
            }
            else{

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
                rules.put(elemDecl.getQName(), dfa);
                elemDeclNames.put(elemDecl.getQName(), elemDecl);
                

                System.err.println("********** AUTOMA speciale DI "+elemDecl.getQName()+" ************");
                printDFA(dfa);

            }
        }
		
        else{
        	System.out.println("niente rule per "+elemDecl.getQName());
        }
		// add alternative contents for element
		//createAlternativeContents(name, model);
	}

	public void createRuleForAttribute(XSDAttributeGroupDefinition attrDecl){
		
		//GLI ATTRIBUTI SONO SEMPRE DI TIPO SIMPLE!!
		
             
              XSDParticle xsdParticle = XSDFactory.eINSTANCE.createXSDParticle();
              
                    XSDParticleImpl.XSDNFA dfa = (XSDParticleImpl.XSDNFA)xsdParticle.getDFA();
            
        	attributes.put(attrDecl.getQName(), dfa);
        	
            
            System.err.println("********** AUTOMA simple DI "+attrDecl.getQName()+" ************");
            printDFA(dfa);
            
        }

	public void createRuleForANY(){
	        
        XSDParticle xsdParticle; 
        xsdParticle = XSDFactory.eINSTANCE.createXSDParticle();
        
        XSDParticleImpl.XSDNFA dfa = (XSDParticleImpl.XSDNFA)xsdParticle.getDFA();
        
    	rules.put("#ANY", dfa);
    	
        
        System.err.println("********** AUTOMA DI #ANY ************");
        printDFA(dfa);
      
	}

	public void createRuleForPCDATA(){
        
        XSDParticle xsdParticle; 
        xsdParticle = XSDFactory.eINSTANCE.createXSDParticle();
        
        XSDParticleImpl.XSDNFA dfa = (XSDParticleImpl.XSDNFA)xsdParticle.getDFA();
        
    	rules.put("#PCDATA", dfa);
    	XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration)xsdParticle.getTerm();  // e' null (il term)
    	elemDeclNames.put("#PCDATA", xsdElementDeclaration);
        
        System.err.println("********** AUTOMA DI #PCDATA ************");
        printDFA(dfa);
      
	}
	
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
		return alignAlternatives(elem_children, rule, choice_point);

	}
	
	
	/**
	 * 
	 * @param fromState
	 * @param label
	 * @return
	 */
	public XSDParticle.DFA.State getNextState(XSDParticle.DFA.State fromState, String label){
		
		// FIXME     sistemare prefissi e namespace
		String nameSpaceUri;
		if(label.startsWith("h:")){
		    nameSpaceUri = "http://www.w3.org/HTML/1998/html4";
		    label = label.substring(label.indexOf(":")+1);
		}
		else
			nameSpaceUri = targetNameSpace;
		
		
		XSDParticle.DFA.State next = (fromState.accept(nameSpaceUri,label)!=null)?fromState.accept(nameSpaceUri,label).getState():null;
		return next;
		
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
		else
			nameSpaceUri = targetNameSpace;
		
		XSDParticle.DFA.Transition accepting = fromState.accept(nameSpaceUri,label);
		return  accepting;
	}
	
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
	          }
	          
//	          else if (xsdTerm instanceof XSDWildcard)
//	          {
//	            XSDWildcard xsdWildcard = (XSDWildcard)xsdTerm;
//	            
//	          
//	          }
			
		}
		return v;
	}
	
	
	
	/**
	 * xsdparticle.issubset(anotherparticle)?????????
	 */
	
	
	
	
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
			return alignWithGaps(sequence.iterator(), startfrom, rule.getStates());
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
		XSDParticle.DFA.Transition tr;
		
		for (Iterator i = sequence.iterator(); i.hasNext() && finalState != null;) {
			String childName = (String) i.next();
			if(childName.equals("#PCDATA")){
				XSDElementDeclaration elemDecl = (XSDElementDeclaration) elemDeclNames.get(ruleName);
				XSDTypeDefinition elemType = elemDecl.getType();
				if(elemType.getComplexType()!=null && ((XSDComplexTypeDefinition)elemType).isMixed()){
					System.err.println("isMixed");
				}
				else{
					tr= getAcceptingTransition(finalState,childName);
//					System.err.println("transizione: "+XSDParticleImpl.XSDNFA.getComponentLabel(Dtr.getParticle()));
					finalState = tr!=null?tr.getState():null;
				}
			}
			else{
				tr= getAcceptingTransition(finalState,childName);
				
				//System.err.println("transizione: "+XSDParticleImpl.XSDNFA.getComponentLabel(Dtr.getParticle()));
				finalState = tr!=null?tr.getState():null;
			}			
		}
		if(finalState!=null){
			System.err.println("FinalState Exiting Size: "+finalState.getTransitions().size());
			if(finalState.isAccepting())
			  System.err.println("FinalState ACCEPTING: TRUE");
			else
			  System.err.println("FinalState ACCEPTING: FALSE");
		}
		return (finalState != null && finalState.isAccepting());//&& finalState.getTransitions().size()==0);   // se non ci sono transizioni uscenti e' un nodo finale 	
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
	public boolean alignWithGaps(Iterator nav, Vector startFrom, List nodes){

		if (!nav.hasNext())
			return true;
		String token = (String) nav.next();

		// cerca tutti i nodi che riconoscono il token

		Vector new_startFrom = new Vector();
		for (Iterator id = nodes.iterator(); id.hasNext();) {
			State src = (State) id.next();
			Transition transitionDest = getAcceptingTransition(src,token);
			if (transitionDest != null) {
				State dest=transitionDest.getState();
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
		return alignWithGaps(nav, new_startFrom, nodes);
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
	public Collection alignAlternatives(Collection sequence, XSDParticle.DFA rule, int choice_point) {
		
		if (rule.getStates().size() == 0)
			System.out.println("Empty automata");

		XSDParticle.DFA.State start_node = rule.getInitialState();
		XSDParticle.DFA.State last_node = start_node;
		
		Object[] seq_array = sequence.toArray();

		// align until the choice point
		//verifica allineamento della sequenza di partenza sequence
		for (int i = 0; i <= choice_point && i < seq_array.length && last_node != null; i++) {
			last_node = getNextState(last_node, (String) seq_array[i]);
		}
		if (last_node == null){
			System.out.println("The sequence does not align, cannot find alternatives");
			return null;
		}

		// try all the alternatives
		XSDParticle.DFA.State choice_node = last_node;
		Vector alternatives = new Vector();
		Collection vocabulary = getVocabulary(choice_node);
		for (Iterator v = vocabulary.iterator(); v.hasNext();) {
			String symbol = (String) v.next();

			// align from the choice point
			last_node = getNextState(choice_node, symbol);
			for (int i = choice_point + 1; i < seq_array.length && last_node != null; i++)
				last_node = getNextState(last_node, (String) seq_array[i]);

			// valid alternative
			if (last_node != null && last_node.isAccepting())
				alternatives.add(symbol);
		}

		return alternatives;
	}


	
	
	
	public void loadRules(String schemaURL){	
		
		
		
		
		
        XSDSchema schema = null;
		try{
		   schema = loadSchemaUsingResourceSet(schemaURL);
		   if (null == schema){
	            System.err.println("ERROR: Could not load a XSDSchema object!");
	            return;
	       } 
		}catch(Exception e){
			System.err.println("exception");
		}
		
		targetNameSpace = schema.getTargetNamespace();
		
		System.err.println("loaded schema");
		System.err.println("TARGET NAMESPACE: "+schema.getTargetNamespace());
		
		List allElements = schema.getElementDeclarations();
		
        
        for (Iterator iter = allElements.iterator(); iter.hasNext(); /* no-op */){
            XSDElementDeclaration elemDecl = (XSDElementDeclaration)iter.next();
            
            System.err.println("----> Sto esaminando l'elemento: "+ elemDecl.getQName());   
            createRuleForElement(elemDecl);
              
        }
        
        createRuleForPCDATA();
        
        List allAttributes = schema.getAttributeGroupDefinitions();//etAttributeDeclarations();
        
        		
        
        for (Iterator iter = allAttributes.iterator(); iter.hasNext(); /* no-op */){
        	XSDAttributeGroupDefinition attrDecl = (XSDAttributeGroupDefinition)iter.next();
            
            System.err.println("----> Sto esaminando l'attributo: "+ attrDecl.getQName());   
            createRuleForAttribute(attrDecl);
              
        }
        
       
        
        
        System.err.println("Creating Rules DONE");
	}
	
	
	
	/**
	 * 
	 * @param schemaURL
	 * @return
	 * @throws Exception
	 */
    public static XSDSchema loadSchemaUsingResourceSet(String schemaURL) throws Exception{

    	    	
    	XSDResourceFactoryImpl resourceFactory = new XSDResourceFactoryImpl();
    	Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xsd",resourceFactory);
    	 		
		// Create a resource set and load the main schema file into it.
		ResourceSet resourceSet = new ResourceSetImpl(); 

	
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//     PROBLEMI CON GLI IMPORT
		/////////////////////////////////////////////////////
		
		String xlinkURL =schemaURL+"/../xlink.xsd";
		File xlinkFile = new File(xlinkURL);
		if (xlinkFile.isFile()){
		  xlinkURL = URI.createFileURI(xlinkFile.getCanonicalFile().toString()).toString();
		  System.out.println(xlinkURL);
		}
		else
		   System.out.println(xlinkURL+"./xlink.xsd not file");
		
	    
	   
		// Create a derived URIConverter to track normalization.
	    //			
	    resourceSet.getURIConverter().getURIMap().put(URI.createURI("./xlink.xsd"),URI.createURI(xlinkURL));
	    resourceSet.getURIConverter().getURIMap().put(URI.createURI("http://www.w3.org/HTML/1998/html4"),URI.createURI("./h.xsd"));
	    resourceSet.getURIConverter().getURIMap().put(URI.createURI("http://www.normeinrete.it/nir/disposizioni/2.2"),URI.createURI("./dsp.xsd"));
		
	    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    
	    
	    
	    //
		// prende il path assoluto di schemaURL
		//
		File file = new File(schemaURL);
		if (file.isFile())
		  schemaURL = URI.createFileURI(file.getCanonicalFile().toString()).toString();
		
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
		
		// getResources() returns an iterator over all the resources, i.e., the main resource
		// and those that have been included, imported, or redefined.
		for (Iterator resources = resourceSet.getResources().iterator(); resources.hasNext(); /* no-op */)
		{
		    // Return the first schema object found
		    Resource resource = (Resource)resources.next();
		    if (resource instanceof XSDResourceImpl)
		    {
		    	 	
		    	 XSDResourceImpl xsdResource = (XSDResourceImpl)resource;

		         // Iterate over the schema's content's looking for directives.
		         //
		         XSDSchema xsdSchema = xsdResource.getSchema();
		         for (Iterator contents = xsdSchema.getContents().iterator(); contents.hasNext(); )
		         {
		           XSDSchemaContent xsdSchemaContent = (XSDSchemaContent)contents.next();
		           if (xsdSchemaContent instanceof XSDSchemaDirective)
		           {   
		        	   XSDSchemaDirective xsdSchemaDirective = (XSDSchemaDirective)xsdSchemaContent;
					   if (xsdSchemaDirective.getResolvedSchema() == null) {
					   		System.err.println("Unresolved schema "+ xsdSchemaDirective.getSchemaLocation() +" in " + xsdResource.getURI());
					   		if(xsdSchemaDirective instanceof XSDImport){
					   			XSDImport xsdSchemaImport = (XSDImport)xsdSchemaDirective;
					   			System.err.println("			IMPORT: namespace --> "+xsdSchemaImport.getNamespace());
					   			System.err.println("			IMPORT: location  --> "+xsdSchemaImport.getSchemaLocation());
					   		}
					   } 
					   else {
					    		System.err.println("Resolved schema "+ xsdSchemaDirective.getSchemaLocation() +" in " + xsdResource.getURI());
					    	}  
		           }
		         }
		        return xsdResource.getSchema();
		    }
		}
		System.err.println("loadSchemaUsingResourceSet(" + schemaURL + ") did not contain any schemas!");
		return null;
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
    
//    private boolean alignWithGaps(Iterator nav, Vector startFrom) throws DtdRulesManagerException {
//		if (!nav.hasNext())
//			return true;
//		String token = (String) nav.next();
//
//		// cerca tutti i nodi che riconoscono il token
//		Collection nodes = nodes_table.values();
//		Vector new_startFrom = new Vector();
//		for (Iterator id = nodes.iterator(); id.hasNext();) {
//			Node src = (Node) id.next();
//			Node dest = src.getNext(token);
//			if (dest != null) {
//				// verifica che il nodo attuale sia raggiungible
//				// dall'insieme dei nodi di partenza
//				for (Iterator is = startFrom.iterator(); is.hasNext();) {
//					Node start = (Node) is.next();
//					if (canReach(start, src)) {
//						// aggiunge il nodo al nuovo insieme dei nodi di partenza
//						new_startFrom.addElement(dest);
//					}
//				}
//			}
//		}
//
//		if (new_startFrom.size() == 0)
//			return false;
//		return alignWithGaps(nav, new_startFrom);
//	}
	

    
    
    
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
                        State dfsa_destination = getNextState(dfsa_node, dfsa_edge);
                        ContentGraph.Node graph_destination = (ContentGraph.Node)graph_nodes_table.get(dfsa_destination.toString());
                        graph_node.addEdge(graph_edge, graph_destination);
                       
                }
                
                if(isMixed(elemName))
                    graph_node.addEdge("#PCDATA", last);
                
                // se il nodo e' un nodo iniziale del DFSA aggiunge una transizione vuota
                // dal nodo iniziale del ContentGraph
                if (rule.getInitialState().equals(dfsa_node))
                        first.addEdge("#EPS", graph_node);

                // se il nodo e' un nodo finale del DFSA aggiunge una transizione vuota verso
                // il
                // nodo finale del ContentGraph
                if (dfsa_node.isAccepting()) {
                        if (rule.getInitialState().equals(dfsa_node)) {
                                // in questo modo si previene che esistano elementi con il contenuto
                                // vuoto
                                graph_node.addEdge("#PCDATA", last);
                        } else
                                graph_node.addEdge("#EPS", last);
                }
        }

        return graph;
}
    
    public boolean isMixed(String elemName){
    	XSDElementDeclaration elemDecl = (XSDElementDeclaration) elemDeclNames.get(elemName);
		XSDTypeDefinition elemType = elemDecl.getType();
		return(elemType.getComplexType()!=null && ((XSDComplexTypeDefinition)elemType).isMixed());
    }
    
    
    
   // PROBLEMA DELL'OutofMemory: 
   // FIXME  
    
   // in SchemaRulesManagerImpl.getContentGraph manca la parte sul queryTextContent e il metodo
   // createTextContentGraph(elem_name); per l'esplosione degli archi elemento (descrittori, redazionale, etc..)
    
//   NON SI VERIFICA
    //   if (edge instanceof ContentGraph) {
    
//  check every edge of the old graph
//	for (Iterator i = graph.visitNodes(); i.hasNext();) {
//		ContentGraph.Node src = (ContentGraph.Node) i.next();
//		for (int j = 0; j < src.getNoEdges(); j++) {
//			Object edge = src.getEdge(j);
//			String edge_name = src.getEdgeName(j);
//			if (edge instanceof ContentGraph) {
//				System.err.println(edge_name+"  instanceOf ContentGraph");
//				// recursively explore subgraph
//				explodeContentGraph((ContentGraph) edge);
//			} else if (edge_name.compareTo("#PCDATA") != 0 && edge_name.compareTo("#EPS") != 0) {
//				// replace edge with ContentGraph
//				src.setEdge(getContentGraph(edge_name), j);
//			}
//		}
//	}
    
    
//    
//   ORIGINAL    
//    
// 
//    /**
//	 * Crea un ContentGraph che rappresenta il contenuto di questo elemento
//	 */
//	public ContentGraph createContentGraph() {
//
//		ContentGraph graph = new ContentGraph(name);
//		ContentGraph.Node first = graph.getFirst();
//		ContentGraph.Node last = graph.getLast();
//
//		// aggiunge un nodo al grafo per ogni nodo del DFSA
//		HashMap graph_nodes_table = new HashMap();
//		for (Iterator i = nodes_table.keySet().iterator(); i.hasNext();) {
//			graph_nodes_table.put((String) i.next(), graph.addNode());
//		}
//
//		// per ogni nodo del DFSA
//		for (Iterator i = nodes_table.keySet().iterator(); i.hasNext();) {
//			String node_name = (String) i.next();
//			DFSA.Node dfsa_node = (DFSA.Node) nodes_table.get(node_name);
//			ContentGraph.Node graph_node = (ContentGraph.Node) graph_nodes_table.get(node_name);
//
//			// aggiunge un arco per ogni transizione
//			for (Iterator j = dfsa_node.getVocabulary().iterator(); j.hasNext();) {
//				String dfsa_edge = (String) j.next();
//				String graph_edge = dfsa_edge;
//				DFSA.Node dfsa_destination = dfsa_node.getNext(dfsa_edge);
//				ContentGraph.Node graph_destination = (ContentGraph.Node) graph_nodes_table.get(dfsa_destination.getName());
//				graph_node.addEdge(graph_edge, graph_destination);
//			}
//
//			// se il nodo e' un nodo iniziale del DFSA aggiunge una transizione vuota
//			// dal nodo iniziale del ContentGraph
//			if (dfsa_node.isStart())
//				first.addEdge("#EPS", graph_node);
//
//			// se il nodo e' un nodo finale del DFSA aggiunge una transizione vuota verso
//			// il
//			// nodo finale del ContentGraph
//			if (dfsa_node.isEnd()) {
//				if (dfsa_node.isStart()) {
//					// in questo modo si previene che esistano elementi con il contenuto
//					// vuoto
//					graph_node.addEdge("#PCDATA", last);
//				} else
//					graph_node.addEdge("#EPS", last);
//			}
//		}
//
//		return graph;
//	}
//   
// 
//    
    
    
    
    
    
}
