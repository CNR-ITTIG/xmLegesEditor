package it.cnr.ittig.xmleges.editor.services.dom.meta.descrittori;
/**
 * Classe per la gestione delle redazioni del documento.
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


public class Redazione {
	
	String data="";
	
	String nome="";
	
	String url="";
	
	String contributo="";
	
	public Redazione(String data, String nome, String url, String contributo) {
		setData(data);
		setNome(nome);
		setUrl(url);
		setContributo(contributo);
	}

	public String getContributo() {
		return contributo;
	}

	public void setContributo(String contributo) {
		this.contributo = contributo;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public String toString(){
		return nome+"  "+data;//+" url: "+url+" contributo: "+contributo;
	}
	

	

}
