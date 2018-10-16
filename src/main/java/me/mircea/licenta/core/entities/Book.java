package me.mircea.licenta.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import me.mircea.licenta.core.utils.Normalizer;

@Entity
@Table(name = "books")
public class Book {
	private static final Logger logger = LoggerFactory.getLogger(Book.class);
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String title;

	@ElementCollection(fetch = FetchType.EAGER)
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
		this.pricepoints = new HashSet<>();
	}

	public Book(Integer id, String title, String description, List<String> authors) {
		super();

		Preconditions.checkNotNull(authors);
		this.id = id;
		this.title = title;
		this.description = description;
		this.authors = authors;
		this.pricepoints = new HashSet<>();
	}

	private Book(Book persisted, Book addition) {
		this();
		Preconditions.checkNotNull(persisted);
		Preconditions.checkNotNull(addition);
		
		boolean oneHasNullIsbn = (persisted.isbn == null || addition.isbn == null);
		boolean haveNonNullAndNonEqualIsbns = (persisted.isbn != null && addition.isbn != null && persisted.isbn.equals(addition.isbn));
		Preconditions.checkArgument(oneHasNullIsbn || haveNonNullAndNonEqualIsbns, "Can not merge the two objects", persisted, addition);
		
		id = persisted.id;
		title = (String)Normalizer.getNotNullIfPossible(persisted.title, addition.title);
		authors = Normalizer.getLongestOfLists(persisted.authors, addition.authors);
		isbn = (String)Normalizer.getNotNullIfPossible(persisted.isbn, addition.isbn);
		description = Normalizer.getLongestOfNullableStrings(persisted.description, addition.description);
		
		pricepoints = persisted.pricepoints;	
		pricepoints.addAll(addition.pricepoints);
		
		publishingHouse = (String)Normalizer.getNotNullIfPossible(persisted.publishingHouse, addition.publishingHouse);
		releaseYear = (Integer)Normalizer.getNotNullIfPossible(persisted.releaseYear, addition.releaseYear);
		format = (String) Normalizer.getNotNullIfPossible(persisted.format, addition.format);
		//TODO: maybe get reachable url
		coverUrl = (String)Normalizer.getNotNullIfPossible(persisted.coverUrl, addition.coverUrl);
	}
	
	/**
	 * @param persisted
	 * @param addition
	 * @return An object resulted from a merger that strives for completeness.
	 */
	public static Optional<Book> merge(Book persisted, Book addition) {
		Book merged = null;
		
		try {
			merged = new Book(persisted, addition);
		} catch (Exception e) {
			logger.error("Unsuccessful merger of two books {}", e);
		}
		
		return Optional.ofNullable(merged);
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
		this.isbn = isbn.replaceAll("[-\\ ]", "");
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authors == null) ? 0 : authors.hashCode());
		result = prime * result + ((coverUrl == null) ? 0 : coverUrl.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
		result = prime * result + ((pricepoints == null) ? 0 : pricepoints.hashCode());
		result = prime * result + ((publishingHouse == null) ? 0 : publishingHouse.hashCode());
		result = prime * result + ((releaseYear == null) ? 0 : releaseYear.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Book)) {
			return false;
		}
		Book other = (Book) obj;
		if (authors == null) {
			if (other.authors != null) {
				return false;
			}
		} else if (!authors.equals(other.authors)) {
			return false;
		}
		if (coverUrl == null) {
			if (other.coverUrl != null) {
				return false;
			}
		} else if (!coverUrl.equals(other.coverUrl)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (format == null) {
			if (other.format != null) {
				return false;
			}
		} else if (!format.equals(other.format)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (isbn == null) {
			if (other.isbn != null) {
				return false;
			}
		} else if (!isbn.equals(other.isbn)) {
			return false;
		}
		if (pricepoints == null) {
			if (other.pricepoints != null) {
				return false;
			}
		} else if (!pricepoints.equals(other.pricepoints)) {
			return false;
		}
		if (publishingHouse == null) {
			if (other.publishingHouse != null) {
				return false;
			}
		} else if (!publishingHouse.equals(other.publishingHouse)) {
			return false;
		}
		if (releaseYear == null) {
			if (other.releaseYear != null) {
				return false;
			}
		} else if (!releaseYear.equals(other.releaseYear)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
	}
	
}
