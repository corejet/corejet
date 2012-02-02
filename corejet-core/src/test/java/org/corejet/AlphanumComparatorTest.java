package org.corejet;

import junit.framework.Assert;

import org.junit.Test;

public class AlphanumComparatorTest {
	@Test
	public void testJiraFormat(){
		AlphanumComparator comparator = new AlphanumComparator();
		Assert.assertEquals(-1,comparator.compare("COREJET-1", "COREJET-2"));
		Assert.assertEquals(1,comparator.compare("COREJET-2", "COREJET-1"));
	}
	
	@Test
	public void testAlphaFormat(){
		AlphanumComparator comparator = new AlphanumComparator();
		Assert.assertEquals(-1,comparator.compare("AAA", "BBB"));
		Assert.assertEquals(1,comparator.compare("BBB", "AAA"));
	}
	
	@Test
	public void testAlphaDifferentLengthFormat(){
		AlphanumComparator comparator = new AlphanumComparator();
		Assert.assertEquals(-1,comparator.compare("AA", "BBB"));
		Assert.assertEquals(1,comparator.compare("BB", "AAA"));
	}
	
	@Test
	public void testDifferentStringWithNumbersFormat(){
		AlphanumComparator comparator = new AlphanumComparator();
		Assert.assertEquals(-1,comparator.compare("Scenario 2 - tests this", "Scenario: 13 - tests this different"));
		Assert.assertEquals(1,comparator.compare("Scenario: 13 - tests this different","Scenario 2 - tests this"));
	}

}
