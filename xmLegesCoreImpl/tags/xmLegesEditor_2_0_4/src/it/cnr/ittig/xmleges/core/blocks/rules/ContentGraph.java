/**
 * Classe che rappresenta il contenuto di un elemento.
 * Viene usata per identificare il contenuto di default.
 */

package it.cnr.ittig.xmleges.core.blocks.rules;

import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.util.lang.Queue;
import it.cnr.ittig.xmleges.core.util.lang.UtilLang;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class ContentGraph implements Serializable {

	/**
	 * Nodo del grafo
	 */
	public class Node implements Serializable {

		/**
		 * Identificativo del nodo
		 */
		String name;

		/**
		 * Vettore di oggetti che rappresentano gli archi uscenti dal nodo Gli oggetti
		 * possono essere delle stringhe o degli altri ContentGraph
		 */
		protected Vector edges;

		/**
		 * Vettore degli id dei nodi destinazione
		 */
		protected Vector destinations;

		/**
		 * Lunghezza del cammino minimo che porta a questo nodo
		 */
		protected int visit_length;

		/**
		 * Valore dell'ultimo arco nel cammino minimo che porta a questo nodo
		 */
		protected Object visit_edge;

		/**
		 * Nodo precedente nel cammino minimo che porta a questo nodo
		 */
		protected Node visit_before;

		/**
		 * Costruttore di default
		 */
		public Node(String id) {
			name = id;
			edges = new Vector();
			destinations = new Vector();
			resetVisit();
		}

		/**
		 * Ritorna l'id del nodo
		 */
		public String getName() {
			return name;
		}

		/**
		 * Ritorna la lunghezza del cammino minimo che porta a questo nodo
		 */
		public int getVisitLength() {
			return visit_length;
		}

		/**
		 * Ritorna l'indice dell'ultimo arco nel cammino minimo che porta a questo nodo
		 */
		public Object getVisitEdge() {
			return visit_edge;
		}

		/**
		 * Ritorna il nodo precedente nel cammino minimo che porta a questo nodo
		 */
		public Node getVisitBefore() {
			return visit_before;
		}

		/**
		 * Setta i valori dell'ultimo passo nel cammino minimo che porta a questo nodo
		 */
		public void setVisit(int length, Object edge, Node before) {
			visit_length = length;
			visit_edge = edge;
			visit_before = before;
		}

		/**
		 * Setta la lunghezza del cammino minimo al valore di default -inf
		 */
		public void resetVisit() {
			visit_length = Integer.MAX_VALUE;
			visit_edge = null;
			visit_before = null;
		}

		/**
		 * Ritorna il numero degli archi uscenti dal nodo
		 */
		public int getNoEdges() {
			return edges.size();
		}

		/**
		 * Ritorna il valore presente sull'arco
		 */
		public Object getEdge(int i) {
			return edges.elementAt(i);
		}
		


		/**
		 * Ritorna il valore presente sull'arco come stringa
		 */
		public String getEdgeName(int i) {
			Object edge = edges.elementAt(i);
			if (edge instanceof String)
				return (String) edge;
			if (edge instanceof ContentGraph)
				return ((ContentGraph) edge).getName();
			return edge.toString();
		}

		/**
		 * Ritorna la destinazione dell'arco
		 */
		public Node getDestination(int i) {
			return (Node) destinations.elementAt(i);
		}
		/**
		 * Ritorna la destinazione dell'arco
		 */
		public Node getDestination(String edgeName) {
			
			for(int i = 0; i< edges.size(); i++){
				if(getEdgeName(i).equals(edgeName)){
					return getDestination(i);
				}			
			}
			return null;
		}

		/**
		 * Aggiunge un arco uscente dal nodo
		 * 
		 * @param edge valore presente sull'arco
		 * @param destination nodo destinazione dell'arco
		 */
		public void addEdge(Object edge, Node destination) {
			edges.add(edge);
			destinations.add(destination);
		}

		/**
		 * Rimuove un arco dal nodo (se presente)
		 * 
		 * @param edge valore presente sull'arco
		 * @param destination nodo destinazione dell'arco
		 */
		public void removeEdge(String edge, Node destination) {
			for (int i = 0; i < edges.size(); i++) {
				if (getDestination(i) == destination && getEdgeName(i) == edge) {
					edges.remove(i);
					destinations.remove(i);
				}
			}
		}

		/**
		 * Modifica il valore di un arco
		 * 
		 * @param edge nuovo valore
		 * @param ind indice dell'arco
		 */
		public void setEdge(Object edge, int ind) {
			edges.setElementAt(edge, ind);
		}

		public String toString() {
			String out = name + "\n";
			for (int i = 0; i < edges.size(); i++) {
				out = out + " - " + getEdgeName(i) + " -> " + getDestination(i).name + "\n";
			}
			return out;
		}
		
		public void setVisit_length(int visit_length) {
			this.visit_length = visit_length;
		}


		public int getVisit_length() {
			return visit_length;
		}

	}

	/**
	 * Nome dell'elemento rappresentato dal grafo
	 */
	protected String name;

	/**
	 * Tabella dei nodi del grafo (nome,puntatore)
	 */
	protected HashMap nodes_table;

	/**
	 * Costruttore di default
	 */
	public ContentGraph(String _name) {
		name = _name;
		nodes_table = new HashMap();

		// crea i nodi inizio e fine
		addNode("S");
		addNode("E");
	}

	/**
	 * Ritorna il nome del grafo
	 */
	public String getName() {
		return name;
	}

	/**
	 * Ritorna il nodo di inizio del grafo
	 */
	public Node getFirst() {
		return getNode("S");
	}

	/**
	 * Ritorna il nodo finale del grafo
	 */
	public Node getLast() {
		return getNode("E");
	}
	
	public Object clone(){
		ContentGraph cloned=new ContentGraph(name);
//		cloned.nodes_table=(HashMap) nodes_table.clone();
		for (Iterator i = getNodes_table().entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            cloned.getNodes_table().put(e.getKey(), e.getValue());
		}
		return cloned;
	}


	/**
	 * Setta la lunghezza del cammino minimo per ogni nodo al valore di default -inf
	 */
	public void resetVisit() {
		
		for (Iterator i = this.visitNodes(); i.hasNext();) {
			((Node) i.next()).resetVisit();
		}
	}

	/**
	 * Ritorna la tabella dei nodi
	 */
	public Iterator visitNodes() {
		return nodes_table.values().iterator();
	}

	/**
	 * Ritorna il numero di nodi del grafo
	 */
	public int getNoNodes() {
		return nodes_table.size();
	}

	/**
	 * Ritorna un nodo del grafo
	 */
	public Node getNode(String id) {
		return (Node) nodes_table.get(id);
	}
	
	/**
	 * Ritorna lo shortest path fra due nodi o <code>null</code> se non esiste nessun
	 * path
	 */
	private Vector reach(Node from, Node to) {
		HashMap visited = new HashMap(); // tabella dei nodi visitati con le loro paths

		Queue tovisit_nodes = new Queue(); // nodi da visitare
		Queue tovisit_paths = new Queue(); // path ai nodi da visitare

		// initialize the search
		tovisit_nodes.push(from);
		tovisit_paths.push(new Vector());

		// perform a breadth first search until all the graph have
		// been visited or the destination have been reached
		while (!tovisit_nodes.empty()) {
			// visit the top of the stack
			Node src = (Node) tovisit_nodes.pop();
			Vector path = (Vector) tovisit_paths.pop();

			// if the visiting nodes is the destination stop
			if (src == to)
				return path;

			// if the visiting node has never been visited or
			// its path is shorter than a preceding one, visit
			// its followers
			String name = src.getName();
			Vector last_path = (Vector) visited.get(name);
			if (last_path == null || last_path.size() > path.size()) {
				visited.put(name, path);

				// schedule the following nodes for a visit
				
				for (int i = 0; i<src.getNoEdges();i++) {
					
					Node dest = src.getDestination(i);
					if (dest != null) {
						Vector new_path = UtilLang.append(path, src.getEdgeName(i));
						tovisit_nodes.push(dest);
						tovisit_paths.push(new_path);
					}
				}
			}
		}
		return null;
	}



	
	

	/**
	 * Allinea una sequenza di string con l'automa, con la possibilita' di inserire dei
	 * gaps fra i token, ritorna l'allineamento piu' corto
	 * 
	 * @param nav iteratore per navigare sulla sequenza di string
	 * @param startFrom collezione di nodi da cui iniziare l'allineamento
	 * @param paths collezione delle path dal nodo iniziale dell'automa fino ai nodi start
	 * @return <code>null</code> se non esiste un allineamento, altrimenti la sequenza
	 *         di nodi che allinea con l'automa
	 * @throws RulesManagerException
	 */
	public Vector getGappedAlignment(Iterator nav, Vector startFrom, Vector paths) throws RulesManagerException {
		if (!nav.hasNext()) {
			Vector shortest_path = null;

			// ottiene lo shortest path verso uno dei nodi terminali
			for (int i = 0; i < startFrom.size(); i++) {
				Vector end_path = null;
				Node start = (Node) startFrom.elementAt(i);
				if (start.name.equals("E")) {
					// se l'ultimo nodo e' un nodo terminale
					// il path e' vuoto
					end_path = new Vector();
				} else {
					// cerca il path piu' corto verso un nodo terminale
					
						Node end = getLast();
						Vector reach_path = reach(start, end);
						if (reach_path != null && (end_path == null || end_path.size() > reach_path.size())) {
							end_path = reach_path;
						}
					
				}

				// controlla se il path trovato e' piu' corto di quello corrente
				Vector path = (Vector) paths.elementAt(i);
				path.addAll(end_path);
				if (shortest_path == null || shortest_path.size() > path.size()) {
					shortest_path = path;
				}
			}
			return shortest_path;
		}

		String token = (String) nav.next();

		// cerca tutti i nodi che riconoscono il token
		Vector new_startFrom = new Vector();
		Vector new_paths = new Vector();
		for (Iterator id = getNodes_table().values().iterator(); id.hasNext();) {
			Node src = (Node) id.next();
			Node dest = src.getDestination(token);
			if (dest != null) {
				// verifica che il nodo attuale sia raggiungible
				// dall'insieme dei nodi di partenza
				for (int i = 0; i < startFrom.size(); i++) {
					Node start = (Node) startFrom.elementAt(i);
					Vector path = (Vector) paths.elementAt(i);

					Vector reach_path = reach(start, src);
					if (reach_path != null) {
						// aggiunge il nodo al nuovo insieme dei nodi di partenza
						new_startFrom.addElement(dest);

						// aggiunge la path all'insieme delle paths dal nodo iniziale
						Vector new_path = new Vector();
						new_path.addAll(path);
						new_path.addAll(reach_path);
						new_path.addElement(token);
						new_paths.addElement(new_path);
					}
				}
			}
		}

		return ((new_startFrom.size() == 0) ? null : getGappedAlignment(nav, new_startFrom, new_paths));
	}

	

	/**
	 * Aggiunge un nodo al grafo
	 * 
	 * @return il nuovo nodo
	 */
	public Node addNode(String id) {
		if (!nodes_table.containsKey(id)) {
			Node new_node = new Node(id);
			nodes_table.put(id, new_node);
			return new_node;
		}
		return (Node) nodes_table.get(id);
	}
	/**
	 * Aggiunge un nodo al grafo
	 * 
	 * @return il nuovo nodo
	 */
	public Node addNewNode() {
		String id = "" + (getNodes_table().size() + 1);
		if (!getNodes_table().containsKey(id)) {
			Node new_node = new Node(id);
			getNodes_table().put(id, new_node);
			return new_node;
		}
		return (Node) getNodes_table().get(id);
	}
	
	public void addNode(Node n) {
		getNodes_table().put(n.name, n);
	}
	
	

	public HashMap getNodes_table() {
		return nodes_table;
	}
	
	public void setNodes_table(HashMap nodes_table) {
		this.nodes_table = nodes_table;
	}

	
	public int getNoEdges() {
		int total_edges=0;
		for (Iterator i = this.visitNodes(); i.hasNext();) {
			total_edges+=((Node) i.next()).getNoEdges();
		}
		return total_edges;
	}


	public String toString() {
		String out = "[" + name + ":\n";
		for (Iterator i = this.visitNodes(); i.hasNext();) {
			out = out + (Node) i.next();
		}
		out = out + "]";

		return out;
	}
}
