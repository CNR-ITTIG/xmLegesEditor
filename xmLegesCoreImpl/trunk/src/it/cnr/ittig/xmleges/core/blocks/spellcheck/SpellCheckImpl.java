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


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


import org.dts.spell.SpellChecker;
import org.dts.spell.dictionary.OpenOfficeSpellDictionary;
import org.dts.spell.dictionary.SpellDictionary;
import org.dts.spell.dictionary.SpellDictionaryException;
import org.dts.spell.event.SpellCheckAdapter;
import org.dts.spell.event.SpellCheckEvent;
import org.dts.spell.finder.CharSequenceWordFinder;






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

	private static String IT_dictFile = "it_IT.zip";
	
	private static String EN_dictFile = "en_GB.zip";
	
	private static String dictFile = "it_IT.zip";   //default

	private SpellDictionary dictionary = null;
	
	private SpellChecker spellCheck;

	private Vector invalidWordsVect;

	ThreadManager threadManager;
	
	CharSequenceWordFinder wordFinder;
	
	private String personal_dictionary="personal_dict.txt";

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		threadManager = (ThreadManager) serviceManager.lookup(ThreadManager.class);
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	public void initialize() throws Exception {

		try {
						
			if(!UtilFile.fileExistInTemp(personal_dictionary)){
				UtilFile.createTemp(personal_dictionary);
			}
			
			
			//System.err.println("carica questo dizionario: "+i18n.getLocale().getLanguage());
			
			// To be improved:  CARICAMENTO DEL DIZIONARIO IN BASE ALLA LINGUA DELL'INTERFACCIA 
			
			if(i18n.getLocale().getLanguage().toLowerCase().contains("it"))
				dictFile = IT_dictFile;
			if(i18n.getLocale().getLanguage().toLowerCase().contains("en"))
			    dictFile = EN_dictFile;
			
			dictionary = new OpenOfficeSpellDictionary(getClass().getResourceAsStream(dictFile),UtilFile.getFileFromTemp(personal_dictionary));			
			spellCheck = new SpellChecker(dictionary);
			spellCheck.setCaseSensitive(false);
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		
	}

	public SpellCheckWord[] spellCheck(String text) {
		if (text != null) {
			invalidWordsVect = new Vector();
			spellCheck.check(new CharSequenceWordFinder(text), this);
			
			SpellCheckWord[] ret = new SpellCheckWord[invalidWordsVect.size()];
			invalidWordsVect.copyInto(ret);
			return ret;
		}
		return null;
	}

	public void spellingError(SpellCheckEvent event) {
		
		int startOffset = event.getCurrentWord().getStart();//.getWordContextPosition();
		int endOffset = startOffset + event.getCurrentWord().length();
				
		if (event.getCurrentWord().getText().trim().length() > 1)
			invalidWordsVect.add(new SpellCheckWordImpl(event.getCurrentWord().getText(), startOffset, endOffset));
	}

	public String[] getSuggestions(String word) {
		if (word != null) {
			List suggestions = dictionary.getSuggestions(word, 5);
			if (suggestions.size() > 0) {
				String[] ret = new String[suggestions.size()];
				int i = 0;
				for (Iterator suggestedWord = suggestions.iterator(); suggestedWord.hasNext();)
					ret[i++] = (String)suggestedWord.next();
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
		
		if(!dictionary.isCorrect(word)){
			try
		    {	          
		      dictionary.addWord(word);     
		    }
		    catch (SpellDictionaryException e)
		    {
		      e.printStackTrace();
		    }
		}
	}

	public void removeWord(String word) {
		// TODO removeWord
	}

	public void modifyWord(String oldWord, String newWord) {
		// TODO modifyWord
	}
		
	public boolean isLoad() {
		return true;
	}

	
	
	
	
	
	
	


}
