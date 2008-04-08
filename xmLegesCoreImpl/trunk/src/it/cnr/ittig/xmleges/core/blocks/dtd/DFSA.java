/** 
 * DFSA contiene le procedure per la creazione di un automa
 * deterministico a partire da uno non deterministico e per
 * l'allineamento di una sequenza di stringhe con l'automa
 */

package it.cnr.ittig.xmleges.core.blocks.dtd;

import it.cnr.ittig.xmleges.core.services.rules.RulesManagerException;
import it.cnr.ittig.xmleges.core.util.lang.Queue;
import it.cnr.ittig.xmleges.core.util.lang.UtilLang;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

public class DFSA implements Serializable {

	protected class Node implements Comparable, Serializable {

		/**
		 * <code>true</code> se il nodo &egrave un nodo inizio dell'automa
		 */
		protected boolean is_start;

		/**
		 * <code>true</code> se il nodo &egrave un nodo fine dell'automa
		 */
		protected boolean is_end;

		/**
		 * Nome del nodo, usato per il suo indirizzamento e calcolato in base al risultato
		 * della eps-chiusra
		 */
		protected String name;

		/**
		 * Tabella di transizione: la chiave &egrave il simbolo sull'arco, mentre il
		 * contenuto &egrave il nodo destinazione
		 */
		protected HashMap transition_table;

		/**
		 * Insieme dei nodi del FSA corrispondente
		 */
		protected Collection eps_chiusura;

		/**
		 * Crea un nodo che rappresenta un insieme di nodi del FSA corrispondente
		 * 
		 * @param name nome del nodo
		 * @param start <code>true</code> se il nodo &egrave di inizio
		 * @param end <code>true</code> se il nodo &egrave di fine
		 * @param eps_chiusura insieme di nodi del FSA corrispondente
		 */
		public Node(String name, boolean start, boolean end, Collection eps_chiusura) {
			this.name = name;
			this.is_start = start;
			this.is_end = end;
			this.eps_chiusura = eps_chiusura;
			transition_table = new HashMap();
		}

		/**
		 * Confronta con un altro nodo in base al valore dell'indice
		 */
		public int compareTo(Object o) {
			return this.name.compareTo(((Node) o).name);
		}

		/**
		 * Confronta con un altro nodo in base al valore dell'indice
		 */
		public boolean equals(Object o) {
			return this.name.equals(((Node) o).name);
		}

		/**
		 * Ritorna la chiave hash per questo oggetto
		 */
		public int hashCode() {
			return name.hashCode();
		}

		/**
		 * Ritorna il nome del nodo
		 */
		public String getName() {
			return name;
		}

		/**
		 * Ritorna la eps-chiusura rappresentata da questo nodo
		 */
		public Collection getEpsChiusura() {
			return eps_chiusura;
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
		 * Aggiunge una transizione dal nodo corrente
		 * 
		 * @param value valore della transizione
		 * @param dest nodo destinazione della transizione
		 * @throws RulesManagerException
		 */
		public void addTransition(String value, Node dest) throws RulesManagerException {
			if (transition_table.containsKey(value))
				throw new RulesManagerException("The transition table contains already this symbol");
			transition_table.put(value, dest);
		}

		/**
		 * Ritorna l'insieme di simboli riconosciuti da questo nodo
		 */
		public Collection getVocabulary() {
			return transition_table.keySet();
		}

		/**
		 * Ritorno l'insieme di nodi raggiungibili
		 */
		public Collection getDestinations() {
			return transition_table.values();
		}

		/**
		 * Applica la funzione di transizione ad un simbolo
		 * 
		 * @param value simbolo da cercare sugli archi del DFSA
		 * @return il nodo destinazione della transizione,
		 *         <code>null</null> se il simbolo non fa parte del
		 * vocabolario del nodo
		 */
		public Node getNext(String value) {
			if (value.compareTo("#ANY") == 0)
				return this;
			return (Node) transition_table.get(value);
		}

		public String toString() {

			// print node informations
			String sw = "Node " + name;
			if (is_start)
				sw = sw + " start ";
			if (is_end)
				sw = sw + " end ";
			sw = sw + "\n";

			// print transitions
			Set keys = transition_table.keySet();
			for (Iterator i = keys.iterator(); i.hasNext();) {
				String symbol = (String) i.next();
				sw = sw + " -- " + symbol + " --> " + ((Node) transition_table.get(symbol)).name + "\n";
			}

			return sw;
		}

	}

