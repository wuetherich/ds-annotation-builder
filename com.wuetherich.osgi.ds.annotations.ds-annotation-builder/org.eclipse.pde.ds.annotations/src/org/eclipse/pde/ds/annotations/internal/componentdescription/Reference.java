/*******************************************************************************
 * Copyright (c) 2015 Gerd Wütherich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Gerd Wütherich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.pde.ds.annotations.internal.componentdescription;

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
