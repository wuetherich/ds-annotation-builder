package com.wuetherich.osgi.ds.annotations.internal.preferences;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Control;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class EnabledStateHelper {

  /** - */
  private List<Control> _controls;

  /**
   * <p>
   * Creates a new instance of type {@link EnabledStateHelper}.
   * </p>
   */
  public EnabledStateHelper() {
    _controls = new LinkedList<Control>();
  }

  /**
   * <p>
   * </p>
   * 
   * @param control
   * @return
   */
  public boolean add(Control control) {
    return _controls.add(control);
  }

  /**
   * <p>
   * </p>
   * 
   * @param control
   * @return
   */
  public boolean remove(Control control) {
    return _controls.remove(control);
  }

  /**
   * <p>
   * </p>
   * 
   */
  public void clear() {
    _controls.clear();
  }

  /**
   * <p>
   * </p>
   */
  public void disable() {
    
    //
    for (Control control : _controls) {
      control.setEnabled(false);
    }
  }

  /**
   * <p>
   * </p>
   */
  public void enable() {

    //
    for (Control control : _controls) {
      control.setEnabled(true);
    }
  }
}
