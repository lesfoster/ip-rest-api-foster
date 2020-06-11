package com.project.challenge.repositories;

import com.project.challenge.entities.CidrBitBlock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<CidrBitBlock, Integer> {
}
