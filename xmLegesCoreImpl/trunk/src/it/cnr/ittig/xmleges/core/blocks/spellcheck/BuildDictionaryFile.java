package it.cnr.ittig.xmleges.core.blocks.spellcheck;


import com.swabunga.spell.engine.DoubleMeta; 
import com.swabunga.spell.engine.Transformator; 
import com.swabunga.spell.engine.GenericTransformator; 
 
import java.io.*; 
import java.util.*; 
 
/** 
* Created by damien on June 19th, 2003 
* reads a wordlist 
* creates a file with code*word entries 
* this file still has to be sorted before use with Jazzy's SpellDictionary 
*/ 
public class BuildDictionaryFile { 
private static String wordlist = "dict/french.words"; 
private static String phonet = "dict/fr_phonet.dat"; 
private static String dico = "dict/francais.dico"; 
static String encoding = "ISO-8859-1"; 
 
public static final void main(String[] args) throws Exception { 
 
if (args.length==3) { 
phonet=args[0]; 
wordlist=args[1]; 
dico=args[2]; 
} else if (args.length==2) { 
phonet=null; 
wordlist=args[1]; 
dico=args[2]; 
} else { 
System.out.println("Usage:\n BuildDictionaryFile [phonetfile] [wordfile] [outputfile]\nOr\n BuildDictionaryFile [wordfile] [outputfile]"); 
System.exit(0); 
} 
new BuildDictionaryFile().buildFile(); 
} 
 

// INNER CLASSES 
// --------------------------------------------------------------------------- 
private class CodeAndWord { 
 
public String code,  
word; 
 
public CodeAndWord(String code, String word){ 
this.code=code; 
this.word=word; 
} 
 
} 


private void buildFile() throws Exception { 
int wordCount = 0; 
Transformator t; 
if (phonet == null) 
t = new DoubleMeta(); 
else 
t = new GenericTransformator(new File(phonet)); 
BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(wordlist), encoding)); 
 
File file = new File(dico); 
if (file.exists()) System.out.println("WARNING: collision!"); 
 
List list = new ArrayList(); 
String word; 
while ((word = r.readLine()) != null) { 
String code = t.transform(word); 
if (!"".equals(code)) { 
list.add(new CodeAndWord(code,word)); 
} 
wordCount++; 
} 
r.close(); 
System.out.println("Words: " + wordCount); 
quickSort(list,0,list.size()-2); 
BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding)); 
CodeAndWord cw; 
for (int i=0;i<list.size();i++) { 
cw=(CodeAndWord)list.get(i); 
w.write(cw.code + "*" + cw.word); 
w.newLine(); 
} 
w.close(); 
} 
 
private void quickSort(List l,int a,int b){ 
int k; 
if (a<b) { 
k=partition(l,a,b); 
quickSort(l,a,k-1); 
quickSort(l,k+1,b); 
} 
} 
 
private int partition(List l, int a, int b){ 
CodeAndWord pivot=(CodeAndWord)l.get(a); 
Object tmp; 
int lower=a+1; 
int upper=b; 
//System.out.println("A="+lower+" B="+upper+" Size:"+l.size()); 
do { 
//System.out.println("Lower="+lower+" Upper="+upper); 
while (((CodeAndWord)l.get(lower)).code.compareTo(pivot.code)<=0 && lower<=upper) { 
lower++; 
} 
while (((CodeAndWord)l.get(upper)).code.compareTo(pivot.code)>0 && lower<=upper) { 
upper--; 
} 
if (lower<=upper) { 
tmp=l.get(lower); 
l.set(lower,l.get(upper)); 
l.set(upper,tmp); 
lower++; 
upper--; 
} 
}while (lower<=upper); 
tmp=l.get(a); 
l.set(a,l.get(upper)); 
l.set(upper,tmp); 
 
return upper; 
} 
 

} 





























