package it.cnr.ittig.xmleges.editor.services.dom.tabelle;

import it.cnr.ittig.services.manager.Service;

import java.util.Vector;

import org.w3c.dom.Node;

/**
 * Servizio per la gestione delle tabelle (tag h:table, h:th, h:tr, h:td).
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface Tabelle extends Service {

	/**
	 * Indica se &egrave; possibile inserire una tabella come figlio del nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile inserire una tabella
	 */
	public int canInsertTable(Node node);

	/**
	 * Indica se &egrave; possibile cancellare la tabella individuata dal nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo della tabella
	 * @return <code>true</code> se &egrave; possibile cancellare la tabella
	 */
	public boolean canDeleteTable(Node node);

	/**
	 * Indica se &egrave; possibile inserire una riga prima di quella
	 * individuata dal nodo <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile inserire
	 */
	public boolean canPrepRiga(Node node);

	/**
	 * Indica se &egrave; possibile inserire una riga dopo quella individuata
	 * dal nodo <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile inserire
	 */
	public boolean canAppRiga(Node node);

	/**
	 * Indica se &egrave; possibile cancellare la riga individuata dal nodo
	 * <code>node</code>.
	 * 
	 * @param node riga
	 * @return <code>true</code> se &egrave; possibile cancellare la riga
	 */
	public boolean canDeleteRiga(Node node);

	/**
	 * Indica se &egrave; possibile inserire una colonna prima di quella
	 * individuata dal nodo <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile inserire
	 */
	public boolean canPrepColonna(Node node);

	/**
	 * Indica se &egrave; possibile inserire una colonna dopo quella individuata
	 * dal nodo <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile inserire
	 */
	public boolean canAppColonna(Node node);

	/**
	 * Indica se &egrave; possibile cancellare la riga individuata dal nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile cancellare
	 */
	public boolean canDeleteColonna(Node node);

	/**
	 * Indica se &egrave; possibile unire le righe individuate dai nodi
	 * <code>node1</code> e <code>node2</code>.
	 * 
	 * @param node1 prima riga
	 * @param node2 seconda riga
	 * @return <code>true</code> se &egrave; possibile unire
	 */
	public boolean canMergeRighe(Node node1, Node node2);

	/**
	 * Indica se &egrave; possibile allineare il testo della colonna individuata
	 * dal nodo <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile allineare il testo
	 */
	public boolean canAllignTextCol(Node node);

	/**
	 * Crea una tabella.
	 * 
	 * @param righe numero di righe
	 * @param colonne numero di colonne
	 * @param caption <code>true</code> per inserire il titolo della tabella
	 * @param head <code>true</code> per inserire l'intestazione
	 * @param foot <code>true</code> per inserire il pi&egrave; di tabella
	 * @return tabella
	 */
	public Node creaTabella(int righe, int colonne, boolean caption, boolean head, boolean foot);

	/**
	 * Indica se &egrave; possibile cancellare la tabella individuata dal nodo
	 * <code>node</code>.
	 * 
	 * @param node nodo di riferimento
	 * @return <code>true</code> se &egrave; possibile cancellare
	 */
	public void eliminaTabella(Node node);

	/**
	 * Crea una riga prima o dopo al nodo <code>pos</code>.
	 * 
	 * @param pos nodo di riferimento
	 * @param appendi <code>true</code> per appenderla
	 * @return riga creata
	 */
	public Node creaRiga(Node pos, boolean appendi);

	/**
	 * Elimina la riga individuata dal nodo <code>pos</code>.
	 * 
	 * @param pos riga
	 */
	public void eliminaRiga(Node pos);

	/**
	 * Unisce le righe individuate dai nodi <code>pos1</code> e
	 * <code>pos2</code>.
	 * 
	 * @param pos1 prima riga
	 * @param pos2 seconda riga
	 */
	public void mergeRighe(Node pos1, Node pos2);

	/**
	 * Crea una colonna prima o dopo al nodo <code>pos</code>.
	 * 
	 * @param pos nodo di riferimento
	 * @param appendi <code>true</code> per appenderla
	 * @return array delle celle create
	 */
	public Node[] creaColonna(Node pos, boolean appendi);

	/**
	 * Elimina la colonna individuata dal nodo <code>pos</code>.
	 * 
	 * @param pos riga
	 */
	public void eliminaColonna(Node pos);

	/**
	 * Allinea il testo della colonna individuata dal nodo <code>pos</code>.
	 * 
	 * @param pos colonna
	 * @param allinea tipo di allineamento
	 */
	public void allineaTestoCol(Node pos, String allinea);

	public Vector getChildHtd(org.w3c.dom.Node elem);
}
