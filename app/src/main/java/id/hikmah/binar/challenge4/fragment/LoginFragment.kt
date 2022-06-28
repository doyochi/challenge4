package id.hikmah.binar.challenge4.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import id.hikmah.binar.challenge4.MainActivity
import id.hikmah.binar.challenge4.R
import id.hikmah.binar.challenge4.database.UserDatabase
import id.hikmah.binar.challenge4.databinding.FragmentLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var userDb: UserDatabase?= null
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userDb = UserDatabase.getInstance(requireContext())
        sharedPref = requireContext().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        isLogin()
        doLogin()
        moveToRegister()
    }

    private fun isLogin() {
        val loginState = sharedPref.getBoolean("LOGIN_STATE", false)
        if (loginState) {
            val intent = Intent(requireContext(),MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun moveToRegister() {
        binding.btnToregist.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun doLogin() {
        binding.btnLogin.setOnClickListener {
            val etUsername = binding.editUsername.editableText.toString()
            val etPassword = binding.editPassword.editableText.toString()
            if (validateLogin(etUsername, etPassword)) {
                moveToHome(etUsername, etPassword)
            }
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        if (username.isEmpty()) {
            binding.editUsername.error = "Mohon isi username"
            return false
        }
        if (password.isEmpty()) {
            binding.editPassword.error = "Mohon isi password"
            return false
        }
        return true
    }

    private fun moveToHome(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val checkLogin = userDb?.userDao()?.checkLogin(username, password)
            val getId = userDb?.userDao()?.getUserId(username)
            if (!checkLogin.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    val userID = getId?.id!!
                    val editor = sharedPref.edit()
                    editor.apply {
                        putInt("USERID", userID)
                        putString("USERNAME", username)
                        putBoolean("LOGIN_STATE", true)
                        apply()
                    }
                    Toast.makeText(requireContext(), "Selamat datang $userID", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
            }

            else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Username atau Password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}