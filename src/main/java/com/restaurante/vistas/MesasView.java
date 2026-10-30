package com.restaurante.vistas;

import com.restaurante.datos.GestorArchivos;
import com.restaurante.model.Mesa;
import com.restaurante.model.Reserva;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;

// Vista para ver, crear, editar y eliminar mesas
@Route(value = "mesas", layout = MainLayout.class)
@PageTitle("Mesas")
public class MesasView extends VerticalLayout {

    public ArrayList<Mesa> mesas;
    public Grid<Mesa> grid = new Grid<>();

    public MesasView() {
        mesas = GestorArchivos.leerMesas();

        H2 titulo = new H2("Mesas");
        Button btnNueva = new Button("Nueva mesa", VaadinIcon.PLUS.create(), e -> abrirFormulario(null));

        // Columnas de la tabla
        grid.addColumn(mesa -> mesa.numero).setHeader("Número");
        grid.addColumn(mesa -> mesa.capacidad).setHeader("Capacidad");
        grid.addColumn(mesa -> mesa.zona).setHeader("Zona");
        grid.addColumn(mesa -> mesa.fila).setHeader("Fila");
        grid.addColumn(mesa -> mesa.columna).setHeader("Columna");
        grid.addComponentColumn(mesa -> crearBotones(mesa)).setHeader("Acciones");

        add(titulo, btnNueva, grid);
        actualizarGrid();
    }

    // Vuelve a cargar las mesas en la tabla
    public void actualizarGrid() {
        grid.setItems(mesas);
    }

    // Botones de editar y eliminar de cada fila
    public HorizontalLayout crearBotones(Mesa mesa) {
        Button btnEditar = new Button(VaadinIcon.EDIT.create(), e -> abrirFormulario(mesa));
        Button btnEliminar = new Button(VaadinIcon.TRASH.create(), e -> confirmarEliminar(mesa));
        btnEliminar.getStyle().set("color", "red");
        return new HorizontalLayout(btnEditar, btnEliminar);
    }

    // Abre el formulario. Si mesa es null es una mesa nueva, si no se edita esa mesa
    public void abrirFormulario(Mesa mesa) {
        Dialog dialogo = new Dialog();

        TextField txtNumero = new TextField("Número");
        TextField txtCapacidad = new TextField("Capacidad (personas)");
        ComboBox<String> cmbZona = new ComboBox<>("Zona");
        cmbZona.setItems("Salón", "Terraza");
        TextField txtFila = new TextField("Fila en el plano (1 a " + Mesa.MAX_FILAS + ")");
        TextField txtColumna = new TextField("Columna en el plano (1 a " + Mesa.MAX_COLUMNAS + ")");

        if (mesa == null) {
            dialogo.setHeaderTitle("Nueva mesa");
        } else {
            dialogo.setHeaderTitle("Editar mesa " + mesa.numero);
            txtNumero.setValue(String.valueOf(mesa.numero));
            txtNumero.setReadOnly(true); // el numero no se cambia porque las reservas lo usan
            txtCapacidad.setValue(String.valueOf(mesa.capacidad));
            cmbZona.setValue(mesa.zona);
            txtFila.setValue(String.valueOf(mesa.fila));
            txtColumna.setValue(String.valueOf(mesa.columna));
        }

        FormLayout formulario = new FormLayout(txtNumero, txtCapacidad, cmbZona, txtFila, txtColumna);
        dialogo.add(formulario);

        Button btnCancelar = new Button("Cancelar", e -> dialogo.close());
        Button btnGuardar = new Button("Guardar", e -> {
            boolean guardado = guardarMesa(mesa, txtNumero.getValue(), txtCapacidad.getValue(),
                    cmbZona.getValue(), txtFila.getValue(), txtColumna.getValue());
            if (guardado) {
                dialogo.close();
            }
        });
        dialogo.getFooter().add(btnCancelar, btnGuardar);
        dialogo.open();
    }

