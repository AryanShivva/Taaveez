package com.itssuryansh.taaveez

import  android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isEmpty
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.taaveez.R
import com.google.taaveez.databinding.ActivityNotesBinding
import com.google.taaveez.databinding.DeleteItemBinding
import com.google.taaveez.databinding.UpdateNotesBinding
import jp.wasabeef.richeditor.RichEditor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Notes : AppCompatActivity() {


    private var binding: ActivityNotesBinding? = null

//    val openGalleryLauncher: ActivityResultLauncher<Intent> =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == RESULT_OK && result.data != null) {
////                val imageBackground: ImageView = findViewById(R.id.iv_background)
////                imageBackground.setImageURI(result.data?.data)
//            }
//        }
//    private val requestPermission: ActivityResultLauncher<Array<String>> =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permission ->
//            permission.entries.forEach {
//                val permissionName = it.key
//                val isGranted = it.value
//                if (isGranted) {
//                    Toast.makeText(
//                        this@Notes,
//                        "Permission granted for read storage",
//                        Toast.LENGTH_LONG
//                    ).show()
//
//                    val pickIntent =
//                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                    openGalleryLauncher.launch(pickIntent)
//
//                } else {
//                    if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
//                        Toast.makeText(
//                            this,
//                            "Permission denied for read storage",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//
//                }
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val typeface: Typeface =
            Typeface.createFromAsset(assets,"arabian_onenighjtstand.ttf")
        binding?.tvNotesHeading?.typeface = typeface



        binding?.tvabout?.setOnClickListener {
            val intent = Intent(this@Notes, About::class.java )
            startActivity(intent)
            overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_rigth);

        }

        binding?.tvSetting?.setOnClickListener {
            val intent = Intent(this@Notes, Setting::class.java)
            startActivity(intent)
                overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_left);

        }


        val NotesDao = (application as NotesApp).db.NotesDao()
        binding?.idFABAdd?.setOnClickListener {
            NewPoemDialog(NotesDao)
        }


        lifecycleScope.launch {
            NotesDao.fetchAllNotes().collect {
                val list = ArrayList(it)
                setupListOfDateINtoRecycleVIew(list, NotesDao)
            }
        }

    }


    private fun NewPoemDialog(NotesDao: NotesDao) {
        val PoemDialog = Dialog(this)
        PoemDialog.setCancelable(false)
        PoemDialog.setContentView(R.layout.notes_add_dialog)

        val PoemDes :RichEditor = PoemDialog.findViewById(R.id.idnotes)

        PoemDes.setPlaceholder("Enter text here...")
        PoemDes.setEditorHeight(200)
        PoemDes.setEditorFontSize(22)
        PoemDes.setPadding(10, 10, 10, 10)

        val btnBold : ImageButton?= PoemDialog.findViewById(R.id.btn_bold)
        btnBold?.setOnClickListener { PoemDes?.setBold() }
        val btnItalic : ImageButton?= PoemDialog.findViewById(R.id.btn_italic)
        btnItalic?.setOnClickListener { PoemDes?.setItalic() }
        val btnUnderline : ImageButton? = PoemDialog.findViewById(R.id.btn_underline)
        btnUnderline?.setOnClickListener { PoemDes?.setUnderline() }



        val cancelBtn = PoemDialog.findViewById<Button>(R.id.idBtnCancel)
        val addBtn = PoemDialog.findViewById<Button>(R.id.idBtnAdd)
        val itemTopic = PoemDialog.findViewById<EditText>(R.id.idTopic)
        val btn_addLink = PoemDialog.findViewById<ImageButton>(R.id.btn_add_link)


        btn_addLink.setOnClickListener{
                val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_insert_link, null)
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Insert Link")
                    .setView(dialogView)
                    .setPositiveButton("OK") { _, _ ->
                        val urlEditText = dialogView.findViewById<EditText>(R.id.url_edit_text)
                        val titleEditText = dialogView.findViewById<EditText>(R.id.title_edit_text)
                        val url = urlEditText.text.toString()
                        val title = titleEditText.text.toString()
                        PoemDes.insertLink(url, title)
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .create()
                dialog.show()

        }
//        val PoemDes = PoemDialog.findViewById<EditText>(R.id.idnotes)


//        val addImage = PoemDialog.findViewById<ImageView>(R.id.ib_gallery)
//        addImage.setOnClickListener {
//            requestStoragePermission()
//        }

        cancelBtn.setOnClickListener {
            PoemDialog.dismiss()

        }

        addBtn.setOnClickListener {
            var itemTopic: String = itemTopic.text.toString()
//            val PoemDes: String = PoemDes.toString()
            val htmlContentPoemDes= PoemDes.html.toString()
//            val Image : ImageView? = addImage


            val c = Calendar.getInstance()
            val dateTime = c.time
            Log.e("Date: ", "" + dateTime)
            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
            val date = sdf.format(dateTime)
            Log.e("Formatted Date: ", "" + date)




            if (!(htmlContentPoemDes.isEmpty())) {
                if (!(TextUtils.isEmpty(itemTopic.trim { it <= ' ' }))) {
                    lifecycleScope.launch {
                        NotesDao.insert(NotesEntity(Topic = itemTopic, Poem = htmlContentPoemDes, Date = date, CreatedDate = date))
                        Toast.makeText(applicationContext,
                            getString(R.string.Record_saved),
                            Toast.LENGTH_LONG).show()
                    }

                } else {
                    itemTopic = "दुआ"
                    lifecycleScope.launch {
                        NotesDao.insert(NotesEntity(Topic = itemTopic, Poem = htmlContentPoemDes, Date = date,
                            CreatedDate = date ))
                        Toast.makeText(applicationContext,
                            getString(R.string.Record_saved),
                            Toast.LENGTH_LONG).show()

                    }

                }
                PoemDialog.dismiss()
                Toast.makeText(this, "$htmlContentPoemDes", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(this, "field cannot be blank", Toast.LENGTH_LONG).show()
            }

        }

        PoemDialog.show()

    }



//    private fun requestStoragePermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(
//                this, Manifest.permission.READ_EXTERNAL_STORAGE
//            )
//        ) {
//            showRationalDialog(
//                "Kids Drawing App",
//                "Kids Drawing App " + "needs to Access your External Storage"
//            )
//        } else {
//            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE))
//
//        }
//    }

//    private fun showRationalDialog(
//        title: String,
//        message: String,
//    ) {
//        val builder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(this)
//        builder.setTitle(title)
//            .setMessage(message)
//            .setPositiveButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//        builder.create().show()
//    }




    private fun setupListOfDateINtoRecycleVIew(
        NotesList: ArrayList<NotesEntity>,
        NotesDao: NotesDao
    ) {
        if (NotesList.isNotEmpty()) {
            val itemAdapter = itemAdapter(
                NotesList,
                { updateId ->
                    updateRecordDialog(updateId, NotesDao)
                },
                { deleteId ->
                    deleteRecordAlertDialog(deleteId, NotesDao)
                },
                { OpenId ->
                    openNotes(OpenId, NotesDao)
                },
                { ShareId ->
                    ShareNotes(ShareId, NotesDao)
                },
            )

            binding?.rvItemsPoem?.layoutManager = LinearLayoutManager(this)
            binding?.rvItemsPoem?.adapter = itemAdapter
            binding?.rvItemsPoem?.visibility = View.VISIBLE
            binding?.tvNoDataAvailable?.visibility = View.GONE
            binding?.ivNoData?.visibility = View.GONE
        } else {
            binding?.rvItemsPoem?.visibility = View.GONE
            binding?.tvNoDataAvailable?.visibility = View.VISIBLE
            binding?.ivNoData?.visibility = View.VISIBLE

        }
    }



    private fun ShareNotes(id: Int, NotesDao: NotesDao) {

        var Topic: String?
        var PoemDes : String?

        lifecycleScope.launch {
            NotesDao.fetchNotesById(id).collect {
                if (it != null) {
                    Topic = it.Topic
                    PoemDes = it.Poem
//                    val HtmlConte
                    val sendIntent = Intent()
                    sendIntent.type = "text/plain"
                    sendIntent.action = Intent.ACTION_SEND
                    val body = "The Poem Topic is = ${Topic}\n" +
                            "--------------------------------\n" +
                            "${PoemDes}"
                    sendIntent.putExtra(Intent.EXTRA_TEXT, body)
                    Intent.createChooser(sendIntent, "Share using")
                    intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(sendIntent)

                }
            }

        }


    }


    private fun openNotes(id: Int, NotesDao: NotesDao) {
//        val OpenDialog = Dialog(this)
//        OpenDialog.setCancelable(false)
//        val binding = OpenNotesBinding.inflate(layoutInflater)
//        OpenDialog.setContentView(binding.root)
        var Topic :String?
        var PoemDes :String?
        var CreatedDate :String?
        var UpdatedDate : String?


        lifecycleScope.launch {
            NotesDao.fetchNotesById(id).collect {
                if (it != null) {
                    Topic = it.Topic
                    PoemDes = it.Poem
                    CreatedDate = it.CreatedDate
                    UpdatedDate = it.Date


                    val intent = Intent(this@Notes, OpenPoem::class.java)
                    intent.putExtra(Constants.POEM_TOPIC, Topic)
                    intent.putExtra(Constants.POEM_DES,PoemDes)
                    intent.putExtra(Constants.CREATED_DATE,CreatedDate)
                    intent.putExtra(Constants.UPDATED_DATE,UpdatedDate)
                    startActivity(intent)
                    overridePendingTransition(R.drawable.slide_in_right, R.drawable.slide_out_left);
                }
            }

        }


    }


    private fun updateRecordDialog(id: Int, NotesDao: NotesDao) {
        val updateDialog = Dialog(this)
        updateDialog.setCancelable(false)
        val binding = UpdateNotesBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        val PoemDes: RichEditor = updateDialog.findViewById(R.id.etUpdatePoem)
        PoemDes.setEditorHeight(2000)
        PoemDes.setEditorFontSize(22)
        PoemDes.setPadding(10, 10, 10, 10)

        val btnBold : ImageButton? = updateDialog.findViewById(R.id.btn_update_bold)
        btnBold?.setOnClickListener { PoemDes?.setBold() }
        val btnItalic : ImageButton? = updateDialog.findViewById(R.id.btn_update_italic)
        btnItalic?.setOnClickListener { PoemDes?.setItalic() }
//        val btnUnderline : ImageButton? = updateDialog.findViewById(R.id.btn_update_underline)
//        btnUnderline?.setOnClickListener { PoemDes?.setUnderline() }


        var CreatedDate:String=""
//        var PoemDes :RichEditor?

        lifecycleScope.launch {
            NotesDao.fetchNotesById(id).collect {
                if (it != null) {
                    binding.etPoemTopic.setText(it.Topic)
//                    binding.etUpdatePoem.setText(it.Poem)
//                    val html = (it.Poem).html
                    binding.etUpdatePoem.setHtml(it.Poem)
//                    val richEditor = RichEditor(context)
//                    richEditor.html = htmlContent
                    CreatedDate = it.CreatedDate

                }
            }

        }
        binding.btnUpdatePoem.setOnClickListener {
            var Topic = binding.etPoemTopic.text.toString()
            var Poem = binding.etUpdatePoem.html

            

            val c = Calendar.getInstance()
            val dateTime = c.time
            Log.e("Date: ", "" + dateTime)
            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
            val date = sdf.format(dateTime)
            Log.e("Formatted Date: ", "" + date)


            if (!(Poem.isEmpty())) {
                if (!(TextUtils.isEmpty(Topic.trim { it <= ' ' }))) {
                    lifecycleScope.launch {
                        NotesDao.update(NotesEntity(id, Topic, Poem, date,CreatedDate))
                        Toast.makeText(applicationContext, "Record Updated", Toast.LENGTH_LONG)
                            .show()
                        intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        updateDialog.dismiss()
                    }
                } else {
                    Topic = "दुआ"
                    lifecycleScope.launch {
                        NotesDao.update(NotesEntity(id, Topic, Poem, date, CreatedDate))
                        Toast.makeText(applicationContext, "Record Updated", Toast.LENGTH_LONG)
                            .show()
                        intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        updateDialog.dismiss()
                    }
                }
            } else{
                    Toast.makeText(applicationContext, "filed cannot be blank", Toast.LENGTH_LONG)
                        .show()
                }
            }

            binding.btnCancelPoem.setOnClickListener {
                updateDialog.dismiss()
            }
            updateDialog.show()
        }


    private fun deleteRecordAlertDialog(id: Int, employeeDao: NotesDao) {

        val deleteDialog = Dialog(this)
        deleteDialog.setCancelable(false)
        val binding = DeleteItemBinding.inflate(layoutInflater)
        deleteDialog.setContentView(binding.root)



        binding?.btnDeleteNo?.setOnClickListener {
            deleteDialog.dismiss()
        }
        binding?.btnDeleteYes?.setOnClickListener {
            lifecycleScope.launch {
                employeeDao.delete(NotesEntity(id))
                    Toast.makeText(applicationContext,
                        "Record deleted successfully",
                        Toast.LENGTH_LONG).show()
                }
                deleteDialog.dismiss()

        }

        deleteDialog.show()



    }


}










