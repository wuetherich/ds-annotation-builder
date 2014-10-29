package com.wuetherich.osgi.ds.annotations.internal.componentdescription;

import java.util.List;
import java.util.Map;

import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationProblem;
import com.wuetherich.osgi.ds.annotations.internal.builder.ComponentProperty;

public interface IComponentDescription {

  boolean hasProblems();

  List<DsAnnotationProblem> getProblems();

  void setActivateMethod(String fullyQualifiedName);

  void setDeactivateMethod(String fullyQualifiedName);

  void setModified(String fullyQualifiedName);

  void addReference(String service, String bind, String name, String cardinality, String policy,
      String policyOption, String unbind, String updated, String target);

  void setName(String value);

  void setEnabled(Boolean value);

  void setImmediate(Boolean value);

  void setFactory(String value);

  void setConfigurationPolicy(String lowerCase);
  
  void setConfigurationPid(String value);

  void addProperty(Map<String, List<ComponentProperty>> properties);

  void addProperties(String keyValue);

  void setService(String[] array);

  void setServiceFactory(Boolean value);

  String toXml();

  String getName();

  String getSourceFile();
}
