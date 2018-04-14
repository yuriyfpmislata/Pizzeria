package modelo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class Pizza {

    private String masa;
    private String tipo;
    private Set<String> ingredientesExtra;
    private String tamaño;

    private Precios precios;

    public Pizza() {}

    public Pizza(String masa, String tipo, Set<String> ingredientesExtra, String tamaño) {
        this.masa = masa;
        this.tipo = tipo;
        this.ingredientesExtra = ingredientesExtra;
        this.tamaño = tamaño;
    }

    public String getMasa() {
        return masa;
    }

    public void setMasa(String masa) {
        this.masa = masa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Set<String> getIngredientesExtra() {
        return ingredientesExtra;
    }

    public void setIngredientesExtra(Set<String> ingredientesExtra) {
        this.ingredientesExtra = ingredientesExtra;
    }

    public String getTamaño() {
        return tamaño;
    }

    public void setTamaño(String tamaño) {
        this.tamaño = tamaño;
    }

    public Precios getPrecios() {
        return precios;
    }

    public void setPrecios(Precios precios) {
        this.precios = precios;
    }

    public Double calcularPrecio() {
        Double precioTotal = 0.00;

        Double precioMasa = precios.precioDeMasa(this.masa);
        Double precioTipo = precios.precioDeTipo(this.tipo);
        Double porcentajeTamaño = precios.porcentajeDeTamaño(this.tamaño);
        Double precioIngredientes = precios.precioDeIngredientes(this.ingredientesExtra);

        precioTotal = (precioMasa + precioTipo + precioIngredientes);
        precioTotal += (precioTotal * (porcentajeTamaño / 100));

        return precioTotal;
    }

    public String composicion() {
        String cadena = "";
        Double precioTotal = 0.00;

        Double precioMasa = precios.precioDeMasa(this.masa);
        Double precioTipo = precios.precioDeTipo(this.tipo);
        Double porcentajeTamaño = precios.porcentajeDeTamaño(this.tamaño);
        Double precioIngredientes = precios.precioDeIngredientes(this.ingredientesExtra);

        precioTotal = this.calcularPrecio();

        cadena += "MASA: " + this.masa + " - " + precioMasa + "€";
        cadena += "\n";
        cadena += "TIPO: " + this.tipo + " - " + precioTipo + "€";
        cadena += "\n";
        cadena += "INGREDIENTES EXTRA: " + this.ingredientesExtra + " - " + precioIngredientes + "€";
        cadena += "\n";
        cadena += "TAMAÑO: " + this.tamaño + " - " + porcentajeTamaño + "%";
        cadena += "\n";
        cadena += "TOTAL: " + String.format("%.2f", precioTotal) + "€";

        return cadena;
    }

    public void generarTicket(Path ruta) {
        Path rutaArchivo;

        String fechaAhora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-s"));

        rutaArchivo = Paths.get(ruta.toString() + "\\pedidoPizzeria_" + fechaAhora + ".txt");

        try (BufferedWriter bw = Files.newBufferedWriter(rutaArchivo, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW)) {
            bw.write(this.composicion());
        } catch (IOException ex) {
            System.err.println("Error al escribir pedido a archivo: " + ex);
        }
    }

    public void cargaPrecios(Path ruta) {
        Map<String, Double> masa = new HashMap<>();
        Map<String, Double> tipo = new HashMap<>();
        Map<String, Double> ingredientesExtra = new HashMap<>();
        Map<String, Double> tamaño = new HashMap<>();

        try (Stream<String> datos = Files.lines(ruta)) {
            Iterator<String> it = datos.iterator();

            Map<String, Double> mapActual = null;

            while (it.hasNext()) {
                String linea = it.next();

                if (linea.charAt(0) == '@') {
                    // cambio de map
                    switch (linea.substring(1).toLowerCase()) {
                        case "masa":
                            mapActual = masa;
                            break;
                        case "tipo":
                            mapActual = tipo;
                            break;
                        case "ingrediente":
                            mapActual = ingredientesExtra;
                            break;
                        case "tamaño":
                            mapActual = tamaño;
                            break;
                        default:
                            throw new RuntimeException("Linea con @, pero map desconocido");
                    }
                } else {
                    // items
                    if (mapActual == null) {
                        throw new RuntimeException("La primera linea no define un map, o el bucle ha pasado por una linea con un map desconocido");
                    } else {
                        String[] lineaTroceada = linea.split(":");
                        if (lineaTroceada.length != 2 || (lineaTroceada.length == 2 && (lineaTroceada[1].equals("") || lineaTroceada[1].equals(" ")))) {
                            // la linea no contiene 2 partes, o solo la primera
                            throw new RuntimeException("La linea no contiene 2 partes, posible item sin precio / o la segunda parte esta vacía");
                        }
                        mapActual.put(lineaTroceada[0], Double.parseDouble(lineaTroceada[1]));
                    }
                }
            }
            this.precios = new Precios(masa, tipo, ingredientesExtra, tamaño);
        } catch (IOException ex) {
            throw new RuntimeException("Error en la lectura");
        }
    }
}
