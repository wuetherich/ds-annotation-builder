package com.wuetherich.osgi.ds.annotations.internal.componentdescription;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.DsAnnotationVersion;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.impl.SCR_1_1_ComponentDescription;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.impl.SCR_1_2_ComponentDescription;
import com.wuetherich.osgi.ds.annotations.internal.prefs.DsAnnotationsPreferences;

public class ComponentDescriptionFactory {

  /**
   * <p>
   * </p>
   * 
   * @param typeDeclaration
   * @return
   */
  public static IComponentDescription createComponentDescription(TypeDeclaration typeDeclaration, IProject project) {

    // get the ds version
    String dsVersionString = DsAnnotationsPreferences.get(project, Constants.PREF_DS_VERSION,
        DsAnnotationVersion.V_1_2.name());
    DsAnnotationVersion dsVersion = DsAnnotationVersion.valueOf(dsVersionString);

    //
    switch (dsVersion) {
    case V_1_1: {
      return new SCR_1_1_ComponentDescription(typeDeclaration);
    }
    case V_1_2: {
      return new SCR_1_2_ComponentDescription(typeDeclaration);
    }
    default: {
      // TODO
      throw new RuntimeException("Unsupported version");
    }
    }
  }
}
