package com.wuetherich.osgi.ds.annotations.test.issues_26;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.wuetherich.osgi.ds.annotations.internal.builder.ComponentDescription;
import com.wuetherich.osgi.ds.annotations.xml.Tcomponent;

public class Issue26_ComponentDescriptionEqualsTest {

	@Test
	public void test() throws Exception {

		//
		JAXBContext context = ComponentDescription.createJAXBContext();
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		//
		@SuppressWarnings("unchecked")
		JAXBElement<Tcomponent> jaxbElement1 = (JAXBElement<Tcomponent>) unmarshaller
				.unmarshal(new File(
						"src/com/wuetherich/osgi/ds/annotations/test/issues_26/Issue26_ComponentDescriptionEqualsTest.xml"));
		Tcomponent tcomponent1 = jaxbElement1.getValue();

		//
		@SuppressWarnings("unchecked")
		JAXBElement<Tcomponent> jaxbElement2 = (JAXBElement<Tcomponent>) unmarshaller
				.unmarshal(new File(
						"src/com/wuetherich/osgi/ds/annotations/test/issues_26/Issue26_ComponentDescriptionEqualsTest.xml"));
		Tcomponent tcomponent2 = jaxbElement2.getValue();
		
		//
		assertTrue(ComponentDescription.equals(tcomponent1, tcomponent2));
		tcomponent1.setDeactivate("deactivate");
		assertFalse(ComponentDescription.equals(tcomponent1, tcomponent2));
	}
}
