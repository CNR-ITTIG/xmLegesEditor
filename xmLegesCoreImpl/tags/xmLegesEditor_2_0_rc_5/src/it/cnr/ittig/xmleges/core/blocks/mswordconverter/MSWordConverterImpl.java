package it.cnr.ittig.xmleges.core.blocks.mswordconverter;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.mswordconverter.MSWordConverter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.File;
import java.io.FileInputStream;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.mswordconverter.MSWordConverter</code>. </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * Nessuna
 * <h1>I18n</h1>
 * Nessuna
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
public class MSWordConverterImpl implements MSWordConverter, Loggable {
	Logger logger;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////// MSWordConverter Interface
	public File convert(File src) {
		return convert(src, 0, null);
	}

	public File convert(File src, int column) {
		return convert(src, column, null);
	}

	public File convert(File src, int column, String map) {
		try {
			copyProgram();
			String outFileName = UtilFile.getTempDirName() + File.separatorChar + "antiword.out";
			UtilFile.copyFileInTemp(new FileInputStream(src), "antiword.in");
			String command = UtilFile.getTempDirName() + File.separatorChar + "antiword.bat";
			command += " " + column;
			command += " " + UtilFile.getTempDirName() + File.separatorChar + "antiword.in";
			command += " " + UtilFile.getTempDirName() + File.separatorChar + "antiword.out";
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			return new File(outFileName);
		} catch (Exception ex) {
			logger.error("Exception in ms-word conversion" + ex.getMessage(), ex);
			return null;
		}

	}

	protected void copyProgram() {
		String osName = System.getProperty("os.name");
		String osArch = System.getProperty("os.arch");

		String file = null;
		if (osName.equalsIgnoreCase("linux") && osArch.equalsIgnoreCase("i386")) {
			file = "linux-i386/antiword.bat";
		} else if (osName.toLowerCase().matches("windows.*")) {
			file = "win-32/antiword.bat";
		}

		if (file != null) {
			UtilFile.copyFileInTemp(getClass().getResourceAsStream(file), file);
			UtilFile.setExecPermission(UtilFile.getFileFromTemp(new File(file).getName()));
		}

	}

}
