package taass.model;

import com.fasterxml.jackson.annotation.JsonView;
import taass.payload.Views;

import javax.persistence.*;

@Entity
@Table(name = "categorie")
@JsonView(Views.Category.class)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal.class)
    private long id;

    private String name;
    private String descr;
    private String defaultImage;

    public Category(){}

    public Category(String name, String descr, String defaultImage) {
        this.name = name;
        this.descr = descr;
        this.defaultImage = defaultImage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(String defaultImage) {
        this.defaultImage = defaultImage;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                '}';
    }
}
