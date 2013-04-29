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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.exceptions.DomainException;
import pt.ist.fenixframework.Atomic;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

/**
 * 
 * @author João Neves
 * @author Paulo Abrantes
 * 
 */
public abstract class DashBoardPanel extends DashBoardPanel_Base {

    public DashBoardPanel() {
        super();
        for (int order = 0; order < 3; order++) {
            new DashBoardColumn(order, this);
        }
        setDashBoardController(DashBoardController.getInstance());

    }

    public DashBoardPanel(MultiLanguageString name) {
        this();
        setName(name);
    }

    public DashBoardColumn getColumn(int order) {
        for (DashBoardColumn column : getDashBoardColumns()) {
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
        return isAccessibleToUser(UserView.getCurrentUser());
    }

    public abstract boolean isAccessibleToUser(User user);

    @Atomic
    public void edit(List<DashBoardColumnBean> beans) {
        if (!isAccessibleToCurrentUser()) {
            throw new DomainException("error.permission.denied");
        }
        for (DashBoardColumnBean bean : beans) {
            DashBoardColumn column = getColumn(bean.getOrder());
            column.rearrangeColumnTo(bean.getWidgets());
        }
    }

    public Set<DashBoardColumn> getOrderedColumns() {
        Set<DashBoardColumn> columns = new TreeSet<DashBoardColumn>(DashBoardColumn.IN_PANEL_COMPARATOR);
        columns.addAll(getDashBoardColumns());
        return columns;
    }

    public Set<DashBoardWidget> getWidgetsSet() {
        Set<DashBoardWidget> widgets = new HashSet<DashBoardWidget>();
        for (DashBoardColumn column : getDashBoardColumns()) {
            widgets.addAll(column.getWidgets());
        }

        return widgets;
    }

    @SuppressWarnings("unchecked")
    public static <T extends DashBoardPanel> List<T> getPanelsForUser(User user, Class<T> panelClass) {
        List<T> panels = new ArrayList<T>();
        for (DashBoardPanel panel : user.getUserDashBoardsSet()) {
            if (panelClass.isAssignableFrom(panel.getClass())) {
                panels.add((T) panel);
            }
        }

        return panels;
    }

    @Deprecated
    public java.util.Set<module.dashBoard.domain.DashBoardColumn> getDashBoardColumns() {
        return getDashBoardColumnsSet();
    }

}