	/**
	 * Nome dell'elemento rappresentato da questo automa
	 */
	protected String name;

	/**
	 * Lista dei nodi indirizzati dal loro nome
	 */
	protected HashMap nodes_table;

	/**
	 * Nodo di inizio dell automa
	 */
	protected Node start_node;

	/**
	 * Nodi terminali dell'automa
	 */
	protected Vector end_nodes;

	/**
	 * Crea un automa deterministico a partire da uno non deterministico
	 * @param _name etichetta della regola
	 * @param nd_fsa l'automa da trasformare
	 * @throws RulesManagerException
	 */
	public DFSA(String _name, FSA nd_fsa) throws RulesManagerException {
		name = _name;
		nodes_table = new HashMap();
		if (nd_fsa.getNoNodes() == 0)
			throw new RulesManagerException("Empty FSA");

		// crea il nodo di inizio
		start_node = epsChiusuraStart(nd_fsa.getNode(0));
		nodes_table.put(start_node.getName(), start_node);

		Stack visiting_nodes = new Stack();
		visiting_nodes.push(start_node);

		// crea l'automa
		while (!visiting_nodes.empty()) {

			// enumera le transizioni uscenti dal nodo da visitare
			Node tovisit = (Node) visiting_nodes.pop();
			HashMap transitions = enumerateTransitions(tovisit.getEpsChiusura());

			// aggiunge le transizioni al nodo
			Collection vocabulary = transitions.keySet();
			for (Iterator i = vocabulary.iterator(); i.hasNext();) {

				// aggiunge una transizione per ogni simbolo del vocabolario
				String symbol = (String) i.next();
				Node destination = epsChiusura((Collection) transitions.get(symbol));

				// se il nodo destinazione non esiste lo aggiunge
				// al DFSA e lo inserisce nella lista di quelli da
				// visitare
				Node ret = (Node) nodes_table.get(destination.getName());
				if (ret != null)
					destination = ret;
				else {
					nodes_table.put(destination.getName(), destination);
					visiting_nodes.push(destination);
				}
				tovisit.addTransition(symbol, destination);
			}
		}

		// collect end nodes
		end_nodes = new Vector();
		for (Iterator i = nodes_table.values().iterator(); i.hasNext();) {
			Node n = (Node) i.next();
			if (n.isEnd())
				end_nodes.addElement(n);
		}
	}

	/**
	 * Enumera tutte le transizioni uscenti da un insieme di nodi
	 * 
	 * @param sources l'insieme di nodi sorgente
	 * @return una mappa che ha per chiave il simbolo della transizione e per valore
	 *         l'insieme di nodi destinazione
	 */
	protected HashMap enumerateTransitions(Collection sources) {
		HashMap transitions = new HashMap();

		for (Iterator i = sources.iterator(); i.hasNext();) {
			FSA.Node n = (FSA.Node) i.next();

			for (int l = 0; l < n.getNoTransitions(); l++) {
				FSA.Transition transition = n.getTransition(l);
				if (transition.isEpsilon())
					continue;

				HashSet destinations = (HashSet) transitions.get(transition.getValue());

				// add an entry if the symbol is not present in
				// the vocabulary
				if (destinations == null) {
					destinations = new HashSet();
					transitions.put(transition.getValue(), destinations);
				}

				// add the destination to the set if it is not present in the set
				destinations.add(transition.getDestination());
			}
		}
		return transitions;
	}

