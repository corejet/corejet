package uk.co.deloitte.corejet.selftest;
import org.junit.Test;


public class CTest {

	@Test
	public void test1() throws InterruptedException {
		System.out.println("Hello world C1");
		Thread.sleep(1000L);
	}
	
	@Test
	public void test2() throws InterruptedException {
		System.out.println("Hello world C2");
		Thread.sleep(1000L);
	}
	
	@Test
	public void test3() throws InterruptedException {
		System.out.println("Hello world C3");
		Thread.sleep(1000L);
	}
}
