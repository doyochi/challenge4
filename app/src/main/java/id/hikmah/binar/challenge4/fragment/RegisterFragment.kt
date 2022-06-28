package id.hikmah.binar.challenge4.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import id.hikmah.binar.challenge4.R
import id.hikmah.binar.challenge4.database.User
import id.hikmah.binar.challenge4.database.UserDatabase
import id.hikmah.binar.challenge4.databinding.FragmentRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var userDb: UserDatabase?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userDb = UserDatabase.getInstance(requireContext())

        doRegister()
    }

    private fun doRegister() {
        binding.btnDaftar.setOnClickListener {
            val etUsername = binding.editUsername.editableText.toString()
            val etEmail = binding.editEmail.editableText.toString()
            val etPassword1 = binding.editPassword1.editableText.toString()
            val etPassword2 = binding.editPassword2.editableText.toString()
            if (validateRegister(etUsername, etEmail, etPassword1, etPassword2)) {
                insertToDb(etUsername, etEmail, etPassword1)
            }
        }
    }

    private fun insertToDb(username: String, email: String, password: String) {
        val user = User(null, username, email, password)
        CoroutineScope(Dispatchers.IO).launch {
            val checkUsername = userDb?.userDao()?.checkRegisteredUsername(username)
            if (!checkUsername.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(),
                        "Gagal Daftar", Toast.LENGTH_SHORT).show()
                    binding.editUsername.error = "Username sudah dipakai"
                }
            } else {
                val result = userDb?.userDao()?.insertUser(user)
                if (result != 0L) {
                    CoroutineScope(Dispatchers.Main).launch {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        Toast.makeText(requireContext(),
                            "Berhasil Daftar", Toast.LENGTH_SHORT).show()
                        binding.editUsername
                    }
                }
            }
        }
    }

    private fun validateRegister(username: String, email: String, pwa: String, pwb: String): Boolean {
        if (username.isEmpty()) {
            binding.editUsername.error = "Mohon isi username"
            return false
        }
        if (email.isEmpty()) {
            binding.editEmail.error = "Mohon masukkan email"
            return false
        }
        if (pwa.isEmpty()) {
            binding.editPassword1.error = "Mohon masukkan password"
            return false
        }
        if (pwb.isEmpty()) {
            binding.editPassword2.error = "Password yang dimasukkan tidak sama"
            return false
        } else if (pwb != pwa) {
            binding.editPassword2.error = "Password yang dimasukkan tidak sama"
            return false
        }
        return true
    }
}