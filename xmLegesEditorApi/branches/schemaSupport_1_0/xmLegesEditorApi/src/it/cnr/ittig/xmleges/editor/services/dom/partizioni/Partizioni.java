package it.cnr.ittig.xmleges.editor.services.dom.partizioni;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per la creazione di nuove partizioni dell'articolato o loro
 * aggregazione.
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface Partizioni extends Service {
	/** Indica la partizione di tipo: LIBRO */
	public final static String LIBRO = "libro";

	/** Indica la partizione di tipo: PARTE */
	public final static String PARTE = "parte";

	/** Indica la partizione di tipo: TITOLO */
	public final static String TITOLO = "titolo";

	/** Indica la partizione di tipo: CAPO */
	public final static String CAPO = "capo";

	/** Indica la partizione di tipo: SEZIONE */
	public final static String SEZIONE = "sezione";

	/** Indica la partizione di tipo: ARTICOLO */
	public final static String ARTICOLO = "articolo";

	/** Indica la partizione di tipo: COMMA */
	public final static String COMMA = "comma";

	/** Indica la partizione di tipo: EL */
	public final static String EL = "el";

	/** Indica la partizione di tipo: EN */
	public final static String EN = "en";

	/** Indica la partizione di tipo: RUBRICA */
	public final static String RUBRICA = "rubrica";

	/**
	 * @param node nodo di partenza per effettuare l'inserimento
	 * @param elemName tipo di partizione
	 * @return -1 se non e' possibile inserire la partizione, altrimenti il
	 *         codice dell' operazione di inserimento ammessa
	 */
	public int canInsertNuovaPartizione(Node node, String elemName);

	/**
	 * @param node nodo di partenza per effettuare l'inserimento
	 * @param partizione nodo partizione
	 * @return -1 se non e' possibile inserire la partizione, altrimenti il
	 *         codice dell' operazione di inserimento ammessa
	 */
	public int canInsertNuovaPartizione(Node node, Node partizione);

	/**
	 * Inserisce una nuova partizione vuota di tipo <code>elemName</code>
	 * successiva al nodo <code>node</code> o ai suoi antenati. <br>
	 * Il tipo elemName deve essere uno tra le propriet&agrave; di questa
	 * interfaccia.
	 * 
	 * @param node nodo di partenza per effettuare l'inserimento
	 * @param elemName tipo di partizione
	 * @return nodo modificato; null se l'operazione non &egrave andata a buon
	 *         fine
	 */
	public Node nuovaPartizione(Node node, String elemName);

	/**
	 * Inserisce la nuova partizione <code>partizione</code> successiva al
	 * nodo <code>node</code> o ai suoi antenati.
	 * 
	 * @param node nodo di partenza per effettuare l'inserimento
	 * @param partizione nuova partizione
	 * @return nodo modificato; null se l'operazione non &egrave andata a buon
	 *         fine
	 */
	public Node nuovaPartizione(Node node, Node partizione);

	/**
	 * Inserisce una nuova partizione vuota di tipo <code>elemName</code>
	 * successiva al nodo <code>node</code> o ai suoi antenati. <br>
	 * Il tipo elemName deve essere uno tra le propriet&agrave; di questa
	 * interfaccia.
	 * 
	 * @param node nodo di partenza per effettuare l'inserimento
	 * @param elemName tipo di partizione
	 * @param action azione di inserimento ammessa
	 * @return nodo modificato; null se l'operazione non &egrave andata a buon
	 *         fine
	 */
	public Node nuovaPartizione(Node node, String elemName, int action);

	/**
	 * Inserisce la nuova partizione <code>partizione</code> successiva al
	 * nodo <code>node</code> o ai suoi antenati.
	 * 
	 * @param node nodo di partenza per effettuare l'inserimento
	 * @param partizione nuova partizione
	 * @param action azione di inserimento ammessa
	 * @return nodo modificato; null se l'operazione non &egrave andata a buon
	 *         fine
	 */
	public Node nuovaPartizione(Node node, Node partizione, int action);

	/**
	 * Aggrega i nodi <code>node[]</code> nella nuova partizione di tipo
	 * <code>elemName</code> costruendo opportunamente i nodi mancanti.
	 * L'aggregazione verr&agrave; inserita dopo l'ultimo nodo della selezione
	 * nel punto opportuno.
	 * 
	 * @param elemName tipo di partizione
	 * @param node nodi che devono essere aggragati
	 */
	public void aggregaInPartizione(String elemName, Node[] node);

}
