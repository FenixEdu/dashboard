/*
 * @(#)DashBoardColumn.java
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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public class DashBoardColumn extends DashBoardColumn_Base {

    public static final Comparator<DashBoardColumn> IN_PANEL_COMPARATOR = (column1, column2) -> Integer.valueOf(
            column1.getColumnOrder()).compareTo(column2.getColumnOrder());

    public DashBoardColumn(int order, DashBoardPanel panel) {
        super();
        setDashBoardController(DashBoardController.getInstance());
        setColumnOrder(order);
        setDashBoardPanel(panel);
    }

    @Atomic
    public void addWidget(DashBoardWidget widget) {
        if (!getDashBoardPanel().isAccessibleToCurrentUser()) {
            throw DashBoardDomainException.permissionDenied();
        }
        for (DashBoardWidget existingWidget : getWidgetsSet()) {
            existingWidget.setOrderInColumn(existingWidget.getOrderInColumn() + 1);
        }
        super.addWidgets(widget);
    }

    @Atomic
    public void removeWidget(DashBoardWidget widget) {
        if (!getDashBoardPanel().isAccessibleToCurrentUser()) {
            throw DashBoardDomainException.permissionDenied();
        }
        super.removeWidgets(widget);
    }

    public void rearrangeColumnTo(List<DashBoardWidget> widgets) {
        getWidgetsSet().clear();
        int order = 0;
        for (DashBoardWidget widget : widgets) {
            widget.setOrderInColumn(order++);
            getWidgetsSet().add(widget);
        }
    }

    public Set<DashBoardWidget> getOrderedWidgets() {
        Set<DashBoardWidget> widgets = new TreeSet<DashBoardWidget>(DashBoardWidget.IN_COLUMN_COMPARATOR);
        widgets.addAll(getWidgetsSet());
        return widgets;
    }

    public void delete() {
        setDashBoardController(null);
        setDashBoardPanel(null);
        for (final DashBoardWidget dashBoardWidget : getWidgetsSet()) {
            dashBoardWidget.delete();
        }
        deleteDomainObject();
    }

}
