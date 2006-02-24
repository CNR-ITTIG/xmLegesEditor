package it.cnr.ittig.xmleges.editor.blocks.template;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;
import it.cnr.ittig.xmleges.editor.services.template.Template;
import it.cnr.ittig.xmleges.editor.services.template.TemplateException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.template.Template</code>.</h1>
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
 * @see
 * @version 1.0
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class TemplateImpl implements Template, Loggable {
	Logger logger;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public File getNirTemplate(String src, Properties props) throws TemplateException {
		return (getNirTemplate(getClass().getResourceAsStream(src), props));
	}

	public File getNirTemplate(InputStream is, Properties props) throws TemplateException {

		String dest = "nuovo.xml";
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		String codice = " codice=\"00100\"";
		if (props.getProperty("DOCTYPE").indexOf("base") != -1)
			codice = " ";

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
			// TODO se da' errore di codifica rimettere l'encoding new
			// InputStreamReader(is, "ISO-8859-1")
			String line;
			Pattern p = Pattern.compile("\\%([a-zA-Z]+)\\%"); // cerca stringe
																// di tipo %any%
			while ((line = br.readLine()) != null) {

				if (line.indexOf("%CODICE%") != -1)
					line = line.substring(0, line.indexOf('%') - 1) + codice + line.substring(line.lastIndexOf('%') + 1);

				Matcher m = p.matcher(line); // e le matcha con righe intere
				if (m.matches()) {
					if (props != null) {
						String val = props.getProperty(m.group(1));
						if (val != null)
							line = m.replaceAll(val);
						else
							line = m.replaceAll("");
					} else
						line = m.replaceAll("");
				}
				bw.write(line);
				bw.write('\n');
			}

			bw.flush();
			UtilFile.copyFileInTemp(new ByteArrayInputStream(baos.toByteArray()), dest);
			br.close();
			bw.close();
			return (UtilFile.getFileFromTemp(dest));
		} catch (Exception e) {
			throw new TemplateException("Errore", e);
		}
	}

}
