package com.restaurante.model;

import java.time.LocalDate;
import java.time.LocalTime;

// Clase para guardar los datos de cada reserva
public class Reserva {
    // Horario del restaurante
    public static final LocalTime HORA_APERTURA = LocalTime.of(12, 0);
    public static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    public int id = 0;
    public String cliente = "";
    public String telefono = "";
    public LocalDate fecha;
    public LocalTime hora;
    public int personas = 0;
    public int numeroMesa = 0;
    public boolean confirmada = false;

    // Pasa la reserva a una linea de texto para el archivo
    public String toTxt() {
        return id + "|" + cliente + "|" + telefono + "|" + fecha + "|" + hora + "|"
                + personas + "|" + numeroMesa + "|" + confirmada;
    }

    // Crea una reserva a partir de una linea del archivo
    public static Reserva fromTxt(String linea) {
        String[] partes = linea.split("\\|");
        Reserva reserva = new Reserva();
        reserva.id = Integer.parseInt(partes[0]);
        reserva.cliente = partes[1];
        reserva.telefono = partes[2];
        reserva.fecha = LocalDate.parse(partes[3]);
        reserva.hora = LocalTime.parse(partes[4]);
        reserva.personas = Integer.parseInt(partes[5]);
        reserva.numeroMesa = Integer.parseInt(partes[6]);
        reserva.confirmada = Boolean.parseBoolean(partes[7]);
        return reserva;
    }
}
