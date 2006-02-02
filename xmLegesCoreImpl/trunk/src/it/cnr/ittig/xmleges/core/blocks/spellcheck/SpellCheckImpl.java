package it.cnr.ittig.xmleges.core.blocks.spellcheck;

import it.cnr.ittig.services.manager.Initializable;
import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.services.manager.ServiceException;
import it.cnr.ittig.services.manager.ServiceManager;
import it.cnr.ittig.services.manager.Serviceable;
import it.cnr.ittig.xmleges.core.services.i18n.I18n;
import it.cnr.ittig.xmleges.core.services.preference.PreferenceManager;
import it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheck;
import it.cnr.ittig.xmleges.core.services.spellcheck.SpellCheckWord;
import it.cnr.ittig.xmleges.core.services.threads.ThreadManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

/**
 * Implementazione del servizio per la gestione del controllo ortografico del testo.
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni </a>
 */
public class SpellCheckImpl implements SpellCheck, Loggable, SpellCheckListener, Serviceable, Initializable {

	Logger logger;

	PreferenceManager preferenceManager;

	I18n i18n;

	private static String dictFile = "it.dic";

	private static String phonetFile = null;

	private SpellChecker spellCheck = null;

	private SpellDictionary dictionary = null;

	private List suggestions;

	private Vector invalidWordsVect;

	private String testo;

	ThreadManager threadManager;

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		threadManager = (ThreadManager) serviceManager.lookup(ThreadManager.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	public void initialize() throws Exception {

		// FIXME per il momento evito di caricare il dizionario all'avvio visto che prende
		// molta memoria

		// threadManager.execute(new Runnable(){
		// public void run(){
		// try{
		// dictionary = new SpellDictionaryHashMap(new
		// InputStreamReader(getClass().getResourceAsStream(dictFile)));
		// }
		// catch(IOException e){
		// logger.error(e.getMessage(),e);
		// }
		// }
		// });
	}

	public SpellCheckWord[] spellCheck(String text) {

		try {
			if (null == dictionary)
				dictionary = new SpellDictionaryHashMap(new InputStreamReader(getClass().getResourceAsStream(i18n.getTextFor("spellcheck.dictionary"))));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		spellCheck = new SpellChecker(dictionary);
		spellCheck.addSpellCheckListener(this);

		if (text != null) {
			this.testo = text;
			invalidWordsVect = new Vector();
			spellCheck.checkSpelling(new StringWordTokenizer(text));

			SpellCheckWord[] ret = new SpellCheckWord[invalidWordsVect.size()];
			invalidWordsVect.copyInto(ret);
			return ret;
		}
		return null;
	}

	public void spellingError(SpellCheckEvent event) {

		int startOffset = event.getWordContextPosition();
		int endOffset = startOffset + event.getInvalidWord().length();
		// int startOffset = testo.indexOf(event.getInvalidWord());
		// int endOffset = startOffset+event.getInvalidWord().length();
		if (event.getInvalidWord().trim().length() > 1)
			invalidWordsVect.add(new SpellCheckWordImpl(event.getInvalidWord(), startOffset, endOffset));
	}

	public String[] getSuggestions(String word) {
		if (word != null) {
			List suggestions = dictionary.getSuggestions(word, 1);
			if (suggestions.size() > 0) {
				String[] ret = new String[suggestions.size()];
				int i = 0;
				for (Iterator suggestedWord = suggestions.iterator(); suggestedWord.hasNext();)
					ret[i++] = (((Word) suggestedWord.next()).getWord());
				return ret;
			}
		}
		return null;
	}

	public String[] getDictionaries() {
		// TODO getDictionaries
		return null;
	}

	public void addDictionary(String name) {
		// TODO addDictionary
	}

	public void removeDictionary(String name) {
		// TODO removeDictionary
	}

	public void setCustomDictionary(String name) {
		// TODO setCustomDictionary
	}

	public void addWord(String word) {
		// TODO addWord
	}

	public void removeWord(String word) {
		// TODO removeWord
	}

	public void modifyWord(String oldWord, String newWord) {
		// TODO modifyWord
	}

}
