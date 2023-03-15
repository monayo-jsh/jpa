import domain.Member;
import domain.MemberType;
import domain.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JPQLCollectionFetchJoin {

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

            String query = "select t from Team t join fetch t.members where t.name = '팀A'";
            List<Team> resultList = entityManager.createQuery(query, Team.class)
                                                 .getResultList();

            for(Team team : resultList) {
                System.out.println("team.getName() = " + team.getName() + ", team.members() = " + team.getMembers().size());
            }

            //패치 조인과 distinct
            //sql의 distinct는 중복된 결과를 제거하는 명령
            //jpql의 distinct는 2가지 기능 제공
            // - sql의 distinct 추가
            // - 애플리케이션에서 엔티티 중복 제거
            query = "select distinct t from Team t join fetch t.members where t.name = '팀A'";
            resultList = entityManager.createQuery(query, Team.class)
                                                 .getResultList();

            for(Team team : resultList) {
                System.out.println("team.getName() = " + team.getName() + ", team.members() = " + team.getMembers().size());
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
