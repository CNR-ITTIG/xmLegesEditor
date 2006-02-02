package it.cnr.ittig.xmleges.core.blocks.exec;

import java.util.EventObject;

/**
 * Evento di ThreadedStreamReader.
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
 * @author <a href="mailto:mirco.taddei@gmail.com">Mirco Taddei</a>
 * @version 1.0
 */
public class ThreadedStreamReaderEvent extends EventObject {
	/** Indica che l'evento proviene dallo standard input */
	public final static int TYPE_STDIN = 1;

	/** Indica che l'evento proviene dallo standard output */
	public final static int TYPE_STDOUT = 2;

	/** Indica che l'evento proviene dallo standard error */
	public final static int TYPE_STDERR = 3;

	/** Percentuale */
	int perc;

	/** Testo */
	String text;

	/** Tipo (deve essere uno tra TYPE_STDIN, TYPE_STDOUT,TYPE_STDERR) */
	int type;

	/**
	 * Costruttore di <code>ThreadedStreamReaderEvent</code>.
	 * 
	 * @param src sorgente
	 * @param perc percentuale
	 * @param text testo
	 * @param type tipo (deve essere uno tra TYPE_STDIN, TYPE_STDOUT,TYPE_STDERR)
	 */
	public ThreadedStreamReaderEvent(Object src, int perc, String text, int type) {
		super(src);
		this.perc = perc;
		this.text = text;
		this.type = type;
	}

	/**
	 * Restituisce la percentuale.
	 * 
	 * @return percentuale
	 */
	public int getPerc() {
		return this.perc;
	}

	/**
	 * Restituisce il testo.
	 * 
	 * @return testo
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * Restituisce il tipo (valore uguale a uno tra TYPE_STDIN, TYPE_STDOUT,TYPE_STDERR)
	 * 
	 * @return tipo
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Indica se il messaggio proviene dallo standard input.
	 * 
	 * @return <code>true</code> se proviene dallo standard input
	 */
	public boolean isStdin() {
		return type == TYPE_STDIN;
	}

	/**
	 * Indica se il messaggio proviene dallo standard output.
	 * 
	 * @return <code>true</code> se proviene dallo standard output
	 */
	public boolean isStdout() {
		return type == TYPE_STDOUT;
	}

	/**
	 * Indica se il messaggio proviene dallo standard error.
	 * 
	 * @return <code>true</code> se proviene dallo standard error
	 */
	public boolean isStderr() {
		return type == TYPE_STDERR;
	}
}
