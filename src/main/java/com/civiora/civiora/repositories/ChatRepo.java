package com.civiora.civiora.repositories;

import com.civiora.civiora.models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepo extends JpaRepository<ChatMessage, Integer> {

    // ── Public messages (receiverId IS NULL), ordered oldest-first ──
    List<ChatMessage> findByReceiverIdIsNullOrderBySentAtAsc();

    @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.receiverId IS NULL
          AND m.id > :lastId
        ORDER BY m.sentAt ASC
        """)
    List<ChatMessage> findPublicSince(@Param("lastId") int lastId);

    // ── DM thread between two users (both directions), ordered oldest-first ──
    @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.receiverId IS NOT NULL
          AND (
                (m.userId = :userA AND m.receiverId = :userB)
             OR (m.userId = :userB AND m.receiverId = :userA)
          )
        ORDER BY m.sentAt ASC
        """)
    List<ChatMessage> findDmThread(
        @Param("userA") int userA,
        @Param("userB") int userB
    );

    @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.receiverId IS NOT NULL
          AND (
                (m.userId = :userA AND m.receiverId = :userB)
             OR (m.userId = :userB AND m.receiverId = :userA)
          )
          AND m.id > :lastId
        ORDER BY m.sentAt ASC
        """)
    List<ChatMessage> findDmThreadSince(
        @Param("userA") int userA,
        @Param("userB") int userB,
        @Param("lastId") int lastId
    );

    // ── Legacy ──
    List<ChatMessage> findAllByOrderBySentAtAsc();
    List<ChatMessage> findByIdGreaterThanOrderBySentAtAsc(int lastId);

    // ── UNIFIED FEED: public + all DMs involving this user ──────────
    // Used when the UI shows a single combined timeline.
    @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.receiverId IS NULL
           OR m.userId      = :userId
           OR m.receiverId  = :userId
        ORDER BY m.sentAt ASC
        """)
    List<ChatMessage> findVisibleToUser(@Param("userId") int userId);

    @Query("""
        SELECT m FROM ChatMessage m
        WHERE (m.receiverId IS NULL
               OR m.userId     = :userId
               OR m.receiverId = :userId)
          AND m.id > :lastId
        ORDER BY m.sentAt ASC
        """)
    List<ChatMessage> findVisibleToUserSince(
        @Param("userId") int userId,
        @Param("lastId") int lastId
    );
}
