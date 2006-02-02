package it.cnr.ittig.xmleges.core.util.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.w3c.dom.Node;

/**
 * Classe astratta per definire il <i>DragAndDrop </i> di stringhe e nodi dom
 * tra componenti. Per utilizzare il <i>DragAndDrop </i> &egrave; necessario
 * estendere la classe <code>DomTransferHandler</code>, impostarla come
 * TransferHandler del componente grafico e attivare il supporto del
 * <i>DragAndDrop </i>. <br>
 * Esempio:
 * 
 * <pre>
 *     public class MyTransferHandler extends DomTransferHandler {
 *      ...
 *     }
 *     
 *     JComponent c = ...
 *     c.setTransferHandler(new MyTransferHandler());
 *     c.setDragEnabled(true);
 *     
 * </pre>
 * 
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
public abstract class DomTransferHandler extends TransferHandler {

	static protected boolean shouldRemove;

	/**
	 * Crea un oggetto <code>DomTransferable</code> per il trasferimento di un
	 * nodo DOM o di una stringa.
	 * 
	 * @param comp componente sorgente del <i>DragAndDrop </i>
	 * @throws RuntimeException se i metodi <code>isDomSupported()</code> e is
	 *         <code>StringSupported()</code> restituisco entrambi
	 *         <code>false</code>
	 */
	protected Transferable createTransferable(JComponent c) {
		if (isDomSupported(c))
			return new DomTransferable(exportDom(c));
		if (isStringSupported(c))
			return new DomTransferable(exportString(c));
		throw new RuntimeException("Dom and String are not supported.");
	}

	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	public boolean importData(JComponent c, Transferable t) {
		DataFlavor[] dataFlavors = t.getTransferDataFlavors();
		if (canImport(c, dataFlavors)) {
			try {
				if (isDomSupported(c))
					return importDom(c, (Node) t.getTransferData(DomTransferable.domFlavor));
				else
					return importString(c, (String) t.getTransferData(DataFlavor.stringFlavor));
			} catch (UnsupportedFlavorException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return false;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {
		cleanup(c, action == MOVE);
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (DomTransferable.domFlavor.equals(flavors[i]) && isDomSupported(c))
				return true;
			if (DataFlavor.stringFlavor.equals(flavors[i]) && isStringSupported(c))
				return true;
		}
		return false;
	}

	/**
	 * Indica se la classe supporta la gestione del trasferimento dei nodi DOM.
	 * 
	 * @param comp componente interessato al <i>DragAndDrop </i>
	 * @return <code>true</code> se &egrave; gestita
	 */
	protected abstract boolean isDomSupported(JComponent comp);

	/**
	 * Esporta il nodo DOM dal componente <code>comp</code> per il
	 * trasferimento a un altro componente.
	 * 
	 * @param comp componente sorgente del <i>DragAndDrop </i>
	 * @return nodo da trasferire
	 */
	protected abstract Node exportDom(JComponent comp);

	/**
	 * Importa il nodo <code>node</code> nel componente <code>comp</code>.
	 * 
	 * @param comp componente destinazione del <i>DragAndDrop </i>
	 * @param node nodo da importare
	 * @return <code>true</code> se l'importazione &egrave; riuscita
	 */
	protected abstract boolean importDom(JComponent comp, Node node);

	/**
	 * Indica se la classe supporta la gestione del trasferimento delle
	 * stringhe.
	 * 
	 * @param comp componente interessato al <i>DragAndDrop </i>
	 * @return <code>true</code> se &egrave; gestita
	 */
	protected abstract boolean isStringSupported(JComponent comp);

	/**
	 * Esporta la stringa dal componente <code>comp</code> per il
	 * trasferimento a un altro componente.
	 * 
	 * @param comp componente sorgente del <i>DragAndDrop </i>
	 * @return stringa da trasferire
	 */
	protected abstract String exportString(JComponent comp);

	/**
	 * Importa la stringa <code>str</code> nel componente <code>comp</code>.
	 * 
	 * @param comp componente destinazione del <i>DragAndDrop </i>
	 * @param str stringa da importare
	 * @return <code>true</code> se l'importazione &egrave; riuscita
	 */
	protected abstract boolean importString(JComponent comp, String str);

	/**
	 * Metodo invocato al termine dell'operazione di Drag And Drop. Se
	 * l'operazione &egrave; di spostamento il parametro <code>remove</code>
	 * &egrave; uguale a <code>true</code>.
	 * 
	 * @param comp componente sorgente del <i>DragAndDrop </i>
	 * @param remove <code>true</code> se l'operazione &egrave; di spostamento
	 */
	protected abstract void cleanup(JComponent comp, boolean remove);

}
