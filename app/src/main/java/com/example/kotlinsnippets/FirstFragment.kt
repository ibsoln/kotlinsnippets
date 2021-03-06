package com.example.kotlinsnippets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.couchbase.lite.CouchbaseLiteException
import com.example.kotlinsnippets.databinding.FragmentFirstBinding
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



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.buttonFirst.setOnClickListener {
            NavHostFragment.findNavController(this@FirstFragment)
                    .navigate(R.id.action_FirstFragment_to_SecondFragment)


        }

    }


       public class myAdapter : BaseAdapter() {

        override fun getCount(): Int {
            TODO("Not yet implemented")
        }

        override fun getItem(position: Int): Any {
            TODO("Not yet implemented")
        }

        override fun getItemId(position: Int): Long {
            TODO("Not yet implemented")
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            TODO("Not yet implemented")
        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun runQueries() {
        val dbm: DatabaseManager? = DatabaseManager.Companion.sharedInstance
        try {
            dbm?.seedDatabase()
            dbm?.testQuerySyntaxCount()
            dbm?.testQuerySyntaxJson()
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun runJSONapi() {
        val dbm: DatabaseManager? = DatabaseManager.Companion.sharedInstance
        try {
            dbm?.seedDatabase()
            dbm?.testQuerySyntaxCount()
            dbm?.useJSONAPImethods()
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


}