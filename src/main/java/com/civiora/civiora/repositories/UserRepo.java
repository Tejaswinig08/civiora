package com.civiora.civiora.repositories;

import com.civiora.civiora.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User,Integer>
{
    User findByNameAndWingAndFlatAndRoleAndPassword(String name, String wing, String flat, String role, String password);
    User findByEmail(String email);
}