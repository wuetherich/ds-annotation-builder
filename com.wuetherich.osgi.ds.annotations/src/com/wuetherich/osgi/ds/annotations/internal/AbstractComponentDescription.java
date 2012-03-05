/*******************************************************************************
 * Copyright (c) 2012 Gerd Wuetherich (gerd@gerd-wuetherich.de).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Wuetherich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package com.wuetherich.osgi.ds.annotations.internal;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.Assert;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

import com.wuetherich.osgi.ds.annotations.xml.ObjectFactory;
import com.wuetherich.osgi.ds.annotations.xml.Tcomponent;
import com.wuetherich.osgi.ds.annotations.xml.TconfigurationPolicy;
import com.wuetherich.osgi.ds.annotations.xml.Timplementation;
import com.wuetherich.osgi.ds.annotations.xml.Tpolicy;
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
public abstract class AbstractComponentDescription {

  private static final String              REFERENCE_TARGET  = "target";

  private static final String              COMPONENT_SERVICE = "service";

  private static final String              NO_SUPERTYPE_S    = "NO SUPERTYPE '%s'.";

  private static final String              INVALID_FILTER    = "Invalid filter '%s'.";

  /** - */
  private Tcomponent                       _tcomponent;

  /** - */
  private List<DsAnnotationProblem>        _problems;

  private String                           _xmlname;

  private static ThreadLocal<Marshaller>   marshaller;

  private static ThreadLocal<Unmarshaller> unmarshaller;

  static {
    // the JAXBContext can be used by multiple concurrent Threads ...
    final JAXBContext jaxbContext;
    try {
      jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
    } catch (JAXBException e1) {
      // Should never happen!
      throw new AssertionError("Oooooppps!? Creating of the marshaller failed!");
    }
    // .. the marshaller is not! We wrap it here in a thread local to reduce the burdon
    // of creating multiple instances even if running from same thread.
    marshaller = new ThreadLocal<Marshaller>() {
      @Override
      protected Marshaller initialValue() {
        try {
          Marshaller marshaller = jaxbContext.createMarshaller();
          // set formatted output
          marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
          return marshaller;
        } catch (JAXBException e) {
          // Should never happen!
          throw new AssertionError("Oooooppps!? Creating of the marshaller failed!");
        }
      }
    };
    // .. same goes for unmarshaller here...
    unmarshaller = new ThreadLocal<Unmarshaller>() {
      @Override
      protected Unmarshaller initialValue() {
        try {
          return jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
          // Should never happen!
          throw new AssertionError("Oooooppps!? creating of the unmarshaller failed");
        }
      }
    };
  }

  /**
   * <p>
   * Creates a new instance of type {@link AbstractComponentDescription}.
   * </p>
   * 
   * @throws JAXBException
   */
  public AbstractComponentDescription() {
    _tcomponent = new Tcomponent();
    _problems = new LinkedList<DsAnnotationProblem>();
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  protected abstract String getImplementationClassName();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  protected abstract List<String> getAllDirectlyImplementedSuperInterfaces();

  /**
   * <p>
   * </p>
   * 
   * @param service
   * @return
   */
  protected abstract boolean isInstanceOf(String service);

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

  public void setXMLName(String _xmlname) {
    this._xmlname = _xmlname;
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
    tproperty.setPropertyName(strings[0]);
    tproperty.setPropertyValue(strings[1]);
    tproperty.setValue("");
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
          throw new DsAnnotationException(String.format(NO_SUPERTYPE_S, service, COMPONENT_SERVICE));
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
   * @param unbind
   *          the name of the unbind method
   * @param target
   *          the target filter for the reference
   */
  public void addReference(String service, String bind, String name, String cardinality, String policy, String unbind,
      String target) {

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
        throw new DsAnnotationException(String.format(INVALID_FILTER, target), REFERENCE_TARGET);
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

      // marshaller.setProperty("com.sun.xml.internal.bind.marshaller.namespacePrefixMapper",
      // new MyNameSpacePrefixMapper());

      //
      StringWriter result = new StringWriter();

      //
      marshaller.get().marshal(new ObjectFactory().createComponent(_tcomponent), result);

      //
      return result.toString();

    } catch (JAXBException e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * Check if this object is equal to the one in represented by this {@link InputStream}
   * 
   * @throws JAXBException
   */
  public boolean equals(InputStream inputStream) throws JAXBException {
    Object unmarshaledObject = unmarshaller.get().unmarshal(inputStream);
    if (unmarshaledObject instanceof JAXBElement<?>) {
      JAXBElement<?> jaxbElement = (JAXBElement<?>) unmarshaledObject;
      unmarshaledObject = jaxbElement.getValue();
    }
    if (unmarshaledObject instanceof Tcomponent) {
      Tcomponent tcomponent = (Tcomponent) unmarshaledObject;
      boolean equals = tcomponent.equals(_tcomponent);
      if (!equals) {
        System.out.println(tcomponent);
        System.out.println(_tcomponent);
      }
      return equals;
    } else {
      System.err.println("Unmarshalled object is not a Tcomponent but a "
          + (unmarshaledObject != null ? unmarshaledObject.getClass() : "<null>"));
      return false;
    }
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

    if (false) {
      _tcomponent.setEnabled(true);
      _tcomponent.setImmediate(false);
      _tcomponent.setConfigurationPolicy(TconfigurationPolicy.OPTIONAL);
      _tcomponent.setFactory(null);
    }

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
    if (_xmlname != null) {
      return _xmlname;
    }
    return _tcomponent.getImplementation().getClazz();
  }

}
