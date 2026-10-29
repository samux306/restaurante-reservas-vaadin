package com.restaurante.vistas;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

// Layout con el menu que comparten todas las vistas
public class MainLayout extends AppLayout {

    public MainLayout() {
        // Barra de arriba: boton para abrir/cerrar el menu y el titulo
        DrawerToggle botonMenu = new DrawerToggle();
        H3 titulo = new H3("Reservas Restaurante");
        addToNavbar(botonMenu, titulo);

        // Menu de la izquierda
        Button btnInicio = new Button("Inicio", e -> UI.getCurrent().navigate(InicioView.class));
        btnInicio.setWidthFull();

        VerticalLayout menu = new VerticalLayout(btnInicio);
        addToDrawer(menu);
    }
}
