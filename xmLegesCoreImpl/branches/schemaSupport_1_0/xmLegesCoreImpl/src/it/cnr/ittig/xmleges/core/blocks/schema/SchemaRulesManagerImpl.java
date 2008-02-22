/**
 * RulesManager &egrave la classe che gestisce le regole scritte nella
 * DTD del documento. RulesManager legge una DTD e trasforma il
 * content di ogni elemento in un automa a stati finiti che
 * rappresenta le regole a cui deve sottostare il contenuto
 * dell'elemento. Questo automa pu&ograve essere usate per interrogare
 * la classe su come &egrave possibile modificare un elemento.
 * @author Alessio Ceroni
 * @version %I%, %G%
 */

package it.cnr.ittig.xmleges.core.blocks.schema;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.rules.RulesManager;
import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.lang.UtilLang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementazione del servizio it.cnr.ittig.xmleges.editor.services.rulesmanager.RulesManager;
 * 
 */
public class SchemaRulesManagerImpl implements RulesManager {

	Logger logger;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	
	protected boolean pre_check = false;
	
	
	protected UtilXsd utilXsd;
	
	
	/**
	 * Tabella hash contenente le regole per l'interrogazione sotto-forma di automi
	 * deterministici. Ogni regola &egrave associata ad un elemento.
	 */
	protected HashMap rules;
	protected HashMap elemDeclNames;
	
	/**
	 * Tabella hash dei possibili contenuti alternativi dei vari elementi. Ogni valore
	 * della tabella hash &egrave costituito da un vettore di stringhe formate da nomi di
	 * elementi separati da virgole che rappresentano le possibili alternative. La tabella
	 * hash &egrave indirizzata dai nomi degli elementi.
	 */
	protected HashMap alternative_contents;
	
	/**
	 * Tabella hash degli attributi associati agli elementi. La tabella hash &egrave
	 * indirizzata dai nomi degli elementi. Ogni valore della tabella hash &grave
	 * costituito da una seconda tabella hash che contiene tutti gli attributi
	 * dell'elemento associato. Questa seconda tabella hash &egrave indirizzata dai nomi
	 * degli attibuti dell'elemento. Ogni valore di questa seconda tabella &egrave
	 * costituito da un'istanza della classe AttributeDecl, e definisce l'attributo
	 * specifico.
	 */
	protected HashMap attributes;



	public void createRulesManager(String extension) {
		// TODO Auto-generated method stub
		
	}
	
	
	///////////////////////////////////////////////////////////////////////
	//
	//				 INIZIALIZZAZIONE     SCHEMA
	//
	
	
	// ------------ COSTRUTTORI --------------------

	public SchemaRulesManagerImpl(Logger logger) {
		enableLogging(logger);
		
		rules = new HashMap();
		elemDeclNames = new HashMap();
		alternative_contents = new HashMap();
		attributes = new HashMap();
			
		utilXsd = new UtilXsd(this);		
	}
	

	public void clear() {
		rules = new HashMap();
		elemDeclNames = new HashMap();
		alternative_contents = new HashMap();
		attributes = new HashMap();
	}

	// ------------ INIZIALIZZAZIONE DELLE REGOLE --------------------

//	public void loadRules(String filename) {
//		File xml_file = new File(filename);
//		loadRules(xml_file);
//	}
	
	
	public void loadRules(String schemaPath) {
		clear();
		utilXsd.loadRules(schemaPath);
		
	}
	
	
	public void loadRules(String filename, String schemaPath) {
		//logger.info("START loading rules from SCHEMA");
		
		String key = null;
		File xml_file = new File(filename);
		
		if (schemaPath.startsWith(".")) // crea path name assoluto
			schemaPath = xml_file.getParent().concat(File.separator+schemaPath.substring(2));
		
		File schema_file = UtilFile.getGrammarFile(schemaPath);
		
		
		
//		java.io.NotSerializableException: org.eclipse.xsd.impl.XSDParticleImpl$XSDNFA
		
//		try {
//			key = UtilLang.bytesToHexString(UtilFile.calculateMD5(schema_file));
//			//logger.debug("key for " + schemaPath + " = " + key.toString());
//		} catch (Exception ex) {
//			logger.error(ex.getMessage(), ex);
//		}
//		
//		
//		
//		String md5Path = new String("schemamd5");
//		new File(md5Path).mkdir();
//		File rulesMap = new File(md5Path + File.separator, key + "_rules");
//		File elemDeclMap = new File(md5Path + File.separator, key + "_elemDecl");
//		File alternativesMap = new File(md5Path, key + "_alternatives");
//		File attributesMap = new File(md5Path, key + "_attributes");
//
//		if (key != null && rulesMap.exists() && elemDeclMap.exists() && alternativesMap.exists() && attributesMap.exists()) {
//			loadRulesFromCachedMap(rulesMap, elemDeclMap, alternativesMap, attributesMap);
//		} else {
//			clear();
//			utilXsd.loadRules(schema_file.getAbsolutePath());
//			saveRulesOnCachedMap(rulesMap, elemDeclMap, alternativesMap, attributesMap);
//		}
		
        // OLD (Uncached)
		// clear old rules
		clear();
		utilXsd.loadRules(schema_file.getAbsolutePath());

	}
	
	

	
	public void loadRules(File xml_file) {
		// TODO dal file xml estrarre lo schemaURL dall'intestazione		
	}
	

	
	
	
	// lettura delle regole dalle mappe salvate su file

	private void loadRulesFromCachedMap(File rulesMap, File elemDeclMap, File alternativesMap, File attributesMap) {
				
		logger.info("START loading rules from files");

		FileInputStream fis = null;
		ObjectInputStream in = null;

		// reading rules
		try {
			fis = new FileInputStream(rulesMap);
			in = new ObjectInputStream(new BufferedInputStream(fis));
			rules = (HashMap) in.readObject();
			in.close();
		} catch (Exception ex) {
			logger.error("Error reading rules map " + ex.getMessage(), ex);
		}
		
		// reading elemDeclNames
		try {
			fis = new FileInputStream(elemDeclMap);
			in = new ObjectInputStream(new BufferedInputStream(fis));
			elemDeclNames = (HashMap) in.readObject();
			in.close();
		} catch (Exception ex) {
			logger.error("Error reading elemDeclNames map " + ex.getMessage(), ex);
		}

		// reading alternative_contents
		try {
			fis = new FileInputStream(alternativesMap);
			in = new ObjectInputStream(new BufferedInputStream(fis));
			alternative_contents = (HashMap) in.readObject();
			in.close();
		} catch (Exception ex) {
			logger.error("Error reading alternatives map " + ex.getMessage(), ex);
		}

		// reading attributes
		try {
			fis = new FileInputStream(attributesMap);
			in = new ObjectInputStream(new BufferedInputStream(fis));
			attributes = (HashMap) in.readObject();
			in.close();
		} catch (Exception ex) {
			logger.error("Error reading attributes map " + ex.getMessage(), ex);
		}

		logger.info("END loading rules from files");

	}

