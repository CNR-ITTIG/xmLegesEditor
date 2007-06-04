/**
 * FSA contiene le funzionalit&agrave per la creazione e la
 * gestione di un automa a stati finiti.
 */

package it.cnr.ittig.xmleges.core.blocks.schema;

import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;

import java.io.Serializable;
import java.util.Vector;

public class FSA implements Serializable {

	public class Node implements Comparable, Serializable {

		/**
		 * Indice del nodo nell'automa
		 */
		protected int index;

		/**
		 * <code>true</code> se il nodo &egrave un nodo inizio dell'automa
		 */
		protected boolean is_start;

		/**
		 * <code>true</code> se il nodo &egrave un nodo fine dell'automa
		 */
		protected boolean is_end;

		/**
		 * Vettore delle transizioni uscenti dal nodo
		 */
		protected Vector transitions;

		/**
		 * Costruisce un nodo senza transizioni uscenti
		 * 
		 * @param index indice del nodo nel FSA
		 * @param start <code>true</code> se il nodo &egrave di inizio
		 * @param end <code>true</code> se il nodo &egrave di fine
		 */
		public Node(int index, boolean start, boolean end) {
			this.index = index;
			this.is_start = start;
			this.is_end = end;
			transitions = new Vector();
		}

		/**
		 * Restituisce l'indice del nodo
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * Confronta con un altro nodo in base al valore dell'indice
		 */
		public int compareTo(Object o) {
			Node n = (Node) o;
			if (this.index > n.index)
				return 1;
			if (this.index < n.index)
				return -1;
			return 0;
		}

		/**
		 * Confronta con un altro nodo in base al valore dell'indice
		 */
		public boolean equals(Object o) {
			Node n = (Node) o;
			return (this.index == n.index);
		}

		/**
		 * Ritorna la chiave hash per questo oggetto
		 */
		public int hashCode() {
			return index;
		}

		/**
		 * Controlla se il nodo &egrave un nodo inizio dell'automa
		 */
		public boolean isStart() {
			return is_start;
		}

		/**
		 * Controlla se il nodo &egrave un nodo fine dell'automa
		 */
		public boolean isEnd() {
			return is_end;
		}

		/**
		 * Ritorna il numero di transizioni uscenti dal nodo
		 */
		public int getNoTransitions() {
			return transitions.size();
		}

		/**
		 * Ritorna una transizione uscente dal nodo
		 * 
		 * @param index indice della transizione
		 * @return la transizione prescelta
		 */
		public Transition getTransition(int index) {
			return (Transition) transitions.get(index);
		}

		/**
		 * Crea una transizione e la aggiunge al nodo
		 * 
		 * @param dest nodo destinazione della transizione
		 * @param val valore della transizione
		 */
		public void addTransition(Node dest, String val) {
			transitions.add(new Transition(dest, val));
		}

		/**
		 * Crea una transizione epsilon e la aggiunge al nodo
		 * 
		 * @param dest nodo destinazione della transizione
		 * @throws DtdRulesManagerException
		 */
		public void addTransition(Node dest) throws DtdRulesManagerException {
			if (this.index == dest.index)
				throw new DtdRulesManagerException("Cowardly refusing to add an eps-transition with the node itself");
			transitions.add(new Transition(dest));
		}

		/**
		 * Aggiunge una transizione al nodo
		 * 
		 * @param toadd transizione da aggiungere
		 * @throws DtdRulesManagerException
		 */
		public void addTransition(Transition toadd) throws DtdRulesManagerException {
			if (this.index == toadd.getDestination().index)
				throw new DtdRulesManagerException("Cowardly refusing to add an eps-transition with the node itself");
			transitions.add(toadd);
		}

		public String toString() {

			// print node informations
			String sw = "Node " + index;
			if (is_start)
				sw = sw + " start ";
			if (is_end)
				sw = sw + " end ";
			sw = sw + "\n";

			// print transitions
			for (int i = 0; i < transitions.size(); i++)
				sw = sw + transitions.get(i).toString() + "\n";

			return sw;
		}
	}

