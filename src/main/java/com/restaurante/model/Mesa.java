package com.restaurante.model;

// Clase para guardar los datos de cada mesa
public class Mesa {
    public int numero = 0;
    public int capacidad = 0;
    public String zona = "";
    public int fila = 0;
    public int columna = 0;

    // Pasa la mesa a una linea de texto para el archivo
    public String toTxt() {
        return numero + "|" + capacidad + "|" + zona + "|" + fila + "|" + columna;
    }

    // Crea una mesa a partir de una linea del archivo
    public static Mesa fromTxt(String linea) {
        String[] partes = linea.split("\\|");
        Mesa mesa = new Mesa();
        mesa.numero = Integer.parseInt(partes[0]);
        mesa.capacidad = Integer.parseInt(partes[1]);
        mesa.zona = partes[2];
        mesa.fila = Integer.parseInt(partes[3]);
        mesa.columna = Integer.parseInt(partes[4]);
        return mesa;
    }
}
