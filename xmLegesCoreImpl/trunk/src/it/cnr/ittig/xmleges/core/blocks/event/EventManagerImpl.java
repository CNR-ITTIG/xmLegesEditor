package it.cnr.ittig.xmleges.core.blocks.event;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.threads.ThreadManager;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Vector;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.eventmanager.EventManager</code>.</h1>
 * <h1>Descrizione</h1>
 * Questa implementanzione prevede l'esistenza del ThreadManager per la gestione degli
 * eventi tramite un pool di thread.
 * <h1>Configurazione</h1>
 * La configurazione pu&ograve; avere il seguente tag:
 * <ul>
 * <li><code>&lt;multithread&gt;</code>: true/false per la gestione del multithread;</li>
 * </ul>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.core.services.threads.threadManager:1.0</li>
 * </ul>
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>, <a
 *         href="mailto:t.paba@onetech.it">Tommaso Paba </a>
 */
public class EventManagerImpl implements EventManager, Runnable, Loggable, Serviceable, Initializable, Configurable {

	Logger logger;

	ThreadManager threadManager = null;

	boolean multithread = false;

	Hashtable listeners = new Hashtable(1000);

	Class allEvent = this.getClass();

	EventDispatcherImpl eventDispatcher;

	final static int MAX_THREADS = 100;

	Vector threads = new Vector(MAX_THREADS, MAX_THREADS);

	Vector queue = new Vector();

	boolean terminate = false;

	int listenerCount;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		threadManager = (ThreadManager) serviceManager.lookup(ThreadManager.class);
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		try {
			this.multithread = configuration.getChild("multithread").getValueAsBoolean(false);
		} catch (Exception ex) {
			this.multithread = false;
		}
		logger.info("Multithread is " + this.multithread);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws Exception {
		eventDispatcher = new EventDispatcherImpl(threadManager, logger);
		if (multithread)
			threadManager.execute(this);
	}

	// ////////////////////////////////////////////////// EventManager Interface
	public void addListener(EventManagerListener listener) {
		addListener(listener, allEvent);
	}

	public synchronized void addListener(EventManagerListener listener, Class eventClass) {
		Vector v = (Vector) listeners.get(eventClass);
		if (v == null)
			v = new Vector();
		if (!v.contains(listener)) {
			v.addElement(listener);
			listeners.put(eventClass, v);
			if (logger.isDebugEnabled()) {
				logger.debug("Listener added: " + eventClass + "->" + listener);
				logger.debug("Listners for " + eventClass);
				for (int i = 0; i < v.size(); i++)
					logger.debug("listener[" + i + "]=" + v.get(i));
			}
		} else if (logger.isDebugEnabled())
			logger.debug("Listener exists for event " + eventClass + ":" + listener);
	}

	public synchronized void removeListener(EventManagerListener listener) {
		Enumeration en = listeners.keys();
		while (en.hasMoreElements())
			removeListener(listener, (Class) en.nextElement());
	}

	public synchronized void removeListener(EventManagerListener listener, Class eventClass) {
		Vector v = (Vector) listeners.get(eventClass);
		v.remove(listener);
		if (logger.isDebugEnabled())
			logger.debug("Listener removed:" + eventClass + "->" + listener);
	}

	public synchronized void fireEvent(EventObject eventObject) {
		fireEvent(eventObject, false);
	}

	public synchronized void fireEventSerially(EventObject eventObject) {
		fireEvent(eventObject, true);
	}

	protected void fireEvent(EventObject eventObject, boolean serial) {
		if (serial || !multithread) {
			for (Enumeration en = getListeners(eventObject).elements(); en.hasMoreElements();) {
				try {
					EventManagerListener listener = (EventManagerListener) en.nextElement();
					if (!listener.equals(eventObject.getSource())) {
						listener.manageEvent(eventObject);
					}
				} catch (Exception ex) {
					logger.error(ex.toString(), ex);
				}
			}

		} else {
			queue.addElement(eventObject);
			synchronized (queue) {
				notify();
			}
		}
	}

	protected void fireEvent(EventObject eventObject, Vector v, boolean serial) {
		if (v == null)
			return;
		if (serial || !multithread) {
			for (Enumeration en = v.elements(); en.hasMoreElements();) {
				try {
					EventManagerListener listener = (EventManagerListener) en.nextElement();
					if (!listener.equals(eventObject.getSource())) {
						listener.manageEvent(eventObject);
					}
				} catch (Exception ex) {
					logger.error(ex.toString(), ex);
				}
			}
		} else {
			// Aggiungi gli eventi al dispatcher
			for (Enumeration en = v.elements(); en.hasMoreElements();) {
				try {
					EventManagerListener listener = (EventManagerListener) en.nextElement();
					if (!listener.equals(eventObject.getSource())) {
						eventDispatcher.add(listener, eventObject);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.error(ex.toString(), ex);
				}
			}
			eventDispatcher.dispatchEvents();
		}
	}

	protected Vector getListeners(EventObject eventObject) {
		// TODO ottimizzare con array?
		Vector allListeners = new Vector();
		synchronized (listeners) {
			Vector v1 = (Vector) listeners.get(eventObject.getClass());
			Vector v2 = (Vector) listeners.get(allEvent);
			if (v1 != null)
				allListeners.addAll(v1);
			if (v2 != null)
				allListeners.addAll(v2);
		}
		return allListeners;
	}

	protected EventThread getThread() {
		if (threads.size() == 0) {
			for (int i = 0; i < MAX_THREADS; i++)
				threads.addElement(new EventThread(this));
		}
		return (EventThread) threads.remove(0);
	}

	public void threadFinish(EventThread thread) {
		listenerCount--;
		if (listenerCount == 0)
			synchronized (this) {
				notify();
			}
		threads.addElement(thread);
	}

	public void run() {
		while (!terminate) {
			try {

				// 
				while (queue.size() == 0)
					try {
						synchronized (queue) {
							queue.wait(50);
						}
					} catch (InterruptedException ex) {
						Thread.interrupted();
					}

				EventObject event = (EventObject) queue.remove(0);
				Vector v = getListeners(event);
				listenerCount = v.size();
				for (Enumeration en = v.elements(); en.hasMoreElements();) {
					EventManagerListener listener = (EventManagerListener) en.nextElement();
					if (!listener.equals(event.getSource())) {
						EventThread thread = getThread();
						thread.event = event;
						thread.listener = (EventManagerListener) listener;
						threadManager.execute(thread);
					} else
						listenerCount--;
				}

				// attende che tutti i thread abbiano finito l'esecuzione
				while (listenerCount > 0)
					try {
						synchronized (this) {
							// TODO timeout?
							wait(50);
						}
					} catch (InterruptedException ex) {
						Thread.interrupted();
					}
			} catch (Exception ex) {
				logger.error(ex.toString(), ex);
			}
		}
	}
}
