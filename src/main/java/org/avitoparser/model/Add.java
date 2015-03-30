package org.avitoparser.model;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Advertisement")
public class Add {

    @Id
    @GeneratedValue
    @Column(name = "add_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "add_person_id", referencedColumnName = "person_id", nullable = false)
    private Person person;

    @NotNull
    @Column(name = "add_description")
    @Type(type="text")
    private String description;

    @NotNull
    @Column(name = "add_date_post")
    private Date datePost;

    @NotNull
    @Column(name = "add_price")
    private int prise;

    @NotNull
    @Column(name = "add_avitoid")
    private Long avitoIdentificator;

    public Apartment getApartments() {
        return apartments;
    }

    public void setApartments(Apartment apartments) {
        this.apartments = apartments;
    }

    @ManyToOne(cascade = {CascadeType.ALL})

    @JoinTable(name="Add_Apartment",
            joinColumns={@JoinColumn(name="add_ap_add_id")},
            inverseJoinColumns={@JoinColumn(name="add_ap_ap_id")})
    private Apartment apartments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDatePost() {
        return datePost;
    }

    public void setDatePost(Date datePost) {
        this.datePost = datePost;
    }

    public int getPrise() {
        return prise;
    }

    public void setPrise(int prise) {
        this.prise = prise;
    }

    public Long getAvitoIdentificator() {
        return avitoIdentificator;
    }

    public void setAvitoIdentificator(Long avitoIdentificator) {
        this.avitoIdentificator = avitoIdentificator;
    }

}

