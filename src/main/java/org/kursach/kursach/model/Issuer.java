package org.kursach.kursach.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "issuer")
public class Issuer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "country")
    private String country;
    
    @Column(name = "sector")
    private String sector;
    
    @Column(name = "rating")
    private String rating;
    
    @Column(name = "description", length = 2000)
    private String description;
    
    @OneToMany(mappedBy = "issuer")
    private List<Security> securities;
    
    public Issuer() {
    }
    
    public Issuer(String name, String country, String sector, String rating, String description) {
        this.name = name;
        this.country = country;
        this.sector = sector;
        this.rating = rating;
        this.description = description;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Security> getSecurities() {
        return securities;
    }

    public void setSecurities(List<Security> securities) {
        this.securities = securities;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Issuer issuer = (Issuer) o;
        return id != null ? id.equals(issuer.id) : issuer.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Issuer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", sector='" + sector + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
} 