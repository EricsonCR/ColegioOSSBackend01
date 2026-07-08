package upc.colegioossbackend01.service;

public interface EmailService {

    boolean enviarCorreoRecuperacion(String email, String token);
}