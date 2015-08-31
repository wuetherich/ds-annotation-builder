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
package org.eclipse.pde.ds.annotations.ui.internal.preferences.fwk;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class RadioGroupDialogField extends Composite {

  private String   _label;

  private String[] _names;

  private Object[] _values;

  private Button[] _buttons;

  private int      _layoutType;

  private Object   _selection;

  /**
   * <p>
   * Creates a new instance of type {@link RadioGroupDialogField}.
   * </p>
   * 
   * @param parent
   * @param style
   */
  public RadioGroupDialogField(Composite parent, String label, String[] names, Object[] values, int layoutType) {
    super(parent, SWT.NONE);

    _label = label;
    _names = names;
    _values = values;
    _layoutType = layoutType;

    //
    init();
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public Object getSelection() {
    return _selection;
  }

  /**
   * <p>
   * </p>
   * 
   * @param selection
   */
  public void setSelection(Object selection) {

    //
    Assert.isNotNull(selection);

    //
    for (Button button : _buttons) {
      if (selection.equals(button.getData())) {
        button.setSelection(true);
      }
    }

    //
    _selection = selection;
  }

  /**
   * <p>
   * </p>
   * 
   */
  protected void init() {

    // create the composite
    this.setLayout(new FillLayout(SWT.VERTICAL));

    // create the label
    Label aLabel = new Label(this, SWT.NO);
    aLabel.setText(_label);

    //
    SelectionListener selectionListener = new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        _selection = ((Button) e.getSource()).getData();
      }
    };

    //
    _buttons = new Button[_values.length];
    Composite group1 = new Composite(this, SWT.NO);
    group1.setLayout(new RowLayout(_layoutType));
    for (int i = 0; i < _values.length; i++) {
      _buttons[i] = new Button(group1, SWT.RADIO);
      _buttons[i].setText(_names[i]);
      _buttons[i].setData(_values[i]);
      _buttons[i].addSelectionListener(selectionListener);
    }
  }
}
