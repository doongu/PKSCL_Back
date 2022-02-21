package com.example.pkscl.repository;

import java.util.List;

import com.example.pkscl.domain.major.Major;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {
    List<Major> findByMajorstatus(String majorstatus);
    Major findByMajornumber(int majornumber);
}