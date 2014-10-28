package com.wuetherich.osgi.ds.annotations.internal.componentdescription;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.wuetherich.osgi.ds.annotations.internal.componentdescription.impl.SCR_1_2_ComponentDescription;

public class ComponentDescriptionFactory {

  /**
   * <p>
   * </p>
   * 
   * @param typeDeclaration
   * @return
   */
  public static IComponentDescription createComponentDescription(TypeDeclaration typeDeclaration) {

    //
    SCR_1_2_ComponentDescription componentDescription = new SCR_1_2_ComponentDescription(typeDeclaration);
    return componentDescription;
  }
}
