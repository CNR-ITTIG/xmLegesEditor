package it.cnr.ittig.xmleges.core.services.mswordconverter;

import it.cnr.ittig.services.manager.Service;

import java.io.File;

/**
 * Servizio per convertire file di Microsoft Word.
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
public interface MSWordConverter extends Service {

	/**
	 * Converte il file <code>src</code> in formato txt. Se la conversione
	 * avviene con successo il file &egrave; restituito dal metodo, altrimenti
	 * &egrave; restituito <code>null</code>.
	 * 
	 * @param src file da convertire
	 * @return file convertito o <code>null</code>
	 */
	public File convert(File src);

	/**
	 * Converte il file <code>src</code> in formato txt. Se la conversione
	 * avviene con successo il file &egrave; restituito dal metodo, altrimenti
	 * &egrave; restituito <code>null</code>.
	 * 
	 * @param src file da convertire
	 * @param column numero di colonne per riga
	 * @return file convertito o <code>null</code>
	 */
	public File convert(File src, int column);

	/**
	 * Converte il file <code>src</code> in formato txt. Se la conversione
	 * avviene con successo il file &egrave; restituito dal metodo, altrimenti
	 * &egrave; restituito <code>null</code>.
	 * 
	 * @param src file da convertire
	 * @param column numero di colonne per riga
	 * @param map file per trasformare i caratteri
	 * @return file convertito o <code>null</code>
	 */
	public File convert(File src, int column, String map);

}
