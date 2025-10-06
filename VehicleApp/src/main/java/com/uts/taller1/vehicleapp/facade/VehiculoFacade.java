/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uts.taller1.vehicleapp.facade;

import com.uts.taller1.vehicleapp.model.Vehiculo;
import com.uts.taller1.vehicleapp.persistence.VehiculoDAO;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;
import java.util.Arrays;
import java.util.List;

/**
 * Fachada para operaciones sobre vehículos.
 * Aplica las reglas de negocio antes de llamar al DAO.
 * Maneja validaciones y restricciones de datos.
 */
@Stateless
public class VehiculoFacade {

    @Resource(lookup = "jdbc/garageDB")
    private DataSource ds;

    /** Lista de colores válidos permitidos. */
    private static final List<String> COLORES_VALIDOS = Arrays.asList("Rojo", "Blanco", "Negro", "Azul", "Gris");

    /**
     * Lista todos los vehículos registrados.
     * @return Lista de Vehiculo
     * @throws SQLException si ocurre un error de conexión a BD
     */
    public List<Vehiculo> listar() throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.listar();
        }
    }

    /**
     * Busca un vehículo por su identificador.
     * @param id identificador único
     * @return Vehiculo encontrado o null si no existe
     * @throws SQLException si ocurre un error de BD
     */
    public Vehiculo buscarPorId(int id) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);
            return dao.buscarPorId(id);
        }
    }

    /**
     * Agrega un nuevo vehículo después de validar reglas de negocio.
     * @param v vehículo a registrar
     * @throws SQLException si ocurre un error de BD
     * @throws IllegalArgumentException si alguna regla de negocio es violada
     */
    public void agregar(Vehiculo v) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);

            // Regla 1: No permitir placa duplicada
            if (dao.existePlaca(v.getPlaca())) {
                throw new IllegalArgumentException("La placa ya existe en el sistema");
            }

            // Regla 2: Propietario no vacío y >= 5 caracteres
            if (v.getPropietario() == null || v.getPropietario().trim().length() < 5) {
                throw new IllegalArgumentException("El propietario no puede estar vacío y debe tener al menos 5 caracteres");
            }

            // Regla 3: Marca, modelo y placa >= 3 caracteres
            if (v.getMarca() == null || v.getMarca().trim().length() < 3) {
                throw new IllegalArgumentException("La marca debe tener al menos 3 caracteres");
            }
            if (v.getModelo() == null || v.getModelo().trim().length() < 3) {
                throw new IllegalArgumentException("El modelo debe tener al menos 3 caracteres");
            }
            if (v.getPlaca() == null || v.getPlaca().trim().length() < 3) {
                throw new IllegalArgumentException("La placa debe tener al menos 3 caracteres");
            }

            // Regla 4: Color válido
            if (!COLORES_VALIDOS.contains(v.getColor())) {
                throw new IllegalArgumentException("El color no es válido. Solo se permiten: " + COLORES_VALIDOS);
            }

            // Regla 5: Modelo no mayor a 20 años de antigüedad
            try {
                int añoActual = Year.now().getValue();
                int añoModelo = Integer.parseInt(v.getModelo());
                if (añoModelo < (añoActual - 20)) {
                    throw new IllegalArgumentException("El vehículo tiene más de 20 años de antigüedad");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("El modelo debe representar un año válido (ejemplo: 2015)");
            }

            // Regla 8: Validar SQL Injection en campos
            validarSQLInjection(v.getPlaca());
            validarSQLInjection(v.getMarca());
            validarSQLInjection(v.getModelo());
            validarSQLInjection(v.getColor());
            validarSQLInjection(v.getPropietario());

            // Regla 9: Notificación si es Ferrari
            if ("Ferrari".equalsIgnoreCase(v.getMarca())) {
                System.out.println("⚠ Notificación: Se ha agregado un vehículo de marca Ferrari");
            }

            dao.agregar(v);
        }
    }

    /**
     * Actualiza un vehículo existente validando reglas de negocio.
     * @param v vehículo a actualizar
     * @throws SQLException si ocurre un error de BD
     * @throws IllegalArgumentException si alguna regla de negocio es violada
     */
    public void actualizar(Vehiculo v) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);

            // Regla 7: Solo actualizar si existe
            Vehiculo existente = dao.buscarPorId(v.getId());
            if (existente == null) {
                throw new IllegalArgumentException("No se puede actualizar un vehículo que no existe");
            }

            // Validaciones similares a agregar
            if (v.getPropietario() == null || v.getPropietario().trim().length() < 5) {
                throw new IllegalArgumentException("El propietario no puede estar vacío y debe tener al menos 5 caracteres");
            }
            if (v.getMarca() == null || v.getMarca().trim().length() < 3) {
                throw new IllegalArgumentException("La marca debe tener al menos 3 caracteres");
            }
            if (v.getModelo() == null || v.getModelo().trim().length() < 3) {
                throw new IllegalArgumentException("El modelo debe tener al menos 3 caracteres");
            }
            if (v.getPlaca() == null || v.getPlaca().trim().length() < 3) {
                throw new IllegalArgumentException("La placa debe tener al menos 3 caracteres");
            }
            if (!COLORES_VALIDOS.contains(v.getColor())) {
                throw new IllegalArgumentException("El color no es válido. Solo se permiten: " + COLORES_VALIDOS);
            }

            validarSQLInjection(v.getPlaca());
            validarSQLInjection(v.getMarca());
            validarSQLInjection(v.getModelo());
            validarSQLInjection(v.getColor());
            validarSQLInjection(v.getPropietario());

            dao.actualizar(v);
        }
    }

    /**
     * Elimina un vehículo de la base de datos.
     * @param id identificador del vehículo
     * @throws SQLException si ocurre un error de BD
     * @throws IllegalArgumentException si las reglas de negocio lo prohíben
     */
    public void eliminar(int id) throws SQLException {
        try (Connection con = ds.getConnection()) {
            VehiculoDAO dao = new VehiculoDAO(con);

            // Regla 6: No eliminar si propietario es "Administrador"
            Vehiculo v = dao.buscarPorId(id);
            if (v != null && "Administrador".equalsIgnoreCase(v.getPropietario())) {
                throw new IllegalArgumentException("No se puede eliminar un vehículo cuyo propietario sea 'Administrador'");
            }

            dao.eliminar(id);
        }
    }

    /**
     * Método auxiliar para validar posibles intentos de SQL Injection.
     * Simulación: revisa caracteres o palabras sospechosas.
     * @param campo texto a validar
     */
    private void validarSQLInjection(String campo) {
        if (campo != null && (campo.contains("'") || campo.contains(";") || campo.toLowerCase().contains("drop"))) {
            throw new IllegalArgumentException("Valor inválido detectado en el campo: posible SQL Injection");
        }
    }
}

