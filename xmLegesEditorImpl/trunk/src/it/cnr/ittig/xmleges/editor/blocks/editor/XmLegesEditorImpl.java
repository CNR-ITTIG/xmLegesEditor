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
 * <h1>Configurazione</h1>
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>license-manager</li>
 * <li>version</li>
 * <li>frame</li>
 * <li>thread-manager</li>
 * </ul>
 * <h1>I18n</h1>
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
					
//					String[] files = new String[] { "dtd-2.0/globali.dtd", "dtd-2.0/meta.dtd", "dtd-2.0/namespaces.dtd", "dtd-2.0/nirbase.dtd",
//							"dtd-2.0/nircompleto.dtd", "dtd-2.0/nirflessibile.dtd", "dtd-2.0/norme.dtd", "dtd-2.0/testo.dtd", "dtd-2.0/tipi.dtd", 
//
//							"dtd-2.0/ISOdia.ent", "dtd-2.0/ISOgrk3.ent", "dtd-2.0/ISOlat1.ent", "dtd-2.0/ISOlat2.ent", "dtd-2.0/ISOnum.ent",
//							"dtd-2.0/ISOpub.ent", "dtd-2.0/ISOtech.ent",
//
//							"dtd-dl/dllight.dtd", "dtd-dl/dllight.spp", "dtd-dl/ISOdia.ent", "dtd-dl/ISOgrk3.ent", "dtd-dl/ISOlat1.ent", "dtd-dl/ISOlat2.ent",
//							"dtd-dl/ISOnum.ent", "dtd-dl/ISOpub.ent", "dtd-dl/ISOtech.ent",
//							
//							"dtd-cnr/cnr.dtd", "dtd-cnr/globali-cnr.dtd", "dtd-cnr/meta-cnr.dtd", "dtd-cnr/nirflessibile-cnr.dtd", "dtd-cnr/norme-cnr.dtd",
//							"dtd-cnr/testo-cnr.dtd", "dtd-cnr/tipi-cnr.dtd"};
//					
					String[] files = new String[] { "dtd-2.1/globali.dtd", "dtd-2.1/meta.dtd", "dtd-2.1/namespaces.dtd", "dtd-2.1/nirbase.dtd",
							"dtd-2.1/nircompleto.dtd", "dtd-2.1/nirflessibile.dtd", "dtd-2.1/norme.dtd", "dtd-2.1/testo.dtd", "dtd-2.1/tipi.dtd", "dtd-2.1/pst.dtd",

							"dtd-dl/dllight.dtd", "dtd-dl/dllight.spp", "dtd-dl/ISOdia.ent", "dtd-dl/ISOgrk3.ent", "dtd-dl/ISOlat1.ent", "dtd-dl/ISOlat2.ent",
							"dtd-dl/ISOnum.ent", "dtd-dl/ISOpub.ent", "dtd-dl/ISOtech.ent",
							
							"dtd-cnr/cnr.dtd", "dtd-cnr/globali-cnr.dtd", "dtd-cnr/meta-cnr.dtd", "dtd-cnr/nirflessibile-cnr.dtd", "dtd-cnr/norme-cnr.dtd",
							"dtd-cnr/testo-cnr.dtd", "dtd-cnr/tipi-cnr.dtd",
							
//							"dtd-2.1/ISOdia.ent", "dtd-2.1/ISOgrk3.ent", "dtd-2.1/ISOlat1.ent", "dtd-2.1/ISOlat2.ent", "dtd-2.1/ISOnum.ent",
//							"dtd-2.1/ISOpub.ent", "dtd-2.1/ISOtech.ent"
				            };
					
					for (int i = 0; i < files.length; i++) {
						UtilFile.copyFileInTemp(getClass().getResourceAsStream(files[i]), files[i]);
					}
					
					
					String[] entities = new String[]{"dtd-2.1/ISOdia.ent", "dtd-2.1/ISOdia.dtd", "dtd-2.1/ISOgrk3.ent", "dtd-2.1/ISOgrk3.dtd", "dtd-2.1/ISOlat1.ent", "dtd-2.1/ISOlat1.dtd", "dtd-2.1/ISOlat2.ent", "dtd-2.1/ISOlat2.dtd", "dtd-2.1/ISOnum.ent",
							"dtd-2.1/ISOpub.ent", "dtd-2.1/ISOpub.dtd", "dtd-2.1/ISOtech.ent",  "dtd-2.1/ISOtech.dtd"};
					
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