	/**
	 * Crea un nodo del DFSA rappresentante l'eps-chiusura di un insieme di nodi del FSA
	 * 
	 * @param nodes l'insieme di nodi dell'automa non-deterministico di cui calcolare
	 *            l'eps-chiusura
	 * @param start il nuovo nodo deve rappresentare il nodo inizio dell'automa
	 * @return il node che rappresenta l'eps-chiusura
	 */
	protected Node epsChiusura(Collection nodes) {

		// calcula la eps-chiusura come unione di tutte le eps_chiusure
		// dei nodi dell'insieme
		HashSet eps_chiusura = new HashSet();
		for (Iterator i = nodes.iterator(); i.hasNext();)
			eps_chiusura.addAll(epsChiusura1((FSA.Node) i.next()));

		// genera il nome del nodo
		Object[] eps_nodes = eps_chiusura.toArray();
		Arrays.sort(eps_nodes);

		String name = "";
		boolean end = false;
		for (int i = 0; i < eps_nodes.length; i++) {
			FSA.Node n = (FSA.Node) eps_nodes[i];
			name += String.valueOf(n.getIndex());
			if (i < eps_nodes.length - 1)
				name += "_";
			end |= n.isEnd();
		}

		// crea il nuovo nodo
		return new Node(name, false, end, eps_chiusura);
	}

	/**
	 * Crea un nodo del DFSA rappresentante l'eps-chiusura del nodo inizio del FSA
	 * 
	 * @param node il nodo dell'automa non-deterministico di cui calcolare l'eps-chiusura
	 * @return il nodo che rappresenta l'eps-chiusura
	 */
	protected Node epsChiusuraStart(FSA.Node node) {

		// calcula la eps-chiusura come unione di tutte le eps_chiusure
		// dei nodi dell'insieme
		Collection eps_chiusura = epsChiusura1(node);

		// genera il nome del nodo
		Object[] eps_nodes = eps_chiusura.toArray();
		Arrays.sort(eps_nodes);

		String name = "";
		boolean end = false;
		for (int i = 0; i < eps_nodes.length; i++) {
			FSA.Node n = (FSA.Node) eps_nodes[i];
			name += String.valueOf(n.getIndex());
			if (i < eps_nodes.length - 1)
				name += "_";
			end |= n.isEnd();
		}

		// crea il nuovo nodo
		return new Node(name, true, end, eps_chiusura);
	}