	// scrittura delle regole su mappe salvate su file

	private void saveRulesOnCachedMap(File rulesMap, File elemDeclMap, File alternativesMap, File attributesMap) {
		
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		// saving rules
		try {
			fos = new FileOutputStream(rulesMap);
			out = new ObjectOutputStream(new BufferedOutputStream(fos));
			out.writeObject(rules);
			out.close();
		} catch (Exception ex) {
			logger.error("Error saving rules map " + ex.getMessage(), ex);
		}
		
		// saving elemDeclNames
		try {
			fos = new FileOutputStream(elemDeclMap);
			out = new ObjectOutputStream(new BufferedOutputStream(fos));
			out.writeObject(elemDeclNames);
			out.close();
		} catch (Exception ex) {
			logger.error("Error saving elemDeclNames map " + ex.getMessage(), ex);
		}


		// saving alternative_contents
		try {
			fos = new FileOutputStream(alternativesMap);
			out = new ObjectOutputStream(new BufferedOutputStream(fos));
			out.writeObject(alternative_contents);
			out.close();
		} catch (Exception ex) {
			logger.error("Error saving alternatives map " + ex.getMessage(), ex);
		}

		// saving attributes
		try {
			fos = new FileOutputStream(attributesMap);
			out = new ObjectOutputStream(new BufferedOutputStream(fos));
			out.writeObject(attributes);
			out.close();
		} catch (Exception ex) {
			logger.error("Error saving attributes map " + ex.getMessage(), ex);
		}	
	}
	
	//
	//
	//
	////////////////////   FINE   INIZIALIZZAZIONE SCHEMA   /////////////////////////
	
	
	
	public boolean assessAttribute(Node node, String attributeName){
		return utilXsd.assessAttribute(node,attributeName);
	}
	
	
	public boolean assess(Node node){
		return utilXsd.assess(node);
	}
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////
	//
	// 				 GESTIONE CONTENUTO DI DEFAULT DI UN ELEMENTO
	//
	//

