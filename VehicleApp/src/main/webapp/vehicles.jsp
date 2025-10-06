<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lista de Vehículos</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container py-4">

    <h2 class="mb-4">Vehículos Registrados</h2>

    <!-- Mensaje de error -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            ${error}
        </div>
    </c:if>

    <!-- Tabla -->
    <table class="table table-striped table-hover">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Placa</th>
                <th>Marca</th>
                <th>Modelo</th>
                <th>Color</th>
                <th>Propietario</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
        <c:forEach var="v" items="${vehicles}">
            <tr>
                <td>${v.id}</td>
                <td>${v.placa}</td>
                <td>${v.marca}</td>
                <td>${v.modelo}</td>
                <td>${v.color}</td>
                <td>${v.propietario}</td>
                <td>
                    <a href="vehicles?action=edit&id=${v.id}" class="btn btn-sm btn-primary">Editar</a>
                    <a href="vehicles?action=delete&id=${v.id}" class="btn btn-sm btn-danger"
                       onclick="return confirm('¿Seguro que deseas eliminar este vehículo?');">
                        Eliminar
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <a href="formVehiculo.jsp" class="btn btn-success">Registrar nuevo vehículo</a>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
