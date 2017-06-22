/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.eumetsat.usd.gcp.client.slider;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * A widget that allows the user to select a value within a range of possible values using a sliding bar that responds
 * to mouse events.
 * 
 * <h3>Keyboard Events</h3>
 * <p>
 * SliderBar listens for the following key events. Holding down a key will repeat the action until the key is released.
 * <ul class='css'>
 * <li>left arrow - shift left one step</li>
 * <li>right arrow - shift right one step</li>
 * <li>ctrl+left arrow - jump left 10% of the distance</li>
 * <li>ctrl+right arrow - jump right 10% of the distance</li>
 * <li>home - jump to min value</li>
 * <li>end - jump to max value</li>
 * <li>space - jump to middle value</li>
 * </ul>
 * </p>
 * 
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-SliderBar-shell { primary style }</li>
 * <li>.gwt-SliderBar-shell-focused { primary style when focused }</li>
 * <li>.gwt-SliderBar-shell gwt-SliderBar-line { the line that the knob moves along }</li>
 * <li>.gwt-SliderBar-shell gwt-SliderBar-line-sliding { the line that the knob moves along when sliding }</li>
 * <li>.gwt-SliderBar-shell .gwt-SliderBar-knob { the sliding knob }</li>
 * <li>.gwt-SliderBar-shell .gwt-SliderBar-knob-sliding { the sliding knob when sliding }</li>
 * <li>
 * .gwt-SliderBar-shell .gwt-SliderBar-tick { the ticks along the line }</li>
 * <li>.gwt-SliderBar-shell .gwt-SliderBar-label { the text labels along the line }</li>
 * </ul>
 */
public class SliderBar extends FocusPanel implements RequiresResize, HasValue<Double>, HasValueChangeHandlers<Double>
{
    /**
     * A formatter used to format the labels displayed in the widget.
     */
    public interface LabelFormatter
    {
        /**
         * Generate the text to display in each label based on the label's value.
         * 
         * Override this method to change the text displayed within the SliderBar.
         * 
         * @param slider
         *            the Slider bar
         * @param value
         *            the value the label displays
         * @return the text to display for the label
         */
        String formatLabel(SliderBar slider, double value);
    }

    /**
     * A {@link ClientBundle} that provides images for {@link SliderBar}.
     */
    public interface SliderBarImages extends ClientBundle
    {
        /** Unique instance. */
        SliderBarImages INSTANCE = GWT.create(SliderBarImages.class);

        /**
         * An image used for the sliding knob.
         * 
         * @return a prototype of this image
         */
        @Source("slider.gif")
        ImageResource slider();

        /**
         * An image used for the sliding knob.
         * 
         * @return a prototype of this image
         */
        @Source("sliderDisabled.gif")
        ImageResource sliderDisabled();

        /**
         * An image used for the sliding knob while sliding.
         * 
         * @return a prototype of this image
         */
        @Source("sliderSliding.gif")
        ImageResource sliderSliding();

        /**
         * CSS for the Slider Bar element.
         * 
         * @return Css resource.
         */
        @NotStrict
        @Source("SliderBar.css")
        CssResource sliderBarCss();
    }

    /**
     * The timer used to continue to shift the knob as the user holds down one of the left/right arrow keys. Only IE
     * auto-repeats, so we just keep catching the events.
     */
    private class KeyTimer extends Timer
    {
        /**
         * A bit indicating that this is the first run.
         */
        private boolean firstRun = true;

        /**
         * The delay between shifts, which shortens as the user holds down the button.
         */
        private int repeatDelay = 30;

        /**
         * A bit indicating whether we are shifting to a higher or lower value.
         */
        private boolean shiftRight = false;

        /**
         * The number of steps to shift with each press.
         */
        private int multiplier = 1;

