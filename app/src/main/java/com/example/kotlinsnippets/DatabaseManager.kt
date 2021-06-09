package com.example.kotlinsnippets

import android.content.Context
import android.util.Log
import com.couchbase.lite.*
import com.couchbase.lite.Function
import com.couchbase.lite.Dictionary as CBL
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.Dictionary
import kotlin.Any
import kotlin.String
import kotlin.Throws
import kotlin.arrayOf
import kotlin.collections.HashMap
import kotlin.toString


//package com.couchbase.userprofile.util;
//import MyApp;
class DatabaseManager {
    private var listenerToken: ListenerToken? = null

    // tag::initCouchbaseLite[]
    fun initCouchbaseLite(parCtxt: Context?) {
        if (!couchbaseInitialized) {
            ctxt = parCtxt
            CouchbaseLite.init(ctxt!!)
            couchbaseInitialized = true
        }
    }

    // end::initCouchbaseLite[]
    // tag::userProfileDocId[]

    val currentUserDocId: String
        get() = "user::" + Companion.currentUser

    // end::userProfileDocId[]
    // tag::userProfileDocId[]
    val currentUser: String
        get() = Companion.currentUser

    // end::userProfileDocId[]
    // tag::openOrCreateDatabase[]
    fun openOrCreateDatabaseForUser(username: String?): Database? // end::openOrCreateDatabase[]
    {
        if(!couchbaseInitialized) {initCouchbaseLite(ctxt)}


        // tag::databaseConfiguration[]
        val config = DatabaseConfiguration()
        config.directory = String.format("%s/%s", ctxt!!.filesDir, username)
        // end::databaseConfiguration[]
        if (username == null) {
            Companion.currentUser = setCurrentUser(null)
        } else {
            Companion.currentUser = setCurrentUser(username)
        }
        try {
            // tag::createDatabase[]
            database = Database(dbName, config)
            // end::createDatabase[]
            registerForDatabaseChanges()
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
            return null
        }
        return database
    }

    // tag::registerForDatabaseChanges[]
    private fun registerForDatabaseChanges() // end::registerForDatabaseChanges[]
    {
        // tag::addDatabaseChangelistener[]
        // Add database change listener
        listenerToken = database!!.addChangeListener { change ->
            if (change != null) {
                for (docId in change.documentIDs) {
                    val doc = database!!.getDocument(docId!!)
                    if (doc != null) {
                        Log.i("DatabaseChangeEvent", "Document was added/updated")
                    } else {
                        Log.i("DatabaseChangeEvent", "Document was deleted")
                    }
                }
            }
        }
        // end::addDatabaseChangelistener[]
    }

    // tag::closeDatabaseForUser[]
    fun closeDatabaseForUser() // end::closeDatabaseForUser[]
    {
        try {
            if (database != null) {
                deregisterForDatabaseChanges()
                // tag::closeDatabase[]
                database!!.close()
                // end::closeDatabase[]
                database = null
            }
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }

        Log.d("close", "what happened" )
    }

    // tag::deregisterForDatabaseChanges[]
    private fun deregisterForDatabaseChanges() // end::deregisterForDatabaseChanges[]
    {
        if (listenerToken != null) {
            // tag::removedbchangelistener[]
            database!!.removeChangeListener(listenerToken!!)
            // end::removedbchangelistener[]
        }
    }




