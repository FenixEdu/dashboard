/*
 * @(#)WidgetRequest.java
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
package module.dashBoard.presentationTier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import module.dashBoard.domain.DashBoardPanel;
import module.dashBoard.domain.DashBoardWidget;

import org.fenixedu.bennu.core.domain.User;

import pt.ist.fenixWebFramework.renderers.components.state.IViewState;
import pt.ist.fenixWebFramework.renderers.model.MetaObject;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter;
import pt.ist.fenixframework.DomainObject;

/**
 * 
 * @author Paulo Abrantes
 * 
 */
public class WidgetRequest {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    private final User currentUser;
    private final DashBoardWidget widget;

    public WidgetRequest(HttpServletRequest request, HttpServletResponse response, DashBoardWidget widget, User user) {
        this.request = request;
        this.response = response;
        this.currentUser = user;
        this.widget = widget;
    }

    public DomainObject getStateObject() {
        return this.widget.getStateObject();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    protected HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public DashBoardPanel getPanel() {
        return this.widget.getDashBoardPanel();
    }

    public void setAttribute(String name, Object object) {
        request.setAttribute(name, object);
    }

    public String getContextPath() {
        return request.getContextPath();
    }

    public String injectChecksumIn(String url) {
        return GenericChecksumRewriter.injectChecksumInUrl(getContextPath(), url, request.getSession());
    }

    public DashBoardWidget getWidget() {
        return widget;
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T getRenderedObject(final String id) {
        final IViewState viewState = RenderUtils.getViewState(id);
        return (T) getRenderedObject(viewState);
    }

    @SuppressWarnings("unchecked")
    private <T extends Object> T getRenderedObject(final IViewState viewState) {
        if (viewState != null) {
            MetaObject metaObject = viewState.getMetaObject();
            if (metaObject != null) {
                return (T) metaObject.getObject();
            }
        }
        return null;
    }

}
