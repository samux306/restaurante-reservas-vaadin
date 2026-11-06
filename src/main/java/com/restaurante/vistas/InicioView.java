package com.restaurante.vistas;

import com.restaurante.datos.GestorArchivos;
import com.restaurante.model.Mesa;
import com.restaurante.model.Reserva;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
        add(new Paragraph("Sistema para manejar las reservas del restaurante. Use el menú para ver las mesas, "
                + "las reservas, el plano y la ocupación del día."));

        // Tarjetas con el resumen
        HorizontalLayout tarjetas = new HorizontalLayout(
                crearTarjeta("Mesas", mesas.size()),
                crearTarjeta("Reservas", reservas.size()),
                crearTarjeta("Para hoy", contarReservasDeHoy(reservas)),
                crearTarjeta("Pendientes", contarPendientes(reservas)));
        tarjetas.getStyle().set("flex-wrap", "wrap"); // en el celular bajan a la siguiente linea
        add(tarjetas);
    }

    // Crea un recuadro con un numero grande y su descripcion
    public VerticalLayout crearTarjeta(String texto, int numero) {
        H2 valor = new H2(String.valueOf(numero));
        valor.getStyle().set("margin", "0");
        Span descripcion = new Span(texto);

        VerticalLayout tarjeta = new VerticalLayout(valor, descripcion);
        tarjeta.setAlignItems(Alignment.CENTER);
        tarjeta.setWidth("140px");
        tarjeta.getStyle().set("border", "1px solid #dddddd");
        tarjeta.getStyle().set("border-radius", "12px");
        tarjeta.getStyle().set("background-color", "white");
        return tarjeta;
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

    // Cuenta cuantas reservas estan sin confirmar
    public int contarPendientes(ArrayList<Reserva> reservas) {
        int contador = 0;
        for (Reserva reserva : reservas) {
            if (!reserva.confirmada) {
                contador++;
            }
        }
        return contador;
    }
}
