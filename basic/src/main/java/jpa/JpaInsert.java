package jpa;

import domain.Member;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaInsert {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        try {
            Member member = new Member();
            member.setId(1L);
            member.setName("member1");

            entityManager.persist(member);

            transaction.commit();
        } catch(Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
