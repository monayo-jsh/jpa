package learn;

import com.sun.tools.javac.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import learn.domain.Member;
import learn.domain.common.Address;
import learn.domain.common.AddressEntity;

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
            member.setName("member2");
            member.setAddress(new Address("city", "street", "zipcode"));

            member.getFavoriteFoods().addAll(
                List.of("치킨", "족발", "피자")
            );

            member.getAddressHistory().addAll(
              List.of(
                  new AddressEntity(new Address("city1", "street1", "zipcode1")),
                  new AddressEntity(new Address("city2", "street2", "zipcode2")),
                  new AddressEntity(new Address("city3", "street3", "zipcode3"))
              )
            );

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            Member findMember = entityManager.find(Member.class, member.getId());

            //잘못된 설정 신규 객체로 변경하는게 옳다.
            findMember.setAddress(new Address("city modify", "street modify", "zipcode modify"));
            //findMember.getAddressHistory().remove(2);
            //equals를 통해 객체 삭제
            findMember.getAddressHistory().remove(0);

            //컬렉션이므로 해당하는 데이터를 삭제, 추가 처리
            findMember.getFavoriteFoods().remove("치킨");
            findMember.getFavoriteFoods().add("한식");

            entityManager.persist(findMember);

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
