package it.cnr.ittig.xmleges.core.blocks.spellcheck.dom;

import it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheckWord;
import it.cnr.ittig.xmleges.core.services.spellcheck.dom.DomSpellCheckWord;

import org.w3c.dom.Node;

/**
 * Interfaccia per indicare la parola errata all'interno di un nodo DOM.
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
 * @author <a href="mailto:agnoloni@dsi.unifi.it">Tommaso Agnoloni </a>
 */
public class DomSpellCheckWordImpl implements DomSpellCheckWord {

	private SpellCheckWord spellCheckWord;

	private Node node;

	public DomSpellCheckWordImpl(SpellCheckWord spellCheckWord, Node node) {
		this.spellCheckWord = spellCheckWord;
		this.node = node;
	}

	public Node getNode() {
		return this.node;
	}

	public SpellCheckWord getSpellCheckWord() {
		return this.spellCheckWord;
	}

	public String toString() {
		return ("Node " + this.node.getNodeName() + "  " + this.spellCheckWord.toString());
	}

}
