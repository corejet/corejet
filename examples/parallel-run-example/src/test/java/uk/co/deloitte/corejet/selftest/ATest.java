package uk.co.deloitte.corejet.selftest;
import org.junit.Test;

public class ATest {

	@Test
	public void test1() throws InterruptedException {
		System.out.println("Hello world A1");
		Thread.sleep(100L);
	}
	
	@Test
	public void test2() throws InterruptedException {
		System.out.println("Hello world A2");
		Thread.sleep(2000L);
	}
	
	@Test
	public void test3() throws InterruptedException {
		System.out.println("Hello world A3");
		Thread.sleep(300L);
	}
}
