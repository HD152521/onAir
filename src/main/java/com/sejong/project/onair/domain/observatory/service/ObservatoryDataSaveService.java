package com.sejong.project.onair.domain.observatory.service;

import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObservatoryDataSaveService {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void saveData(ObservatoryData data){
        em.persist(data);
    }
}
