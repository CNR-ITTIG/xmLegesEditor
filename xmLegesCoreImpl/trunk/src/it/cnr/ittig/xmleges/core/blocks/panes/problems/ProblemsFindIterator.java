package it.cnr.ittig.xmleges.core.blocks.panes.problems;

import it.cnr.ittig.xmleges.core.services.frame.FindIterator;
import it.cnr.ittig.xmleges.core.services.panes.problems.Problem;

import javax.swing.JList;
import javax.swing.ListModel;

/**
 * FindIterator per il testo contenuto nei problemi.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ProblemsFindIterator implements FindIterator {

	JList list;

	String toFind;

	String toFindLower;

	int lastFind;

	boolean caseSensitive = false;

	/**
	 * Costruttore di <code>ProblemsFindIterator</code>.
	 */
	public ProblemsFindIterator(JList list) {
		super();
		this.list = list;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void initFind(String text) {
		this.toFind = text;
		this.toFindLower = text.toLowerCase();
		this.lastFind = -1;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean next() {
		ListModel listModel = list.getModel();
		// for (int i = lastFind == -1 ? 0 : lastFind; i < listModel.getSize(); i++) {
		for (int i = lastFind + 1; i < listModel.getSize(); i++) {
			Problem problem = (Problem) listModel.getElementAt(i);
			String text = problem.getText();
			if ((caseSensitive && text.indexOf(toFind) != -1) || (!caseSensitive && text.toLowerCase().indexOf(toFindLower) != -1)) {
				lastFind = i;
				list.setSelectedIndex(i);
				return true;
			}
		}
		return false;
	}

	public boolean canReplace(String text) {
		return false;
	}

	public void replace(String text) {
	}

}
