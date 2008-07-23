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
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipFile;

import org.dts.spell.SpellChecker;
import org.dts.spell.dictionary.OpenOfficeSpellDictionary;
import org.dts.spell.dictionary.SpellDictionary;
import org.dts.spell.event.SpellCheckAdapter;
import org.dts.spell.event.SpellCheckEvent;
import org.dts.spell.finder.CharSequenceWordFinder;
import org.dts.spell.finder.Word;



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
 * <dt><b>Lincense: </b></dt>
 * <dd><a href="http://www.gnu.org/licenses/gpl.html" target="_blank">GNU General Public
 * License </a></dd>
 * </dl>
 * 
 * @version 1.0
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @author <a href="mailto:agnoloni@dsi.unifi.it">Tommaso Agnoloni </a>
 */
public class SpellCheckImpl extends SpellCheckAdapter implements SpellCheck, Loggable, Serviceable, Initializable  {

	Logger logger;

	PreferenceManager preferenceManager;

	I18n i18n;

	private static String dictFile = "it_IT.zip";

	private static String phonetFile = null;

	
	private SpellDictionary dictionary = null;
	
	private SpellChecker spellCheck;

	private List suggestions;

	private Vector invalidWordsVect;

	private String testo;

	ThreadManager threadManager;
	
	CharSequenceWordFinder wordFinder;

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
			
			ZipFile zip = new ZipFile(UtilFile.getFileFromTemp(dictFile).getAbsolutePath());
			
			if (null == dictionary)
				dictionary = new OpenOfficeSpellDictionary(zip);//dictFile/*i18n.getTextFor("spellcheck.dictionary")*/));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		spellCheck = new SpellChecker(dictionary);
		spellCheck.setCaseSensitive(false);
		
		
	
		text= "qusto testo e corrretto";
		wordFinder = new CharSequenceWordFinder(text);
		
		spellCheck.check(wordFinder, this);
		
		
				
		test(spellCheck, "piu");
		test(spellCheck, "gatto");
		test(spellCheck, "più");
		test(spellCheck, "mas");
		
//		spellCheck.addSpellCheckListener(this);
//
//		if (text != null) {
//			this.testo = text;
//			invalidWordsVect = new Vector();
//			spellCheck.checkSpelling(new StringWordTokenizer(text));
//
//			SpellCheckWord[] ret = new SpellCheckWord[invalidWordsVect.size()];
//			invalidWordsVect.copyInto(ret);
//			return ret;
//		}
		return null;
	}

	
	private void test(SpellChecker checker, String txt){
		Word badWord =checker.checkSpell(txt);
		if(badWord == null){
			System.out.println("OK");
		}else{
			System.out.println("WRONG");
		}
	}
	
	public void spellingError(SpellCheckEvent event) {
		
		System.err.println("INVALID WORD:     "+event.getCurrentWord().getText());

		int startOffset = event.getCurrentWord().getStart();//.getWordContextPosition();
		int endOffset = startOffset + event.getCurrentWord().length();
		
		String[] sugg = getSuggestions(event.getCurrentWord().getText());
		
		for(int i=0;i<sugg.length;i++){
			System.err.println("sugg for "+event.getCurrentWord().getText() +": "+ sugg[i]);
		}
		
		// int startOffset = testo.indexOf(event.getInvalidWord());
		// int endOffset = startOffset+event.getInvalidWord().length();
		//if (event.getCurrentWord().getText().trim().length() > 1)
			//invalidWordsVect.add(new SpellCheckWordImpl(event.getCurrentWord().getText(), startOffset, endOffset));
	}

	public String[] getSuggestions(String word) {
		if (word != null) {
			List suggestions = dictionary.getSuggestions(word, 5);
			if (suggestions.size() > 0) {
				String[] ret = new String[suggestions.size()];
				int i = 0;
				for (Iterator suggestedWord = suggestions.iterator(); suggestedWord.hasNext();)
					ret[i++] = (((Word) suggestedWord.next()).getText());
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

	public void addSuggestion(String word, String suggestion, boolean temporaryDict) {
		// TODO Auto-generated method stub
		
	}

	public void addWord(String word, boolean temporaryDict) {
		// TODO Auto-generated method stub
		
	}

	public boolean isLoad() {
		// TODO Auto-generated method stub
		return true;
	}

	
	
	
	
	


}
