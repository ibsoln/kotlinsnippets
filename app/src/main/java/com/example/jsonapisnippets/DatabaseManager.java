package com.example.jsonapisnippets;//package com.couchbase.userprofile.util;

import android.content.Context;
import android.util.Log;
import com.couchbase.lite.*;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseChange;
import com.couchbase.lite.DatabaseChangeListener;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.SelectResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
//import MyApp;
import android.content.Context;

public class DatabaseManager {
    private static Database database;
    private static DatabaseManager instance = null;
    private ListenerToken listenerToken;
    private static  String currentUser = setCurrentUser("ian");
    private static String dbName = "hotels";
    private static boolean couchbaseInitialized = false;
    private static Context  ctxt = contextServer.getAppContext();

    protected DatabaseManager() {
        ctxt = contextServer.getAppContext();
    }

    public static DatabaseManager getSharedInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }

    private static String setCurrentUser(String username) {
        if (username==null) {
            return "ian";
        } else {
            return username;
        }
    }

    public static Database getDatabase() {
        return database;
    }

    // tag::initCouchbaseLite[]

    public void initCouchbaseLite(Context parCtxt) {

        if(!couchbaseInitialized) {

            ctxt = parCtxt;

            CouchbaseLite.init(ctxt);

            couchbaseInitialized = true;

        }
    }
    // end::initCouchbaseLite[]

    // tag::userProfileDocId[]
    public String getCurrentUserDocId() {
        return "user::" + currentUser;
    }
    // end::userProfileDocId[]


    // tag::userProfileDocId[]
    public String getCurrentUser() { return currentUser;}

    // end::userProfileDocId[]

    // tag::openOrCreateDatabase[]
    public Database openOrCreateDatabaseForUser(String username)
    // end::openOrCreateDatabase[]
    {
        // tag::databaseConfiguration[]
        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(String.format("%s/%s", ctxt.getFilesDir(), username));
        // end::databaseConfiguration[]
        if(username==null) {
            currentUser = setCurrentUser(null);
        } else {
            currentUser = setCurrentUser(username);
        }
        try {
            // tag::createDatabase[]
            database = new Database(dbName, config);
            // end::createDatabase[]
            registerForDatabaseChanges();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return null;
        }
        return database;
    }

    // tag::registerForDatabaseChanges[]
    private void registerForDatabaseChanges()
    // end::registerForDatabaseChanges[]
    {
        // tag::addDatabaseChangelistener[]
        // Add database change listener
        listenerToken = database.addChangeListener(new DatabaseChangeListener() {
            @Override
            public void changed(final DatabaseChange change) {
                if (change != null) {
                    for(String docId : change.getDocumentIDs()) {
                        Document doc = database.getDocument(docId);
                        if (doc != null) {
                            Log.i("DatabaseChangeEvent", "Document was added/updated");
                        }
                        else {

                            Log.i("DatabaseChangeEvent","Document was deleted");
                        }
                    }
                }
            }
        });
        // end::addDatabaseChangelistener[]
    }

    // tag::closeDatabaseForUser[]
    public void closeDatabaseForUser()
    // end::closeDatabaseForUser[]
    {
        try {
            if (database != null) {
                deregisterForDatabaseChanges();
                // tag::closeDatabase[]
                database.close();
                // end::closeDatabase[]
                database = null;
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    // tag::deregisterForDatabaseChanges[]
    private void deregisterForDatabaseChanges()
    // end::deregisterForDatabaseChanges[]
    {
        if (listenerToken != null) {
            // tag::removedbchangelistener[]
            database.removeChangeListener(listenerToken);
            // end::removedbchangelistener[]
        }
    }

    public void seedDatabase() throws JSONException {

        initCouchbaseLite(ctxt);

        setCurrentUser("ian");

        try {
            deleteDB();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        Database db = openOrCreateDatabaseForUser(getCurrentUser());

//        Hotel hotel = new Hotel();

        HashMap<String, Hotel> hotels = new HashMap();

        String[] id = {"1000", "1001", "1002"};
        String[] name = {"Ted", "Fred", "Ned"};
        String[] city = {"Paris", "London", "Sydney"};
        String[] country = {"France", "England", "Australia"};

        for (int i = 0; i < 3; i++) {
            Hotel hotel = new Hotel();
            hotel.id = id[i];

            hotel.name = "Hotel  " + name[i];
            hotel.city = city[i];
            hotel.country = country[i];
            hotel.description = "Undefined description for " + hotel.name;
            hotels.put(hotel.id, hotel);
            Log.e("Info", "seedDatabase: " + hotel.toString());

            MutableDocument doc = new MutableDocument(hotel.id);
            doc.setString("id", hotel.id);
            doc.setString("type", "hotel");
            doc.setString("name", hotel.name);
            doc.setString("city", hotel.city);
            doc.setString("country", hotel.country);
            doc.setString("description", hotel.description);

            try {
                db.save(doc);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
            Log.e("Info", "seedDatabase: " + doc.toString());
            Log.e("Info", "Doc items =  " + doc.count());

        }
//        try {
//            testQuerySyntaxCount(ctxt);
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//        }
//
//        try {
////            testQuerySyntaxAll(ctxt);
//            testQuerySyntaxJson(ctxt);
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//        }

    }

    public void testQuerySyntaxCount() throws CouchbaseLiteException  {

        Database db = openOrCreateDatabaseForUser(getCurrentUser());

        // tag::query-syntax-count-only[]
        Query listQuery = QueryBuilder.select(
                SelectResult.expression(Function.count(Expression.string("*"))).as("mycount")) // <.>
                .from(DataSource.database(db));

        // end::query-syntax-count-only[]


        // tag::query-access-count-only[]
        try {
            for (Result result : listQuery.execute()) {

                // Retrieve count using key 'mycount'
                Integer altDocId = result.getInt("mycount");

                // Alternatively, use the index
                Integer orDocId = result.getInt(0);
            }
            // Or even miss out the for-loop altogether
            Integer resultCount = listQuery.execute().next().getInt("mycount");
            Log.e("Info", "testQuerySyntaxCount: " + resultCount.toString() );
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        // end::query-access-count-only[]
    }

    public void testQuerySyntaxAll() throws CouchbaseLiteException {

        // tag::query-syntax-all[]
//        try {
//            this_Db = new Database("hotels");
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//        }


        Database db = openOrCreateDatabaseForUser(getCurrentUser());

        Query listQuery = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(db));
        // end::query-syntax-all[]

        // tag::query-access-all[]
        HashMap<String, Hotel> hotels = new HashMap();
        try {
            for (Result result : listQuery.execute().allResults()) {
                // get the k-v pairs from the 'hotel' key's value into a dictionary
                Dictionary thisDocsProps = result.getDictionary(0); // <.>
                String thisDocsId = thisDocsProps.getString("id");
                String thisDocsName = thisDocsProps.getString("name");
                String thisDocsType = thisDocsProps.getString("type");
                String thisDocsCity = thisDocsProps.getString("city");

                // Alternatively, access results value dictionary directly
                final Hotel hotel = new Hotel();
                hotel.id = result.getDictionary(0).getString("id"); // <.>
                hotel.type = result.getDictionary(0).getString("type");
                hotel.name = result.getDictionary(0).getString("name");
                hotel.city = result.getDictionary(0).getString("city");
                hotel.country= result.getDictionary(0).getString("country");
                hotel.description = result.getDictionary(0).getString("description");
                hotels.put(hotel.id, hotel);

            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        // end::query-access-all[]
    }

    public void testQuerySyntaxJson() throws CouchbaseLiteException, JSONException {
        Database db = openOrCreateDatabaseForUser(getCurrentUser());
        // tag::query-syntax-all[]
        // Example assumes Hotel class object defined elsewhere
//        Database db = null;
//        try {
//                db = new Database(dbName);
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//        }

        // Build the query
        Query listQuery = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(db));

        // end::query-syntax-all[]

        // tag::query-access-json[]

        // Run query
//        try {
            ArrayList<Hotel> hotels = new ArrayList<Hotel>();
            for (Result result : listQuery.execute()) {

                // Get result as JSON string
                String thisJsonString = result.getDictionary(0).toJSON(); // <.>
                Log.d("Convert", "JSON string: " + thisJsonString );

                // Get JSON object from JSON string
                JSONObject thisJSONobject = new JSONObject(thisJsonString);
                Log.d("Convert", "JSON object: " + thisJSONobject.get("name"));

                // Get native object from JSON object
                JSONObject jsonObject = new JSONObject(thisJsonString.trim());
                Iterator<String> keys = jsonObject.keys();
                HashMap<String,Object> jsonDict  = new HashMap<String,Object>();

                while(keys.hasNext()) { /* <.> */
                    String key = keys.next();
                    jsonDict.put(key,jsonObject.get(key));
                    // do something with jsonObject here
                    Log.d("Convert", "Native object: " + jsonDict.get(key));
                }

                // Get custom object from Native 'dictionary' object
                Hotel thisHotel = new Hotel();
                thisHotel.id = jsonDict.get("id").toString(); // <.>
                thisHotel.type = jsonDict.get("type").toString();
                thisHotel.name = jsonDict.get("name").toString();
                thisHotel.city = jsonDict.get("city").toString();
                thisHotel.country = jsonDict.get("country").toString();
                thisHotel.description = jsonDict.get("description").toString();
                hotels.add(thisHotel);
                Log.d("Convert", "MutableArray of Hotels: " + hotels.get(hotels.size()-1).name);

            }

        // end::query-access-json[]
    }


    private void deleteDB() throws CouchbaseLiteException {

        Database db = openOrCreateDatabaseForUser(getCurrentUser());
        db.delete();

    }
}
