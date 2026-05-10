package com.softdesign.tourney.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads user credentials directly from auth_db via JDBC.
 * This is the pragmatic Option A approach — both services point at the same
 * auth_db so sessions work without needing JWT or a shared session store.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final DataSource dataSource;

    public CustomUserDetailsService(@Qualifier("authDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try (Connection conn = dataSource.getConnection()) {

            // Load the user's password
            PreparedStatement userStmt = conn.prepareStatement(
                    "SELECT id, password FROM users WHERE user_name = ?");
            userStmt.setString(1, username);
            ResultSet userRs = userStmt.executeQuery();

            if (!userRs.next()) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            long userId = userRs.getLong("id");
            String password = userRs.getString("password");

            // Load the user's roles
            PreparedStatement roleStmt = conn.prepareStatement(
                    "SELECT r.name FROM roles r " +
                            "JOIN users_roles ur ON r.id = ur.role_id " +
                            "WHERE ur.user_id = ?");
            roleStmt.setLong(1, userId);
            ResultSet roleRs = roleStmt.executeQuery();

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            while (roleRs.next()) {
                authorities.add(new SimpleGrantedAuthority(roleRs.getString("name")));
            }

            return new User(username, password, authorities);

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error loading user: " + username, e);
        }
    }
}