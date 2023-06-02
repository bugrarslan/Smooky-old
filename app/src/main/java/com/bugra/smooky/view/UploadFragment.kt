package com.bugra.smooky.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.bugra.smooky.R
import com.bugra.smooky.databinding.FragmentUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.HashMap

class UploadFragment : Fragment() {

    private lateinit var binding: FragmentUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    var selectedPicture: Uri? = null
    var downloadUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener { selectImage(it) }
        binding.uploadButton.setOnClickListener { uploadClicked(it) }
        binding.deleteButton.setOnClickListener { deleteClicked(it) }
        binding.likeButton.setOnClickListener { likeClicked(it) }

        arguments?.let {
            binding.deleteButton.visibility = View.GONE
            binding.uploadButton.visibility = View.GONE
            binding.likeButton.visibility = View.GONE
            val info = UploadFragmentArgs.fromBundle(it).info
            if (info.equals("new")) {
                //new
                binding.deleteButton.visibility = View.GONE
                binding.likeButton.visibility = View.GONE
                binding.uploadButton.visibility = View.VISIBLE
                binding.commentText.setText("")
                val selectedImageBackground = BitmapFactory.decodeResource(context?.resources, R.drawable.adsiz)
                binding.imageView.setImageBitmap(selectedImageBackground)
            } else {
                //old
                binding.uploadButton.visibility = View.GONE
                val username = UploadFragmentArgs.fromBundle(it).username
                val comment = UploadFragmentArgs.fromBundle(it).comment
                downloadUrl = UploadFragmentArgs.fromBundle(it).downloadUrl
                println(downloadUrl)
                binding.commentText.setText(comment)
                Picasso.get().load(downloadUrl).into(binding.imageView)
                if (auth.currentUser!!.email.toString().equals(username)) {
                    binding.deleteButton.visibility = View.VISIBLE
                } else {
                    binding.likeButton.visibility = View.VISIBLE
                }
            }
        }
    }

    fun uploadClicked(view: View) {
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val imageReference = storage.reference.child("images").child(imageName)

        if (selectedPicture != null) {
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                val uploadedPictureReference = storage.reference.child("images").child(imageName)
                uploadedPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    val postMap = hashMapOf<String, Any>()
                    postMap.put("downloadUrl", downloadUrl)
                    postMap.put("userEmail", auth.currentUser!!.email!!)
                    postMap.put("comment", binding.commentText.text.toString())
                    postMap.put("date", Timestamp.now())

                    firestore.collection("Posts").add(postMap).addOnSuccessListener {
                        val action = UploadFragmentDirections.actionUploadFragmentToFeedFragment()
                        Navigation.findNavController(requireActivity(), R.id.fragment2).navigate(action)
                    }.addOnFailureListener {
                        Toast.makeText(requireActivity(), it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(requireActivity(), it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun selectImage(view: View) {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Galeriye erişim için izin isteniyor", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",) {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            } else {
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //start activity for result
            activityResultLauncher.launch(intentToGallery)
        }
    }

    fun deleteClicked(view: View) {
        firestore.collection("Posts").whereEqualTo("downloadUrl", downloadUrl).addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireActivity(), error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        val documentId = value.documents.get(0).id
                        firestore.collection("Posts").document("$documentId").delete().addOnSuccessListener {
                            val reference = storage.getReferenceFromUrl(downloadUrl!!)
                            reference.delete().addOnSuccessListener {
                                println("deleted")
                            }.addOnFailureListener {
                                Toast.makeText(requireActivity(), it.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                            val action = UploadFragmentDirections.actionUploadFragmentToFeedFragment()
                            Navigation.findNavController(view).navigate(action)
                        }.addOnFailureListener {
                            Toast.makeText(requireActivity(), it.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    fun likeClicked(view: View) {
        firestore.collection("Posts").whereEqualTo("downloadUrl", downloadUrl).addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireActivity(), error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (value != null) {
                    if (!value.isEmpty) {
                        val documentId = value.documents.get(0).id
                        val likeMap = HashMap<String, Any>(value.documents.get(0).data)
                        var likes = likeMap.get("likes")
                        firestore.collection("Posts").document("$documentId").update("likes", likes)
                    }
                }
            }
        }
    }



    private fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    selectedPicture?.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                Toast.makeText(requireActivity(), "İzin gerekli!", Toast.LENGTH_LONG).show()
            }
        }

    }
}