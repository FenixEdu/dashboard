package module.dashBoard.servlet;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import module.dashBoard.widgets.DashboardWidget;
import module.dashBoard.widgets.WidgetController;

@HandlesTypes(DashboardWidget.class)
public class DashboardContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        if (c != null) {
            for (Class<?> type : c) {
                if (!WidgetController.class.isAssignableFrom(type)) {
                    throw new ServletException("@DashboardWidget " + type.getName() + " does not extend WidgetController!");
                }
                WidgetRegistry.register(type);
            }
        }
    }

}
