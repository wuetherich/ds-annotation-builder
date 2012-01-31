package com.wuetherich.osgi.ds.annotations;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.runtime.Assert;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

import com.wuetherich.osgi.ds.annotations.xml.ObjectFactory;
import com.wuetherich.osgi.ds.annotations.xml.Tcomponent;
import com.wuetherich.osgi.ds.annotations.xml.TconfigurationPolicy;
import com.wuetherich.osgi.ds.annotations.xml.Timplementation;
import com.wuetherich.osgi.ds.annotations.xml.TjavaTypes;
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

  private static final String       REFERENCE_TARGET  = "target";

  private static final String       COMPONENT_SERVICE = "service";

  private static final String       NO_SUPERTYPE_S    = "NO SUPERTYPE '%s'.";

  private static final String       INVALID_FILTER    = "Invalid filter '%s'.";

  /** - */
  private Tcomponent                _tcomponent;

  /** - */
  private List<DsAnnotationProblem> _problems;

  /**
   * <p>
   * Creates a new instance of type {@link AbstractComponentDescription}.
   * </p>
   */
  public AbstractComponentDescription() {

    //
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
      reference.setCardinality(cardinality);
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

      // the JAXBContext
      JAXBContext jaxbContext = JAXBContext.newInstance(Tcomponent.class, TconfigurationPolicy.class,
          Timplementation.class, TjavaTypes.class, Tpolicy.class, Tproperties.class, Tproperty.class, Treference.class,
          Tservice.class);

      // create the marshaller
      Marshaller marshaller = jaxbContext.createMarshaller();

      // set formatted output
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      // marshaller.setProperty("com.sun.xml.internal.bind.marshaller.namespacePrefixMapper",
      // new MyNameSpacePrefixMapper());

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
   * @param component
   */
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
    return _tcomponent.getImplementation().getClazz();
  }
}
