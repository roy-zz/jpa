package com.roy.jpa.theory;

import com.roy.jpa.theory.dto.TaxiEventDTO;
import com.roy.jpa.theory.entity.TaxiEvent;

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
			TaxiEvent taxiEvent1 = new TaxiEvent();
			taxiEvent1.setCost(1000);
			TaxiEvent taxiEvent2 = new TaxiEvent();
			taxiEvent2.setCost(2000);
			entityManager.persist(taxiEvent1);
			entityManager.persist(taxiEvent2);

			List<TaxiEventDTO> result = entityManager.createQuery(
					"SELECT new com.roy.jpa.theory.dto.TaxiEventDTO(TE.id, TE.cost) " +
							"FROM TaxiEvent TE WHERE TE.cost > 500", TaxiEventDTO.class)
					.getResultList();
			result.forEach(i -> {
				System.out.println("i.toString() = " + i.toString());
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
