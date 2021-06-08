package com.example.jsonapisnippets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.couchbase.lite.CouchbaseLiteException
import com.example.jsonapisnippets.databinding.FragmentFirstBinding
import org.json.JSONException

class FirstFragment : Fragment() {
    private var binding: FragmentFirstBinding? = null

    //    private Context app = MainActivity.getApplicationContext();
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    fun runQueries() {
        val dbm: DatabaseManager = DatabaseManager.Companion.getSharedInstance()
        try {
            dbm.seedDatabase()
            dbm.testQuerySyntaxCount()
            dbm.testQuerySyntaxJson()
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.buttonFirst.setOnClickListener {
            NavHostFragment.findNavController(this@FirstFragment)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment)
            runQueries()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}