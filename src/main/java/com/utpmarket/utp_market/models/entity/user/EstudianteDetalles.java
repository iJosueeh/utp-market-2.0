package com.utpmarket.utp_market.models.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "estudiantes_detalles")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "usuario")
@EqualsAndHashCode(of = "id")
public class EstudianteDetalles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String codigo_estudiante;
    private String ciclo;
    private String carrera;
    private String dni;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecha_nacimiento;

    private String photoUrl;
    private String telefono;
}
