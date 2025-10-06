<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Formulario Vehículo</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container py-4">

    <h2>
        <c:choose>
            <c:when test="${not empty vehiculo and vehiculo.id > 0}">Editar Vehículo</c:when>
            <c:otherwise>Registrar Vehículo</c:otherwise>
        </c:choose>
    </h2>

    <!-- Mensaje de error -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            ${error}
        </div>
    </c:if>

    <!-- Formulario -->
    <form action="vehicles" method="post" class="row g-3">

        <c:if test="${vehiculo ne null and vehiculo.id ne null and vehiculo.id > 0}">
            <input type="hidden" name="id" value="${vehiculo.id}" />
        </c:if>


        <div class="col-md-6">
            <label for="placa" class="form-label">Placa</label>
            <input type="text" class="form-control" name="placa" id="placa"
                   value="${vehiculo.placa}" required>
        </div>

        <div class="col-md-6">
            <label for="marca" class="form-label">Marca</label>
            <input type="text" class="form-control" name="marca" id="marca"
                   value="${vehiculo.marca}" required>
        </div>

        <div class="col-md-6">
            <label for="modelo" class="form-label">Modelo</label>
            <input type="text" class="form-control" name="modelo" id="modelo"
                   value="${vehiculo.modelo}" required>
        </div>

        <div class="col-md-6">
            <label for="color" class="form-label">Color</label>
            <select class="form-select" name="color" id="color">
                <option value="Rojo" ${vehiculo.color == 'Rojo' ? 'selected' : ''}>Rojo</option>
                <option value="Blanco" ${vehiculo.color == 'Blanco' ? 'selected' : ''}>Blanco</option>
                <option value="Negro" ${vehiculo.color == 'Negro' ? 'selected' : ''}>Negro</option>
                <option value="Azul" ${vehiculo.color == 'Azul' ? 'selected' : ''}>Azul</option>
                <option value="Gris" ${vehiculo.color == 'Gris' ? 'selected' : ''}>Gris</option>
            </select>
        </div>

        <div class="col-md-12">
            <label for="propietario" class="form-label">Propietario</label>
            <input type="text" class="form-control" name="propietario" id="propietario"
                   value="${vehiculo.propietario}" required>
        </div>

        <div class="col-12">
            <button type="submit" class="btn btn-primary">
                <c:choose>
                  <c:when test="${vehiculo ne null and vehiculo.id ne null and vehiculo.id > 0}">Actualizar</c:when>
                  <c:otherwise>Registrar</c:otherwise>
                </c:choose>
            </button>
            <a href="vehicles" class="btn btn-secondary">Cancelar</a>
        </div>
    </form>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
