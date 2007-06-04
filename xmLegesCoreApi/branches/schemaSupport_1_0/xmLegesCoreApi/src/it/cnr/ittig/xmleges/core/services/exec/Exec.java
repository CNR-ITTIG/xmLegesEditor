package it.cnr.ittig.xmleges.core.services.exec;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per l'esecuzione di un comando esterno alla Java Virtual Machine con
 * i flussi di input, output ed error gestiti opportunamente.
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
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 */
public interface Exec extends Service {

	/**
	 * Imposta il timeout per l'esecuzione del comando.
	 * 
	 * @param timeOut timeout di esecuzione in millisecondi
	 */
	public void setTimeOut(int timeOut);

	/**
	 * Esegue il comando <code>command</code>.
	 * 
	 * @param command comando da eseguire
	 * @return valore di uscita del comando
	 * @throws ExecException se ci sono problemi con l'esecuzione del comando
	 * @throws ExecTimeoutException se il comando non termina entro il timeout
	 *         impostato
	 */
	public int runCommand(String command) throws ExecException, ExecTimeoutException;

	/**
	 * Esegue il comando <code>command</code> scrivendo sul suo flusso di
	 * input il testo <code>stdinText</code>.
	 * 
	 * @param command comando da eseguire
	 * @param stdinText testo da passare sullo standard input del comando
	 * @return valore di uscita del comando
	 * @throws ExecException se ci sono problemi con l'esecuzione del comando
	 * @throws ExecTimeoutException se il comando non termina entro il timeout
	 *         impostato
	 */
	public int runCommand(String command, String stdinText) throws ExecException, ExecTimeoutException;

	/**
	 * Forza la fine di esecuzione dell'applicazione.
	 */
	public void terminate();

	/**
	 * Restituisce l'intero flusso di output del comando.
	 * 
	 * @return flusso di output
	 */
	public String getStdout();

	/**
	 * Restituisce l'intero flusso di error del comando.
	 * 
	 * @return flusso di error
	 */
	public String getStderr();

}
