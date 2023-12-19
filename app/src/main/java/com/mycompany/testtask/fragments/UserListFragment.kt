package com.mycompany.testtask.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mycompany.testtask.R
import com.mycompany.testtask.database.AppDatabase
import com.mycompany.testtask.database.data.User
import com.mycompany.testtask.databinding.CustomRowUserListBinding
import com.mycompany.testtask.databinding.ViewFragmentUserBinding
import com.mycompany.testtask.image.CircleTransformation
import com.mycompany.testtask.threads.ApiService
import com.squareup.picasso.Picasso
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserListFragment : Fragment() {

    private lateinit var binding: CustomRowUserListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CustomRowUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUserList()
    }

    private var onItemClickListener: View.OnClickListener? = null
    fun setOnClickListener(listener: View.OnClickListener) {
        onItemClickListener = listener
    }

    private fun printUserList() {
        val userList = ArrayList<ViewFragmentUserBinding>()
        val userDAO = AppDatabase.getDatabase(requireActivity().baseContext).userDao()

        GlobalScope.async {
            val listOfUser = userDAO.getAllUsers()

            for (user: User in listOfUser) {
                val userBinding: ViewFragmentUserBinding =
                    ViewFragmentUserBinding.inflate(layoutInflater)
                userBinding.userNameView.text = user.name
                userBinding.userEmailView.text = user.email
                userBinding.userAddInfoView.text = user.company.catchPhrase
                userBinding.userAddInfoView.apply {
                    isSingleLine = true
                    ellipsize = TextUtils.TruncateAt.END
                }
                userBinding.toUserInfoButton.tag = user.id + 1
                userBinding.toUserInfoButton.setOnClickListener(onItemClickListener)

                val avatarUrl: String = IMAGE_URL + user.id + IMG_TYPE
                requireActivity().runOnUiThread {
                    Picasso.get().load(avatarUrl).transform(CircleTransformation())
                        .into(userBinding.userAvatarView)
                }
                userList.add(userBinding)
            }

            if (userList.isNotEmpty()) {
                requireActivity().runOnUiThread {
                    for (userView: ViewFragmentUserBinding in userList)
                        binding.usersShownLinearContainer.addView(userView.root)
                }
            } else {
                binding.userListEmptyTitle.visibility = View.VISIBLE
                binding.userListEmptyTitle.text = getString(R.string.list_is_empty)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initializeUserList() {
        val userDAO = AppDatabase.getDatabase(requireActivity().baseContext).userDao()

        if (!isInternetAvailable(requireActivity().baseContext)) {
            if (userDAO.getUsersCount() > 0) {
                Toast.makeText(
                    requireActivity().baseContext,
                    R.string.prob_internet_connection_cached,
                    Toast.LENGTH_SHORT
                ).show()
                printUserList()
            } else Toast.makeText(
                requireActivity().baseContext,
                R.string.prob_internet_connection,
                Toast.LENGTH_SHORT
            ).show()
        } else if (isInternetAvailable(requireActivity().baseContext)) {
            val retrofit = Retrofit.Builder()
                .baseUrl(LINK_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val apiService = retrofit.create(ApiService::class.java)
            val listOfUser: ArrayList<User> = ArrayList()

            GlobalScope.async {
                try {
                    val users = apiService.getUsers()
                    listOfUser.addAll(users)
                    for (user: User in users)
                        userDAO.insertUser(user)
                    printUserList()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    companion object {
        const val LINK_URL: String = "http://jsonplaceholder.typicode.com/"
        const val IMAGE_URL: String = "https://quizee.app/storage/avatars/"
        const val IMG_TYPE = ".jpeg"
    }
}