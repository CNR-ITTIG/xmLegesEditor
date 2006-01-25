package it.cnr.ittig.services.manager;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Gestore dei servizi dell'applicazione.
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

public class ServiceManager {
	Logger logger = new Logger("cli");

	/** Registro dei servizi. */
	Hashtable services = new Hashtable(100);

	/** Registro delle istanze */
	Hashtable instances = new Hashtable(100);

	/** Registro dei servizi Startable */
	Vector startables = new Vector(100);

	/** Registro dei servizi Disposable */
	Vector disposables = new Vector(100);

	public ServiceManager() {
	}

	void setShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.debug("Stopping Startables...");
				for (Enumeration en = startables.elements(); en.hasMoreElements();)
					try {
						((Startable) en.nextElement()).stop();
					} catch (Exception ex) {
						logger.error("Exception on startable:" + ex, ex);
					}
				logger.debug("Stopping Startables OK");
				logger.debug("Dispose Disposables...");
				for (Enumeration en = disposables.elements(); en.hasMoreElements();)
					try {
						((Disposable) en.nextElement()).dispose();
					} catch (Exception ex) {
						logger.error("Exception on disposable:" + ex, ex);
					}
				logger.debug("Dispose Disposables OK");
			}
		});
	}

	public void addService(ServiceContainer service) throws ServiceException {
		try {
			if (services.containsKey(service.getService()))
				throw new ServiceException("Component already defined:" + service.getService());
			services.put(service.getService(), service);
		} catch (NullPointerException ex) {
			throw new ServiceException("Service without correct interface: name=" + service.getService() + ", class=" + service.getImpl());
		}
	}

	public Service lookup(Class cl) throws ServiceException {
		ServiceContainer service = (ServiceContainer) services.get(cl);
		if (instances.containsKey(cl) && service.isLifeStyleSingleton())
			return (Service) instances.get(cl);
		else
			try {
				Hashtable hash = new Hashtable();
				Class impl = Class.forName(service.getImpl());
				Object o = impl.newInstance();
				Class[] inter = impl.getInterfaces();
				for (int i = 0; i < inter.length; i++)
					hash.put(inter[i], inter[i]);
				instances.put(cl, o);
				service.setInstance(o);
				callInterfaces(service, o, hash);
				return (Service) o;
			} catch (Throwable tr) {
				throw new ServiceException("Implementation not found:" + cl, tr);
			}
	}

	protected void callInterfaces(ServiceContainer service, Object o, Hashtable hash) {
		try {
			logger.debug("START incarnation for: '" + service.getService() + '\'');
			if (hash.containsKey(Loggable.class)) {
				logger.debug("Loggable...");
				String name = service.getService().getName();
				((Loggable) o).enableLogging(new Logger(name.substring(name.lastIndexOf('.'))));
				logger.debug("Loggable OK");
			}

			if (hash.containsKey(Serviceable.class)) {
				logger.debug("Serviceable...");
				((Serviceable) o).service(this);
				logger.debug("Serviceable OK");
			}

			if (hash.containsKey(Configurable.class)) {
				logger.debug("Configurable...");
				((Configurable) o).configure(service.getConfiguration());
				logger.debug("Configurable OK");
			}

			if (hash.containsKey(Initializable.class)) {
				logger.debug("Initializable...");
				((Initializable) o).initialize();
				logger.debug("Initializable OK");
			}

			if (hash.containsKey(Startable.class)) {
				logger.debug("Startable...");
				((Startable) o).start();
				startables.addElement(o);
				logger.debug("Startable OK");
			}

			if (hash.containsKey(Disposable.class)) {
				logger.debug("Registering Disposable...");
				disposables.addElement(o);
				logger.debug("Registering Disposable OK");
			}

			logger.debug("END incarnation for: '" + service.getService() + '\'');
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			System.exit(1);
		}
	}

}
