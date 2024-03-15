package com.api.dms.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.api.dms.auth.db.repository.TbAuthRepository;

@Service
public class AuthService {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private TbAuthRepository tbAuthRepository;	
}
