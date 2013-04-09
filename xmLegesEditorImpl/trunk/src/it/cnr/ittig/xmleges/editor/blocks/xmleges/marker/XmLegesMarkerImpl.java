package it.cnr.ittig.xmleges.editor.blocks.xmleges.marker;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
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
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.xmleges.marker.XmLegesMarker;
import it.cnr.ittig.xmleges.editor.services.xmleges.marker.XmLegesMarkerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.parser.struttura.StrutturaParser</code>.
 * </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>xxx.yyy:</li>
 * </ul>
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
public class XmLegesMarkerImpl implements XmLegesMarker, Loggable, Serviceable, Configurable, Initializable {
	Logger logger;

	Exec exec;

	ExecMonitor execMonitor;

	int timeout;

	String format;

	String dtd;

	String tipoDoc;

	String tipoDocAltro;

	String tipoComma;

	String tipoRubrica;

	boolean testoConRubriche;

	String encoding;

	boolean controlloSequenza;

	boolean modAnnessi;

	boolean modTabelle;

	String logLevel;

	String error = null;
	
	boolean postFix = false; //Attiva il Fixing dell'XML a posteriori

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
	public void configure(Configuration configuration) throws ConfigurationException {
		try {
			timeout = configuration.getChild("timeout").getValueAsInteger(30);
		} catch (Exception ex) {
			timeout = 30;
		}
	}

	// ///////////////////////////////////////////////// Initializable Interface
	public void initialize() throws java.lang.Exception {
		setDefault();
		// FIXME con gli eventi seriali non funziona
		// execMonitor.setExec(exec);
		
		copyProgram();
	}

	// /////////////////////////////////////////////// StrutturaParser Interface
	public void setDefault() {
		try {
			setTipoInput(TIPO_INPUT_VALORE[0]);
			setTipoDtd(TIPO_DTD_VALORE[0]);
			setTipoDoc(TIPO_DOC_VALORE[0]);
			setTipoDocAltro("");
			setTipoComma(TIPO_COMMA_VALORE[0]);
			setTipoRubrica(TIPO_RUBRICA_VALORE[0]);
			setTestoConRubriche(true);
			setEncoding(null);
			setControlloSequenza(true);
			setModuloAnnessi(true);
			setModuloTabelle(true);
			setLoggerLevel(LOGGER_VALORE[0]);
		} catch (XmLegesMarkerException ex) {
			logger.error(ex.toString(), ex);
		}
	}

	public void setTipoInput(String format) throws XmLegesMarkerException {
		checkParam(TIPO_INPUT_VALORE, format);
		this.format = format;
	}

	public void setTipoDtd(String dtd) throws XmLegesMarkerException {
		checkParam(TIPO_DTD_VALORE, dtd);
		this.dtd = dtd;
	}

	public void setTipoDoc(String tipoDoc) throws XmLegesMarkerException {
		checkParam(TIPO_DOC_VALORE, tipoDoc);
		this.tipoDoc = tipoDoc;
	}

	public void setTipoDocAltro(String tipoDocAltro) {
		this.tipoDocAltro = tipoDocAltro;
	}

	public void setTipoComma(String tipoComma) throws XmLegesMarkerException {
		checkParam(TIPO_COMMA_VALORE, tipoComma);
		this.tipoComma = tipoComma;
	}

	public void setTipoRubrica(String tipoRubrica) throws XmLegesMarkerException {
		checkParam(TIPO_RUBRICA_VALORE, tipoRubrica);
		this.tipoRubrica = tipoRubrica;
	}

