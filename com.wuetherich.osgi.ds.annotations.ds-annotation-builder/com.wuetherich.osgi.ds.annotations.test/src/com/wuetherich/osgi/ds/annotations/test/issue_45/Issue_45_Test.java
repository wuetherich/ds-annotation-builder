package com.wuetherich.osgi.ds.annotations.test.issue_45;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.test.util.AbstractDsAnnotationsTest;
import com.wuetherich.osgi.ds.annotations.test.util.EclipseProjectUtils;

public class Issue_45_Test extends AbstractDsAnnotationsTest {

	@Test
	public void test() throws Exception {

		//
		EclipseProjectUtils.checkFileExists(getProject(),
				Constants.COMPONENT_DESCRIPTION_FOLDER + "/de.test.Test.xml");

		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		// parse using builder to get DOM representation of the XML file
		Document document = db.parse(getProject().getFile(
				Constants.COMPONENT_DESCRIPTION_FOLDER + "/de.test.Test.xml")
				.getContents());
		
		Element documentElement = document.getDocumentElement();
		NodeList nodelist = documentElement.getElementsByTagName("property");
		Assert.assertEquals(nodelist.item(0).getAttributes().getNamedItem("name").getNodeValue(), "a");
		Assert.assertEquals(nodelist.item(1).getAttributes().getNamedItem("name").getNodeValue(), "b");
		Assert.assertEquals(nodelist.item(2).getAttributes().getNamedItem("name").getNodeValue(), "c");
		Assert.assertEquals(nodelist.item(3).getAttributes().getNamedItem("name").getNodeValue(), "d");
		Assert.assertEquals(nodelist.item(4).getAttributes().getNamedItem("name").getNodeValue(), "e");
	}

	@Override
	protected SourceFile createSourceFile() {
		return new SourceFile.Default(
				"de/test/Test.java",
				"package de.test;import java.io.Serializable;import org.osgi.service.component.annotations.Component;import org.osgi.service.component.annotations.Reference;@Component(property={\"a=1\",\"b=2\",\"c=3\",\"d=4\",\"e=3\"})public class Test {}");
	}
}
