package project.control;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class TooltipProlongHelper {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TooltipProlongHelper.class);

    public static void setTooltipDuration(Duration openDelay, Duration visibleDuration, Duration closeDelay) {
        try {
            Field field = Tooltip.class.getDeclaredField("BEHAVIOR");
            field.setAccessible(true);

            Class<?> behaviorClass = findBehaviorClass();
            if (behaviorClass == null) {
                log.warn("Can't find tooltip behavior class");
                return;
            }

            Constructor<?> behaviorConstructor = behaviorClass.getDeclaredConstructor(Duration.class, Duration.class,
                    Duration.class, boolean.class);
            if (behaviorConstructor == null) {
                log.warn("Can't find proper behavior constructor");
                return;
            }

            behaviorConstructor.setAccessible(true);
            field.set(null, behaviorConstructor.newInstance(openDelay, visibleDuration, closeDelay, false));

        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
                | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            log.fatal(e);
            return;
        }
    }

    private static Class<?> findBehaviorClass() {
        for (Class<?> clazz : Tooltip.class.getDeclaredClasses()) {
            if (clazz.getName().endsWith("TooltipBehavior")) {
                return clazz;
            }
        }
        return null;
    }
}

