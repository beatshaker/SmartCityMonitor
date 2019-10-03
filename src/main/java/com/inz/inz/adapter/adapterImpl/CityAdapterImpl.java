package com.inz.inz.adapter.adapterImpl;

import com.inz.inz.ExcpetionHandler.DbException;
import com.inz.inz.adapter.CityAdapter;
import com.inz.inz.entity.CityEntity;
import com.inz.inz.mapper.CityMapper;
import com.inz.inz.repository.CityEntityRepository;
import com.inz.inz.resoruce.CityResource;
import com.inz.inz.resoruce.CityResourceGetLight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CityAdapterImpl implements CityAdapter {

    @Autowired
    CityMapper cityMapper;

    @Autowired
    CityEntityRepository cityEntityRepository;

    @Override
    public List<CityResourceGetLight> getCities() throws DbException {

        List<CityEntity> cityEntities = cityEntityRepository.findAll();
        if (cityEntities != null) {
            return cityEntities.stream().map(cityMapper::mapToCityResourceGetLight).collect(Collectors.toList());
        } else {
            throw new DbException("ResourceNotFound", "D3", null, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public CityResource getCity(Long id) throws DbException {
        Optional<CityEntity> cityEntity = cityEntityRepository.findById(id);
        if (cityEntity.isPresent()) {
            return cityMapper.mapToCityResource(cityEntity.get());
        } else {
            throw new DbException("ResourceNotFound", "D3", null, HttpStatus.NOT_FOUND);
        }
    }
}
