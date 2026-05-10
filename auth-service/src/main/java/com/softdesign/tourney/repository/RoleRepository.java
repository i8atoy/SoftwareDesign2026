package com.softdesign.tourney.repository;

import com.softdesign.tourney.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    public Role findByName(String roleName);
}
