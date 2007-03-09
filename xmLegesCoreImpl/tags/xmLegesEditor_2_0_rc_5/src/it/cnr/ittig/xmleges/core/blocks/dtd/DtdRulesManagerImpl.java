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

package it.cnr.ittig.xmleges.core.blocks.dtd;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManager;
import it.cnr.ittig.xmleges.core.services.dtd.DtdRulesManagerException;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.core.util.lang.UtilLang;
import it.cnr.ittig.xmleges.core.util.xml.UtilXml;

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
import org.xml.sax.ext.DeclHandler;

/**
 * Implementazione del servizio it.cnr.ittig.xmleges.editor.services.dtdrulesmanager.DtdRulesManager;
 * 
 */
public class DtdRulesManagerImpl implements DtdRulesManager, DeclHandler, Loggable {

	Logger logger;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Tabella hash contenente le regole per l'interrogazione sotto-forma di automi
	 * deterministici. Ogni regola &egrave associata ad un elemento.
	 */
	protected HashMap rules;

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

	protected boolean pre_check = false;

	// --------- Funzioni per l'interpretazione della DTD ---------------------

	public void attributeDecl(String elementName, String attributeName, String type, String valueDefault, String value) {
		if (logger.isDebugEnabled())
			logger.debug("ATTRIBUTE: elem=" + elementName + " name=" + attributeName + " type=" + type + " default=" + valueDefault + " value=" + value);

		// get the hash table of attributes associated with the element
		// or create a new one if not existing
		HashMap att_hash = (HashMap) attributes.get(elementName);
		if (att_hash == null) {
			att_hash = new HashMap();
			attributes.put(elementName, att_hash);
		}

		// add a new attribute definition
		att_hash.put(attributeName, new AttributeDeclaration(type, valueDefault, value));
	}

