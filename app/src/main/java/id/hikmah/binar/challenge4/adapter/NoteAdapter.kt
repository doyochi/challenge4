package id.hikmah.binar.challenge4.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.hikmah.binar.challenge4.R
import id.hikmah.binar.challenge4.database.Note

class NoteAdapter(
    private val listener: NoteActionListener
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    val diffCallback = object: DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private var differ = AsyncListDiffer(this, diffCallback)

    fun updateDataRecycler(note: List<Note>) = differ.submitList(note)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_data, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class NoteViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvTitle = view.findViewById<TextView>(R.id.card_title_value)
        val tvNote = view.findViewById<TextView>(R.id.card_note_value)
        val btnEdit = view.findViewById<ImageView>(R.id.btn_edit)
        val btnDelete = view.findViewById<ImageView>(R.id.btn_delete)

        fun bind(note: Note) {
            tvTitle.text = note.titleNote
            tvNote.text = note.note

            btnEdit.setOnClickListener {
                listener.onEdit(note)
            }

            btnDelete.setOnClickListener {
                listener.onDelete(note)
            }
        }
    }
}

interface NoteActionListener {
    fun onDelete(note: Note)
    fun onEdit(note: Note)
}