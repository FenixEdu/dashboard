/*
 * @(#)DashBoardWidget.java
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
package module.dashBoard.domain;

import java.util.Comparator;

import module.dashBoard.widgets.WidgetController;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainObject;
import pt.ist.fenixframework.FenixFramework;

/**
 * 
 * @author João Neves
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public class DashBoardWidget extends DashBoardWidget_Base {

    public static final Comparator<DashBoardWidget> IN_COLUMN_COMPARATOR = (widget1, widget2) -> Integer.valueOf(
            widget1.getOrderInColumn()).compareTo(widget2.getOrderInColumn());

    public DashBoardWidget(Class<? extends WidgetController> controller) {
        super();
        setDashBoardController(DashBoardController.getInstance());
        setWidgetController(WidgetController.internalize(controller));
        getWidgetController().init(this, Authenticate.getUser());
        setOrderInColumn(0);
    }

    @Override
    public WidgetController getWidgetController() {
        if (getDashBoardColumn() != null && getDashBoardPanel() != null && !getDashBoardPanel().isAccessibleToCurrentUser()) {
            throw DashBoardDomainException.illegalAccessToWidget();
        }
        return super.getWidgetController();
    }

    @Atomic
    public void delete() {
        if (!isClosable()) {
            throw new UnsupportedOperationException();
        }
        if (getDashBoardPanel() != null && !getDashBoardPanel().isAccessibleToCurrentUser()) {
            throw DashBoardDomainException.illegalAccessToWidget();
        }
        setDashBoardColumn(null);
        setDashBoardController(null);
        getWidgetController().kill(this, Authenticate.getUser());
        deleteDomainObject();
    }

    public <T extends DomainObject> T getStateObject() {
        return getStateObjectId() != null ? FenixFramework.<T> getDomainObject(getStateObjectId()) : null;
    }

    public void setStateObject(DomainObject domainObject) {
        setStateObjectId(domainObject.getExternalId());
    }

    public boolean isEditionModeSupported() {
        return getWidgetController().isEditionModeSupported();
    }

    public boolean isOptionsModeSupported() {
        return getWidgetController().isOptionsModeSupported();
    }

    public boolean isHelpModeSupported() {
        return getWidgetController().isHelpModeSupported();
    }

    public boolean isClosable() {
        final WidgetController widgetController = getWidgetController();
        return widgetController == null || widgetController.isClosable();
    }

    public DashBoardPanel getDashBoardPanel() {
        return getDashBoardColumn().getDashBoardPanel();
    }

    public boolean isAccessibleToUser(User user) {
        return getDashBoardPanel().isAccessibleToUser(user);
    }

    @Atomic
    public static DashBoardWidget newWidget(Class<? extends WidgetController> widgetClass) {
        return new DashBoardWidget(widgetClass);
    }

}
