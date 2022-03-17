package com.roy.jpa.theory;

import com.roy.jpa.theory.entity.TaxiDriver;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaTheoryApplication {

	public static void main(String[] args) {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();

		try {
			TaxiDriver driver1 = new TaxiDriver();
			driver1.setName("1번 기사님");

			String query = "UPDATE TaxiDriver TD SET TD.phone = FUNCTION('REPLACE', TD.phone, '-', '')";
			int resultCount = entityManager.createQuery(query)
					.executeUpdate();


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
