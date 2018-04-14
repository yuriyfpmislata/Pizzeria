package modelo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Precios {

    private Map<String, Double> masa = new HashMap<>();
    private Map<String, Double> tipo = new HashMap<>();
    private Map<String, Double> ingrediente = new HashMap<>();
    private Map<String, Double> tamaño = new HashMap<>();

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
        
        for (String ingrediente : ingredientesAsumar) {
            precioIngredientes += this.ingrediente.get(ingrediente);            
        }
        
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
