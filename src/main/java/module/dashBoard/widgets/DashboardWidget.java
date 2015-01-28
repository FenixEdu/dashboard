package module.dashBoard.widgets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import module.dashBoard.domain.DashBoardPanel;
import module.dashBoard.servlet.WidgetRegistry.WidgetAditionPredicate;

import org.fenixedu.bennu.core.domain.User;

/**
 * Marks the annotated class as a Dashboard Widget.
 * 
 * An error is thrown if the annotated class does not extends {@link WidgetController}.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DashboardWidget {

    /**
     * Specifies the column in which this widget will be inserted when a new dashboard is inserted.
     * 
     * A negative value means that the widget will not be instantiated automatically.
     * 
     * @return
     *         The widget column
     */
    int defaultColumn() default -1;

    String nameBundle();

    String nameKey();

    /**
     * Specifies the {@link WidgetAditionPredicate} that determines whether the widget can be added to a given dashboard.
     */
    Class<? extends WidgetAditionPredicate> aditionPredicate() default AlwaysAddPredicate.class;

    static final class AlwaysAddPredicate implements WidgetAditionPredicate {
        @Override
        public boolean canBeAdded(DashBoardPanel panel, User userAdding) {
            return true;
        }
    }

}
