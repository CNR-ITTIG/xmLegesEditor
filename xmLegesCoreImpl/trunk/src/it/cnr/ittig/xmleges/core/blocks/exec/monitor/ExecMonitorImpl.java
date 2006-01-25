package it.cnr.ittig.xmleges.core.blocks.exec.monitor;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.event.EventManagerListener;
import it.cnr.ittig.xmleges.core.services.exec.Exec;
import it.cnr.ittig.xmleges.core.services.exec.ExecEvent;
import it.cnr.ittig.xmleges.core.services.exec.ExecFinishedEvent;
import it.cnr.ittig.xmleges.core.services.exec.ExecStartedEvent;
import it.cnr.ittig.xmleges.core.services.exec.monitor.ExecMonitor;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JProgressBar;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.exec.monitor.ExecMonitor</code>.</h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>exec.monitor.form.close: pulsante close</li>
 * <li>exec.monitor.debug.icon: icona per debug</li>
 * <li>exec.monitor.info.icon: icona per info</li>
 * <li>exec.monitor.warn.icon: icona per warn</li>
 * <li>exec.monitor.error.icon: icona per error</li>
 * <li>exec.monitor.generic.icon: icona per generico</li>
 * </ul>
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
 */
public class ExecMonitorImpl implements ExecMonitor, EventManagerListener, Loggable, Serviceable, Initializable {
	Logger logger;

	EventManager eventManager;

	Form form;

	Exec exec;

	I18n i18n;

	DefaultListModel listModel = new DefaultListModel();

	JProgressBar progress;

	Component owner;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
		form = (Form) serviceManager.lookup(Form.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		eventManager.addListener(this, ExecEvent.class);
		eventManager.addListener(this, ExecStartedEvent.class);
		eventManager.addListener(this, ExecFinishedEvent.class);
		form.setName("exec.monitor.form");
		form.setMainComponent(getClass().getResourceAsStream("ExecMonitor.jfrm"));
		form.setCustomButtons(new String[] { "exec.monitor.form.close" });
		JList list = (JList) form.getComponentByName("exec.monitor.form.list");
		list.setModel(listModel);
		list.setCellRenderer(new ExecEventRenderer(i18n));
		progress = (JProgressBar) form.getComponentByName("exec.monitor.form.progress");
	}

	// //////////////////////////////////////// EventManagerListener Interface
	public void manageEvent(EventObject event) {
		if (exec == null)
			return;
		if (!exec.equals(event.getSource()))
			return;
		if (event instanceof ExecEvent) {
			ExecEvent e = (ExecEvent) event;
			if (e.isPerc()) {
				logger.debug("perc:" + e.getPerc());
				setProgress(e.getPerc());
			} else {
				logger.debug("generic:" + e.getGenericMsg());
				listModel.addElement(e);
			}
		} else if (event instanceof ExecStartedEvent) {
			form.showDialog();
			exec.terminate();
			setProgress(0);
		} else if (event instanceof ExecFinishedEvent)
			progress.setIndeterminate(false);
	}

	protected void setProgress(int value) {
		progress.setValue(value);
		progress.setString("" + value + "%");
	}

	// /////////////////////////////////////////////////// ExecMonitor Interface
	public void setExec(Exec exec) {
		this.exec = exec;
		clear();
	}

	public Component getAsComponent() {
		return form.getAsComponent();
	}

	public void clear() {
		listModel.clear();
		progress.setValue(0);
	}

	public void close() {
		form.close();
	}

}
