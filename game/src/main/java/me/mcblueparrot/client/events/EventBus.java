package me.mcblueparrot.client.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.replaymod.replaystudio.lib.viaversion.libs.kyori.adventure.util.Nag;
import me.mcblueparrot.client.util.ThreadSafetyIssue;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Event bus system focused on performance.
 */
@NotThreadSafe
public class EventBus {

    public static class MethodData {

        public Object instance;
        public Method target;

        public MethodData(Object instance, Method target) {
            this.instance = instance;
            this.target = target;
        }

    }

    private boolean inPost;
    private List<Object> toRemove = new ArrayList<>();
    private Minecraft mc = Minecraft.getMinecraft();
    private Map<Class<?>, List<MethodData>> handlers = new HashMap<Class<?>, List<MethodData>>();
    private Logger logger = LogManager.getLogger();

    private Set<Method> getMethods(Class<?> clazz) {
        Set<Method> methods = new HashSet<>();

        Collections.addAll(methods, clazz.getMethods());
        Collections.addAll(methods, clazz.getDeclaredMethods());

        methods.forEach((method) -> method.setAccessible(true));

        return methods;
    }

    public void register(Object obj) {
        for(Method method : getMethods(obj.getClass())) {
            if(validate(method)) {
                handlers.computeIfAbsent(method.getParameters()[0].getType(), (ignore) -> new ArrayList<>())
                        .add(new MethodData(obj, method));
            }
        }
    }

    public void unregister(Object obj) {
        if(inPost) {
            toRemove.add(obj);
            return;
        }

        for(List<MethodData> methods : handlers.values()) {
            methods.removeIf((method) -> method.instance == obj);
        }
    }

    public <T> T post(T event) {
        if(!handlers.containsKey(event.getClass())) {
            return event;
        }

        try {
            toRemove.clear();
            inPost = true;
            for (MethodData method : handlers.get(event.getClass())) {
                try {
                    method.target.invoke(method.instance, event);
                } catch (IllegalAccessException | IllegalArgumentException error) {
                    logger.error("Failed to invoke " + method.target.getName() + ":", error);
                } catch (InvocationTargetException error) {
                    logger.error("Error while executing " + method.target.getName() + ":", error.getCause());
                }
            }
            inPost = false;
            toRemove.forEach(this::unregister);
        }
        catch(ConcurrentModificationException ignored) {
            // This is how
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
