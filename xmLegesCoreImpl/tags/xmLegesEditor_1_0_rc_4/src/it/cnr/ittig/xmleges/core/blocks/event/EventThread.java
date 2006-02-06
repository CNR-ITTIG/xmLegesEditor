package it.cnr.ittig.xmleges.core.blocks.event;

import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;

import java.util.EventObject;

/**
 * Thread per l'esecuzione del metodo <code>manageEvent</code> di un
 * EventManagerListener.
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
 * @see it.cnr.ittig.xmleges.core.services.event.EventManager
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */

public class EventThread implements Runnable {
	EventManagerImpl eventManagerImpl;

	EventObject event;

	EventManagerListener listener;

	public EventThread(EventManagerImpl eventManagerImpl) {
		this.eventManagerImpl = eventManagerImpl;
	}

	public void run() {
		boolean end = false;
		while (!end)
			try {
				listener.manageEvent(event);
				end = true;
			} catch (Throwable tr) {
				if (!Thread.interrupted()) {
					end = true;
					eventManagerImpl.logger.error(tr.toString(), tr);
				}
			}
		eventManagerImpl.threadFinish(this);
	}
}
