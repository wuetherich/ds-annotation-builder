package com.wuetherich.osgi.ds.annotations.test.componentname;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.InputStream;
import java.util.concurrent.Callable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.junit.Test;

import com.jayway.awaitility.Awaitility;
import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.test.util.AbstractDsAnnotationsTest;
import com.wuetherich.osgi.ds.annotations.test.util.EclipseProjectUtils;

public class ComponentNameTest extends AbstractDsAnnotationsTest {

  @Test
  public void test() throws Exception {

    //
    EclipseProjectUtils.checkFileExists(getProject(), Constants.COMPONENT_DESCRIPTION_FOLDER + "/de.test.Test.xml");

    //
    String expected_before = fromStream(getClass().getResourceAsStream(this.getClass().getSimpleName() + "_Before.xml"));
    //
    String actual_before = fromStream(getProject()
        .getFile(Constants.COMPONENT_DESCRIPTION_FOLDER + "/de.test.Test.xml").getContents());

    //
    assertXMLEqual(expected_before, actual_before);

    IFile javaSourcefile = getProject().getFile("src/de/test/Test.java");
    InputStream sourceInput = getClass().getResourceAsStream(this.getClass().getSimpleName() + ".input");
    javaSourcefile.setContents(sourceInput, true, false, null);

    //
    final String expected_after = fromStream(getClass().getResourceAsStream(
        this.getClass().getSimpleName() + "_After.xml"));

    //
    Awaitility.await().until(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        //
        IFile file = getProject().getFile(Constants.COMPONENT_DESCRIPTION_FOLDER + "/Hurz.xml");

        if (!file.exists()) {
          return false;
        }

        file.refreshLocal(IResource.DEPTH_INFINITE, null);
        String actual_after = fromStream(file.getContents());
        //
        return expected_after.equals(actual_after);
      }
    });
  }

  @Override
  protected SourceFile createSourceFile() {
    return new SourceFile.Default("de/test/Test.java",
        "package de.test; import org.osgi.service.component.annotations.Component; @Component public class Test {}");
  }

}
