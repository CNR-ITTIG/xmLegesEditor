package it.cnr.ittig.xmleges.editor.blocks.editor;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.services.manager.Startable;
import it.cnr.ittig.xmleges.core.services.frame.Frame;
import it.cnr.ittig.xmleges.core.services.license.LicenseManager;
import it.cnr.ittig.xmleges.core.services.threads.ThreadManager;
import it.cnr.ittig.xmleges.core.services.version.Version;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.editor.XmLegesEditor;


/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.editor.XmLegesEditor</code>.</h1>
 * <h1>Descrizione</h1>
 * Servizio per l'esecuzione dell'editor.
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.core.services.license.LicenseManager:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.version.Version:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.frame.Frame:1.0</li>
 * <li>it.cnr.ittig.xmleges.core.services.threads.threadManager:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * Nessuno
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
public class XmLegesEditorImpl implements XmLegesEditor, Loggable, Serviceable, Startable {
	Logger logger;

	LicenseManager licenseManager;

	Version version;

	Frame frame;

	ThreadManager threadManager;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		licenseManager = (LicenseManager) serviceManager.lookup(LicenseManager.class);
		version = (Version) serviceManager.lookup(Version.class);
		frame = (Frame) serviceManager.lookup(Frame.class);
		threadManager = (ThreadManager) serviceManager.lookup(ThreadManager.class);
	}

	// ///////////////////////////////////////////////////// Startable Interface
	public void start() throws Exception {
		frame.setInteraction(false);
		threadManager.execute(new Runnable() {
			public void run() {
				String key = version.getVersion();
				String text = null;
				try {
					text = UtilFile.inputStreamToString(getClass().getResourceAsStream("gpl.html"));
				} catch (Exception ex) {
					logger.fatalError("license not found", ex);
					System.exit(-1);
				}
				if (!licenseManager.checkLicense(key, text))
					System.exit(-1);
				else {
					
					String[] files = new String[] { 
							
							// NIR 2.2 
							"dtd-2.2/globali.dtd", "dtd-2.2/meta.dtd", "dtd-2.2/nirbase.dtd","dtd-2.2/nircompleto.dtd", "dtd-2.2/nirflessibile.dtd", 
							"dtd-2.2/norme.dtd", "dtd-2.2/testo.dtd", "dtd-2.2/tipi.dtd", "dtd-2.2/pst.dtd", "dtd-2.2/proprietario.dtd",
							
							// ENTITIES
							"dtd-2.2/ISOdia.ent", "dtd-2.2/ISOgrk3.ent", "dtd-2.2/ISOlat1.ent", "dtd-2.2/ISOlat2.ent",
							"dtd-2.2/ISOnum.ent", "dtd-2.2/ISOpub.ent", "dtd-2.2/ISOtech.ent",
							
							// DISEGNI DI LEGGE
							"dtd-dl/dllight.dtd", "dtd-dl/dllight.spp",
							
							// CNR
							"dtd-cnr/cnr.dtd",

							// ITTIG
							"dtd-ittig/ittig.dtd",
							
							// PACTO
							"dtd-pacto/pacto.dtd"
				            };
					
					for (int i = 0; i < files.length; i++) {
						UtilFile.copyFileInTemp(getClass().getResourceAsStream(files[i]), files[i]);
					}
					
					
					String[] entities = new String[]{"dtd-2.2/ISOdia.ent", "dtd-2.2/ISOgrk3.ent", "dtd-2.2/ISOlat1.ent", "dtd-2.2/ISOlat2.ent", "dtd-2.2/ISOnum.ent",
							"dtd-2.2/ISOpub.ent",  "dtd-2.2/ISOtech.ent"};
					
					// copia nella sottodirectory entities
					
					for (int i = 0; i < entities.length; i++) {
						UtilFile.copyFileInTempDir(getClass().getResourceAsStream(entities[i]), "entities", entities[i]);
					}					
					
					frame.setInteraction(true);
				}
			}
		});
	}

	public void stop() throws Exception {
	}
}
