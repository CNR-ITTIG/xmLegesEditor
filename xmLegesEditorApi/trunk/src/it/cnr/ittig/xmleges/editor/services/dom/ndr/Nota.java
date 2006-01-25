package it.cnr.ittig.xmleges.editor.services.dom.ndr;

import it.cnr.ittig.xmleges.core.util.dom.UtilDom;

import org.w3c.dom.Node;

/**
 * Classe per la rappresentazione delle note.
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
 * @version 1.0
 * @author <a href="mailto:agnoloni@dsi.unifi.it">Tommaso Agnoloni </a>
 */
public class Nota {

	/** Id della nota */
	String id;

	/** Nodo dom della nota */
	Node nota;

	public Nota(Node nota) {
		setNota(nota);
		setId(UtilDom.getAttributeValueAsString(nota, "id"));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNota(Node nota) {
		this.nota = nota;
	}

	public Node getNota() {
		return nota;
	}

	public String toString() {
		return id + "  " + UtilDom.restrict(UtilDom.getRecursiveTextNode(nota), 30);
	}
}
