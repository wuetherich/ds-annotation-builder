/*******************************************************************************
 * Copyright (c) 2011-2013 Gerd W&uuml;therich (gerd@gerd-wuetherich.de).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd W&uuml;therich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package com.wuetherich.osgi.ds.annotations.internal.builder;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationException;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationProblem;
import com.wuetherich.osgi.ds.annotations.xml.ObjectFactory;
import com.wuetherich.osgi.ds.annotations.xml.Tcomponent;
import com.wuetherich.osgi.ds.annotations.xml.TconfigurationPolicy;
import com.wuetherich.osgi.ds.annotations.xml.Timplementation;
import com.wuetherich.osgi.ds.annotations.xml.TjavaTypes;
import com.wuetherich.osgi.ds.annotations.xml.Tpolicy;
import com.wuetherich.osgi.ds.annotations.xml.TpolicyOption;
import com.wuetherich.osgi.ds.annotations.xml.Tproperties;
import com.wuetherich.osgi.ds.annotations.xml.Tproperty;
import com.wuetherich.osgi.ds.annotations.xml.Tprovide;
import com.wuetherich.osgi.ds.annotations.xml.Treference;
import com.wuetherich.osgi.ds.annotations.xml.Tservice;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ComponentDescription {

  /** - */
  private static final String       FIELD_NAME_TARGET    = "target";

  /** - */
  private static final String       FIELD_NAME_SERVICE   = "service";

  /** - */
  private static final String       MSG_NO_SUPERTYPE_S   = "NO SUPERTYPE '%s'.";

  /** - */
  private static final String       MSG_INVALID_FILTER_S = "Invalid filter '%s'.";

  /** - */
  private Tcomponent                _tcomponent;

  /** - */
  private List<DsAnnotationProblem> _problems;

  /** - */
  private TypeDeclaration           _typeDeclaration;

  /** - */
  private String                    _sourceFile;

  /**
   * <p>
   * Creates a new instance of type {@link ComponentDescription}.
   * </p>
   * 
   * @param typeDeclaration
   */
  public ComponentDescription(TypeDeclaration typeDeclaration) {
    Assert.isNotNull(typeDeclaration);

    //
    try {
      CompilationUnit compilationUnit = (CompilationUnit) typeDeclaration.getParent();
      _sourceFile = compilationUnit.getTypeRoot().getCorrespondingResource().getProjectRelativePath()
          .toPortableString();
    } catch (JavaModelException e) {
      //
      // TODO: LOG
    }

    this._typeDeclaration = typeDeclaration;
    _tcomponent = new Tcomponent();
    _problems = new LinkedList<DsAnnotationProblem>();
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public boolean hasProblems() {
    return !_problems.isEmpty();
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public List<DsAnnotationProblem> getProblems() {
    return _problems;
  }

  public void setName(String value) {
    _tcomponent.setName(value);
  }

  public void setModifiedMethod(String methodName) {

    // TODO: check exists

    _tcomponent.setModified(methodName);
  }

  public void setDeactivateMethod(String methodName) {

    // TODO: check exists

    _tcomponent.setDeactivate(methodName);
  }

  public void setActivateMethod(String methodName) {
    _tcomponent.setActivate(methodName);
  }

  public void setEnabled(Boolean value) {
    _tcomponent.setEnabled(value);
  }

  public void setImmediate(Boolean value) {
    _tcomponent.setImmediate(value);

  }

  public void setFactory(String value) {
    _tcomponent.setFactory(value);
  }

  public void addProperties(String value) {
    Tproperties tproperties = new Tproperties();
    _tcomponent.getPropertyOrProperties().add(tproperties);
    tproperties.setEntry(value);
  }

  public void addProperty(String keyValue) {

    String[] strings = keyValue.split("=");

    //
    Tproperty tproperty = new Tproperty();
    _tcomponent.getPropertyOrProperties().add(tproperty);
    String[] nameTypePair = strings[0].split(":");
    if (nameTypePair.length > 1) {
      tproperty.setPropertyName(nameTypePair[0]);
      tproperty.setPropertyType(TjavaTypes.fromValue(nameTypePair[1]));
    } else {
      tproperty.setPropertyName(strings[0]);
    }

    //
    tproperty.setPropertyValue(strings[1]);
  }

  public void setService(String[] services) {

    if (services == null || services.length == 0) {

      _tcomponent.setService(null);

    } else {

      //
      if (_tcomponent.getService() == null) {
        _tcomponent.setService(new Tservice());
      }

      //
      _tcomponent.getService().getProvide().clear();

      //
      for (String service : services) {

        Assert.isNotNull(service);
        if (!isInstanceOf(service)) {
          throw new DsAnnotationException(String.format(MSG_NO_SUPERTYPE_S, service, FIELD_NAME_SERVICE));
        }

        Tprovide tprovide = new Tprovide();
        tprovide.setInterface(service);
        _tcomponent.getService().getProvide().add(tprovide);
      }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @param service
   *          the fully qualified name of the service to bind to this reference.
   * @param bind
   *          the name of the bind method
   * @param name
   *          the name of this reference
   * @param cardinality
   *          the cardinality of the reference
   * @param policy
   *          the policy for the reference
   * @param policyOption TODO
   * @param unbind
   *          the name of the unbind method
   * @param target
   *          the target filter for the reference
   */
  public void addReference(String service, String bind, String name, String cardinality, String policy, String policyOption,
      String unbind, String target) {

    Assert.isNotNull(service);
    Assert.isNotNull(bind);

    // create the reference
    Treference reference = new Treference();

    // step 1: set the interface
    reference.setInterface(service);

    // step 2: set the bind method name
    reference.setBind(bind);

    // step 3: set the name of the bind method
    if (isNotEmpty(name)) {
      reference.setName(name);
    } else {
      name = bind;
      if (name.startsWith("add")) {
        name = name.substring("add".length());
      } else if (name.startsWith("set")) {
        name = name.substring("set".length());
      }
      reference.setName(name);
    }

    // step 4: set the name of the unbind method
    if (isNotEmpty(unbind)) {
      if ("-".equals(unbind)) {
        reference.setUnbind(null);
      } else {
        reference.setUnbind(unbind);
      }
    } else {
      if (bind.startsWith("set")) {
        reference.setUnbind("unset" + bind.substring("set".length()));
      } else if (bind.startsWith("add")) {
        reference.setUnbind("remove" + bind.substring("add".length()));
      } else {
        reference.setUnbind("un" + bind);
      }
    }

    // step 5: set the filter
    if (isNotEmpty(target)) {
      try {
        FrameworkUtil.createFilter(target);
        reference.setTarget(target);
      } catch (InvalidSyntaxException e) {
        throw new DsAnnotationException(String.format(MSG_INVALID_FILTER_S, target), FIELD_NAME_TARGET);
      }
    }

    if (isNotEmpty(cardinality)) {
      if ("at_least_one".equalsIgnoreCase(cardinality)) {
        reference.setCardinality("1..n");
      } else if ("optional".equalsIgnoreCase(cardinality)) {
        reference.setCardinality("0..1");
      } else if ("mandatory".equalsIgnoreCase(cardinality)) {
        reference.setCardinality("1..1");
      } else if ("multiple".equalsIgnoreCase(cardinality)) {
        reference.setCardinality("0..n");
      }
    }

    if (isNotEmpty(policy)) {
      reference.setPolicy(Tpolicy.fromValue(policy.toLowerCase()));
    }
    
    if (isNotEmpty(policyOption)) {
      reference.setPolicyOption(TpolicyOption.fromValue(policyOption.toLowerCase()));
    }

    _tcomponent.getReference().add(reference);
  }

  private boolean isNotEmpty(String name) {
    return name != null && name.trim().length() > 0;
  }

  /**
   * <p>
   * </p>
   * 
   * @param xmlProjectDescription
   * @param outputStream
   */
  public String toXml() {

    try {

      JAXBContext jaxbContext = createJAXBContext();

      // create the marshaller
      Marshaller marshaller = jaxbContext.createMarshaller();

      // set formatted output
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      if (_sourceFile != null) {
        try {
          marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
              String.format(Constants.DS_ANNOTATION_BUILDER_GENERATED_COMMENT, _sourceFile));
        } catch (PropertyException ex) {
          marshaller.setProperty("com.sun.xml.bind.xmlHeaders",
              String.format(Constants.DS_ANNOTATION_BUILDER_GENERATED_COMMENT, _sourceFile));
        }
      }
      //
      StringWriter result = new StringWriter();

      //
      marshaller.marshal(new ObjectFactory().createComponent(_tcomponent), result);

      //
      return result.toString();

    } catch (JAXBException e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @throws JAXBException
   */
  public boolean equals(InputStream inputStream) throws JAXBException {

    JAXBContext jaxbContext = createJAXBContext();

    // create the marshaller
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

    //
    @SuppressWarnings("unchecked")
    JAXBElement<Tcomponent> jaxbElement = (JAXBElement<Tcomponent>) unmarshaller.unmarshal(inputStream);
    Tcomponent tcomponent = jaxbElement.getValue();

    //
    return tcomponent.equals(_tcomponent);
  }

  /**
   * <p>
   * </p>
   * 
   * @param component
   */
  public void setComponentDefaults() {

    //
    _tcomponent.setName(getImplementationClassName());

    Timplementation timplementation = new Timplementation();
    timplementation.setClazz(getImplementationClassName());
    _tcomponent.setImplementation(timplementation);

    List<String> stypes = getAllDirectlyImplementedSuperInterfaces();

    setService(stypes.toArray(new String[0]));
  }

  /**
   * <p>
   * </p>
   * 
   * @param lowerCase
   */
  public void setConfigurationPolicy(String lowerCase) {

    TconfigurationPolicy tconfigurationPolicy = TconfigurationPolicy.fromValue(lowerCase);

    _tcomponent.setConfigurationPolicy(tconfigurationPolicy);
  }

  public void setServiceFactory(Boolean value) {

    //
    if (_tcomponent.getService() == null) {
      _tcomponent.setService(new Tservice());
    }

    //
    _tcomponent.getService().setServicefactory(value);
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public String getName() {
    return _tcomponent.getImplementation().getClazz();
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   * @throws JAXBException
   */
  public static JAXBContext createJAXBContext() throws JAXBException {

    // the JAXBContext
    return JAXBContext.newInstance(Tcomponent.class, TconfigurationPolicy.class, Timplementation.class,
        TjavaTypes.class, Tpolicy.class, Tproperties.class, Tproperty.class, Treference.class, Tservice.class);
  }

  /**
   * {@inheritDoc}
   */
  protected String getImplementationClassName() {
    return _typeDeclaration.resolveBinding().getBinaryName();
  }

  /**
   * {@inheritDoc}
   */
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