    // Valida los datos y guarda la mesa. Devuelve true si se pudo guardar
    public boolean guardarMesa(Mesa mesa, String textoNumero, String textoCapacidad, String zona,
                               String textoFila, String textoColumna) {
        try {
            int numero = Integer.parseInt(textoNumero.trim());
            int capacidad = Integer.parseInt(textoCapacidad.trim());
            int fila = Integer.parseInt(textoFila.trim());
            int columna = Integer.parseInt(textoColumna.trim());

            if (numero <= 0) {
                throw new IllegalArgumentException("El número de la mesa debe ser mayor a 0");
            }
            if (capacidad < 1 || capacidad > Mesa.MAX_CAPACIDAD) {
                throw new IllegalArgumentException("La capacidad debe estar entre 1 y " + Mesa.MAX_CAPACIDAD);
            }
            if (zona == null) {
                throw new IllegalArgumentException("Elija la zona de la mesa");
            }
            if (fila < 1 || fila > Mesa.MAX_FILAS) {
                throw new IllegalArgumentException("La fila debe estar entre 1 y " + Mesa.MAX_FILAS);
            }
            if (columna < 1 || columna > Mesa.MAX_COLUMNAS) {
                throw new IllegalArgumentException("La columna debe estar entre 1 y " + Mesa.MAX_COLUMNAS);
            }

            // Revisa que no se repita el numero ni la posicion con otra mesa
            for (Mesa otra : mesas) {
                if (otra != mesa && otra.numero == numero) {
                    throw new IllegalArgumentException("Ya existe una mesa con el número " + numero);
                }
                if (otra != mesa && otra.fila == fila && otra.columna == columna) {
                    throw new IllegalArgumentException("En esa posición ya está la mesa " + otra.numero);
                }
            }

            // Si es nueva se crea y se agrega a la lista
            if (mesa == null) {
                mesa = new Mesa();
                mesas.add(mesa);
            }
            mesa.numero = numero;
            mesa.capacidad = capacidad;
            mesa.zona = zona;
            mesa.fila = fila;
            mesa.columna = columna;

            GestorArchivos.guardarMesas(mesas);
            actualizarGrid();
            Notification.show("Mesa " + numero + " guardada");
            return true;

        } catch (NumberFormatException e) {
            Notification.show("Número, capacidad, fila y columna deben ser números enteros");
        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage());
        }
        return false;
    }

    // Pregunta antes de eliminar la mesa
    public void confirmarEliminar(Mesa mesa) {
        int cantidad = contarReservasDeMesa(mesa.numero);
        if (cantidad > 0) {
            Notification.show("No se puede eliminar: la mesa " + mesa.numero + " tiene " + cantidad + " reserva(s)");
            return;
        }

        ConfirmDialog confirmar = new ConfirmDialog();
        confirmar.setHeader("¿Eliminar la mesa " + mesa.numero + "?");
        confirmar.setText("Esta acción no se puede deshacer.");
        confirmar.setCancelable(true);
        confirmar.setCancelText("Cancelar");
        confirmar.setConfirmText("Eliminar");
        confirmar.addConfirmListener(e -> eliminarMesa(mesa));
        confirmar.open();
    }

    // Quita la mesa de la lista y guarda el archivo
    public void eliminarMesa(Mesa mesa) {
        mesas.remove(mesa);
        GestorArchivos.guardarMesas(mesas);
        actualizarGrid();
        Notification.show("Mesa " + mesa.numero + " eliminada");
    }

    // Cuenta cuantas reservas tiene una mesa
    public int contarReservasDeMesa(int numeroMesa) {
        ArrayList<Reserva> reservas = GestorArchivos.leerReservas();
        int contador = 0;
        for (Reserva reserva : reservas) {
            if (reserva.numeroMesa == numeroMesa) {
                contador++;
            }
        }
        return contador;
    }
}
