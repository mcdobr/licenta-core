package me.mircea.licenta.core.entities;

import javax.persistence.*;

import org.hibernate.Session;

import me.mircea.licenta.core.utils.HibernateUtil;

@Entity
@Table(name = "sites")
public class Site {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String url;

	public Site() {
	}
	
	public static void main(String[] args) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		session.getTransaction().commit();
		session.close();
	}

	public Site(Integer id, String name, String url) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}