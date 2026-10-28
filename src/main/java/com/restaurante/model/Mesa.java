package com.restaurante.model;

// Una mesa del restaurante.
// Es el "molde" con el que se crea cada mesa (como la clase Celda del Buscaminas).
public class Mesa {
    public int numero;
    public int capacidad;
    public String zona; // "Salón" o "Terraza"
    public int fila;    // posición en el plano
    public int columna;

    public Mesa(int numero, int capacidad, String zona, int fila, int columna) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.zona = zona;
        this.fila = fila;
        this.columna = columna;
    }

    // Convierte la mesa en una línea de texto para guardarla en el archivo
    // Ejemplo: 3|4|Salón|0|2
    public String toTxt() {
        return numero + "|" + capacidad + "|" + zona + "|" + fila + "|" + columna;
    }

    // Hace lo contrario: recibe una línea del archivo y crea la mesa
    public static Mesa fromTxt(String linea) {
        String[] partes = linea.split("\\|");
        int numero = Integer.parseInt(partes[0]);
        int capacidad = Integer.parseInt(partes[1]);
        String zona = partes[2];
        int fila = Integer.parseInt(partes[3]);
        int columna = Integer.parseInt(partes[4]);
        return new Mesa(numero, capacidad, zona, fila, columna);
    }
}
