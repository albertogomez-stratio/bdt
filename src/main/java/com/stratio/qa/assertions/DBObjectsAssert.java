/*
 * Copyright (c) 2014. Stratio Big Data Inc., Sucursal en España. All rights reserved.
 *
 * This software is licensed under the Apache Licence 2.0. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the terms of the License for more details.
 * SPDX-License-Identifier: Artistic-2.0
 */

package com.stratio.qa.assertions;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import cucumber.api.DataTable;
import org.assertj.core.api.AbstractAssert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DBObjectsAssert extends AbstractAssert<DBObjectsAssert, ArrayList<DBObject>> {


    public DBObjectsAssert(ArrayList<DBObject> actual) {
        super(actual, DBObjectsAssert.class);
    }

    public static DBObjectsAssert assertThat(ArrayList<DBObject> actual) {
        return new DBObjectsAssert(actual);
    }

    public DBObjectsAssert containedInMongoDBResult(DataTable table) {
        boolean resultado = matchesSafely(actual, table);
        if (resultado == false) {
            failWithMessage("The table does not contains the data required.");
        }
        return new DBObjectsAssert(actual);
    }

    protected boolean matchesSafely(ArrayList<DBObject> item, DataTable table) {
        List<String[]> colRel = coltoArrayList(table);

        for (int i = 1; i < table.raw().size(); i++) {
            // Obtenemos la fila correspondiente
            BasicDBObject doc = new BasicDBObject();
            List<String> row = table.raw().get(i);
            for (int x = 0; x < row.size(); x++) {
                String[] colNameType = colRel.get(x);
                Object data = castSTringTo(colNameType[1], row.get(x));
                doc.put(colNameType[0], data);
            }
            if (!isContained(item, doc)) {
                return false;
            }
        }
        return true;
    }

    private boolean isContained(List<DBObject> item, BasicDBObject doc) {
        boolean res = false;
        for (int i = 0; i < item.size() && !res; i++) {
            DBObject aux = item.get(i);
            aux.removeField("_id");
            aux.removeField("timestamp");

            if (aux.keySet().equals(doc.keySet())) {
                res = true;
            }
            // Obtenemos los columnNames
            List<String> cols = new ArrayList<String>(doc.keySet());
            for (int x = 0; x < cols.size() && res; x++) {
                if (!aux.get(cols.get(x)).equals(doc.get(cols.get(x)))) {
                    res = false;
                } else {
                    res = true;
                }
            }
        }
        return res;
    }


    private List<String[]> coltoArrayList(DataTable table) {
        List<String[]> res = new ArrayList<String[]>();
        // Primero se obiente la primera fila del datatable
        List<String> firstRow = table.raw().get(0);
        for (int i = 0; i < firstRow.size(); i++) {
            String[] colTypeArray = firstRow.get(i).split("-");
            res.add(colTypeArray);
        }
        return res;
    }


    private Object castSTringTo(String dataType, String data) {
        switch (dataType) {
            case "String":
                return data;
            case "Integer":
                return Integer.parseInt(data);
            case "Double":
                return Double.parseDouble(dataType);
            case "Boolean":
                return Boolean.parseBoolean(dataType);
            case "Timestamp":
                return Timestamp.valueOf(dataType);
            default:
                return null;
        }
    }


}