//package it.cnr.ittig.xmleges.core.blocks.spellcheck;
//
//import com.swabunga.spell.engine.DoubleMeta; 
//import com.swabunga.spell.engine.Transformator; 
//import com.swabunga.spell.engine.GenericTransformator; 
//
//import java.io.*; 
//import java.util.*; 
//
///** 
// * Created by damien on June 19th, 2003 
// * reads a wordlist 
// * creates a sorted file with code*word entries 
// * warning: loads the dictionary into memory for sorting ! 
// * compile ex.: javac -classpath jazzy/dist/lib/jazzy.jar:. -d classes BuildDictionaryFile.java 
// * use ex.: java -Xms64m -Xmx128m -classpath jazzy/dist/lib/jazzy.jar:classes BuildDictionaryFile dict/fr_phonet.dat dict/french.words dict/francais.dico 
// */ 
//public class BuildDictionaryFile { 
//	private static String wordlist; 
//	private static String phonet; 
//	private static String dico; 
//	private static String encoding = "ISO-8859-1"; 
//	
//	public  BuildDictionaryFile(String[] args){ 
//		if (args.length == 3) { 
//			phonet = args[0]; 
//			wordlist = args[1]; 
//			dico = args[2]; 
//		} else if (args.length == 2) { 
//			phonet = null; 
//			wordlist = args[1]; 
//			dico = args[2]; 
//		} else { 
//			System.out.println("Usage:\n BuildDictionaryFile [phonetfile] [wordfile] [outputfile]\nOr\n BuildDictionaryFile [wordfile] [outputfile]"); 
//			System.out.println("Example:\n java -Xms64m -Xmx128m -classpath jazzy.jar:. BuildDictionaryFile fr_phonet.dat french.words francais.dico"); 
//			System.exit(0); 
//		} 
//		try{
//		buildFile();
//		}catch(Exception ex){
//			
//		}
//	} 
//	
//	private void buildFile() throws Exception { 
////		input 
//		int wordCount = 0; 
//		Transformator t; 
//		if (phonet == null) 
//			t = new DoubleMeta(); 
//		else 
//			t = new GenericTransformator(new File(phonet), encoding); 
//		BufferedReader r; 
//		if (encoding == null) 
//			r = new BufferedReader(new FileReader(wordlist)); 
//		else 
//			r = new BufferedReader(new InputStreamReader(new FileInputStream(wordlist), encoding)); 
//		
////		output 
//		File file = new File(dico); 
//		if (file.exists()) System.out.println("WARNING: collision!"); 
//		
//		System.out.println("reading..."); 
//		List list = new ArrayList(); 
//		String word; 
//		while ((word = r.readLine()) != null) { 
//			String code = t.transform(word); 
//			if (!"".equals(code)) 
//				list.add(new CodeAndWord(code,word)); 
//			wordCount++; 
//		} 
//		r.close(); 
//		System.out.println("Words: " + wordCount); 
//		System.out.println("sorting..."); 
//		quickSort(list, 0, list.size()-1); 
//		BufferedWriter w; 
//		if (encoding == null) 
//			w = new BufferedWriter(new FileWriter(file)); 
//		else 
//			w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding)); 
//		CodeAndWord cw; 
//		for (int i=0; i<list.size(); i++) { 
//			cw = (CodeAndWord)list.get(i); 
//			w.write(cw.code + "*" + cw.word); 
//			w.newLine(); 
//		} 
//		w.close(); 
//	} 
//	
//	private void quickSort(List l, int a, int b) { 
//		int k; 
//		if (a < b) { 
//			k = partition(l, a, b); 
//			if (k == b) 
//				k--; 
//			quickSort(l, a, k); 
//			quickSort(l, k+1, b); 
//		} 
//	} 
//	
//	private int partition(List l, int a, int b) { 
//		CodeAndWord pivot = (CodeAndWord)l.get(a); 
//		Object tmp; 
//		while (true) { 
//			while (((CodeAndWord)l.get(b)).compareTo(pivot)>=0 && a<b) 
//				b--; 
//			while (((CodeAndWord)l.get(a)).compareTo(pivot)<0 && a<b) 
//				a++; 
//			if (a < b) { 
//				tmp = l.get(a); 
//				l.set(a, l.get(b)); 
//				l.set(b, tmp); 
//			} else 
//				return(b); 
//		} 
//	} 
//	
////	INNER CLASSES 
////	--------------------------------------------------------------------------- 
//	private class CodeAndWord { 
//		
//		public String code, word; 
//		
//		public CodeAndWord(String code, String word) { 
//			this.code = code; 
//			this.word = word; 
//		} 
//		
//		public int compareTo(CodeAndWord cw) { 
//			int c = code.compareTo(cw.code); 
//			if (c != 0) 
//				return(c); 
//			else 
//				return(word.compareTo(cw.word)); 
//		} 
//	} 
//	
//} 