package com.example.ai_travel_agent_app.repository.worker;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.model.WorkerStatus;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {

    Optional<Worker> findByUser(User user);

        // Return the first matching worker for a user to avoid NonUniqueResultException
        Optional<Worker> findFirstByUser(User user);

    List<Worker> findByStatus(WorkerStatus status);

    // Random workers query
    @Query("SELECT w FROM Worker w WHERE w.status = :status ORDER BY FUNCTION('NEWID')")
    List<Worker> findByStatusRandomOrder(@Param("status") WorkerStatus status);

    @Query("SELECT w FROM Worker w JOIN w.services s WHERE w.status = :status AND s.isActive = true GROUP BY w ORDER BY FUNCTION('NEWID')")
    List<Worker> findByStatusAndServicesIsActiveTrueRandomOrder(@Param("status") WorkerStatus status);

    long countByStatus(WorkerStatus status);

    List<Worker> findByStatusAndServicesIsActiveTrue(WorkerStatus status);
    
    // New methods for AI Agent search
    List<Worker> findByAddressContainingIgnoreCaseAndStatus(String address, WorkerStatus status);
    
    @Query("SELECT DISTINCT w FROM Worker w " +
           "JOIN w.services s " +
           "JOIN s.categories c " +
           "WHERE LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :categoryName, '%')) " +
           "AND w.status = :status " +
           "AND s.isActive = true")
    List<Worker> findByServicesCategories_CategoryNameContainingIgnoreCaseAndStatus(
            @Param("categoryName") String categoryName, 
            @Param("status") WorkerStatus status);
    
    @Query("SELECT DISTINCT w FROM Worker w " +
           "JOIN w.services s " +
           "WHERE (LOWER(s.serviceName) LIKE LOWER(CONCAT('%', :serviceName, '%')) " +
           "OR LOWER(CAST(s.serviceDescription AS string)) LIKE LOWER(CONCAT('%', :serviceName, '%'))) " +
           "AND w.status = :status " +
           "AND s.isActive = true")
    List<Worker> findByServicesServiceNameContainingIgnoreCaseAndStatus(
            @Param("serviceName") String serviceName, 
            @Param("status") WorkerStatus status);
    
    @Query("SELECT DISTINCT w FROM Worker w " +
           "JOIN w.services s " +
           "WHERE (LOWER(s.serviceName) LIKE LOWER(CONCAT('%', :serviceName, '%')) " +
           "OR LOWER(CAST(s.serviceDescription AS string)) LIKE LOWER(CONCAT('%', :serviceName, '%'))) " +
           "AND LOWER(w.address) LIKE LOWER(CONCAT('%', :location, '%')) " +
           "AND w.status = :status " +
           "AND s.isActive = true")
    List<Worker> findByServicesServiceNameAndLocationContainingIgnoreCaseAndStatus(
            @Param("serviceName") String serviceName,
            @Param("location") String location,
            @Param("status") WorkerStatus status);

    /**
     * Lấy featured workers dựa theo số lượng reviews và rating
     */
    @Query(value = """
            SELECT w.id as workerId,
                   w.full_name as fullName,
                   COALESCE(u.avatar, 'https://via.placeholder.com/150') as avatar,
                   w.address as address,
                   STRING_AGG(s.service_name, ' - ') as services,
                   COALESCE(COUNT(r.review_id), 0) as reviewCount,
                   COALESCE(AVG(CAST(r.rating AS FLOAT)), 0.0) as averageRating,
                   CASE WHEN AVG(CAST(r.rating AS FLOAT)) >= 4.5 AND COUNT(r.review_id) >= 10 THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END as isPro,
                   COALESCE(COUNT(r.review_id), 0) as completedJobs
            FROM workers w
            JOIN users u ON w.user_user_id = u.user_id
            LEFT JOIN services s ON s.worker_id = w.id AND s.is_active = 1
            LEFT JOIN reviews r ON r.worker_id = w.id
            WHERE w.status = 'ACTIVE'
            GROUP BY w.id, w.full_name, u.avatar, w.address
            HAVING COUNT(r.review_id) > 0
            ORDER BY NEWID()
            """, nativeQuery = true)
    List<Object[]> findFeaturedWorkersByReviews();

    /**
     * Lấy featured workers dựa theo số lượng reviews và rating với thứ tự cao nhất (cho top worker)
     */
    @Query(value = """
            SELECT w.id as workerId,
                   w.full_name as fullName,
                   COALESCE(u.avatar, 'https://via.placeholder.com/150') as avatar,
                   w.address as address,
                   STRING_AGG(s.service_name, ' - ') as services,
                   COALESCE(COUNT(r.review_id), 0) as reviewCount,
                   COALESCE(AVG(CAST(r.rating AS FLOAT)), 0.0) as averageRating,
                   CASE WHEN AVG(CAST(r.rating AS FLOAT)) >= 4.5 AND COUNT(r.review_id) >= 10 THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END as isPro,
                   COALESCE(COUNT(r.review_id), 0) as completedJobs
            FROM workers w
            JOIN users u ON w.user_user_id = u.user_id
            LEFT JOIN services s ON s.worker_id = w.id AND s.is_active = 1
            LEFT JOIN reviews r ON r.worker_id = w.id
            WHERE w.status = 'ACTIVE'
            GROUP BY w.id, w.full_name, u.avatar, w.address
            HAVING COUNT(r.review_id) > 0
            ORDER BY COUNT(r.review_id) DESC, AVG(CAST(r.rating AS FLOAT)) DESC
            """, nativeQuery = true)
    List<Object[]> findTopWorkerByReviews();
}
