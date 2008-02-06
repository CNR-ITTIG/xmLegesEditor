package it.cnr.ittig.xmleges.core.blocks.pdfconverter;

import it.cnr.ittig.services.manager.Loggable;
import it.cnr.ittig.services.manager.Logger;
import it.cnr.ittig.xmleges.core.services.pdfconverter.PDFConverter;
import it.cnr.ittig.xmleges.core.util.file.UtilFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFText2HTML;
import org.pdfbox.util.PDFTextStripper;

/**
 * <h1>Implementazione del servizio
 * <code>it.cnr.ittig.xmleges.editor.services.pdfconverter.PDFConverter</code>. </h1>
 * <h1>Descrizione</h1>
 * <h1>Configurazione</h1>
 * Nessuna
 * <h1>Dipendenze</h1>
 * Nessuna
 * <h1>I18n</h1>
 * Nessuna
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
 * @author <a href="mailto:agnoloni@ittig.cnr.it">Tommaso Agnoloni</a>
 */
public class PDFConverterImpl implements PDFConverter, Loggable {
	Logger logger;

	// //////////////////////////////////////////////////// LogEnabled Interface
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	
	public File convert(File src){
		
		return convert(src, false);
	}
	
	public File convert(File src, boolean toHtml) {
		
		return convert(src, toHtml, "UTF-8");
	}
	
	// /////////////////////////////////////////////// PDFConverter Interface
	public File convert(File src, boolean toHtml, String encoding) {
		
		String pdfFile = src.getAbsolutePath();		
		String outFileName = UtilFile.getTempDirName() + File.separatorChar + "antipdf.out";
		
		File outputFile = null; 
		
		// FIXME manca la gestione del setting dell'encoding dall'esterno
		
		//Possible encoding values:
        //"ISO-8859-1";
        //"ISO-8859-6"; //arabic
        //"US-ASCII";
        //"UTF-8";
        //"UTF-16";
        //"UTF-16BE";
        //"UTF-16LE";
		     
		Writer output = null;
        PDDocument document = null;
        try
        {           	
            document = PDDocument.load( pdfFile );
            outputFile = new File(outFileName);
            logger.debug(outFileName);
            
            if( encoding != null ){
               output = new OutputStreamWriter(new FileOutputStream( outFileName ), encoding );
            }
            else{
               //use default encoding
               output = new OutputStreamWriter(new FileOutputStream( outFileName ) );
            }
            
            PDFTextStripper stripper = null;
            if(toHtml) {
               stripper = new PDFText2HTML();
            } else { 
               stripper = new PDFTextStripper();
            }
           
            //Default values from ExtractText.java:
            boolean sort = false;
            int startPage = 1;
            int endPage = Integer.MAX_VALUE;            
            stripper.setSortByPosition( sort );
            stripper.setStartPage( startPage );
            stripper.setEndPage( endPage );
            
            //Extract text!
            stripper.writeText( document, output );
            
            return outputFile;
        }
        catch(Exception ex){
        	logger.error(ex.getMessage(),ex);
        	return null;
        }
        finally
        {
            if( output != null ){
            	try{
            		output.close();
            	}catch(IOException ex){
            		logger.error(ex.getMessage(),ex);
            	}
            }
            if( document != null ){
            	try{
            		document.close();
            	}catch(IOException ex){
            		logger.error(ex.getMessage(),ex);
            	}
            }
        }
	}
	
}


