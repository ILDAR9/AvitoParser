package org.avitoparser.model;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "City")
public class City {
    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public City(){}

    public City(String name) {

        this.name = name;
    }

    @Id
    @GeneratedValue
    @Column(name = "city_id")
    private Long id;

    @NotNull
    @Column(name = "city_name", unique = true)
    private String name;
}
