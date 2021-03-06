package pizzeria;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.ListSpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import modelo.Pizza;
import modelo.Precios;

public class PizzeriaController implements Initializable {

    @FXML
    private RadioButton radioNormal;
    @FXML
    private ToggleGroup grupoRadiosMasa;
    @FXML
    private RadioButton radioIntegral;
    @FXML
    private Label labelMasa;
    @FXML
    private Spinner<String> spinnerTamaño;
    @FXML
    private ComboBox<String> choiceTipo;
    @FXML
    private Label labelTipo;
    @FXML
    private Label labelIngredientes;
    @FXML
    private Label labelTamaño;
    @FXML
    private ListView<String> listViewIngredientes;

    @FXML
    private Label labelConsejoIngredientes;
    @FXML
    private Label labelPedido;
    @FXML
    private TextArea textareaPedido;
    @FXML
    private ImageView imgImprimirTicket;
    @FXML
    private ImageView imgCargarPrecios;
    @FXML
    private Pane panePreciosSinCargar;
    @FXML
    private Label labelAvisoPrecios;
    @FXML
    private Line linePaneAvisoPrecios;
    @FXML
    private ImageView imageFondo;
    @FXML
    private Rectangle rectanglePanelUsuario;

    private Pizza pizza = new Pizza();
    private boolean actualizandoNombres = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void nombresYvaloresPorDefecto() {
        actualizandoNombres = true;

        Precios precios = pizza.getPrecios();
        choiceTipo.setItems(FXCollections.observableArrayList(precios.tiposTipo()));
        listViewIngredientes.setItems(FXCollections.observableArrayList(precios.tiposIngrediente()));
        listViewIngredientes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ListSpinnerValueFactory<String> factoryTamaños = new ListSpinnerValueFactory(FXCollections.observableArrayList(precios.tiposTamaño()));
        spinnerTamaño.setValueFactory(factoryTamaños);

        // valores por defecto 
        radioNormal.setSelected(true);

        actualizandoNombres = false;
    }

    private void calcularPedidoPorDefecto() {
        masa();
        tamaño();
        ingredientes();

        /*
        Workaround para el bug que hace que setValue haga saltar onAction del ComboBox, haciendo que se recalcule el precio.
        Al ser el ultimo, las partes anteriores ya se han seteado en pizza, por lo que es seguro hacerlo.
        (realmente debería ir en nombresYvaloresPorDefecto)
         */
        choiceTipo.setValue("Básica");
    }

    @FXML
    private void accionMasa(ActionEvent event) {
        masa();
        mostrarPedido();
    }

    private void masa() {
        String masa = ((RadioButton) grupoRadiosMasa.getSelectedToggle()).getText();
        pizza.setMasa(masa);
    }

    @FXML
    private void accionTamaño(MouseEvent event) {
        tamaño();
        mostrarPedido();
    }

    public void tamaño() {
        String tamaño = spinnerTamaño.getValue();
        pizza.setTamaño(tamaño);
    }

    @FXML
    private void accionIngredientes(MouseEvent event) {
        ingredientes();
        mostrarPedido();
    }

    private void ingredientes() {
        Set<String> ingredientesExtra = new HashSet<>();

        for (String ingrediente : listViewIngredientes.getSelectionModel().getSelectedItems()) {
            // proteger ante un extraño bug que hace que el ObservableList tenga null(s)
            if (ingrediente != null) {
                ingredientesExtra.add(ingrediente);
            }
        }

        pizza.setIngredientesExtra(ingredientesExtra);
    }

    @FXML
    private void accionTipo(ActionEvent event) {
        tipo();
        // prevenir el calculo de pedido si el evento fue causado por el setItems de nombresYvaloresPorDefecto
        if (!actualizandoNombres) {
            mostrarPedido();
        }
    }

    private void tipo() {
        String tipo = choiceTipo.getValue();
        pizza.setTipo(tipo);
    }

    private void mostrarPedido() {
        textareaPedido.setText(pizza.composicion());
    }

    @FXML
    private void imprimirTicket(MouseEvent event) {
        Path directorioSeleccionado;
        DirectoryChooser selectorDirectorio = new DirectoryChooser();
        selectorDirectorio.setTitle("Ubicación del ticket a guardar");

        // establecer como directorio inicial la carpeta actual del proyecto -> datos -> pedidos
        selectorDirectorio.setInitialDirectory(new File(".\\datos\\pedidos"));

        try {
            directorioSeleccionado = selectorDirectorio.showDialog(null).toPath();
            pizza.generarTicket(directorioSeleccionado);
        } catch (Exception e) {
            System.err.println("El usuario pulsó en Cancelar");
        }

    }

    @FXML
    private void cargarPrecios(MouseEvent event) {
        Path archivoSeleccionado;
        FileChooser selectorArchivo = new FileChooser();
        selectorArchivo.setTitle("Archivo de precios");
        selectorArchivo.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));

        // establecer como directorio inicial la carpeta actual del proyecto -> datos -> precios
        selectorArchivo.setInitialDirectory(new File(".\\datos\\precios"));

        try {
            archivoSeleccionado = selectorArchivo.showOpenDialog(null).toPath();
            pizza.cargaPrecios(archivoSeleccionado);

            // ocultar aviso
            panePreciosSinCargar.setVisible(false);
            // cargar pedido por defecto, ahora que tenemos los precios
            nombresYvaloresPorDefecto();

            calcularPedidoPorDefecto();
        } catch (Exception e) {
            if (e.getMessage() == null) {
                System.err.println("El usuario pulsó en Cancelar o el archivo pasado no es válido");
            } else {
                System.err.println(e.getMessage());
            }
        }
    }
}
