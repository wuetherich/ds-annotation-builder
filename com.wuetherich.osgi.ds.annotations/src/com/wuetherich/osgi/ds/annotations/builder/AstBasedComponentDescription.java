package com.wuetherich.osgi.ds.annotations.builder;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.wuetherich.osgi.ds.annotations.AbstractComponentDescription;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class AstBasedComponentDescription extends AbstractComponentDescription {

  /** - */
  private TypeDeclaration _typeDeclaration;

  public AstBasedComponentDescription(TypeDeclaration typeDeclaration) {
    Assert.isNotNull(typeDeclaration);
    this._typeDeclaration = typeDeclaration;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getImplementationClassName() {
    return _typeDeclaration.resolveBinding().getBinaryName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected List<String> getAllDirectlyImplementedSuperInterfaces() {

    //
    List<String> result = new LinkedList<String>();

    for (Object type : _typeDeclaration.superInterfaceTypes()) {
      result.add(((Type) type).resolveBinding().getBinaryName());
    }

    //
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isInstanceOf(String service) {
    return isInstanceOf(service, _typeDeclaration.resolveBinding());
  }

  /**
   * <p>
   * </p>
   * 
   * @param service
   * @param typeBinding
   * @return
   */
  protected boolean isInstanceOf(String service, ITypeBinding typeBinding) {

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
}
