/*
 * @(#)NoteWidget.java
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
import module.dashBoard.domain.Note;
import module.dashBoard.presentationTier.WidgetRequest;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.i18n.BundleUtil;

/**
 * 
 * @author João Neves
 * @author Paulo Abrantes
 * 
 */
@DashboardWidget(nameBundle = "resources.DashBoardResources", nameKey = "widget.title.NoteWidget")
public class NoteWidget extends WidgetController {

    @Override
    public void init(DashBoardWidget widget, User user) {
        Note note = widget.getStateObject();
        if (note == null) {
            note = new Note();
            widget.setStateObject(note);
        }
    }

    @Override
    public void kill(DashBoardWidget widget, User user) {
        Note note = widget.getStateObject();
        note.delete();
    }

    @Override
    public void doView(WidgetRequest request) {
        DashBoardWidget widget = request.getWidget();
        request.setAttribute("note-" + widget.getExternalId(), widget.getStateObject());
    }

    @Override
    public boolean isEditionModeSupported() {
        return true;
    }

    @Override
    public void doEdit(WidgetRequest request) {
        DashBoardWidget widget = request.getWidget();
        request.setAttribute("edit-note-" + widget.getExternalId(), widget.getStateObject());
    }

    @Override
    public boolean isHelpModeSupported() {
        return true;
    }

    @Override
    public String getHelp() {
        return BundleUtil.getString("resources/DashBoardResources", "widget.help.NoteWidget");
    }

    @Override
    public String getWidgetDescription() {
        return BundleUtil.getString("resources/DashBoardResources", "widget.description.NoteWidget");
    }

}
