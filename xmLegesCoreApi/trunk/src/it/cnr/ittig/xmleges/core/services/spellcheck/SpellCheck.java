package it.cnr.ittig.xmleges.core.services.spellcheck;

import it.cnr.ittig.services.manager.Service;

/**
 * Servizio per la gestione del controllo ortografico del testo.
 * <p>
 * <dl>
 * <dt><b>Copyright &copy;: </b></dt>
 * <dd>2003 - 2004</dd>
 * <dd><a href="http://www.ittig.cnr.it" target="_blank">Istituto di Teoria e
 * Tecniche dell'Informazione Giuridica (ITTIG) <br>
 * Consiglio Nazionale delle Ricerche - Italy </a></dd>
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU
 * General Public License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @author <a href="mailto:agnoloni@dsi.unifi.it">Tommaso Agnoloni </a>
 */
public interface SpellCheck extends Service {

	/**
	 * Restituisce la lista dei suggerimenti per una data parola
	 * 
	 * @param word parola di cui cercare i suggerimenti
	 */
	public String[] getSuggestions(String word);

	/**
	 * Esegue il controllo ortografico sul testo <code>text</code>.
	 * 
	 * @param text testo sul quale effettuare il controllo ortografico
	 * @return elenco delle parole con errore
	 */
	public SpellCheckWord[] spellCheck(String text);

	/**
	 * Restituisce la lista dei dizionari <code>custom</code>.
	 * 
	 * @return lista nei nomi dei dizionari
	 */
	public String[] getDictionaries();

	/**
	 * Aggiunge il dizionario <code>custom</code> con il nome
	 * <code>name</code>.
	 * 
	 * @param name nome del dizionazio
	 */
	public void addDictionary(String name);

	/**
	 * Rimuove il dizionario <code>custom</code> di nome <code>name</code>.
	 * 
	 * @param name nome del dizionario
	 */
	public void removeDictionary(String name);

	/**
	 * Imposta il dizionario <code>custom</code> con nome <code>name</code>.
	 * 
	 * @param name nome del dizionario
	 */
	public void setCustomDictionary(String name);

	/**
	 * Aggiunge la parola <code>word</code> al dizionario <code>custom</code>.
	 * 
	 * @param word parola da aggiungere
	 */
	public void addWord(String word);

	/**
	 * Rimuove la parola <code>word</code> dal dizionario <code>custom</code>.
	 * 
	 * @param word parola da rimuovere
	 */
	public void removeWord(String word);

	/**
	 * Modifica la parola <code>oldWord</code> con <code>newWord</code>.
	 * 
	 * @param oldWord parola da modificare
	 * @param newWord nuova parola da sostituire
	 */
	public void modifyWord(String oldWord, String newWord);

}
