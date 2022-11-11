package com.morakmorak.morak_back_end.repository;


import com.morakmorak.morak_back_end.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
