package me.mircea.licenta.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

import org.hibernate.annotations.Type;

import com.google.common.base.Preconditions;

@Entity
@Table(name = "books")
public class Book {
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

	@OneToMany(cascade = CascadeType.ALL)
	private Set<PricePoint> pricepoints;

	private String publishingHouse;

	private Integer releaseYear;

	private String format;
	
	@Column
	@Type(type = "text")
	private String coverUrl;

	public Book() {
		this.authors = new ArrayList<>();
		this.pricepoints = new TreeSet<>();
	}

	public Book(Integer id, String title, String description, List<String> authors) {
		super();

		Preconditions.checkNotNull(authors);
		this.id = id;
		this.title = title;
		this.description = description;
		this.authors = authors;
		this.pricepoints = new TreeSet<>();
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the authors
	 */
	public List<String> getAuthors() {
		return authors;
	}

	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	/**
	 * @return the isbn
	 */
	public String getIsbn() {
		return isbn;
	}

	/**
	 * @param isbn
	 *            the isbn to set
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the pricepoints
	 */
	public Set<PricePoint> getPricepoints() {
		return pricepoints;
	}

	/**
	 * @param pricepoints
	 *            the pricepoints to set
	 */
	public void setPricepoints(Set<PricePoint> pricepoints) {
		this.pricepoints = pricepoints;
	}

	/**
	 * @return the publishingHouse
	 */
	public String getPublishingHouse() {
		return publishingHouse;
	}

	/**
	 * @param publishingHouse
	 *            the publishingHouse to set
	 */
	public void setPublishingHouse(String publishingHouse) {
		this.publishingHouse = publishingHouse;
	}

	/**
	 * @return the releaseYear
	 */
	public Integer getReleaseYear() {
		return releaseYear;
	}

	/**
	 * @param releaseYear
	 *            the releaseYear to set
	 */
	public void setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the coverUrl
	 */
	public String getCoverUrl() {
		return coverUrl;
	}

	/**
	 * @param coverUrl - the coverUrl to set
	 */
	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	/**
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Book [id=").append(id);
		builder.append(", title=").append(title);
		builder.append(", authors=").append(authors);
		builder.append(", isbn=").append(isbn);
		builder.append(", description=").append(description != null);
		builder.append(", pricepoints=").append(pricepoints);
		builder.append(", publishingHouse=").append(publishingHouse);
		builder.append(", releaseYear=").append(releaseYear);
		builder.append(", format=").append(format);
		builder.append(", coverUrl=").append(coverUrl);
		builder.append("]");
		return builder.toString();
	}
}
