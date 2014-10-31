package com.wuetherich.osgi.ds.annotations.internal.componentdescription;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.wuetherich.osgi.ds.annotations.DsAnnotationVersion;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.impl.SCR_1_0_ComponentDescription;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.impl.SCR_1_1_ComponentDescription;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.impl.SCR_1_2_ComponentDescription;

public class ComponentDescriptionFactory {

  /**
   * <p>
   * </p>
   * 
   * @param typeDeclaration
   * @return
   */
  public static IComponentDescription createComponentDescription(TypeDeclaration typeDeclaration, IProject project,
      DsAnnotationVersion requestedversion) {

    //
    switch (requestedversion) {
    case V_1_0: {
      return new SCR_1_0_ComponentDescription(typeDeclaration);
    }
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
