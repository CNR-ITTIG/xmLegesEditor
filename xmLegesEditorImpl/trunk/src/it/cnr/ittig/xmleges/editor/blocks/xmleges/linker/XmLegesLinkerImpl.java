package it.cnr.ittig.xmleges.editor.blocks.xmleges.linker;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.exec.Exec;
import it.cnr.ittig.xmleges.core.services.exec.ExecException;
import it.cnr.ittig.xmleges.core.services.exec.ExecTimeoutException;
import it.cnr.ittig.xmleges.core.services.exec.monitor.ExecMonitor;
import it.cnr.ittig.xmleges.core.util.dom.UtilDom;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.xmleges.linker.XmLegesLinkerException;
import it.cnr.ittig.xmleges.editor.services.xmleges.linker.XmLegesLinker;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.w3c.dom.Node;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.xmleges.linker.XmLegesLinker</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.exec.Exec:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * Nessuno
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
public class XmLegesLinkerImpl implements XmLegesLinker, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	Exec exec;

	ExecMonitor execMonitor;

	String regione = null;

	String ministero = null;
	
	boolean rifIncompleti = false;
	
	boolean rifInterni = false;

	int timeout;

	String error = null;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		exec = (Exec) serviceManager.lookup(Exec.class);
		execMonitor = (ExecMonitor) serviceManager.lookup(ExecMonitor.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) {
		try {
			timeout = configuration.getChild("timeout").getValueAsInteger(30);
		} catch (Exception ex) {
			timeout = 30;
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		// execMonitor.setExec(exec);
	}

	// //////////////////////////////////////////// RiferimentiParser Interface
	public void setRegione(String regione) {
		this.regione = regione;
	}

	public void setMinistero(String ministero) {
		this.ministero = ministero;
	}
	
	public void setEnabledRifInterni(boolean rifInterni){
		this.rifInterni = rifInterni;
	}
	
	public void setEnabledRifIncompleti(boolean rifIncompleti){
		this.rifIncompleti = rifIncompleti;
	}

	protected boolean firstTime = true;

	public InputStream parse(String text) throws XmLegesLinkerException {
		// FIXME copiare in sibdir parser/riferimenti
		// if (firstTime) {
		copyProgram();
		// firstTime = false;
		// }
		File command = UtilFile.getFileFromTemp("xmLeges-Linker.exe");
		UtilFile.setExecPermission(command);
		StringBuffer sb = new StringBuffer(command.getAbsolutePath());
		if (regione != null)
			sb.append(" -R " + this.regione);
		if (ministero != null)
			sb.append(" -M " + this.ministero);
		sb.append(" -m dtdnir -i xml ");
		
		if(rifIncompleti && rifInterni)
			sb.append(" -r ni");
		else if(rifIncompleti)
			sb.append(" -r n");
		else if(rifInterni)
			sb.append(" -r i");
		
		sb.append(" -f " + UtilFile.getTempDirName() + "/pr.in");
		sb.append(" -F " + UtilFile.getTempDirName() + "/pr.out");
		sb.append(" -L file=" + UtilFile.getTempDirName() + "/pr.err");
		try {
			UtilFile.copyFileInTemp(new ByteArrayInputStream(text.getBytes()), "pr.in");

			String toExec = sb.toString();
			logger.debug("command=" + toExec);
			exec.runCommand(toExec);
			error = exec.getStderr().trim();
			if (error.length() == 0)
				error = null;
			// return new ByteArrayInputStream(exec.getStdout().getBytes());
			return new FileInputStream(UtilFile.getTempDirName() + File.separatorChar + "pr.out");

		} catch (ExecTimeoutException ex) {
			logger.warn("Timeout del parser");
		} catch (ExecException ex) {
			logger.error("error", ex);
		} catch (Exception ex) {
			logger.error("error", ex);
		}
		// execMonitor.close();
		return null;
	}

	public InputStream parse(Node node) throws XmLegesLinkerException {
		return parse(UtilDom.domToString(node, true));
	}

	public String getError() {
		return error;
	}

	protected void copyProgram() {
		String osName = System.getProperty("os.name");
		String osArch = System.getProperty("os.arch");

		String[] files = null;
		if (osName.equalsIgnoreCase("linux") && osArch.equalsIgnoreCase("i386"))
			files = new String[] { "linux-i386/xmLeges-Linker.exe" };
		if (osName.toLowerCase().matches("windows.*"))
			files = new String[] { "win-32/xmLeges-Linker.exe", "win-32/cygwin1.dll" };
		if (files != null)
			for (int i = 0; i < files.length; i++)
				UtilFile.copyFileInTemp(getClass().getResourceAsStream(files[i]), files[i]);

	}
}
