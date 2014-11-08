package com.wuetherich.osgi.ds.annotations.internal.componentdescription;

public class Reference {

  private String name;

  private String service;

  private String bind;

  private String cardinality;

  private String policy;

  private String policyOption;

  private String unbind;

  private String updated;

  private String target;

  public Reference(String service, String bind, String name, String cardinality, String policy, String policyOption,
    String unbind, String updated, String target) {

    this.name = name;
    this.service = service;
    this.bind = bind;
    this.cardinality = cardinality;
    this.policy = policy;
    this.policyOption = policyOption;
    this.unbind = unbind;
    this.updated = updated;
    this.target = target;
  }

  public String getName() {
    return name;
  }

  public String getService() {
    return service;
  }

  public String getBind() {
    return bind;
  }

  public String getCardinality() {
    return cardinality;
  }

  public String getPolicy() {
    return policy;
  }

  public String getPolicyOption() {
    return policyOption;
  }

  public String getUnbind() {
    return unbind;
  }

  public String getUpdated() {
    return updated;
  }

  public String getTarget() {
    return target;
  }
}
