package org.shiftlab.store.repos;

import org.shiftlab.store.entity.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerRepository extends JpaRepository<SellerEntity, Integer> {

//    @EntityGraph(attributePaths = {"transactions"})
//    @Query("select s from SellerEntity s")
    @Query("select s from SellerEntity s left join fetch s.transactions")
    List<SellerEntity> findAllSellersJoinTransactions();

}
