package it.cnr.ittig.xmleges.editor.blocks.contents;

import it.cnr.ittig.xmleges.core.services.panes.problems.Problem;

public abstract class ContentsValidatorProblem implements Problem {

	ContentsValidatorImpl cv;

	public ContentsValidatorProblem(ContentsValidatorImpl cv) {
		this.cv = cv;
	}

	abstract public boolean test();

}
