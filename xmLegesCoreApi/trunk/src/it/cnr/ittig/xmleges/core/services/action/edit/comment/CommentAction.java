package it.cnr.ittig.xmleges.core.services.action.edit.comment;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per le azioni di inserimento di un <code>commento</code> e una
 * <code>processing-instruction</code>.
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
public interface CommentAction extends Service {

	/**
	 * Effettua l'inserimento di un commento.
	 * 
	 * @return <code>true</code> se l'inserimento &egrave; stato effettuato.
	 */
	public boolean doComment();

	/**
	 * Effettua l'inserimento di una <code>procesing-instruction</code>.
	 * 
	 * @return <code>true</code> se l'inserimento &egrave; stato effettuato.
	 */
	public boolean doProcessingInstruction();

}
