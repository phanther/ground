package edu.berkeley.ground.api.models.neo4j;

import edu.berkeley.ground.api.models.Structure;
import edu.berkeley.ground.api.models.StructureFactory;
import edu.berkeley.ground.api.versions.GroundType;
import edu.berkeley.ground.api.versions.neo4j.Neo4jItemFactory;
import edu.berkeley.ground.db.DBClient.GroundDBConnection;
import edu.berkeley.ground.db.DbDataContainer;
import edu.berkeley.ground.db.Neo4jClient;
import edu.berkeley.ground.db.Neo4jClient.Neo4jConnection;
import edu.berkeley.ground.exceptions.GroundException;
import org.neo4j.driver.v1.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Neo4jStructureFactory extends StructureFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(Neo4jStructureFactory.class);
    private Neo4jClient dbClient;
    private Neo4jItemFactory itemFactory;

    public Neo4jStructureFactory(Neo4jClient dbClient, Neo4jItemFactory itemFactory) {
        this.dbClient = dbClient;
        this.itemFactory = itemFactory;
    }

    public Structure create(String name) throws GroundException {
        Neo4jConnection connection = this.dbClient.getConnection();

        try {
            String uniqueId = "Structures." + name;

            List<DbDataContainer> insertions = new ArrayList<>();
            insertions.add(new DbDataContainer("name", GroundType.STRING, name));
            insertions.add(new DbDataContainer("id", GroundType.STRING, uniqueId));

            connection.addVertex("Structure", insertions);

            connection.commit();
            LOGGER.info("Created structure " + name + ".");

            return StructureFactory.construct(uniqueId, name);
        } catch (GroundException e) {
            connection.abort();

            throw e;
        }
    }

    public Structure retrieveFromDatabase(String name)  throws GroundException {
        Neo4jConnection connection = this.dbClient.getConnection();

        try {
            List<DbDataContainer> predicates = new ArrayList<>();
            predicates.add(new DbDataContainer("name", GroundType.STRING, name));
            predicates.add(new DbDataContainer("label", GroundType.STRING, "Nodes"));

            Record record = connection.getVertex(predicates);

            String id = record.get("id").toString();

            connection.commit();
            LOGGER.info("Retrieved structure " + name + ".");

            return StructureFactory.construct(id, name);
        } catch (GroundException e) {
            connection.abort();

            throw e;
        }
    }

    public void update(GroundDBConnection connection, String itemId, String childId, Optional<String> parent) throws GroundException {
        this.itemFactory.update(connection, "Structures." + itemId, childId, parent);
    }

}