package me.mircea.licenta.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import com.google.common.base.Preconditions;

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String title;

	@ElementCollection
	private List<String> authors;

	@Column(unique = true)
	private String isbn;

	@Column
	@Type(type = "text")
	private String description;

	//TODO: maybe switch to SortedSet?
	@OneToMany(cascade = CascadeType.ALL)
	private Set<PricePoint> pricepoints;

	public Product() {
		this.authors = new ArrayList<>();
		this.pricepoints = new TreeSet<>();
	}

	public Product(Integer id, String title, String description, List<String> authors) {
		super();
		
		Preconditions.checkNotNull(authors);
		this.id = id;
		this.title = title;
		this.description = description;
		this.authors = authors;
		this.pricepoints = new TreeSet<>();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<PricePoint> getPricepoints() {
		return pricepoints;
	}

	public void setPricepoints(Set<PricePoint> pricepoints) {
		this.pricepoints = pricepoints;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", title=" + title + ", authors=" + authors + ", isbn=" + isbn + ", description="
				+ (description != null) + ", pricepoints=" + pricepoints + "]";
	}
}
