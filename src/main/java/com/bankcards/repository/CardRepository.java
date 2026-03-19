package com.bankcards.repository;

import com.bankcards.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, Integer> {
    List<CardEntity> findByUser_Id(Integer userId);
    Optional<CardEntity> findByNumber(String number);
}
