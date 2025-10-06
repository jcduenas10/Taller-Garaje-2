/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uts.taller1.vehicleapp.persistence;

import com.uts.taller1.vehicleapp.model.Vehiculo; 
import java.sql.*; 
import java.util.*;

/**
 *
 * @author juaka
 */
public class VehiculoDAO {
    private final Connection con;
    
    public VehiculoDAO(Connection con){
        this.con=con;
    }
    
    /** * Busca todos los vehículos en la base de datos. 
     * @return Lista de Vehiculo o lista vacía. 
     * @throws SQLException si hay error de conexión BID. 
     */
    public List<Vehiculo> listar() throws SQLException { 
        List<Vehiculo> lista = new ArrayList<>();
        String sql = "SELECT * FROM vehiculos"; 
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) { 
            while (rs.next()) { 
                Vehiculo v = new Vehiculo( rs.getInt("id"), 
                        rs.getString("placa"), 
                        rs.getString("marca"), 
                        rs.getString("modelo"), 
                        rs.getString("color"), 
                        rs.getString("propietario") ); 
                lista.add(v); 
            } 
        } catch (SQLException ex) { 
        // Manejo de error: loggear el error y relanzar 
        System.err.println("Error al listar vehículos: " + ex.getMessage()); 
        throw ex; 
        // relanzar para manejar en capa superior 
        } 
        return lista; 
    }
    
    /**
     * Busca un vehículo por ID
     */
    
    public Vehiculo buscarPorId(int id) throws SQLException{
        String sql ="SELECT * FROM vehiculos WHERE id=?";
        try(PreparedStatement ps = con.prepareStatement(sql)){
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return new Vehiculo(
                    rs.getInt("id"),
                    rs.getString("placa"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getString("color"),
                    rs.getString("propietario")
                );
            }  
        } catch(SQLException ex){
            System.err.println("Error al buscar vehículo por id: " + ex.getMessage());
            throw ex;
        }
        return null;
    }
    
    /**
     * Verifica si ya existe una placa registrada.
     * @param placa placa a buscar
     * @return true si existe, false si no
     */
    public boolean existePlaca(String placa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vehiculos WHERE placa=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Error al verificar placa: " + ex.getMessage());
            throw ex;
        }
        return false;
    }
    
    /**
     * Agrega un nuevo vehículo.
     */
    public void agregar(Vehiculo v) throws SQLException {
        String sql = "INSERT INTO vehiculos (placa, marca, modelo, color, propietario) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setString(4, v.getColor());
            ps.setString(5, v.getPropietario());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al agregar vehículo: " + ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Actualiza todos los datos de un vehículo existente por id.
     */
    public void actualizar(Vehiculo v) throws SQLException {
        String sql = "UPDATE vehiculos SET placa=?, marca=?, modelo=?, color=?, propietario=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setString(4, v.getColor());
            ps.setString(5, v.getPropietario());
            ps.setInt(6, v.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al actualizar vehículo: " + ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Borra un vehículo por id.
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM vehiculos WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al eliminar vehículo: " + ex.getMessage());
            throw ex;
        }
    }
    
}
