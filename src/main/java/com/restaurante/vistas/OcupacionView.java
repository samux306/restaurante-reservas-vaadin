package com.restaurante.vistas;

import com.restaurante.datos.GestorArchivos;
import com.restaurante.model.Mesa;
import com.restaurante.model.Reserva;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.ArrayList;

// Ocupacion del dia: todas las reservas de una fecha
@Route(value = "ocupacion", layout = MainLayout.class)
@PageTitle("Ocupación del día")
public class OcupacionView extends VerticalLayout {

    public ArrayList<Reserva> reservas;
    public ArrayList<Mesa> mesas;
    public DatePicker dpFecha = new DatePicker("Fecha");
    public Paragraph resumen = new Paragraph();
    public Grid<Reserva> grid = new Grid<>();

    public OcupacionView() {
        reservas = GestorArchivos.leerReservas();
        mesas = GestorArchivos.leerMesas();

        H2 titulo = new H2("Ocupación del día");

        dpFecha.setValue(LocalDate.now());
        dpFecha.addValueChangeListener(e -> actualizarOcupacion());

        // Columnas de la tabla
        grid.addColumn(reserva -> reserva.hora).setHeader("Hora");
        grid.addColumn(reserva -> reserva.numeroMesa).setHeader("Mesa");
        grid.addColumn(reserva -> zonaDeMesa(reserva.numeroMesa)).setHeader("Zona");
        grid.addColumn(reserva -> reserva.cliente).setHeader("Cliente");
        grid.addColumn(reserva -> reserva.telefono).setHeader("Teléfono");
        grid.addColumn(reserva -> reserva.personas).setHeader("Personas");
        grid.addColumn(reserva -> reserva.confirmada ? "Confirmada" : "Pendiente").setHeader("Estado");
        grid.setAllRowsVisible(true); // la tabla crece con las filas en vez de tener su propia barra

        add(titulo, dpFecha, resumen, grid);
        actualizarOcupacion();
    }

    // Busca las reservas de la fecha elegida, las ordena por hora y calcula los totales
    public void actualizarOcupacion() {
        LocalDate fecha = dpFecha.getValue();
        if (fecha == null) {
            grid.setItems(new ArrayList<>());
            resumen.setText("Elija una fecha");
            return;
        }

        ArrayList<Reserva> delDia = new ArrayList<>();
        int personas = 0;
        int confirmadas = 0;
        for (Reserva reserva : reservas) {
            if (reserva.fecha.equals(fecha)) {
                delDia.add(reserva);
                personas += reserva.personas;
                if (reserva.confirmada) {
                    confirmadas++;
                }
            }
        }
        int pendientes = delDia.size() - confirmadas;

        ordenarPorHora(delDia);
        grid.setItems(delDia);

        resumen.setText("Reservas: " + delDia.size() + "  |  Personas: " + personas
                + "  |  Confirmadas: " + confirmadas + "  |  Pendientes: " + pendientes);
    }

    // Ordenamiento burbuja: deja las reservas de la hora mas temprana a la mas tarde
    public void ordenarPorHora(ArrayList<Reserva> lista) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).hora.isAfter(lista.get(j + 1).hora)) {
                    Reserva temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
    }

    // Devuelve la zona de una mesa buscandola por su numero
    public String zonaDeMesa(int numeroMesa) {
        for (Mesa mesa : mesas) {
            if (mesa.numero == numeroMesa) {
                return mesa.zona;
            }
        }
        return "";
    }
}
