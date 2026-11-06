package com.restaurante.vistas;

import com.restaurante.datos.GestorArchivos;
import com.restaurante.model.Mesa;
import com.restaurante.model.Reserva;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

// Vista para ver, buscar, crear, editar, confirmar y cancelar reservas
@Route(value = "reservas", layout = MainLayout.class)
@PageTitle("Reservas")
public class ReservasView extends VerticalLayout {

    public ArrayList<Reserva> reservas;
    public ArrayList<Mesa> mesas;
    public Grid<Reserva> grid = new Grid<>();
    public TextField txtBuscar = new TextField();
    public ComboBox<String> cmbEstado = new ComboBox<>();

    public ReservasView() {
        reservas = GestorArchivos.leerReservas();
        mesas = GestorArchivos.leerMesas();

        H2 titulo = new H2("Reservas");
        Button btnNueva = new Button("Nueva reserva", VaadinIcon.PLUS.create(), e -> abrirFormulario(null));

        // Buscador: filtra cada vez que se escribe una letra
        txtBuscar.setPlaceholder("Buscar por cliente o teléfono");
        txtBuscar.setPrefixComponent(VaadinIcon.SEARCH.create());
        txtBuscar.setValueChangeMode(ValueChangeMode.EAGER);
        txtBuscar.addValueChangeListener(e -> actualizarGrid());

        // Filtro por estado
        cmbEstado.setItems("Todas", "Pendientes", "Confirmadas");
        cmbEstado.setValue("Todas");
        cmbEstado.addValueChangeListener(e -> actualizarGrid());

        btnNueva.addThemeVariants(ButtonVariant.PRIMARY);

        HorizontalLayout barra = new HorizontalLayout(btnNueva, txtBuscar, cmbEstado);
        barra.getStyle().set("flex-wrap", "wrap"); // en el celular bajan a la siguiente linea

        // Columnas de la tabla
        grid.addColumn(reserva -> reserva.cliente).setHeader("Cliente");
        grid.addColumn(reserva -> textoFrecuente(reserva)).setHeader("Frecuente");
        grid.addColumn(reserva -> reserva.telefono).setHeader("Teléfono");
        grid.addColumn(reserva -> reserva.fecha).setHeader("Fecha");
        grid.addColumn(reserva -> reserva.hora).setHeader("Hora");
        grid.addColumn(reserva -> reserva.personas).setHeader("Personas");
        grid.addColumn(reserva -> reserva.numeroMesa).setHeader("Mesa");
        grid.addColumn(reserva -> textoEstado(reserva)).setHeader("Estado");
        grid.addComponentColumn(reserva -> crearBotones(reserva)).setHeader("Acciones");
        grid.setAllRowsVisible(true); // la tabla crece con las filas en vez de tener su propia barra

        add(titulo, barra, grid);
        actualizarGrid();
    }

    // Muestra en la tabla solo las reservas que cumplen la busqueda y el filtro
    public void actualizarGrid() {
        String texto = txtBuscar.getValue().trim().toLowerCase();
        String estado = cmbEstado.getValue();

        ArrayList<Reserva> filtradas = new ArrayList<>();
        for (Reserva reserva : reservas) {
            // Busqueda: el nombre o el telefono deben contener lo escrito
            if (!reserva.cliente.toLowerCase().contains(texto) && !reserva.telefono.contains(texto)) {
                continue;
            }
            // Filtro por estado
            if ("Pendientes".equals(estado) && reserva.confirmada) {
                continue;
            }
            if ("Confirmadas".equals(estado) && !reserva.confirmada) {
                continue;
            }
            filtradas.add(reserva);
        }
        grid.setItems(filtradas);
    }

    public String textoEstado(Reserva reserva) {
        if (reserva.confirmada) {
            return "Confirmada";
        } else {
            return "Pendiente";
        }
    }

    public String textoFrecuente(Reserva reserva) {
        if (esClienteFrecuente(reserva.telefono)) {
            return "⭐ Sí";
        } else {
            return "";
        }
    }

    // Un cliente es frecuente si tiene 3 o mas reservas (se identifica por el telefono)
    public boolean esClienteFrecuente(String telefono) {
        int contador = 0;
        for (Reserva reserva : reservas) {
            if (reserva.telefono.equals(telefono)) {
                contador++;
            }
        }
        return contador >= 3;
    }

