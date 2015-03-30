package org.avitoparser.model;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "HouseType")
public class HouseType {
    @Id
    @GeneratedValue
    @Column(name = "hs_type_id")
    private Long id;

    public HouseType(){}

    @NotNull
    @Column(name = "hs_type_name", unique = true)
    private String name;

    public HouseType(String name) {
        this.name = name;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
