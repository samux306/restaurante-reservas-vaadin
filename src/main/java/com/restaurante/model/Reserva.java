package com.restaurante.model;

import java.time.LocalDate;
import java.time.LocalTime;

// Una reserva de una mesa para una fecha y hora.
public class Reserva {
    public int id;
    public String cliente;
    public String telefono;
    public LocalDate fecha;
    public LocalTime hora;
    public int personas;
    public int numeroMesa;
    public boolean confirmada; // false = pendiente, true = confirmada

    public Reserva(int id, String cliente, String telefono, LocalDate fecha, LocalTime hora,
                   int personas, int numeroMesa, boolean confirmada) {
        this.id = id;
        this.cliente = cliente;
        this.telefono = telefono;
        this.fecha = fecha;
        this.hora = hora;
        this.personas = personas;
        this.numeroMesa = numeroMesa;
        this.confirmada = confirmada;
    }

    // Convierte la reserva en una línea de texto para guardarla en el archivo
    // Ejemplo: 1|Ana Gómez|3001234567|2026-10-27|19:00|4|3|true
    public String toTxt() {
        return id + "|" + cliente + "|" + telefono + "|" + fecha + "|" + hora + "|"
                + personas + "|" + numeroMesa + "|" + confirmada;
    }

    // Hace lo contrario: recibe una línea del archivo y crea la reserva
    public static Reserva fromTxt(String linea) {
        String[] partes = linea.split("\\|");
        int id = Integer.parseInt(partes[0]);
        String cliente = partes[1];
        String telefono = partes[2];
        LocalDate fecha = LocalDate.parse(partes[3]);  // "2026-10-27" -> fecha
        LocalTime hora = LocalTime.parse(partes[4]);   // "19:00" -> hora
        int personas = Integer.parseInt(partes[5]);
        int numeroMesa = Integer.parseInt(partes[6]);
        boolean confirmada = Boolean.parseBoolean(partes[7]);
        return new Reserva(id, cliente, telefono, fecha, hora, personas, numeroMesa, confirmada);
    }
}
