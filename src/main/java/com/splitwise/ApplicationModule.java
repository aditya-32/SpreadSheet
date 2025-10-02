package com.splitwise;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.splitwise.dao.DaoModule;

public class ApplicationModule extends AbstractModule {

    public void configure() {
        binder().requireAtInjectOnConstructors();
        binder().requireExplicitBindings();
        binder().requireExactBindingAnnotations();
        binder().disableCircularProxies();


        install(new DaoModule());
        bind(Splitwise.class).in(Scopes.SINGLETON);
    }
}
