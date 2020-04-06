package com.demente.ideas.app.models.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "SPRING_LEARN_T_ROLES",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"client_id", "authority"})})
public class Role implements Serializable {

    @Id // indica que este atributo es la llave primaria (primary key)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // indica cual es la estrategia para generar el id
    // el motor de BD, por eje; auto incremental
    private Long id;

    @NotNull
    @Column(length = 45, nullable = false)
    private String authority;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
