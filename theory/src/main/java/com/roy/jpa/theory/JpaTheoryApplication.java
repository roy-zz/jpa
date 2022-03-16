package com.roy.jpa.theory;

import com.roy.jpa.theory.entity.TaxiDriver;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaTheoryApplication {

	public static void main(String[] args) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		try {
			for (int i = 0; i < 100; i++) {
				TaxiDriver taxiDriver = new TaxiDriver();
				taxiDriver.setName(String.valueOf(i));
				entityManager.persist(taxiDriver);
			}

			String query = "SELECT TD FROM TaxiDriver TD";
			List<TaxiDriver> results = entityManager.createQuery(query, TaxiDriver.class)
					.setFirstResult(50)
					.setMaxResults(20)
					.getResultList();

			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			entityManager.close();
		}
		entityManagerFactory.close();
	}

}