	public class Transition implements Serializable {

		/**
		 * Transizione epsilon in un automa non-deterministico
		 */
		protected boolean is_epsilon;

		/**
		 * Valore della transizione
		 */
		protected String value;

		/**
		 * Nodo destinazione della transizione
		 */
		protected Node destination;

		/**
		 * Costruisce una transizione epsilon
		 * 
		 * @param dest destinazione della transizione
		 */
		public Transition(Node dest) {
			is_epsilon = true;
			value = "";
			destination = dest;
		}

		/**
		 * Costruisce una transizione normale
		 * 
		 * @param dest destinazione della transizione
		 * @param val valore della transizione
		 */
		public Transition(Node dest, String val) {
			is_epsilon = false;
			value = val;
			destination = dest;
		}

		/**
		 * Controlla se la transizione &egrave di tipo epsilon
		 */
		boolean isEpsilon() {
			return is_epsilon;
		}

		/**
		 * Restituisce il valore della transizione
		 */
		String getValue() {
			return value;
		}

		/**
		 * Restituisce il nodo destinazione della transizione
		 */
		Node getDestination() {
			return destination;
		}

		public String toString() {
			if (is_epsilon)
				return (" -- eps --> " + destination.getIndex());
			return (" -- " + value + " --> " + destination.getIndex());
		}
	}

	/**
	 * Nodi dell'automa, il nodo in posizione 0 &egrave sempre il nodo d'inizio
	 */
	Vector nodes;

	/**
	 * Costruisce un automa senza nodi
	 */
	public FSA() {
		nodes = new Vector();
	}

	/**
	 * Restituisce il numero di nodi dell'automa
	 */
	public int getNoNodes() {
		return nodes.size();
	}

	/**
	 * Restituisce un nodo dell'automa
	 * 
	 * @param index indice del nodo
	 * @return il nodo prescelto
	 */
	public Node getNode(int index) {
		return (Node) nodes.get(index);
	}

	/**
	 * Crea un nodo e lo aggiunge all'automa, il primo nodo aggiunto &egrave sempre il
	 * nodo inizio
	 * 
	 * @return l'indice del nodo aggiunto
	 */
	public int addNode() {
		int index = nodes.size();
		nodes.add(new Node(index, index == 0, false));
		return index;
	}

	/**
	 * Crea un nodo e lo aggiunge all'automa, il primo nodo aggiunto &egrave sempre il
	 * nodo inizio
	 * 
	 * @param end <code>true</code> se il nodo &egrave di fine
	 * @return l'indice del nodo aggiunto
	 */
	public int addNode(boolean end) {
		int index = nodes.size();
		nodes.add(new Node(index, index == 0, end));
		return index;
	}

	/**
	 * Crea una transizione fra due nodi dell'automa
	 * 
	 * @param source nodo di inizio della transizione
	 * @param dest nodo destinazione della transizione
	 * @param val valore della transizione
	 */
	public void addTransition(int source, int dest, String val) {
		getNode(source).addTransition(getNode(dest), val);
	}

	/**
	 * Crea una transizione epsilon fra due nodi dell'automa
	 * 
	 * @param source nodo di inizio della transizione
	 * @param dest nodo destinazione della transizione
	 * @throws DtdRulesManagerException
	 */
	public void addTransition(int source, int dest) throws DtdRulesManagerException {
		if (source == dest)
			throw new DtdRulesManagerException("Cowardly refusing to add an eps-transition with the node itself");
		getNode(source).addTransition(getNode(dest));
	}

	public String toString() {
		String sw = "FSA\n";
		for (int i = 0; i < nodes.size(); i++)
			sw = sw + nodes.get(i).toString();
		return (sw + "\n");
	}
}
