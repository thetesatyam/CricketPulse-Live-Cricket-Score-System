package com.cricketpulse.repository;

import com.cricketpulse.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByInningsIdOrderBySequenceNumberAsc(Long inningsId);
    List<Delivery> findByInningsIdAndIsReversedFalseOrderBySequenceNumberDesc(Long inningsId);
    List<Delivery> findByInningsIdAndOverNumber(Long inningsId, int overNumber);

    @Query("SELECT MAX(d.sequenceNumber) FROM Delivery d WHERE d.innings.id = :inningsId")
    Optional<Integer> findMaxSequenceNumberByInningsId(Long inningsId);

    @Query("SELECT d FROM Delivery d WHERE d.innings.match.id = :matchId AND d.isReversed = false ORDER BY d.id DESC")
    List<Delivery> findLatestDeliveriesByMatchId(Long matchId);
}
