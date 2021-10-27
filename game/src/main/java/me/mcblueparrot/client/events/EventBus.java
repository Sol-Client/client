package me.mcblueparrot.client.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Event bus system focused on performance.
 */
public class EventBus {

    public class MethodData {

        public Object instance;
        public Method target;

        public MethodData(Object instance, Method target) {
            this.instance = instance;
            this.target = target;
        }

    }

    private Map<Class<?>, List<MethodData>> handlers = new HashMap<Class<?>, List<MethodData>>();
    private Logger logger = LogManager.getLogger();

    public void register(Object obj) {
        for(Method method : obj.getClass().getMethods()) {
            if(validate(method)) {
                handlers.computeIfAbsent(method.getParameters()[0].getType(), (ingore) -> new ArrayList<>())
                        .add(new MethodData(obj, method));
            }
        }
    }

    public void unregister(Object obj) {
        for(List<MethodData> methods : handlers.values()) {
            methods.removeIf((method) -> method.instance == obj);
        }
    }

    public <T> T post(T event) {
        if(!handlers.containsKey(event.getClass())) {
            return event;
        }

        for(MethodData method : handlers.get(event.getClass())) {
            try {
                method.target.invoke(method.instance, event);
            }
            catch(IllegalAccessException | IllegalArgumentException error) {
                logger.error("Failed to invoke " + method.target.getName() + ":", error);
            }
            catch(InvocationTargetException error) {
                logger.error("Error while executing " + method.target.getName() + ":", error.getCause());
            }
        }

        return event;
    }

    private boolean validate(Method method) {
        if(method.isAnnotationPresent(EventHandler.class)) {
            Validate.isTrue(method.getParameterCount() == 1, "Method " + method.getName() + " has " + method.getParameterCount() + " parameter; expected 1.");
            return true;
        }
        return false;
    }

}
