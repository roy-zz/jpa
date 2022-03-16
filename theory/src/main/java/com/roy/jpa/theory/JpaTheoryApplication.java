package com.roy.jpa.theory;

import com.roy.jpa.theory.entity.TaxiCompany;
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
			TaxiCompany company1 = new TaxiCompany();
			company1.setName("A업체");
			TaxiCompany company2 = new TaxiCompany();
			company2.setName("B업체");
			TaxiDriver driver1 = new TaxiDriver();
			driver1.setName("1번 기사님");
			driver1.setTaxiCompany(company1);
			TaxiDriver driver2 = new TaxiDriver();
			driver2.setName("2번 기사님");
			driver2.setTaxiCompany(company1);
			TaxiDriver driver3 = new TaxiDriver();
			driver3.setName("3번 기사님");
			driver3.setTaxiCompany(company2);
			entityManager.persist(company1);
			entityManager.persist(company2);
			entityManager.persist(driver1);
			entityManager.persist(driver2);
			entityManager.persist(driver3);
			transaction.commit();
			entityManager.clear();

			transaction.begin();
			String query = "SELECT " +
						   " TC " +
					       "FROM TaxiCompany TC";
			List<TaxiCompany> result = entityManager.createQuery(query, TaxiCompany.class)
					.setFirstResult(0)
					.setMaxResults(2)
					.getResultList();

			result.forEach(i -> {
				System.out.println("업체의 주소값 = " + i.toString());
				for (TaxiDriver taxiDriver : i.getTaxiDrivers()) {
					System.out.println("기사의 주소값 = " + taxiDriver.toString());
				}
			});
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
