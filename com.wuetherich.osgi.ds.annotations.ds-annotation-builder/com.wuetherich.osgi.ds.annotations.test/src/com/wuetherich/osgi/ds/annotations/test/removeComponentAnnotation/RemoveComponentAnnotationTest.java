package com.wuetherich.osgi.ds.annotations.test.removeComponentAnnotation;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.util.concurrent.Callable;

import org.eclipse.core.resources.IFile;
import org.junit.Test;

import com.jayway.awaitility.Awaitility;
import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.test.util.AbstractDsAnnotationsTest;
import com.wuetherich.osgi.ds.annotations.test.util.EclipseProjectUtils;

public class RemoveComponentAnnotationTest extends AbstractDsAnnotationsTest {

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
    // System.out.println("Actual  : " + actual_before);
    // System.out.println("Expected: " + expected_before);
    assertXMLEqual(expected_before, actual_before);

    IFile javaSourcefile = getProject().getFile("src/de/test/Test.java");
    javaSourcefile.setContents(getClass().getResourceAsStream(this.getClass().getSimpleName() + "_2.input"), true,
        false, null);

    //
    assertNoResource(Constants.COMPONENT_DESCRIPTION_FOLDER + "/de.test.Test.xml");
  }

  @Override
  protected SourceFile createSourceFile() {
    return new SourceFile.Default("de/test/Test.java", fromStream(getClass().getResourceAsStream(
        this.getClass().getSimpleName() + "_1.input")));
  }

  /**
   * <p>
   * </p>
   */
  protected void assertNoResource(final String resourcePath) {
    Awaitility.await().until(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return getProject().findMember(resourcePath) == null;
      }
    });
  }

}
