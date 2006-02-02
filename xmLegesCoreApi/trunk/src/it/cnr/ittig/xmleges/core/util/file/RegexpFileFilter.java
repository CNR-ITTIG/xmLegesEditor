package it.cnr.ittig.xmleges.core.util.file;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Filtro per JFileChooser con espressioni regolari.
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
public class RegexpFileFilter extends FileFilter {
	/** Descrizione visualizzata nella finestra di dialogo. */
	String descr;

	/** Espressioni regolari per filtrare i file. */
	String[] masks;

	/**
	 * Costruttore di RegexpFileFilter.
	 * 
	 * @param descr descrizione per la visualizzazione
	 * @param mask espressione regolare per filtrare i file
	 */
	public RegexpFileFilter(String descr, String mask) {
		this(descr, new String[] { mask });
	}

	/**
	 * Costruttore di RegexpFileFilter.
	 * 
	 * @param descr descrizione per la visualizzazione
	 * @param masks espressioni regolari per filtrare i file
	 */
	public RegexpFileFilter(String descr, String[] masks) {
		this.descr = descr;
		this.masks = masks;
	}

	public boolean accept(File file) {
		if (file != null && (file.isDirectory() || checkMasks(file.getName())))
			return true;
		return false;
	}

	public String getDescription() {
		return descr;
	}

	protected boolean checkMasks(String name) {
		if (name != null)
			for (int i = 0; i < masks.length; i++)
				if (name.matches(masks[i]))
					return true;
		return false;
	}
}
