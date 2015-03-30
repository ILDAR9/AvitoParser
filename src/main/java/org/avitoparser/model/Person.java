package org.avitoparser.model;

import com.sun.istack.internal.NotNull;

import javax.persistence.*;

@Entity
@Table(name = "Person")
public class Person {
    public Person(String name, String telephone) {
        this.name = name;
        this.telephone = telephone;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Id
    @GeneratedValue

    @Column(name = "person_id")
    private Long id;

    @NotNull  //ToDo add hibernate validator if it's neede
    @Column(name = "person_name")
    private String name;

    @NotNull
    @Column(name = "person_telephone")
    private String telephone;

}
