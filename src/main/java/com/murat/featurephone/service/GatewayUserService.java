package com.murat.featurephone.service;

import com.murat.featurephone.persistence.entity.GatewayUserEntity;
import com.murat.featurephone.persistence.repository.GatewayUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GatewayUserService {

    private final GatewayUserRepository gatewayUserRepository;


    @Transactional
    public GatewayUserEntity create (String username){

        GatewayUserEntity user = new GatewayUserEntity();

        user.setUsername(username);
       return gatewayUserRepository.save(user);

    }


    @Transactional(readOnly = true)
    public GatewayUserEntity getById (Long id){
        return gatewayUserRepository.findById(id)
                .orElseThrow(()->
                        new IllegalArgumentException("GatewayUser not found" + id));
    }
}
