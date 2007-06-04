/**
 * Classe che rappresenta il contenuto di un elemento.
 * Viene usata per identificare il contenuto di default.
 */

package it.cnr.ittig.xmleges.core.blocks.schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
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
	 * Aggiunge un nodo al grafo
	 * 
	 * @return il nuovo nodo
	 */
	public Node addNode() {
		String id = "" + (nodes_table.size() + 1);
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
	public Node addNode(String id) {
		if (!nodes_table.containsKey(id)) {
			Node new_node = new Node(id);
			nodes_table.put(id, new_node);
			return new_node;
		}
		return (Node) nodes_table.get(id);
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
