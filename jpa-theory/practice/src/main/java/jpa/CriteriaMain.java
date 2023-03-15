package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jpa.domain.Member;

public class CriteriaMain {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        try {

            //Criteria 사용법
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

            CriteriaQuery<Member> query = criteriaBuilder.createQuery(Member.class);

            Root<Member> from = query.from(Member.class);

            CriteriaQuery<Member> criteriaQuery = query.select(from)
                                                       .where(criteriaBuilder.equal(from.get("id"), 10L));

            List<Member> members = entityManager.createQuery(criteriaQuery)
                                                .getResultList();

            members.stream().forEach(System.out::println);

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
