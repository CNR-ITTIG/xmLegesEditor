package it.cnr.ittig.xmleges.core.util.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import org.w3c.dom.Node;

/**
 * Classe di utilit&agrave; per la gestione della clipboard.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class UtilClipboard {

	/**
	 * Copia il testo <code>text</code> nella clipboard di sistema.
	 * 
	 * @param text testo da copiare nella clipboard
	 */
	public static void set(String text) {
		StringSelection s = new StringSelection(new String(text));
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(s, null);
	}

	/**
	 * Copia il nodo <code>node</code> nella clipboard di sistema.
	 * 
	 * @param node nodo da copiare nella clipboard
	 */
	public static void set(Node node) {
		NodeSelection ns = new NodeSelection(node.cloneNode(true));
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(ns, null);
	}

	/**
	 * Indica se nella clipboard di sistema &egrave; presente una stringa.
	 * 
	 * @return <code>true</code> se la clipboard contiene una stringa
	 */
	public static boolean hasString() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipboardContent = clipboard.getContents(null);
		if (clipboardContent != null && clipboardContent.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return true;
		}
		return false;
	}

	/**
	 * Indica se nella clipboard di sistema &egrave; presente un nodo DOM.
	 * 
	 * @return <code>true</code> se la clipboard contiene un nodo DOM
	 */
	public static boolean hasNode() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipboardContent = clipboard.getContents(null);
		if (clipboardContent != null && clipboardContent.isDataFlavorSupported(NodeSelection.NodeFlavor)) {
			return true;
		}
		return false;
	}

	/**
	 * Restituisce il contenuto della clipboard di sistema come
	 * <code>java.lang.String</code>.
	 * 
	 * @return stringa contenuta nella clipboard
	 */
	public static String getAsString() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipboardContent = clipboard.getContents(null);
		String s = null;
		try {
			s = (String) clipboardContent.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * Restituisce il contenuto della clipboard di sistema come
	 * <code>org.w3c.dom.Node</code>.
	 * 
	 * @return stringa contenuta nella clipboard
	 */
	public static Node getAsNode() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable clipboardContent = clipboard.getContents(null);
		Node n = null;
		try {
			n = (Node) clipboardContent.getTransferData(NodeSelection.NodeFlavor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return n;
	}
}