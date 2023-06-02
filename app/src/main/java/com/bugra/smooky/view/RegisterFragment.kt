package com.bugra.smooky.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bugra.smooky.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var binding : FragmentRegisterBinding

            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener { saveClicked(view) }
        auth = Firebase.auth
    }

    fun saveClicked(view: View) {
        val username = binding.emailTextRegister.text.toString()
        val password = binding.passwordTextRegister.text.toString()
        println(username)

        if (username.equals("") || password.equals("")) {
            Toast.makeText(requireActivity(), "Kullanıcı adınızı ve şifrenizi giriniz!", Toast.LENGTH_LONG).show()
        } else {
            auth.createUserWithEmailAndPassword(username, password).addOnSuccessListener {
                val intent = Intent(requireActivity(), PostActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }.addOnFailureListener {
                Toast.makeText(requireActivity(), it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}