        /**
         * This method will be called when a timer fires. Override it to implement the timer's logic.
         */
        @Override
        public void run()
        {
            // Highlight the knob on first run
            if (firstRun)
            {
                firstRun = false;
                startSliding(true, false);
            }

            // Slide the slider bar
            if (shiftRight)
            {
                setCurrentValue(curValue + multiplier * stepSize);
            } else
            {
                setCurrentValue(curValue - multiplier * stepSize);
            }

            // Repeat this timer until cancelled by keyup event
            schedule(repeatDelay);
        }

        /**
         * Schedules a timer to elapse in the future.
         * 
         * @param delayMillis
         *            how long to wait before the timer elapses, in milliseconds
         * @param shiftRight
         *            whether to shift up or not
         * @param multiplier
         *            the number of steps to shift
         */
        public void schedule(final int delayMillis, final boolean shiftRight, final int multiplier)
        {
            this.firstRun = true;
            this.shiftRight = shiftRight;
            this.multiplier = multiplier;
            super.schedule(delayMillis);
        }
    }

    /**
     * The current value.
     */
    private double curValue;

    /**
     * The knob that slides across the line.
     */
    private Image knobImage = new Image();

    /**
     * The timer used to continue to shift the knob if the user holds down a key.
     */
    private KeyTimer keyTimer = new KeyTimer();

    /**
     * The elements used to display labels above the ticks.
     */
    private List<Element> labelElements = new ArrayList<Element>();

    /**
     * The formatter used to generate label text.
     */
    private LabelFormatter labelFormatter;

    /**
     * The line that the knob moves over.
     */
    private Element lineElement;

    /**
     * The offset between the edge of the shell and the line.
     */
    private int lineLeftOffset = 0;

    /**
     * The maximum slider value.
     */
    private double maxValue;

    /**
     * The minimum slider value.
     */
    private double minValue;

    /**
     * The number of labels to show.
     */
    private int numLabels = 0;

    /**
     * The number of tick marks to show.
     */
    private int numTicks = 0;

    /**
     * A bit indicating whether or not we are currently sliding the slider bar due to keyboard events.
     */
    private boolean slidingKeyboard = false;

    /**
     * A bit indicating whether or not we are currently sliding the slider bar due to mouse events.
     */
    private boolean slidingMouse = false;

    /**
     * A bit indicating whether or not the slider is enabled.
     */
    private boolean enabled = true;

    /**
     * The images used with the sliding bar.
     */
    private SliderBarImages images;

    /**
     * The size of the increments between knob positions.
     */
    private double stepSize;

    /**
     * The elements used to display tick marks, which are the vertical lines along the slider bar.
     */
    private List<Element> tickElements = new ArrayList<Element>();

    /**
     * Create a slider bar.
     * 
     * @param minValue
     *            the minimum value in the range
     * @param maxValue
     *            the maximum value in the range
     */
    public SliderBar(final double minValue, final double maxValue)
    {
        this(minValue, maxValue, null);
    }

    /**
     * Create a slider bar for UI binder.
     * 
     * @param minValue
     *            the minimum value in the range
     * @param maxValue
     *            the maximum value in the range
     */
    @UiConstructor
    public SliderBar(final String minValue, final String maxValue)
    {
        this(Double.valueOf(minValue), Double.valueOf(maxValue), null);
    }

    /**
     * Create a slider bar.
     * 
     * @param minValue
     *            the minimum value in the range
     * @param maxValue
     *            the maximum value in the range
     * @param labelFormatter
     *            the label formatter
     */
    public SliderBar(final double minValue, final double maxValue, final LabelFormatter labelFormatter)
    {
        this(minValue, maxValue, labelFormatter, SliderBarImages.INSTANCE);
    }

