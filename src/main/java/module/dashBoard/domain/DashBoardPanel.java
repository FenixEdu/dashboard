/*
 * @(#)DashBoardPanel.java
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;

import module.dashBoard.servlet.WidgetRegistry;
import pt.ist.fenixframework.Atomic;

/**
 * 
 * @author João Neves
 * @author Paulo Abrantes
 * 
 */
public class DashBoardPanel extends DashBoardPanel_Base {

    public DashBoardPanel(User user) {
        super();
        for (int order = 0; order < 3; order++) {
            new DashBoardColumn(order, this);
        }
        setDashBoardController(DashBoardController.getInstance());
        setUser(user);
        WidgetRegistry.addWidgetsForNewPanel(this);
    }

    public DashBoardColumn getColumn(int order) {
        for (DashBoardColumn column : getDashBoardColumnsSet()) {
            if (column.getColumnOrder() == order) {
                return column;
            }
        }
        return null;
    }

    public void addWidgetToColumn(int column, DashBoardWidget widget) {
        getColumn(column).addWidget(widget);
    }

    public void removeWidgetFromColumn(int column, DashBoardWidget widget) {
        getColumn(column).removeWidget(widget);
    }

    public boolean isAccessibleToCurrentUser() {
        return isAccessibleToUser(Authenticate.getUser());
    }

    public boolean isAccessibleToUser(User user) {
        return user != null && user == getUser();
    }

    @Atomic
    public void edit(List<DashBoardColumnBean> beans) {
        if (!isAccessibleToCurrentUser()) {
            throw DashBoardDomainException.permissionDenied();
        }
        for (DashBoardColumnBean bean : beans) {
            DashBoardColumn column = getColumn(bean.getOrder());
            column.rearrangeColumnTo(bean.getWidgets());
        }
    }

    public Set<DashBoardColumn> getOrderedColumns() {
        Set<DashBoardColumn> columns = new TreeSet<DashBoardColumn>(DashBoardColumn.IN_PANEL_COMPARATOR);
        columns.addAll(getDashBoardColumnsSet());
        return columns;
    }

    public Set<DashBoardWidget> getWidgetsSet() {
        Set<DashBoardWidget> widgets = new HashSet<DashBoardWidget>();
        getDashBoardColumnsSet().forEach(c -> widgets.addAll(c.getWidgetsSet()));
        return widgets;
    }

    public void delete() {
        setUser(null);
        setDashBoardController(null);
        getDashBoardColumnsSet().forEach(c -> c.delete());
        deleteDomainObject();
    }

}
