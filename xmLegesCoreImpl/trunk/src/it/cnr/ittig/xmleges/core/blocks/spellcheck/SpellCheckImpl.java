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
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

import com.xmlmind.spellcheck.engine.SpellChecker;
import com.xmlmind.spellcheck.engine.SpellException;



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
public class SpellCheckImpl implements SpellCheck, Loggable, Serviceable, Initializable {

	Logger logger;

	PreferenceManager preferenceManager;

	I18n i18n;
	
	private static String dictPath = UtilFile.getTempDirName()+"/it";
	
	private static String dictName = "it.dar";
	
	private SpellChecker checker=null;
	
	private String testo;

	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	public void service(ServiceManager serviceManager) throws ServiceException {
		i18n = (I18n) serviceManager.lookup(I18n.class);
	}

	public void initialize() throws Exception {
		
		// copia dictName in dictPath se non esiste già
		new File(dictPath).mkdir();
	    File dictFile = new File(dictPath + File.separator,dictName);
	    if(!dictFile.exists()){
		   UtilFile.copyFile(getClass().getResourceAsStream(dictName),dictFile);
		   UtilFile.copyFile(getClass().getResourceAsStream("language"),new File(dictPath + File.separator,"language"));
		   UtilFile.copyFile(getClass().getResourceAsStream("default"),new File(dictPath + File.separator,"default"));
	    }
		
		if (null==checker){	
			checker = new SpellChecker(dictPath);					
			try{
			   checker.setPersonalDictionaryPath("dizionarioUtente_%L%.txt");
			   checker.setSelectedLanguage("it");					
			}
			catch(SpellException ex){
			   logger.error(ex.getMessage(),ex);
			}
		    logger.debug("---Vocabolario caricato---"+checker.getSelectedLanguage()+"---");
		 }
	}

	
	public SpellCheckWord[] spellCheck(String text) {	
		if (text != null) {
			this.testo = text;
			return doSearch();
		}
		return null;
	}
	
	
	private SpellCheckWord[] doSearch() {
		try {
			 Vector listaparole = new Vector();		
			 StringTokenizer esamina = new StringTokenizer(testo);
			 int offsetToken = 0;
		     while (esamina.hasMoreTokens()) {	
		    	String parola = esamina.nextToken(); 	
				for (;;) {
					// prepare
					// acquire next fragment from application:
					checker.setInput(parola);
					offsetToken = testo.indexOf(parola);
					try{
					int err = checker.checkNext();
					
					if (err == SpellChecker.ERR_NONE) {
						//end reached: update position in source
						break;
					}
					String failingWord = checker.getWord();
					int replacePos = checker.getPosition();
					int replaceSize = failingWord.length();

					switch(err) {
					case SpellChecker.ERR_DUPLICATE:
						break;
					case SpellChecker.ERR_REPLACE:
						continue;
					case SpellChecker.ERR_WRONG_CAP:
						break;
					case SpellChecker.ERR_PUNCTUATION:
						break;
					case SpellChecker.ERR_UNKNOWN_WORD:
						listaparole.add(new SpellCheckWordImpl(failingWord, offsetToken+replacePos, offsetToken+replacePos+replaceSize));
						break;
					}	
					//get and process user commands:
					break;
					}
					catch(SpellException ex){
						System.err.println(ex.getMessage());
					}	
					System.err.println("SONO NEL FORRE: ");
				 }
		        }
		        SpellCheckWord[] ret = new SpellCheckWord[listaparole.size()];
				listaparole.copyInto(ret);
				return ret;
		       } 
		 	  catch(ClassCastException ex){
		 	  }
 	     return null; 
	}
	

	public String[] getSuggestions(String word) {
		
	 if (word != null) {
	  try {
		
		checker.setInput(word);  
		
		int err = checker.checkNext();
		if (err == SpellChecker.ERR_NONE) return null;
		switch(err) {	
			   case SpellChecker.ERR_UNKNOWN_WORD: 	   //potrei testare anche gli altri casi
		
				   
		       String[] suggerimenti = checker.getSuggestions().toArray();
			    
  		       return suggerimenti;
		     
		} //chiudo lo switch
		
	  } catch(SpellException ex){}
	 }		
     return null;

	}

	public Object getChecker() {
		return  checker;
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
