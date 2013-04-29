/*
 * @(#)WidgetController.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: Paulo Abrantes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Dashboard Module.
 *
 *   The Dashboard Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Dashboard Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Dashboard Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.dashBoard.widgets;

import module.dashBoard.domain.DashBoardWidget;
import module.dashBoard.domain.WidgetOptions;
import module.dashBoard.presentationTier.DashBoardManagementAction;
import module.dashBoard.presentationTier.WidgetRequest;

import org.apache.struts.action.ActionForward;

import pt.ist.bennu.core.domain.User;
import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author João Neves
 * @author Paulo Abrantes
 * 
 */
public abstract class WidgetController {

    /**
     * This method is called when a new DashBoardWidget is being created. The
     * controller is initiated and it can be used for example to store something
     * in the StateObject.
     * 
     * @param widget
     *            The widget that will store this WidgetController
     * @param user
     *            The user that is creating the widget
     */
    public void init(DashBoardWidget widget, User user) {
        if (isOptionsModeSupported()) {
            getOrCreateOptions(widget);
        }
    }

    /**
     * This method is called when a new DashBoardWidget is being created.
     * Obtains and returns the WidgetOptions, if not null. Otherwise, creates
     * the WidgetOptions, associates it with this widget and returns it.
     * 
     * @param widget
     *            The widget from which to obtain or create the WidgetOptions
     */
    @Atomic
    protected WidgetOptions getOrCreateOptions(DashBoardWidget widget) {
        WidgetOptions options = widget.getOptions();
        if (options == null) {
            options = new WidgetOptions();
            widget.setOptions(options);
        }
        return options;
    }

    /**
     * This method is called when a DashBoardWidget is being deleted. The
     * controller is killed this is used for example to also delete the
     * StateObject
     * 
     * @param widget
     *            The widget that is being deleted
     * @param user
     *            The user that is deleting the widget
     */
    public void kill(DashBoardWidget widget, User user) {
        if (isOptionsModeSupported()) {
            getOrCreateOptions(widget).delete();
        }
    }

    /**
     * This method is called when there is a request to view a widget in the
     * DashBoardPanel
     * 
     * @param request
     */

    public abstract void doView(WidgetRequest request);

    /**
     * This method is called when there is a request to edit a widget in the
     * DashBoardPanel
     * 
     * By default throws UnsupportedOperationException.
     * 
     * @param request
     */
    public void doEdit(WidgetRequest request) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is called when there is a request to edit the widget options
     * in the DashBoardPanel
     * 
     * By default throws UnsupportedOperationException.
     * 
     * @param request
     */
    public void doEditOptions(WidgetRequest request) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is called when there is a request to view the widget's help
     * in the DashBoardPanel
     * 
     * By default throws UnsupportedOperationException.
     * 
     * @param request
     */
    public void doHelp(WidgetRequest request) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is called when there is a request to submit the widget in the
     * DashBoardPanel.
     * 
     * Returns the ActionForward to where the client should be sent after
     * submit.
     * 
     * By default returns to the dashBoard view.
     * 
     * @param request
     */
    public ActionForward doSubmit(WidgetRequest request) {
        return DashBoardManagementAction.forwardToDashBoard(request);
    }

    /**
     * This allows the DashBoardPanel to know if the edit icon (and it's
     * functionality) should be rendered in the DashBoardWidget or not.
     * 
     */
    public boolean isEditionModeSupported() {
        return false;
    }

    /**
     * Indicates whether this WidgetController supports the options mode.
     * 
     */
    public boolean isOptionsModeSupported() {
        return false;
    }

    /**
     * This allows the DashBoardPanel to know if the help icon (and it's
     * functionality) should be rendered in the DashBoardWidget or not.
     * 
     */
    public boolean isHelpModeSupported() {
        return false;
    }

    /**
     * This contains the help text that will be used when helpMode is supported
     * 
     */
    public String getHelp() {
        return null;
    }

    /**
     * This allows the DashBoardPanel to know if the close icon (and it's
     * functionality) should be rendered in the DashBoardWidget or not.
     * 
     */
    public boolean isClosable() {
        return true;
    }

    /**
     * This method returns a textual description of the widget that is displayed
     * to the user when he/she is in the add widget interface and generates a
     * mouse over event in the "Add" link.
     */

    public abstract String getWidgetDescription();

}
