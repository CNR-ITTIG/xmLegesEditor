package it.cnr.ittig.xmleges.core.blocks.exec;

import it.cnr.ittig.services.manager.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * StreamReader usando un thread separato.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public class ThreadedStreamReader extends Thread {
	InputStream input = null;

	OutputStream output = null;

	String text = null;

	boolean terminate = false;

	boolean error = false;

	ExecImpl execImpl;

	Logger logger;

	/**
	 * Costruttore di <code>ThreadedStreamReader</code>.
	 * 
	 * @param execImpl implementazione di exec per notificare la lettura.
	 */
	public ThreadedStreamReader(ExecImpl execImpl) {
		this.execImpl = execImpl;
		this.logger = execImpl.getLogger();
	}

	/**
	 * Imposta il flusso di lettura per il thread.
	 * 
	 * @param input input stream per il thread
	 * @param error <code>true</code> per lettura standard error
	 */
	public void setInputStream(InputStream input, boolean error) {
		this.input = input;
		this.error = error;
	}

	/**
	 * Imposta l'output stream per il thread.
	 * 
	 * @param output output stream per il thread
	 */
	public void setOutputStream(OutputStream output) {
		this.output = output;
	}

	/**
	 * Imposta il testo da inviare al processo attraverso l'output stream.
	 * 
	 * @param text testo da inviare al processo
	 */
	public void setText(String text) {
		this.text = text;
	}

	// /////////////////////////////////////////////////// Extends Thread
	public void run() {
		terminate = false;
		try {
			if (input != null) {
				if (logger.isDebugEnabled())
					logger.debug("Reading from InputStream ...");
				ByteArrayOutputStream res = new ByteArrayOutputStream(2048);
				BufferedInputStream bis = new BufferedInputStream(input);
				for (int c = bis.read(); c != -1 && !isTerminate(); c = bis.read()) {
					res.write(c);
					if (error && c == '\n') {
						execImpl.stderrArrived(res.toString());
						res.reset();
					}
				}
				if (!error)
					execImpl.stdinArrived(res.toString());
				if (logger.isDebugEnabled())
					logger.debug("Reading from InputStream ok.");
				terminate();

			} else {
				if (text != null) {
					if (logger.isDebugEnabled())
						logger.debug("Writing to OutputStream...");
					BufferedOutputStream bos = new BufferedOutputStream(output, 2048);
					bos.write(text.getBytes());
					bos.flush();
					bos.close();
					if (logger.isDebugEnabled())
						logger.debug("Writing to OutputStream ok.");
				}
				terminate();
			}
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
	}

	/**
	 * Termina l'esecuzione del thread.
	 */
	public void terminate() {
		this.terminate = true;
	}

	/**
	 * Indica se il thread &egrave; stato terminato.
	 * 
	 * @return <code>true</code> se &egrave; stato terminato.
	 */
	public boolean isTerminate() {
		return terminate;
	}
}
