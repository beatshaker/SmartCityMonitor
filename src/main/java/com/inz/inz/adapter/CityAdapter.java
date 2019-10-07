package com.inz.inz.adapter;


import com.inz.inz.ExcpetionHandler.DbException;
import com.inz.inz.resoruce.CityResource;
import com.inz.inz.resoruce.CityResourceGetLight;

import java.util.List;

public interface CityAdapter {

    List<CityResourceGetLight> getCities() throws DbException;

    CityResource getCity(Long id, boolean reportsAcitve) throws DbException;
}