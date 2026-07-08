package upc.colegioossbackend01.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import upc.colegioossbackend01.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${app.frontend.reset-password-url}")
    private String resetPasswordUrl;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public boolean enviarCorreoRecuperacion(String email, String token) {
        try {
            String enlace = resetPasswordUrl + "?token=" + token;

            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(email);
            mensaje.setSubject("Recuperación de contraseña");
            mensaje.setText(
                    "Hola,\n\n" +
                            "Recibimos una solicitud para restablecer tu contraseña.\n" +
                            "Haz clic en el siguiente enlace para continuar (válido por 30 minutos):\n\n" +
                            enlace + "\n\n" +
                            "Si no solicitaste este cambio, ignora este correo."
            );

            javaMailSender.send(mensaje);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}