package com.uts.taller1.vehicleapp.controller;

import com.uts.taller1.vehicleapp.model.Vehiculo;
import com.uts.taller1.vehicleapp.facade.VehiculoFacade;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet controlador para la gestión de vehículos.
 * <p>
 * Este servlet actúa como capa intermedia entre las vistas JSP
 * y la fachada de negocio {@link VehiculoFacade}. 
 * Recibe peticiones HTTP (GET y POST), invoca las operaciones del Facade
 * y reenvía los resultados o errores a las vistas correspondientes.
 * <p>
 * Reglas:
 * <ul>
 *   <li>GET: lista todos los vehículos, permite editar o eliminar.</li>
 *   <li>POST: registra o actualiza un vehículo según el contexto.</li>
 *   <li>Captura excepciones de negocio y técnicas, mostrando mensajes amigables.</li>
 * </ul>
 */
@WebServlet("/vehicles")
public class VehiculoServlet extends HttpServlet {

    /**
     * Fachada EJB para gestionar las reglas de negocio y acceso a datos de Vehículo.
     */
    @EJB
    private VehiculoFacade facade;

    /**
     * Maneja peticiones HTTP GET.
     * <p>
     * Según el parámetro "action" realiza:
     * <ul>
     *     <li><b>action=delete</b>: elimina un vehículo existente.</li>
     *     <li><b>action=edit</b>: carga datos de un vehículo para edición.</li>
     *     <li>Sin action: lista todos los vehículos.</li>
     * </ul>
     *
     * @param request  petición HTTP entrante
     * @param response respuesta HTTP saliente
     * @throws ServletException en caso de error de servlet
     * @throws IOException en caso de error de E/S
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("delete".equals(action)) {
                // Eliminar vehículo existente
                int id = Integer.parseInt(request.getParameter("id"));
                facade.eliminar(id);
                response.sendRedirect("vehicles");
                return;

            } else if ("edit".equals(action)) {
                // Cargar datos para edición
                int id = Integer.parseInt(request.getParameter("id"));
                Vehiculo v = facade.buscarPorId(id);
                request.setAttribute("vehiculo", v);
                request.getRequestDispatcher("/formVehiculo.jsp").forward(request, response);
                return;
            }

            // Listar todos los vehículos
            List<Vehiculo> vehicles = facade.listar();
            request.setAttribute("vehicles", vehicles);
            request.getRequestDispatcher("/vehicles.jsp").forward(request, response);

        } catch (EJBException ejbEx) {
            // Desempacar excepciones del EJB para mostrar mensajes amigables
            Throwable cause = rootCause(ejbEx);

            if (cause instanceof IllegalArgumentException) {
                request.setAttribute("error", cause.getMessage()); // regla de negocio
            } else if (cause instanceof SQLException) {
                request.setAttribute("error", "Ocurrió un problema al acceder a la base de datos. Intente más tarde.");
            } else {
                request.setAttribute("error", "Error inesperado en la aplicación.");
            }
            request.getRequestDispatcher("/vehicles.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Ocurrió un problema al acceder a la base de datos. Intente más tarde.");
            request.getRequestDispatcher("/vehicles.jsp").forward(request, response);

        } catch (IllegalArgumentException e) { // por si no viene envuelta
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/vehicles.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado en la aplicación.");
            request.getRequestDispatcher("/vehicles.jsp").forward(request, response);
        }
    }

    /**
     * Maneja peticiones HTTP POST.
     * <p>
     * Inserta un nuevo vehículo o actualiza uno existente, dependiendo
     * de si el parámetro "id" viene vacío o con un valor válido.
     * <p>
     * Captura y muestra mensajes de reglas de negocio, ocultando errores técnicos.
     *
     * @param request  petición HTTP entrante con datos del formulario
     * @param response respuesta HTTP saliente
     * @throws ServletException en caso de error de servlet
     * @throws IOException en caso de error de E/S
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        String placa = request.getParameter("placa");
        String marca = request.getParameter("marca");
        String modelo = request.getParameter("modelo");
        String color = request.getParameter("color");
        String propietario = request.getParameter("propietario");

        // Determinar si es creación o actualización
        boolean esCrear = (idStr == null || idStr.isBlank() || "0".equals(idStr));

        Vehiculo v = new Vehiculo();
        if (!esCrear) v.setId(Integer.parseInt(idStr));
        v.setPlaca(placa);
        v.setMarca(marca);
        v.setModelo(modelo);
        v.setColor(color);
        v.setPropietario(propietario);

        try {
            if (esCrear) {
                facade.agregar(v);     // Crear nuevo
            } else {
                facade.actualizar(v);  // Actualizar existente
            }
            response.sendRedirect("vehicles");
            return;

        } catch (EJBException ejbEx) {
            // Desempacar causa raíz
            Throwable cause = rootCause(ejbEx);

            if (cause instanceof IllegalArgumentException) {
                request.setAttribute("error", cause.getMessage());          // negocio: mostrar tal cual
            } else if (cause instanceof SQLException) {
                request.setAttribute("error", "Ocurrió un problema al acceder a la base de datos. Intente más tarde.");
            } else {
                request.setAttribute("error", "Error inesperado en la aplicación.");
            }
            request.setAttribute("vehiculo", v);
            request.getRequestDispatcher("/formVehiculo.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("error", "Ocurrió un problema al acceder a la base de datos. Intente más tarde.");
            request.setAttribute("vehiculo", v);
            request.getRequestDispatcher("/formVehiculo.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("vehiculo", v);
            request.getRequestDispatcher("/formVehiculo.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado en la aplicación.");
            request.setAttribute("vehiculo", v);
            request.getRequestDispatcher("/formVehiculo.jsp").forward(request, response);
        }
    }

    /**
     * Obtiene la causa raíz de una excepción.
     * <p>
     * Útil para des-encapsular {@link EJBException} y acceder a la
     * excepción original lanzada por el Facade o DAO.
     *
     * @param ex excepción anidada
     * @return la causa raíz más profunda
     */
    private static Throwable rootCause(Throwable ex) {
        Throwable t = ex;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        return t;
    }
}
