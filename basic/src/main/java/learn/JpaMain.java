package learn;

import com.sun.tools.javac.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import learn.domain.Member;
import learn.domain.common.Address;

public class JpaMain {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        try {

            Member member = new Member();
            member.setName("member1");
            member.setAddress(new Address("city", "street", "zipcode"));

            member.getFavoriteFoods().addAll(
                List.of("치킨", "족발", "피자")
            );

            member.getAddressHistory().addAll(
              List.of(
                  new Address("city1", "street1", "zipcode1"),
                  new Address("city2", "street2", "zipcode2"),
                  new Address("city3", "street3", "zipcode3")
              )
            );

            entityManager.persist(member);

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
