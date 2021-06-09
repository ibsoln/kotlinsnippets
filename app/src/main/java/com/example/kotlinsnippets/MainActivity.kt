package com.example.kotlinsnippets

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.couchbase.lite.Database
import com.example.kotlinsnippets.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private var appBarConfiguration: AppBarConfiguration? = null
    private var binding: ActivityMainBinding? = null
    lateinit var listVw : ListView
    var hotels: ArrayList<Hotel> = ArrayList()
    var adapter: listVwAdapter? = null
    val dbm = DatabaseManager()
//    var dbmtxt = dbm.initCouchbaseLite(applicationContext)
    val DB = dbm.openOrCreateDatabaseForUser("ian")


    //    public Context ctxt = getApplicationContext();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_first)

        listVw = findViewById(R.id.frag1_listview1)
//        dbm.seedDatabase()
        hotels.addAll(dbm.testQuerySyntaxJson())
        Log.d("INFO", "onCreate: ${hotels.size} hotels")

        val txtVw : TextView = findViewById(R.id.frag1_title)
        txtVw.text = "My Kotlin App"



        listVw.adapter = listVwAdapter(hotels, applicationContext)

        var uilist:ArrayList<String> = ArrayList(hotels.size)

        var i = hotels.iterator()
        while(i.hasNext()) {uilist.add(i.next().name)}

        val adapter = ArrayAdapter(this, R.layout.fragment1_listview_tvitem_name, uilist)
        listVw.adapter = adapter
        Log.d("DEBUG", "onCreate: ")

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding!!.root)
//        setSupportActionBar(binding!!.toolbar)
//        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration!!)
//        binding!!.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//


    } // end on create



} // end mainactivity class

class listVwAdapter(arghotels:ArrayList<Hotel>, argctxt:Context) : BaseAdapter() {

    val locHotels : ArrayList<Hotel> = arghotels
    var locCtxt : Context = argctxt

    override fun getCount(): Int {

        return locHotels.size

    }

    override fun getItem(position: Int): Any {

        return position

    }

    override fun getItemId(position: Int): Long {

        return position.toLong()

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
//        convertView = LayoutInflater.from(locCtxt).inflate(R.layout.row, parent, false)
//        serialNum = convertView.findViewById(R.id.serialNumber)
//        name = convertView.findViewById(R.id.studentName)
//        contactNum = convertView.findViewById(R.id.mobileNum)
//        serialNum.text = " " + arrayList[position].num
//        name.text = arrayList[position].name
//        contactNum.text = arrayList[position].mobileNumber
        return convertView!!

    }

}
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        val id = item.itemId
//        return if (id == R.id.action_settings) {
//            true
//        } else super.onOptionsItemSelected(item)
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
//        return (NavigationUI.navigateUp(navController, appBarConfiguration!!)
//                || super.onSupportNavigateUp())
//    }
