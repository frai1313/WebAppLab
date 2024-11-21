package com.dipartimento.demowebapplications.persistence.dao;

import com.dipartimento.demowebapplications.model.Ristorante;

import java.util.List;

public interface RistoranteDao {

    public List<Ristorante> findAll();

    public Ristorante findByPrimaryKey(String nome);

    public void create(Ristorante ristorante);

    public void save(Ristorante ristorante);

    public void delete(Ristorante ristorante);

    List<Ristorante> findRistorantiByPiattoName(String nomePiatto);
}
