import domain.Member;
import domain.MemberType;
import domain.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPQLFetchJoin {

    public static void main(String[] args) {
        //EntityManagerFactory 하나만 생성해서 애플리케이션 전체에서 공유
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");

        //EntityManager 쓰레드간 공유 X 일회성으로 사용하고 버리기
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        //JPA 는 Transaction 안에서만 동작
        EntityTransaction tx = entityManager.getTransaction();

        tx.begin();
        try {

            Team teamA = new Team();
            teamA.setName("팀A");
            entityManager.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            entityManager.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setAge(10);
            member1.setType(MemberType.USER);
            member1.setTeam(teamA);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setAge(20);
            member2.setType(MemberType.USER);
            member2.setTeam(teamA);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setAge(30);
            member3.setType(MemberType.ADMIN);
            member3.setTeam(teamB);

            Member member4 = new Member();
            member4.setUsername("member4");
            member4.setAge(40);
            member4.setType(MemberType.USER);

            entityManager.persist(member1);
            entityManager.persist(member2);
            entityManager.persist(member3);
            entityManager.persist(member4);

            entityManager.flush();
            entityManager.clear();

            //fetch join -> sql 쿼리 아니고 jpql 문법
            //패치 조인으로 회원과 팀을 함께 조회해서 지연 로딩 X
            String query1 = "select m from Member m join m.team";
            List<Member> resultList = entityManager.createQuery(query1, Member.class)
                                                .getResultList();

            for(Member member : resultList) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
                //회원1, 팀A (SQL 실행 후 응답)
                //회원2, 팀A (1차 캐시에서 가져와 설정 후 응답)
                //회원3, 팀B (SQL 실행 후 응답)

                //회원 100명 -> N + 1
            }

            //fetch join -> sql 쿼리 아니고 jpql 문법
            //패치 조인으로 회원과 팀을 함께 조회해서 지연 로딩 X
            String query2 = "select m from Member m join fetch m.team";
            List<Member> members = entityManager.createQuery(query2, Member.class)
                                                .getResultList();

            for(Member member : members) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
            }

            tx.commit();
        } catch(Exception e) {
            e.printStackTrace();
            tx.rollback();
        }

        entityManager.close();

        entityManagerFactory.close();
    }

}
