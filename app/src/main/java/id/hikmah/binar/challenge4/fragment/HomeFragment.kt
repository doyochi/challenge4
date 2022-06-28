package id.hikmah.binar.challenge4.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import id.hikmah.binar.challenge4.AuthActivity
import id.hikmah.binar.challenge4.R
import id.hikmah.binar.challenge4.adapter.NoteActionListener
import id.hikmah.binar.challenge4.adapter.NoteAdapter
import id.hikmah.binar.challenge4.database.Note
import id.hikmah.binar.challenge4.database.UserDatabase
import id.hikmah.binar.challenge4.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPref: SharedPreferences
    private lateinit var noteAdapter: NoteAdapter
    private var userDb: UserDatabase?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireContext().getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        userDb = UserDatabase.getInstance(requireContext())

        welcomeUsername()
        initRecyclerView()
        getNotesData()
        addNote()
        actionLogout()
    }

    private fun welcomeUsername() {
        val getUsername = sharedPref.getString("USERNAME", "Default Value")
        binding.txtWelcomeUser.text = "Welcome, $getUsername"
    }

    private fun initRecyclerView() {
        binding.apply {
            noteAdapter = NoteAdapter(action)
            rvData.adapter = noteAdapter
            rvData.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun addNote() {
        binding.fabAdd.setOnClickListener {
            showCustomDialog(null)
        }
    }

    private fun showCustomDialog(note: Note?) {
        val customLayout = LayoutInflater.from(requireContext()).inflate(R.layout.tambah_layout, null, false)
        val dialogBuilder = AlertDialog.Builder(requireContext())

        val tvTitleDialog = customLayout.findViewById<TextView>(R.id.title_dialog)
        val etTitle = customLayout.findViewById<TextInputLayout>(R.id.edit_title)
        val etNote = customLayout.findViewById<TextInputLayout>(R.id.edit_note)
        val btnAddUpdate = customLayout.findViewById<Button>(R.id.btn_add_update)

        if (note != null) {
            etTitle.editText?.setText(note.titleNote)
            etNote.editText?.setText(note.note)
            tvTitleDialog.text = "Edit"
            btnAddUpdate.text = "Perbarui"
        }

        dialogBuilder.setView(customLayout)
        val dialog = dialogBuilder.create()

        btnAddUpdate.setOnClickListener {
            val title = etTitle.editText?.text.toString()
            val notes = etNote.editText?.text.toString()

            if (note != null) {
                val newNote = Note(note.id, note.idUser, title, notes)
                updateToDb(newNote)
            } else {
                insertToDb(title, notes)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getNotesData() {
        val getUserId = sharedPref.getInt("USERID", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val result = userDb?.userDao()?.getAllNote(getUserId)
            if (!result.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    noteAdapter.updateDataRecycler(result)
                    binding.txtNoteEmpty.isGone = true
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    noteAdapter.updateDataRecycler(result!!)
                    binding.txtNoteEmpty.isGone = false
                }
            }
        }
    }

    private fun insertToDb(title: String, note: String) {
        val getUserId = sharedPref.getInt("USERID", 0)
        val notes = Note(null, getUserId, title, note)
        CoroutineScope(Dispatchers.IO).launch {
            val result = userDb?.userDao()?.addNotes(notes)
            if (result != 0L) {
                getNotesData()
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(),
                        "Berhasil menambahkan notes", Toast.LENGTH_SHORT).show()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(),
                        "Gagal menambahkan notes", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateToDb(note: Note) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = userDb?.userDao()?.updateNote(note)
            if (result != 0) {
                getNotesData()
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(),
                        "Berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(),
                        "Gagal diperbarui", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDeleteDialog(note: Note) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Hapus Catatan ?")
        dialog.setCancelable(true)
        dialog.setPositiveButton("Hapus") { dialogInterface, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                val result = userDb?.userDao()?.deleteNote(note)
                if (result != 0) {
                    getNotesData()
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(),
                            "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                        dialogInterface.dismiss()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(requireContext(),
                            "Gagal dihapus", Toast.LENGTH_SHORT).show()
                        dialogInterface.dismiss()
                    }
                }
            }
        }
        dialog.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        dialog.show()
    }

    private fun actionLogout() {
        binding.btnLogout.setOnClickListener {
            val editor = sharedPref.edit()
            editor.apply {
                clear()
                putBoolean("LOGIN_STATE", false)
                apply()
                startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }

        }
    }

    //recycle view action listener
    private val action = object : NoteActionListener {
        override fun onDelete(note: Note) {
            showDeleteDialog(note)
        }

        override fun onEdit(note: Note) {
            showCustomDialog(note)
        }
    }
}