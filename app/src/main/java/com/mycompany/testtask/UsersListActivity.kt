package com.mycompany.testtask

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mycompany.testtask.data.User
import com.mycompany.testtask.databinding.ActivityUsersListBinding
import com.mycompany.testtask.databinding.FragmentUserBinding
import com.mycompany.testtask.image.CircleTransformation
import com.mycompany.testtask.sharedprp.DasCashed
import com.mycompany.testtask.threads.ReadJSONThread
import com.mycompany.testtask.sharedprp.UserList
import com.squareup.picasso.Picasso

class UsersListActivity : AppCompatActivity() {
    private val LINK_URL : String = "http://jsonplaceholder.typicode.com/users"
    private val IMAGE_URL : String = "https://quizee.app/storage/avatars/"

    private val KEY_URER_ID = "user_id"
    private val IMG_TYPE = ".jpeg"

    private lateinit var binding : ActivityUsersListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userList = ArrayList<FragmentUserBinding>()

        val listOfUser = UserList(this)
        val dasCashed = DasCashed(this)

        val buttonToThreeScreen = View.OnClickListener { v ->
            val nextIntent = Intent(
                this@UsersListActivity,
                CurrentUserInfoActivity::class.java )
            val bundle = Bundle()
            val userID = v.id - 1
            bundle.putInt(KEY_URER_ID, userID)
            nextIntent.putExtras(bundle)
            startActivity(nextIntent)
        }

        val userPrintThread : Thread = Thread {
            val users : List<User> = listOfUser.getUserListAsList()
            for (user: User in users){
                val userBinding: FragmentUserBinding =
                    FragmentUserBinding.inflate(layoutInflater)
                userBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    resources.getDimensionPixelSize(R.dimen.height_100dp))
                userBinding.userName.text = user.name
                userBinding.userEmail.text = user.email
                userBinding.userInfo.text = user.company.catchPhrase
                userBinding.toThreeScreen.id = user.id+1
                userBinding.toThreeScreen.setOnClickListener(buttonToThreeScreen)

                val avatarUrl : String = IMAGE_URL + user.id + IMG_TYPE;
                runOnUiThread { Picasso.get().load(avatarUrl).transform(CircleTransformation()).into(userBinding.userAvatar) }
                userList.add(userBinding)
            }
            runOnUiThread(Runnable {
                if (!userList.isEmpty()){
                    for (userView: FragmentUserBinding in userList)
                        binding.UserContainer.addView(userView.root)
                } else {
                    binding.UserNotInListTitle.visibility = View.VISIBLE
                    binding.UserNotInListTitle.text = getString(R.string.list_is_empty)
                }
            })
        }

        if (!isInternetAvailable(this)){
            if (dasCashed.isCashed()){
                Toast.makeText(this, R.string.prob_internet_connection_cached, Toast.LENGTH_SHORT).show()
                userPrintThread.start()
                userPrintThread.join()
            } else Toast.makeText(this, R.string.prob_internet_connection, Toast.LENGTH_SHORT).show()
        } else if (isInternetAvailable(this)) {
            val readUser:ReadJSONThread = ReadJSONThread(LINK_URL, this)
            listOfUser.clearUserList()
            readUser.start()
            readUser.join()
            userPrintThread.start()
            dasCashed.saveCashedState(true)
        } else Toast.makeText(this, R.string.prob_internet_connection, Toast.LENGTH_SHORT).show()
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}


