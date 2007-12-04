package it.cnr.ittig.xmleges.core.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

/**
 * Classe di utilit&agrave; per i file.
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
public class UtilFile {

	final static String tempDir;
	final static String suffixFolder = "_xmlfile/";
	
	static {
		tempDir = "temp";
		new File(tempDir).mkdir();
	}

	/**
	 * Crea un file temporaneo copiandoci il contenuto del file con nome
	 * <code>fileName</code>.
	 * 
	 * @param fileName nome del file
	 * @return file temporaneo
	 * @throws IOException
	 */
	public static File createTemp(String fileName) throws IOException {
		File temp = new File(getTempDirName() + File.separatorChar + fileName);
		if (temp.exists())
			temp.delete();
		if (temp.createNewFile() || temp.canWrite()) {
			return temp;
		} else
			throw new IOException("Cannot create temp file " + fileName);
	}
		
	
	/**
	 * Crea un file temporaneo copiandoci il contenuto del file
	 * <code>file</code>.
	 * 
	 * @param file file da copiare
	 * @return file temporaneo
	 * @throws IOException
	 */
	public static File createTemp(File file) throws IOException {
		String fileTempName = getTempDirName() + File.separatorChar + file.getName();
		File temp = new File(fileTempName);
		temp.deleteOnExit();
		temp.createNewFile();
		// File temp = File.createTempFile("", "tmp");
		// temp.deleteOnExit();
		copyFile(new FileInputStream(file), temp);
		return temp;
	}
	
	public static File createDirInTemp(String dname) {
		
//		String fileTempName = getTempDirName() + File.separatorChar + base;
//		File temp = new File(fileTempName);
//		if(!temp.canWrite()) {
//			return null;
//		}
//		temp.mkdir()
		
		String fileTempName = getTempDirName() + File.separatorChar + dname;
		File tempDir = new File(fileTempName);
		tempDir.mkdir();
		return tempDir;
	}

	/**
	 * Copia il file <code>source</code> in <code>dest</code>.
	 * 
	 * @param source file da copiare
	 * @param dest destinazione
	 * @return <code>true</code> se la copia &egrave; terminata con successo
	 */
	public static boolean copyFile(String source, String dest) {
		try {
			FileInputStream fis = new FileInputStream(source);
			copyFile(fis, dest);
			fis.close();
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	/**
	 * Copia lo stream <code>source</code> in <code>dest</code>.
	 * 
	 * @param source stream da copiare
	 * @param dest destinazione
	 * @return <code>true</code> se la copia &egrave; terminata con successo
	 */
	public static boolean copyFile(InputStream source, String dest) {
		return copyFile(source, new File(dest));
	}

	/**
	 * Copia lo stream <code>source</code> nel file <code>dest</code>.
	 * 
	 * @param source stream da copiare
	 * @param dest destinazione
	 * @return <code>true</code> se la copia &egrave; terminata con successo
	 */
	public static boolean copyFile(InputStream source, File dest) {
		try {
			if (!dest.exists())
				if (!dest.createNewFile())
					return false;
			/*
			 * FileOutputStream fos = new FileOutputStream(dest); for (int c =
			 * source.read(); c != -1; c = source.read()) fos.write(c);
			 * fos.flush(); fos.close();
			 */
			// *
			BufferedInputStream bis = new BufferedInputStream(source, 2048);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest), 2048);
			for (int c = bis.read(); c != -1; c = bis.read())
				bos.write(c);
			bos.flush();
			bos.close();
			bis.close();
			// */
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	public static String getTempDirName() {
		return tempDir;
	}

	/**
	 * Copia <code>fileName</code> nella cartella che contiene gli allegati del file.
	 * Se la cartella non esiste ancora, viene creata.
	 * 
	 * @param docName il nome del file xml corrente
	 * @param fileName il nome del file da allagare
	 * @return <code>fileName</code> se la copia &egrave; terminata con successo
	 */
	public static String copyInFolder(String docName, String fileName) throws IOException {
		
		File vecchio = new File(fileName);
		String folderPath = getFolderPath(docName);
		
		//test che nuovo sia diverso da vecchio!!!!!!!!
		if ((folderPath).substring(0, (folderPath).length()-1).equals(vecchio.getParent()))	
			return vecchio.getName();
		
		new File(folderPath).mkdir();
		if (!vecchio.exists())
			return "";
		File nuovo = new File(folderPath + vecchio.getName());
		
		if (nuovo.exists())
			nuovo.delete();
		if (copyFile(new FileInputStream(vecchio), nuovo))
			return nuovo.getName();
		return "";
	}
	

	/**
	 * Restituisce il nome della cartella che contiene gli allegati del file.
	 * 
	 * @param file il nome del file xml corrente
	 * @return il nome della cartella
	 */
	public static String getFolderPath(String file) {
		String returnPath = "";
		while (file.indexOf(File.separatorChar)!=-1) {
			returnPath = returnPath + file.substring(0, file.indexOf(File.separatorChar))+File.separatorChar;
			file = file.substring(file.indexOf(File.separatorChar)+1, file.length());
		}
		if (file.indexOf(".")!=-1)
			returnPath = returnPath + file.substring(0, file.indexOf("."));
		else
			returnPath = returnPath + file;
		return returnPath+suffixFolder;
	}
	
	
	/**
	 * Verifica la presenza del file <code>fileName</code> nella directory
	 * temporanea di sistema.
	 * 
	 * @param fileName nome del file
	 * @return <code>true</code> se esiste
	 */
	public static boolean fileExistInTemp(String fileName) {
		//return fileExistInTemp(new File(fileName));
		return new File(tempDir + File.separatorChar + fileName).exists();
	}

	/**
	 * Verifica la presenza del file <code>file</code> nella directory
	 * temporanea di sistema.
	 * 
	 * @param file file
	 * @return <code>true</code> se esiste
	 */
	public static boolean fileExistInTemp(File file) {
		return new File(tempDir + File.separatorChar + file.getName()).exists();
	}

	/**
	 * Copia lo stream <code>source</code> nel file di nome <code>dest</code>
	 * nella directory temporanea di sistema.
	 * 
	 * @param source stream da copiare
	 * @param dest nome del file di destinazione
	 * @return <code>true</code> se la copia &egrave; terminata con successo
	 */
	public static boolean copyFileInTemp(InputStream source, String dest) {
		return copyFileInTemp(source, new File(dest));
	}
	
	

	/**
	 * Copia lo stream <code>source</code> nel file <code>dest</code> nella
	 * directory temporanea di sistema.
	 * 
	 * @param source stream da copiare
	 * @param dest destinazione
	 * @return <code>true</code> se la copia &egrave; terminata con successo
	 */
	public static boolean copyFileInTemp(InputStream source, File dest) {
		return copyFile(source, tempDir + File.separatorChar + dest.getName());
	}

	
	/**
	 * Copia lo stream <code>source</code> nel file di nome <code>dest</code>
	 * nella sottodirectory <code>dirName</code> della directory temporanea di sistema.
	 * 
	 * @param source stream da copiare
	 * @param dirName nome della sottodirectory
	 * @param dest nome del file di destinazione
	 * @return <code>true</code> se la copia &egrave; terminata con successo
	 */
	public static boolean copyFileInTempDir(InputStream source, String dirName, String dest) {
		return copyFileInTempDir(source, dirName, new File(dest));
	}
	
	
	/**
	 * Copia lo stream <code>source</code> nel file <code>dest</code> nella
	 * sottodirectory <code>dirName</code> della directory temporanea di sistema.
	 * 
	 * @param source stream da copiare
	 * @param dirName nome della sottodirectory
	 * @param dest destinazione
	 * @return <code>true</code> se la copia &egrave; terminata con successo
	 */
	public static boolean copyFileInTempDir(InputStream source, String dirName, File dest) {
		new File(getTempDirName() + File.separatorChar + dirName).mkdir();
		return copyFile(source, tempDir + File.separatorChar +dirName +File.separatorChar + dest.getName());
	}
	
	
	/**
	 * Restituisce il file <code>fileName</code> se presente nella directory
	 * temporanea di sistema.
	 * 
	 * @param fileName nome del file
	 * @return file presente nella directory di sistema (null se non esiste)
	 */
	public static File getFileFromTemp(String fileName) {
		//return getFileFromTemp(new File(fileName));
		File ret = new File(tempDir + File.separatorChar + fileName);
		return ret.exists() ? ret : null;
	}

	/**
	 * Restituisce il file <code>file</code> se presente nella directory
	 * temporanea di sistema.
	 * 
	 * @param file nome del file
	 * @return file presente nella directory di sistema (null se non esiste)
	 */
	public static File getFileFromTemp(File file) {
		File ret = new File(tempDir + File.separatorChar + file.getName());
		return ret.exists() ? ret : null;
	}

	/**
	 * Converte lo stream di input in una stringa.
	 * 
	 * @param is stream di input
	 * @return stringa contenente tutto il flusso
	 * @throws IOException se non &egrave; possibile leggere dallo stream
	 */
	public static String inputStreamToString(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		BufferedInputStream bis = new BufferedInputStream(is, 2048);
		for (int c = bis.read(); c != -1; c = bis.read())
			baos.write(c);
		bis.close();
		return baos.toString();
	}

	/**
	 * Converte l'InputStream in un array di stringhe i cui elementi sono le
	 * righe del flusso.
	 * 
	 * @param is stream di input
	 * @return array di stringhe
	 * @throws IOException se non &egrave; possibile leggere dallo stream
	 */
	public static String[] inputStreamToStringArray(InputStream is) throws IOException {
		StringTokenizer st = new StringTokenizer(inputStreamToString(is), "\n");
		String[] ret = new String[st.countTokens()];
		for (int i = 0; i < ret.length; i++)
			ret[i] = st.nextToken();
		return ret;
	}

	/**
	 * Legge il file <code>fileName</code> e lo restituisce come
	 * <code>String</code>.
	 * 
	 * @param fileName nome del file da leggere
	 * @return stringa contenente l'intero file
	 * @throws FileNotFoundException se il file non esiste
	 * @throws IOException se non &egrave; possibile leggere il file
	 */
	public static String fileToString(String fileName) throws FileNotFoundException, IOException {
		return fileToString(new File(fileName));
	}

	/**
	 * Legge il file <code>file</code> e lo restituisce come
	 * <code>String</code>.
	 * 
	 * @param file nome del file da leggere
	 * @return stringa contenente l'intero file
	 * @throws FileNotFoundException se il file non esiste
	 * @throws IOException se non &egrave; possibile leggere il file
	 */
	public static String fileToString(File file) throws FileNotFoundException, IOException {
		return inputStreamToString(new FileInputStream(file));
	}

	/**
	 * Legge il contenuto della URL e lo restituisce come <code>String</code>.
	 * 
	 * @param url URL da recuperare
	 * @return stringa contenente il contenute della URL
	 * @throws IOException se non &egrave; possibile accedere alla URL
	 */
	public static String URLToString(URL url) throws IOException {
		return inputStreamToString(url.openStream());
	}

	/**
	 * Scrive la stringa <code>str</code> nel file <code>fileName</code>.
	 * 
	 * @param str stringa da salvare
	 * @param fileName file di destinazione
	 * @throws FileNotFoundException se il file esiste ma non &egrave; possibile
	 *         scriverci
	 * @throws IOException se non &egrave; possibile leggere il file
	 */
	public static void stringToFile(String str, String fileName) throws FileNotFoundException, IOException {
		stringToFile(str, new File(fileName));
	}

	/**
	 * Scrive la stringa <code>str</code> nel file <code>file</code>.
	 * 
	 * @param str stringa da salvare
	 * @param file file di destinazione
	 * @throws FileNotFoundException se il file esiste ma non &egrave; possibile
	 *         scriverci
	 * @throws IOException se non &egrave; possibile leggere il file
	 */
	public static void stringToFile(String str, File file) throws FileNotFoundException, IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(str.getBytes());
		fos.flush();
		fos.close();
	}

	/**
	 * Imposta l'attributo di esecuzione di un file in sistemi UNIX.
	 * 
	 * @param file file del quale impostare l'attributo di esecuzione
	 * @return <code>true</code> se l'impostazione &egrave; avvenuta con
	 *         successo
	 */
	public static boolean setExecPermission(File file) {
		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec("chmod u+x " + file.getAbsolutePath());
			process.waitFor();
			process.destroy();
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Calcola l'MD5 del file specificato.
	 * 
	 * @param file file sul quale calcolare l'MD5
	 * @return MD5 del file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] calculateMD5(File file) throws FileNotFoundException, IOException, NoSuchAlgorithmException {
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		byte[] b = new byte[1];
		MessageDigest md = MessageDigest.getInstance("MD5");
		for (int e = bis.read(b); e != -1; e = bis.read(b))
			md.update(b);
		return md.digest();
	}

	/**
	 * @param dtdAbsolutePath
	 * @return
	 */
	public static File getDTDFile(String dtdAbsolutePath) {
		File ret = null;
		if (dtdAbsolutePath.startsWith(File.separator)||dtdAbsolutePath.indexOf(":")!=-1) { // dtd con percorso
			// specificato nel documento
			ret = new File(dtdAbsolutePath);
			if (!ret.exists()) { // se non ha trovato la dtd associata al documento, la cerca nella temp
				String[] pathChunks = dtdAbsolutePath.split("/");
				ret = getFileFromTemp(pathChunks[pathChunks.length - 1]);
			}
		} else
			ret = getFileFromTemp(dtdAbsolutePath);

		return ret.exists() ? ret : null;
	}

	/**
	 * Verifica se il file <code>file</code> ha estensione <code>ext</code>.
	 * 
	 * @param file file da verificare
	 * @param ext estensione
	 * @param caseSensitive <code>true</code> per considerare differenti le
	 *        lettere maiuscole e minuscole
	 * @return <code>true</code> se <code>file</code> ha estensione
	 *         <code>ext</code>
	 */
	public static boolean hasExtension(File file, String ext, boolean caseSensitive) {
		try {
			String fn = file.getName();
			if (caseSensitive)
				return ext.equals(fn.substring(fn.lastIndexOf('.') + 1));
			else
				return ext.equalsIgnoreCase(fn.substring(fn.lastIndexOf('.') + 1));
		} catch (Exception ex) {
			return false;
		}
	}
	
	
}
