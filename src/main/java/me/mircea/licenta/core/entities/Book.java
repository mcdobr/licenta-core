package me.mircea.licenta.core.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import me.mircea.licenta.core.utils.Normalizer;

@Entity
public class Book {
	private static final Logger logger = LoggerFactory.getLogger(Book.class);
	
	@Id
	private Long id;
	@Index
	private String title;
	@Index
	private List<String> authors;
	@Index
	private String isbn;
	private String description;
	private List<Key<PricePoint>> pricepoints;
	private String publisher;
	private Integer releaseYear;
	private String format;
	private String imageUrl;

	public Book() {
		this.authors = new ArrayList<>();
		this.pricepoints = new ArrayList<>();
	}

	public Book(Long id, String title, String description, List<String> authors) {
		super();

		Preconditions.checkNotNull(authors);
		this.id = id;
		this.title = title;
		this.description = description;
		this.authors = authors;
		this.pricepoints = new ArrayList<>();
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
		
		publisher = (String)Normalizer.getNotNullIfPossible(persisted.publisher, addition.publisher);
		releaseYear = (Integer)Normalizer.getNotNullIfPossible(persisted.releaseYear, addition.releaseYear);
		format = (String) Normalizer.getNotNullIfPossible(persisted.format, addition.format);
		//TODO: maybe get reachable url
		imageUrl = (String)Normalizer.getNotNullIfPossible(persisted.imageUrl, addition.imageUrl);
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
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
		this.isbn = isbn.replaceAll("[-\\ ]", "");
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Key<PricePoint>> getPricepoints() {
		return pricepoints;
	}

	public void setPricepoints(List<Key<PricePoint>> pricepoints) {
		this.pricepoints = pricepoints;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Integer getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Book [id=").append(id);
		builder.append(", title=").append(title);
		builder.append(", authors=").append(authors);
		builder.append(", isbn=").append(isbn);
		builder.append(", description=").append(description != null);
		builder.append(", pricepoints=").append(pricepoints);
		builder.append(", publisher=").append(publisher);
		builder.append(", releaseYear=").append(releaseYear);
		builder.append(", format=").append(format);
		builder.append(", imageUrl=").append(imageUrl);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authors == null) ? 0 : authors.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((format == null) ? 0 : format.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
		result = prime * result + ((pricepoints == null) ? 0 : pricepoints.hashCode());
		result = prime * result + ((publisher == null) ? 0 : publisher.hashCode());
		result = prime * result + ((releaseYear == null) ? 0 : releaseYear.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

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
		if (imageUrl == null) {
			if (other.imageUrl != null) {
				return false;
			}
		} else if (!imageUrl.equals(other.imageUrl)) {
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
		if (publisher == null) {
			if (other.publisher != null) {
				return false;
			}
		} else if (!publisher.equals(other.publisher)) {
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
