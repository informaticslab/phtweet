/**
 * 
 */
package org.phiresearchlab.twitter.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 * @author Joel M. Rives
 * Jun 22, 2011
 */
public class TermAssociation implements Comparable<TermAssociation>, IsSerializable {

	private List<TermReport> terms;
	private int count;
	
	public TermAssociation() { }
	
	public void addTerm(TermReport term) {
		getTerms().add(term);
	}
	
	@Override
	public int compareTo(TermAssociation other) {
		return other.count - count;
	}
	
	public boolean contains(TermReport one, TermReport another) {
		if (null == one || null == another)
			return false;
		
		String oneTerm = one.getTerm();
		String anotherTerm = another.getTerm();
		
		boolean foundOne = false;
		boolean foundAnother = false;
		
		for (TermReport report: getTerms()) {
			if (oneTerm.equals(report.getTerm()))
				foundOne = true;
		
			if (anotherTerm.equals(report.getTerm()))
				foundAnother = true;
		}
		
		return foundOne && foundAnother;
	}

	public List<TermReport> getTerms() {
		if (null == terms)
			terms = new ArrayList<TermReport>();
		return terms;
	}

	public void setTerms(List<TermReport> terms) {
		this.terms = terms;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
