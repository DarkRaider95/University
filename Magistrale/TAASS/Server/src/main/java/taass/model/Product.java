
package taass.model;

import com.fasterxml.jackson.annotation.JsonView;
import taass.payload.Views;

import javax.persistence.*;

@Entity
@Table(name = "prodotti")
@JsonView(Views.Product.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Minimal.class)
    private long id;

    @JsonView(Views.Summary.class)
    private String name;

    private String descr;

    @JsonView(Views.Summary.class)
    private String image;

    private float price;

    @ManyToOne
    //@JoinColumn(name="category_id")
    private Category category;

    @ManyToOne
    //@JoinColumn(name="user_id")
    //@JsonIgnore
    private User owner;

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Product(){
    }

    public Product(String name, String descr, String image, float price, Category category, User owner) {
        this.name = name;
        this.descr = descr;
        this.image = image;
        this.price = price;
        this.category = category;
        this.owner = owner;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", descr='" + descr + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", category=" + category +
                '}';
    }
}


