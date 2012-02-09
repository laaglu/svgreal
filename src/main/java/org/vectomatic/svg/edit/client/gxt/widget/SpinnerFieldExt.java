/**********************************************
 * Copyright (C) 2011 Lukas Laag
 * This file is part of svgreal.
 * 
 * svgreal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * svgreal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with svgreal.  If not, see http://www.gnu.org/licenses/
 **********************************************/
/*
 * Ext GWT 2.2.5 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.vectomatic.svg.edit.client.gxt.widget;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.ClickRepeaterEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.ClickRepeater;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.form.NumberPropertyEditor;
import com.extjs.gxt.ui.client.widget.form.TwinTriggerField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.constants.NumberConstants;
import com.google.gwt.user.client.Element;

/**
 * Extended SpinnerField which provides access to the ClickRepeater
 * object used to control the spinner buttons. This enables registering
 * listeners on the ClickRepeater events, in order to deactivate command
 * generations while the spinner buttons are held pressed (otherwise,
 * dozens of commands would be generated when one clicks the spinner button
 * and keeps it pressed).
 * @author laaglu
 */
public class SpinnerFieldExt extends TwinTriggerField<Number> {

	  /**
	   * SpinnerField messages.
	   */
	  public class SpinnerFieldMessages extends TextFieldMessages {
	    private String maxText;
	    private String minText;
	    private String nanText;
	    private String negativeText = GXT.MESSAGES.numberField_negativeText();

	    /**
	     * Returns the max error text.
	     * 
	     * @return the error text
	     */
	    public String getMaxText() {
	      return maxText;
	    }

	    /**
	     * Returns the minimum error text.
	     * 
	     * @return the minimum error text
	     */
	    public String getMinText() {
	      return minText;
	    }

	    /**
	     * Returns the not a number error text.
	     * 
	     * @return the not a number error text
	     */
	    public String getNanText() {
	      return nanText;
	    }

	    /**
	     * Returns the negative error text.
	     * 
	     * @return the error text
	     */
	    public String getNegativeText() {
	      return negativeText;
	    }

	    /**
	     * Error text to display if the maximum value validation fails (defaults to
	     * "The maximum value for this field is {maxValue}").
	     * 
	     * @param maxText the max error text
	     */
	    public void setMaxText(String maxText) {
	      this.maxText = maxText;
	    }

	    /**
	     * Sets the Error text to display if the minimum value validation fails
	     * (defaults to "The minimum value for this field is {minValue}").
	     * 
	     * @param minText min error text
	     */
	    public void setMinText(String minText) {
	      this.minText = minText;
	    }

	    /**
	     * Sets the error text to display if the value is not a valid number. For
	     * example, this can happen if a valid character like '.' or '-' is left in
	     * the field with no number (defaults to "{value} is not a valid number").
	     * 
	     * @param nanText the not a number text
	     */
	    public void setNanText(String nanText) {
	      this.nanText = nanText;
	    }

	    /**
	     * Sets the negative error text (defaults to 'The value must be greater or
	     * equal to 0').
	     * 
	     * @param negativeText the error text
	     */
	    public void setNegativeText(String negativeText) {
	      this.negativeText = negativeText;
	    }
	  }

	  protected List<Character> allowed;
	  protected NumberConstants constants;
	  protected String decimalSeparator = ".";
	  protected KeyNav<ComponentEvent> keyNav;
	  private boolean allowDecimals = true;
	  private boolean allowNegative = true;
	  private String baseChars = "0123456789";
	  private Number increment = 1d;
	  private int lastKeyCode;
	  private Number maxValue = Double.MAX_VALUE;
	  private Number minValue = Double.NEGATIVE_INFINITY;
	  // begin laaglu
	  private ClickRepeater repeater;
	  private ClickRepeater twinRepeater;
	  // end laaglu

