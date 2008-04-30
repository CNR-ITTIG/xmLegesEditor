package it.cnr.ittig.xmleges.core.blocks.document;

import it.cnr.ittig.xmleges.core.services.panes.problems.Problem;

import org.w3c.dom.Node;

/**
 * Classe per visualizzare i problemi relativi al documento aperto.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e Tecniche
 * dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class DocumentProblemImpl implements Problem {
	int type;

	String text;

	/**
	 * Costruttore di DocumentProblemImpl.
	 * 
	 * @param text testo del problema
	 */
	public DocumentProblemImpl(int type, String text) {
		super();
		this.type = type;
		this.text = text;
	}

	// /////////////////////////////////////////////////////// Problem Interface
	public int getType() {
		return this.type;
	}

	public String getText() {
		return this.text;
	}

	public boolean canRemoveByUser() {
		return false;
	}

	public Node getNode() {
		return null;
	}

	public boolean canResolveProblem() {
		return false;
	}

	public boolean resolveProblem() {
		return false;
	}

}
