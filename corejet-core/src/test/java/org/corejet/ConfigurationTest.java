package org.corejet;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConfigurationTest {

	@Test
	public void testLoadSimpleConfig() {
		
		assertEquals("assigned.value", Configuration.getProperty("test.property"));
		assertEquals("other.value", Configuration.getProperty("test.second.property"));
	}
	
	@Test(expected=RuntimeException.class)
	public void testExceptionForMissingProperty() {
		
		Configuration.getProperty("missing.property");
	}
	
	@Test
	public void testGetPropertyOrDefault() {
		assertEquals("defaultValue", Configuration.getPropertyOrDefault("missing.property", "defaultValue"));
	}
}
