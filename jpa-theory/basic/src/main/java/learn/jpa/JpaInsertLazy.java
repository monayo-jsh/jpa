package learn.jpa;

import learn.domain.Member;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaInsertLazy {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        try {
            //커밋 시점에 쿼리 수행

            Member member1 = new Member(10L, "member10");
            Member member2 = new Member(20L, "member20");

            System.out.println("before persist");
            entityManager.persist(member1);
            entityManager.persist(member2);
            System.out.println("after persist");

            System.out.println("before transaction commit");
            transaction.commit();
            System.out.println("after transaction commit");
        } catch(Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
