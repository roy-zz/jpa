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
			String query = "SELECT " +
						   "NULLIF(C.name, '로이') " +
					       "FROM Customer C ";

			List<TaxiDriver> results = entityManager.createQuery(query, TaxiDriver.class)
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