    // Botones de cada fila
    public HorizontalLayout crearBotones(Reserva reserva) {
        Button btnConfirmar = new Button(VaadinIcon.CHECK.create(), e -> confirmarReserva(reserva));
        btnConfirmar.getStyle().set("color", "green");
        btnConfirmar.setEnabled(!reserva.confirmada); // si ya esta confirmada no se puede volver a confirmar

        Button btnEditar = new Button(VaadinIcon.EDIT.create(), e -> abrirFormulario(reserva));

        Button btnCancelar = new Button(VaadinIcon.TRASH.create(), e -> preguntarCancelar(reserva));
        btnCancelar.getStyle().set("color", "red");

        return new HorizontalLayout(btnConfirmar, btnEditar, btnCancelar);
    }

    // Pasa la reserva de pendiente a confirmada
    public void confirmarReserva(Reserva reserva) {
        reserva.confirmada = true;
        GestorArchivos.guardarReservas(reservas);
        actualizarGrid();
        Notification.show("Reserva de " + reserva.cliente + " confirmada");
    }

    // Pregunta antes de cancelar la reserva
    public void preguntarCancelar(Reserva reserva) {
        ConfirmDialog confirmar = new ConfirmDialog();
        confirmar.setHeader("¿Cancelar la reserva?");
        confirmar.setText(reserva.cliente + " - " + reserva.fecha + " a las " + reserva.hora
                + " en la mesa " + reserva.numeroMesa + ". La reserva se va a eliminar.");
        confirmar.setCancelable(true);
        confirmar.setCancelText("No");
        confirmar.setConfirmText("Sí, cancelar");
        confirmar.addConfirmListener(e -> cancelarReserva(reserva));
        confirmar.open();
    }

    // Quita la reserva de la lista y guarda el archivo
    public void cancelarReserva(Reserva reserva) {
        reservas.remove(reserva);
        GestorArchivos.guardarReservas(reservas);
        actualizarGrid();
        Notification.show("Reserva de " + reserva.cliente + " cancelada");
    }

    // Abre el formulario. Si reserva es null es nueva, si no se edita esa reserva
    public void abrirFormulario(Reserva reserva) {
        Dialog dialogo = new Dialog();

        TextField txtCliente = new TextField("Cliente");
        TextField txtTelefono = new TextField("Teléfono (10 números)");
        DatePicker dpFecha = new DatePicker("Fecha");
        dpFecha.setMin(LocalDate.now()); // no deja escoger dias pasados

        // Hora de la reserva cada 30 minutos y solo en el horario del restaurante
        TimePicker tpHora = new TimePicker("Hora");
        tpHora.setStep(Duration.ofMinutes(30));
        tpHora.setMin(Reserva.HORA_APERTURA);
        tpHora.setMax(Reserva.HORA_CIERRE);

        TextField txtPersonas = new TextField("Número de personas");

        // Lista de mesas, se muestra el numero, la capacidad y la zona
        ComboBox<Mesa> cmbMesa = new ComboBox<>("Mesa");
        cmbMesa.setItems(mesas);
        cmbMesa.setItemLabelGenerator(mesa -> "Mesa " + mesa.numero + " - " + mesa.capacidad + " personas - " + mesa.zona);

        if (reserva == null) {
            dialogo.setHeaderTitle("Nueva reserva");
        } else {
            dialogo.setHeaderTitle("Editar reserva de " + reserva.cliente);
            txtCliente.setValue(reserva.cliente);
            txtTelefono.setValue(reserva.telefono);
            dpFecha.setValue(reserva.fecha);
            tpHora.setValue(reserva.hora);
            txtPersonas.setValue(String.valueOf(reserva.personas));
            cmbMesa.setValue(buscarMesa(reserva.numeroMesa));
        }

        FormLayout formulario = new FormLayout(txtCliente, txtTelefono, dpFecha, tpHora, txtPersonas, cmbMesa);
        dialogo.add(formulario);

        Button btnCancelar = new Button("Cancelar", e -> dialogo.close());
        Button btnGuardar = new Button("Guardar", e -> {
            boolean guardado = guardarReserva(reserva, txtCliente.getValue(), txtTelefono.getValue(),
                    dpFecha.getValue(), tpHora.getValue(), txtPersonas.getValue(), cmbMesa.getValue());
            if (guardado) {
                dialogo.close();
            }
        });
        btnGuardar.addThemeVariants(ButtonVariant.PRIMARY);
        dialogo.getFooter().add(btnCancelar, btnGuardar);
        dialogo.open();
    }