	public void elementDecl(String name, String model) {

		if (logger.isDebugEnabled())
			logger.debug("ELEMENT: name=" + name + " model=" + model);

		try {
			// add element rule
			createRule(name, model);

			// add alternative contents for element
			createAlternativeContents(name, model);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void externalEntityDecl(String name, String publicId, String systemId) {
		if (logger.isDebugEnabled())
			logger.debug("EXTERNAL ENTITY: name=" + name + " publicId=" + publicId + " systemId=" + systemId);
	}

	public void internalEntityDecl(String name, String value) {
		if (logger.isDebugEnabled())
			logger.debug("INTERNAL ENTITY: name=" + name + " value=" + value);
	}

	// ------------ COSTRUTTORI --------------------

	public DtdRulesManagerImpl() {
		rules = new HashMap();
		alternative_contents = new HashMap();
		attributes = new HashMap();
	}

	public void clear() {
		rules = new HashMap();
		alternative_contents = new HashMap();
		attributes = new HashMap();
	}

	// ------------ INIZIALIZZAZIONE DELLE REGOLE --------------------

	public void loadRules(String filename) {

		logger.info("START loading rules from DTD");

		// open file
		File xml_file = new File(filename);

		// clear old rules
		clear();

		// parse DTD
		UtilXml.readDTD(xml_file, this);

		logger.info("END loading rules from DTD");

	}

	public void loadRules(String filename, String dtdPath) {

		String key = null;
		File xml_file = new File(filename);

		if (dtdPath.startsWith(".")) // crea path name assoluto
			dtdPath = xml_file.getParent().concat(dtdPath.substring(1));

		File dtd_file = UtilFile.getDTDFile(dtdPath);

		// Generazione della chiave
		try {
			key = UtilLang.bytesToHexString(UtilFile.calculateMD5(dtd_file));
			logger.debug("key for " + dtdPath + " = " + key.toString());
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		String md5Path = new String("dtdmd5");
		new File(md5Path).mkdir();
		File rulesMap = new File(md5Path + File.separator, key + "_rules");
		File alternativesMap = new File(md5Path, key + "_alternatives");
		File attributesMap = new File(md5Path, key + "_attributes");

		if (key != null && rulesMap.exists() && alternativesMap.exists() && attributesMap.exists()) {
			loadRulesFromCachedMap(rulesMap, alternativesMap, attributesMap);
		} else {
			loadRules(xml_file);
			saveRulesOnCachedMap(rulesMap, alternativesMap, attributesMap);
		}
	}

	public void loadRules(File xml_file) {

		logger.info("START loading rules from DTD");

		// clear old rules
		clear();

		// parse DTD
		UtilXml.readDTD(xml_file, this);

		logger.info("END loading rules from DTD");
	}

	// lettura delle regole dalle mappe salvate su file

	private void loadRulesFromCachedMap(File rulesMap, File alternativesMap, File attributesMap) {
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

	private void saveRulesOnCachedMap(File rulesMap, File alternativesMap, File attributesMap) {
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

	/**
	 * Crea una regola per un elemento a partire da un modello della DTD
	 * 
	 * @return la nuova regola
	 * @throws DtdRulesManagerException
	 */
	protected DFSA createRule(String name, String model) throws DtdRulesManagerException {
		// System.out.println("Creating FSA");
		FSA qrule = readModel(model);

		// System.out.println("Creating DFSA");
		DFSA vrule = new DFSA(name, qrule);
		rules.put(name, vrule);

		return vrule;
	}

	/**
	 * Crea i contenuti alternativi per un elemento
	 * 
	 * @return il vettore di alternative
	 * @throws DtdRulesManagerException
	 */
	protected Collection createAlternativeContents(String name, String model) throws DtdRulesManagerException {
		// System.out.println("Reading alternatives");
		Vector contents_strings = readAlternativeContents(model);
		alternative_contents.put(name, contents_strings);

		return contents_strings;
	}

	/**
	 * Crea l'automa a stati finiti che rappresenta il content di un elemento
	 * 
	 * @param model modello dell'elemento da rappresentare
	 * @throws DtdRulesManagerException
	 */
	protected FSA readModel(String model) throws DtdRulesManagerException {
		FSA rule = new FSA();

		int start = rule.addNode(); // aggiunge il nodo inizio
		int end = rule.addNode(true); // aggiunge il nodo fine

		if (model == "ANY")
			// rule.addTransition(start,end,"#ANY");
			rule.addTransition(start, end, "#PCDATA");
		else if (model == "EMPTY")
			rule.addTransition(start, end);
		else
			readContent(rule, start, end, model);

		return rule;
	}

	/**
	 * Divide una stringa in tokens separati da un carattere specificato. Le parti di
	 * stringa racchiuse da parentesi tonde sono considerate come singoli tokens.
	 * 
	 * @param content stringa da separare
	 * @param separator carattere separatore dei tokens
	 * @return il vettore dei tokens
	 */

	protected Collection splitContent(String content, char separator) throws DtdRulesManagerException {
		int size = content.length();
		if (size == 0)
			throw new DtdRulesManagerException("Empty content");

		int last = 0;
		int open_pars = 0;
		Vector tokens = new Vector();
		for (int i = 0; i < size; i++) {
			char thischar = content.charAt(i);

			if (thischar == '(')
				open_pars++;
			else if (thischar == ')')
				open_pars--;

			if (open_pars < 0)
				throw new DtdRulesManagerException("Malformed content: " + content);
			if (open_pars == 0) {
				if (thischar == separator) {
					if (last == i)
						throw new DtdRulesManagerException("Malformed content: " + content);
					tokens.add(content.substring(last, i));
					last = i + 1;
				}
			}
		}
		tokens.add(content.substring(last, size));

		return tokens;
	}

	/**
	 * Aggiunge le transizioni che rappresentano questo content all'automa
	 * 
	 * @param automata automa da modificare
	 * @param start indice del nodo inizio transizione
	 * @param end indice del nodo fine transizione
	 * @param item content da parsare
	 * @param content il modello del contenuto di un elemento
	 * @throws DtdRulesManagerException
	 */
	protected void readContent(FSA automata, int start, int end, String content) throws DtdRulesManagerException {
		// get cardinality

		boolean is_repeatable = false;
		boolean is_optional = false;
		if (content.endsWith("?"))
			is_optional = true;
		else if (content.endsWith("+"))
			is_repeatable = true;
		else if (content.endsWith("*")) {
			is_optional = true;
			is_repeatable = true;
		}
		if (is_optional || is_repeatable)
			content = content.substring(0, content.length() - 1);

		// parse content
		if (content.startsWith("(") && content.endsWith(")")) {
			content = content.substring(1, content.length() - 1);

			// check if it is a sequence
			Collection sequence = splitContent(content, ',');
			if (sequence.size() > 1) {
				int last = start;
				for (Iterator i = sequence.iterator(); i.hasNext();) {
					// add state for blocking epsilon transitions
					int new_start = automata.addNode();
					int new_end = automata.addNode();
					readContent(automata, new_start, new_end, (String) i.next());
					automata.addTransition(last, new_start);
					last = new_end;
				}
				automata.addTransition(last, end);
			} else {
				// check if it's a choice
				Collection choice = splitContent(content, '|');
				if (choice.size() > 1) {
					if (((String) choice.iterator().next()).equals("#PCDATA")) {
						// mixed content, cardinality=='*'
						is_optional = true;
						is_repeatable = true;
					}

					for (Iterator i = choice.iterator(); i.hasNext();) {
						// add state for blocking epsilon transitions
						int new_start = automata.addNode();
						int new_end = automata.addNode();
						readContent(automata, new_start, new_end, (String) i.next());
						automata.addTransition(start, new_start);
						automata.addTransition(new_end, end);
					}
				} else {
					// single element
					readContent(automata, start, end, content);
				}
			}
		} else {
			// single element
			automata.addTransition(start, end, content);
		}

		// cardinality modificator
		if (is_optional)
			automata.addTransition(start, end);
		if (is_repeatable)
			automata.addTransition(end, start);
	}

	protected Vector mergeAlternatives(Vector before, Vector after) {
		if (before.size() == 0)
			return after;
		if (after.size() == 0)
			return before;

		Vector alternatives = new Vector();
		for (Iterator i = before.iterator(); i.hasNext();) {
			String str_before = (String) i.next();
			for (Iterator l = after.iterator(); l.hasNext();) {
				String str_after = (String) l.next();
				alternatives.add(str_before + "," + str_after);
			}
		}
		return alternatives;
	}

	/**
	 * Crea le possibili alternative del contenuto di un elemento
	 * 
	 * @param content il modello del contenuto di un elemento
	 * @throws DtdRulesManagerException
	 */
	protected Vector readAlternativeContents(String content) throws DtdRulesManagerException {

		Vector alternatives = new Vector();

		// get cardinality
		boolean is_repeatable = false;
		boolean is_optional = false;
		if (content.endsWith("?"))
			is_optional = true;
		else if (content.endsWith("+"))
			is_repeatable = true;
		else if (content.endsWith("*")) {
			is_optional = true;
			is_repeatable = true;
		}
		if (is_optional || is_repeatable)
			content = content.substring(0, content.length() - 1);

		// optional contents are ignored, repeating is not considered
		if (is_optional)
			return alternatives;

		// parse content
		if (content.startsWith("(") && content.endsWith(")")) {
			content = content.substring(1, content.length() - 1);

			// check if it is a sequence
			Collection sequence = splitContent(content, ',');
			if (sequence.size() > 1) {
				for (Iterator i = sequence.iterator(); i.hasNext();)
					alternatives = mergeAlternatives(alternatives, readAlternativeContents((String) i.next()));
			} else {
				// check if it's a choice
				Collection choice = splitContent(content, '|');
				if (choice.size() > 1) {
					// mixed content, cardinality=='*', are ignored
					if (((String) choice.iterator().next()).equals("#PCDATA"))
						return alternatives;

					// add all the alternatives given from the possible choices
					for (Iterator i = choice.iterator(); i.hasNext();)
						alternatives.addAll(readAlternativeContents((String) i.next()));
				} else {
					// single element
					return readAlternativeContents(content);
				}
			}
		} else {
			// single element
			alternatives.add(content);
		}

		return alternatives;
	}

	// ------------ GESTIONE CONTENUTO DI DEFAULT DI UN ELEMENTO
	// --------------------

	/**
	 * Controlla se il ContentGraph contiene un contenuto di default: un cammino fatto
	 * solo di elementi di testo e transizioni vuote dal primo all'ultimo nodo
	 * 
	 * @param graph il ContentGraph
	 * @return <code>Integer.MAX_VALUE</code> se non esiste un cammino, altrimenti
	 *         ritorna la lunghezza del cammino minimo
	 */
	static protected int visitContentGraph(ContentGraph graph) {
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
			output += getXMLContent((ContentGraph) i.next());
		output = output + "</" + graph.getName() + ">";

		return output;
	}

	/**
	 * Sostituisce ogni arco del ContentGraph che rappresenta un elemento, con il suo
	 * ContentGraph. Scende ricorsivamente negli archi gia' esplosi
	 * 
	 * @param graph il ContentGraph da esplodere
	 * @throws DtdRulesManagerException
	 */
	protected void explodeContentGraph(ContentGraph graph) throws DtdRulesManagerException {
		// check every edge of the old graph
		for (Iterator i = graph.visitNodes(); i.hasNext();) {
			ContentGraph.Node src = (ContentGraph.Node) i.next();
			for (int j = 0; j < src.getNoEdges(); j++) {
				Object edge = src.getEdge(j);
				String edge_name = src.getEdgeName(j);
				if (edge instanceof ContentGraph) {
					// recursively explore subgraph
					explodeContentGraph((ContentGraph) edge);
				} else if (edge_name.compareTo("#PCDATA") != 0 && edge_name.compareTo("#EPS") != 0) {
					// replace edge with ContentGraph
					src.setEdge(getContentGraph(edge_name), j);
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
	 * @throws DtdRulesManagerException
	 */
	protected String getDefaultContent(ContentGraph graph) throws DtdRulesManagerException {
		// cycle until a default content has been found
		while (true) {
			if (visitContentGraph(graph) < Integer.MAX_VALUE)
				return getXMLContent(graph);

			// no default content: substitute each element with its ContentGraph
			explodeContentGraph(graph);
		}
	}

	/**
	 * Ritorna una stringa in formato XML che definisce il contenuto di default di un
	 * elemento
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @throws DtdRulesManagerException
	 */
	public String getDefaultContent(String elem_name) throws DtdRulesManagerException {
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
	 * @throws DtdRulesManagerException
	 */
	public String getDefaultContent(String elem_name, String alternative) throws DtdRulesManagerException {
		if (elem_name.compareTo("#PCDATA") == 0)
			return "";

		DFSA elem_rule = (DFSA) rules.get(elem_name);
		if (elem_rule == null)
			throw new DtdRulesManagerException("No rule for element <" + elem_name + ">");

		// init computation, get content of this alternative
		return getDefaultContent(createAlternativeContentGraph(elem_name, alternative));
	}

	/**
	 * Ritorna una stringa in formato XML che definisce il contenuto di default di un
	 * elemento, tale elemento deve contenere i nodi desiderati
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param nodes i nodi che devono essere contenuti nell'elemento
	 * @throws DtdRulesManagerException
	 */
	public String getDefaultContent(String elem_name, Vector nodes) throws DtdRulesManagerException {
		// get node names
		Vector node_names = new Vector();
		for (Iterator i = nodes.iterator(); i.hasNext();) {
			Node node = (Node) i.next();
			if (node == null)
				throw new DtdRulesManagerException("node #" + node_names.size() + " to insert is null");
			node_names.addElement(getNodeName(node));
		}

		// create string content
		String str_content = "";
		Vector content = getGappedAlignment(elem_name, node_names);
		if (content == null)
			throw new DtdRulesManagerException("The nodes does not align with the element content");

		// transform element names in XML content
		for (int i = 0, l = 0; i < content.size(); i++) {
			String item = (String) content.elementAt(i);
			if (l < nodes.size() && item.equals((String) node_names.elementAt(l))) {
				// insert the desired node with its content
				str_content += UtilDom.domToString((Node) nodes.elementAt(l++));
			} else {
				// insert the default content
				str_content += getDefaultContent(item);
			}
		}
		return str_content;
	}

	/**
	 * Ritorna i possibili contenuti alternativi di un elemento
	 * 
	 * @throws DtdRulesManagerException
	 */
	public Vector getAlternativeContents(String elem_name) throws DtdRulesManagerException {
		if (elem_name.compareTo("#PCDATA") == 0)
			return new Vector();

		Vector alternatives = (Vector) alternative_contents.get(elem_name);
		if (alternatives == null)
			throw new DtdRulesManagerException("No alternative contents for element <" + elem_name + ">");

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
	 * Ritorna il content graph associato ad un elemento
	 * 
	 * @param elem_name il nome dell'elemento
	 * @throws DtdRulesManagerException
	 */
	protected ContentGraph getContentGraph(String elem_name) throws DtdRulesManagerException {
		if (queryTextContent(elem_name)) // don't explode text and mixed
			// elements to save iterations
			return createTextContentGraph(elem_name);

		DFSA elem_rule = (DFSA) rules.get(elem_name);
		if (elem_rule == null)
			throw new DtdRulesManagerException("No rule for element <" + elem_name + ">");
		return elem_rule.createContentGraph();
	}

	// ------------ QUERY SUL CONTENUTO DI UN ELEMENTO --------------------

	/**
	 * Controlla se una collezione di nomi di elementi puo rappresentare un insieme di
	 * figli di un nodo
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param elem_children la collezione dei nomi dei figli
	 * @throws DtdRulesManagerException
	 */
	public boolean isValid(String elem_name, Collection elem_children) throws DtdRulesManagerException {
		return isValid(elem_name, elem_children, false);
	}

	/**
	 * Controlla se una collezione di nomi di elementi puo rappresentare un insieme di
	 * figli di un nodo
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param elem_children la collezione dei nomi dei figli
	 * @param with_gaps <code>true</code> se l'allineamento puo' essere fatto con gaps
	 * @throws DtdRulesManagerException
	 */
	public boolean isValid(String elem_name, Collection elem_children, boolean with_gaps) throws DtdRulesManagerException {
		// text elements must have no children
		if (elem_name.startsWith("#"))
			return (elem_children.size() == 0);

		// get automata
		DFSA rule = (DFSA) rules.get(elem_name);
		if (rule == null)
			throw new DtdRulesManagerException("No rule for element <" + elem_name + ">");

		// check if the children list is valid
		if (!rule.align(elem_children, with_gaps)) {
			// if the children list is empty try to add an empty text
			Vector empty_list = new Vector();
			empty_list.add("#PCDATA");
			return rule.align(elem_children, with_gaps);
		}
		return true;
	}

	/**
	 * Ritorna l'allineamento piu' corto della collezione di nodi desiderata con l'automa
	 * che rappresenta il contenuto dell'elemento, inserendo dei gaps dove necessario
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param elem_children la collezione dei nomi dei figli
	 * @return <code>null</code> se non esiste un allineamento, altrimenti la sequenza
	 *         di nodi che allinea con l'automa
	 * @throws DtdRulesManagerException
	 */
	public Vector getGappedAlignment(String elem_name, Collection elem_children) throws DtdRulesManagerException {
		// text elements must have no children
		if (elem_name.startsWith("#"))
			return new Vector();

		// get automata
		DFSA rule = (DFSA) rules.get(elem_name);
		if (rule == null)
			throw new DtdRulesManagerException("No rule for element <" + elem_name + ">");

		// return the alignment
		return rule.getGappedAlignment(elem_children);
	}

	/**
	 * Enumera le alternative possibili dati il nodo padre ed i nomi dei nodi figli.
	 * 
	 * @param elem_name il nome dell'elemento padre
	 * @param elem_children la collezione dei nomi dei figli
	 * @param choice_point la posizione nella sequenza di figli dopo la quale valutare le
	 *            alternative
	 * @throws DtdRulesManagerException
	 */
	public Collection getAlternatives(String elem_name, Collection elem_children, int choice_point) throws DtdRulesManagerException {
		if (elem_name.startsWith("#"))
			return new Vector();

		DFSA rule = (DFSA) rules.get(elem_name);
		if (rule == null)
			throw new DtdRulesManagerException("No rule for element <" + elem_name + ">");

		return rule.alignAlternatives(elem_children, choice_point);
	}

	/**
	 * Ritorna un vettore di stringhe contenente il nome dei figli di un nodo I nodi che
	 * non sono testo o elementi vengono ignorati
	 * 
	 * @param node il nodo padre
	 * @throws DtdRulesManagerException
	 */
	public Vector getChildren(Node node) throws DtdRulesManagerException {
		if (node.getNodeType() != Node.ELEMENT_NODE)
			return new Vector();

		Vector str_children = new Vector();
		NodeList dom_children = node.getChildNodes();
		for (int i = 0; i < dom_children.getLength(); i++) {
			Node dom_child = dom_children.item(i);
			if (dom_child.getNodeType() == Node.ELEMENT_NODE)
				str_children.add(dom_child.getNodeName());
			else if (dom_child.getNodeType() == Node.TEXT_NODE) {
				// String value = UtilLang.trimText(dom_child.getNodeValue());
				// if( value!=null && value.length()>0 )
				str_children.add(new String("#PCDATA"));
			} else
				str_children.add(new String("#ANY")); // comment and
			// processing
			// instructions can go
			// everywhere
		}
		return str_children;
	}

	/**
	 * Ritorna il nome di un nodo
	 * 
	 * @param node il nodo di cui si richiede il nome
	 * @throws DtdRulesManagerException
	 */
	public String getNodeName(Node dom_node) throws DtdRulesManagerException {
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
	 * @throws DtdRulesManagerException
	 */
	public int getChildIndex(Node parent, Node child) throws DtdRulesManagerException {
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
		throw new DtdRulesManagerException("Cannot find the child element in the children list");
	}

	/**
	 * Controlla se il contenuto di un nodo &egrave valido
	 * 
	 * @param node il nodo da validare
	 * @throws DtdRulesManagerException
	 */
	public boolean queryIsValid(Node dom_node) throws DtdRulesManagerException {
		if (dom_node == null)
			throw new DtdRulesManagerException("node is null");
		if (dom_node.getNodeType() != Node.ELEMENT_NODE)
			return true;
		return isValid(getNodeName(dom_node), getChildren(dom_node));
	}

	/**
	 * Controlla se un nodo pu&ograve contenere del testo
	 * 
	 * @param node il nodo in esame
	 * @throws DtdRulesManagerException
	 */
	public boolean queryTextContent(Node dom_node) throws DtdRulesManagerException {
		if (dom_node == null)
			throw new DtdRulesManagerException("node is null");
		if (dom_node.getNodeType() != Node.ELEMENT_NODE)
			return true;
		return isValid(getNodeName(dom_node), UtilLang.singleton("#PCDATA"));
	}

	/**
	 * Controlla se un nodo pu&ograve contenere del testo
	 * 
	 * @param elem_name il nome del nodo in esame
	 * @throws DtdRulesManagerException
	 */
	public boolean queryTextContent(String elem_name) throws DtdRulesManagerException {
		if (elem_name.compareTo("#PCDATA") == 0)
			return true;
		return isValid(elem_name, UtilLang.singleton("#PCDATA"));
	}

	/**
	 * Controlla se &egrave possibile appendere un nodo ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_node il nodo da appendere
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanAppend(Node parent, Node new_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (new_node == null)
			throw new DtdRulesManagerException("node to append is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		str_children.add(getNodeName(new_node));
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Controlla se &egrave possibile appendere una collezione di nodi ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_nodes i nodi da appendere
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanAppend(Node parent, Collection new_nodes) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");

		int c = 0;
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		for (Iterator i = new_nodes.iterator(); i.hasNext(); c++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new DtdRulesManagerException("node #" + c + " to append is null");
			str_children.add(getNodeName(new_node));
		}

		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Ritorna i possibili nodi da appendere ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @throws DtdRulesManagerException
	 */
	public Collection queryAppendable(Node parent) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		return getAlternatives(getNodeName(parent), str_children, str_children.size() - 1);
	}

	/**
	 * Controlla se &egrave possibile pre-pendere un nodo ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_node il nodo da pre-pendere
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanPrepend(Node parent, Node new_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (new_node == null)
			throw new DtdRulesManagerException("node to prepend is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		str_children.insertElementAt(getNodeName(new_node), 0);
		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Controlla se &egrave possibile pre-pendere una collezione di nodi ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @param new_nodes i nodi da pre-pendere
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanPrepend(Node parent, Collection new_nodes) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");

		int inserted = 0;
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new DtdRulesManagerException("node #" + inserted + " to prepend is null");
			str_children.insertElementAt(getNodeName(new_node), inserted);
		}

		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Ritorna i possibili nodi da pre-pendere ad un elemento
	 * 
	 * @param parent il nodo padre
	 * @throws DtdRulesManagerException
	 */
	public Collection queryPrependable(Node parent) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		return getAlternatives(getNodeName(parent), str_children, -1);
	}

	/**
	 * Controlla se &egrave possibile inserire un nodo dopo un certo figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio dopo cui inserire il nuovo nodo
	 * @param new_node il nodo da inserire
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanInsertAfter(Node parent, Node child_node, Node new_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");
		if (new_node == null)
			throw new DtdRulesManagerException("node to insert is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanInsertAfter(Node parent, Node child_node, Collection new_nodes) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");

		int inserted = 0;
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new DtdRulesManagerException("node #" + inserted + " to insert is null");
			str_children.insertElementAt(getNodeName(new_node), child_index + inserted + 1);
		}

		return isValid(getNodeName(parent), str_children);
	}

	/**
	 * Ritorna i nodi che &egrave possibile inserire dopo un certo figlio di un elemento
	 * 
	 * @param parent il nodo padre
	 * @param child_node il figlio dopo cui inserire il nuovo nodo
	 * @throws DtdRulesManagerException
	 */
	public Collection queryInsertableAfter(Node parent, Node child_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanInsertBefore(Node parent, Node child_node, Node new_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");
		if (new_node == null)
			throw new DtdRulesManagerException("node to insert is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanInsertBefore(Node parent, Node child_node, Collection new_nodes) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");

		int inserted = 0;
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new DtdRulesManagerException("node #" + inserted + " to insert is null");
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
	 * @throws DtdRulesManagerException
	 */
	public Collection queryInsertableBefore(Node parent, Node child_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		// System.out.println("parent=" + getNodeName(parent) + " children=" +
		// str_children + " point=" + child_index);
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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanEncloseIn(Node parent, Node child_node, int no_children, Node new_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");
		if (new_node == null)
			throw new DtdRulesManagerException("node to insert is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

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
	 * @throws DtdRulesManagerException
	 */
	public Collection queryContainers(Node parent, Node child_node, int no_children) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanEncloseTextIn(Node parent, Node child_node, Node new_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");
		if (child_node.getNodeType() != Node.TEXT_NODE)
			throw new DtdRulesManagerException("Cannot enclose text: child node is not a text node");
		if (new_node == null)
			throw new DtdRulesManagerException("node to insert is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

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
	 * @throws DtdRulesManagerException
	 */
	public Collection queryTextContainers(Node parent, Node child_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanReplaceWith(Node parent, Node child_node, int no_children, Collection new_nodes) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");

		// get the children
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		// find the first child
		int child_index = getChildIndex(parent, child_node);

		// get the children to enclose
		// Vector toreplace = extractSubList(str_children, child_index,
		// no_children);

		// replace the children to enclose with the new nodes
		int inserted = 0;
		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new DtdRulesManagerException("node #" + inserted + " to insert is null");
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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanReplaceTextWith(Node parent, Node child_node, Collection new_nodes) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");
		if (child_node.getNodeType() != Node.TEXT_NODE)
			throw new DtdRulesManagerException("Cannot enclose text: child node is not a text node");

		// get the children
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		// find the text node
		int child_index = getChildIndex(parent, child_node);

		// insert the new nodes inside the text
		int inserted = 0;
		str_children.insertElementAt("#PCDATA", child_index + 1);
		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new DtdRulesManagerException("node #" + inserted + " to insert is null");
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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanDelete(Node parent, Node child_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanDelete(Node parent, Node child_node, int no_children) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int from_index = getChildIndex(parent, child_node);
		for (int i = 0; i < no_children; i++) {
			if (str_children.size() == from_index)
				throw new DtdRulesManagerException("invalid number of children to remove");
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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanInsertInside(Node parent, Node child_node, Node new_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");
		if (new_node == null)
			throw new DtdRulesManagerException("node to insert is null");
		if (child_node.getNodeType() != Node.TEXT_NODE)
			throw new DtdRulesManagerException("Cannot insert inside: child node is not a text node");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryCanInsertInside(Node parent, Node child_node, Collection new_nodes) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");
		if (child_node.getNodeType() != Node.TEXT_NODE)
			throw new DtdRulesManagerException("Cannot insert inside: child node is not a text node");

		int inserted = 0;
		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		for (Iterator i = new_nodes.iterator(); i.hasNext(); inserted++) {
			Node new_node = (Node) i.next();
			if (new_node == null)
				throw new DtdRulesManagerException("node #" + inserted + " to insert is null");
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
	 * @throws DtdRulesManagerException
	 */
	public Collection queryInsertableInside(Node parent, Node child_node) throws DtdRulesManagerException {
		if (parent == null)
			throw new DtdRulesManagerException("parent is null");
		if (child_node == null)
			throw new DtdRulesManagerException("child is null");
		if (child_node.getNodeType() != Node.TEXT_NODE)
			throw new DtdRulesManagerException("Cannot insert inside: child node is not a text node");

		Vector str_children = getChildren(parent);
		if (pre_check && !isValid(getNodeName(parent), str_children))
			throw new DtdRulesManagerException("element <" + getNodeName(parent) + "> has invalid content: " + str_children);

		int child_index = getChildIndex(parent, child_node);
		if (((String) str_children.get(child_index)).compareTo("#PCDATA") != 0)
			throw new DtdRulesManagerException("Cannot insert inside: child node is not a text node");
		str_children.insertElementAt(new String("#PCDATA"), child_index + 1);
		return getAlternatives(getNodeName(parent), str_children, child_index);
	}

	// ------------ QUERY SUGLI ATTRIBUTI DI UN ELEMENTO --------------------

	private AttributeDeclaration getAttributeDeclaration(String elem_name, String att_name) throws DtdRulesManagerException {
		if (elem_name == "#PCDATA")
			throw new DtdRulesManagerException("No attributes for element <" + elem_name + ">");
		if (!rules.containsKey(elem_name))
			throw new DtdRulesManagerException("No rule for element <" + elem_name + ">");

		HashMap att_hash = (HashMap) attributes.get(elem_name);
		if (att_hash == null)
			throw new DtdRulesManagerException("No attributes for element <" + elem_name + ">");

		AttributeDeclaration att_decl = (AttributeDeclaration) att_hash.get(att_name);
		if (att_decl == null)
			throw new DtdRulesManagerException("No attribute <" + att_name + "> for element <" + elem_name + ">");
		return att_decl;
	}

	/**
	 * Ritorna la lista dei nomi degli attributi di un elemento
	 * 
	 * @param elem_name il nome dell'elemento di cui si vuole la lista degli attributi
	 * @throws DtdRulesManagerException
	 */
	public Collection queryGetAttributes(String elem_name) throws DtdRulesManagerException {
		if (elem_name == "#PCDATA")
			return new Vector();
		if (!rules.containsKey(elem_name))
			throw new DtdRulesManagerException("No rule for element <" + elem_name + ">");

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
	 * @throws DtdRulesManagerException
	 */
	public String queryGetAttributeDefaultValue(String elem_name, String att_name) throws DtdRulesManagerException {
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
	 * @throws DtdRulesManagerException
	 */
	public Collection queryGetAttributePossibleValues(String elem_name, String att_name) throws DtdRulesManagerException {
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
	 * @throws DtdRulesManagerException
	 */
	public boolean queryIsValidAttribute(String elem_name, String att_name) throws DtdRulesManagerException {
		if (elem_name == "#PCDATA")
			throw new DtdRulesManagerException("No attributes for element <" + elem_name + ">");
		if (!rules.containsKey(elem_name))
			throw new DtdRulesManagerException("No rule for element <" + elem_name + ">");
		return (attributes.containsKey(elem_name) && ((HashMap) attributes.get(elem_name)).containsKey(att_name));
	}

	/**
	 * Ritorna <code>true</code> se l'attributo specificato &egrave di tipo
	 * <code>#REQUIRED</code>
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede l'esistena dell'attributo
	 * @param att_name il nome dell'attributo
	 * @throws DtdRulesManagerException
	 */
	public boolean queryIsRequiredAttribute(String elem_name, String att_name) throws DtdRulesManagerException {
		AttributeDeclaration att_decl = getAttributeDeclaration(elem_name, att_name);
		return ((att_decl.valueDefault != null) && (att_decl.valueDefault.equalsIgnoreCase("#REQUIRED") || att_decl.valueDefault.equalsIgnoreCase("#FIXED")));
	}

	/**
	 * Ritorna <code>true</code> se l'attributo specificato &egrave di tipo
	 * <code>#FIXED</code>
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede l'esistena dell'attributo
	 * @param att_name il nome dell'attributo
	 * @throws DtdRulesManagerException
	 */
	public boolean queryIsFixedAttribute(String elem_name, String att_name) throws DtdRulesManagerException {
		AttributeDeclaration att_decl = getAttributeDeclaration(elem_name, att_name);
		return (att_decl.valueDefault == "#FIXED");
	}

	/**
	 * Ritorna <code>true</code> se l'attributo specificato per questo elemento
	 * pu&ograve assumere il valore richiesto
	 * 
	 * @param elem_name il nome dell'elemento di cui si chiede la validit&agrave del
	 *            valore dell'attributo
	 * @param att_name il nome dell'attributo
	 * @param value il valore dell'attributo
	 * @throws DtdRulesManagerException
	 */
	public boolean queryIsValidAttributeValue(String elem_name, String att_name, String value) throws DtdRulesManagerException {
		AttributeDeclaration att_decl = getAttributeDeclaration(elem_name, att_name);
		if (att_decl.valueDefault == "#FIXED") {
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
	 * @throws DtdRulesManagerException
	 */
	public void fillRequiredAttributes(Node elem) throws DtdRulesManagerException {
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

	/*
	 * private void printAttributes(Node elem) { org.w3c.dom.NamedNodeMap att_nodes =
	 * elem.getAttributes(); for (int i = 0; i < att_nodes.getLength(); i++) { Node att =
	 * att_nodes.item(i); System.out.println(" attribute: name=" + att.getNodeName() + "
	 * value=" + att.getNodeValue()); } }
	 */

}
