package it.cnr.ittig.xmleges.core.util.dnd;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.w3c.dom.Node;

/**
 * Classe che mantiene i dati per il trasferimento di stringhe o nodi DOM
 * tramite la tecnica di <i>DragAndDrop </i>.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class DomTransferable implements Transferable, ClipboardOwner {

	/** Node oggetto del Drag And Drop */
	private Node node;

	/** Stringa oggetto del Drag And Drop */
	private String str;

	/**
	 * DataFlavor per i nodi dom.
	 */
	public final static DataFlavor domFlavor = new DataFlavor(Node.class, "Node");

	private static final DataFlavor[] flavors = { domFlavor, DataFlavor.stringFlavor };

	/**
	 * Costruttore di <code>DomTransferable</code> per il trasferimento di un
	 * nodo DOM.
	 * 
	 * @param node nodo da trasferire
	 */
	public DomTransferable(Node node) {
		this.node = node;
	}

	/**
	 * Costruttore di <code>DomTransferable</code> per il trasferimento di una
	 * stringa.
	 * 
	 * @param str stringa da trasferire
	 */
	public DomTransferable(String str) {
		this.str = str;
	}

	public DataFlavor[] getTransferDataFlavors() {
		System.out.println("DomTransferable.getTransferDataFlavors");
		return (DataFlavor[]) flavors.clone();
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		System.out.println("DomTransferable.isDataFlavorSupported");
		for (int i = 0; i < flavors.length; i++)
			if (flavor.equals(flavors[i]))
				return true;

		return false;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		System.out.println("DomTransferable.getTransferData");
		if (domFlavor.equals(flavor))
			return node;
		else if (DataFlavor.stringFlavor.equals(flavor))
			return str;
		else
			throw new UnsupportedFlavorException(flavor);
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		System.out.println("DomTransferable.lostOwnership");
	}

}
