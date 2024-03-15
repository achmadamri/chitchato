package com.api.dms.auth.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.dms.auth.db.entity.TbAuth;

public interface TbAuthRepository extends JpaRepository<TbAuth, Integer> {
	public final static String Active = "active";
	public final static String NonActive = "non active";
}