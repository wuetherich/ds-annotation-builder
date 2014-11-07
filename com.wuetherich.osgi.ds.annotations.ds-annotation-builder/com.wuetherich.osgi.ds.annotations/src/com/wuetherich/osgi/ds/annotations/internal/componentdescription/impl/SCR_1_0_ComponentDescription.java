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
package com.wuetherich.osgi.ds.annotations.internal.componentdescription.impl;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.Assert;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.xmlns.scr.v1_0.ObjectFactory;
import org.osgi.xmlns.scr.v1_0.Tcomponent;
import org.osgi.xmlns.scr.v1_0.Timplementation;
import org.osgi.xmlns.scr.v1_0.TjavaTypes;
import org.osgi.xmlns.scr.v1_0.Tpolicy;
import org.osgi.xmlns.scr.v1_0.Tproperties;
import org.osgi.xmlns.scr.v1_0.Tproperty;
import org.osgi.xmlns.scr.v1_0.Tprovide;
import org.osgi.xmlns.scr.v1_0.Treference;
import org.osgi.xmlns.scr.v1_0.Tservice;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationException;
import com.wuetherich.osgi.ds.annotations.internal.builder.AbstractDsAnnotationAstVisitor;
import com.wuetherich.osgi.ds.annotations.internal.builder.ComponentProperty;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.ITypeAccessor;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class SCR_1_0_ComponentDescription extends AbstractComponentDescription {

  /** - */
  private Tcomponent  _tcomponent;

  /** - */
  private JAXBContext _jaxbContext;

  /**
   * <p>
   * Creates a new instance of type {@link SCR_1_0_ComponentDescription extends AbstractComponentDescription}.
   * </p>
   * 
   * @param typeAccessor
   */
  public SCR_1_0_ComponentDescription(ITypeAccessor typeAccessor) {
    super(typeAccessor);

    //
    try {

      //
      _jaxbContext = JAXBContext.newInstance(Tcomponent.class, Timplementation.class, TjavaTypes.class, Tpolicy.class,
          Tproperties.class, Tproperty.class, Treference.class, Tservice.class);

    } catch (JAXBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // create the component...
    _tcomponent = new Tcomponent();

    // ...and set the defaults (name, implementation class, service)
    _tcomponent.setName(getImplementationClassName());

    Timplementation timplementation = new Timplementation();
    timplementation.setClazz(getImplementationClassName());
    _tcomponent.setImplementation(timplementation);

    // set the default services
    List<String> stypes = getTypeDeclarationReader().getAllDirectlyImplementedSuperInterfaces();
    setService(stypes.toArray(new String[0]));
  }

  @Override
  public void onSetName(String value) {
    _tcomponent.setName(value);
  }

  @Override
  public String getName() {
    return _tcomponent.getName();
  }

  @Override
  public String getActivate() {
    return null;
  }

  @Override
  public void onSetActivate(String methodName) {
    throw new DsAnnotationException(Messages.ComponentDescription_UNSUPPORTED_ANNOTATION_ACTIVATE);
  }

  @Override
  public String getDeactivate() {
    return null;
  }

  @Override
  public void onSetDeactivate(String methodName) {
    throw new DsAnnotationException(Messages.ComponentDescription_UNSUPPORTED_ANNOTATION_DEACTIVATE);
  }

  @Override
  public void onSetModified(String methodName) {
    throw new DsAnnotationException(Messages.ComponentDescription_UNSUPPORTED_ANNOTATION_MODIFIED);
  }

  @Override
  public void onSetImmediate(Boolean value) {
    _tcomponent.setImmediate(value);
  }

  @Override
  public void onSetEnabled(Boolean value) {
    _tcomponent.setEnabled(value);
  }

  @Override
  public void onSetFactory(String value) {
    _tcomponent.setFactory(value);
  }

  @Override
  public void onSetServiceFactory(Boolean value) {

    //
    if (_tcomponent.getService() == null) {
      _tcomponent.setService(new Tservice());
    }

    //
    _tcomponent.getService().setServicefactory(value);
  }

  @Override
  public void onSetConfigurationPolicy(String lowerCase) {
    throw new DsAnnotationException(Messages.ComponentDescription_UNSUPPORTED_ATTRIBUTE_CONFIGURATIONPOLICY);
  }

  @Override
  public void onSetConfigurationPid(String configurationPid) {
    throw new DsAnnotationException(String.format(Messages.ComponentDescription_UNSUPPORTED_ATTRIBUTE_CONFIGURATIONPID,
        "1.0")); //$NON-NLS-1$
  }

  @Override
  public void onAddProperties(String value) {
    Tproperties tproperties = new Tproperties();
    tproperties.setEntry(value);
    _tcomponent.getPropertyOrProperties().add(tproperties);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onAddProperty(Map<String, List<ComponentProperty>> properties) {

    //
    for (String name : properties.keySet()) {

      Tproperty tproperty = new Tproperty();
      _tcomponent.getPropertyOrProperties().add(tproperty);

      List<ComponentProperty> componentProperties = properties.get(name);

      ComponentProperty componentProperty = componentProperties.get(0);
      tproperty.setPropertyName(componentProperty.getName());
      if (componentProperty.getType() != null) {
        try {
          tproperty.setPropertyType(TjavaTypes.fromValue(componentProperty.getType()));
        } catch (Exception e) {
          throw new DsAnnotationException(String.format(Messages.ComponentDescription_INVALID_PROPERTY_TYPE,
              componentProperty.getType()));
        }
      }

      if (componentProperties.size() == 1) {
        tproperty.setPropertyValue(componentProperty.getValue());
      } else {
        StringBuilder stringBuilder = new StringBuilder(System.getProperty("line.separator")); //$NON-NLS-1$
        for (ComponentProperty prop : componentProperties) {
          stringBuilder.append(prop.getValue());
          stringBuilder.append(System.getProperty("line.separator")); //$NON-NLS-1$
        }
        tproperty.setValue(stringBuilder.toString());
      }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @param services
   */
  public void onSetService(String[] services) {

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
        Tprovide tprovide = new Tprovide();
        tprovide.setInterface(service);
        _tcomponent.getService().getProvide().add(tprovide);
      }
    }
  }

  @Override
  public String toXml() {

    try {

      // create the marshaller
      Marshaller marshaller = _jaxbContext.createMarshaller();

      // set formatted output
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      if (getAssociatedSourceFile() != null) {
        try {
          marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders", //$NON-NLS-1$
              String.format(Constants.DS_ANNOTATION_BUILDER_GENERATED_COMMENT, getAssociatedSourceFile()));
        } catch (PropertyException ex) {
          marshaller.setProperty("com.sun.xml.bind.xmlHeaders", //$NON-NLS-1$
              String.format(Constants.DS_ANNOTATION_BUILDER_GENERATED_COMMENT, getAssociatedSourceFile()));
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

    Unmarshaller unmarshaller = _jaxbContext.createUnmarshaller();

    //
    @SuppressWarnings("unchecked")
    JAXBElement<Tcomponent> jaxbElement = (JAXBElement<Tcomponent>) unmarshaller.unmarshal(inputStream);
    Tcomponent tcomponent = jaxbElement.getValue();

    return equals(tcomponent, _tcomponent);
  }

  /**
   * <p>
   * </p>
   * 
   * @param comp1
   * @param comp1
   * @return
   */
  public static boolean equals(Tcomponent comp1, Tcomponent comp2) {

    //
    if (!comp1.equals(comp2)) {
      return false;
    }

    //
    return true;
  }

  public void onAddReference(String service, String bind, String name, String cardinality, String policy,
      String policyOption, String unbind, String updated, String target) {

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
      if (name == null || name.isEmpty()) {
        throw new DsAnnotationException(String.format(Messages.ComponentDescription_INVALID_REFERENCE_NAME,
            reference.getName()));
      }
      reference.setName(name);
    } else {
      name = bind;
      if (name.startsWith("add")) { //$NON-NLS-1$
        name = name.substring("add".length()); //$NON-NLS-1$
      } else if (name.startsWith("set")) { //$NON-NLS-1$
        name = name.substring("set".length()); //$NON-NLS-1$
      } else if (name.startsWith("bind")) { //$NON-NLS-1$
        name = name.substring("bind".length()); //$NON-NLS-1$
      }
      if (name == null || name.isEmpty()) {
        throw new DsAnnotationException(String.format(Messages.ComponentDescription_INVALID_DERIVED_REFERENCE_NAME,
            reference.getName(), bind));
      }
      reference.setName(name);
    }

    // [https://github.com/wuetherich/ds-annotation-builder/issues/21]
    // check if reference name is unique
    for (Treference treference : _tcomponent.getReference()) {
      if (treference.getName().equals(reference.getName())) {
        throw new DsAnnotationException(String.format(Messages.ComponentDescription_REFERENCE_NAME_NOT_UNIQUE,
            reference.getName()));
      }
    }

    // step 4: set the name of the unbind method
    if (isNotEmpty(unbind)) {
      if ("-".equals(unbind)) { //$NON-NLS-1$
        reference.setUnbind(null);
      } else {

        //
        if (!getTypeDeclarationReader().checkMethodExists(unbind)) {
          throw new DsAnnotationException(String.format(Messages.ComponentDescription_NON_EXISTING_UNBIND_METHOD,
              unbind));
        }
        getTypeDeclarationReader().assertNoDsAnnotation(unbind);

        //
        reference.setUnbind(unbind);
      }
    } else {

      //
      String computedUnbindMethodName = AbstractDsAnnotationAstVisitor.computeUnbindMethodName(bind);

      // osgi.cmpn-5.0.0.pdf, 112.13.7.6, p. 322
      // The unbind method is only set if the component type contains a method with the derived name.
      if (getTypeDeclarationReader().checkMethodExists(computedUnbindMethodName)) {
        getTypeDeclarationReader().assertNoDsAnnotation(computedUnbindMethodName);
        reference.setUnbind(computedUnbindMethodName);
      }
    }

    // step 5: set the name of the updated method
    if (isNotEmpty(updated)) {
      throw new DsAnnotationException(String.format(Messages.ComponentDescription_UNSUPPORTED_ATTRIBUTE_UPDATED, "1.0")); //$NON-NLS-1$
    }

    // step 6: set the filter
    if (isNotEmpty(target)) {
      try {
        FrameworkUtil.createFilter(target);
        reference.setTarget(target);
      } catch (InvalidSyntaxException e) {
        throw new DsAnnotationException(String.format(Messages.ComponentDescription_INVALID_FILTER, target),
            FIELD_NAME_TARGET);
      }
    }

    if (isNotEmpty(cardinality)) {
      if ("at_least_one".equalsIgnoreCase(cardinality)) { //$NON-NLS-1$
        reference.setCardinality("1..n"); //$NON-NLS-1$
      } else if ("optional".equalsIgnoreCase(cardinality)) { //$NON-NLS-1$
        reference.setCardinality("0..1"); //$NON-NLS-1$
      } else if ("mandatory".equalsIgnoreCase(cardinality)) { //$NON-NLS-1$
        reference.setCardinality("1..1"); //$NON-NLS-1$
      } else if ("multiple".equalsIgnoreCase(cardinality)) { //$NON-NLS-1$
        reference.setCardinality("0..n"); //$NON-NLS-1$
      }
    }

    if (isNotEmpty(policy)) {
      reference.setPolicy(Tpolicy.fromValue(policy.toLowerCase()));
    }

    if (isNotEmpty(policyOption)) {
      throw new DsAnnotationException(String.format(
          Messages.ComponentDescription_UNSUPPORTED_ATTRIBUTE_CONFIGURATIONPOLICY, "1.0"));
    }

    _tcomponent.getReference().add(reference);
  }
}
