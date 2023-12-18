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

    private lateinit var binding: FragmentUsersListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        val userPrintThread = Thread {
            val users : List<User> = listOfUser.getUserListAsList()
            for (user: User in users){
                val userBinding: ViewFragmentUserBinding =
                    ViewFragmentUserBinding.inflate(layoutInflater)
                userBinding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.height_90dp))
                userBinding.userNameView.text = user.name
                userBinding.userEmailView.text = user.email
                userBinding.userAddInfoView.text = user.company.catchPhrase
                userBinding.userAddInfoView.apply {
                    isSingleLine = true
                    ellipsize = TextUtils.TruncateAt.END
                }
                userBinding.toUserInfoButton.id = user.id+1
                userBinding.toUserInfoButton.setOnClickListener(onItemClickListener)

                val avatarUrl : String = IMAGE_URL + user.id + IMG_TYPE
                requireActivity().runOnUiThread { Picasso.get().load(avatarUrl).transform(CircleTransformation()).into(userBinding.userAvatarView) }
                userList.add(userBinding)
            }
            requireActivity().runOnUiThread {
                if (userList.isNotEmpty()) {
                    for (userView: ViewFragmentUserBinding in userList)
                        binding.usersShownLinearContainer.addView(userView.root)
                } else {
                    binding.userListEmptyTitle.visibility = View.VISIBLE
                    binding.userListEmptyTitle.text = getString(R.string.list_is_empty)
                }
            }
        }

        if (!isInternetAvailable(requireActivity().baseContext)){
            if (dasCashed.isCashed()){
                Toast.makeText(requireActivity().baseContext, R.string.prob_internet_connection_cached, Toast.LENGTH_SHORT).show()
                userPrintThread.start()
                userPrintThread.join()
            } else Toast.makeText(requireActivity().baseContext, R.string.prob_internet_connection, Toast.LENGTH_SHORT).show()
        } else if (isInternetAvailable(requireActivity().baseContext)) {
            val readUser = ReadJSONThread(LINK_URL, requireActivity().baseContext)
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

    companion object{
        const val LINK_URL: String = "http://jsonplaceholder.typicode.com/users"
        const val IMAGE_URL: String = "https://quizee.app/storage/avatars/"
        const val IMG_TYPE = ".jpeg"
    }
}