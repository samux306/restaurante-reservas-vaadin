package com.restaurante.vistas;

import com.restaurante.datos.GestorArchivos;
import com.restaurante.model.Mesa;
import com.restaurante.model.Reserva;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

// Plano del restaurante como una cuadricula de botones (como el Buscaminas)
@Route(value = "plano", layout = MainLayout.class)
@PageTitle("Plano")
public class PlanoView extends VerticalLayout {

    public ArrayList<Mesa> mesas;
    public ArrayList<Reserva> reservas;
    public DatePicker dpFecha = new DatePicker("Fecha");
    public TimePicker tpHora = new TimePicker("Hora");
    public Paragraph resumen = new Paragraph();
    public VerticalLayout planoLayout = new VerticalLayout();

    public PlanoView() {
        mesas = GestorArchivos.leerMesas();
        reservas = GestorArchivos.leerReservas();

        H2 titulo = new H2("Plano del restaurante");

        // Fecha y hora que se quieren revisar
        dpFecha.setValue(LocalDate.now());
        tpHora.setStep(Duration.ofMinutes(30));
        tpHora.setMin(Reserva.HORA_APERTURA);
        tpHora.setMax(Reserva.HORA_CIERRE);
        tpHora.setValue(Reserva.HORA_APERTURA);

        // Cada vez que cambia la fecha o la hora se vuelve a dibujar el plano
        dpFecha.addValueChangeListener(e -> dibujarPlano());
        tpHora.addValueChangeListener(e -> dibujarPlano());

        HorizontalLayout controles = new HorizontalLayout(dpFecha, tpHora);
        controles.getStyle().set("flex-wrap", "wrap"); // en el celular bajan a la siguiente linea
        Paragraph leyenda = new Paragraph("🟩 Libre    🟥 Reservada    (toque una mesa para ver sus reservas del día)");

        planoLayout.setPadding(false);
        planoLayout.setSpacing(false);

        add(titulo, controles, leyenda, resumen, planoLayout);
        dibujarPlano();
    }

    // Dibuja la cuadricula: un boton por cada posicion del plano
    public void dibujarPlano() {
        planoLayout.removeAll();

        LocalDate fecha = dpFecha.getValue();
        LocalTime hora = tpHora.getValue();
        if (fecha == null || hora == null) {
            resumen.setText("Elija la fecha y la hora para ver el plano");
            return;
        }

        // Matriz con la mesa que hay en cada posicion (null si no hay mesa)
        // Se resta 1 porque las filas y columnas de las mesas empiezan en 1 y la matriz en 0
        Mesa[][] plano = new Mesa[Mesa.MAX_FILAS][Mesa.MAX_COLUMNAS];
        for (Mesa mesa : mesas) {
            plano[mesa.fila - 1][mesa.columna - 1] = mesa;
        }

        int libres = 0;
        for (int f = 0; f < Mesa.MAX_FILAS; f++) {
            HorizontalLayout filaLayout = new HorizontalLayout();
            filaLayout.setSpacing(false);

            for (int c = 0; c < Mesa.MAX_COLUMNAS; c++) {
                Mesa mesa = plano[f][c];
                Button boton = new Button();
                boton.setWidth("56px");
                boton.setHeight("56px");
                boton.getStyle().set("margin", "3px");

                if (mesa == null) {
                    // Posicion vacia: espacio libre del restaurante, sin texto y desactivado
                    boton.setEnabled(false);
                    boton.getStyle().set("background-color", "transparent");
                    boton.getStyle().set("border", "1px dashed #cccccc");
                } else {
                    boton.setText(String.valueOf(mesa.numero));
                    boton.getStyle().set("color", "white");
                    boton.getStyle().set("font-weight", "bold");

                    if (estaReservada(mesa.numero, fecha, hora)) {
                        boton.getStyle().set("background-color", "#e53935"); // rojo
                    } else {
                        boton.getStyle().set("background-color", "#43a047"); // verde
                        libres++;
                    }
                    crearPopover(boton, mesa, fecha);
                }
                filaLayout.add(boton);
            }
            planoLayout.add(filaLayout);
        }

        resumen.setText("Mesas libres el " + fecha + " a las " + hora + ": " + libres + " de " + mesas.size());
    }

    // Revisa si la mesa tiene una reserva en esa fecha y hora
    public boolean estaReservada(int numeroMesa, LocalDate fecha, LocalTime hora) {
        for (Reserva reserva : reservas) {
            if (reserva.numeroMesa == numeroMesa && reserva.fecha.equals(fecha) && reserva.hora.equals(hora)) {
                return true;
            }
        }
        return false;
    }

    // Popover que se abre al tocar la mesa y muestra sus reservas de ese dia
    public void crearPopover(Button boton, Mesa mesa, LocalDate fecha) {
        Popover popover = new Popover();
        popover.setTarget(boton);

        popover.add(new H4("Mesa " + mesa.numero));
        popover.add(new Paragraph(mesa.capacidad + " personas - " + mesa.zona));

        int cantidad = 0;
        for (Reserva reserva : reservas) {
            if (reserva.numeroMesa == mesa.numero && reserva.fecha.equals(fecha)) {
                String estado = reserva.confirmada ? "Confirmada" : "Pendiente";
                popover.add(new Paragraph(reserva.hora + " - " + reserva.cliente + " ("
                        + reserva.personas + " pers.) - " + estado));
                cantidad++;
            }
        }
        if (cantidad == 0) {
            popover.add(new Paragraph("Sin reservas este día"));
        }
    }
}
