package it.cnr.ittig.xmleges.core.blocks.threads;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.threads.ThreadManager;

import java.util.Vector;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.threads.ThreadManager</code>.</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * Nessuna
 * <h1>I18n</h1>
 * Nessuno
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ThreadManagerImpl implements ThreadManager, Loggable, Configurable, Initializable {
	Logger logger;

	int max = 1000;

	int min = 30;

	int spare = 20;

	Vector pool = null;

	int nThread = 1;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		try {
			max = configuration.getChild("max").getValueAsInteger();
		} catch (Exception ex) {
			logger.warn("no max configuration");
		}
		try {
			min = configuration.getChild("min").getValueAsInteger();
		} catch (Exception ex) {
			logger.warn("no min configuration");
		}
		try {
			spare = configuration.getChild("spare").getValueAsInteger();
		} catch (Exception ex) {
			logger.warn("no spare configuration");
		}
		logger.info("maxThread=" + max + " minThreads=" + min + " spareThreads" + spare);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		pool = new Vector(min, spare);
		createThreads(min);
	}

	protected void createThreads(int n) {
		while (n-- > 0)
			pool.add(new PooledThread(this, nThread++));
	}

	public void execute(Runnable runnable) {
		synchronized (pool) {
			if (pool.size() == 0)
				createThreads(spare);
			((PooledThread) pool.remove(0)).set(runnable);
		}
	}

}