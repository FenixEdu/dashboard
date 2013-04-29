/*
 * @(#)UserDashBoardPanel.java
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

import pt.ist.bennu.core.domain.User;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

/**
 * 
 * @author João Neves
 * @author Luis Cruz
 * @author Paulo Abrantes
 * 
 */
public class UserDashBoardPanel extends UserDashBoardPanel_Base {

    protected UserDashBoardPanel() {
        super();
    }

    public UserDashBoardPanel(MultiLanguageString name, User user) {
        super();
        setName(name);
        setUser(user);
    }

    @Override
    public boolean isAccessibleToUser(User user) {
        return user != null && user == getUser();
    }

    public void delete() {
        setUser(null);
        setDashBoardController(null);
        for (final DashBoardColumn dashBoardColumn : getDashBoardColumnsSet()) {
            dashBoardColumn.delete();
        }
        deleteDomainObject();
    }

}
