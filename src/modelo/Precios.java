package modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Precios {

    private Map<String, Double> masa = new HashMap<>();
    private Map<String, Double> tipo = new HashMap<>();
    private Map<String, Double> ingrediente = new HashMap<>();
    private Map<String, Double> tamaño = new HashMap<>();

    public Precios() {
        this.masa.put("Normal", 9.00);
        this.masa.put("Integral", 9.50);

        this.tipo.put("Básica", 3.00);
        this.tipo.put("Cuatro quesos", 5.00);
        this.tipo.put("Barbacoa", 7.00);
        this.tipo.put("Mexicana", 8.50);

        this.ingrediente.put("Jamón", 0.50);
        this.ingrediente.put("Queso", 0.75);
        this.ingrediente.put("Tomate", 1.50);
        this.ingrediente.put("Cebolla", 2.50);
        this.ingrediente.put("Olivas", 1.00);

        this.tamaño.put("Pequeña", 0.00);
        this.tamaño.put("Mediana", 15.00);
        this.tamaño.put("Familiar", 30.00);
    }

    public Precios(
            Map<String, Double> masa,
            Map<String, Double> tipo,
            Map<String, Double> ingrediente,
            Map<String, Double> tamaño
    ) {
        this.masa = masa;
        this.tipo = tipo;
        this.ingrediente = ingrediente;
        this.tamaño = tamaño;
    }

    public Double precioDeMasa(String masa) {
        return this.masa.get(masa);
    }

    public Double precioDeTipo(String tipo) {
        return this.tipo.get(tipo);
    }

    public Double precioDeIngredientes(Set<String> ingredientesAsumar) {
        Double precioIngredientes = 0.00;
        System.out.println("PRECIO");
        for (String ingrediente : ingredientesAsumar) {
            precioIngredientes += this.ingrediente.get(ingrediente);
        }
System.out.println("PRECIO");
        return precioIngredientes;
    }

    public Double porcentajeDeTamaño(String tamaño) {
        return this.tamaño.get(tamaño);
    }

    public Set<String> tiposMasa() {
        return masa.keySet();
    }

    public Set<String> tiposTipo() {
        return tipo.keySet();
    }

    public Set<String> tiposIngrediente() {
        return ingrediente.keySet();
    }

    public Set<String> tiposTamaño() {
        return tamaño.keySet();
    }
}
