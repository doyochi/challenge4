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

        checkLoginState()
        // Melakukan submit Login
        doLogin()
        moveToRegister()
    }

    private fun checkLoginState() {
        val loginState = sharedPref.getBoolean("LOGIN_STATE", false)
        if (loginState) { // true = pindah ke Home
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun doLogin() {
        binding.btnLogin.setOnClickListener {
            val etUsername = binding.editUsername.editableText.toString()
            val etPassword = binding.editPassword.editableText.toString()
            if (loginValidation(etUsername, etPassword)) {
                moveToHome(etUsername, etPassword)
            }
        }
    }

    private fun loginValidation(username: String, password: String): Boolean {
        var result = true
        if (username.isEmpty()) { // jika kosong
            binding.editUsername.error = "Username tidak boleh kosong!"
            result = false
        } else {
            binding.editUsername
        }

        if (password.isEmpty()) { // jika kosong
            binding.editPassword.error = "Password tidak boleh kosong!"
            result = false
        }  else {
            binding.editPassword
        }

        return result
    }

    private fun moveToHome(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // query mencari user & pass
            val checkLogin = userDb?.userDao()?.checkLogin(username, password)
            // query mencari ID dari user login
            val getId = userDb?.userDao()?.getUserId(username)
            // Jika user & pass cocok (ada pada DB)
            if (!checkLogin.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    // Simpan ID pada variable
                    val userID = getId?.id!!
                    // Buat editor sharedpref
                    val editor = sharedPref.edit()
                    // Simpan ke sharedpref
                    editor.apply {
                        putInt("USERID", userID)
                        putString("USERNAME", username)
                        putBoolean("LOGIN_STATE", true)
                        apply()
                    }

//                    Toast.makeText(requireContext(), "$userID berhasil login", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
            } else { // Jika user & pass tdk ditemukan
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "Username atau Password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun moveToRegister() {
        binding.btnToregist.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }


}