package me.mircea.licenta.core.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String title;
	private String description;
	
	@ElementCollection
	private List<String> authors;
	
	@OneToMany
	private List<PricePoint> pricepoints;

	public Product() {
		this.authors = new ArrayList<>();
		this.pricepoints = new ArrayList<>();
	}

	public Product(Integer id, String title, String description, List<String> authors) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.authors = authors;
		this.pricepoints = new ArrayList<>();
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}
	
	public List<PricePoint> getPricepoints() {
		return pricepoints;
	}

	public void setPricepoints(List<PricePoint> pricepoints) {
		this.pricepoints = pricepoints;
	}
}
