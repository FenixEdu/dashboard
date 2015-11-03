/*
 * @(#)DashBoardController.java
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

import org.fenixedu.bennu.core.domain.Bennu;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

/**
 * 
 * @author Paulo Abrantes
 * 
 */
public class DashBoardController extends DashBoardController_Base {

    private DashBoardController() {
        setBennu(Bennu.getInstance());
    }

    public static DashBoardController getInstance() {
        final DashBoardController controller = Bennu.getInstance().getDashBoardController();
        return controller == null ? initialize() : controller;
    }

    @Atomic(mode = TxMode.WRITE)
    private static DashBoardController initialize() {
        final DashBoardController controller = Bennu.getInstance().getDashBoardController();
        return controller == null ? new DashBoardController() : controller;
    }

}
