import domain.Address;
import domain.Member;
import domain.MemberDTO;
import domain.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPQLSelectMain {

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
            member.setUsername("memeber1");
            member.setAge(10);

            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            //엔티티 프로젝션
            List<Member> members = entityManager.createQuery("select m from Member m", Member.class)
                                                .getResultList();

            //엔티티 프로젝션 - 이런 형태도 가능하지만 사용 X
            //List<Team> Teams = entityManager.createQuery("select m.team from Member m", Team.class)
            //                                    .getResultList();
            //엔티티 프로젝션 - 다음과 같이 사용할 것을 권장 - 쿼리가 예상되어야 함.
            List<Team> Teams = entityManager.createQuery("select t from Member m join m.team t ", Team.class)
                                            .getResultList();

            //임베디드 타입 프로젝션
            List<Address> addresses = entityManager.createQuery("select o.address from Order o", Address.class)
                                                   .getResultList();

            //스칼라 타입 프로젝션
            List results = entityManager.createQuery("select m.username, m.age from Member m")
                                        .getResultList();

            //distinct 조회
            List distincts = entityManager.createQuery("select distinct m.username, m.age from Member m")
                                          .getResultList();

            //new 명령어로 조회 - 생성자로 객체 반환
            List<MemberDTO> memberDTOs = entityManager.createQuery("select new domain.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                                                      .getResultList();

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