	/**
	 * Restituisce l'eps-chiusura di un nodo
	 * 
	 * @param node il nodo dell'automa non-deterministico di cui calcolare l'eps-chiusura
	 * @return l'insieme di nodi che rappresentano l'eps-chiusura del nodo
	 */
	protected Collection epsChiusura1(FSA.Node node) {
		HashSet eps_chiusura = new HashSet();
		Stack visiting_nodes = new Stack();

		visiting_nodes.push(node);
		while (!visiting_nodes.empty()) {
			FSA.Node tovisit = (FSA.Node) visiting_nodes.pop();

			eps_chiusura.add(tovisit);
			int no_transitions = tovisit.getNoTransitions();
			for (int i = 0; i < no_transitions; i++) {
				FSA.Transition transition = tovisit.getTransition(i);
				if (transition.isEpsilon() && !eps_chiusura.contains(transition.getDestination()))
					visiting_nodes.push(transition.getDestination());
			}
		}

		return eps_chiusura;
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
	public boolean align(Collection sequence, boolean with_gaps) throws RulesManagerException {
		if (with_gaps)
			return alignWithGaps(sequence);
		return align(sequence);
	}

	/**
	 * Allinea una sequenza di stringhe con l'automa
	 * 
	 * @param sequence la sequenza da allineare
	 * @return <code>true</code> se la sequenza di nodi allinea con l'automa
	 * @throws RulesManagerException
	 */
	public boolean align(Collection sequence) throws RulesManagerException {
		if (nodes_table.size() == 0)
			throw new RulesManagerException("Empty automata");

		Node last_node = start_node;
		for (Iterator i = sequence.iterator(); i.hasNext() && last_node != null;) {
			last_node = last_node.getNext((String) i.next());
		}

		return (last_node != null && last_node.isEnd());
	}

	/**
	 * Allinea una sequenza di string con l'automa, con la possibilita' di inserire dei
	 * gaps fra i token
	 * 
	 * @param sequence la sequenza da allineare
	 * @return <code>true</code> se la sequenza di nodi allinea con l'automa
	 * @throws RulesManagerException
	 */
	public boolean alignWithGaps(Collection sequence) throws RulesManagerException {
		if (nodes_table.size() == 0)
			throw new RulesManagerException("Empty automata");

		return alignWithGaps(sequence.iterator(), UtilLang.singleton(start_node));
	}

	/**
	 * Allinea una sequenza di string con l'automa, con la possibilita' di inserire dei
	 * gaps fra i token, e ritorna l'allineamento ottenuto
	 * 
	 * @param sequence la sequenza da allineare
	 * @return <code>null</code> se non esiste un allineamento, altrimenti la sequenza
	 *         di nodi che allinea con l'automa
	 * @throws RulesManagerException
	 */
	public Vector getGappedAlignment(Collection sequence) throws RulesManagerException {
		if (nodes_table.size() == 0)
			throw new RulesManagerException("Empty automata");

		return getGappedAlignment(sequence.iterator(), UtilLang.singleton(start_node), UtilLang.singleton(new Vector()));
	}

	/**
	 * Allinea una sequenza di string con l'automa, con la possibilita' di inserire dei
	 * gaps fra i token
	 * 
	 * @param nav iteratore per navigare sulla sequenza di string
	 * @param startFrom collezione di nodi da cui iniziare l'allineamento
	 * @return <code>true</code> se la sequenza di nodi allinea con l'automa
	 * @throws RulesManagerException
	 */
	private boolean alignWithGaps(Iterator nav, Vector startFrom) throws RulesManagerException {
		if (!nav.hasNext())
			return true;
		String token = (String) nav.next();

		// cerca tutti i nodi che riconoscono il token
		Collection nodes = nodes_table.values();
		Vector new_startFrom = new Vector();
		for (Iterator id = nodes.iterator(); id.hasNext();) {
			Node src = (Node) id.next();
			Node dest = src.getNext(token);
			if (dest != null) {
				// verifica che il nodo attuale sia raggiungible
				// dall'insieme dei nodi di partenza
				for (Iterator is = startFrom.iterator(); is.hasNext();) {
					Node start = (Node) is.next();
					if (canReach(start, src)) {
						// aggiunge il nodo al nuovo insieme dei nodi di partenza
						new_startFrom.addElement(dest);
					}
				}
			}
		}

		if (new_startFrom.size() == 0)
			return false;
		return alignWithGaps(nav, new_startFrom);
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
	private Vector getGappedAlignment(Iterator nav, Vector startFrom, Vector paths) throws RulesManagerException {
		if (!nav.hasNext()) {
			Vector shortest_path = null;

			// ottiene lo shortest path verso uno dei nodi terminali
			for (int i = 0; i < startFrom.size(); i++) {
				Vector end_path = null;
				Node start = (Node) startFrom.elementAt(i);
				if (start.isEnd()) {
					// se l'ultimo nodo e' un nodo terminale
					// il path e' vuoto
					end_path = new Vector();
				} else {
					// cerca il path piu' corto verso un nodo terminale
					for (int l = 0; l < end_nodes.size(); l++) {
						Node end = (Node) end_nodes.elementAt(l);
						Vector reach_path = reach(start, end);
						if (reach_path != null && (end_path == null || end_path.size() > reach_path.size())) {
							end_path = reach_path;
						}
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
		for (Iterator id = nodes_table.values().iterator(); id.hasNext();) {
			Node src = (Node) id.next();
			Node dest = src.getNext(token);
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
	 * Verifica che vi sia un path fra i due nodi
	 */
	private boolean canReach(Node from, Node to) {
		HashSet visited = new HashSet();
		Stack tovisit = new Stack();

		tovisit.push(from);
		while (!tovisit.empty()) {
			// visit the top of the stack
			Node src = (Node) tovisit.pop();
			visited.add(src.getName());

			// check if the final destination has been reached
			if (src == to)
				return true;

			// schedule the following nodes for a visit
			Collection dests = src.getDestinations();
			for (Iterator id = dests.iterator(); id.hasNext();) {
				Node dest = (Node) id.next();
				// if the next node has not been visited and is not in the stack
				// schedule it for a visit
				if (!visited.contains(dest.getName()) && tovisit.search(dest) == -1) {
					tovisit.push(dest);
				}
			}
		}
		return false;
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
				Collection tokens = src.getVocabulary();
				for (Iterator it = tokens.iterator(); it.hasNext();) {
					String token = (String) it.next();
					Node dest = src.getNext(token);
					if (dest != null) {
						Vector new_path = UtilLang.append(path, token);
						tovisit_nodes.push(dest);
						tovisit_paths.push(new_path);
					}
				}
			}
		}
		return null;
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
	public Collection alignAlternatives(Collection sequence, int choice_point) throws RulesManagerException {
		if (nodes_table.size() == 0)
			throw new RulesManagerException("Empty automata");

		Node last_node = start_node;
		Object[] seq_array = sequence.toArray();

		// align until the choice point
		for (int i = 0; i <= choice_point && i < seq_array.length && last_node != null; i++) {
			last_node = last_node.getNext((String) seq_array[i]);
		}
		if (last_node == null)
			throw new RulesManagerException("The sequence does not align, cannot find alternatives");

		// try all the alternatives
		Node choice_node = last_node;
		Vector alternatives = new Vector();
		Collection vocabulary = choice_node.getVocabulary();
		for (Iterator v = vocabulary.iterator(); v.hasNext();) {
			String symbol = (String) v.next();

			// align from the choice point
			last_node = choice_node.getNext(symbol);
			for (int i = choice_point + 1; i < seq_array.length && last_node != null; i++)
				last_node = last_node.getNext((String) seq_array[i]);

			// valid alternative
			if (last_node != null && last_node.isEnd())
				alternatives.add(symbol);
		}

		return alternatives;
	}

	/**
	 * Crea un ContentGraph che rappresenta il contenuto di questo elemento
	 */
	public ContentGraph createContentGraph() {

		ContentGraph graph = new ContentGraph(name);
		ContentGraph.Node first = graph.getFirst();
		ContentGraph.Node last = graph.getLast();

		// aggiunge un nodo al grafo per ogni nodo del DFSA
		HashMap graph_nodes_table = new HashMap();
		for (Iterator i = nodes_table.keySet().iterator(); i.hasNext();) {
			graph_nodes_table.put((String) i.next(), graph.addNode());
		}

		// per ogni nodo del DFSA
		for (Iterator i = nodes_table.keySet().iterator(); i.hasNext();) {
			String node_name = (String) i.next();
			DFSA.Node dfsa_node = (DFSA.Node) nodes_table.get(node_name);
			ContentGraph.Node graph_node = (ContentGraph.Node) graph_nodes_table.get(node_name);

			// aggiunge un arco per ogni transizione
			for (Iterator j = dfsa_node.getVocabulary().iterator(); j.hasNext();) {
				String dfsa_edge = (String) j.next();
				String graph_edge = dfsa_edge;
				DFSA.Node dfsa_destination = dfsa_node.getNext(dfsa_edge);
				ContentGraph.Node graph_destination = (ContentGraph.Node) graph_nodes_table.get(dfsa_destination.getName());
				graph_node.addEdge(graph_edge, graph_destination);
			}

			// se il nodo e' un nodo iniziale del DFSA aggiunge una transizione vuota
			// dal nodo iniziale del ContentGraph
			if (dfsa_node.isStart())
				first.addEdge("#EPS", graph_node);

			// se il nodo e' un nodo finale del DFSA aggiunge una transizione vuota verso
			// il
			// nodo finale del ContentGraph
			if (dfsa_node.isEnd()) {
				if (dfsa_node.isStart()) {
					// in questo modo si previene che esistano elementi con il contenuto
					// vuoto
					graph_node.addEdge("#PCDATA", last);
				} else
					graph_node.addEdge("#EPS", last);
			}
		}

		return graph;
	}

	public String toString() {
		String sw = "DFSA\n";

		Collection nodes = nodes_table.values();
		for (Iterator i = nodes.iterator(); i.hasNext();)
			sw = sw + i.next().toString();
		return (sw + "\n");
	}
}
