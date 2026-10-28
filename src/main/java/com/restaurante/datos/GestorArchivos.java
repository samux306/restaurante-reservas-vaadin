package com.restaurante.datos;

import com.restaurante.model.Mesa;
import com.restaurante.model.Reserva;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

// Clase que lee y guarda los datos en los archivos .txt
public class GestorArchivos {

    public static final String ARCHIVO_MESAS = "mesas.txt";
    public static final String ARCHIVO_RESERVAS = "reservas.txt";

    // Lee las mesas del archivo
    public static ArrayList<Mesa> leerMesas() {
        ArrayList<Mesa> mesas = new ArrayList<>();

        // Si el archivo no existe es la primera vez, entonces se crean datos de ejemplo
        File archivo = new File(ARCHIVO_MESAS);
        if (!archivo.exists()) {
            mesas = mesasDeEjemplo();
            guardarMesas(mesas);
            return mesas;
        }

        try (FileReader fr = new FileReader(ARCHIVO_MESAS);
             BufferedReader br = new BufferedReader(fr)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Si una linea esta mal escrita se salta y sigue con la otra
                try {
                    mesas.add(Mesa.fromTxt(linea));
                } catch (Exception e) {
                    System.err.println("Linea mala en mesas.txt: " + linea);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer: " + e.getMessage());
        }
        return mesas;
    }

    // Guarda todas las mesas en el archivo
    public static void guardarMesas(ArrayList<Mesa> mesas) {
        try (FileWriter fw = new FileWriter(ARCHIVO_MESAS);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (Mesa mesa : mesas) {
                out.println(mesa.toTxt());
            }
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }

    // Lee las reservas del archivo
    public static ArrayList<Reserva> leerReservas() {
        ArrayList<Reserva> reservas = new ArrayList<>();

        File archivo = new File(ARCHIVO_RESERVAS);
        if (!archivo.exists()) {
            reservas = reservasDeEjemplo();
            guardarReservas(reservas);
            return reservas;
        }

        try (FileReader fr = new FileReader(ARCHIVO_RESERVAS);
             BufferedReader br = new BufferedReader(fr)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                try {
                    reservas.add(Reserva.fromTxt(linea));
                } catch (Exception e) {
                    System.err.println("Linea mala en reservas.txt: " + linea);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer: " + e.getMessage());
        }
        return reservas;
    }

    // Guarda todas las reservas en el archivo
    public static void guardarReservas(ArrayList<Reserva> reservas) {
        try (FileWriter fw = new FileWriter(ARCHIVO_RESERVAS);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            for (Reserva reserva : reservas) {
                out.println(reserva.toTxt());
            }
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }

    // Mesas que se crean la primera vez que se abre la aplicacion
    public static ArrayList<Mesa> mesasDeEjemplo() {
        String[] lineas = {
            "1|2|Salón|0|0",
            "2|2|Salón|0|1",
            "3|4|Salón|0|2",
            "4|4|Salón|0|3",
            "5|6|Salón|1|0",
            "6|4|Salón|1|1",
            "7|8|Salón|1|3",
            "8|2|Terraza|2|0",
            "9|4|Terraza|2|1",
            "10|6|Terraza|2|3"
        };
        ArrayList<Mesa> mesas = new ArrayList<>();
        for (String linea : lineas) {
            mesas.add(Mesa.fromTxt(linea));
        }
        return mesas;
    }

    // Reservas que se crean la primera vez que se abre la aplicacion
    public static ArrayList<Reserva> reservasDeEjemplo() {
        LocalDate hoy = LocalDate.now();
        LocalDate manana = hoy.plusDays(1);

        String[] lineas = {
            "1|Ana Gómez|3001234567|" + hoy + "|12:30|2|1|true",
            "2|Carlos Pérez|3109876543|" + hoy + "|13:00|4|3|false",
            "3|Ana Gómez|3001234567|" + hoy + "|19:00|3|4|true",
            "4|Luisa Martínez|3205551234|" + hoy + "|20:00|6|5|false",
            "5|Ana Gómez|3001234567|" + manana + "|19:30|2|8|true",
            "6|Carlos Pérez|3109876543|" + manana + "|20:00|7|7|false",
            "7|Jorge Ríos|3157778899|" + manana + "|13:00|5|10|true"
        };
        ArrayList<Reserva> reservas = new ArrayList<>();
        for (String linea : lineas) {
            reservas.add(Reserva.fromTxt(linea));
        }
        return reservas;
    }
}
