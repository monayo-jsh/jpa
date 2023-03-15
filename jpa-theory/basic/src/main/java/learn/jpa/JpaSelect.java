package learn.jpa;

import learn.domain.Member;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaSelect {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        try {

            // 동일한 Select 2회 호출 시 1번의 Select
            Member member1 = entityManager.find(Member.class, 0L);
            Member member2 = entityManager.find(Member.class, 0L);

            System.out.println("member1 == member2 : " + (member1 == member2));

            transaction.commit();
        } catch(Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
