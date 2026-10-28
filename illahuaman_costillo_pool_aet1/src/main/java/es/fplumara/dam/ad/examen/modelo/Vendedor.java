package es.fplumara.dam.ad.examen.modelo;

import java.util.Objects;

public class Vendedor {
    private int id;
    private String nombre;
    private String apellidos;
    private int codigoPostal;


    // Constructor vacío obligatorio para que Jackson pueda serializar/deserializar (P08 / P09)
    public Vendedor() {

    }

    public Vendedor(int id, String nombre, String apellidos, int codigoPostal) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.codigoPostal = codigoPostal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(int codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vendedor vendedor = (Vendedor) o;
        return id == vendedor.id;    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vendedor{id=" + id + ", nombre='" + nombre + "', apellidos='" + apellidos + "', codigoPostal=" + codigoPostal + "}";
    }


}
