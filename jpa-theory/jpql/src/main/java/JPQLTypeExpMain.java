import domain.Member;
import domain.MemberType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPQLTypeExpMain {

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
            member.setUsername("Member");
            member.setType(MemberType.ADMIN);

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            String query = "select m.username, 'HELLO', true, m.type from Member m where m.type = :userType";
            List<Object[]> results = entityManager.createQuery(query)
                                                  .setParameter("userType", MemberType.ADMIN)
                                                  .getResultList();

            for(Object[] objects : results) {
                System.out.println("objects[0] = " + objects[0]);
                System.out.println("objects[1] = " + objects[1]);
                System.out.println("objects[2] = " + objects[2]);
                System.out.println("objects[3] = " + objects[3]);
            }

            //엔티티 타입으로 상속 관계 조회에서 사용
            //아이템을 조회하는데 Book 만 조회하려고 할 때
            //entityManager.createQuery("select i from Item i where type(i) = Book", Item.class);

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
