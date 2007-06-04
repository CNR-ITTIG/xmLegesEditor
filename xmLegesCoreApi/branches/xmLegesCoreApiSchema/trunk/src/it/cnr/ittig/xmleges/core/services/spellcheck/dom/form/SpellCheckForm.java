package it.cnr.ittig.xmleges.core.services.spellcheck.dom.form;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la form controllo ortografico.
 * 
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
 * @author <a href="mailto:t.paba@onetech.it">Tommaso Paba</a>
 */
public interface SpellCheckForm extends Service {

	/**
	 * Apre la form per il controllo ortografico
	 * 
	 * @return <code>true</code> se la form &egrave; valida
	 */
	public boolean openForm();

	/**
	 * Imposta la parola originale
	 * 
	 * @param originalWord parola originale
	 */
	public void setOriginalWord(String originalWord);

	/**
	 * Imposta la parola da controllare
	 * 
	 * @param word parola da controllare
	 */
	public void setWord(String word);

	/**
	 * Imposta i suggerimenti
	 * 
	 * @param suggestions suggerimenti
	 */
	public void setSuggestions(String[] suggestions);

	/**
	 * Imposta l'icona status parola
	 * 
	 * @param status stato dell'icona
	 */
	public void setStatusIcon(int status);

	/**
	 * Restituisce la parola inserita dell'utente
	 * 
	 * @return parola inserita dall'utente
	 */
	public String getWord();
}