	/**
	 * Controlla se il ContentGraph contiene un contenuto di default: un cammino fatto
	 * solo di elementi di testo e transizioni vuote dal primo all'ultimo nodo
	 * 
	 * @param graph il ContentGraph
	 * @return <code>Integer.MAX_VALUE</code> se non esiste un cammino, altrimenti
	 *         ritorna la lunghezza del cammino minimo
	 */
	static protected int visitContentGraph(ContentGraph graph) {
		
		
		// init visit		
		graph.resetVisit();
		ContentGraph.Node first = graph.getFirst();
		ContentGraph.Node last = graph.getLast();


		Vector queue = new Vector();

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
					step = visitContentGraph((ContentGraph) edge);
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



	/**
	 * Restituisce una stringa in formato XML a partire dal contenuto di default
	 * dell'elemento
	 * 
	 * @param graph il ContentGraph dell'elemento
	 */
	static protected String getXMLContent(ContentGraph graph) {

		// get visit path
		Vector path = new Vector();
		ContentGraph.Node nav = graph.getLast();
		while (nav != null) {
			Object edge = nav.getVisitEdge();
			ContentGraph.Node before = nav.getVisitBefore();
			if (before != null && edge instanceof ContentGraph)
				path.add(0, edge);
			nav = before;
		}

		// get xml content
		String output = "<" + graph.getName() + ">";
		for (Iterator i = path.iterator(); i.hasNext();)
			//in questo modo il path non è esploso
			output += getXMLContent((ContentGraph) i.next());
		output = output + "</" + graph.getName() + ">";
		
		return output;
	}

	/**
	 * Restituisce una stringa in formato XML a partire dal contenuto di default
	 * dell'elemento
	 * 
	 * @param graph il ContentGraph dell'elemento
	 */
	static protected Vector getMinPath(ContentGraph graph) {

		Vector path=new Vector(2);
		// get visit path
		Vector nodePath = new Vector();
		Vector edgePath = new Vector();
		ContentGraph.Node nav = graph.getLast();
		while (nav != null) {
			Object edge = nav.getVisitEdge();
			ContentGraph.Node before = nav.getVisitBefore();
			if (before != null && !edge.equals("#EPS") && !edge.equals("#PCDATA")){ //instanceof ContentGraph){//non lo visualizza l'eps
				edgePath.add(0, edge);
				nodePath.add(0,nav);
			}
			nav = before;
		}

		path.add(0,nodePath);
		path.add(1,edgePath);
		return path;
	}

	/**
	 * Sostituisce ogni arco del ContentGraph che rappresenta un elemento, con il suo
	 * ContentGraph. Scende ricorsivamente negli archi gia' esplosi
	 * 
	 * @param graph il ContentGraph da esplodere
	 * @throws RulesManagerException
	 */
	
	
	protected void explodeEdge(ContentGraph edgeToExplode) throws RulesManagerException {
			for (Iterator i = edgeToExplode.visitNodes(); i.hasNext();) {
					ContentGraph.Node sub_src = (ContentGraph.Node) i.next();
					for (int k = 0; k < sub_src.getNoEdges(); k++) {
						Object sub_edge = sub_src.getEdge(k);
						String subedge_name = sub_src.getEdgeName(k);
						// sub_edge is instanceof ContentGraph iif is already exploded!
						//perchè il createContentGraph() crea un grafo con archi di tipo stringa e NON di tipo ContentGraph
						if (sub_edge instanceof ContentGraph) {
							explodeEdge((ContentGraph) sub_edge);
							
						}else if (subedge_name.compareTo("#PCDATA") != 0 && subedge_name.compareTo("#EPS") != 0) {
							// replace edge with ContentGraph of level 1
							sub_src.setEdge(getContentGraph(subedge_name), k);
						} 
					}
					
			}		
	}


	/**
	 * Ritorna una stringa in formato XML che definisce il contenuto di default di un
	 * elemento
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param graph ContentGraph da cui iniziare a definire il contenuto di default
	 * @throws RulesManagerException
	 */
	protected String getDefaultContent(ContentGraph graph) throws RulesManagerException {
	
		int length_visit= visitContentGraph(graph);
		
		System.err.println("-------Lunghezza visita iniziale di "+graph.name+": "+length_visit);
		if (length_visit < Integer.MAX_VALUE){
			return getXMLContent(graph);
		}

		
			// cycle until a default content has been found
		while (true) {			
						
			System.err.println("!   scendo di livello ");

			
			for (Iterator i = graph.visitNodes(); i.hasNext();) {
						
				ContentGraph.Node nodeToExplode = (ContentGraph.Node) i.next();
				
				for (int j = 0; j < nodeToExplode.getNoEdges(); j++) {
					Object edgeToExplode = nodeToExplode.getEdge(j);
					String edge_name = nodeToExplode.getEdgeName(j);
					if (edgeToExplode instanceof ContentGraph) {
						
						explodeEdge((ContentGraph) edgeToExplode);
						
					}else if (edge_name.compareTo("#PCDATA") != 0 && edge_name.compareTo("#EPS") != 0) {
						// replace edge with ContentGraph
						nodeToExplode.setEdge(getContentGraph(edge_name), j);
						
					}
					length_visit= visitContentGraph(graph);
					System.err.println("-------Lunghezza visita di "+nodeToExplode.name+" --> "+edge_name+" = "+length_visit);
					
					if (length_visit < Integer.MAX_VALUE){
						return getXMLContent(graph);
					}
					
				}
				
			}
			
			
		}
	}

	/**
	 * Ritorna una stringa in formato XML che definisce il contenuto di default di un
	 * elemento
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param graph ContentGraph da cui iniziare a definire il contenuto di default
	 * @throws RulesManagerException
	 */
	protected Vector getAlternativeContents(ContentGraph graph) throws RulesManagerException {
		//FIXME: non riesco a fare il clone del grafo originale
		graph.resetVisit();
		System.out.println("grafo su cui cercherà il minimo: \n"+graph);

		ContentGraph newcg=(ContentGraph) graph.clone();
		
//		new ContentGraph(graph.name); //GRAFO CORRENTE A CUI VENGONO SOTTRATTI GLI ARCHI DEL CAMMINO MINIMO PRECEDENTEM TROVATO
//		newcg.nodes_table=new HashMap(graph.nodes_table);

		Vector firstMinPath = new Vector();		
		firstMinPath=findCurrentMinPath(newcg);
		newcg.resetVisit();



		int minPathLength=firstMinPath.size()>0?((Vector) firstMinPath.elementAt(0)).size():0;
		Vector nodesMinPath=new Vector(); //vettore con i nodi del cammino minimo corrente
		Vector edgesMinPath=new Vector(); //vettore con gli archi del cammino minimo corrente
		if(firstMinPath.size()>0){
			nodesMinPath=(Vector) firstMinPath.elementAt(0);
			edgesMinPath=(Vector) firstMinPath.elementAt(1);
		}
		else
			return null;


		ContentGraph.Node toRemove=newcg.getFirst().getDestination(0);//nodo da cui rimuovere gli archi del cammino minimo per cercarne altri

		Vector allAlternatives=new Vector(); //vettore con tutti i cammini minimi alternativi di stessa lunghezza

		allAlternatives.add(firstMinPath);

		int currentMinPathLenght=minPathLength; //lunghezza del cammino minimo corrente
		Vector currentMinPath = new Vector(); //cammino minimo corrente
		Vector checked_edgeoff = new Vector();
		System.out.println("nodi prima alt: "+firstMinPath.elementAt(0));
		System.out.println("edge prima alt: "+firstMinPath.elementAt(1));
		for(int k=0; k< allAlternatives.size();k++){
			System.out.println("alternative per ora: "+allAlternatives.size());
			//per ogni cammino minimo trovato, devo eliminare uno a uno tutti i nodi fino a che non trovo piu cammini minimi
			System.out.println("eliminero' "+minPathLength+" archi");
			for(int i=0; i< minPathLength;i++){
				newcg=(ContentGraph) graph.clone();
				
				System.out.println("grafo su cui cercherà il minimo (come orig): \n"+newcg);

				//per tutti gli archi del cammino minimo corrente
				String nameOfEdgeToRemove=((edgesMinPath.elementAt(i) instanceof ContentGraph)?((ContentGraph)edgesMinPath.elementAt(i)).name:edgesMinPath.elementAt(i).toString());

				//controllo se l'avevo già tolto non lo levo
				if(!checked_edgeoff.contains(nameOfEdgeToRemove)){


					if(!toRemove.removeEdge(nameOfEdgeToRemove, newcg.getNode(((ContentGraph.Node) nodesMinPath.elementAt(i)).name))){
						//non riesco a rimuovere l'arco--caso anomalo
						System.out.println("ANOMALIA su "+graph.name);
						currentMinPathLenght=Integer.MAX_VALUE;
						break;
					}
					System.out.println("eliminato arco "+(i+1)+" di "+minPathLength);
					checked_edgeoff.add(nameOfEdgeToRemove);
					System.out.println("grafo su cui cercherà il minimo (potato): \n"+newcg);
					currentMinPath=findCurrentMinPath(newcg);
					System.out.println("nodi trovata alt: "+currentMinPath.elementAt(0));
					System.out.println("edge trovata alt: "+currentMinPath.elementAt(1));
					currentMinPathLenght=((Vector) currentMinPath.elementAt(0)).size();

					newcg.resetVisit();

					if(currentMinPath!=null && currentMinPath.size()>0 && currentMinPathLenght==minPathLength){//((Vector) currentMinPath.elementAt(0)).size()==minPathLength /*&& !currentMinPath.elementAt(1).equals(edgesMinPath)*/){
						//TROVATO ALTRO CAMMINO MINIMO
						allAlternatives.add(currentMinPath);

					}
					toRemove.addEdge(nameOfEdgeToRemove, newcg.getNode(((ContentGraph.Node) nodesMinPath.elementAt(i)).name));

//					else{
//					//NON ESISTE CAMMINO MINIMO QUINDI RIPOPOLO IL GRAFO E CONTINUO CON UN ALTRO MINIMO EVENTUALE ALTERNATIVO
//					newcg=new ContentGraph(graph.name);
//					newcg.nodes_table=new HashMap(graph.nodes_table);
//					//						System.out.println("STOP!!");
//					break;
//					}


				}
				toRemove=((ContentGraph.Node) nodesMinPath.elementAt(i)); //IL NODO DA CUI TOGLIERE GLI ARCHI SI SPOSTA NEL CAMMINO
			}
			if((k+1)<allAlternatives.size()){
				//SE PRECEDENT E' STATO TROVATO UN MINIMO ALTERNATIVO IL NODO DA CUI RIMUOVERE GLI ARCHI DIVENTA IL PRIMO DI TALE CAMMINO
				nodesMinPath=(Vector)((Vector) allAlternatives.elementAt(k+1)).elementAt(0);
				edgesMinPath=(Vector)((Vector) allAlternatives.elementAt(k+1)).elementAt(1);
				toRemove=newcg.getFirst().getDestination(0);
			}
		}


		if (minPathLength==0){
			System.out.print("alternatives for "+graph.name+"  SIZE 0");
			return null;
		}
		System.out.print("alternatives for "+graph.name+"  SIZE "+allAlternatives.size());
		for(int i=0; i< allAlternatives.size();i++){
			System.out.print("\n------- ALT. "+(i+1)+"  ");
			Vector currentAlternative=(Vector) allAlternatives.elementAt(i);
			Vector edgesCurrentAlternatives=(Vector) currentAlternative.elementAt(1);
			for(int j=0; j< minPathLength;j++){

				System.out.print(((edgesCurrentAlternatives.elementAt(j) instanceof ContentGraph)?((ContentGraph)edgesCurrentAlternatives.elementAt(j)).name:edgesCurrentAlternatives.elementAt(j))+",");
			}
		}
		return allAlternatives;


	}

	protected Vector findCurrentMinPath(ContentGraph graph) throws RulesManagerException {

		Vector currentMinPath = new Vector();
		getDijkstraShortestPath(graph);
		currentMinPath=getMinPath(graph);
		return currentMinPath;

	}


	/**
	 * Controlla se il ContentGraph contiene un contenuto di default: un cammino fatto
	 * solo di elementi di testo e transizioni vuote dal primo all'ultimo nodo
	 * 
	 * @param graph il ContentGraph
	 * @return <code>Integer.MAX_VALUE</code> se non esiste un cammino, altrimenti
	 *         ritorna la lunghezza del cammino minimo
	 */
	static protected Vector getDijkstraShortestPath(ContentGraph graph) {
		ContentGraph.Node first = graph.getFirst();
		ContentGraph.Node last = graph.getLast();

		// init visit
		int distance=Integer.MAX_VALUE;
		Vector predecessors=new Vector();

		//the set of unsettled vertices
		Vector queue = new Vector();
		//the set of settled vertices, the vertices whose shortest distances from the source have been found
		Vector settledNodes = new Vector();

		for (Iterator i = graph.visitNodes(); i.hasNext();)
			((ContentGraph.Node) i.next()).resetVisit();


		queue.add(first);
		first.setVisit(0, "", null);

		// visit the graph
		while (queue.size() > 0) {
			// pop first element from queue
			ContentGraph.Node tovisit = extract_minimum(queue);
			settledNodes.add(tovisit);

			// for all the outgoing edges relax_neighbours(toVisit)
			for (int i = 0; i < tovisit.getNoEdges(); i++) {
				// check if the edge is visitable
				Object edge = tovisit.getEdge(i);
				String edgename = tovisit.getEdgeName(i);
				ContentGraph.Node destination = tovisit.getDestination(i);
				if(!settledNodes.contains(destination)){
					int visit_length = tovisit.getVisitLength();
					if (destination.getVisitLength() > visit_length+1) {
						// push the next node of the path in the queue
						destination.setVisit(visit_length+1, edge, tovisit);
						queue.add(destination);
						predecessors.add(tovisit);
					}
				}

			}
		}

		return predecessors;
	}


	private static ContentGraph.Node extract_minimum(Vector queue) {
		int min_length=Integer.MAX_VALUE;
		int min_node_index=-1;
		for(int i=0; i<queue.size();i++){
			int current_length = ((ContentGraph.Node)queue.elementAt(i)).visit_length;
			if(current_length<min_length){
				min_length=current_length;
				min_node_index=i;
			}
		}
		if (min_length<Integer.MAX_VALUE){
			ContentGraph.Node toRemove=(ContentGraph.Node) queue.elementAt(min_node_index);
			queue.removeElementAt(min_node_index);
			return toRemove;
		}

		return null;
	}



	/**
	 * Ritorna una stringa in formato XML che definisce il contenuto di default di un
	 * elemento
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @throws RulesManagerException
	 */
	public String getDefaultContent(String elem_name) throws RulesManagerException {
		if (elem_name.compareTo("#PCDATA") == 0)
			return "";
		
		return getDefaultContent(getContentGraph(elem_name));
	}




	/**
	 * Trasforma la rappresentazione stringa di un contenuto alternativo nella sua
	 * rappresentazione come ContentGraph
	 * 
	 * @param alternative un contenuto alternativo di un elemento
	 * @return la sua rappresentazione come ContentGraph
	 */
	protected ContentGraph createAlternativeContentGraph(String elem_name, String alternative) {

		ContentGraph graph = new ContentGraph(elem_name);
		String[] tokens = alternative.split(","); // regex for the comma mark

		ContentGraph.Node src = graph.getFirst();
		for (int i = 0; i < tokens.length; i++) {
			ContentGraph.Node dst = graph.addNode();
			src.addEdge(tokens[i], dst);
			src = dst;
		}
		src.addEdge("#EPS", graph.getLast());

		return graph;
	}


	/**
	 * Ritorna una stringa in formato XML che definisce il contenuto di default di un
	 * elemento
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param alternative uno dei possibili contenuti alternativi dell'elemento
	 * @throws RulesManagerException
	 */
	public String getDefaultContent(String elem_name, String alternative) throws RulesManagerException {
		if (elem_name.compareTo("#PCDATA") == 0)
			return "";
		
		if (rules.get(elem_name) == null)
		   throw new RulesManagerException("No rule for element <" + elem_name + ">");

		// init computation, get content of this alternative
		return getDefaultContent(createAlternativeContentGraph(elem_name, alternative));
	}


	/**
	 * SCHEMA Implementation 
	 * 
	 * Ritorna i possibili contenuti alternativi di un elemento
	 * 
	 * @throws RulesManagerException
	 */
	public Vector getAlternativeContents(String elem_name) throws RulesManagerException {
		if (elem_name.compareTo("#PCDATA") == 0)
			return new Vector();
		Vector alternatives =  getAlternativeContents(getContentGraph(elem_name));
		if (alternatives == null)
			throw new RulesManagerException("No alternative contents for element <" + elem_name + ">");
		return alternatives;
	}

	/**
	 * Crea un ContentGraph che rappresenta un elemento di testo
	 * 
	 * @param elem_name il nome dell'elemento
	 */
	protected ContentGraph createTextContentGraph(String elem_name) {
		ContentGraph graph = new ContentGraph(elem_name);
		graph.getFirst().addEdge("#PCDATA", graph.getLast());
		return graph;
	}



	/**
	 * Ritorna il content graph associato ad un elemento    (VERSIONE BASATA SU SCHEMA)
	 * 
	 * @param elem_name il nome dell'elemento
	 * @throws RulesManagerException
	 */
	protected ContentGraph getContentGraph(String elem_name) throws RulesManagerException {
		
		
		if (queryTextContent(elem_name)) // don't explode text and mixed elements to save iterations
			return createTextContentGraph(elem_name);

		if (rules.get(elem_name) == null)
		   throw new RulesManagerException("No rule for element <" + elem_name + ">");
		return utilXsd.createContentGraph(elem_name);
	}

	//
	//			     FINE   GESTIONE   DEFAULT E ALTERNATIVE CONTENT
	//					
	///////////////////////////////////////////////////////////////////////////////////
	
	

	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////
	//
	//					 QUERY SUL CONTENUTO DI UN ELEMENTO 
	//
	//
	
	/**
	 * Controlla se una collezione di nomi di elementi puo rappresentare un insieme di
	 * figli di un nodo
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param elem_children la collezione dei nomi dei figli
	 * @throws RulesManagerException
	 */
	public boolean isValid(String elem_name, Collection elem_children) throws RulesManagerException {
		return isValid(elem_name, elem_children, false);
	}

	/**
	 * Controlla se una collezione di nomi di elementi puo rappresentare un insieme di
	 * figli di un nodo
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param elem_children la collezione dei nomi dei figli
	 * @param with_gaps <code>true</code> se l'allineamento puo' essere fatto con gaps
	 * @throws RulesManagerException
	 */
	public boolean isValid(String elem_name, Collection elem_children, boolean with_gaps) throws RulesManagerException {		
		return utilXsd.isValid(elem_name, elem_children, with_gaps);
	}

	
	/**
	 * SCHEMA IMPLEMENTATION
	 * 
	 * Enumera le alternative possibili dati il nodo padre ed i nomi dei nodi figli.
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param elem_children la collezione dei nomi dei figli
	 * @param choice_point la posizione nella sequenza di figli dopo la quale valutare le
	 *            alternative
	 * @throws RulesManagerException
	 */
	public Collection getAlternatives(String elem_name, Collection elem_children, int choice_point) throws RulesManagerException {

		if (rules.get(elem_name) == null)
		   throw new RulesManagerException("No rule for element <" + elem_name + ">");

		return utilXsd.getAlternatives(elem_name, elem_children, choice_point);
	}

	/**
	 * Ritorna un vettore di stringhe contenente il nome dei figli di un nodo I nodi che
	 * non sono testo o elementi vengono ignorati
	 * 
	 * @param node il nodo padre
	 * @throws RulesManagerException
	 */
	public Vector getChildren(Node node) throws RulesManagerException {
		if (node.getNodeType() != Node.ELEMENT_NODE)
			return new Vector();

		Vector str_children = new Vector();
		NodeList dom_children = node.getChildNodes();
		for (int i = 0; i < dom_children.getLength(); i++) {
			Node dom_child = dom_children.item(i);
			if (dom_child.getNodeType() == Node.ELEMENT_NODE)
				str_children.add(dom_child.getNodeName());
			else if (dom_child.getNodeType() == Node.TEXT_NODE) {
				str_children.add(new String("#PCDATA"));
			} else
				str_children.add(new String("#ANY")); // comment and processing/ instructions can go everywhere
		}
		return str_children;
	}


	/**
	 * Ritorna il nome di un nodo
	 * 
	 * @param dom_node il nodo di cui si richiede il nome
	 * @throws RulesManagerException
	 */
	public String getNodeName(Node dom_node) throws RulesManagerException {		
		if (dom_node.getNodeType() == Node.ELEMENT_NODE)
			return dom_node.getNodeName();
		if (dom_node.getNodeType() == Node.TEXT_NODE)
			return new String("#PCDATA");
		return new String("#ANY");
		
	}

	/**
	 * Ritorna l'indice di un nodo all'interno del padre
	 * 
	 * @param parent il nodo padre
	 * @param child il nodo figlio
	 * @throws RulesManagerException
	 */
	public int getChildIndex(Node parent, Node child) throws RulesManagerException {
		int child_index = 0;
		NodeList dom_children = parent.getChildNodes();
		for (int i = 0; i < dom_children.getLength(); i++) {
			Node dom_child = dom_children.item(i);
			/*
			 * if( dom_child.getNodeType() == dom_child.TEXT_NODE ) { // skip empty text
			 * nodes String value = UtilLang.trimText(dom_child.getNodeValue()); if(
			 * value==null || value.length()==0 ) continue; }
			 */
			if (dom_child == child)
				return child_index;
			child_index++;
		}
		throw new RulesManagerException("Cannot find the child element in the children list");
	}

	

	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//
	//
	/// DA QUI IN POI TUTTE LE QUERY SONO SOSTITUITE CON QUELLE DELLO SCHEMA
	//
	//
	//
	//
	//
	//
	
	/**
	 * Controlla se il contenuto di un nodo &egrave valido
	 * 
	 * @param dom_node il nodo da validare
	 * @throws RulesManagerException
	 */
	public boolean queryIsValid(Node dom_node) throws RulesManagerException {
		if (dom_node == null)
			throw new RulesManagerException("node is null");
		if (dom_node.getNodeType() != Node.ELEMENT_NODE)
			return true;
		return isValid(getNodeName(dom_node), getChildren(dom_node));
	}

	/**
	 * Controlla se un nodo pu&ograve contenere del testo
	 * 
	 * @param dom_node il nodo in esame
	 * @throws RulesManagerException
	 */
	public boolean queryTextContent(Node dom_node) throws RulesManagerException {
		if (dom_node == null)
			throw new RulesManagerException("node is null");
		if (dom_node.getNodeType() != Node.ELEMENT_NODE)
			return true;
		return queryTextContent(getNodeName(dom_node));
		// MODIFICA (Tommaso) 10-1-2008
		//return isValid(getNodeName(dom_node), UtilLang.singleton("#PCDATA"));
	}

	
//	/**
//	 * Controlla se un nodo pu&ograve contenere del testo
//	 * 
//	 * @param elem_name il nome del nodo in esame
//	 * @throws RulesManagerException
//	 */
//	public boolean queryTextContent(String elem_name) throws RulesManagerException {
//		if (elem_name.compareTo("#PCDATA") == 0)
//			return true;
//		return isValid(elem_name, UtilLang.singleton("#PCDATA"));
//	}
	
	
	
	
	/**
	 * Controlla se un nodo pu&ograve contenere del testo
	 * 
	 * @param elem_name il nome del nodo in esame
	 * @throws RulesManagerException
	 */
	public boolean queryTextContent(String elem_name) throws RulesManagerException {
		if (elem_name.compareTo("#PCDATA") == 0){
			return true;
		}
		if(rules.get(elem_name)==null){   // modifica per l'elemento <p> presente sui documenti METALEX e non dichiarato nell'xsd
			return false;
		}
		if (isValid(elem_name, UtilLang.singleton("#PCDATA"))){
			//System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% "+elem_name +" is singleton #PCDATA");
			return true;
		}
		if (utilXsd.isSimpleType(elem_name)){
		//if (isValid(elem_name,new Vector())){
			System.err.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% "+elem_name +" is LEAF");
			return true;
		}
		return false;
	}

	/**
	 * Controlla se &egrave possibile appendere un nodo ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_node il nodo da appendere
	 * @throws RulesManagerException
	 */
	public boolean queryCanAppend(Node parent, Node new_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (new_node == null)
			throw new RulesManagerException("node to append is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		str_children.add(getNodeName(new_node));
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Controlla se &egrave possibile appendere una collezione di nodi ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_nodes i nodi da appendere
	 * @throws RulesManagerException
	 */
	public boolean queryCanAppend(Node parent, Collection new_nodes) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");

		int c = 0;
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		for (Iterator i = new_nodes.iterator(); i.hasNext(); c++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new RulesManagerException("node #" + c + " to append is null");
			str_children.add(getNodeName(new_node));
		}

		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Ritorna i possibili nodi da appendere ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @throws RulesManagerException
	 */
	public Collection queryAppendable(Node parent) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		return getAlternatives(getNodeName(parent), str_children, str_children.size() - 1);
	}

	/**
	 * Controlla se &egrave possibile pre-pendere un nodo ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_node il nodo da pre-pendere
	 * @throws RulesManagerException
	 */
	public boolean queryCanPrepend(Node parent, Node new_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (new_node == null)
			throw new RulesManagerException("node to prepend is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		str_children.insertElementAt(getNodeName(new_node), 0);
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Controlla se &egrave possibile pre-pendere una collezione di nodi ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_nodes i nodi da pre-pendere
	 * @throws RulesManagerException
	 */
	public boolean queryCanPrepend(Node parent, Collection new_nodes) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");

		int inserted = 0;
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new RulesManagerException("node #" + inserted + " to prepend is null");
			str_children.insertElementAt(getNodeName(new_node), inserted);
		}

		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Ritorna i possibili nodi da pre-pendere ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @throws RulesManagerException
	 */
	public Collection queryPrependable(Node parent) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		return getAlternatives(getNodeName(parent), str_children, -1);
	}

	/**
	 * Controlla se &egrave possibile inserire un nodo dopo un certo figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio dopo cui inserire il nuovo nodo
	 * @param new_node il nodo da inserire
	 * @throws RulesManagerException
	 */
	public boolean queryCanInsertAfter(Node parent, Node child_node, Node new_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");
		if (new_node == null)
			throw new RulesManagerException("node to insert is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		str_children.insertElementAt(getNodeName(new_node), child_index + 1);

		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Controlla se &egrave possibile inserire una collezione di nodi dopo un certo figlio
	 * di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio dopo cui inserire il nuovo nodo
	 * @param new_nodes i nodi da inserire
	 * @throws RulesManagerException
	 */
	public boolean queryCanInsertAfter(Node parent, Node child_node, Collection new_nodes) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");

		int inserted = 0;
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new RulesManagerException("node #" + inserted + " to insert is null");
			str_children.insertElementAt(getNodeName(new_node), child_index + inserted + 1);
		}

		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Ritorna i nodi che &egrave possibile inserire dopo un certo figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio dopo cui inserire il nuovo nodo
	 * @throws RulesManagerException
	 */
	public Collection queryInsertableAfter(Node parent, Node child_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		return getAlternatives(getNodeName(parent), str_children, child_index);
	}

	/**
	 * Controlla se &egrave possibile inserire un nodo prima di un certo figlio di un
	 * elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio prima di cui inserire il nuovo nodo
	 * @param new_node il nodo da inserire
	 * @throws RulesManagerException
	 */
	public boolean queryCanInsertBefore(Node parent, Node child_node, Node new_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");
		if (new_node == null)
			throw new RulesManagerException("node to insert is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		str_children.insertElementAt(getNodeName(new_node), child_index);
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Controlla se &egrave possibile inserire una collezione di nodi prima di un certo
	 * figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio prima di cui inserire il nuovo nodo
	 * @param new_nodes i nodi da inserire
	 * @throws RulesManagerException
	 */
	public boolean queryCanInsertBefore(Node parent, Node child_node, Collection new_nodes) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");

		int inserted = 0;
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new RulesManagerException("node #" + inserted + " to insert is null");
			str_children.insertElementAt(getNodeName(new_node), child_index + inserted);
		}

		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Ritorna i nodi che &egrave possibile inserire prima di un certo figlio di un
	 * elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio prima di cui inserire il nuovo nodo
	 * @throws RulesManagerException
	 */
	public Collection queryInsertableBefore(Node parent, Node child_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		return getAlternatives(getNodeName(parent), str_children, child_index - 1);
	}

	private Vector extractSubList(Vector list, int first, int size) {
		Vector sublist = new Vector();
		for (int i = 0; i < size; i++) {
			sublist.add(list.elementAt(first));
			list.removeElementAt(first);
		}
		return sublist;
	}

	/**
	 * Controlla se &egrave possibile racchiudere un insieme di figli di un elemento in un
	 * altro elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il primo figlio da racchiudere
	 * @param no_children il numero di figli da racchiudere
	 * @param new_node il nodo contenitore
	 * @throws RulesManagerException
	 */
	public boolean queryCanEncloseIn(Node parent, Node child_node, int no_children, Node new_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");
		if (new_node == null)
			throw new RulesManagerException("node to insert is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		// find the first child
		int child_index = getChildIndex(parent, child_node);

		// get the children to enclose
		Vector toenclose = extractSubList(str_children, child_index, no_children);

		// check if the container node can enclose the children
		if (!isValid(getNodeName(new_node), toenclose, true))
			return false;

		// replace the children to enclose with the container node
		str_children.insertElementAt(getNodeName(new_node), child_index);

		// check if the parent can contain this new set of children
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Ritorna gli elementi in cui &egrave possibile racchiudere un insieme di figli di un
	 * elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il primo figlio da racchiudere
	 * @param no_children il numero di figli da racchiudere
	 * @return gli elementi contenitore
	 * @throws RulesManagerException
	 */
	public Collection queryContainers(Node parent, Node child_node, int no_children) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		// find the first child
		int child_index = getChildIndex(parent, child_node);

		// get the children to enclose
		Vector toenclose = extractSubList(str_children, child_index, no_children);

		// get all the nodes that can be inserted in the position of the first
		// child
		Collection insertable = getAlternatives(getNodeName(parent), str_children, child_index - 1);

		// check which of the alternatives can contain the children
		Vector containers = new Vector();
		for (Iterator i = insertable.iterator(); i.hasNext();) {
			String elem = (String) i.next();
			if (isValid(elem, toenclose, true))
				containers.add(elem);
		}
		return containers;
	}

	/**
	 * Controlla se &egrave possibile racchiudere una parte di testo di un elemento in un
	 * altro elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il nodo testo da racchiudere
	 * @param new_node il nodo contenitore
	 * @throws RulesManagerException
	 */
	public boolean queryCanEncloseTextIn(Node parent, Node child_node, Node new_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");
		if (child_node.getNodeType() != Node.TEXT_NODE)
			throw new RulesManagerException("Cannot enclose text: child node is not a text node");
		if (new_node == null)
			throw new RulesManagerException("node to insert is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		// find the text node
		int child_index = getChildIndex(parent, child_node);

		// check if the container node can contain text
		if (!isValid(getNodeName(new_node), UtilLang.singleton("#PCDATA")))
			return false;

		// insert the container node inside the text
		str_children.insertElementAt(getNodeName(new_node), child_index + 1);
		str_children.insertElementAt("#PCDATA", child_index + 2);

		// check if the parent can contain this new set of children
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Ritorna gli elementi in cui &egrave possibile racchiudere una parte di testo di un
	 * elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il nodo di testo da racchiudere
	 * @return gli elementi contenitore
	 * @throws RulesManagerException
	 */
	public Collection queryTextContainers(Node parent, Node child_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		// find the text node
		int child_index = getChildIndex(parent, child_node);
		str_children.insertElementAt("#PCDATA", child_index + 1);

		// get all the nodes that can be inserted in the text
		Collection insertable = getAlternatives(getNodeName(parent), str_children, child_index);

		// check which of the alternatives can contain text
		Vector containers = new Vector();
		for (Iterator i = insertable.iterator(); i.hasNext();) {
			String elem = (String) i.next();
			if (isValid(elem, UtilLang.singleton("#PCDATA")))
				containers.add(elem);
		}

		return containers;
	}

	/**
	 * Controlla se &egrave possibile sostituire un insieme di figli di un elemento con un
	 * altro insieme di elementi
	 * 
	 * @param parent il nodo padre
	 * @param child_node il primo figlio da racchiudere
	 * @param no_children il numero di figli da racchiudere
	 * @param new_nodes gli elementi con cui sostituire i figli specificati
	 * @throws RulesManagerException
	 */
	public boolean queryCanReplaceWith(Node parent, Node child_node, int no_children, Collection new_nodes) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");

		// get the children
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		// find the first child
		int child_index = getChildIndex(parent, child_node);

		// replace the children to enclose with the new nodes
		int inserted = 0;
		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new RulesManagerException("node #" + inserted + " to insert is null");
			str_children.insertElementAt(getNodeName(new_node), child_index + inserted);
		}

		// check if the parent can contain this new set of children
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Controlla se &egrave possibile sostituire una parte di testo con un insieme di
	 * elementi
	 * 
	 * @param parent il nodo padre
	 * @param child_node il nodo testo da racchiudere
	 * @param new_nodes gli elementi con cui sostituire il testo specificato
	 * @throws RulesManagerException
	 */
	public boolean queryCanReplaceTextWith(Node parent, Node child_node, Collection new_nodes) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");
		if (child_node.getNodeType() != Node.TEXT_NODE)
			throw new RulesManagerException("Cannot enclose text: child node is not a text node");

		// get the children
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		// find the text node
		int child_index = getChildIndex(parent, child_node);

		// insert the new nodes inside the text
		int inserted = 0;
		str_children.insertElementAt("#PCDATA", child_index + 1);
		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new RulesManagerException("node #" + inserted + " to insert is null");
			str_children.insertElementAt(getNodeName(new_node), child_index + inserted);
		}

		// check if the parent can contain this new set of children
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Controlla se &egrave possibile eliminare un certo figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio da eliminare
	 * @throws RulesManagerException
	 */
	public boolean queryCanDelete(Node parent, Node child_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		str_children.removeElementAt(child_index);
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Controlla se &egrave possibile eliminare un certo insieme di figli di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il primo figlio da eliminare
	 * @param no_children il numero di figli da eliminare
	 * @throws RulesManagerException
	 */
	public boolean queryCanDelete(Node parent, Node child_node, int no_children) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int from_index = getChildIndex(parent, child_node);
		for (int i = 0; i < no_children; i++) {
			if (str_children.size() == from_index)
				throw new RulesManagerException("invalid number of children to remove");
			str_children.removeElementAt(from_index);
		}

		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Controlla se &egrave possibile inserire un nodo all'interno di un certo figlio di
	 * un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio in cui inserire il nuovo nodo, deve essere un nodo
	 *            testo
	 * @param new_node il nodo da inserire
	 * @throws RulesManagerException
	 */
	public boolean queryCanInsertInside(Node parent, Node child_node, Node new_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");
		if (new_node == null)
			throw new RulesManagerException("node to insert is null");
		if (child_node.getNodeType() != Node.TEXT_NODE)
			throw new RulesManagerException("Cannot insert inside: child node is not a text node");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		str_children.insertElementAt(getNodeName(new_node), child_index + 1);
		str_children.insertElementAt(new String("#PCDATA"), child_index + 2);
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Controlla se &egrave possibile inserire una collezione di nodi all'interno di un
	 * certo figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio in cui inserire il nuovo nodo, deve essere un nodo
	 *            testo
	 * @param new_nodes i nodi da inserire
	 * @throws RulesManagerException
	 */
	public boolean queryCanInsertInside(Node parent, Node child_node, Collection new_nodes) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");
		if (child_node.getNodeType() != Node.TEXT_NODE)
			throw new RulesManagerException("Cannot insert inside: child node is not a text node");

		int inserted = 0;
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new RulesManagerException("node #" + inserted + " to insert is null");
			str_children.insertElementAt(getNodeName(new_node), child_index + inserted + 1);
		}
		str_children.insertElementAt(new String("#PCDATA"), child_index + inserted + 1);
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Ritorna i nodi che &egrave possibile inserire all'interno di un certo figlio di un
	 * elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio in cui inserire il nuovo nodo, deve essere un nodo
	 *            testo
	 * @throws RulesManagerException
	 */
	public Collection queryInsertableInside(Node parent, Node child_node) throws RulesManagerException {
		if (parent == null)
			throw new RulesManagerException("parent is null");
		if (child_node == null)
			throw new RulesManagerException("child is null");
		if (child_node.getNodeType() != Node.TEXT_NODE)
			throw new RulesManagerException("Cannot insert inside: child node is not a text node");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new RulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		if (((String) str_children.get(child_index)).compareTo("#PCDATA") != 0)
			throw new RulesManagerException("Cannot insert inside: child node is not a text node");
		str_children.insertElementAt(new String("#PCDATA"), child_index + 1);
		return getAlternatives(getNodeName(parent), str_children, child_index);
	}

	
	//
	//				END QUERY SU ELEMENTI
	//
	////////////////////////////////////////////////////////////////////////////////
	//
	//
	//
	// 				 QUERY SUGLI ATTRIBUTI DI UN ELEMENTO 
    //
	//
	//
	private AttributeDeclaration getAttributeDeclaration(String elem_name, String att_name) throws RulesManagerException {
		if (elem_name == "#PCDATA")
			throw new RulesManagerException("No attributes for element <" + elem_name + ">");
		if (!rules.containsKey(elem_name))
			throw new RulesManagerException("No rule for element <" + elem_name + ">");

		HashMap att_hash = (HashMap) attributes.get(elem_name);
		if (att_hash == null)
			throw new RulesManagerException("No attributes for element <" + elem_name + ">");

		AttributeDeclaration att_decl = (AttributeDeclaration) att_hash.get(att_name);
		if (att_decl == null)
			throw new RulesManagerException("No attribute <" + att_name + "> for element <" + elem_name + ">");
		return att_decl;
	}

	/**
	 * Ritorna la lista dei nomi degli attributi di un elemento
	 * 
	 * @param elem_name il nome dell'elemento di cui si vuole la lista degli attributi
	 * @throws RulesManagerException
	 */
	public Collection queryGetAttributes(String elem_name) throws RulesManagerException {
		if (elem_name == "#PCDATA")
			return new Vector();
		if (!rules.containsKey(elem_name))
			throw new RulesManagerException("No rule for element <" + elem_name + ">");

		HashMap att_hash = (HashMap) attributes.get(elem_name);
		if (att_hash == null)
			return new Vector(); // no attributes for element

		return att_hash.keySet();
	}

	/**
	 * Ritorna il valore di default (se esiste) di un attributo di un elemento
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede il valore di default
	 *            dell'attributo
	 * @param att_name il nome dell'attributo
	 * @return la stringa vuota se non esiste il valore di default
	 * @throws RulesManagerException
	 */
	public String queryGetAttributeDefaultValue(String elem_name, String att_name) throws RulesManagerException {
		AttributeDeclaration att_decl = getAttributeDeclaration(elem_name, att_name);
		if (att_decl.value == null) {
			if (att_decl.type.startsWith("(") && att_decl.type.endsWith(")")) {
				// tokenized value
				String[] values = att_decl.type.substring(1, att_decl.type.length() - 1).split("\\|");
				if (values.length > 0)
					return values[0];
			}
			return "";
		}
		return att_decl.value;
	}

	/**
	 * Ritorna i valori possibili per un attributo
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede il valore di default
	 *            dell'attributo
	 * @param att_name il nome dell'attributo
	 * @return <code>null</code> se l'attributo puo' avere qualsiasi valore
	 * @throws RulesManagerException
	 */
	public Collection queryGetAttributePossibleValues(String elem_name, String att_name) throws RulesManagerException {
		AttributeDeclaration att_decl = getAttributeDeclaration(elem_name, att_name);
		if (att_decl.type.startsWith("(") && att_decl.type.endsWith(")"))
			return java.util.Arrays.asList(att_decl.type.substring(1, att_decl.type.length() - 1).split("\\|"));
		return null;
	}

	/**
	 * Ritorna <code>true</code> se esiste l'attributo specificato per questo elemento
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede l'esistena dell'attributo
	 * @param att_name il nome dell'attributo
	 * @throws RulesManagerException
	 */
	public boolean queryIsValidAttribute(String elem_name, String att_name) throws RulesManagerException {
		if (elem_name == "#PCDATA")
			throw new RulesManagerException("No attributes for element <" + elem_name + ">");
		if (!rules.containsKey(elem_name))
			throw new RulesManagerException("No rule for element <" + elem_name + ">");
		return (attributes.containsKey(elem_name) && ((HashMap) attributes.get(elem_name)).containsKey(att_name));
	}

	/**
	 * Ritorna <code>true</code> se l'attributo specificato &egrave di tipo
	 * <code>#REQUIRED</code>
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede l'esistena dell'attributo
	 * @param att_name il nome dell'attributo
	 * @throws RulesManagerException
	 */
	public boolean queryIsRequiredAttribute(String elem_name, String att_name) throws RulesManagerException {
		AttributeDeclaration att_decl = getAttributeDeclaration(elem_name, att_name);
		return ((att_decl.valueDefault != null) && (att_decl.valueDefault.equalsIgnoreCase("#REQUIRED") || att_decl.valueDefault.equalsIgnoreCase("#FIXED") 
				|| att_decl.valueDefault.equalsIgnoreCase("required") || att_decl.valueDefault.equalsIgnoreCase("fixed")));
	}

	/**
	 * Ritorna <code>true</code> se l'attributo specificato &egrave di tipo
	 * <code>#FIXED</code>
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede l'esistena dell'attributo
	 * @param att_name il nome dell'attributo
	 * @throws RulesManagerException
	 */
	public boolean queryIsFixedAttribute(String elem_name, String att_name) throws RulesManagerException {
		AttributeDeclaration att_decl = getAttributeDeclaration(elem_name, att_name);
		return (att_decl.valueDefault == "#FIXED" || att_decl.valueDefault.equalsIgnoreCase("fixed"));
	}

	/**
	 * Ritorna <code>true</code> se l'attributo specificato per questo elemento
	 * pu&ograve assumere il valore richiesto
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede la validit&agrave del
	 *            valore dell'attributo
	 * @param att_name il nome dell'attributo
	 * @param value il valore dell'attributo
	 * @throws RulesManagerException
	 */
	public boolean queryIsValidAttributeValue(String elem_name, String att_name, String value) throws RulesManagerException {
		AttributeDeclaration att_decl = getAttributeDeclaration(elem_name, att_name);
		if (att_decl.valueDefault == "#FIXED" || att_decl.valueDefault.equalsIgnoreCase("fixed")) {
			// fixed value
			return (att_decl.value.compareTo(value) == 0);
		}
		if (att_decl.type.startsWith("(") && att_decl.type.endsWith(")")) {
			// tokenized value
			String[] values = att_decl.type.substring(1, att_decl.type.length() - 1).split("\\|");
			for (int i = 0; i < values.length; i++) {
				if (values[i].compareTo(value) == 0)
					return true;
			}
			return false;
		}

		// unchecked
		return true;
	}

	/**
	 * Riempie il nodo con gli attributi necessari
	 * 
	 * @param elem il nodo di cui si vogliono settare gli attributi
	 * @throws RulesManagerException
	 */
	public void fillRequiredAttributes(Node elem) throws RulesManagerException {
		String elem_name = elem.getNodeName();
		org.w3c.dom.Document doc = elem.getOwnerDocument();
		Collection att_names = queryGetAttributes(elem_name);
		if (att_names == null)
			return;

		org.w3c.dom.NamedNodeMap att_nodes = elem.getAttributes();
		for (Iterator i = att_names.iterator(); i.hasNext();) {
			String att_name = (String) i.next();
			if (queryIsRequiredAttribute(elem_name, att_name)) {
				// the attribute is required
				Node att = att_nodes.getNamedItem(att_name);
				if (att == null) {
					// the attribute is not set, add a new one with the correct
					// value
					org.w3c.dom.Attr new_att = doc.createAttribute(att_name);
					new_att.setValue(queryGetAttributeDefaultValue(elem_name, att_name));
					att_nodes.setNamedItem(new_att);
				}
			}
		}
	}
	
	
	Logger getLogger() {
		return this.logger;
	}


	public String getDefaultContent(String elem_name, Vector nodes) throws RulesManagerException {
		// TODO Auto-generated method stub
		return null;
	}


}
