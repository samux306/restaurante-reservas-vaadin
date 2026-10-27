package com.restaurante.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {
    private String id;
    private String cliente;
    private String telefono;
    private LocalDate fecha;
    private LocalTime hora;
    private int personas;
    private int numeroMesa;
    private boolean confirmada;

    public Reserva() {}

    public Reserva(String id, String cliente, String telefono, LocalDate fecha, LocalTime hora, int personas, int numeroMesa, boolean confirmada) {
        this.id = id;
        this.cliente = cliente;
        this.telefono = telefono;
        this.fecha = fecha;
        this.hora = hora;
        this.personas = personas;
        this.numeroMesa = numeroMesa;
        this.confirmada = confirmada;
    }

    // Convierte el objeto a formato TXT separado por |
    public String toTxt() {
        return id + "|" + cliente + "|" + telefono + "|" + fecha + "|" + hora + "|" + personas + "|" + numeroMesa + "|" + confirmada;
    }

    // Crea el objeto desde una línea del archivo TXT
    public static Reserva fromTxt(String linea) {
        String[] p = linea.split("\\|");
        return new Reserva(
            p[0],
            p[1],
            p[2],
            LocalDate.parse(p[3]),
            LocalTime.parse(p[4]),
            Integer.parseInt(p[5]),
            Integer.parseInt(p[6]),
            Boolean.parseBoolean(p[7])
        );
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public int getPersonas() { return personas; }
    public void setPersonas(int personas) { this.personas = personas; }
    public int getNumeroMesa() { return numeroMesa; }
    public void setNumeroMesa(int numeroMesa) { this.numeroMesa = numeroMesa; }
    public boolean isConfirmada() { return confirmada; }
    public void setConfirmada(boolean confirmada) { this.confirmada = confirmada; }
}