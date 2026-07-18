package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.AprobarUsuarioRequest;
import upc.colegioossbackend01.dto.request.CambiarRolRequest;
import upc.colegioossbackend01.dto.request.CrearUsuarioRequest;
import upc.colegioossbackend01.dto.response.UsuarioResponse;
import upc.colegioossbackend01.enums.EstadoUsuario;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponse> listarPendientes();

    List<UsuarioResponse> listar(EstadoUsuario estado, Long rolId);

    UsuarioResponse crear(CrearUsuarioRequest request);

    UsuarioResponse aprobarUsuario(Long usuarioId, AprobarUsuarioRequest request);

    UsuarioResponse cambiarRol(Long usuarioId, CambiarRolRequest request);

    UsuarioResponse cambiarEstado(String usernameAutenticado, Long usuarioIdObjetivo, EstadoUsuario nuevoEstado);
}