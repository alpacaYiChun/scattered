package com.suneo.flag.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    private static final String TEST_USER = "SuneoAdmin";
    private static final String TEST_PWD = "123456";
	
    @Autowired
    private PasswordEncoder encoder;
    
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (!username.equals(TEST_USER)) {
			throw new UsernameNotFoundException("Bad User");
		}
		
		return User.builder().username(username)
				.passwordEncoder(pwd -> encoder.encode(TEST_PWD))
				.authorities(new SimpleGrantedAuthority("SuneoAdmin"))
				.build();
	}

}
