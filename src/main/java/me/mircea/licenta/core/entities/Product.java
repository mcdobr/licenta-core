package me.mircea.licenta.core.entities;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "products")
public class Product {
	@Id
	private Integer id;
	private String title;
	private String description;
	
	@ElementCollection
	private List<String> authors;
	
	public Product() {}

	public Product(Integer id, String title, String description, List<String> authors) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.authors = authors;
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
}