    /**
     * Create a slider bar.
     * 
     * @param minValue
     *            the minimum value in the range
     * @param maxValue
     *            the maximum value in the range
     * @param labelFormatter
     *            the label formatter
     * @param images
     *            the images to use for the slider
     */
    public SliderBar(final double minValue, final double maxValue, final LabelFormatter labelFormatter,
            final SliderBarImages images)
    {
        super();
        images.sliderBarCss().ensureInjected();
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.images = images;
        setLabelFormatter(labelFormatter);

        // Create the outer shell
        getElement().getStyle().setProperty("position", "relative");
        setStyleName("gwt-SliderBar-shell");

        // Create the line
        lineElement = DOM.createDiv();
        DOM.appendChild(getElement(), lineElement);
        lineElement.getStyle().setProperty("position", "absolute");
        lineElement.setPropertyString("className", "gwt-SliderBar-line");

        // Create the knob
        knobImage.setResource(images.slider());
        Element knobElement = knobImage.getElement();
        DOM.appendChild(getElement(), knobElement);
        knobElement.getStyle().setProperty("position", "absolute");
        knobElement.setPropertyString("className", "gwt-SliderBar-knob");

        sinkEvents(Event.MOUSEEVENTS | Event.KEYEVENTS | Event.FOCUSEVENTS);

        // workaround to render properly when parent Widget does not
        // implement ProvidesResize since DOM doesn't provide element
        // height and width until onModuleLoad() finishes.
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            @Override
            public void execute()
            {
                onResize();
            }
        });
    }

    /**
     * Add value change handler.
     * 
     * @param handler
     *            value change handler to be registered.
     * @return HandlerRegistration registration of the handler.
     */
    public final HandlerRegistration addValueChangeHandler(final ValueChangeHandler<Double> handler)
    {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * Return the current value.
     * 
     * @return the current value
     */
    public final double getCurrentValue()
    {
        return curValue;
    }

    /**
     * Return the label formatter.
     * 
     * @return the label formatter
     */
    public final LabelFormatter getLabelFormatter()
    {
        return labelFormatter;
    }

    /**
     * Return the max value.
     * 
     * @return the max value
     */
    public final double getMaxValue()
    {
        return maxValue;
    }

    /**
     * Return the minimum value.
     * 
     * @return the minimum value
     */
    public final double getMinValue()
    {
        return minValue;
    }

    /**
     * Return the number of labels.
     * 
     * @return the number of labels
     */
    public final int getNumLabels()
    {
        return numLabels;
    }

    /**
     * Return the number of ticks.
     * 
     * @return the number of ticks
     */
    public final int getNumTicks()
    {
        return numTicks;
    }

    /**
     * Return the step size.
     * 
     * @return the step size
     */
    public final double getStepSize()
    {
        return stepSize;
    }

    /**
     * Return the total range between the minimum and maximum values.
     * 
     * @return the total range
     */
    public final double getTotalRange()
    {
        if (minValue > maxValue)
        {
            return 0;
        } else
        {
            return maxValue - minValue;
        }
    }

    /**
     * Get Current Value.
     * 
     * @return current value.
     */
    public final Double getValue()
    {
        return curValue;
    }

    /**
     * @return Gets whether this widget is enabled
     */
    public final boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Listen for events that will move the knob.
     * 
     * @param event
     *            the event that occurred
     */
    @Override
    public final void onBrowserEvent(final Event event)
    {
        super.onBrowserEvent(event);
        if (enabled)
        {
            switch (DOM.eventGetType(event))
            {
            // Unhighlight and cancel keyboard events
                case Event.ONBLUR:
                    keyTimer.cancel();
                    if (slidingMouse)
                    {
                        DOM.releaseCapture(getElement());
                        slidingMouse = false;
                        slideKnob(event);
                        stopSliding(true, true);
                    } else if (slidingKeyboard)
                    {
                        slidingKeyboard = false;
                        stopSliding(true, true);
                    }
                    unhighlight();
                    break;

                // Highlight on focus
                case Event.ONFOCUS:
                    highlight();
                    break;

                // Mousewheel events
                case Event.ONMOUSEWHEEL:
                    int velocityY = event.getMouseWheelVelocityY();
                    event.preventDefault();
                    if (velocityY > 0)
                    {
                        shiftRight(1);
                    } else
                    {
                        shiftLeft(1);
                    }
                    break;

                // Shift left or right on key press
                case Event.ONKEYDOWN:
                    if (!slidingKeyboard)
                    {
                        int multiplier = 1;
                        if (event.getCtrlKey())
                        {
                            multiplier = (int) (getTotalRange() / stepSize / 10);
                        }

                        switch (event.getKeyCode())
                        {
                            case KeyCodes.KEY_HOME:
                                event.preventDefault();
                                setCurrentValue(minValue);
                                break;
                            case KeyCodes.KEY_END:
                                event.preventDefault();
                                setCurrentValue(maxValue);
                                break;
                            case KeyCodes.KEY_LEFT:
                                event.preventDefault();
                                slidingKeyboard = true;
                                startSliding(false, true);
                                shiftLeft(multiplier);
                                keyTimer.schedule(400, false, multiplier);
                                break;
                            case KeyCodes.KEY_RIGHT:
                                event.preventDefault();
                                slidingKeyboard = true;
                                startSliding(false, true);
                                shiftRight(multiplier);
                                keyTimer.schedule(400, true, multiplier);
                                break;
                            case 32:
                                event.preventDefault();
                                setCurrentValue(minValue + getTotalRange() / 2);
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                // Stop shifting on key up
                case Event.ONKEYUP:
                    keyTimer.cancel();
                    if (slidingKeyboard)
                    {
                        slidingKeyboard = false;
                        stopSliding(true, true);
                    }
                    break;

                // Mouse Events
                case Event.ONMOUSEDOWN:
                    setFocus(true);
                    slidingMouse = true;
                    DOM.setCapture(getElement());
                    startSliding(true, true);
                    event.preventDefault();
                    slideKnob(event);
                    break;
                case Event.ONMOUSEUP:
                    if (slidingMouse)
                    {
                        DOM.releaseCapture(getElement());
                        slidingMouse = false;
                        slideKnob(event);
                        stopSliding(true, true);
                    }
                    break;
                case Event.ONMOUSEMOVE:
                    if (slidingMouse)
                    {
                        slideKnob(event);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * This method is called when the dimensions of the parent element change. Subclasses should override this method as
     * needed.
     * 
     * @param width
     *            the new client width of the element
     * @param height
     *            the new client height of the element
     */
    public final void onResize(final int width, final int height)
    {
        // Center the line in the shell
        int lineWidth = lineElement.getOffsetWidth();
        lineLeftOffset = (width / 2) - (lineWidth / 2);
        lineElement.getStyle().setProperty("left", lineLeftOffset + "px");

        // Draw the other components
        drawLabels();
        drawTicks();
        drawKnob();
    }

    /**
     * Redraw the progress bar when something changes the layout.
     */
    public final void redraw()
    {
        if (isAttached())
        {
            int width = getElement().getClientWidth();
            int height = getElement().getClientHeight();
            onResize(width, height);
        }
    }

    /**
     * Set the current value and fire the onValueChange event.
     * 
     * @param curValue
     *            the current value
     */
    public final void setCurrentValue(final double curValue)
    {
        setCurrentValue(curValue, true);
    }

    /**
     * Set the current value and optionally fire the onValueChange event.
     * 
     * @param curValue
     *            the current value
     * @param fireEvent
     *            fire the onValue change event if true
     */
    public final void setCurrentValue(final double curValue, final boolean fireEvent)
    {
        // Confine the value to the range
        this.curValue = Math.max(minValue, Math.min(maxValue, curValue));
        double remainder = (this.curValue - minValue) % stepSize;
        this.curValue -= remainder;

        // Go to next step if more than halfway there
        if ((remainder > (stepSize / 2)) && ((this.curValue + stepSize) <= maxValue))
        {
            this.curValue += stepSize;
        }

        // Redraw the knob
        drawKnob();

        // Fire the ValueChangeEvent
        if (fireEvent)
        {
            ValueChangeEvent.fire(this, this.curValue);
        }
    }

    /**
     * Sets whether this widget is enabled.
     * 
     * @param enabled
     *            true to enable the widget, false to disable it
     */
    public final void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
        if (enabled)
        {
            knobImage.setResource(images.slider());
            lineElement.setPropertyString("className", "gwt-SliderBar-line");
        } else
        {
            knobImage.setResource(images.sliderDisabled());
            lineElement.setPropertyString("className", "gwt-SliderBar-line gwt-SliderBar-line-disabled");
        }
        redraw();
    }

    /**
     * Set the label formatter.
     * 
     * @param labelFormatter
     *            the label formatter
     */
    public final void setLabelFormatter(final LabelFormatter labelFormatter)
    {
        this.labelFormatter = labelFormatter;
    }

    /**
     * Set the max value.
     * 
     * @param maxValue
     *            the current value
     */
    public final void setMaxValue(final double maxValue)
    {
        this.maxValue = maxValue;
        drawLabels();
        resetCurrentValue();
    }

    /**
     * Set the minimum value.
     * 
     * @param minValue
     *            the current value
     */
    public final void setMinValue(final double minValue)
    {
        this.minValue = minValue;
        drawLabels();
        resetCurrentValue();
    }

    /**
     * Set the number of labels to show on the line. Labels indicate the value of the slider at that point. Use this
     * method to enable labels.
     * 
     * If you set the number of labels equal to the total range divided by the step size, you will get a properly
     * aligned "jumping" effect where the knob jumps between labels.
     * 
     * Note that the number of labels displayed will be one more than the number you specify, so specify 1 labels to
     * show labels on either end of the line. In other words, numLabels is really the number of slots between the
     * labels.
     * 
     * setNumLabels(0) will disable labels.
     * 
     * @param numLabels
     *            the number of labels to show
     */
    public final void setNumLabels(final int numLabels)
    {
        this.numLabels = numLabels;
        drawLabels();
    }

    /**
     * Set the number of ticks to show on the line. A tick is a vertical line that represents a division of the overall
     * line. Use this method to enable ticks.
     * 
     * If you set the number of ticks equal to the total range divided by the step size, you will get a properly aligned
     * "jumping" effect where the knob jumps between ticks.
     * 
     * Note that the number of ticks displayed will be one more than the number you specify, so specify 1 tick to show
     * ticks on either end of the line. In other words, numTicks is really the number of slots between the ticks.
     * 
     * setNumTicks(0) will disable ticks.
     * 
     * @param numTicks
     *            the number of ticks to show
     */
    public final void setNumTicks(final int numTicks)
    {
        this.numTicks = numTicks;
        drawTicks();
    }

    /**
     * Set the step size.
     * 
     * @param stepSize
     *            the current value
     */
    public final void setStepSize(final double stepSize)
    {
        this.stepSize = stepSize;
        resetCurrentValue();
    }

    /**
     * Set current value.
     * 
     * @param value
     *            current value.
     */
    public final void setValue(final Double value)
    {
        setCurrentValue(value, false);
    }

    /**
     * Set current value.
     * 
     * @param value
     *            current value.
     * @param fireEvent
     *            fire event.
     */
    public final void setValue(final Double value, final boolean fireEvent)
    {
        setCurrentValue(value, fireEvent);
    }

    /**
     * Shift to the left (smaller value).
     * 
     * @param numSteps
     *            the number of steps to shift
     */
    public final void shiftLeft(final int numSteps)
    {
        setCurrentValue(getCurrentValue() - numSteps * stepSize);
    }

    /**
     * Shift to the right (greater value).
     * 
     * @param numSteps
     *            the number of steps to shift
     */
    public final void shiftRight(final int numSteps)
    {
        setCurrentValue(getCurrentValue() + numSteps * stepSize);
    }

    /**
     * Format the label to display above the ticks
     * 
     * Override this method in a subclass to customize the format. By default, this method returns the integer portion
     * of the value.
     * 
     * @param value
     *            the value at the label
     * @return the text to put in the label
     */
    protected final String formatLabel(final double value)
    {
        if (labelFormatter != null)
        {
            return labelFormatter.formatLabel(this, value);
        } else
        {
            return (int) (10 * value) / 10.0 + "";
        }
    }

    /**
     * Get the percentage of the knob's position relative to the size of the line. The return value will be between 0.0
     * and 1.0.
     * 
     * @return the current percent complete
     */
    protected final double getKnobPercent()
    {
        // If we have no range
        if (maxValue <= minValue)
        {
            return 0;
        }

        // Calculate the relative progress
        double percent = (curValue - minValue) / (maxValue - minValue);
        return Math.max(0.0, Math.min(1.0, percent));
    }

    /**
     * This method is called immediately after a widget becomes attached to the browser's document.
     */
    @Override
    protected final void onLoad()
    {
        // Reset the position attribute of the parent element
        getElement().getStyle().setProperty("position", "relative");
    }

    /**
     * Draw the knob where it is supposed to be relative to the line.
     */
    private void drawKnob()
    {
        // Abort if not attached
        if (!isAttached())
        {
            return;
        }

        // Move the knob to the correct position
        Element knobElement = knobImage.getElement();
        int lineWidth = lineElement.getOffsetWidth();
        int knobWidth = knobElement.getOffsetWidth();
        int knobLeftOffset = (int) (lineLeftOffset + (getKnobPercent() * lineWidth) - (knobWidth / 2));
        knobLeftOffset = Math.min(knobLeftOffset, lineLeftOffset + lineWidth - (knobWidth / 2) - 1);
        knobElement.getStyle().setProperty("left", knobLeftOffset + "px");
    }

    /**
     * Draw the labels along the line.
     */
    private void drawLabels()
    {
        // Abort if not attached
        if (!isAttached())
        {
            return;
        }

        // Draw the labels
        int lineWidth = lineElement.getOffsetWidth();
        if (numLabels > 0)
        {
            // Create the labels or make them visible
            for (int i = 0; i <= numLabels; i++)
            {
                Element label = null;
                if (i < labelElements.size())
                {
                    label = labelElements.get(i);
                } else
                { // Create the new label
                    label = DOM.createDiv();
                    label.getStyle().setProperty("position", "absolute");
                    label.getStyle().setProperty("display", "none");
                    if (enabled)
                    {
                        label.setPropertyString("className", "gwt-SliderBar-label");
                    } else
                    {
                        label.setPropertyString("className", "gwt-SliderBar-label-disabled");
                    }
                    DOM.appendChild(getElement(), label);
                    labelElements.add(label);
                }

                // Set the label text
                double value = minValue + (getTotalRange() * i / numLabels);
                label.getStyle().setProperty("visibility", "hidden");
                label.getStyle().setProperty("display", "");
                label.getStyle().setProperty("innerHTML", formatLabel(value));

                // Move to the left so the label width is not clipped by the shell
                label.getStyle().setProperty("left", "0px");

                // Position the label and make it visible
                int labelWidth = label.getOffsetWidth();
                int labelLeftOffset = lineLeftOffset + (lineWidth * i / numLabels) - (labelWidth / 2);
                labelLeftOffset = Math.min(labelLeftOffset, lineLeftOffset + lineWidth - labelWidth);
                labelLeftOffset = Math.max(labelLeftOffset, lineLeftOffset);
                label.getStyle().setProperty("left", labelLeftOffset + "px");
                label.getStyle().setProperty("visibility", "visible");
            }

            // Hide unused labels
            for (int i = (numLabels + 1); i < labelElements.size(); i++)
            {
                labelElements.get(i).getStyle().setProperty("display", "none");
            }
        } else
        { // Hide all labels
            for (Element elem : labelElements)
            {
                elem.getStyle().setProperty("display", "none");
            }
        }
    }

    /**
     * Draw the tick along the line.
     */
    private void drawTicks()
    {
        // Abort if not attached
        if (!isAttached())
        {
            return;
        }

        // Draw the ticks
        int lineWidth = lineElement.getOffsetWidth();
        if (numTicks > 0)
        {
            // Create the ticks or make them visible
            for (int i = 0; i <= numTicks; i++)
            {
                Element tick = null;
                if (i < tickElements.size())
                {
                    tick = tickElements.get(i);
                } else
                { // Create the new tick
                    tick = DOM.createDiv();
                    tick.getStyle().setProperty("position", "absolute");
                    tick.getStyle().setProperty("display", "none");
                    DOM.appendChild(getElement(), tick);
                    tickElements.add(tick);
                }
                if (enabled)
                {
                    tick.setPropertyString("className", "gwt-SliderBar-tick");
                } else
                {
                    tick.setPropertyString("className", "gwt-SliderBar-tick gwt-SliderBar-tick-disabled");
                }
                // Position the tick and make it visible
                tick.getStyle().setProperty("visibility", "hidden");
                tick.getStyle().setProperty("display", "");
                int tickWidth = tick.getOffsetWidth();
                int tickLeftOffset = lineLeftOffset + (lineWidth * i / numTicks) - (tickWidth / 2);
                tickLeftOffset = Math.min(tickLeftOffset, lineLeftOffset + lineWidth - tickWidth);
                tick.getStyle().setProperty("left", tickLeftOffset + "px");
                tick.getStyle().setProperty("visibility", "visible");
            }

            // Hide unused ticks
            for (int i = (numTicks + 1); i < tickElements.size(); i++)
            {
                tickElements.get(i).getStyle().setProperty("display", "none");
            }
        } else
        { // Hide all ticks
            for (Element elem : tickElements)
            {
                elem.getStyle().setProperty("display", "none");
            }
        }
    }

    /**
     * Highlight this widget.
     */
    private void highlight()
    {
        String styleName = getStylePrimaryName();
        getElement().setPropertyString("className", styleName + " " + styleName + "-focused");
    }

    /**
     * Reset the progress to constrain the progress to the current range and redraw the knob as needed.
     */
    private void resetCurrentValue()
    {
        setCurrentValue(getCurrentValue());
    }

    /**
     * Slide the knob to a new location.
     * 
     * @param event
     *            the mouse event
     */
    private void slideKnob(final Event event)
    {
        int x = event.getClientX();
        if (x > 0)
        {
            int lineWidth = lineElement.getOffsetWidth();
            int lineLeft = lineElement.getAbsoluteLeft();
            double percent = (double) (x - lineLeft) / lineWidth * 1.0;
            setCurrentValue(getTotalRange() * percent + minValue, true);
        }
    }

    /**
     * Start sliding the knob.
     * 
     * @param highlight
     *            true to change the style
     * @param fireEvent
     *            true to fire the event
     */
    private void startSliding(final boolean highlight, final boolean fireEvent)
    {
        if (highlight)
        {
            lineElement.setPropertyString("className", "gwt-SliderBar-line gwt-SliderBar-line-sliding");
            knobImage.getElement().setPropertyString("className", "gwt-SliderBar-knob gwt-SliderBar-knob-sliding");
            knobImage.setResource(images.sliderSliding());
        }
    }

    /**
     * Stop sliding the knob.
     * 
     * @param unhighlight
     *            true to change the style
     * @param fireEvent
     *            true to fire the event
     */
    private void stopSliding(final boolean unhighlight, final boolean fireEvent)
    {
        if (unhighlight)
        {
            lineElement.setPropertyString("className", "gwt-SliderBar-line");

            knobImage.getElement().setPropertyString("className", "gwt-SliderBar-knob");
            knobImage.setResource(images.slider());
        }
    }

    /**
     * Unhighlight this widget.
     */
    private void unhighlight()
    {
        getElement().setPropertyString("className", getStylePrimaryName());
    }

    @Override
    public final void onResize()
    {
        redraw();
    }
}
