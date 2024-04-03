package com.api.cct.backend.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.cct.backend.db.entity.Connector;

public interface ConnectorRepository extends JpaRepository<Connector, String> {
}