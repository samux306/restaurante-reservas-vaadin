package com.restaurante.vistas;

import com.restaurante.datos.GestorArchivos;
import com.restaurante.model.Mesa;
import com.restaurante.model.Reserva;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.ArrayList;

// Pagina de inicio con un resumen
@Route(value = "", layout = MainLayout.class)
@PageTitle("Inicio")
public class InicioView extends VerticalLayout {

    public InicioView() {
        ArrayList<Mesa> mesas = GestorArchivos.leerMesas();
        ArrayList<Reserva> reservas = GestorArchivos.leerReservas();

        add(new H1("Bienvenido"));
        add(new Paragraph("Sistema para manejar las reservas del restaurante."));
        add(new Paragraph("Mesas registradas: " + mesas.size()));
        add(new Paragraph("Reservas registradas: " + reservas.size()));
        add(new Paragraph("Reservas para hoy: " + contarReservasDeHoy(reservas)));
    }

    // Cuenta cuantas reservas hay para la fecha de hoy
    public int contarReservasDeHoy(ArrayList<Reserva> reservas) {
        LocalDate hoy = LocalDate.now();
        int contador = 0;
        for (Reserva reserva : reservas) {
            if (reserva.fecha.equals(hoy)) {
                contador++;
            }
        }
        return contador;
    }
}