	  /**
	   * Creates a new number field.
	   */
	  public SpinnerFieldExt() {
	    messages = new SpinnerFieldMessages();
	    propertyEditor = new NumberPropertyEditor();
	    constants = LocaleInfo.getCurrentLocale().getNumberConstants();
	    decimalSeparator = constants.decimalSeparator();
	  }

	  /**
	   * Returns true of decimal values are allowed.
	   * 
	   * @return the allow decimal state
	   */
	  public boolean getAllowDecimals() {
	    return allowDecimals;
	  }

	  /**
	   * Returns true if negative values are allowed.
	   * 
	   * @return the allow negative value state
	   */
	  public boolean getAllowNegative() {
	    return allowNegative;
	  }

	  /**
	   * Returns the base characters.
	   * 
	   * @return the base characters
	   */
	  public String getBaseChars() {
	    return baseChars;
	  }

	  /**
	   * Returns the field's number format.
	   * 
	   * @return the number format
	   */
	  public NumberFormat getFormat() {
	    return getPropertyEditor().getFormat();
	  }

	  /**
	   * Sets the increment value.
	   * 
	   * @return the increment
	   */
	  public Number getIncrement() {
	    return increment;
	  }

	  /**
	   * Returns the fields max value.
	   * 
	   * @return the max value
	   */
	  public Number getMaxValue() {
	    return maxValue;
	  }

	  @Override
	  public SpinnerFieldMessages getMessages() {
	    return (SpinnerFieldMessages) messages;
	  }

	  /**
	   * Returns the field's minimum value.
	   * 
	   * @return the min value
	   */
	  public Number getMinValue() {
	    return minValue;
	  }

	  @Override
	  public NumberPropertyEditor getPropertyEditor() {
	    return (NumberPropertyEditor) propertyEditor;
	  }

	  /**
	   * Returns the number property editor number type.
	   * 
	   * @see NumberPropertyEditor#setType(Class)
	   * @return the number type
	   */
	  public Class<?> getPropertyEditorType() {
	    return getPropertyEditor().getType();
	  }

	  /**
	   * Sets whether decimal value are allowed (defaults to true).
	   * 
	   * @param allowDecimals true to allow negative values
	   */
	  public void setAllowDecimals(boolean allowDecimals) {
	    this.allowDecimals = allowDecimals;
	  }

	  /**
	   * Sets whether negative value are allowed.
	   * 
	   * @param allowNegative true to allow negative values
	   */
	  public void setAllowNegative(boolean allowNegative) {
	    this.allowNegative = allowNegative;
	  }

	  /**
	   * Sets the base set of characters to evaluate as valid numbers (defaults to
	   * '0123456789').
	   * 
	   * @param baseChars the base character
	   */
	  public void setBaseChars(String baseChars) {
	    assertPreRender();
	    this.baseChars = baseChars;
	  }

	  /**
	   * Sets the cell's number formatter.
	   * 
	   * @param format the format
	   */
	  public void setFormat(NumberFormat format) {
	    getPropertyEditor().setFormat(format);
	  }

	  /**
	   * Sets the increment that should be used (defaults to 1d).
	   * 
	   * @param increment the increment to set.
	   */
	  public void setIncrement(Number increment) {
	    this.increment = increment;
	  }

	  /**
	   * Sets the field's max allowable value.
	   * 
	   * @param maxValue the max value
	   */
	  public void setMaxValue(Number maxValue) {
	    this.maxValue = maxValue.doubleValue();
	    if (rendered && maxValue.doubleValue() != Double.MAX_VALUE) {
	      getInputEl().dom.setAttribute("aria-valuemax", "" + maxValue);
	    }
	  }

	  /**
	   * Sets the field's minimum allowed value.
	   * 
	   * @param minValue the minimum value
	   */
	  public void setMinValue(Number minValue) {
	    this.minValue = minValue.doubleValue();
	    if (rendered && maxValue.doubleValue() != Double.NEGATIVE_INFINITY) {
	      getInputEl().dom.setAttribute("aria-valuemin", "" + minValue);
	    }
	  }

