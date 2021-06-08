package com.example.kotlinsnippets

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.kotlinsnippets.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private var appBarConfiguration: AppBarConfiguration? = null
    private var binding: ActivityMainBinding? = null

    //    public Context ctxt = getApplicationContext();
    var dbm = DatabaseManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialized CBL
        dbm.initCouchbaseLite(applicationContext)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        setSupportActionBar(binding!!.toolbar)
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration!!)
        binding!!.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        return (NavigationUI.navigateUp(navController, appBarConfiguration!!)
                || super.onSupportNavigateUp())
    }
}