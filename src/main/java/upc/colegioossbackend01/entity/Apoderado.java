package upc.colegioossbackend01.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import upc.colegioossbackend01.enums.TipoDocumento;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "apoderado")
public class Apoderado extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @Column(nullable = false, length = 150)
    private String nombres;

    @Column(nullable = false, length = 150)
    private String apellidos;

    @Column(length = 20)
    private String telefono;

    @Column(length = 150)
    private String email;

    @Column(length = 255)
    private String direccion;

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;
}