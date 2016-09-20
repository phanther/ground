/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.berkeley.ground.api.versions;

import edu.berkeley.ground.db.DBClient.GroundDBConnection;
import edu.berkeley.ground.exceptions.GroundException;

import java.util.ArrayList;
import java.util.List;

public abstract class VersionHistoryDAGFactory {
    public abstract <T extends Version> VersionHistoryDAG<T> create(String itemId) throws GroundException;

    public abstract <T extends Version> VersionHistoryDAG<T> retrieveFromDatabase(GroundDBConnection connection, String itemId) throws GroundException;

    /**
     * Add a new edge between parentId and childId in DAG
     *
     * @param connection
     * @param dag the DAG to update
     * @param parentId the parent's id
     * @param childId the child's id
     * @param itemId the id of the Item whose DAG we're updating
     * @throws GroundException
     */
    public abstract void addEdge(GroundDBConnection connection, VersionHistoryDAG dag, String parentId, String childId, String itemId) throws GroundException;

    protected static <T extends Version> VersionHistoryDAG<T> construct(String itemId) {
        return new VersionHistoryDAG<>(itemId, new ArrayList<>());
    }

    protected static <T extends Version> VersionHistoryDAG<T> construct(String itemId, List<VersionSuccessor<T>> edges) {
        return new VersionHistoryDAG<>(itemId, edges);
    }
}
