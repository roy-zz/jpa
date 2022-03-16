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

		TaxiDriver taxiDriver1 = new TaxiDriver();
		taxiDriver1.setName("1번 기사님의 이름");
		TaxiDriver taxiDriver2 = new TaxiDriver();
		taxiDriver2.setName("2번 기사님의 이름");
		TaxiDriver taxiDriver3 = new TaxiDriver();
		taxiDriver3.setName("3번 기사님의 이름");
		entityManager.persist(taxiDriver1);
		entityManager.persist(taxiDriver2);
		entityManager.persist(taxiDriver3);

		try {
			String query = "SELECT " +
						   "FUNCTION('GROUP_CONCAT', TD.name) " +
					       "FROM TaxiDriver TD ";

			String results = entityManager.createQuery(query, String.class)
					.getSingleResult();

			System.out.println("results = " + results);

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
