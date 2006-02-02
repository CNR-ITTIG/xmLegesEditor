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
import it.cnr.ittig.xmleges.editor.services.editor.XmLegisEditor;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmlegis.editor.services.editor.XmLegisEditor</code>.</h1>
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
public class XmLegesEditorImpl implements XmLegisEditor, Loggable, Serviceable, Startable {
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
					String[] files = new String[] { "dtd-1.1/globali.dtd", "dtd-1.1/meta.dtd", "dtd-1.1/namespaces.dtd", "dtd-1.1/nirbase.dtd",
							"dtd-1.1/nircompleto.dtd", "dtd-1.1/nirflessibile.dtd", "dtd-1.1/norme.dtd", "dtd-1.1/testo.dtd",

							"dtd-1.1/ISOdia.pen", "dtd-1.1/ISOgrk3.pen", "dtd-1.1/ISOlat1.pen", "dtd-1.1/ISOlat2.pen", "dtd-1.1/ISOnum.pen",
							"dtd-1.1/ISOpub.pen", "dtd-1.1/ISOtech.pen",

							"dtd-dl/dllight.dtd", "dtd-dl/dllight.spp", "dtd-dl/ISOdia.ent", "dtd-dl/ISOgrk3.ent", "dtd-dl/ISOlat1.ent", "dtd-dl/ISOlat2.ent",
							"dtd-dl/ISOnum.ent", "dtd-dl/ISOpub.ent", "dtd-dl/ISOtech.ent",
							
							"dtd-cnr/cnr.dtd", "dtd-cnr/globali-cnr.dtd", "dtd-cnr/meta-cnr.dtd", "dtd-cnr/nirflessibile-cnr.dtd", "dtd-cnr/norme-cnr.dtd",
							"dtd-cnr/testo-cnr.dtd", "dtd-cnr/tipi-cnr.dtd"};
					for (int i = 0; i < files.length; i++) {
						UtilFile.copyFileInTemp(getClass().getResourceAsStream(files[i]), files[i]);
					}
					frame.setInteraction(true);
				}
			}
		});
	}

	public void stop() throws Exception {
	}
}
