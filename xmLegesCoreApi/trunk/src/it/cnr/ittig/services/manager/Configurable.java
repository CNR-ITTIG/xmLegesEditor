package it.cnr.ittig.services.manager;

/**
 * Classe necessaria ai componenti per ricevere la configurazione.
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
public interface Configurable {

	/**
	 * Imposta la configurazione al componente che implementa questa
	 * interfaccia.
	 * 
	 * @param configuration configurazione
	 * @throws ConfigurationException
	 */
	public void configure(Configuration configuration) throws ConfigurationException;
}
