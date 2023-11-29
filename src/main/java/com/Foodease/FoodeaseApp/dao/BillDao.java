package com.Foodease.FoodeaseApp.dao;

import com.Foodease.FoodeaseApp.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillDao extends JpaRepository<Bill,Integer> {
}
