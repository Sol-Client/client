package me.mcblueparrot.client.mod.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import me.mcblueparrot.client.mod.Mod;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigOption {

    /**
     * The option's name.
     * @return The name.
     */
    String value();

    int priority() default 0;

    class Cached {

        public Mod mod;
        public String name;
        public Field field;
        public int priority;

        public Cached(Mod mod, ConfigOption option, Field field) {
            this.mod = mod;
            if(option != null) {
                name = option.value();
            }

            if(field != null) {
                this.field = field;
                field.setAccessible(true);
            }

            if(option != null) {
                priority = option.priority();
            }
        }

        public Class<?> getType() {
            return field.getType();
        }

        public Object getValue() {
            try {
                return field.get(mod);
            }
            catch(IllegalArgumentException | IllegalAccessException error) {
                throw new IllegalStateException(error);
            }
        }

        public void setValue(Object value) {
            try {
                if(!value.equals(field.get(mod))) {
                    if(mod.onOptionChange(field.getName(), value)) {
                        field.set(mod, value);
                        mod.postOptionChange(field.getName(), value);
                    }
                }
            }
            catch(IllegalArgumentException | IllegalAccessException error) {
                throw new IllegalStateException(error);
            }
        }

        public static List<Cached> get(Mod mod) {
            List<Cached> result = new ArrayList<Cached>();
            add(mod, mod.getClass(), result);
            return result;
        }

        private static List<Cached> add(Mod mod, Class<? extends Mod> clazz, List<Cached> list) {
            List<Field> fields = new ArrayList<Field>();

            for(Field field : clazz.getDeclaredFields()) {
                if(!fields.contains(field)) {
                    fields.add(field);
                }
            }

            for(Field field : clazz.getFields()) {
                if(!fields.contains(field)) {
                    fields.add(field);
                }
            }

            for(Field field : fields) {
                if(field.isAnnotationPresent(ConfigOption.class)) {
                    if(list.stream().noneMatch((cached) -> cached.field.equals(field))) {
                        list.add(new Cached(mod, field.getAnnotation(ConfigOption.class), field));
                    }
                }
            }

            if(clazz.getSuperclass() != null && Mod.class.isAssignableFrom(clazz.getSuperclass())) {
                add(mod, (Class<? extends Mod>) clazz.getSuperclass(), list);
            }

            list.sort(Comparator.comparingInt(c -> -c.priority));

            return list;
        }

    }

}
