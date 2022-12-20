package org.fpm.di.example;

import org.fpm.di.Binder;

import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class DummyBinder implements Binder {

    private final ArrayList<Class<?>> classList = new ArrayList<>();
    private final HashMap<Class<?>, Class<?>> implementationDependenciesMap = new HashMap<>();
    private final HashMap<Class<?>,Object> instanceMap = new HashMap<>();

    @Override
    public <T> void bind(Class<T> clazz) {
        if(clazz.getAnnotation(Singleton.class) != null){
            try {
                Constructor<T> constructor = clazz.getConstructor();
                bind(clazz, constructor.newInstance());
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        classList.add(clazz);
    }


    @Override
    public <T> void bind(Class<T> clazz, Class<? extends T> implementation) {
        implementationDependenciesMap.put(clazz, implementation);
    }

    @Override
    public <T> void bind(Class<T> clazz, T instance) {
        instanceMap.put(clazz, instance);
    }

    public<T> Class<T> getClass(Class<T> initial){
        if(classList.contains(initial))
            return initial;
        return null;
    }

    public<T> Class<? extends T> getImplementation(Class<T> baseClass){
        if(implementationDependenciesMap.containsKey(baseClass))
            return (Class<? extends T>) implementationDependenciesMap.get(baseClass);
        return null;
    }

    public<T> T getSingleton(Class<T> baseClass){
        if(instanceMap.containsKey(baseClass))
            return (T) instanceMap.get(baseClass);
        return null;
    }
}