    // Valida los datos y guarda la reserva. Devuelve true si se pudo guardar
    public boolean guardarReserva(Reserva reserva, String cliente, String telefono, LocalDate fecha,
                                  LocalTime hora, String textoPersonas, Mesa mesa) {
        try {
            cliente = cliente.trim();
            telefono = telefono.trim();

            if (cliente.isEmpty()) {
                throw new IllegalArgumentException("Escriba el nombre del cliente");
            }
            if (cliente.contains("|")) {
                throw new IllegalArgumentException("El nombre no puede tener el caracter |");
            }
            if (!esTelefonoValido(telefono)) {
                throw new IllegalArgumentException("El teléfono debe tener 10 números");
            }
            if (fecha == null) {
                throw new IllegalArgumentException("Elija la fecha de la reserva");
            }
            if (hora == null) {
                throw new IllegalArgumentException("Elija la hora de la reserva");
            }
            if (fecha.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("No se puede reservar en una fecha pasada");
            }
            if (fecha.equals(LocalDate.now()) && hora.isBefore(LocalTime.now())) {
                throw new IllegalArgumentException("Esa hora de hoy ya pasó");
            }
            if (hora.isBefore(Reserva.HORA_APERTURA) || hora.isAfter(Reserva.HORA_CIERRE)) {
                throw new IllegalArgumentException("El restaurante atiende de " + Reserva.HORA_APERTURA
                        + " a " + Reserva.HORA_CIERRE);
            }
            if (hora.getMinute() != 0 && hora.getMinute() != 30) {
                throw new IllegalArgumentException("La hora debe ser en punto o y media");
            }

            int personas = Integer.parseInt(textoPersonas.trim());
            if (personas < 1) {
                throw new IllegalArgumentException("Debe ser al menos 1 persona");
            }
            if (mesa == null) {
                throw new IllegalArgumentException("Elija la mesa");
            }
            if (personas > mesa.capacidad) {
                throw new IllegalArgumentException("La mesa " + mesa.numero + " solo tiene capacidad para "
                        + mesa.capacidad + " personas");
            }
            if (estaMesaOcupada(mesa.numero, fecha, hora, reserva)) {
                throw new IllegalArgumentException("La mesa " + mesa.numero + " ya está reservada ese día a esa hora");
            }

            // Si es nueva se crea con el siguiente id y empieza pendiente
            if (reserva == null) {
                reserva = new Reserva();
                reserva.id = siguienteId();
                reserva.confirmada = false;
                reservas.add(reserva);
            }
            reserva.cliente = cliente;
            reserva.telefono = telefono;
            reserva.fecha = fecha;
            reserva.hora = hora;
            reserva.personas = personas;
            reserva.numeroMesa = mesa.numero;

            GestorArchivos.guardarReservas(reservas);
            actualizarGrid();
            Notification.show("Reserva de " + cliente + " guardada");
            return true;

        } catch (NumberFormatException e) {
            Notification.show("El número de personas debe ser un número entero");
        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage());
        }
        return false;
    }

    // Revisa que el telefono tenga 10 caracteres y que todos sean numeros
    public boolean esTelefonoValido(String telefono) {
        if (telefono.length() != 10) {
            return false;
        }
        for (int i = 0; i < telefono.length(); i++) {
            char c = telefono.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    // Revisa si otra reserva ya tiene esa mesa a la misma fecha y hora
    public boolean estaMesaOcupada(int numeroMesa, LocalDate fecha, LocalTime hora, Reserva reservaActual) {
        for (Reserva otra : reservas) {
            if (otra != reservaActual && otra.numeroMesa == numeroMesa
                    && otra.fecha.equals(fecha) && otra.hora.equals(hora)) {
                return true;
            }
        }
        return false;
    }

    // El siguiente id es el mayor que exista mas 1
    public int siguienteId() {
        int mayor = 0;
        for (Reserva reserva : reservas) {
            if (reserva.id > mayor) {
                mayor = reserva.id;
            }
        }
        return mayor + 1;
    }

    // Busca una mesa por su numero, si no la encuentra devuelve null
    public Mesa buscarMesa(int numero) {
        for (Mesa mesa : mesas) {
            if (mesa.numero == numero) {
                return mesa;
            }
        }
        return null;
    }
}
