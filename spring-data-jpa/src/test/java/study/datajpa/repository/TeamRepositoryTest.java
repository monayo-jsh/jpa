package study.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
class TeamRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Test
    void basicCRUD() {
        //C
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        Team teamC = new Team("teamC");

        teamRepository.save(teamA);
        teamRepository.save(teamB);
        teamRepository.save(teamC);

        //R
        Optional<Team> findOptionalTeam = teamRepository.findById(teamA.getId());

        assertThat(findOptionalTeam.isPresent()).isTrue();

        Team findTeam = findOptionalTeam.get();

        assertThat(findTeam.getId()).isEqualTo(teamA.getId());
        assertThat(findTeam.getName()).isEqualTo(teamA.getName());

        long count = teamRepository.count();
        assertThat(count).isEqualTo(3);

        List<Team> teams = teamRepository.findAll();

        for(Team team : teams) {
            System.out.println("team = " + team);
        }

        //D
        teamRepository.delete(teamB);

        Optional<Team> deleteOptionalTeam = teamRepository.findById(teamB.getId());

        assertThat(deleteOptionalTeam.isEmpty()).isTrue();

        count = teamRepository.count();
        assertThat(count).isEqualTo(2);
    }
}