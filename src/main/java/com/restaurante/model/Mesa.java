package com.restaurante.model;

public class Mesa {
    private int numero;
    private int capacidad;
    private String zona; // "Salón" o "Terraza"
    private int fila;
    private int columna;

    public Mesa() {}

    public Mesa(int numero, int capacidad, String zona, int fila, int columna) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.zona = zona;
        this.fila = fila;
        this.columna = columna;
    }

    // Convierte el objeto a formato TXT separado por |
    public String toTxt() {
        return numero + "|" + capacidad + "|" + zona + "|" + fila + "|" + columna;
    }

    // Crea el objeto desde una línea del archivo TXT
    public static Mesa fromTxt(String linea) {
        String[] p = linea.split("\\|");
        return new Mesa(
            Integer.parseInt(p[0]),
            Integer.parseInt(p[1]),
            p[2],
            Integer.parseInt(p[3]),
            Integer.parseInt(p[4])
        );
    }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }
    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }
}