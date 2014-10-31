package com.wuetherich.osgi.ds.annotations.internal.componentdescription.impl;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.osgi.service.component.annotations.Component;

import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationException;

public class TypeDeclarationReader {

  /** - */
  private TypeDeclaration _typeDeclaration;

  /**
   * <p>
   * Creates a new instance of type {@link TypeDeclarationReader}.
   * </p>
   * 
   * @param _typeDeclaration
   */
  public TypeDeclarationReader(TypeDeclaration _typeDeclaration) {
    super();
    this._typeDeclaration = _typeDeclaration;
  }

  public String getImplementationClassName() {
    return _typeDeclaration.resolveBinding().getBinaryName();
  }

  public List<String> getAllDirectlyImplementedSuperInterfaces() {

    //
    List<String> result = new LinkedList<String>();

    for (Object type : _typeDeclaration.superInterfaceTypes()) {
      result.add(((Type) type).resolveBinding().getBinaryName());
    }

    //
    return result;
  }

  public boolean isInstanceOf(String service) {
    return isInstanceOf(service, _typeDeclaration.resolveBinding());
  }

  public boolean isInstanceOf(String service, ITypeBinding typeBinding) {

    //
    if (typeBinding == null) {
      return false;
    }

    //
    if (service.equals(typeBinding.getBinaryName())) {
      return true;
    }

    //
    if (isInstanceOf(service, typeBinding.getSuperclass())) {
      return true;
    }

    //
    for (ITypeBinding iface : typeBinding.getInterfaces()) {
      if (isInstanceOf(service, iface)) {
        return true;
      }
    }

    //
    return false;
  }

  public boolean checkMethodExists(String computedUnbindMethodName) {

    //
    for (MethodDeclaration methodDeclaration : _typeDeclaration.getMethods()) {
      if (methodDeclaration.getName().getFullyQualifiedName().equals(computedUnbindMethodName)) {
        return true;
      }
    }

    return false;
  }

  /**
   * <p>
   * </p>
   * 
   * @param methodName
   */
  public void assertNoDsAnnotation(String methodName) {

    //
    for (MethodDeclaration methodDeclaration : _typeDeclaration.getMethods()) {
      if (methodDeclaration.getName().getFullyQualifiedName().equals(methodName)) {

        for (Object modifier : methodDeclaration.modifiers()) {
          if (modifier instanceof MarkerAnnotation) {
            if (isDsAnnotation((MarkerAnnotation) modifier)) {
              throw new DsAnnotationException(String.format(
                  "Method '%s' must not be annotated with the DS annotation '@%s'.", methodName,
                  ((MarkerAnnotation) modifier).getTypeName()));
            }
          }
        }
      }
    }
  }

  /**
   * <p>
   * Helper method. Returns <code>true</code> if the specified annotation is a DS annotation, <code>false</code>
   * otherwise.
   * </p>
   * 
   * @return <code>true</code> if the specified annotation is a DS annotation, <code>false</code> otherwise.
   */
  public static boolean isDsAnnotation(Annotation annotation) {

    //
    ITypeBinding typeBinding = annotation.resolveTypeBinding();

    //
    if (typeBinding != null) {
      return Component.class.getPackage().getName().equals(annotation.resolveTypeBinding().getPackage().getName());
    } else {
      return false;
    }
  }
}
