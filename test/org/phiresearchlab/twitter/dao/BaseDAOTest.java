/**
 * 
 */
package org.phiresearchlab.twitter.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

/**
 * This sets up the environment that is common for all DAO tests.
 * 
 * @author Joel M. Rives
 * May 9, 2011
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={ "/META-INF/test/applicationContext.xml" })
public abstract class BaseDAOTest {

	@BeforeTransaction
	public void doBeforeTransaction() {
		
	}
	
	@Before
	public void setUp() {
		
	}
	
	@After
	public void tearDown() {
		
	}
	
	@AfterTransaction
	public void doAfterTransaction() {
		
	}
	
}
