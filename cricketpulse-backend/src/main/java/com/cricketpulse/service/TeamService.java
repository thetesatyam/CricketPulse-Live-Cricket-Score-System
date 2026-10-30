package com.cricketpulse.service;

import com.cricketpulse.dto.CreateTeamRequest;
import com.cricketpulse.entity.Team;
import com.cricketpulse.exception.BadRequestException;
import com.cricketpulse.exception.ResourceNotFoundException;
import com.cricketpulse.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final AuditService auditService;

    public TeamService(TeamRepository teamRepository, AuditService auditService) {
        this.teamRepository = teamRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
    }

    @Transactional
    public Team createTeam(CreateTeamRequest request) {
        if (teamRepository.findByName(request.getName()).isPresent()) {
            throw new BadRequestException("Team name already exists: " + request.getName());
        }
        if (teamRepository.findByShortName(request.getShortName()).isPresent()) {
            throw new BadRequestException("Team short name already exists: " + request.getShortName());
        }

        Team team = new Team(request.getName(), request.getShortName(), request.getCountry(), request.getLogoUrl());
        Team saved = teamRepository.save(team);
        auditService.log("TEAM_CREATE", "Team", saved.getId(), "Created team: " + saved.getName());
        return saved;
    }
}
