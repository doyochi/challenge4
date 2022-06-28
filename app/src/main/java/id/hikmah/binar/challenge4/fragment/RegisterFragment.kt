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
        // Melakukan submit Register
        doRegister()
    }

    private fun doRegister() {
        binding.btnDaftar.setOnClickListener {
            // Get value dari TextEditText
            val etUsername = binding.editUsername.editableText.toString()
            val etEmail = binding.editEmail.editableText.toString()
            val etPassword1 = binding.editPassword1.editableText.toString()
            val etPassword2 = binding.editPassword2.editableText.toString()
            // Validasi inputan jika tidak ada yg kosong / jml karakter terpenuhi
            if (registerValidation(etUsername, etEmail, etPassword1, etPassword2)) {
                // Jika berbasil daftar maka pindah ke Login Screen & munculkan Toast
                insertToDb(etUsername, etEmail, etPassword1)
            }
        }
    }

    private fun registerValidation(username: String, email: String, pass1: String, pass2: String): Boolean {
        var result = true
        if (username.isEmpty()) { // jika kosong
            binding.editUsername.error = "Username tidak boleh kosong!"
            result = false
        } else if (username.length < 6) { // jika kurang dari 6 karakter
            binding.editUsername.error = "Minimum 6 karakter!"
            result = false
        } else {
            binding.editUsername
        }

        if (email.isEmpty()) { // jika kosong
            binding.editEmail.error = "Email tidak boleh kosong!"
            result = false
        } else {
            binding.editEmail
        }

        if (pass1.isEmpty()) { // jika kosong
            binding.editPassword1.error = "Password tidak boleh kosong!"
            result = false
        } else if (pass1.length < 8) { // jika kurang dari 8 karakter
            binding.editPassword1.error = "Password minimum 8 Karakter!"
            result = false
        } else {
            binding.editPassword1
        }

        if (pass2.isEmpty()) { // jika kosong
            binding.editPassword2.error = "Konfirmasi Password tidak boleh kosong!"
            result = false
        } else if (pass2 != pass1) { // jika konfirm pass tdk sama dgn pass
            binding.editPassword2.error = "Password harus sama!"
            result = false
        } else {
            binding.editPassword2
        }

        return result
    }

    private fun insertToDb(username: String, email: String, password: String) {
        val user = User(null, username, email, password)
        CoroutineScope(Dispatchers.IO).launch {
            // query check username
            val checkUsername = userDb?.userDao()?.checkRegisteredUsername(username)
            // Jika username yg diinputkan sudah ada pada DB
            if (!checkUsername.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    // Munculkan toast gagal daftar
                    Toast.makeText(requireContext(), "Gagal Daftar", Toast.LENGTH_SHORT).show()
                    // Munculkan error pada textinputlayout
                    binding.editUsername.error = "Username sudah dipakai"
                }
            } else { // jika username belum ada
                // Jalankan query insert
                val result = userDb?.userDao()?.insertUser(user)
                if (result != 0L) {
                    CoroutineScope(Dispatchers.Main).launch {
                        // Jika berhasil insert -> Pindah ke Login
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        // Munculkan toast Berhasil
                        Toast.makeText(requireContext(), "Berhasil Daftar", Toast.LENGTH_SHORT).show()
                        binding.editUsername
                    }
                }
            }
        }
    }
}