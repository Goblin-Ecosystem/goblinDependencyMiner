package com.cifre.sap.su.dataBaseFill.graphGenerator;

import com.cifre.sap.su.dataBaseFill.graphInstruction.*;
import com.cifre.sap.su.model.Link;
import com.cifre.sap.su.model.Release;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Set;

public class SqlGenerator extends DatabaseGenerator {

    private final EntityManager entityManager;
    private final EntityTransaction entityTransaction;

    public SqlGenerator() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
        this.entityManager = emf.createEntityManager();
        entityManager.getTransaction().begin();
        entityTransaction = entityManager.getTransaction();
    }

    @Override
    public void accept(GraphInstruction graphInstruction) {
        if(graphInstruction instanceof CreateVertexInstruction){
            Set<Release> releases = (Set<Release>) graphInstruction.getObject();
            for(Release release : releases){
                entityManager.persist(release);
                entityTransaction.commit();
                entityTransaction.begin();
                entityManager.clear();
            }
        }
        else if(graphInstruction instanceof CreateEdgeInstruction){
            List<Link> links = (List<Link>) graphInstruction.getObject();
            for(Link link : links){
                entityManager.persist(link);
                entityTransaction.commit();
                entityTransaction.begin();
                entityManager.clear();
            }
        }
    }
}
