package it.cnr.ittig.xmleges.core.util.xml;

import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
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
 * @author Gianni Giorgetti
 */
public class DtdResolver implements EntityResolver {

	public InputSource resolveEntity(String publicId, String systemId) {
		boolean localFile = true;
		File dtdFile;

		try {

			// preprocess sytemId's syntax
			if (systemId.startsWith("file:")) {
				systemId = systemId.substring(5);
			} else if (systemId.startsWith("file://")) {
				systemId = systemId.substring(7);
			} else if (systemId.startsWith("http://")) {
				// system id is not a local file. es. http://...
				localFile = false;
			}

			if (localFile) {
				// try to locate the file on the current machine
				dtdFile = new File(systemId);
				if (dtdFile.canRead()) {
					// ok. file can be read from current position
					// return null for default file handling
					return null;
				} else {
					// file cannot be read from current position
					// try to locate it in the tmp directory
					String name = dtdFile.getName();
					dtdFile = new File(UtilFile.getTempDirName() + File.separatorChar + name);
					if (dtdFile.canRead()) {
						// replace the file with the local copy
						InputSource is = new InputSource(new FileInputStream(dtdFile));
						// !! fondamentale senno non riesce a risolvere gli URI
						// successivi
						is.setSystemId("file://" + dtdFile.getAbsolutePath());
						return is;
					} else {
						// cannot find a local version of the file
						// return null for default handling
						return null;
					}
				}
			} else {
				// systemId is not a local file. try to replace it
				// with a local copy of the same file
				URI dtdUri = new URI(systemId);
				dtdFile = new File(dtdUri);
				String name = dtdFile.getName();
				dtdFile = new File(UtilFile.getTempDirName() + File.separatorChar + name);
				if (dtdFile.canRead()) {
					// replace the file with the local copy
					InputSource is = new InputSource(new FileInputStream(dtdFile));
					// !! fondamentale altrimenti non riesce a risolvere gli URI
					// successivi
					is.setSystemId("file://" + dtdFile.getAbsolutePath());
					return is;
				} else {
					// cannot find a local version of the file
					// return null for default handling
					return null;
				}
			}
		} catch (Exception ex) {
			return null;
		}
	}

}
