package it.cnr.ittig.xmleges.core.blocks.event;

import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.threads.ThreadManager;

import java.util.EventObject;
import java.util.Vector;

/**
 * <p>
 * Invia un evento ai listener registrati.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>License: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @author <a href="mailto:t.paba@onetech.it">Tommaso Paba </a>
 */
public class EventDispatcherImpl implements Runnable {

	/**
	 * Se blocking è impostato a true, prima di uscire da dispatchEvents il
	 * dispatcher aspetterà che tutti i thread lanciati abbiano terminato
	 * l'esecuzione di manageEvent. Questo assicura che ogni blocco di eventi
	 * sia completato prima che il controllo venga restituito per il blocco
	 * successivo. Purtroppo può provocare deadlock...
	 */
	private boolean blocking = false;

	private int counter;

	private Vector events = new Vector(50);

	private ThreadManager threadManager;

	private Logger logger;

	private class EventData {
		public EventManagerListener listener = null;

		public EventObject event = null;

		public long timestamp = 0;

		EventData(EventManagerListener listener, EventObject event) {
			this.listener = listener;
			this.event = event;
			this.timestamp = System.currentTimeMillis();
		}
	}

	EventDispatcherImpl(ThreadManager threadManager, Logger logger) {
		this.threadManager = threadManager;
		this.logger = logger;
	}

	public void add(EventManagerListener listener, EventObject event) {
		synchronized (events) {
			events.add(new EventData(listener, event));
		}
	}

	public void dispatchEvents() {
		synchronized (this) {
			int nEvents = events.size();
			for (int i = 0; i < nEvents; i++) {
				if (blocking) {
					counter++;
				}
				threadManager.execute(this);
			}
			if (blocking && nEvents > 0) {
				try {
					this.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void dispatchSerially() {
		synchronized (this) {
			boolean done = false;
			while (!done) {
				EventData ed = null;
				synchronized (events) {
					if (events.size() > 0)
						ed = (EventData) events.remove(0);
					else
						done = true;
				}
				if (ed != null) {
					if (logger.isDebugEnabled())
						logger.debug("Event (Serial) - TS: " + ed.timestamp + ", Evt: " + ed.event.toString() + ", To: " + ed.listener.toString());
					ed.listener.manageEvent(ed.event);
				}
			}
		}
	}

	public void run() {
		EventData ed = (EventData) events.remove(0);
		if (logger.isDebugEnabled())
			logger.debug("Event - TS: " + ed.timestamp + ", Evt: " + ed.event.toString() + ", To: " + ed.listener.toString());
		ed.listener.manageEvent(ed.event);
		if (blocking) {
			synchronized (this) {
				counter--;
				if (counter == 0) {
					notify();
				}
			}
		}
	}
}