	public void setTestoConRubriche(boolean testoConRubriche) {
		this.testoConRubriche = testoConRubriche;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setControlloSequenza(boolean controlloSequenza) {
		this.controlloSequenza = controlloSequenza;
	}

	public void setModuloAnnessi(boolean modAnnessi) {
		this.modAnnessi = modAnnessi;
	}

	public void setModuloTabelle(boolean modTabelle) {
		this.modTabelle = modTabelle;
	}

	public void setLoggerLevel(String logLevel) throws XmLegesMarkerException {
		checkParam(LOGGER_VALORE, logLevel);
		this.logLevel = logLevel;
	}
	
	
	public InputStream parseAutoTipoDoc(File file) {
		
		execMonitor.clear();
		
		// From buildCommand 
		// FIXME fare copia in dir parser/struttura
		// if (firsTime) {
										//copyProgram();		spostato nell'inizialize
		// firsTime = false;
		// }

		File commandFile = UtilFile.getFileFromTemp("xmLeges-Marker.exe");
		UtilFile.setExecPermission(commandFile);
		StringBuffer sb = new StringBuffer(commandFile.getAbsolutePath());
		sb.append(" -i ");
		sb.append(format);
		sb.append(" -t unknown");
		String command = sb.toString();
		
		logger.debug("Command=" + command);
		try {
			UtilFile.copyFileInTemp(new FileInputStream(file), "unknown_type.in");
			command += " -f "  + UtilFile.getTempDirName() + File.separatorChar + "unknown_type.in";
			exec.runCommand(command);
			error = exec.getStderr().trim();
			if (error.length() == 0)
				error = null;
			return new FileInputStream(UtilFile.getTempDirName() + File.separatorChar + "unknown_type.tmp");
		} catch (ExecTimeoutException ex) {
			logger.error("timeout", ex);
		} catch (ExecException ex) {
			logger.error("error", ex);
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
		execMonitor.close();
		return null;
	}

	
	public InputStream parse(File file) {
		execMonitor.clear();
		String command = buildCommand();
		logger.debug("Command=" + command);
		try {
			UtilFile.copyFileInTemp(new FileInputStream(file), "import");
			command += " -f " + UtilFile.getTempDirName() + File.separatorChar + "import";
			command += " -o " + UtilFile.getTempDirName() + File.separatorChar + "import.xml";
			
			String commandUnz = command + " -o " + UtilFile.getTempDirName() + File.separatorChar + "importUnz.xml -Z ";

			exec.runCommand(commandUnz);
			
			if( this.tipoDoc.equals("ddlr")) {
				postFix = true; //Attiva fix xml per ddl/pdl regionali
				this.tipoDoc = "Legge"; //In caso di fix XML a posteriori, lancia il parser come fosse una Legge.				
			}

			if(postFix) {
				command += " -Z "; //Disattiva analisi header e mette l'intestazione in error per analisi successiva
			}
			
			System.out.println("Executing " + command);
			exec.runCommand(command);
			error = exec.getStderr().trim();
			if (error.length() == 0)
				error = null;
			// return new ByteArrayInputStream(exec.getStdout().getBytes());
			if(!postFix) {
				return new FileInputStream(UtilFile.getTempDirName() + File.separatorChar + "import.xml");
			} else {
				//Fix a posteriori dell'XML -- Supporto ai Disegni/Proposte di legge regionali (Piemonte)
				DdlRegionaliMarker drm = new DdlRegionaliMarker();
				drm.setInputFileName(UtilFile.getTempDirName() + File.separatorChar + "import.xml");
				drm.setOutputFileName(UtilFile.getTempDirName() + File.separatorChar + "import2.xml");
				drm.setEncoding(this.encoding);
				if(drm.processXML()) {
					return new FileInputStream(UtilFile.getTempDirName() + File.separatorChar + "import2.xml");
				} else {
					return new FileInputStream(UtilFile.getTempDirName() + File.separatorChar + "import.xml");
				}
			}
		} catch (ExecTimeoutException ex) {
			logger.error("timeout", ex);
		} catch (ExecException ex) {
			logger.error("error", ex);
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
		execMonitor.close();
		return null;
	}

	
	public String getError() {
		return error;
	}

	protected boolean firsTime = true;

	
	protected String buildCommand() {
		
		// FIXME fare copia in dir parser/struttura
		// if (firsTime) {
	    //copyProgram();
		// firsTime = false;
		// }
		
		
		// xmLeges-Marker.exe gia' copiato nella temp da parseAutoTipoDoc
		File command = UtilFile.getFileFromTemp("xmLeges-Marker.exe");
		UtilFile.setExecPermission(command);
		StringBuffer sb = new StringBuffer(command.getAbsolutePath());
		sb.append(" -i ");
		sb.append(format);
		sb.append(" -d ");
		sb.append(dtd);
		sb.append(" -t ");
		if(tipoDoc.equals("ddlr")) {
			sb.append("l");			
		} else {
			sb.append(tipoDoc);
		}
		if (tipoDocAltro != null && tipoDocAltro.trim().length()>0) {
			sb.append(" -T ");
			sb.append(tipoDocAltro);
		}
		sb.append(" -C ");
		sb.append(tipoComma);
		sb.append(" -r ");
		sb.append(tipoRubrica);
		// sb.append(" -R ");
		// sb.append(testoConRubriche);
		if (encoding != null) {
			sb.append(" -e ");
			sb.append(encoding);
		}
		sb.append(" -s ");
		sb.append(controlloSequenza ? 1 : 0); // TODO
		// TODO MODULI
		// sb.append(" - ");
		// sb.append();
		// this.modAnnessi = modAnnessi;
		// this.modTabelle = modTabelle;
		sb.append(" -v ");
		sb.append(logLevel);
		sb.append(" -L file=" + UtilFile.getTempDirName() + "/ps.err");
		return sb.toString();
	}

	protected void checkParam(String[] array, String value) throws XmLegesMarkerException {
		for (int i = 0; i < array.length; i++)
			if (array[i].equals(value))
				return;
		throw new XmLegesMarkerException("Parametro non valido:" + value);
	}

	protected void copyProgram() {
		String osName = System.getProperty("os.name");
		String osArch = System.getProperty("os.arch");

		String[] files = null;
		if (osName.equalsIgnoreCase("linux") && osArch.equalsIgnoreCase("i386"))
			files = new String[] { "linux-i386/xmLeges-Marker.exe" };
		if (osName.toLowerCase().matches("windows.*"))
			files = new String[] { "win-32/cygiconv-2.dll", "win-32/cygwin1.dll", "win-32/cygxml2-2.dll", "win-32/cygz.dll", "win-32/xmLeges-Marker.exe" };

		if (files != null)
			for (int i = 0; i < files.length; i++)
				UtilFile.copyFileInTemp(getClass().getResourceAsStream(files[i]), files[i]);

	}
}
