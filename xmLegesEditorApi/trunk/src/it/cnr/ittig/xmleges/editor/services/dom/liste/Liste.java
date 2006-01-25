package it.cnr.ittig.xmleges.editor.services.dom.liste;

import it.cnr.ittig.services.manager.Service;

import org.w3c.dom.Node;

/**
 * Servizio per la gestione DOM delle liste ordinate, non ordinate e puntate
 * html.
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
public interface Liste extends Service {

	public int canCreaLista(Node node, String tipolista);

	// public int canCreaListaNUM(Node node);

	public boolean canMuoviSu(Node node);

	public boolean canMuoviGiu(Node node);

	public boolean canPromuovi(Node node);

	public boolean canRiduci(Node node);

	public Node muoviSu(Node node);

	public Node muoviGiu(Node node);

	public Node promuovi(Node node);

	public Node riduci(Node node, String tipolista);

	public Node creaLista(Node node, String tipolista);

	// public Node creaListaNUM(Node node);

	public Node creaListaDEF(Node node);
}
