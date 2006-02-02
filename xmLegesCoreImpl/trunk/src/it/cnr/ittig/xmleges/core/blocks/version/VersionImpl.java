package it.cnr.ittig.xmleges.core.blocks.version;

import it.cnr.ittig.services.manager.Configurable;
import it.cnr.ittig.services.manager.Configuration;
import it.cnr.ittig.services.manager.ConfigurationException;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.form.Form;
import it.cnr.ittig.xmleges.core.services.util.msg.UtilMsg;
import it.cnr.ittig.xmleges.core.services.version.Version;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * <h1>Implementazione del servizio <code>it.cnr.ittig.xmleges.editor.services.version.Version</code>.</h1>
 * <h1>Descrizione</h1>
 * Questa implementazione restituisce la versione corrente scritta nel file di
 * configurazione (tag <code>&lt;version&gt;</code>). Permette inoltre di visualizzare
 * una form con la lista delle nuove versioni e relative descrizioni. Il file remoto (tag
 * <code>&lt;url&gt;</code>) contenente la lista delle versioni deve essere ordinato in
 * modo decrescente e avere i seguenti campi separati dal carattere '|' (pipe):
 * <ul>
 * <li>versione;</li>
 * <li>url del file da recuperare;</li>
 * <li>url del file di descrizione (possibilmente html).</li>
 * </ul>
 * Esempio del file remoto contenente le versioni:
 * 
 * <pre>
 *   2.0.alpha.2|http://xmleges.ittig.cnr.it/version/2.0.alpha.2/editor.xml|http://xmleges.ittig.cnr.it/version/2.0.alpha.2/desc.html
 *   2.0.alpha.1|http://xmleges.ittig.cnr.it/version/2.0.alpha.1/editor.xml|http://xmleges.ittig.cnr.it/version/2.0.alpha.1/desc.html
 * </pre>
 * 
 * <h1>Configurazione</h1>
 * Il file di configurazione deve avere i seguenti tag:
 * <ul>
 * <li>&lt;version&gt; che contiene la versione corrente;</li>
 * <li>&lt;url&gt; che contiene la URL delle nuove versioni.</li>
 * </ul>
 * Esempio:
 * 
 * <pre>
 *   &lt;version&gt;2.0-alpha.1&lt;/version&gt;
 *   &lt;url&gt;http://xmleges.ittig.cnr.it/version/versions.txt&lt;/url&gt;
 * </pre>
 * 
 * <h1>Dipendenze</h1>
 * <ul>
 * <li>it.cnr.ittig.xmleges.editor.services.form.Form:1.0</li>
 * <li>it.cnr.ittig.xmleges.editor.services.util.msg.UtilMsg:1.0</li>
 * </ul>
 * <h1>I18n</h1>
 * <ul>
 * <li>version.noupdate: messaggio se non ci sono aggiornamenti</li>
 * <li>version.rerun: messaggio per rilanciare l'applicazione</li>
 * <li>version.listlabel: etichetta per la lista delle versioni</li>
 * <li>version.desclabel: etichetta per la descrizione della versione selezionata</li>
 * <li>version.text: testo del titolo della form</li>
 * <li>version.icon: icona della form</li>
 * </ul>
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
 * @see it.cnr.ittig.xmleges.core.services.version.Version
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>, Maurizio Mollicone
 */
public class VersionImpl implements Version, ListSelectionListener, Loggable, Serviceable, Configurable {
	Logger logger;

	Form form;

	UtilMsg utilMsg;

	String version;

	String url;

	JList list;

	JEditorPane desc;

	VersionListItem versionSelected;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	// /////////////////////////////////////////////////// Serviceable Interface
	public void service(ServiceManager serviceManager) throws ServiceException {
		form = (Form) serviceManager.lookup(Form.class);
		utilMsg = (UtilMsg) serviceManager.lookup(UtilMsg.class);
	}

	// ////////////////////////////////////////////////// Configurable Interface
	public void configure(Configuration configuration) throws ConfigurationException {
		version = configuration.getChild("version").getValue();
		url = configuration.getChild("url").getValue();
	}

	// /////////////////////////////////////////////////////// Version Interface
	public String getVersion() {
		return version;
	}

	public void update() {
		try {
			form.setMainComponent(this.getClass().getResourceAsStream("Version.jfrm"));
			form.setSize(700, 400);
			form.setName("version");
			list = (JList) form.getComponentByName("version.list");
			desc = (JEditorPane) form.getComponentByName("version.desc");
			list.addListSelectionListener(this);
		} catch (Exception ex) {
			utilMsg.msgError(ex.toString());
			logger.error("Error creating form", ex);
		}
		VersionListItem[] items = getUpdatesArray();
		if (items == null || items.length == 0) {
			utilMsg.msgInfo("version.noupdate");
			return;
		}
		list.setListData(items);
		form.showDialog();
		if (form.isOk()) {
			try {
				String url = versionSelected.getUrl();
				String file = url.substring(url.lastIndexOf('/') + 1);
				String dest = System.getProperties().getProperty("user.dir") + File.separatorChar + file;
				UtilFile.copyFile(new URL(versionSelected.getUrl()).openStream(), dest);
				utilMsg.msgInfo("version.rerun");
				logger.info("New version in " + dest);
			} catch (Exception ex) {
				utilMsg.msgError(ex.toString());
				logger.error("Reading file from remote host" + ex);
			}
		}
	}

	protected VersionListItem[] getAvailableUpdates() {
		try {
			// Crea una matrice nx3 contiene: numeri versione, URL download e
			// URL descrizione
			String[] lines = UtilFile.inputStreamToStringArray(new URL(url).openStream());
			VersionListItem[] items = new VersionListItem[lines.length];
			// recupera le linee del file delle versioni
			for (int i = 0; i < lines.length; i++) {
				logger.debug("line " + i + ':' + lines[i]);
				StringTokenizer st = new StringTokenizer(lines[i], "|");
				items[i] = new VersionListItem();
				if (st.hasMoreTokens())
					items[i].setVersion(st.nextToken());
				if (st.hasMoreTokens())
					items[i].setUrl(st.nextToken());
				if (st.hasMoreTokens())
					try {
						items[i].setDescription(UtilFile.inputStreamToString(new URL(st.nextToken()).openStream()));
					} catch (Exception ex) {
						logger.error("Reading description from remote host" + ex);
					}
			}
			return items;
		} catch (MalformedURLException e) {
			utilMsg.msgError(e.toString());
			logger.error(e.toString(), e);
		} catch (IOException e) {
			utilMsg.msgError(e.toString());
			logger.error(e.toString(), e);
		}
		return new VersionListItem[0];
	}

	protected VersionListItem[] getUpdatesArray() {
		VersionListItem[] items = getAvailableUpdates();
		int lastUpdate = items.length;
		for (int i = 0; i < items.length; i++)
			if (items[i].getVersion().equalsIgnoreCase(getVersion())) {
				lastUpdate = i;
				break;
			}
		VersionListItem[] ret = new VersionListItem[lastUpdate];
		for (int i = 0; i < lastUpdate; i++)
			ret[i] = items[i];
		return ret;
	}

	// ///////////////////////////////////////// ListSelectionListener Interface
	public void valueChanged(ListSelectionEvent e) {
		versionSelected = (VersionListItem) list.getSelectedValue();
		desc.setText(versionSelected.getDescription());
	}

}
