package uk.co.deloitte.corejet.selftest;
import org.junit.Test;


public class BTest {

	@Test
	public void test1() throws InterruptedException {
		System.out.println("Hello world B1");
		Thread.sleep(1000L);
	}
	
	@Test
	public void test2() throws InterruptedException {
		System.out.println("Hello world B2");
		Thread.sleep(1000L);
	}
	
	@Test
	public void test3() throws InterruptedException {
		System.out.println("Hello world B3");
		Thread.sleep(1000L);
	}
}
