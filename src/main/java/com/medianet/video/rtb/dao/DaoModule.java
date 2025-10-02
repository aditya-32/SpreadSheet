package com.medianet.video.rtb.dao;

import com.google.inject.PrivateModule;
import com.google.inject.Scopes;

public class DaoModule extends PrivateModule {
    @Override
    protected void configure() {
        binder().requireAtInjectOnConstructors();
        binder().requireExplicitBindings();
        binder().requireExactBindingAnnotations();
        binder().disableCircularProxies();

        bind(UserDao.class).in(Scopes.SINGLETON);
        bind(GroupDao.class).in(Scopes.SINGLETON);
        expose(GroupDao.class);
        expose(UserDao.class);
    }
}
