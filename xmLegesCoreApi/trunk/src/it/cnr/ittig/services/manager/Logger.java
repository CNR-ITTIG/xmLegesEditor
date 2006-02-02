package it.cnr.ittig.services.manager;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Element;

/**
 * Gestione dei log dell'applicazione tramite log4j.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */

public class Logger {
	org.apache.log4j.Logger logger;

	static Hashtable loggers = new Hashtable();

	public static void init(Element element) {
		DOMConfigurator.configure(element);
	}

	public static String[] getLoggerNames() {
		String[] ret = new String[loggers.size()];
		Enumeration en = loggers.keys();
		for (int i = 0; en.hasMoreElements(); i++)
			ret[i] = en.nextElement().toString();
		return ret;
	}

	public static Logger getLogger(String name) {
		return (Logger) loggers.get(name);
	}

	public static Logger[] getLoggers() {
		Logger[] ret = new Logger[loggers.size()];
		Enumeration en = loggers.keys();
		for (int i = 0; en.hasMoreElements(); i++)
			ret[i] = (Logger) loggers.get(en.nextElement());
		return ret;
	}

	public Logger(String name) {
		logger = org.apache.log4j.Logger.getLogger(name);
		loggers.put(name, this);
	}

	public final void debug(String msg) {
		logger.debug(msg);
	}

	public final void debug(String msg, Throwable tr) {
		logger.debug(msg, tr);
	}

	public final void info(String msg) {
		logger.info(msg);
	}

	public final void info(String msg, Throwable tr) {
		logger.info(msg, tr);
	}

	public final void warn(String msg) {
		logger.warn(msg);
	}

	public final void warn(String msg, Throwable tr) {
		logger.warn(msg, tr);
	}

	public final void error(String msg) {
		logger.error(msg);
	}

	public final void error(String msg, Throwable tr) {
		logger.error(msg, tr);
	}

	public final void fatalError(String msg) {
		logger.fatal(msg);
		System.exit(1);
	}

	public final void fatalError(String msg, Throwable tr) {
		logger.fatal(msg, tr);
		System.exit(1);
	}

	public final void setDebugEnabled() {
		logger.setLevel(Level.DEBUG);
	}

	public final boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public final void setInfoEnabled() {
		logger.setLevel(Level.INFO);
	}

	public final boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public final void setWarningEnabled() {
		logger.setLevel(Level.WARN);
	}

	public final boolean isWarningEnabled() {
		return logger.isEnabledFor(Priority.WARN);
	}

	public final void setErrorEnabled() {
		logger.setLevel(Level.ERROR);
	}

	public final boolean isErrorEnabled() {
		return logger.isEnabledFor(Priority.ERROR);
	}

	public final void setFatalErrorEnabled() {
		logger.setLevel(Level.FATAL);
	}

	public final boolean isFatalErrorEnabled() {
		return logger.isEnabledFor(Priority.FATAL);
	}
}
