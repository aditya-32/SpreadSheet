package com.medianet.video.rtb;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.medianet.video.rtb.dao.DaoModule;

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