	  /**
	   * Specifies the number type used when converting a String to a Number
	   * instance (defaults to Double).
	   * 
	   * @param type the number type (Short, Integer, Long, Float, Double).
	   */
	  public void setPropertyEditorType(Class<?> type) {
	    getPropertyEditor().setType(type);
	  }

	  @Override
	  protected Size adjustInputSize() {
	    return new Size(isHideTrigger() ? 0 : trigger.getStyleSize().width, 0);
	  }

	  protected void afterRender() {
	    super.afterRender();
	    addStyleOnOver(trigger.dom, "x-form-spinner-overup");
	    addStyleOnOver(twinTrigger.dom, "x-form-spinner-overdown");
	  }

	  protected void doSpin(boolean up) {
	    if (!readOnly) {
	      Number n = getValue();
	      double d = n == null ? 0d : getValue().doubleValue();
	      if (up) {
	        setValue(Math.max(minValue.doubleValue(), Math.min(d + increment.doubleValue(), maxValue.doubleValue())));
	      } else {
	        setValue(Math.max(
	            minValue.doubleValue(),
	            Math.min(allowNegative ? d - increment.doubleValue() : Math.max(0, d - increment.doubleValue()),
	                maxValue.doubleValue())));
	      }
	    }
	  }

	  @Override
	  protected void onKeyDown(FieldEvent fe) {
	    super.onKeyDown(fe);
	    // must key code in key code as character returned in key press
	    lastKeyCode = getKeyCode(fe.getEvent());
	  }

	  @Override
	  protected void onKeyPress(FieldEvent fe) {
	    super.onKeyPress(fe);
	    char key = getChar(fe.getEvent());

	    if (fe.isSpecialKey(lastKeyCode) || fe.isControlKey()) {
	      return;
	    }

	    if (!allowed.contains(key)) {
	      fe.stopEvent();
	    }
	  }

	  @Override
	  protected void onRender(Element target, int index) {
	    super.onRender(target, index);
	    allowed = new ArrayList<Character>();
	    for (int i = 0; i < baseChars.length(); i++) {
	      allowed.add(baseChars.charAt(i));
	    }

	    if (allowNegative) {
	      allowed.add('-');
	    }
	    if (allowDecimals) {
	      for (int i = 0; i < decimalSeparator.length(); i++) {
	        allowed.add(decimalSeparator.charAt(i));
	      }
	    }

	    Listener<ClickRepeaterEvent> listener = new Listener<ClickRepeaterEvent>() {
	      public void handleEvent(ClickRepeaterEvent be) {
	        if (SpinnerFieldExt.this.isEnabled()) {
	          if (!hasFocus) {
	            focus();
	          }
	          if (be.getType() == Events.OnClick) {
	            if (be.getEl() == trigger) {
	              onTriggerClick(null);
	            } else if (be.getEl() == twinTrigger) {
	              onTwinTriggerClick(null);
	            }
	          } else if (be.getType() == Events.OnMouseDown) {
	            if (be.getEl() == trigger) {
	              trigger.addStyleName("x-form-spinner-clickup");
	            } else if (be.getEl() == twinTrigger) {
	              twinTrigger.addStyleName("x-form-spinner-clickdown");
	            }

	          } else if (be.getType() == Events.OnMouseUp) {
	            if (be.getEl() == trigger) {
	              trigger.removeStyleName("x-form-spinner-clickup");
	            } else if (be.getEl() == twinTrigger) {
	              twinTrigger.removeStyleName("x-form-spinner-clickdown");
	            }
	          }
	        }
	      }
	    };

	    repeater = new ClickRepeater(trigger);
	    repeater.addListener(Events.OnClick, listener);
	    repeater.addListener(Events.OnMouseDown, listener);
	    repeater.addListener(Events.OnMouseUp, listener);
	    addAttachable(repeater);

	    twinRepeater = new ClickRepeater(twinTrigger);
	    twinRepeater.addListener(Events.OnClick, listener);
	    twinRepeater.addListener(Events.OnMouseDown, listener);
	    twinRepeater.addListener(Events.OnMouseUp, listener);
	    addAttachable(twinRepeater);

	    addStyleName("x-spinner-field");
	    trigger.addStyleName("x-form-spinner-up");
	    twinTrigger.addStyleName("x-form-spinner-down");

	    setMaxValue(maxValue);
	    setMinValue(minValue);
	    getInputEl().dom.setAttribute("role", "spinbutton");

	    keyNav = new KeyNav<ComponentEvent>(this) {
	      @Override
	      public void onDown(ComponentEvent ce) {
	        doSpin(false);
	      }

	      @Override
	      public void onUp(ComponentEvent ce) {
	        doSpin(true);
	      }
	    };
	  }

