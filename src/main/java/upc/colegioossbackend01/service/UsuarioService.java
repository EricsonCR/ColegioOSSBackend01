package upc.colegioossbackend01.service;

import upc.colegioossbackend01.dto.request.AprobarUsuarioRequest;
import upc.colegioossbackend01.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponse> listarPendientes();

    UsuarioResponse aprobarUsuario(Long usuarioId, AprobarUsuarioRequest request);
}