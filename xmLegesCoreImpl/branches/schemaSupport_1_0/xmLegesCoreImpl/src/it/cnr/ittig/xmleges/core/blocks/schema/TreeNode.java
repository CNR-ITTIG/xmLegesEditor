/**
 * Classe che rappresenta il nodo di un albero m-ario 
 * dove ogni figlio contiene un valore distinto.
 * Va bene per rappresentare il default content di un elemento
 * perche' si suppone che non vi siano ripetizioni nei figli,
 * visto che si cerca la minima rappresentazione dell'elemento.
 */

package it.cnr.ittig.xmleges.core.blocks.schema;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

public class TreeNode implements Serializable {

	/**
	 * Valore contenuto nel nodo dell'albero
	 */
	protected String value;

	/**
	 * Vettore dei figli del nodo. Si usa un vettore perche' l'ordine dei figli deve
	 * rispettare quello di inserimento
	 */
	protected Vector children;

	/**
	 * Costruttore di default
	 */
	public TreeNode(String val) {
		value = val;
		children = new Vector();
	}

	/**
	 * Ritorna il valore contenuto nel nodo dell'albero
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Ritorna l'insieme dei figli del nodo
	 */
	public Vector getChildren() {
		return children;
	}

	/**
	 * Ritorna il figlio del nodo che ha <code>val</code> come valore. Ritorna
	 * <code>null</code> se non esiste nessun figlio con questo valore.
	 */
	public TreeNode getChild(String val) {
		for (Iterator i = children.iterator(); i.hasNext();) {
			TreeNode child = (TreeNode) i.next();
			if (child.getValue().compareTo(val) == 0)
				return child;
		}
		return null;
	}

	/**
	 * Aggiunge un nuovo figlio se non e' gia' presente. Ritorna il figlio corrispondente
	 * a questo valore
	 */
	public TreeNode addChild(String val) {
		TreeNode new_node = getChild(val);
		if (new_node == null) {
			new_node = new TreeNode(val);
			children.add(new_node);
		}
		return new_node;
	}

	public String toString(int level) {
		String out = "";
		for (int i = 0; i < level; i++)
			out = out + "\t";
		out = out + "<" + value + ">\n";
		for (Iterator i = children.iterator(); i.hasNext();)
			out = out + ((TreeNode) i.next()).toString(level + 1);
		return out;
	}

	public String toString() {
		return toString(0);
	}
}
