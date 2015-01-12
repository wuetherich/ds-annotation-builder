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
import org.osgi.xmlns.scr.v1_1.ObjectFactory;
import org.osgi.xmlns.scr.v1_1.Tcomponent;
import org.osgi.xmlns.scr.v1_1.TconfigurationPolicy;
import org.osgi.xmlns.scr.v1_1.Timplementation;
import org.osgi.xmlns.scr.v1_1.TjavaTypes;
import org.osgi.xmlns.scr.v1_1.Tpolicy;
import org.osgi.xmlns.scr.v1_1.Tproperties;
import org.osgi.xmlns.scr.v1_1.Tproperty;
import org.osgi.xmlns.scr.v1_1.Tprovide;
import org.osgi.xmlns.scr.v1_1.Treference;
import org.osgi.xmlns.scr.v1_1.Tservice;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationException;
import com.wuetherich.osgi.ds.annotations.internal.builder.AbstractDsAnnotationAstVisitor;
import com.wuetherich.osgi.ds.annotations.internal.builder.ComponentProperty;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.AbstractTypeAccessor;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class SCR_1_1_ComponentDescription extends AbstractComponentDescription {

  /** - */
  private Tcomponent  _tcomponent;

  /** - */
  private JAXBContext _jaxbContext;

  /**
   * <p>
   * Creates a new instance of type {@link SCR_1_1_ComponentDescription extends AbstractComponentDescription}.
   * </p>
   * 
   * @param typeAccessor
   */
  public SCR_1_1_ComponentDescription(AbstractTypeAccessor typeAccessor) {
    super(typeAccessor);

    //
    try {

      //
      _jaxbContext = JAXBContext.newInstance(Tcomponent.class, TconfigurationPolicy.class, Timplementation.class,
          TjavaTypes.class, Tpolicy.class, Tproperties.class, Tproperty.class, Treference.class, Tservice.class);

    } catch (JAXBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // create the component...
    _tcomponent = new Tcomponent();

    //
    Timplementation timplementation = new Timplementation();
    timplementation.setClazz(typeAccessor.getImplementationClassName());
    _tcomponent.setImplementation(timplementation);
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
  public void onSetActivate(String methodName) {
    _tcomponent.setActivate(methodName);
  }

  @Override
  public void onSetDeactivate(String methodName) {
    _tcomponent.setDeactivate(methodName);
  }

  @Override
  public void onSetModified(String methodName) {
    _tcomponent.setModified(methodName);
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
    if (_tcomponent.getService() == null) {
      _tcomponent.setService(new Tservice());
    }
    _tcomponent.getService().setServicefactory(value);
  }

  @Override
  public void onSetConfigurationPolicy(String lowerCase) {
    TconfigurationPolicy tconfigurationPolicy = TconfigurationPolicy.fromValue(lowerCase);
    _tcomponent.setConfigurationPolicy(tconfigurationPolicy);
  }

  @Override
  public void onSetConfigurationPid(String configurationPid) {
    throw new DsAnnotationException(String.format(Messages.ComponentDescription_UNSUPPORTED_ATTRIBUTE_CONFIGURATIONPID,
        "1.1"));
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
  public void onAddProperty(List<String> orderedProperties, Map<String, List<ComponentProperty>> properties) {

    //
    for (String name : orderedProperties) {

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
    if (comp2.isSetActivate() != comp1.isSetActivate()) {
      return false;
    }

    //
    if (comp2.isSetDeactivate() != comp1.isSetDeactivate()) {
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
    reference.setName(name);

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
        if (!getTypeAccessor().checkMethodExists(unbind)) {
          throw new DsAnnotationException(String.format(Messages.ComponentDescription_NON_EXISTING_UNBIND_METHOD,
              unbind));
        }
        getTypeAccessor().assertNoDsAnnotation(unbind);

        //
        reference.setUnbind(unbind);
      }
    } else {

      //
      String computedUnbindMethodName = AbstractDsAnnotationAstVisitor.computeUnbindMethodName(bind);

      // osgi.cmpn-5.0.0.pdf, 112.13.7.6, p. 322
      // The unbind method is only set if the component type contains a method with the derived name.
      if (getTypeAccessor().checkMethodExists(computedUnbindMethodName)) {
        getTypeAccessor().assertNoDsAnnotation(computedUnbindMethodName);
        reference.setUnbind(computedUnbindMethodName);
      }
    }

    // step 5: set the name of the updated method
    if (isNotEmpty(updated)) {
      throw new DsAnnotationException(String.format(Messages.ComponentDescription_UNSUPPORTED_ATTRIBUTE_UPDATED, "1.1"));
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
      throw new DsAnnotationException(String.format(Messages.ComponentDescription_UNSUPPORTED_ATTRIBUTE_POLICYOPTION,
          "1.1"));
    }

    _tcomponent.getReference().add(reference);
  }
}
