package org.apache.commons.compress.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class ServiceLoaderIterator<E> implements Iterator<E> {
    private E nextServiceLoader;
    private final Class<E> service;
    private final Iterator<E> serviceLoaderIterator;

    public ServiceLoaderIterator(Class<E> service2) {
        this(service2, ClassLoader.getSystemClassLoader());
    }

    public ServiceLoaderIterator(Class<E> service2, ClassLoader classLoader) {
        this.service = service2;
        this.serviceLoaderIterator = ServiceLoader.load(service2, classLoader).iterator();
        this.nextServiceLoader = null;
    }

    private boolean getNextServiceLoader() {
        while (this.nextServiceLoader == null) {
            try {
                if (!this.serviceLoaderIterator.hasNext()) {
                    return false;
                }
                this.nextServiceLoader = this.serviceLoaderIterator.next();
            } catch (ServiceConfigurationError e) {
                if (!(e.getCause() instanceof SecurityException)) {
                    throw e;
                }
            }
        }
        return true;
    }

    public boolean hasNext() {
        return getNextServiceLoader();
    }

    public E next() {
        if (getNextServiceLoader()) {
            E tempNext = this.nextServiceLoader;
            this.nextServiceLoader = null;
            return tempNext;
        }
        throw new NoSuchElementException("No more elements for service " + this.service.getName());
    }

    public void remove() {
        throw new UnsupportedOperationException("service=" + this.service.getName());
    }
}
