package com.murat.featurephone.persistence.repository;

import com.murat.featurephone.persistence.entity.GatewayUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GatewayUserRepository extends JpaRepository<GatewayUserEntity, Long> {

    Optional<GatewayUserEntity> findByUsername(String username);
}
