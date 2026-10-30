package com.cricketpulse.service;

import com.cricketpulse.dto.CreatePlayerRequest;
import com.cricketpulse.dto.PlayerStatsDto;
import com.cricketpulse.entity.Player;
import com.cricketpulse.entity.PlayerMatchStats;
import com.cricketpulse.entity.Team;
import com.cricketpulse.exception.ResourceNotFoundException;
import com.cricketpulse.repository.PlayerMatchStatsRepository;
import com.cricketpulse.repository.PlayerRepository;
import com.cricketpulse.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final PlayerMatchStatsRepository playerMatchStatsRepository;
    private final AuditService auditService;

    public PlayerService(PlayerRepository playerRepository,
                         TeamRepository teamRepository,
                         PlayerMatchStatsRepository playerMatchStatsRepository,
                         AuditService auditService) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.playerMatchStatsRepository = playerMatchStatsRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Player getPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Player> getPlayersByTeamId(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }

    @Transactional
    public Player createPlayer(CreatePlayerRequest request) {
        Team team = null;
        if (request.getTeamId() != null) {
            team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + request.getTeamId()));
        }

        Player player = new Player(
                request.getFullName(),
                team,
                request.getBattingStyle(),
                request.getBowlingStyle(),
                request.getPlayingRole()
        );
        player.setProfileImageUrl(request.getProfileImageUrl());

        Player saved = playerRepository.save(player);
        auditService.log("PLAYER_CREATE", "Player", saved.getId(), "Created player: " + saved.getFullName());
        return saved;
    }

    @Transactional(readOnly = true)
    public PlayerStatsDto getPlayerStatistics(Long playerId) {
        Player player = getPlayerById(playerId);
        List<PlayerMatchStats> statsList = playerMatchStatsRepository.findByPlayerId(playerId);

        PlayerStatsDto dto = new PlayerStatsDto();
        dto.setPlayerId(player.getId());
        dto.setFullName(player.getFullName());
        if (player.getTeam() != null) {
            dto.setTeamName(player.getTeam().getName());
            dto.setTeamShortName(player.getTeam().getShortName());
        }
        dto.setPlayingRole(player.getPlayingRole());

        int totalRuns = 0;
        int ballsFaced = 0;
        int fours = 0;
        int sixes = 0;
        int timesOut = 0;
        int wickets = 0;
        int legalBallsBowled = 0;
        int runsConceded = 0;

        for (PlayerMatchStats s : statsList) {
            totalRuns += s.getRunsScored();
            ballsFaced += s.getBallsFaced();
            fours += s.getFours();
            sixes += s.getSixes();
            if (s.isOut()) timesOut++;
            wickets += s.getWicketsTaken();
            legalBallsBowled += s.getLegalBallsBowled();
            runsConceded += s.getRunsConceded();
        }

        dto.setMatchesPlayed(statsList.size());
        dto.setTotalRuns(totalRuns);
        dto.setBallsFaced(ballsFaced);
        dto.setFours(fours);
        dto.setSixes(sixes);
        dto.setBattingAverage(timesOut > 0 ? Math.round(((double) totalRuns / timesOut) * 100.0) / 100.0 : totalRuns);
        dto.setStrikeRate(ballsFaced > 0 ? Math.round(((double) totalRuns / ballsFaced) * 10000.0) / 100.0 : 0.0);
        dto.setWickets(wickets);
        dto.setLegalBallsBowled(legalBallsBowled);
        dto.setRunsConceded(runsConceded);
        if (legalBallsBowled > 0) {
            double overs = legalBallsBowled / 6.0;
            dto.setBowlingEconomy(Math.round((runsConceded / overs) * 100.0) / 100.0);
        } else {
            dto.setBowlingEconomy(0.0);
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public List<PlayerStatsDto> getTopRunScorers() {
        List<Object[]> rows = playerMatchStatsRepository.findTopRunScorersGlobal();
        List<PlayerStatsDto> list = new ArrayList<>();
        int count = 0;
        for (Object[] row : rows) {
            if (count++ >= 10) break;
            PlayerStatsDto dto = new PlayerStatsDto();
            dto.setPlayerId(((Number) row[0]).longValue());
            dto.setFullName((String) row[1]);
            dto.setTeamShortName((String) row[2]);
            dto.setTotalRuns(((Number) row[3]).intValue());
            dto.setBallsFaced(((Number) row[4]).intValue());
            dto.setFours(((Number) row[5]).intValue());
            dto.setSixes(((Number) row[6]).intValue());
            if (dto.getBallsFaced() > 0) {
                dto.setStrikeRate(Math.round(((double) dto.getTotalRuns() / dto.getBallsFaced()) * 10000.0) / 100.0);
            }
            list.add(dto);
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<PlayerStatsDto> getTopWicketTakers() {
        List<Object[]> rows = playerMatchStatsRepository.findTopWicketTakersGlobal();
        List<PlayerStatsDto> list = new ArrayList<>();
        int count = 0;
        for (Object[] row : rows) {
            if (count++ >= 10) break;
            PlayerStatsDto dto = new PlayerStatsDto();
            dto.setPlayerId(((Number) row[0]).longValue());
            dto.setFullName((String) row[1]);
            dto.setTeamShortName((String) row[2]);
            dto.setWickets(((Number) row[3]).intValue());
            dto.setLegalBallsBowled(((Number) row[4]).intValue());
            dto.setRunsConceded(((Number) row[5]).intValue());
            if (dto.getLegalBallsBowled() > 0) {
                double overs = dto.getLegalBallsBowled() / 6.0;
                dto.setBowlingEconomy(Math.round((dto.getRunsConceded() / overs) * 100.0) / 100.0);
            }
            list.add(dto);
        }
        return list;
    }
}
