package com.wuetherich.osgi.ds.annotations.internal.componentdescription;

import java.util.List;

public interface ITypeAccessor {

  String getImplementationClassName();

  List<String> getAllDirectlyImplementedSuperInterfaces();

  boolean isInstanceOf(String service);

  boolean checkMethodExists(String computedUnbindMethodName);

  void assertNoDsAnnotation(String methodName);
  
  String getAssociatedSourceFile();
}
