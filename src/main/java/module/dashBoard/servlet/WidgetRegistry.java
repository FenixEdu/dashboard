/*
 * @(#)WidgetRegister.java
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
package module.dashBoard.servlet;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;

import module.dashBoard.domain.DashBoardPanel;
import module.dashBoard.domain.DashBoardWidget;
import module.dashBoard.widgets.DashboardWidget;
import module.dashBoard.widgets.WidgetController;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;

/**
 * 
 * @author João Neves
 * @author Paulo Abrantes
 * 
 */
public class WidgetRegistry {

    public static final Comparator<Class<? extends WidgetController>> WIDGET_NAME_COMPARATOR = (o1, o2) -> getNameForWidget(o1)
            .compareTo(getNameForWidget(o2));

    public static void addWidgetsForNewPanel(DashBoardPanel dashBoardPanel) {
        availableWidgets.stream().filter((holder) -> holder.position >= 0)
                .forEach((holder) -> dashBoardPanel.addWidgetToColumn(holder.position, new DashBoardWidget(holder.controller)));
    }

    public static String getNameForWidget(Class<?> type) {
        DashboardWidget widget = type.getAnnotation(DashboardWidget.class);
        return BundleUtil.getString(widget.nameBundle(), widget.nameKey());
    }

    static void register(Class<?> type) throws ServletException {
        try {
            DashboardWidget widget = type.getAnnotation(DashboardWidget.class);
            WidgetAditionPredicate predicate = widget.aditionPredicate().newInstance();
            availableWidgets.add(new WidgetControllerHolder(type, predicate, widget.defaultColumn()));
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServletException(e);
        }
    }

    private static final Set<WidgetControllerHolder> availableWidgets = new HashSet<>();

    public static Set<Class<? extends WidgetController>> getAvailableWidgets(DashBoardPanel panel, User userAdding) {
        return availableWidgets.stream().filter((holder) -> holder.canBeAdded(panel, userAdding))
                .map(WidgetControllerHolder::getController).sorted(WIDGET_NAME_COMPARATOR).collect(Collectors.toSet());
    }

    public static interface WidgetAditionPredicate {
        public boolean canBeAdded(DashBoardPanel panel, User userAdding);
    }

    private static class WidgetControllerHolder {
        private final Class<? extends WidgetController> controller;
        private final WidgetAditionPredicate predicate;
        private final int position;

        @SuppressWarnings("unchecked")
        public WidgetControllerHolder(Class<?> controller, WidgetAditionPredicate predicate, int position) {
            this.controller = (Class<? extends WidgetController>) controller;
            this.predicate = predicate;
            this.position = position;
        }

        public boolean canBeAdded(DashBoardPanel panel, User userAdding) {
            return predicate.canBeAdded(panel, userAdding);
        }

        public Class<? extends WidgetController> getController() {
            return controller;
        }

        /*
         * (non-Javadoc) Delegating equals and hashCode to class, since we want
         * the class to be the identity of this object. This way only one
         * instance of each class controller is allowed to be in the
         * availableWidgets set at a time.
         */
        @Override
        public boolean equals(Object obj) {
            return controller.equals(obj);
        }

        @Override
        public int hashCode() {
            return controller.hashCode();
        }
    }

}
