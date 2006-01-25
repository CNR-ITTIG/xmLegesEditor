package it.cnr.ittig.xmleges.core.blocks.exec;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.event.EventManager;
import it.cnr.ittig.xmleges.core.services.exec.Exec;
import it.cnr.ittig.xmleges.core.services.exec.ExecEvent;
import it.cnr.ittig.xmleges.core.services.exec.ExecException;
import it.cnr.ittig.xmleges.core.services.exec.ExecFinishedEvent;
import it.cnr.ittig.xmleges.core.services.exec.ExecStartedEvent;
import it.cnr.ittig.xmleges.core.services.exec.ExecTimeoutException;

import java.io.ByteArrayOutputStream;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.exec.Exec</code>.</h1>
 * <h1>Descrizione</h1>
 * Questa implementazione gestisce tutti i flussi (input, output ed error) tramite thread.
 * L'esecuzione termina quando il flusso di output viene chiuso dal comando oppure quando
 * scade il timeout.
 * <h1>Configurazione</h1>
 * Pu&ograve; essere impostato il timeout di default (in secondi) con il tag
 * <code>&lt;timeout&gt;</code>.<br>
 * Esempio:
 * 
 * <pre>
 *    &lt;timeout&gt;30&lt;/timeout&gt;
 * </pre>
 * 
 * <h1>Dipendenze</h1>
 * Nessuna.
 * <h1>I18n</h1>
 * Nessuna.
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
 * @see it.cnr.ittig.xmleges.core.services.exec.Exec
 * @see it.cnr.ittig.xmleges.core.blocks.exec.ThreadedStreamReader
 * @see it.cnr.ittig.xmleges.core.blocks.exec.ThreadedStreamReaderListener
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ExecImpl implements Exec, Loggable, Serviceable, Configurable {
	Logger logger;

	EventManager eventManager;

	ThreadedStreamReader stdin;

	ThreadedStreamReader stdout;

	ThreadedStreamReader stderr;

	String stdoutText;

	ByteArrayOutputStream stderrText;

	int timeOut = 30;

	boolean terminated = false;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		eventManager = (EventManager) serviceManager.lookup(EventManager.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		try {
			setTimeOut(configuration.getChild("timeout").getValueAsInteger(30));
		} catch (Exception ex) {
			setTimeOut(30);
		}
	}

	// ////////////////////////////////////////////////////////// Exec Interface
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public int runCommand(String command) throws ExecException, ExecTimeoutException {
		return runCommand(command, null);
	}

	public int runCommand(String command, String stdinText) throws ExecException, ExecTimeoutException {
		try {
			stdoutText = "";
			stderrText = new ByteArrayOutputStream();

			stdin = new ThreadedStreamReader(this);
			stdout = new ThreadedStreamReader(this);
			stderr = new ThreadedStreamReader(this);

			eventManager.fireEvent(new ExecStartedEvent(this));
			Runtime runtime = Runtime.getRuntime();
			if (logger.isDebugEnabled())
				logger.debug("command=" + command);
			Process process = runtime.exec(command);

			// STDOUT DEL SOTTOPROCESSO -> INPUT NOSTRO
			stdout.setInputStream(process.getInputStream(), false);
			stdout.start();

			// STDERR DEL SOTTOPROCESSO -> INPUT NOSTRO
			stderr.setInputStream(process.getErrorStream(), true);
			stderr.start();

			// INPUT DEL SOTTOPROCESSO -> OUTPUT NOSTRO
			if (stdinText != null) {
				stdin.setOutputStream(process.getOutputStream());
				stdin.setText(stdinText + "\n"); // ???
				stdin.start();
			} else
				stdin.terminate();

			int exitValue = process.waitFor();
			stdin.terminate();
			stdout.terminate();
			stderr.terminate();

			System.gc();

			// fare gestione timeout con thread separato
			eventManager.fireEvent(new ExecFinishedEvent(this, exitValue, false, true));
			return exitValue;

			/*
			 * terminated = false; int time = this.timeOut; while (!terminated && time > 0 &&
			 * !(stdin.isTerminate() && stdout.isTerminate() & stderr.isTerminate())) {
			 * Thread.sleep(1000); time--; logger.debug("TimeOut..." + time); }
			 * 
			 * if (time == 0 || terminated) process.destroy();
			 * 
			 * int exitValue = process.exitValue();
			 * 
			 * stdin.terminate(); stdout.terminate(); stderr.terminate();
			 * 
			 * System.gc();
			 * 
			 * if (time == 0) throw new ExecTimeoutException("Timeout expired: " +
			 * timeOut, command, timeOut); else return exitValue;
			 */
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
			throw new ExecException("Error running command:'" + command + '\'', command);
		}
	}

	public void terminate() {
		this.terminated = true;
	}

	public String getStdout() {
		return stdoutText;
	}

	public String getStderr() {
		return stderrText.toString();
	}

	public void stdinArrived(String str) {
		this.stdoutText = str;
		if (logger.isDebugEnabled())
			logger.debug("stdoutText=" + this.stdoutText);
	}

	public void stderrArrived(String str) {
		try {
			this.stderrText.write(str.getBytes());
		} catch (Exception ex) {
		}
		if (logger.isDebugEnabled())
			logger.debug("stderrArrived:" + str);
		eventManager.fireEvent(new ExecEvent(this, str));
	}

	// //////////////////////////////////////////// Methods for package class
	Logger getLogger() {
		return this.logger;
	}
}
