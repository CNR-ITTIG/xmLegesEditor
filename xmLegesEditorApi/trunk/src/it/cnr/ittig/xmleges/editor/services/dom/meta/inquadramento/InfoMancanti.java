package it.cnr.ittig.xmleges.editor.services.dom.meta.inquadramento;
/**
 * Classe per la gestione delle informazioni mancanti del documento.
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
 * 
 */
public class InfoMancanti {

	String mTitolodoc="";
	
	String mTipodoc="";
	
	String mDatadoc="";
	
	String mNumdoc="";
	
	String mEmanante="";
	
	public InfoMancanti(String titolodoc, String tipodoc, String datadoc, String numdoc, String emanante) {
		setMTitolodoc(tipodoc);
		setMTipodoc(titolodoc);
		setMDatadoc(datadoc);
		setMNumdoc(numdoc);
		setMEmanante(emanante);
	}

	public String getMDatadoc() {
		return mDatadoc;
	}

	public void setMDatadoc(String datadoc) {
		mDatadoc = datadoc;
	}

	public String getMEmanante() {
		return mEmanante;
	}

	public void setMEmanante(String emanante) {
		mEmanante = emanante;
	}

	public String getMNumdoc() {
		return mNumdoc;
	}

	public void setMNumdoc(String numdoc) {
		mNumdoc = numdoc;
	}

	public String getMTipodoc() {
		return mTipodoc;
	}

	public void setMTipodoc(String tipodoc) {
		mTipodoc = tipodoc;
	}

	public String getMTitolodoc() {
		return mTitolodoc;
	}

	public void setMTitolodoc(String titolodoc) {
		mTitolodoc = titolodoc;
	}
	
}