	  protected void onTriggerClick(ComponentEvent ce) {
	    super.onTriggerClick(ce);
	    // only do it from the ClickRepeater, not from onBrowserEvent
	    if (ce == null) {
	      doSpin(true);
	    }
	  }

	  protected void onTwinTriggerClick(ComponentEvent ce) {
	    super.onTwinTriggerClick(ce);
	    // only do it from the ClickRepeater, not from onBrowserEvent
	    if (ce == null) {
	      doSpin(false);
	    }
	  }

	  @Override
	  protected boolean validateValue(String value) {
	    // validator should run after super rules
	    Validator tv = validator;
	    validator = null;
	    if (!super.validateValue(value)) {
	      validator = tv;
	      return false;
	    }
	    validator = tv;
	    if (value.length() < 1) { // if it's blank and textfield didn't flag it then
	      // its valid it's valid
	      return true;
	    }

	    String v = value;

	    Number d = null;
	    try {
	      d = getPropertyEditor().convertStringValue(v);
	    } catch (Exception e) {
	      String error = "";
	      if (getMessages().getNanText() == null) {
	        error = GXT.MESSAGES.numberField_nanText(v);
	      } else {
	        error = Format.substitute(getMessages().getNanText(), v);
	      }
	      markInvalid(error);
	      return false;
	    }
	    if (d.doubleValue() < minValue.doubleValue()) {
	      String error = "";
	      if (getMessages().getMinText() == null) {
	        error = GXT.MESSAGES.numberField_minText(minValue.doubleValue());
	      } else {
	        error = Format.substitute(getMessages().getMinText(), minValue);
	      }
	      markInvalid(error);
	      return false;
	    }

	    if (d.doubleValue() > maxValue.doubleValue()) {
	      String error = "";
	      if (getMessages().getMaxText() == null) {
	        error = GXT.MESSAGES.numberField_maxText(maxValue.doubleValue());
	      } else {
	        error = Format.substitute(getMessages().getMaxText(), maxValue);
	      }
	      markInvalid(error);
	      return false;
	    }

	    if (!allowNegative && d.doubleValue() < 0) {
	      markInvalid(getMessages().getNegativeText());
	      return false;
	    }

	    if (validator != null) {
	      String msg = validator.validate(this, value);
	      if (msg != null) {
	        markInvalid(msg);
	        return false;
	      }
	    }

	    if (GXT.isAriaEnabled()) {
	      getInputEl().dom.setAttribute("aria-valuenow", "" + value);
	    }

	    return true;
	  }

	  // needed due to GWT 2.1 changes
	  private native char getChar(NativeEvent e) /*-{
			return e.which || e.charCode || e.keyCode || 0;
	  }-*/;

	  // needed due to GWT 2.1 changes
	  private native int getKeyCode(NativeEvent e) /*-{
			return e.keyCode || 0;
	  }-*/;

	  // begin laaglu
	  /**
	   * Returns the repeater used to control the trigger
	   */
	  public ClickRepeater getRepeater() {
		  return repeater;
	  }
	  /**
	   * Returns the repeater used to control the twin trigger
	   */
	  public ClickRepeater getTwinRepeater() {
		  return twinRepeater;
	  }
	  // end laaglu
}