    @Throws(JSONException::class)
    fun seedDatabase() {
        initCouchbaseLite(ctxt)
        setCurrentUser("ian")
        try {
            deleteDB()
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
        val db = openOrCreateDatabaseForUser(currentUser)

        val hotel = com.example.kotlinsnippets.Hotel()

        val hotels: HashMap<String, Hotel> = HashMap<String, Hotel>()
        val id = arrayOf("1000", "1001", "1002")
        val name = arrayOf("Ted", "Fred", "Ned")
        val city = arrayOf("Paris", "London", "Sydney")
        val country = arrayOf("France", "England", "Australia")
        for (i in 0..2) {
            val hotel = Hotel()
            hotel.id = id[i]
            hotel.name = "Hotel  " + name[i]
            hotel.city = city[i]
            hotel.country = country[i]
            hotel.description = "Undefined description for " + hotel.name
            hotels[hotel.id] = hotel
            Log.e("Info", "seedDatabase: $hotel")
            val doc = MutableDocument(hotel.id)
            doc.setString("id", hotel.id)
            doc.setString("type", "hotel")
            doc.setString("name", hotel.name)
            doc.setString("city", hotel.city)
            doc.setString("country", hotel.country)
            doc.setString("description", hotel.description)
            try {
                db!!.save(doc)
            } catch (e: CouchbaseLiteException) {
                e.printStackTrace()
            }
            Log.e("Info", "seedDatabase: $doc")
            Log.e("Info", "Doc items =  " + doc.count())
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

    @Throws(CouchbaseLiteException::class)
    fun testQuerySyntaxCount() {
        val db = openOrCreateDatabaseForUser(currentUser)

        // tag::query-syntax-count-only[]
        val listQuery: Query = QueryBuilder.select(
                SelectResult.expression(Function.count(Expression.string("*"))).`as`("mycount")) // <.>
                .from(DataSource.database(db!!))

        // end::query-syntax-count-only[]


        // tag::query-access-count-only[]
        try {
            for (result in listQuery.execute()) {

                // Retrieve count using key 'mycount'
                val altDocId = result.getInt("mycount")

                // Alternatively, use the index
                val orDocId = result.getInt(0)
            }
            // Or even miss out the for-loop altogether
            val resultCount = listQuery.execute().next()!!.getInt("mycount")
            Log.e("Info", "testQuerySyntaxCount: $resultCount")
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
        // end::query-access-count-only[]

        closeDatabaseForUser()

    }

    @Throws(CouchbaseLiteException::class)
    fun testQuerySyntaxAll() : HashMap<String,Hotel> {

        // tag::query-syntax-all[]
//        try {
//            db = new Database("hotels");
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//        }
        val db = openOrCreateDatabaseForUser(currentUser)
        val listQuery: Query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(db!!))
        // end::query-syntax-all[]

        // tag::query-access-all[]
        val hotels: HashMap<String, Hotel> = HashMap<String, Hotel>()
        try {
            for (result in listQuery.execute().allResults()) {
                // get the k-v pairs from the 'hotel' key's value into a dictionary
                val thisDocsProps = result.getDictionary(0) // <.>
                val thisDocsId = thisDocsProps!!.getString("id")
                val thisDocsName = thisDocsProps.getString("name")
                val thisDocsType = thisDocsProps.getString("type")
                val thisDocsCity = thisDocsProps.getString("city")

                // Alternatively, access results value dictionary directly
                val hotel = Hotel()
                hotel.id = result.getDictionary(0)!!.getString("id").toString() // <.>
                hotel.type = result.getDictionary(0)!!.getString("type").toString()
                hotel.name = result.getDictionary(0)!!.getString("name").toString()
                hotel.city = result.getDictionary(0)!!.getString("city").toString()
                hotel.country = result.getDictionary(0)!!.getString("country").toString()
                hotel.description = result.getDictionary(0)!!.getString("description").toString()
                hotels[hotel.id] = hotel
            }
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }

        return hotels

//        closeDatabaseForUser()

        // end::query-access-all[]
    }

    @Throws(CouchbaseLiteException::class, JSONException::class)
    fun testQuerySyntaxJson(): ArrayList<Hotel> {
        val db = openOrCreateDatabaseForUser(currentUser)
        // tag::query-syntax-all[]
        // Example assumes Hotel class object defined elsewhere
//        Database db = null;
//        try {
//                db = new Database(dbName);
//        } catch (CouchbaseLiteException e) {
//            e.printStackTrace();
//        }

        // Build the query
        val listQuery: Query = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(db!!))

        // end::query-syntax-all[]

        // tag::query-access-json[]

        // Run query
//        try {
        val hotels = ArrayList<Hotel>()
        for (result in listQuery.execute()) {

            // Get result as JSON string
            val thisJsonString = result.getDictionary(0)!!.toJSON() // <.>
            Log.d("Convert", "JSON string: $thisJsonString")

            // Get JSON object from JSON string
            val thisJSONobject = JSONObject(thisJsonString)
            Log.d("Convert", "JSON object: " + thisJSONobject["name"])

            // Get native object from JSON object
            val jsonObject = JSONObject(thisJsonString.trim { it <= ' ' })
            val keys = jsonObject.keys()
            val jsonDict = HashMap<String, Any>()
            while (keys.hasNext()) { /* <.> */
                val key = keys.next()
                jsonDict[key] = jsonObject[key]
                // do something with jsonObject here
                Log.d("Convert", "Native object: " + jsonDict[key])
            }

            // Get custom object from Native 'dictionary' object
            val thisHotel = Hotel()
            thisHotel.id = jsonDict["id"].toString() // <.>
            thisHotel.type = jsonDict["type"].toString()
            thisHotel.name = jsonDict["name"].toString()
            thisHotel.city = jsonDict["city"].toString()
            thisHotel.country = jsonDict["country"].toString()
            thisHotel.description = jsonDict["description"].toString()
            hotels.add(thisHotel)
            Log.d("Convert", "MutableArray of Hotels: " + hotels[hotels.size - 1].name)
        }

        // end::query-access-json[]
        return hotels
//        closeDatabaseForUser()
    }


    @Throws(CouchbaseLiteException::class)
    fun getHotelIdByName(parHotel: String) : String? {
        // tag::query-syntax-id[]
//        try {
//            db = Database("hotels")
//        } catch (e: CouchbaseLiteException) {
//            e.printStackTrace()
//        }

        var returnId = ""
        val db = openOrCreateDatabaseForUser(currentUser)
        val listQuery: Query = QueryBuilder.select(SelectResult.expression(Meta.id).`as`("metaID")).from(DataSource.database(db!!))

//            .where(Expression.property("name").equalTo(Expression.string(parHotel)))

        // end::query-syntax-id[]


        // tag::query-access-id[]

        try {
            var rs = listQuery.execute().allResults()
            for (result in rs) {
                var me = result.getString("metaID")
                Log.d("JSONAPI: ", "getHotelByName: ${result.getString("name")} : ${result.getString("metaID")} : ${result.getString("Meta.id")}")
                returnId = result.getString("metaID").toString()
//                return thisId

            }
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }

        return returnId
//        db?.close()
        // end::query-access-id[]
    }


    @Throws(CouchbaseLiteException::class)
    private fun deleteDB() {
        val db = openOrCreateDatabaseForUser(currentUser)
//        if (db) {
//            db!!.delete()
//        }
    }


    @Throws(CouchbaseLiteException::class, JSONException::class)
    fun useJSONAPImethods() {

        var tag = "JSONAPI: "

        seedDatabase()


        var myHotels = testQuerySyntaxAll()

//        openOrCreateDatabaseForUser("ian")

//        var db = Database("hotels")

//        var numrecs = db.count

//        Log.d(tag, "Total hotels returned = ${numrecs.toString()}")

// 2.2
        var hotelId = getHotelIdByName("Hotel Ted").toString()

        // 2.2 Get document as json string
        var db = openOrCreateDatabaseForUser("ian")
        var jsonString = db?.getDocument(hotelId)?.toJSON() // <.>
        Log.d(tag, "JSON String document: ${jsonString}")

        // 2.2 Get document as native dictionaryg
//        var docHotel = db.getDocument(hotelId)
//        var dictHotel = docHotel?.toMap()

        // Get Json object from JSON string
        var jsonObject = JSONObject(jsonString) // <.>

        // Get json object into native dictionary
        var keys = jsonObject.keys()
        val jsonDict = HashMap<String, Any>()
        while (keys.hasNext()) { /* <.> */
            val key = keys.next()
            jsonDict[key] = jsonObject[key]
        }
        Log.d(tag, "Native dictionary from JSON: ${jsonDict.toString()}")

        // Get CBL Dictionary from JSON
        var dictionaryFromJSON = MutableDictionary().setJSON(jsonString.toString())
        Log.d(tag, "CouchbaseLite Dictionary from JSON: ${dictionaryFromJSON}")

        // Get JSON from CBL Dictionary
        var jsonFromDictionary = dictionaryFromJSON.toJSON()
        Log.d(tag, "JSON from CouchbaseLite Dictionary: ${jsonFromDictionary}")

//        closeDatabaseForUser()

    }


    companion object {
        var database: Database? = null
            private set
        private var instance: DatabaseManager? = null
        private var currentUser = setCurrentUser("ian")
        private const val dbName = "hotels"
        private var couchbaseInitialized = false
        private var ctxt: Context? = contextServer.Companion.appContext
        val sharedInstance: DatabaseManager?
            get() {
                if (instance == null) {
                    instance = DatabaseManager()
                }
                return instance
            }

        private fun setCurrentUser(username: String?): String {
            return username ?: "ian"
        }
    }

    init {
        ctxt = contextServer.Companion.appContext
    }
} // End of Class