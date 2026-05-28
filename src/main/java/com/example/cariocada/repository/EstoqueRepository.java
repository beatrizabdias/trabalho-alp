package com.example.cariocada.repository;

import com.example.cariocada.model.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    // O Spring Boot vai gerar os métodos como o findAll() e save() automaticamente aqui
}