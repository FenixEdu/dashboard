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
import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
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

    public static final Comparator<DashBoardWidget> IN_COLUMN_COMPARATOR = new Comparator<DashBoardWidget>() {

        @Override
        public int compare(DashBoardWidget widget1, DashBoardWidget widget2) {
            return Integer.valueOf(widget1.getOrderInColumn()).compareTo(widget2.getOrderInColumn());
        }

    };

    private WidgetController instance;

    public DashBoardWidget(Class<? extends WidgetController> widgetClass) {
        super();
        setDashBoardController(DashBoardController.getInstance());
        setWidgetClassName(widgetClass.getName());
        getWidgetController().init(this, UserView.getCurrentUser());
        setOrderInColumn(0);
    }

    public WidgetController getWidgetController() {
        if (getDashBoardColumn() != null && getDashBoardPanel() != null && !getDashBoardPanel().isAccessibleToCurrentUser()) {
            throw new DomainException("error.ilegal.access.to.widget");
        }
        if (instance == null) {
            initializeWidget();
        }
        return instance;
    }

    private synchronized void initializeWidget() {
        try {
            instance = (WidgetController) Class.forName(getWidgetClassName()).newInstance();
            instance.init(this, UserView.getCurrentUser());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Atomic
    public void delete() {
        if (!isClosable()) {
            throw new UnsupportedOperationException();
        }
        if (getDashBoardPanel() != null && !getDashBoardPanel().isAccessibleToCurrentUser()) {
            throw new DomainException("error.ilegal.access.to.widget");
        }
        setDashBoardColumn(null);
        setDashBoardController(null);
        getWidgetController().kill(this, UserView.getCurrentUser());
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
