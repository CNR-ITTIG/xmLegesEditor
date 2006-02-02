package it.cnr.ittig.xmleges.core.blocks.lookandfeel;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.lookandfeel.LookAndFeel;

import javax.swing.UIManager;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.util.services.lookandfeel</code>.</h1>
 * <h1>Descrizione</h1>
 * Imposta l'aspetto grafico in base alla classe specificata nella configurazione.
 * <h1>Configurazione</h1>
 * La configurazione pu&ograve; contenere il tag <code>&lt;lookandfeel&gt;</code> che
 * specifica la classe di descrizione dell'aspetto grafico. <br>
 * Esempio:
 * 
 * <pre>
 *   &lt;lookandfeel&gt;com.jgoodies.plaf.plastic.Plastic3DLookAndFeel&lt;(lookandfeel&gt;
 * </pre>
 * 
 * <h1>Dipendenze</h1>
 * Nessuna.
 * <h1>I18n</h1>
 * Nessuno.
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
 * @see javax.swing.LookAndFeel
 * @see javax.swing.UIManager
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class LookAndFeelImpl implements LookAndFeel, Loggable, Configurable, Initializable {

	Logger logger;

	String lookAndFeel = null;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		try {
			lookAndFeel = configuration.getChild("lookandfeel").getValue();
		} catch (Exception ex) {
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		if (lookAndFeel != null)
			try {
				logger.info("Setting look and feel to:" + lookAndFeel);
				UIManager.setLookAndFeel(lookAndFeel);
				logger.info("Setting look and feel OK");
			} catch (Exception ex) {
				logger.error("LookAndFeel error", ex);
			}
	}

}
