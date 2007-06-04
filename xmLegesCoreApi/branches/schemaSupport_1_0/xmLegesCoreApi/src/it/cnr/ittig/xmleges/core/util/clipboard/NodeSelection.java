package it.cnr.ittig.xmleges.core.util.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.w3c.dom.Node;

/**
 * Wrapper per trasferire un nodo alla/dalla clipboard
 * 
 * @author Tommaso Paba <t.paba@onetech.it>
 */
public class NodeSelection implements Transferable, ClipboardOwner {

	static public DataFlavor NodeFlavor;

	private DataFlavor[] flavors = { NodeFlavor };

	private Node node;

	static {
		try {
			NodeFlavor = new DataFlavor(Class.forName("org.w3c.dom.Node"), "XML Node");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public NodeSelection(Node node) {
		this.node = node;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(NodeFlavor);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

		if (isDataFlavorSupported(flavor)) {
			return node;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	public void lostOwnership(Clipboard arg0, Transferable arg1) {
	}
}
