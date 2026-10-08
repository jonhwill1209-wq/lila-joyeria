package com.lilajoyeria.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class PersistenciaListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent evento) {
        JPAUtil.cerrar();
    }
}
