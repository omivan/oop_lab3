package org.fpm.di.example;

import org.fpm.di.Container;

import javax.inject.Inject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DummyContainer implements Container {
    DummyBinder dummyBinder;

    public DummyContainer(DummyBinder dummyBinder) {
        this.dummyBinder = dummyBinder;
    }

    @Override
    public <T> T getComponent(Class<T> clazz) {
        T singletonObject = dummyBinder.getSingleton(clazz);
        if(singletonObject != null) return singletonObject;

        if(dummyBinder.getClass(clazz) != null) return getGenericObject(clazz);

        Class<? extends T> extendedClass = dummyBinder.getImplementation(clazz);
        if(extendedClass != null) return getGenericObject(clazz, extendedClass);

        return null;
    }



    private <T> T getGenericObject(Class<T> clazz, Class<? extends T> extendedClass){
        T parentObject = getComponent(extendedClass);
        if(parentObject != null){
            return parentObject;
        }
        return getGenericObject(extendedClass);
    }

    private <T> T getGenericObject(Class<T> clazz){
        T resultObject;
        resultObject = getInjectObject(clazz);

        if (resultObject != null){
            return resultObject;
        }

        return getNonInjectObject(clazz);

    }
    private<T> T getInjectObject(Class<T> clazz){
        for(Constructor<?> constructor: clazz.getConstructors()) {
            if(constructor.getAnnotation(Inject.class) != null){
                try {
                    return (T) constructor.newInstance(getComponent(constructor.getParameterTypes()[0]));
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        return null;
    }

    private<T> T getNonInjectObject(Class<T> clazz){
        try {
            Constructor<T> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
