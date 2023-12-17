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
import com.mycompany.testtask.data.User
import com.mycompany.testtask.databinding.FragmentUsersListBinding
import com.mycompany.testtask.databinding.ViewFragmentUserBinding
import com.mycompany.testtask.image.CircleTransformation
import com.mycompany.testtask.sharedprp.DasCashed
import com.mycompany.testtask.sharedprp.UserList
import com.mycompany.testtask.threads.ReadJSONThread
import com.squareup.picasso.Picasso

class UserListFragment : Fragment() {

    private val LINK_URL: String = "http://jsonplaceholder.typicode.com/users"
    private val IMAGE_URL: String = "https://quizee.app/storage/avatars/"

    private val IMG_TYPE = ".jpeg"

    private lateinit var binding: FragmentUsersListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersListBinding.inflate(inflater, container, false)
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

    private fun initializeUserList() {
        val userList = ArrayList<ViewFragmentUserBinding>()

        val listOfUser = UserList(requireActivity().baseContext)
        val dasCashed = DasCashed(requireActivity().baseContext)

        val userPrintThread : Thread = Thread {
            val users : List<User> = listOfUser.getUserListAsList()
            for (user: User in users){
                val userBinding: ViewFragmentUserBinding =
                    ViewFragmentUserBinding.inflate(layoutInflater)
                userBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    resources.getDimensionPixelSize(R.dimen.height_90dp))
                userBinding.userName.text = user.name
                userBinding.userEmail.text = user.email
                userBinding.userInfo.text = user.company.catchPhrase
                userBinding.userInfo.apply {
                    setSingleLine(true)
                    ellipsize = TextUtils.TruncateAt.END
                }
                userBinding.toThreeScreen.id = user.id+1
                userBinding.toThreeScreen.setOnClickListener(onItemClickListener)

                val avatarUrl : String = IMAGE_URL + user.id + IMG_TYPE;
                requireActivity().runOnUiThread { Picasso.get().load(avatarUrl).transform(CircleTransformation()).into(userBinding.userAvatar) }
                userList.add(userBinding)
            }
            requireActivity().runOnUiThread(Runnable {
                if (!userList.isEmpty()){
                    for (userView: ViewFragmentUserBinding in userList)
                        binding.UserContainer.addView(userView.root)
                } else {
                    binding.UserNotInListTitle.visibility = View.VISIBLE
                    binding.UserNotInListTitle.text = getString(R.string.list_is_empty)
                }
            })
        }

        if (!isInternetAvailable(requireActivity().baseContext)){
            if (dasCashed.isCashed()){
                Toast.makeText(requireActivity().baseContext, R.string.prob_internet_connection_cached, Toast.LENGTH_SHORT).show()
                userPrintThread.start()
                userPrintThread.join()
            } else Toast.makeText(requireActivity().baseContext, R.string.prob_internet_connection, Toast.LENGTH_SHORT).show()
        } else if (isInternetAvailable(requireActivity().baseContext)) {
            val readUser: ReadJSONThread = ReadJSONThread(LINK_URL, requireActivity().baseContext)
            listOfUser.clearUserList()
            readUser.start()
            readUser.join()
            userPrintThread.start()
            dasCashed.saveCashedState(true)
        } else Toast.makeText(requireActivity().baseContext, R.string.prob_internet_connection, Toast.LENGTH_SHORT).show()
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

}