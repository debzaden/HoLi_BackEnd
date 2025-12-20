package com.example.ai_travel_agent_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.ai_travel_agent_app.model.Service;
import com.example.ai_travel_agent_app.model.Worker;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    Optional<Service> findByServiceId(Long serviceId);

    boolean existsByServiceId(Long serviceId);

    void deleteByServiceId(Long serviceId);

    List<Service> findAllByWorker(Worker worker);

    List<Service> findAllByWorkerAndIsActiveTrue(Worker worker);

    long countByIsActiveTrue();

    @Override
    @EntityGraph(attributePaths = "categories")
    List<Service> findAll();

    @EntityGraph(attributePaths = "categories")
    @Query("SELECT s FROM Service s")
    List<Service> findAllWithCategories();

    /**
     * Lấy top categories dựa theo số lượng worker có dịch vụ trong category đó
     */
    @Query("""
            SELECT c.categoryId as categoryId,
                   c.categoryName as categoryName,
                   c.categoryDescription as categoryDescription,
                   COUNT(DISTINCT s.worker) as workerCount,
                   COUNT(s) as serviceCount
            FROM Category c
            JOIN c.services s
            WHERE s.isActive = true
            GROUP BY c.categoryId, c.categoryName, c.categoryDescription
            ORDER BY COUNT(DISTINCT s.worker) DESC
            """)
    List<Object[]> findTopCategoriesByWorkerCount();

    /**
     * Lấy top categories dựa theo số lượng reviews và rating trung bình
     */
    @Query("""
            SELECT c.categoryId as categoryId,
                   c.categoryName as categoryName,
                   c.categoryDescription as categoryDescription,
                   COUNT(DISTINCT s.worker) as workerCount,
                   COUNT(s) as serviceCount,
                   COALESCE(COUNT(r.reviewId), 0) as reviewCount,
                   COALESCE(AVG(r.rating), 0.0) as averageRating
            FROM Category c
            JOIN c.services s
            LEFT JOIN s.worker w
            LEFT JOIN Review r ON r.worker = w
            WHERE s.isActive = true
            GROUP BY c.categoryId, c.categoryName, c.categoryDescription
            ORDER BY COUNT(r.reviewId) DESC, AVG(r.rating) DESC
            """)
    List<Object[]> findTopCategoriesByReviews();

    /**
     * Đếm tổng số services đang active
     */
    @Query("SELECT COUNT(s) FROM Service s WHERE s.isActive = true")
    Long countActiveServices();
}
