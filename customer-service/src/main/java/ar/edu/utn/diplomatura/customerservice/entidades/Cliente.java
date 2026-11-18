package ar.edu.utn.diplomatura.customerservice.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Cliente de la fintech. Se identifica por su CUIT/CUIL (el "documento"), ya que el DNI puede repetirse.
 * Sus productos viven en product-service y se consultan con Feign.
 */
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    // CUIT/CUIL: 11 digitos sin guiones, no se puede repetir
    @Column(nullable = false, unique = true, length = 11)
    private String cuit;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;

    public Cliente(String nombre, String apellido, String cuit, String email, BigDecimal saldo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cuit = cuit;
        this.email = email;
        this.saldo = saldo;
    }
}
