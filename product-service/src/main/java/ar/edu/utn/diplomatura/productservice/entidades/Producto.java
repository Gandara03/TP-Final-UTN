package ar.edu.utn.diplomatura.productservice.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Producto financiero de un cliente (cuenta, tarjeta, prestamo o inversion).
 * El cliente vive en otro microservicio (customer-service), por eso aca solo se guarda
 * su id y no hay relacion JPA entre tablas.
 */
@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProducto tipo;

    @Column(nullable = false, unique = true)
    private String numero;

    private String descripcion;

    // saldo de la cuenta, limite de la tarjeta, capital del prestamo o monto invertido
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate fechaAlta = LocalDate.now();

    // baja logica: los productos inactivos no se listan
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean activo = true;

    public Producto(Long clienteId, TipoProducto tipo, String numero, String descripcion, BigDecimal monto) {
        this.clienteId = clienteId;
        this.tipo = tipo;
        this.numero = numero;
        this.descripcion = descripcion;
        this.monto = monto;
    }
}
