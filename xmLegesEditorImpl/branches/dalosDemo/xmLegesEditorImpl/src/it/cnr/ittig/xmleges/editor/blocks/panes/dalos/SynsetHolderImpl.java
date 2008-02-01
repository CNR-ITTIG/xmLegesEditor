package it.cnr.ittig.xmleges.editor.blocks.panes.dalos;

import it.cnr.ittig.xmleges.editor.services.dalos.SynsetHolder;
import it.cnr.ittig.xmleges.editor.services.dalos.objects.Synset;

import java.util.Observable;

public class SynsetHolderImpl extends Observable
implements SynsetHolder {

	private Synset selectedSynset = null;
	
	public void setSynset(Synset syn) {
		
		selectedSynset = syn;
		setChanged();
		notifyObservers(selectedSynset);
	}
	
	public Synset getSynset() {
		
		return selectedSynset;
	}
}
