package com.wuetherich.osgi.ds.annotations.internal.builder;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.internal.util.GenericCache;

/**
 * <p>
 * Helper class that reads the existing component descriptions from the component description folder and returns them
 * for further processing.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ComponentDescriptionReader {

  /** - */
  private static Pattern REGEXP_PATTERN = Pattern.compile(Constants.DS_ANNOTATION_BUILDER_GENERATED_COMMENT_REGEXP);

  /**
   * <p>
   * Returns a map that contains a list of pathes to component descriptions for annotated component classes.
   * </p>
   * 
   * @param project
   * @return
   * @throws CoreException
   */
  public static Map<IPath, List<IPath>> loadGeneratedDescriptionsMap(IProject project) {

    //
    Assert.isNotNull(project);

    //
    IFolder folder = project.getFolder(Constants.COMPONENT_DESCRIPTION_FOLDER);
    try {
      if (folder.exists()) {
        folder.refreshLocal(IResource.DEPTH_INFINITE, null);
      }
    } catch (CoreException e) {
      // noop
    }

    //
    GenericCache<IPath, List<IPath>> genericCache = new GenericCache<IPath, List<IPath>>() {
      private static final long serialVersionUID = 1L;

      @Override
      public List<IPath> create(IPath key) {
        return new LinkedList<IPath>();
      }
    };

    try {
      //
      if (folder.exists()) {
        for (IResource iResource : folder.members()) {

          if (iResource instanceof IFile && iResource.getName().endsWith(".xml")) {
            String source = extractSource((IFile) iResource);
            if (source != null) {
              genericCache.getOrCreate(new Path(source)).add(iResource.getProjectRelativePath());
            }
          }
        }
      }
    } catch (CoreException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    //
    return genericCache;
  }

  public static List<IPath> getAllComponentDescriptions(IProject project) {

    //
    Assert.isNotNull(project);

    //
    IFolder folder = project.getFolder(Constants.COMPONENT_DESCRIPTION_FOLDER);
    try {
      if (folder.exists()) {
        folder.refreshLocal(IResource.DEPTH_INFINITE, null);
      }
    } catch (CoreException e) {
      // noop
    }

    //
    List<IPath> result = new LinkedList<IPath>();

    //
    try {
      if (folder.exists()) {
        for (IResource iResource : folder.members()) {
          if (iResource instanceof IFile && iResource.getName().endsWith(".xml")
              && isComponentDesciptor((IFile) iResource)) {
            result.add(iResource.getProjectRelativePath());
          }
        }
      }
    } catch (CoreException e) {
      e.printStackTrace();
    }

    //
    return result;
  }

  /**
   * <p>
   * </p>
   * 
   * @param file
   * @return
   * @throws Exception
   */
  public static boolean containsDsAnnotationBuilderComment(IFile file) {
    return extractSource(file) != null;
  }

  /**
   * <p>
   * </p>
   * 
   * @param file
   * @return
   * @throws Exception
   */
  public static String extractSource(IFile file) {

    //
    String result = null;

    try {
      // get input stream
      InputStream inputStream = file.getContents();
      XMLStreamReader xmlStreamReader = null;

      try {

        //
        xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
        //
        while (xmlStreamReader.hasNext() && result == null) {
          if (xmlStreamReader.next() == XMLStreamConstants.COMMENT) {
            String comment = xmlStreamReader.getText();
            Matcher matcher = REGEXP_PATTERN.matcher(comment);
            if (matcher.matches()) {
              result = matcher.group(2);
            }
          }
        }
      } finally {
        try {
          inputStream.close();
          xmlStreamReader.close();
        } catch (Exception e) {
          //
        }
      }
    } catch (Throwable e) {
      //
    }

    //
    return result;
  }

  /**
   * <p>
   * </p>
   * 
   * @param file
   * @return
   */
  public static boolean isComponentDesciptor(IFile file) {

    try {
      // get input stream
      InputStream inputStream = file.getContents();
      XMLStreamReader xmlStreamReader = null;

      try {

        //
        xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
        //
        while (xmlStreamReader.hasNext()) {
          if (xmlStreamReader.next() == XMLStreamConstants.START_ELEMENT) {
            String namespace = xmlStreamReader.getNamespaceURI();
            if (namespace != null && namespace.startsWith("http://www.osgi.org/xmlns/scr/")) {
              return true;
            }
          }
        }
      } finally {
        try {
          inputStream.close();
          xmlStreamReader.close();
        } catch (Exception e) {
          //
        }
      }
    } catch (Throwable e) {
      //
    }

    //
    return false;
  }
}
