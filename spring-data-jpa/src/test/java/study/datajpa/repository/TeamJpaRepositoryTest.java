package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional
@Rollback(value = false)
class TeamJpaRepositoryTest {

    @Autowired
    TeamJpaRepository teamJpaRepository;


    @Test
    void basicCRUD() {

        //C
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        Team teamC = new Team("teamC");

        teamJpaRepository.save(teamA);
        teamJpaRepository.save(teamB);
        teamJpaRepository.save(teamC);

        //R
        Optional<Team> findOptionalTeam = teamJpaRepository.findById(teamA.getId());

        assertThat(findOptionalTeam.isPresent()).isTrue();

        Team findTeam = findOptionalTeam.get();

        assertThat(findTeam.getId()).isEqualTo(teamA.getId());
        assertThat(findTeam.getName()).isEqualTo(teamA.getName());

        long count = teamJpaRepository.count();
        assertThat(count).isEqualTo(3);

        List<Team> teams = teamJpaRepository.findAll();

        for(Team team : teams) {
            System.out.println("team = " + team);
        }

        //D
        teamJpaRepository.delete(teamB);

        Optional<Team> deleteOptionalTeam = teamJpaRepository.findById(teamB.getId());

        assertThat(deleteOptionalTeam.isEmpty()).isTrue();

        count = teamJpaRepository.count();
        assertThat(count).isEqualTo(2);
    }
    
}